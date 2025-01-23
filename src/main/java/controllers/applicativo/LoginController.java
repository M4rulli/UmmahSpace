package controllers.applicativo;

import controllers.grafico.GestioneTrackerGUIController;
import controllers.grafico.LoginGUIController;
import engclasses.beans.GestioneTrackerBean;
import engclasses.beans.LoginBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.dao.PartecipanteDAO;
import javafx.scene.control.Alert;
import misc.GestioneTrackerBeanFactory;
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
            return GestioneTrackerBeanFactory.createTrackerBeanFromFactory(tracker);
        } else {
            System.out.println("Nessun tracker trovato per l'utente: " + partecipante.getIdUtente());
            return null;
        }
    }

}