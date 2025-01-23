package controllers.applicativo;

import engclasses.beans.RegistrazioneBean;
import engclasses.dao.PartecipanteDAO;
import javafx.scene.control.Alert;
import misc.Session;
import model.Partecipante;

public class GestisciProfiloPartecipanteController {

    private final Session session;

    public GestisciProfiloPartecipanteController(Session session) {
        this.session = session;
    }

    // Metodo per popolare i campi del profilo utente
    public RegistrazioneBean inizializzaProfilo(String idUtente) {

        // Recupero dei dati
        Partecipante partecipante = PartecipanteDAO.selezionaPartecipante("idUtente", idUtente, session.isPersistence());

        // Creare una bean per il trasferimento
        RegistrazioneBean bean = new RegistrazioneBean();
        bean.setNome(partecipante.getNome());
        bean.setCognome(partecipante.getCognome());
        bean.setUsername(partecipante.getUsername());
        bean.setEmail(partecipante.getEmail());

        return bean;
    }

    // Aggiorna i dati del profilo, incluso l'username
    public boolean aggiornaProfiloPartecipante(RegistrazioneBean updatedBean, String currentPassword, String newPassword, String confirmPassword) {
        boolean persistence = session.isPersistence();
        StringBuilder errori = new StringBuilder();

        // Verifica se il partecipante esiste
        Partecipante partecipanteEsistente = PartecipanteDAO.selezionaPartecipante("idUtente", session.getIdUtente(), persistence);
        if (partecipanteEsistente == null) {
            System.out.println("Partecipante non trovato con username: " + session.getCurrentUsername());
            return false;
        }

        // Validazione nome
        if (updatedBean.getNome() == null || updatedBean.getNome().trim().isEmpty()) {
            errori.append("Il nome non può essere vuoto.\n");
        }

        // Validazione cognome
        if (updatedBean.getCognome() == null || updatedBean.getCognome().trim().isEmpty()) {
            errori.append("Il cognome non può essere vuoto.\n");
        }

        // Validazione username
        if (updatedBean.getUsername() == null || updatedBean.getUsername().trim().isEmpty()) {
            errori.append("L'username non può essere vuoto.\n");
        } else if (!partecipanteEsistente.getUsername().equals(updatedBean.getUsername())) {
            Partecipante partecipanteConNuovoUsername = PartecipanteDAO.selezionaPartecipante("username", updatedBean.getUsername(), persistence);
            if (partecipanteConNuovoUsername != null) {
                errori.append("Username già in uso. \n");
            }
        }

        // Validazione email
        if (updatedBean.getEmail() == null || updatedBean.getEmail().trim().isEmpty()) {
            errori.append("L'email non può essere vuota.\n");
        } else if (!updatedBean.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errori.append("Il formato dell'email non è valido.\n");
        } else if (!partecipanteEsistente.getEmail().equals(updatedBean.getEmail())) {
            Partecipante partecipanteConNuovaEmail = PartecipanteDAO.selezionaPartecipante("email", updatedBean.getEmail(), persistence);
            if (partecipanteConNuovaEmail != null) {
                errori.append("Email già in uso.\n");
            }
        }

        // Se ci sono errori, mostra una finestra di dialogo e ritorna false
        if (errori.length() > 0) {
            showAlert("Errore", errori.toString(), Alert.AlertType.WARNING);
            return false;
        }

        // Verifica se i campi della password sono stati compilati
        if (currentPassword != null && !currentPassword.isEmpty() &&
                newPassword != null && !newPassword.isEmpty() &&
                confirmPassword != null && !confirmPassword.isEmpty()) {

            // Cambia la password se i campi sono compilati
            boolean passwordChanged = changePassword(currentPassword, newPassword, confirmPassword, session.getCurrentUsername());
            if (!passwordChanged) {
                return false; // Interrompi se il cambio password fallisce
            }
        }

        // Crea un nuovo modello Partecipante con i dati aggiornati
        Partecipante partecipanteAggiornato = new Partecipante(
                partecipanteEsistente.getIdUtente(),
                updatedBean.getNome(),
                updatedBean.getCognome(),
                updatedBean.getUsername(),
                updatedBean.getEmail(),
                updatedBean.getPassword(),
                partecipanteEsistente.isStato() // Mantieni lo stato originale
        );

        // Passa il nuovo modello alla DAO per l'aggiornamento
        if (!PartecipanteDAO.aggiornaPartecipante(partecipanteAggiornato, persistence)) {
            System.out.println("Errore durante l'aggiornamento del partecipante.");
            return false;
        }

        // Aggiorna il currentUsername nella sessione se necessario
        if (!session.getCurrentUsername().equals(updatedBean.getUsername())) {
            session.setCurrentUsername(updatedBean.getUsername());
        }

        return true;
    }

    // Modifica la password del partecipante
    public boolean changePassword(String currentPassword, String newPassword, String confirmPassword, String username) {
        boolean persistence = session.isPersistence();

        // Recupera il partecipante esistente
        Partecipante partecipanteEsistente = PartecipanteDAO.selezionaPartecipante("idUtente", session.getIdUtente(), persistence);
        if (partecipanteEsistente == null) {
            showAlert("Errore", "Partecipante non trovato.", Alert.AlertType.ERROR);
            return false;
        }

        // Controlla se la vecchia password corrisponde
        if (!partecipanteEsistente.getPassword().equals(currentPassword)) {
            showAlert("Errore", "La vecchia password non è corretta.", Alert.AlertType.ERROR);
            return false;
        }

        // Controlla se la nuova password coincide con la conferma
        if (!newPassword.equals(confirmPassword)) {
            showAlert("Errore", "La nuova password e la conferma non coincidono.", Alert.AlertType.ERROR);
            return false;
        }

        // Controlla se la nuova password è uguale alla vecchia
        if (currentPassword.equals(newPassword)) {
            showAlert("Errore", "La nuova password non può essere uguale a quella attuale.", Alert.AlertType.ERROR);
            return false;
        }

        // Crea un nuovo modello di partecipante con la password aggiornata
        Partecipante partecipanteAggiornato = new Partecipante(
                partecipanteEsistente.getIdUtente(),
                partecipanteEsistente.getNome(),
                partecipanteEsistente.getCognome(),
                partecipanteEsistente.getUsername(),
                partecipanteEsistente.getEmail(),
                newPassword, // Nuova password
                partecipanteEsistente.isStato()
        );

        // Passa il nuovo modello alla DAO per l'aggiornamento
        if (!PartecipanteDAO.aggiornaPartecipante(partecipanteAggiornato, persistence)) {
            showAlert("Errore", "Impossibile aggiornare la password.", Alert.AlertType.ERROR);
            return false;
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
