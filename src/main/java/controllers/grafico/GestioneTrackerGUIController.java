package controllers.grafico;

import controllers.applicativo.GestioneTrackerController;
import engclasses.beans.GestioneTrackerBean;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import misc.DateUtil;
import misc.Session;
import org.controlsfx.control.ToggleSwitch;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GestioneTrackerGUIController {

    private final Session session;

    @FXML
    private Label dateLabel2;
    @FXML
    public Label pagesReadLabel;
    @FXML
    public Button setGoalButton;
    @FXML
    public Button salvaPreghiereButton;
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
    @FXML
    private CheckBox sunnahCheckBox;
    @FXML
    private CheckBox makeUpCheckBox;
    @FXML
    private CheckBox specificVoluntaryCheckBox;
    @FXML
    private CheckBox generalVoluntaryCheckBox;
    @FXML
    private Button salvaDigiunoButton;
    @FXML
    private ToggleSwitch fastingSwitch;
    @FXML
    private TextArea fastingNotes;

    public GestioneTrackerGUIController(Session session) {
        this.session = session;
    }

    @FXML
    private void initialize() {
        setGoalButton.setOnAction(event -> onSetGoalClicked() );
        addReadingButton.setOnAction(event -> onAddReadingClicked());
        salvaDigiunoButton.setOnAction(actionEvent -> onSalvaDigiunoClicked());
        salvaPreghiereButton.setOnAction(event -> onSalvaPreghiereClicked());
        addReadingButton.setOnAction(event -> onAddReadingClicked());
        setupPrayerButtons();
        // Blocca dimensioni della barra di progresso
        quranProgressBar.setPrefWidth(300); // Larghezza prefissata
        quranProgressBar.setPrefHeight(20); // Altezza prefissata
        quranProgressBar.setMaxWidth(300); // Massima larghezza
        quranProgressBar.setMaxHeight(20); // Massima altezza
        quranProgressBar.setMinWidth(300); // Minima larghezza
        quranProgressBar.setMinHeight(20); // Minima altezza
        // Imposta la data
        dateLabel2.setText(DateUtil.getSynchronizedDate());
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
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore");
                    alert.setHeaderText(null);
                    alert.setContentText("Il numero di pagine deve essere maggiore di zero.");
                    alert.showAndWait();
                    return;
                }

                // Crea una Bean per trasportare i dati
                GestioneTrackerBean trackerBean = new GestioneTrackerBean();
                trackerBean.setLetturaCorano(pages);

                // Passa la Bean al Controller Applicativo
                GestioneTrackerController trackerController = new GestioneTrackerController(session);
                trackerController.addReading(trackerBean);

                // Aggiorna la UI
                aggiornaBarra();

            } catch (IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText(null);
                alert.setContentText("Inserisci un numero valido.");
                alert.showAndWait();
            }
        });
    }

    @FXML
    private void onSetGoalClicked() {
        // Raccogliere i dati dall'UI
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Imposta Obiettivo");
        dialog.setHeaderText("Inserisci un nuovo obiettivo giornaliero");
        dialog.setContentText("Numero di pagine:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(goalStr -> {
            try {
                int goal = Integer.parseInt(goalStr);

                if (goal <= 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore");
                    alert.setHeaderText(null);
                    alert.setContentText("L'obiettivo deve essere maggiore di zero.");
                    alert.showAndWait();
                    return;
                }

                // Crea la Bean temporanea per trasmettere i dati
                GestioneTrackerBean trackerBean = new GestioneTrackerBean();
                trackerBean.setGoal(goal);

                // Passa la Bean al Controller Applicativo
                GestioneTrackerController trackerController = new GestioneTrackerController(session);
                trackerController.setDailyGoal(trackerBean);

                // Aggiorna la UI
                aggiornaBarra();

            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText(null);
                alert.setContentText("Inserisci un numero valido.");
                alert.showAndWait();
            }
        });
    }

    private void aggiornaBarra() {

        // Crea un'istanza del controller applicativo
        GestioneTrackerController gestioneTrackerController = new GestioneTrackerController(session);

        // Ottieni i dati del tracker tramite il controller applicativo e crea la Bean temporanea per trasmettere i dati
        GestioneTrackerBean bean = gestioneTrackerController.aggiornaBarraConProgresso(session.isPersistence());

        // Aggiorna la barra di progresso
        quranProgressBar.setProgress(bean.getProgresso());
        quranProgressBar.setStyle("-fx-accent: gold;"); // Cambia colore in oro

        // Aggiorna le etichette
        goalLabel.setText("Obiettivo giornaliero: " + bean.getGoal() + " pagine");
        pagesReadLabel.setText("Pagine lette: " + bean.getLetturaCorano());

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

    @FXML
    private void onSalvaDigiunoClicked() {
        // Raccoglie i dati dalla GUI
        boolean haDigiunato = fastingSwitch.isSelected();
        String note = fastingNotes.getText();

        Set<String> motivazioni = new HashSet<>();
        if (sunnahCheckBox.isSelected()) motivazioni.add("Sunnah");
        if (makeUpCheckBox.isSelected()) motivazioni.add("Recupero di un digiuno");
        if (specificVoluntaryCheckBox.isSelected()) motivazioni.add("Volontario Specifico");
        if (generalVoluntaryCheckBox.isSelected()) motivazioni.add("Volontario Generale");

        // Crea la Bean temporanea per trasmettere i dati
        GestioneTrackerBean bean = new GestioneTrackerBean();
        bean.setHaDigiunato(haDigiunato);
        bean.setNoteDigiuno(note);
        bean.setMotivazioniDigiuno(motivazioni);

        // Passa i dati al Controller Applicativo
        GestioneTrackerController controller = new GestioneTrackerController(session);
        controller.aggiornaDigiuno(bean);

        // Mostra un messaggio di conferma
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Salvataggio");
        alert.setHeaderText(null);
        alert.setContentText("Le informazioni relative al digiuno sono state salvate.");
        alert.showAndWait();
    }

    @FXML
    private void onSalvaPreghiereClicked() {
        // Raccogliere lo stato delle preghiere
        boolean fajrDone = fajrCircle.getFill().equals(javafx.scene.paint.Color.GOLD);
        boolean dhuhrDone = dhuhrCircle.getFill().equals(javafx.scene.paint.Color.GOLD);
        boolean asrDone = asrCircle.getFill().equals(javafx.scene.paint.Color.GOLD);
        boolean maghribDone = maghribCircle.getFill().equals(javafx.scene.paint.Color.GOLD);
        boolean ishaDone = ishaCircle.getFill().equals(javafx.scene.paint.Color.GOLD);

        // Creare una bean per aggiornare il tracker
        GestioneTrackerBean trackerBean = new GestioneTrackerBean();
        trackerBean.setPreghiera("Fajr", fajrDone);
        trackerBean.setPreghiera("Dhuhr", dhuhrDone);
        trackerBean.setPreghiera("Asr", asrDone);
        trackerBean.setPreghiera("Maghrib", maghribDone);
        trackerBean.setPreghiera("Isha", ishaDone);

        // Passare al Controller Applicativo
        GestioneTrackerController controller = new GestioneTrackerController(session);
        controller.aggiornaPreghiere(trackerBean);

        // Mostrare messaggio di conferma
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Salvataggio");
        alert.setHeaderText(null);
        alert.setContentText("Le preghiere sono state salvate con successo!");
        alert.showAndWait();
    }

    public void aggiornaUIConTracker(GestioneTrackerBean trackerBean) {
        if (trackerBean == null) {
            System.out.println("Errore: Bean tracker è null. Impossibile aggiornare l'interfaccia.");
            return;
        }

        // Aggiorna lo stato delle preghiere
        fajrCircle.setFill(trackerBean.getPreghiera("Fajr") ? javafx.scene.paint.Color.GOLD : javafx.scene.paint.Color.LIGHTGRAY);
        dhuhrCircle.setFill(trackerBean.getPreghiera("Dhuhr") ? javafx.scene.paint.Color.GOLD : javafx.scene.paint.Color.LIGHTGRAY);
        asrCircle.setFill(trackerBean.getPreghiera("Asr") ? javafx.scene.paint.Color.GOLD : javafx.scene.paint.Color.LIGHTGRAY);
        maghribCircle.setFill(trackerBean.getPreghiera("Maghrib") ? javafx.scene.paint.Color.GOLD : javafx.scene.paint.Color.LIGHTGRAY);
        ishaCircle.setFill(trackerBean.getPreghiera("Isha") ? javafx.scene.paint.Color.GOLD : javafx.scene.paint.Color.LIGHTGRAY);

        // Aggiorna la barra di progresso e le etichette del Corano
        double progress = (double) trackerBean.getLetturaCorano() / 604;
        quranProgressBar.setProgress(Math.min(progress, 1.0));
        quranProgressBar.setStyle("-fx-accent: gold;");
        pagesReadLabel.setText("Pagine lette: " + trackerBean.getLetturaCorano());
        goalLabel.setText("Obiettivo giornaliero: " + trackerBean.getGoal() + " pagine");

        // Aggiorna i dati relativi al digiuno
        fastingSwitch.setSelected(trackerBean.isHaDigiunato());
        fastingNotes.setText(trackerBean.getNoteDigiuno() != null ? trackerBean.getNoteDigiuno() : "");

        sunnahCheckBox.setSelected(trackerBean.getMotivazioniDigiuno().contains("Sunnah"));
        makeUpCheckBox.setSelected(trackerBean.getMotivazioniDigiuno().contains("Recupero di un digiuno"));
        specificVoluntaryCheckBox.setSelected(trackerBean.getMotivazioniDigiuno().contains("Volontario Specifico"));
        generalVoluntaryCheckBox.setSelected(trackerBean.getMotivazioniDigiuno().contains("Volontario Generale"));

        // Log di debug
        System.out.println("Interfaccia aggiornata con i dati del Tracker.");
    }


}
