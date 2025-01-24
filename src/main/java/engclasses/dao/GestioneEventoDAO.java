package engclasses.dao;

import model.Evento;

import java.util.ArrayList;
import java.util.List;

public class GestioneEventoDAO {
    private static final List<Evento> eventiBuffer = new ArrayList<>();

    // Recupera tutti gli eventi associati a un organizzatore.
    public static List<Evento> getEventiByOrganizzatore(String idOrganizzatore) {
        List<Evento> eventiOrganizzatore = new ArrayList<>();
        for (Evento evento : eventiBuffer) {
            if (evento.getIdOrganizzatore() == idOrganizzatore) {
                eventiOrganizzatore.add(evento);
            }
        }
        return eventiOrganizzatore;
    }

    //Aggiunge un nuovo evento al buffer per un organizzatore.

    public static void aggiungiEvento(Evento nuovoEvento, String idOrganizzatore) {
        nuovoEvento.setIdOrganizzatore(idOrganizzatore);
        nuovoEvento.setIdEvento(generaIdEvento());
        eventiBuffer.add(nuovoEvento);
    }

    //Aggiorna un evento esistente nel buffer.

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

    //Elimina un evento dal buffer.

    public static boolean eliminaEvento(long idEvento, String idOrganizzatore) {
        for (int i = 0; i < eventiBuffer.size(); i++) {
            Evento evento = eventiBuffer.get(i);
            if (evento.getIdEvento() == idEvento && evento.getIdOrganizzatore() == idOrganizzatore) {
                eventiBuffer.remove(i);
                return true; // Ritorna true se l'evento è stato eliminato
            }
        }
        return false; // Ritorna false se l'evento non è stato trovato o non è stato eliminato
    }




    //Genera un ID unico per un nuovo evento.

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
}

