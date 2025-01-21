package misc;

import controllers.applicativo.IscrizioneEventoController;
import controllers.grafico.*;
import engclasses.beans.EventoBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.dao.OrganizzatoreDAO;
import engclasses.dao.PartecipanteDAO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * La classe ViewFactory è responsabile della gestione e creazione delle viste
 * grafiche (FXML) dell'applicazione. Utilizzando il pattern Factory, la classe
 * centralizza il caricamento delle viste e l'associazione dei relativi controller.

 * Questa classe è strettamente legata al Model, che fornisce un punto
 * di accesso unico a ViewFactory.
 */

public class ViewFactory {


    private void showStage(FXMLLoader loader, String title) {
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento della finestra: " + title, e);
        }
    }

    public void closeStage(Stage stage) {
        if (stage != null) {
            stage.close();
        } else {
            System.err.println("Stage è null, impossibile chiudere la finestra.");
        }
    }

    public void showRegistration(Session session, PartecipanteDAO partecipanteDAO, OrganizzatoreDAO organizzatoreDAO) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistrazioneView.fxml"));
        loader.setController(new RegistrazioneGUIController(session, partecipanteDAO, organizzatoreDAO)); // Iniettare il controller personalizzato
        showStage(loader, "UmmahSpace - Registrazione");
    }

    public void showLogin(Session session, PartecipanteDAO partecipanteDAO, OrganizzatoreDAO organizzatoreDAO) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
        loader.setController(new LoginGUIController(session, partecipanteDAO, organizzatoreDAO)); // Iniettare il controller personalizzato
        showStage(loader, "UmmahSpace - Accesso");
    }

    public void showMainView(Session session, PartecipanteDAO partecipanteDAO, String username) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            loader.setController(new MainViewGUIController(session, partecipanteDAO, username));
            showStage(loader, "UmmahSpace");
    }

    public void loadCalendarioView(Pane parentContainer, Session session) {
        try {
            // Crea e configura il loader per il file FXML del calendario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CalendarioView.fxml"));
            loader.setController(new controllers.grafico.CalendarioGUIController(session)); // Associa il controller personalizzato

            // Carica la vista dal file FXML
            Parent calendarioView = loader.load();

            // Svuota il contenitore e aggiunge la vista caricata
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(calendarioView);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento di CalendarioView.", e);
        }
    }

    public void loadTrackerView(Pane parentContainer, Session session, PartecipanteDAO partecipanteDAO, String currentUsername, GestioneTrackerDAO trackerDAO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TrackerView.fxml"));
            loader.setController(new controllers.grafico.GestioneTrackerGUIController(session, partecipanteDAO, currentUsername, trackerDAO));
            Parent trackerView = loader.load();

            // Svuota il contenitore e aggiunge il contenuto del Tracker
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(trackerView);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento di TrackerView.fxml", e);
        }
    }

    public void showSettings(Session session, PartecipanteDAO partecipanteDAO, String username) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestisciProfiloView.fxml"));
        loader.setController(new GestisciProfiloPartecipanteGUIController(session, partecipanteDAO, username)); // Iniettare il controller personalizzato
        showStage(loader, "Gestione Profilo");
    }

    public void showEventiGiornalieri(List<EventoBean> eventi, IscrizioneEventoController iscrizioneEventoController, Session session) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventiGiornalieriView.fxml"));
            loader.setController(new EventiGiornalieriGUIController(eventi, iscrizioneEventoController, session)); // Inietta il controller con i dati
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Eventi Giornalieri");
            stage.setScene(new Scene(root, 400, 450)); // Imposta dimensioni predefinite
            stage.setResizable(false); // Rendi la finestra non ridimensionabile
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento della finestra Eventi Giornalieri", e);
        }
    }

    public void showEventDetailsView(EventoBean evento, IscrizioneEventoController iscrizioneEventoController, Session session) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DettagliEventoView.fxml"));
        loader.setController(new DettagliEventoGUIController(evento, iscrizioneEventoController, session )); // Iniettare il controller personalizzato
        showStage(loader, "Dettagli Evento");
    }
}