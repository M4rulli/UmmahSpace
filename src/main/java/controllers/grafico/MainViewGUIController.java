package controllers.grafico;

import engclasses.beans.GestioneTrackerBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.dao.PartecipanteDAO;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.util.Duration;
import misc.Session;
import model.Partecipante;
import controllers.applicativo.GestioneTrackerController;

import java.util.Optional;

public class MainViewGUIController {

    private final Session session;
    private final PartecipanteDAO partecipanteDAO;
    private final String currentUsername;

    @FXML
    public Label pagesReadLabel;
    @FXML
    public Button setGoalButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private ProgressBar quranProgressBar;
    @FXML
    private Label goalLabel;
    @FXML
    private Button addReadingButton;
    @FXML
    private Circle fajrCircle, dhuhrCircle, asrCircle, maghribCircle, ishaCircle;
    @FXML
    private Button fajrButton, dhuhrButton, asrButton, maghribButton, ishaButton;
    @FXML
    private Label fajrCheck, dhuhrCheck, asrCheck, maghribCheck, ishaCheck;

    ;

    public MainViewGUIController(Session session, PartecipanteDAO partecipanteDAO, String username) {
        this.session = session;
        this.partecipanteDAO = partecipanteDAO;
        this.currentUsername = username;

    }

    public void initialize() {
        setWelcomeMessage(currentUsername);
        // Imposta eventi per i pulsanti del tracker
        addReadingButton.setOnAction(event -> onAddReadingClicked());
        setupPrayerButtons();
    }

    public void setWelcomeMessage(String username) {
        boolean persistence = session.isPersistence();
        Partecipante partecipante = partecipanteDAO.selezionaPartecipante(username, persistence);

        if (partecipante != null) {
            welcomeLabel.setText("Benvenuto, " + partecipante.getNome() + "!");
        } else {
            System.out.println("Errore: Partecipante non trovato.");
            welcomeLabel.setText("Errore: Partecipante non trovato.");
        }
    }

    @FXML
    public void onProfileButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestisciProfilo.fxml"));
            loader.setControllerFactory(param -> new GestisciProfiloPartecipanteGUIController(session, partecipanteDAO, currentUsername));

            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestisci Profilo");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddReadingClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Aggiungi Lettura");
        dialog.setHeaderText("Aggiungi il numero di pagine lette oggi");
        dialog.setContentText("Numero di pagine:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(pagesStr -> {
            try {
                int pages = Integer.parseInt(pagesStr);

                if (pages <= 0) {
                    showAlert("Errore", "Il numero di pagine deve essere maggiore di zero.");
                    return;
                }

                // Crea un'istanza del controller applicativo
                GestioneTrackerController trackerController = new GestioneTrackerController(new GestioneTrackerDAO(), session);

                // Aggiungi le pagine lette
                trackerController.addReading(pages);

                // Recupera i dati aggiornati e aggiorna l'interfaccia grafica
                GestioneTrackerBean tracker = trackerController.getTrackerData(session.getIdUtente());
                updateTrackerUI(tracker);

            } catch (NumberFormatException e) {
                showAlert("Errore", "Inserisci un numero valido.");
            } catch (IllegalArgumentException e) {
                showAlert("Errore", e.getMessage());
            }
        });
    }

    @FXML
    private void onSetGoalClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Imposta Obiettivo");
        dialog.setHeaderText("Inserisci un nuovo obiettivo giornaliero");
        dialog.setContentText("Numero di pagine:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(goalStr -> {
            try {
                int goal = Integer.parseInt(goalStr);

                if (goal <= 0) {
                    showAlert("Errore", "L'obiettivo deve essere maggiore di zero.");
                    return;
                }

                // Imposta il nuovo obiettivo
                GestioneTrackerController trackerController = new GestioneTrackerController(new GestioneTrackerDAO(), session);
                trackerController.setDailyGoal(goal, session.getIdUtente());

                // Recupera i dati aggiornati e aggiorna la UI
                GestioneTrackerBean tracker = trackerController.getTrackerData(session.getIdUtente());
                updateTrackerUI(tracker);

            } catch (NumberFormatException e) {
                showAlert("Errore", "Inserisci un numero valido.");
            }
        });
    }


    private void updateTrackerUI(GestioneTrackerBean tracker) {
        // Verifica che il tracker non sia nullo
        if (tracker == null) {
            System.err.println("Errore: Tracker nullo.");
            return;
        }

        // Ottieni i dati dal tracker
        int goal = tracker.getGoal();
        int pagesRead = tracker.getLetturaCorano();

        // Calcola il progresso della barra in base all'obiettivo
        double progress = (goal > 0) ? (double) pagesRead / goal : 0.0;
        progress = Math.min(progress, 1.0); // Limita il progresso a 100%

        // Aggiorna la barra di progresso
        quranProgressBar.setPrefWidth(300);
        quranProgressBar.setMinWidth(300);
        quranProgressBar.setMaxWidth(300);
        quranProgressBar.setProgress(progress);
        quranProgressBar.setStyle("-fx-accent: gold;"); // Cambia colore in oro

        // Aggiorna le etichette
        goalLabel.setText("Obiettivo giornaliero: " + goal + " pagine");
        pagesReadLabel.setText("Pagine lette: " + pagesRead);

        // Log di debug (può essere rimosso)
        System.out.println("UI aggiornata: Goal = " + goal + ", Pagine lette = " + pagesRead + ", Progresso = " + progress);
    }

    private void setupPrayerButtons() {
        setupPrayerButton(fajrButton, fajrCircle, fajrCheck);
        setupPrayerButton(dhuhrButton, dhuhrCircle, dhuhrCheck);
        setupPrayerButton(asrButton, asrCircle, asrCheck);
        setupPrayerButton(maghribButton, maghribCircle, maghribCheck);
        setupPrayerButton(ishaButton, ishaCircle, ishaCheck);
    }


    private void setupPrayerButton(Button button, Circle circle, Label check) {
        button.setOnAction(event -> {
            if (circle.getFill().equals(javafx.scene.paint.Color.GOLD)) {
                circle.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                check.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.5);"); // Check
            } else {
                circle.setFill(javafx.scene.paint.Color.GOLD);
                highlightCircle(circle); // Effetto visivo
                check.setStyle("-fx-text-fill: white;"); // Check visibile
            }
        });
    }

    private void highlightCircle(Circle circle) {
        FadeTransition fade = new FadeTransition(Duration.millis(200), circle);
        fade.setFromValue(0.7);
        fade.setToValue(1.0);
        fade.setCycleCount(2);
        fade.setAutoReverse(true);
        fade.play();
    }




    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

