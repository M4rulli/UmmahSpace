package engclasses.dao;

import model.Evento;
import model.IscrizionePartecipante;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IscrizioneEventoDAO {
    private static final List<Evento> eventiBuffer = new ArrayList<>();
    private static final Map<Long, List<String>> partecipantiBuffer = new HashMap<>();

    // Popola inizialmente il buffer con eventi hard-coded
    static {
        eventiBuffer.add(new Evento("Evento 1", "Descrizione 1", "2025-01-01", "10:00", 50, 10, "www.evento1.com", "Mario", "Rossi", true, 1, ""));
        eventiBuffer.add(new Evento("Evento 2", "Descrizione 2", "2025-01-02", "15:00", 30, 5, "www.evento2.com", "Luca", "Bianchi", true, 2, ""));
        eventiBuffer.add(new Evento("Evento 3", "Descrizione 3", "2025-01-03", "09:00", 20, 15, "www.evento3.com", "Anna", "Verdi", true, 3, ""));
        eventiBuffer.add(new Evento("Evento 4", "Descrizione 4", "2025-01-03", "09:00", 20, 20, "www.evento4.com", "Romolo", "Remo", true, 4, ""));
        eventiBuffer.add(new Evento("Evento 5", "Descrizione 5", "2025-01-03", "09:00", 20, 20, "www.evento5.com", "Ciao", "Darwin", true, 5, ""));
    }

        // Metodo per ottenere tutti gli eventi nel buffer
    public static List<Evento> getEventiPerMeseAnno(int mese, int anno) {
        List<Evento> eventiPerMeseAnno = new ArrayList<>();
        for (Evento evento : eventiBuffer) {
            String[] dataSplit = evento.getData().split("-");
            int eventoAnno = Integer.parseInt(dataSplit[0]);
            int eventoMese = Integer.parseInt(dataSplit[1]);

            if (eventoAnno == anno && eventoMese == mese) {
                eventiPerMeseAnno.add(evento);
            }
        }
        return eventiPerMeseAnno;
    }

    public static void aggiungiPartecipanteAdEvento(IscrizionePartecipante iscrizionePartecipante) {
        long idEvento = iscrizionePartecipante.getIdEvento();
        String idUtente = iscrizionePartecipante.getIdUtente();

        partecipantiBuffer.putIfAbsent(idEvento, new ArrayList<>());
        List<String> partecipanti = partecipantiBuffer.get(idEvento);

        if (partecipanti.contains(idUtente)) {
            throw new IllegalArgumentException("Sei già iscritto a questo evento.");
        }
        partecipanti.add(idUtente);
    }

    // Metodo per ottenere un evento dal buffer tramite il suo ID
    public static Evento getEventoById(long idEvento) {
        for (Evento evento : eventiBuffer) {
            if (evento.getIdEvento() == idEvento) {
                return evento;
            }
        }
        return null; // Ritorna null se non viene trovato l'evento
    }

    public static void aggiornaEvento(Evento eventoAggiornato) {
        for (int i = 0; i < eventiBuffer.size(); i++) {
            Evento evento = eventiBuffer.get(i);
            if (evento.getIdEvento() == eventoAggiornato.getIdEvento()) {
                // Incrementa il numero di iscritti
                eventoAggiornato.setIscritti(eventoAggiornato.getIscritti() + 1);
                eventiBuffer.set(i, eventoAggiornato);
                return;
            }
        }
        throw new IllegalArgumentException("Evento non trovato per l'aggiornamento: " + eventoAggiornato.getIdEvento());
    }

}