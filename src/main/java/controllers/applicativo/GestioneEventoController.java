package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.dao.GestioneEventoDAO;
import model.Evento;

import java.util.ArrayList;
import java.util.List;

public class GestioneEventoController {

    public GestioneEventoController() {}

    /**
     * Ottiene tutti gli eventi associati a un organizzatore.
     *
     * @param idOrganizzatore ID dell'organizzatore.
     * @return Lista di eventi associati all'organizzatore.
     */
    public List<EventoBean> getEventiOrganizzatore(String idOrganizzatore) {
        List<Evento> eventi = GestioneEventoDAO.getEventiByOrganizzatore(idOrganizzatore);
        List<EventoBean> eventoBeans = new ArrayList<>();

        for (Evento evento : eventi) {
            EventoBean bean = new EventoBean();
            bean.setIdEvento(evento.getIdEvento());
            bean.setTitolo(evento.getTitolo());
            bean.setDescrizione(evento.getDescrizione());
            bean.setData(evento.getData());
            bean.setOrario(evento.getOrario());
            bean.setLimitePartecipanti(evento.getLimitePartecipanti());
            bean.setIscritti(evento.getIscritti());
            bean.setNomeOrganizzatore(evento.getNomeOrganizzatore());
            bean.setCognomeOrganizzatore(evento.getCognomeOrganizzatore());
            bean.setStato(evento.getStato());
            eventoBeans.add(bean);
        }

        return eventoBeans;
    }

    /**
     * Aggiunge un nuovo evento per un organizzatore.
     *
     * @param eventoBean Dettagli del nuovo evento.
     * @param idOrganizzatore ID dell'organizzatore.
     */
    public boolean aggiungiEvento(EventoBean eventoBean, String idOrganizzatore) throws IllegalArgumentException {

        // Controlla che i campi obbligatori siano presenti
        if (eventoBean.getTitolo() == null || eventoBean.getTitolo().isEmpty()) {
            throw new IllegalArgumentException("Il titolo dell'evento è obbligatorio.");
        }
        if (eventoBean.getDescrizione() == null || eventoBean.getDescrizione().isEmpty()) {
            throw new IllegalArgumentException("La descrizione dell'evento è obbligatoria.");
        }
        if (eventoBean.getData() == null || eventoBean.getData().isEmpty()) {
            throw new IllegalArgumentException("La data dell'evento è obbligatoria.");
        }
        if (eventoBean.getOrario() == null || eventoBean.getOrario().isEmpty()) {
            throw new IllegalArgumentException("L'orario dell'evento è obbligatorio.");
        }
        if (eventoBean.getLimitePartecipanti() <= 0) {
            throw new IllegalArgumentException("Il limite dei partecipanti deve essere maggiore di zero.");
        }

        // Crea il nuovo oggetto Evento
        Evento nuovoEvento = new Evento();
        nuovoEvento.setTitolo(eventoBean.getTitolo());
        nuovoEvento.setDescrizione(eventoBean.getDescrizione());
        nuovoEvento.setData(eventoBean.getData());
        nuovoEvento.setOrario(eventoBean.getOrario());
        nuovoEvento.setLimitePartecipanti(eventoBean.getLimitePartecipanti());
        nuovoEvento.setIscritti(0);
        nuovoEvento.setNomeOrganizzatore(eventoBean.getNomeOrganizzatore());
        nuovoEvento.setCognomeOrganizzatore(eventoBean.getCognomeOrganizzatore());
        nuovoEvento.setStato(true);

        // Salva l'evento tramite il DAO
        GestioneEventoDAO.aggiungiEvento(nuovoEvento, idOrganizzatore);

        System.out.println("Evento aggiunto con successo. Titolo: " + nuovoEvento.getTitolo() + ", ID Organizzatore: " + idOrganizzatore);
        return true;
    }

    /**
     * Modifica un evento esistente.
     *
     * @param eventoBean Evento aggiornato.
     */
    public boolean modificaEvento(EventoBean eventoBean) throws IllegalArgumentException {

        // Controlla che i campi obbligatori siano presenti
        if (eventoBean.getTitolo() == null || eventoBean.getTitolo().isEmpty()) {
            throw new IllegalArgumentException("Il titolo dell'evento è obbligatorio.");
        }
        if (eventoBean.getDescrizione() == null || eventoBean.getDescrizione().isEmpty()) {
            throw new IllegalArgumentException("La descrizione dell'evento è obbligatoria.");
        }
        if (eventoBean.getData() == null || eventoBean.getData().isEmpty()) {
            throw new IllegalArgumentException("La data dell'evento è obbligatoria.");
        }
        if (eventoBean.getOrario() == null || eventoBean.getOrario().isEmpty()) {
            throw new IllegalArgumentException("L'orario dell'evento è obbligatorio.");
        }
        if (eventoBean.getLimitePartecipanti() <= 0) {
            throw new IllegalArgumentException("Il limite dei partecipanti deve essere maggiore di zero.");
        }
        if (eventoBean.getIscritti() < 0) {
            throw new IllegalArgumentException("Il numero di iscritti non può essere negativo.");
        }

        // Crea un oggetto Evento aggiornato
        Evento eventoAggiornato = new Evento();
        eventoAggiornato.setIdEvento(eventoBean.getIdEvento());
        eventoAggiornato.setTitolo(eventoBean.getTitolo());
        eventoAggiornato.setDescrizione(eventoBean.getDescrizione());
        eventoAggiornato.setData(eventoBean.getData());
        eventoAggiornato.setOrario(eventoBean.getOrario());
        eventoAggiornato.setLimitePartecipanti(eventoBean.getLimitePartecipanti());
        eventoAggiornato.setIscritti(eventoBean.getIscritti());
        eventoAggiornato.setNomeOrganizzatore(eventoBean.getNomeOrganizzatore());
        eventoAggiornato.setCognomeOrganizzatore(eventoBean.getCognomeOrganizzatore());
        eventoAggiornato.setStato(eventoBean.isStato());

        // Salva le modifiche tramite il DAO
        GestioneEventoDAO.aggiornaEvento(eventoAggiornato);

        System.out.println("Evento aggiornato con successo. Titolo: " + eventoAggiornato.getTitolo() + ", ID Evento: " + eventoAggiornato.getIdEvento());
        return true;
    }


    /**
     * Elimina un evento.
     *
     * @param idEvento ID dell'evento da eliminare.
     */
    public boolean eliminaEvento(long idEvento, String idOrganizzatore) throws IllegalArgumentException {

        // Verifica che l'ID dell'evento sia valido
        if (idEvento <= 0) {
            throw new IllegalArgumentException("ID dell'evento non valido.");
        }

        // Chiamata al DAO per eliminare l'evento
        boolean successo = GestioneEventoDAO.eliminaEvento(idEvento, idOrganizzatore);

        if (successo) {
            System.out.println("Evento eliminato con successo. ID Evento: " + idEvento);
        } else {
            System.out.println("Errore nell'eliminazione dell'evento. ID Evento: " + idEvento);
        }

        return successo;
    }

}
