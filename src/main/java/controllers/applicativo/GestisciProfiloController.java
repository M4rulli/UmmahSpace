package controllers.applicativo;

import engclasses.beans.RegistrazioneBean;
import engclasses.dao.OrganizzatoreDAO;
import engclasses.dao.PartecipanteDAO;
import javafx.scene.control.Alert;
import misc.Session;
import model.Partecipante;
import model.Organizzatore;

public class GestisciProfiloController {

    private final Session session;

    public GestisciProfiloController(Session session) {
        this.session = session;
    }

    // Metodo per popolare i campi del profilo utente
    public RegistrazioneBean inizializzaProfilo(String idUtente) {

        // Recupero dei dati (controlla se è un partecipante o un organizzatore)
        Object utente;
        if (session.isOrganizzatore()) {
            utente = OrganizzatoreDAO.selezionaOrganizzatore("idUtente", idUtente, session.isPersistence());
        } else {
            utente = PartecipanteDAO.selezionaPartecipante("idUtente", idUtente, session.isPersistence());
        }

        // Creare una bean per il trasferimento
        RegistrazioneBean bean = new RegistrazioneBean();
        if (utente instanceof Partecipante) {
            Partecipante partecipante = (Partecipante) utente;
            bean.setNome(partecipante.getNome());
            bean.setCognome(partecipante.getCognome());
            bean.setUsername(partecipante.getUsername());
            bean.setEmail(partecipante.getEmail());
        } else if (utente != null) {
            Organizzatore organizzatore = (Organizzatore) utente;
            bean.setNome(organizzatore.getNome());
            bean.setCognome(organizzatore.getCognome());
            bean.setUsername(organizzatore.getUsername());
            bean.setEmail(organizzatore.getEmail());
        }

        return bean;
    }

    // Aggiorna i dati del profilo
    public boolean aggiornaProfilo(RegistrazioneBean updatedBean, String currentPassword, String newPassword, String confirmPassword) {
        boolean persistence = session.isPersistence();
        StringBuilder errori = new StringBuilder();

        // Verifica se l'utente è un partecipante o un organizzatore
        Object utenteEsistente;
        if (session.isOrganizzatore()) {
            utenteEsistente = OrganizzatoreDAO.selezionaOrganizzatore("idUtente", session.getIdUtente(), persistence);
        } else {
            utenteEsistente = PartecipanteDAO.selezionaPartecipante("idUtente", session.getIdUtente(), persistence);
        }

        if (utenteEsistente == null) {
            System.out.println("Utente non trovato con idUtente: " + session.getIdUtente());
            return false;
        }

        // Validazione e aggiornamento dei dati
        if (updatedBean.getNome() == null || updatedBean.getNome().trim().isEmpty()) {
            errori.append("Il nome non può essere vuoto.\n");
        }

        if (updatedBean.getCognome() == null || updatedBean.getCognome().trim().isEmpty()) {
            errori.append("Il cognome non può essere vuoto.\n");
        }

        if (updatedBean.getUsername() == null || updatedBean.getUsername().trim().isEmpty()) {
            errori.append("L'username non può essere vuoto.\n");
        }

        if (updatedBean.getEmail() == null || updatedBean.getEmail().trim().isEmpty()) {
            errori.append("L'email non può essere vuota.\n");
        } else if (!updatedBean.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errori.append("Il formato dell'email non è valido.\n");
        }

        // Se ci sono errori, mostra una finestra di dialogo e ritorna false
        if (errori.length() > 0) {
            showAlert("Errore", errori.toString(), Alert.AlertType.WARNING);
            return false;
        }

        // Cambio password se necessario
        if (currentPassword != null && !currentPassword.isEmpty() &&
                newPassword != null && !newPassword.isEmpty() &&
                confirmPassword != null && !confirmPassword.isEmpty()) {
            boolean passwordChanged = changePassword(currentPassword, newPassword, confirmPassword, session.getCurrentUsername());
            if (!passwordChanged) {
                return false;
            }
        }

        // Crea un nuovo modello con i dati aggiornati
        if (utenteEsistente instanceof Partecipante) {
            Partecipante partecipanteAggiornato = new Partecipante(
                    ((Partecipante) utenteEsistente).getIdUtente(),
                    updatedBean.getNome(),
                    updatedBean.getCognome(),
                    updatedBean.getUsername(),
                    updatedBean.getEmail(),
                    updatedBean.getPassword(),
                    ((Partecipante) utenteEsistente).isStato()
            );
            if (!PartecipanteDAO.aggiornaPartecipante(partecipanteAggiornato, persistence)) {
                System.out.println("Errore durante l'aggiornamento del partecipante.");
                return false;
            }
        } else {
            Organizzatore organizzatoreAggiornato = new Organizzatore(
                    ((Organizzatore) utenteEsistente).getIdUtente(),
                    updatedBean.getNome(),
                    updatedBean.getCognome(),
                    updatedBean.getUsername(),
                    updatedBean.getEmail(),
                    updatedBean.getPassword(),
                    true
            );
            if (!OrganizzatoreDAO.aggiornaOrganizzatore(organizzatoreAggiornato, persistence)) {
                System.out.println("Errore durante l'aggiornamento dell'organizzatore.");
                return false;
            }
        }

        return true;
    }

