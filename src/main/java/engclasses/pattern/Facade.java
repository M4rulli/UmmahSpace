package engclasses.pattern;

import engclasses.beans.EventoBean;
import engclasses.exceptions.*;
import engclasses.dao.*;
import model.Evento;
import model.Partecipante;
import model.Partecipazione;


/**
 * Facade per nascondere la complessità dell'accesso ai dati e delle operazioni di aggiornamento.
 */

public class Facade {
    private static Facade facadeInstance = null;

    private Facade() {}

    // Implementazione Singleton per garantire un'unica istanza della Facade
    public static synchronized Facade getInstance() {
        if (facadeInstance == null) {
            facadeInstance = new Facade();
        }
        return facadeInstance;
    }


    // Metodo di facciata per aggiornare un evento
    public void aggiornaEventoFacade(EventoBean updatedBean, long idEvento, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {

        // Recupera l'evento esistente dal database o buffer
        Evento eventoEsistente = GestioneEventoDAO.getEventoById(idEvento, persistence);
        if (eventoEsistente == null) {
            throw new EventoNonTrovatoException("Evento con ID " + idEvento + " non trovato.");
        }

        // Crea un nuovo oggetto Evento con i dati aggiornati
        Evento eventoAggiornato = new Evento(
                updatedBean.getTitolo(),
                updatedBean.getDescrizione(),
                updatedBean.getData(),
                updatedBean.getOrario(),
                updatedBean.getLimitePartecipanti(),
                eventoEsistente.getIscritti(),  // Manteniamo il numero di iscritti
                updatedBean.getLink(),
                eventoEsistente.getNomeOrganizzatore(),  // Manteniamo i dati dell'organizzatore
                eventoEsistente.getCognomeOrganizzatore(),
                eventoEsistente.getStato(),  // Manteniamo lo stato attuale
                eventoEsistente.getIdEvento(),  // Manteniamo lo stesso ID evento
                eventoEsistente.getIdOrganizzatore()  // Manteniamo lo stesso organizzatore
        );
        // Salva l'evento aggiornato nel database o nel buffer
        GestioneEventoDAO.aggiornaEvento(eventoAggiornato, persistence);
    }

    // Metodo di facciata per aggiungere un nuovo evento
    public boolean aggiungiEventoFacade(EventoBean eventoBean, String idOrganizzatore, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {

        // Crea un identificatore per l'evento
        long idEvento = System.currentTimeMillis();

        // Creazione del nuovo evento (nascondiamo la logica di creazione)
        Evento nuovoEvento = new Evento(
                eventoBean.getTitolo(),
                eventoBean.getDescrizione(),
                eventoBean.getData(),
                eventoBean.getOrario(),
                eventoBean.getLimitePartecipanti(),
                0, // Nuovo evento, quindi iscritti = 0
                eventoBean.getLink(),
                eventoBean.getNomeOrganizzatore(),
                eventoBean.getCognomeOrganizzatore(),
                true, // L'evento è sempre attivo alla creazione
                idEvento,
                idOrganizzatore
        );

        // Salva l'evento nel database o nel buffer
        return GestioneEventoDAO.aggiungiEvento(nuovoEvento, persistence);
    }

    // Metodo per creare e salvare una partecipazione
    public void iscriviPartecipanteFacade(long idEvento, String idUtente, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, UtenteNonTrovatoException {
        // Recupera i dati del partecipante dal DAO
        Partecipante partecipante = PartecipanteDAO.selezionaPartecipante("idUtente", idUtente, persistence);
        if (partecipante == null) {
            throw new UtenteNonTrovatoException("Partecipante non trovato.");
        }

        // Crea l'oggetto Partecipazione
        Partecipazione partecipazione = new Partecipazione(
                partecipante.getIdUtente(),
                idEvento,
                partecipante.getNome(),
                partecipante.getCognome(),
                partecipante.getUsername(),
                partecipante.getEmail(),
                java.time.LocalDate.now().toString()
        );
        PartecipazioneDAO.salvaPartecipazione(partecipazione, persistence);
    }

}
