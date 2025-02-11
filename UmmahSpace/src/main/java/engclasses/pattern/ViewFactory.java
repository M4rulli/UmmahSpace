package engclasses.pattern;

import controllers.grafico.gui.*;
import engclasses.exceptions.ViewFactoryException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import misc.Session;

import java.io.IOException;

/**
 * La classe ViewFactory è responsabile della gestione e creazione delle viste
 * grafiche (FXML) dell'applicazione. Utilizzando il pattern Factory, la classe
 * centralizza il caricamento delle viste e l'associazione dei relativi controller.

 * Questa classe è strettamente legata al Model, che fornisce un punto
 * di accesso unico a ViewFactory.
 */

public class ViewFactory {


    private void showStage(FXMLLoader loader, String title) throws ViewFactoryException {
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new ViewFactoryException("Errore durante il caricamento della finestra: " + title, e);
        }
    }

    public void closeStage(Stage stage) {
        stage.close();
    }

    public void showRegistration(Session session) throws ViewFactoryException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegistrazioneView.fxml"));
        loader.setController(new RegistrazioneGUIController(session));
        showStage(loader, "UmmahSpace - Registrazione");
    }

    public void showLogin(Session session) throws ViewFactoryException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
        loader.setController(new LoginGUIController(session));
        showStage(loader, "UmmahSpace - Accesso");
    }

    public void showMainView(Session session) throws ViewFactoryException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        loader.setController(new MainViewGUIController(session));
        Parent root;
        try {
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("UmmahSpace");
            stage.setResizable(false);
            Scene scene = new Scene(root, 900, 800);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new ViewFactoryException("Errore durante il caricamento della vista principale", e);
        }
    }

    public void loadCalendarioView(Pane parentContainer, Session session) throws ViewFactoryException {
        try {
            // Crea e configura il loader per il file FXML del calendario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CalendarioView.fxml"));
            loader.setController(new CalendarioGUIController(session)); // Associa il controller personalizzato

            // Carica la vista dal file FXML
            Parent calendarioView = loader.load();

            // Svuota il contenitore e aggiunge la vista caricata
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(calendarioView);

        } catch (IOException e) {
            throw new ViewFactoryException("Errore durante il caricamento di CalendarioView.", e);
        }
    }

    public void loadTrackerView(Pane parentContainer, Session session) throws ViewFactoryException {
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
                throw new ViewFactoryException("Errore durante il caricamento di TrackerView.", e);
            }
        }
    }

    public void loadListaEventiView(Pane parentContainer, Session session) throws ViewFactoryException {
        try {
            FXMLLoader loader;
            Parent view;

            if (session.isOrganizzatore()) {
                // Carica la vista per l'organizzatore
                loader = new FXMLLoader(getClass().getResource("/ListaEventiView.fxml"));
                GestioneListaEventiGUIController listaEventiController = new GestioneListaEventiGUIController(session);
                loader.setController(listaEventiController);
            } else {
                // Carica la vista per il partecipante
                loader = new FXMLLoader(getClass().getResource("/PartecipazioniView.fxml"));
                PartecipazioniGUIController partecipazioniGUIController = new PartecipazioniGUIController(session);
                loader.setController(partecipazioniGUIController);
            }
            view = loader.load();

            // Svuota il contenitore e aggiunge la vista caricata
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(view);

        } catch (IOException e) {
            throw new ViewFactoryException("Errore durante il caricamento della ListaEventiView.", e);
        }
    }

    public void showSettings(Session session) throws ViewFactoryException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestisciProfiloView.fxml"));
        loader.setController(new GestisciProfiloGUIController(session));
        showStage(loader, "Gestione Profilo");
    }

    public void showEventiGiornalieri(Session session) throws ViewFactoryException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventiGiornalieriView.fxml"));
            loader.setController(new EventiGiornalieriGUIController(session));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Eventi Giornalieri");
            stage.setResizable(false);
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new ViewFactoryException("Errore durante il caricamento della finestra Eventi Giornalieri", e);
        }
    }

    public void showAggiungiEvento(Session session, String selectedDate  ) throws ViewFactoryException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AggiungiEventoView.fxml"));
            loader.setController(new AggiungiEventoGUIController(session, selectedDate)); // Passa la ViewFactory
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Aggiungi un evento");
            stage.setResizable(false);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new ViewFactoryException("Errore durante il caricamento della finestra Eventi Organizzatore", e);
        }
    }

    public void showModificaEvento(Session session) throws ViewFactoryException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModificaEventoView.fxml"));
        loader.setController(new ModificaEventoGUIController(session));
        showStage(loader, "Modifica Evento");
    }

    public void showReportView(Session session) throws ViewFactoryException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReportView.fxml"));
        loader.setController(new ReportGUIController(session));
        showStage(loader, "Genera un Report");
    }

}