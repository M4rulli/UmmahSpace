package controllers.applicativo;

import engclasses.beans.RegistrazioneBean;
import engclasses.dao.PartecipanteDAO;
import javafx.scene.control.Alert;
import misc.Session;
import model.Partecipante;

public class GestisciProfiloPartecipanteController {

    private final PartecipanteDAO partecipanteDAO;
    private final Session session;

    public GestisciProfiloPartecipanteController(PartecipanteDAO partecipanteDAO, Session session) {
        this.partecipanteDAO = partecipanteDAO;
        this.session = session;
    }

    // Aggiorna i dati del profilo, incluso l'username
    public boolean updateProfileData(RegistrazioneBean updatedBean, String oldUsername, String currentPassword, String newPassword, String confirmPassword, String username) {
        boolean persistence = session.isPersistence();

        // Verifica se il partecipante con il vecchio username esiste
        Partecipante partecipante = partecipanteDAO.selezionaPartecipante(oldUsername, persistence);
        if (partecipante == null) {
            System.out.println("Partecipante non trovato con username: " + oldUsername);
            return false;
        }

        // Verifica se i campi della password sono stati compilati
        if (currentPassword != null && !currentPassword.isEmpty() &&
                newPassword != null && !newPassword.isEmpty() &&
                confirmPassword != null && !confirmPassword.isEmpty()) {

            // Cambia la password se i campi sono compilati
            boolean passwordChanged = changePassword(currentPassword, newPassword, confirmPassword, username);
            if (!passwordChanged) {
                return false; // Interrompi se il cambio password fallisce
            }
        }

        // Verifica se l'username è cambiato
        if (!oldUsername.equals(updatedBean.getUsername())) {
            // Controlla se il nuovo username è già utilizzato
            if (partecipanteDAO.selezionaPartecipante(updatedBean.getUsername(), persistence) != null) {
                System.out.println("Errore: Username già in uso: " + updatedBean.getUsername());
                return false;
            }

            // Aggiorna l'username
            if (!partecipanteDAO.updatePartecipanteUsername(oldUsername, updatedBean.getUsername())) {
                System.out.println("Errore durante l'aggiornamento dell'username.");
                return false;
            }

            // Aggiorna il currentUsername nella sessione
            session.setCurrentUsername(updatedBean.getUsername());
            System.out.println("Aggiornamento del buffer completato. Nuovo username: " + updatedBean.getUsername());
        }

        // Aggiorna gli altri dati del partecipante
        partecipante.setNome(updatedBean.getNome());
        partecipante.setCognome(updatedBean.getCognome());
        partecipante.setEmail(updatedBean.getEmail());

        partecipanteDAO.aggiungiPartecipante(partecipante, persistence);
        return true;
    }


    // Modifica la password del partecipante
    public boolean changePassword(String currentPassword, String newPassword, String confirmPassword, String username) {
        boolean persistence = session.isPersistence();
        Partecipante partecipante = partecipanteDAO.selezionaPartecipante(username, persistence);

        // Controlla se la vecchia password corrisponde
        if (!partecipante.getPassword().equals(currentPassword)) {
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

        partecipante.setPassword(newPassword);
        partecipanteDAO.aggiungiPartecipante(partecipante, persistence);

        System.out.println("Password aggiornata con successo.");
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