    // Modifica la password
    public boolean changePassword(String currentPassword, String newPassword, String confirmPassword, String username) {
        boolean persistence = session.isPersistence();

        Object utenteEsistente;
        if (session.isOrganizzatore()) {
            utenteEsistente = OrganizzatoreDAO.selezionaOrganizzatore("idUtente", session.getIdUtente(), persistence);
        } else {
            utenteEsistente = PartecipanteDAO.selezionaPartecipante("idUtente", session.getIdUtente(), persistence);
        }

        if (utenteEsistente == null) {
            showAlert("Errore", "Utente non trovato.", Alert.AlertType.ERROR);
            return false;
        }

        if (utenteEsistente instanceof Partecipante) {
            Partecipante partecipante = (Partecipante) utenteEsistente;
            if (!partecipante.getPassword().equals(currentPassword)) {
                showAlert("Errore", "La vecchia password non è corretta.", Alert.AlertType.ERROR);
                return false;
            }
        } else {
            Organizzatore organizzatore = (Organizzatore) utenteEsistente;
            if (!organizzatore.getPassword().equals(currentPassword)) {
                showAlert("Errore", "La vecchia password non è corretta.", Alert.AlertType.ERROR);
                return false;
            }
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Errore", "La nuova password e la conferma non coincidono.", Alert.AlertType.ERROR);
            return false;
        }

        if (currentPassword.equals(newPassword)) {
            showAlert("Errore", "La nuova password non può essere uguale a quella attuale.", Alert.AlertType.ERROR);
            return false;
        }

        if (utenteEsistente instanceof Partecipante) {
            Partecipante partecipanteAggiornato = new Partecipante(
                    ((Partecipante) utenteEsistente).getIdUtente(),
                    ((Partecipante) utenteEsistente).getNome(),
                    ((Partecipante) utenteEsistente).getCognome(),
                    ((Partecipante) utenteEsistente).getUsername(),
                    ((Partecipante) utenteEsistente).getEmail(),
                    newPassword,
                    ((Partecipante) utenteEsistente).isStato()
            );
            if (!PartecipanteDAO.aggiornaPartecipante(partecipanteAggiornato, persistence)) {
                showAlert("Errore", "Impossibile aggiornare la password.", Alert.AlertType.ERROR);
                return false;
            }
        } else {
            Organizzatore organizzatoreAggiornato = new Organizzatore(
                    ((Organizzatore) utenteEsistente).getIdUtente(),
                    ((Organizzatore) utenteEsistente).getNome(),
                    ((Organizzatore) utenteEsistente).getCognome(),
                    ((Organizzatore) utenteEsistente).getUsername(),
                    ((Organizzatore) utenteEsistente).getEmail(),
                    newPassword,
                    true
            );
            if (!OrganizzatoreDAO.aggiornaOrganizzatore(organizzatoreAggiornato, persistence)) {
                showAlert("Errore", "Impossibile aggiornare la password.", Alert.AlertType.ERROR);
                return false;
            }
        }

        System.out.println("Password aggiornata con successo per l'utente: " + username);
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
