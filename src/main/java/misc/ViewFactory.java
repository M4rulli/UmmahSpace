package misc;

import controllers.grafico.*;
import engclasses.beans.EventoBean;
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
        stage.close();
    }

    public void showRegistration(Session session) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistrazioneView.fxml"));
        loader.setController(new RegistrazioneGUIController(session));
        showStage(loader, "UmmahSpace - Registrazione");
    }

    public void showLogin(Session session) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
        loader.setController(new LoginGUIController(session));
        showStage(loader, "UmmahSpace - Accesso");
    }

    public void showMainView(Session session) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        loader.setController(new MainViewGUIController(session));
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

    public void loadTrackerView(Pane parentContainer, Session session) {
        if (!session.isOrganizzatore()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/TrackerView.fxml"));
                GestioneTrackerGUIController trackerController = new GestioneTrackerGUIController(session);
                loader.setController(trackerController);
                Parent trackerView = loader.load();
                // Svuota il contenitore e aggiunge il contenuto del Tracker
                parentContainer.getChildren().clear();
                parentContainer.getChildren().add(trackerView);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Errore durante il caricamento di TrackerView.fxml", e);
            }
        }
    }

    public void loadListaEventiView(Pane parentContainer, Session session) {
        if (session.isOrganizzatore()) {
            try {
                // Carica la vista per la lista degli eventi
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListaEventiView.fxml"));
                GestioneListaEventiGUIController listaEventiController = new GestioneListaEventiGUIController(session);
                loader.setController(listaEventiController);
                Parent listaEventiView = loader.load();
                // Svuota il contenitore e aggiunge la lista degli eventi
                parentContainer.getChildren().clear();
                parentContainer.getChildren().add(listaEventiView);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Errore durante il caricamento di ListaEventiView.fxml", e);
            }
        }

    }

    public void showSettings(Session session) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestisciProfiloView.fxml"));
        loader.setController(new GestisciProfiloGUIController(session));
        showStage(loader, "Gestione Profilo");
    }

    public void showEventiGiornalieri(Session session, List<EventoBean> eventiDelGiorno) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventiGiornalieriView.fxml"));
            loader.setController(new EventiGiornalieriGUIController(session, eventiDelGiorno));
            Parent root = loader.load();

            // Configura la scena
            Stage stage = new Stage();
            stage.setTitle("Eventi Giornalieri");
            stage.setResizable(false);
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento della finestra Eventi Giornalieri", e);
        }
    }

    public void showEventDetailsView(EventoBean evento, Session session) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DettagliEventoView.fxml"));
        loader.setController(new DettagliEventoGUIController(evento, session));
        showStage(loader, "Dettagli Evento");
    }

    public void showAggiungiEvento(Session session) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AggiungiEventoView.fxml"));
            loader.setController(new AggiungiEventoGUIController(session)); // Passa la ViewFactory
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Aggiungi un evento");
            stage.setResizable(false);
            Scene scene = new Scene(root, 500, 450);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento della finestra Eventi Organizzatore", e);
        }

    }
}