package engclasses.dao;

import model.Evento;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GestioneEventoDAO {
    // Buffer che contiene gli eventi
    private static final List<Evento> eventiBuffer = new ArrayList<>();

    // Popola inizialmente il buffer con eventi hard-coded (simile alla classe IscrizioneEventoDAO)
    static {
        eventiBuffer.add(new Evento("Evento 1", "Descrizione 1", "2025-01-01", "10:00", 50, 10, "www.evento1.com", "Mario", "Rossi", true, 1, "1"));
        eventiBuffer.add(new Evento("Evento 2", "Descrizione 2", "2025-01-02", "15:00", 30, 5, "www.evento2.com", "Luca", "Bianchi", true, 2, "1"));
        eventiBuffer.add(new Evento("Evento 3", "Descrizione 3", "2025-01-03", "09:00", 20, 15, "www.evento3.com", "Anna", "Verdi", true, 3, "1"));
        eventiBuffer.add(new Evento("Evento 4", "Descrizione 4", "2025-01-03", "09:00", 20, 20, "www.evento4.com", "Romolo", "Remo", true, 4, ""));
        eventiBuffer.add(new Evento("Evento 5", "Descrizione 5", "2025-01-03", "09:00", 20, 20, "www.evento5.com", "Ciao", "Darwin", true, 5, ""));
    }

    // Recupera tutti gli eventi associati a un organizzatore.
    public static List<Evento> getEventiByOrganizzatore(String idUtente) {
        List<Evento> eventiOrganizzatore = new ArrayList<>();
        for (Evento evento : eventiBuffer) {
            //if (Objects.equals(evento.getIdOrganizzatore(), idUtente)) {
                eventiOrganizzatore.add(evento);
            //}
        }
        return eventiOrganizzatore;
    }

    // Aggiunge un nuovo evento al buffer per un organizzatore.
    public static boolean aggiungiEvento(Evento nuovoEvento, String idUtente) {
        try {
            // Imposta l'ID organizzatore e genera l'ID evento
            nuovoEvento.setIdOrganizzatore(idUtente);
            nuovoEvento.setIdEvento(nuovoEvento.getIdEvento());

            // Aggiungi l'evento al buffer
            eventiBuffer.add(nuovoEvento);
            return true; // Operazione riuscita
        }
        catch (Exception e) {
            e.printStackTrace(); // Log dell'errore per debug
            return false; // Operazione fallita
        }
    }


            // Aggiorna un evento esistente nel buffer.
    public static void aggiornaEvento(Evento eventoAggiornato) {
        for (int i = 0; i < eventiBuffer.size(); i++) {
            Evento evento = eventiBuffer.get(i);
            if (evento.getIdEvento() == eventoAggiornato.getIdEvento()) {
                eventiBuffer.set(i, eventoAggiornato);
                return;
            }
        }
        throw new IllegalArgumentException("Evento non trovato per l'aggiornamento: " + eventoAggiornato.getIdEvento());
    }

    // Elimina un evento dal buffer.
    public static boolean eliminaEvento(long idEvento, String idUtente) {
        for (int i = 0; i < eventiBuffer.size(); i++) {
            Evento evento = eventiBuffer.get(i);
            //if (evento.getIdEvento() == idEvento && Objects.equals(evento.getIdOrganizzatore(), idUtente)) {
                eventiBuffer.remove(i);
                return true; // Ritorna true se l'evento è stato eliminato
            //}
        }
        return false; // Ritorna false se l'evento non è stato trovato o non è stato eliminato
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

    // Genera un ID unico per un nuovo evento.
    private static long generaIdEvento() {
        if (eventiBuffer.isEmpty()) {
            return 1;
        }
        long maxId = 0;
        for (Evento evento : eventiBuffer) {
            if (evento.getIdEvento() > maxId) {
                maxId = evento.getIdEvento();
            }
        }
        return maxId + 1;
    }

    // Metodo per ottenere tutti gli eventi dell'organizzatore dal buffer
    public static List<Evento> getEventiPerOrganizzatore(String idUtente) {
        List<Evento> eventiPerOrganizzatore = new ArrayList<>();
        for (Evento evento : eventiBuffer) {
            if (Objects.equals(evento.getIdOrganizzatore(), idUtente)) {
                eventiPerOrganizzatore.add(evento);
            }
        }
        return eventiPerOrganizzatore;
    }
    public static boolean modificaEvento(long idEvento, String nuovoTitolo, String nuovaDescrizione, String nuovaData, String nuovoOrario) {
        for (int i = 0; i < eventiBuffer.size(); i++) {
            Evento evento = eventiBuffer.get(i);
            if (evento.getIdEvento() == idEvento) {
                // Modifica i campi desiderati
                evento.setTitolo(nuovoTitolo);
                evento.setDescrizione(nuovaDescrizione);
                evento.setData(nuovaData);
                evento.setOrario(nuovoOrario);
                return true; // Evento modificato con successo
            }
        }
        return false; // Evento non trovato, restituisce false
    }

}
