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
    public List<EventoBean> getEventiOrganizzatore(String idUtente, Session session) {
        List<Evento> eventi = GestioneEventoDAO.getEventiByOrganizzatore(idUtente, session.isPersistence());
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
        } else if (eventoBean.getDescrizione().length() > 500){
                errori.append("La descrizione non può essere più lunga di 500 caratteri.");
        }

        if (eventoBean.getOrario() == null || eventoBean.getOrario().isEmpty()) {
            errori.append("L'orario dell'evento è obbligatorio.\n");
        } else if (!eventoBean.getOrario().matches ("^([01]\\d|2[0-3]):([0-5]\\d)$")){
            errori.append("L'ora deve essere nel formato 'HH:mm' (es. 14:30).");
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

        boolean aggiunto = GestioneEventoDAO.aggiungiEvento(nuovoEvento, idUtente, session.isPersistence());

        return aggiunto;
    }

    // Metodo per eliminare un evento
    public boolean eliminaEvento(long idEvento, String idUtente) {
        if (idEvento <= 0) {
            showAlert("Errore", "ID evento non valido.", Alert.AlertType.ERROR);
            return false;
        }

        boolean eliminato = GestioneEventoDAO.eliminaEvento(idEvento, idUtente, session.isPersistence());
        if (eliminato) {
            showAlert("Successo", "Evento eliminato con successo.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Errore", "Errore durante l'eliminazione dell'evento.", Alert.AlertType.ERROR);
        }

        return eliminato;
    }

    // Metodo per popolare i campi dell'evento da modificare
    public EventoBean inizializzaEvento(long idEvento) {

        Evento evento = GestioneEventoDAO.getEventoById(idEvento, session.isPersistence());
        if (evento == null) {
            return null; // evento non trovato, puoi gestire l'errore in altro modo
        }
        // Creare una bean per il trasferimento
        EventoBean bean = new EventoBean();

        bean.setTitolo(bean.getTitolo());
        bean.setDescrizione(bean.getDescrizione());
        bean.setData(bean.getData());
        bean.setOrario(bean.getOrario());
        return bean;
    }
    public boolean aggiornaEvento(EventoBean updatedBean, long idEvento) {
        // Aggiorna i dati dell'evento
        StringBuilder errori = new StringBuilder();

            // Recupera l'evento corrente
            Evento eventoEsistente = GestioneEventoDAO.getEventoById(idEvento, session.isPersistence());

            if (eventoEsistente == null) {
                errori.append("L'evento non esiste o non è stato trovato.\n");
            }

            // Validazione titolo
            if (updatedBean.getTitolo() == null || updatedBean.getTitolo().trim().isEmpty()) {
                errori.append("Il titolo non può essere vuoto.\n");
            } else if (updatedBean.getTitolo().length() < 3 || updatedBean.getTitolo().length() > 50) {
                errori.append("Il titolo deve avere una lunghezza compresa tra 3 e 50 caratteri.\n");
            }

            // Validazione descrizione
            if (updatedBean.getDescrizione() == null || updatedBean.getDescrizione().trim().isEmpty()) {
                errori.append("La descrizione non può essere vuota.\n");
            } else if (updatedBean.getDescrizione().length() > 500) {
                errori.append("La descrizione non può superare i 500 caratteri.\n");
            }

            // Validazione data
            if (updatedBean.getData() == null) {
                errori.append("La data non può essere vuota.\n");
            } else if (!updatedBean.getData().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                    errori.append("La data deve essere nel formato 'yyyy-MM-dd' (es. 2025-01-26).");
            }

            // validazione orario

            if (updatedBean.getOrario() == null || updatedBean.getOrario().isEmpty()) {
              errori.append("L'orario dell'evento è obbligatorio.\n");
            } else if (!updatedBean.getOrario().matches ("^([01]\\d|2[0-3]):([0-5]\\d)$")){
              errori.append("L'ora deve essere nel formato 'HH:mm' (es. 14:30).");
           }

            if (errori.length() > 0) {
              showAlert("Errore", errori.toString(), Alert.AlertType.WARNING);
            return false;
            }

            // Crea un nuovo modello con i dati aggiornati
            Evento eventoAggiornato = new Evento(
                    updatedBean.getTitolo(),
                    updatedBean.getDescrizione(),
                    updatedBean.getData(),
                    updatedBean.getOrario(),
                    eventoEsistente.getLimitePartecipanti(),
                    eventoEsistente.getIscritti(),
                    eventoEsistente.getLink(),
                    eventoEsistente.getNomeOrganizzatore(),
                    eventoEsistente.getCognomeOrganizzatore(),
                    eventoEsistente.getStato(),
                    eventoEsistente.getIdEvento(),
                    eventoEsistente.getIdOrganizzatore()
            );
            GestioneEventoDAO.aggiornaEvento(eventoAggiornato, session.isPersistence());
            return true;
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
