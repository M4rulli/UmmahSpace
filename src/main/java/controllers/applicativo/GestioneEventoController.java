package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.dao.GestioneEventoDAO;
import javafx.scene.control.Alert;
import misc.Session;
import model.Evento;

import java.util.ArrayList;
import java.util.List;

public class GestioneEventoController {

    private final Session session;

    public GestioneEventoController(Session session) {
        this.session = session;
    }

    // Metodo per recuperare tutti gli eventi associati a un organizzatore
    public List<EventoBean> getEventiOrganizzatore(String idUtente) {
        List<Evento> eventi = GestioneEventoDAO.getEventiByOrganizzatore(idUtente);
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

    // Metodo per aggiungere un nuovo evento per un organizzatore
    public boolean aggiungiEvento(EventoBean eventoBean, String idUtente) {
        StringBuilder errori = new StringBuilder();

        // Validazione dei campi obbligatori
        if (eventoBean.getTitolo() == null || eventoBean.getTitolo().isEmpty()) {
            errori.append("Il titolo dell'evento è obbligatorio.\n");
        }
        if (eventoBean.getDescrizione() == null || eventoBean.getDescrizione().isEmpty()) {
            errori.append("La descrizione dell'evento è obbligatoria.\n");
        }
        if (eventoBean.getData() == null || eventoBean.getData().isEmpty()) {
            errori.append("La data dell'evento è obbligatoria.\n");
        }
        if (eventoBean.getOrario() == null || eventoBean.getOrario().isEmpty()) {
            errori.append("L'orario dell'evento è obbligatorio.\n");
        }
        if (eventoBean.getLimitePartecipanti() <= 0) {
            errori.append("Il limite dei partecipanti deve essere maggiore di zero.\n");
        }

        if (errori.length() > 0) {
            showAlert("Errore", errori.toString(), Alert.AlertType.WARNING);
            return false;
        }

        // Creazione del nuovo evento
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

        boolean aggiunto = GestioneEventoDAO.aggiungiEvento(nuovoEvento, idUtente);
        if (aggiunto) {
            showAlert("Successo", "Evento aggiunto con successo.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Errore", "Errore durante l'aggiunta dell'evento.", Alert.AlertType.ERROR);
        }

        return aggiunto;
    }

    // Metodo per eliminare un evento
    public boolean eliminaEvento(long idEvento, String idUtente) {
        if (idEvento <= 0) {
            showAlert("Errore", "ID evento non valido.", Alert.AlertType.ERROR);
            return false;
        }

        boolean eliminato = GestioneEventoDAO.eliminaEvento(idEvento, idUtente);
        if (eliminato) {
            showAlert("Successo", "Evento eliminato con successo.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Errore", "Errore durante l'eliminazione dell'evento.", Alert.AlertType.ERROR);
        }

        return eliminato;
    }

    // Metodo per modificare un evento
    public boolean modificaEvento(EventoBean eventoBean, String idUtente) {
        StringBuilder errori = new StringBuilder();

        // Validazione dei campi obbligatori
        if (eventoBean.getTitolo() == null || eventoBean.getTitolo().isEmpty()) {
            errori.append("Il titolo dell'evento è obbligatorio.\n");
        }
        if (eventoBean.getDescrizione() == null || eventoBean.getDescrizione().isEmpty()) {
            errori.append("La descrizione dell'evento è obbligatoria.\n");
        }
        if (eventoBean.getData() == null || eventoBean.getData().isEmpty()) {
            errori.append("La data dell'evento è obbligatoria.\n");
        }
        if (eventoBean.getOrario() == null || eventoBean.getOrario().isEmpty()) {
            errori.append("L'orario dell'evento è obbligatorio.\n");
        }
        if (eventoBean.getLimitePartecipanti() <= 0) {
            errori.append("Il limite dei partecipanti deve essere maggiore di zero.\n");
        }

        if (errori.length() > 0) {
            showAlert("Errore", errori.toString(), Alert.AlertType.WARNING);
            return false;
        }

        // Recupero dell'evento esistente
        Evento eventoEsistente = GestioneEventoDAO.getEventoById(eventoBean.getIdEvento());
        if (eventoEsistente == null) {
            showAlert("Errore", "Evento non trovato o permessi insufficienti.", Alert.AlertType.ERROR);
            return false;
        }

        // Aggiornamento dell'evento
        eventoEsistente.setTitolo(eventoBean.getTitolo());
        eventoEsistente.setDescrizione(eventoBean.getDescrizione());
        eventoEsistente.setData(eventoBean.getData());
        eventoEsistente.setOrario(eventoBean.getOrario());
        eventoEsistente.setLimitePartecipanti(eventoBean.getLimitePartecipanti());
        eventoEsistente.setNomeOrganizzatore(eventoBean.getNomeOrganizzatore());
        eventoEsistente.setCognomeOrganizzatore(eventoBean.getCognomeOrganizzatore());

        boolean aggiornato = GestioneEventoDAO.modificaEvento(
                eventoEsistente.getIdEvento(),
                eventoEsistente.getTitolo(),
                eventoEsistente.getDescrizione(),
                eventoEsistente.getData(),
                eventoEsistente.getOrario()
        );

        if (aggiornato) {
            showAlert("Successo", "Evento aggiornato con successo.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Errore", "Errore durante l'aggiornamento dell'evento.", Alert.AlertType.ERROR);
        }

        return aggiornato;
    }

    // Metodo per mostrare un'alert
    private void showAlert(String titolo, String messaggio, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}
