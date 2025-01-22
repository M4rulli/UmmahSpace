package controllers.applicativo;

import controllers.grafico.GestioneTrackerGUIController;
import controllers.grafico.LoginGUIController;
import engclasses.beans.GestioneTrackerBean;
import engclasses.beans.LoginBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.dao.PartecipanteDAO;
import javafx.scene.control.Alert;
import misc.Model;
import misc.Session;
import model.Partecipante;
import model.Tracker;

public class LoginController {

    public GestioneTrackerBean effettuaLogin(LoginBean loginBean, boolean persistence) {

        // Validazione dei campi
        StringBuilder errori = new StringBuilder();
        if (loginBean.getUsername() == null || loginBean.getUsername().trim().isEmpty()) {
            errori.append("Il campo username non può essere vuoto.\n");
        }
        if (loginBean.getPassword() == null || loginBean.getPassword().trim().isEmpty()) {
            errori.append("Il campo password non può essere vuoto.\n");
        }
        if (!errori.isEmpty()) {
            // Mostra gli errori accumulati
            LoginGUIController.showAlert("Errore di Login", errori.toString(), Alert.AlertType.ERROR);
            return null;
        }

        // Recupera il partecipante dal DAO
        Partecipante partecipante = PartecipanteDAO.selezionaPartecipante("username", loginBean.getUsername(), persistence);
        if (partecipante == null || !partecipante.getPassword().equals(loginBean.getPassword())) {
            LoginGUIController.showAlert("Errore di Login", "Credenziali non valide: controlla username e password.", Alert.AlertType.ERROR);
            return null;
        }

        // Recupera il Tracker per l'utente
        Tracker tracker = GestioneTrackerDAO.getTracker(partecipante.getIdUtente(), persistence);

        // Crea una GestioneTrackerBean con i dati del Tracker
        if (tracker != null) {
            GestioneTrackerBean trackerBean = new GestioneTrackerBean();
            trackerBean.setIdUtente(partecipante.getIdUtente());
            trackerBean.setLetturaCorano(tracker.getLetturaCorano());
            trackerBean.setGoal(tracker.getGoal());
            trackerBean.setHaDigiunato(tracker.isHaDigiunato());
            trackerBean.setNoteDigiuno(tracker.getNoteDigiuno());
            trackerBean.setMotivazioniDigiuno(tracker.getMotivazioniDigiuno());
            trackerBean.setPreghiera("Fajr", tracker.getPreghiera("Fajr"));
            trackerBean.setPreghiera("Dhuhr", tracker.getPreghiera("Dhuhr"));
            trackerBean.setPreghiera("Asr", tracker.getPreghiera("Asr"));
            trackerBean.setPreghiera("Maghrib", tracker.getPreghiera("Maghrib"));
            trackerBean.setPreghiera("Isha", tracker.getPreghiera("Isha"));

            return trackerBean;
        } else {
            System.out.println("Nessun tracker trovato per l'utente: " + partecipante.getIdUtente());
            return null;
        }
    }

}