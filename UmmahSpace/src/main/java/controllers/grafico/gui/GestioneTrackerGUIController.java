package controllers.grafico.gui;

import controllers.applicativo.GestioneTrackerController;
import engclasses.beans.GestioneTrackerBean;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.TrackerNonTrovatoException;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import misc.DateUtil;
import misc.Session;
import org.controlsfx.control.ToggleSwitch;

import java.util.*;

import static misc.MessageUtils.mostraMessaggioErrore;

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
    private Circle fajrCircle;
    @FXML
    private Circle dhuhrCircle;
    @FXML
    private Circle asrCircle;
    @FXML
    private Circle maghribCircle;
    @FXML
    private Circle ishaCircle;
    @FXML
    private Button fajrButton;
    @FXML
    private Button dhuhrButton;
    @FXML
    private Button asrButton;
    @FXML
    private Button maghribButton;
    @FXML
    private Button ishaButton;
    @FXML
    private Label fajrCheck;
    @FXML
    private Label dhuhrCheck;
    @FXML
    private Label asrCheck;
    @FXML
    private Label maghribCheck;
    @FXML
    private Label ishaCheck;
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

    private static final String ERRORE = "Errore";

    public GestioneTrackerGUIController(Session session) {
        this.session = session;
    }

    @FXML
    private void initialize() {
        setGoalButton.setOnAction(event -> onImpostaObiettivoClicked() );
        addReadingButton.setOnAction(event -> onAggiungiLetturaClicked());
        salvaDigiunoButton.setOnAction(actionEvent -> {
            try {
                onSalvaDigiunoClicked();
            } catch (DatabaseConnessioneFallitaException | TrackerNonTrovatoException |
                     DatabaseOperazioneFallitaException e) {
                throw new RuntimeException(e);
            }
        });
        salvaPreghiereButton.setOnAction(event -> onSalvaPreghiereClicked());
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
        // Rinfresca la vista del Tracker
        GestioneTrackerBean trackerBean = session.getTracker();
        aggiornaUIConTracker(Objects.requireNonNullElseGet(trackerBean, GestioneTrackerBean::new));
    }

    @FXML
    private void onAggiungiLetturaClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Aggiungi Lettura");
        dialog.setHeaderText("Aggiungi il numero di pagine lette oggi");
        dialog.setContentText("Numero di pagine:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(pagesStr -> {
            try {
                int pages = Integer.parseInt(pagesStr);

                if (pages <= 0) {
                    mostraMessaggioErrore(ERRORE,"Il numero di pagine deve essere maggiore di zero.");
                    return;
                }

                // Crea una Bean per trasportare i dati
                GestioneTrackerBean trackerBean = new GestioneTrackerBean();
                trackerBean.setLetturaCorano(pages);

                // Passa la Bean al Controller Applicativo e ottiene una bean aggiornata
                GestioneTrackerController trackerController = new GestioneTrackerController(session);
                GestioneTrackerBean updatedBean = trackerController.aggiungiLettura(trackerBean);

                // Salva la bean aggiornata nella sessione
                session.setTracker(updatedBean);

                // Aggiorna la UI
                aggiornaBarra();

            } catch (NumberFormatException | TrackerNonTrovatoException | DatabaseConnessioneFallitaException |
                     DatabaseOperazioneFallitaException e) {
                mostraMessaggioErrore(ERRORE, "Inserisci un numero valido.");
            }
        });
    }


    @FXML
    private void onImpostaObiettivoClicked() {
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
                    mostraMessaggioErrore(ERRORE, "L'obiettivo deve essere maggiore di zero.");
                    return;
                }
                if (goal > 604) {
                    mostraMessaggioErrore(ERRORE,"L'obiettivo non può essere più grande di 604 pagine");
                    return;
                }

                // Crea la Bean temporanea per trasmettere i dati
                GestioneTrackerBean trackerBean = new GestioneTrackerBean();
                trackerBean.setGoal(goal);

                // Passa la Bean al Controller Applicativo e ottiene una bean aggiornata
                GestioneTrackerController trackerController = new GestioneTrackerController(session);
                GestioneTrackerBean updatedBean = trackerController.setObiettivoGiornaliero(trackerBean);

                // Salva la bean aggiornata nella sessione
                session.setTracker(updatedBean);

                // Aggiorna la UI
                aggiornaBarra();
            }
                catch (NumberFormatException | TrackerNonTrovatoException | DatabaseConnessioneFallitaException |
                       DatabaseOperazioneFallitaException e) {
                mostraMessaggioErrore(ERRORE, "Inserisci un numero valido.");
            }
        });
    }

    private void aggiornaBarra() throws DatabaseConnessioneFallitaException, TrackerNonTrovatoException, DatabaseOperazioneFallitaException {

        // Crea un'istanza del controller applicativo
        GestioneTrackerController gestioneTrackerController = new GestioneTrackerController(session);

        // Ottieni i dati del tracker tramite il controller applicativo e crea la Bean temporanea per trasmetterli
        GestioneTrackerBean updatedBean = gestioneTrackerController.aggiornaBarraConProgresso();

        // Aggiorna la barra di progresso
        quranProgressBar.setProgress(updatedBean.getProgresso());
        quranProgressBar.setStyle("-fx-accent: gold;"); // Cambia colore in oro

        // Aggiorna le etichette
        goalLabel.setText("Obiettivo giornaliero: " + updatedBean.getGoal() + " pagine");
        pagesReadLabel.setText("Pagine lette: " + updatedBean.getLetturaCorano());

        // Salva lo stato corrente del tracker nella sessione
        session.setTracker(updatedBean);
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
    private void onSalvaDigiunoClicked() throws DatabaseConnessioneFallitaException, TrackerNonTrovatoException, DatabaseOperazioneFallitaException {
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

        // Passa i dati al Controller Applicativo per aggiornare il database e la sessione
        GestioneTrackerController controller = new GestioneTrackerController(session);
        GestioneTrackerBean updatedBean = controller.aggiornaDigiuno(bean);

        // Salva la bean aggiornata nella sessione
        session.setTracker(updatedBean);
    }

    @FXML
    private void onSalvaPreghiereClicked() {

        // Raccogliere lo stato delle preghiere dall'UI
        boolean fajrDone = fajrCircle.getFill().equals(javafx.scene.paint.Color.GOLD);
        boolean dhuhrDone = dhuhrCircle.getFill().equals(javafx.scene.paint.Color.GOLD);
        boolean asrDone = asrCircle.getFill().equals(javafx.scene.paint.Color.GOLD);
        boolean maghribDone = maghribCircle.getFill().equals(javafx.scene.paint.Color.GOLD);
        boolean ishaDone = ishaCircle.getFill().equals(javafx.scene.paint.Color.GOLD);

        // Creare una bean per trasportare i dati
        GestioneTrackerBean trackerBean = new GestioneTrackerBean();

        // Lista delle preghiere con i rispettivi valori booleani
        List<String> preghiere = List.of("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha");
        List<Boolean> statiPreghiere = List.of(fajrDone, dhuhrDone, asrDone, maghribDone, ishaDone);

        for (int i = 0; i < preghiere.size(); i++) {
            trackerBean.setPreghiera(preghiere.get(i), statiPreghiere.get(i));
        }

        // Passare al Controller Applicativo e ottenere una bean aggiornata
        GestioneTrackerController controller = new GestioneTrackerController(session);
        try {
            GestioneTrackerBean updatedBean = controller.aggiornaPreghiere(trackerBean);

            // Salva la bean aggiornata nella sessione
            session.setTracker(updatedBean);

        } catch (IllegalArgumentException | TrackerNonTrovatoException | DatabaseConnessioneFallitaException |
                 DatabaseOperazioneFallitaException e) {
            mostraMessaggioErrore(ERRORE , null);
        }
    }

    public void aggiornaUIConTracker(GestioneTrackerBean trackerBean) {

        // Aggiorna lo stato delle preghiere
        fajrCircle.setFill(trackerBean.getPreghiera("Fajr") ? javafx.scene.paint.Color.GOLD : javafx.scene.paint.Color.LIGHTGRAY);
        dhuhrCircle.setFill(trackerBean.getPreghiera("Dhuhr") ? javafx.scene.paint.Color.GOLD : javafx.scene.paint.Color.LIGHTGRAY);
        asrCircle.setFill(trackerBean.getPreghiera("Asr") ? javafx.scene.paint.Color.GOLD : javafx.scene.paint.Color.LIGHTGRAY);
        maghribCircle.setFill(trackerBean.getPreghiera("Maghrib") ? javafx.scene.paint.Color.GOLD : javafx.scene.paint.Color.LIGHTGRAY);
        ishaCircle.setFill(trackerBean.getPreghiera("Isha") ? javafx.scene.paint.Color.GOLD : javafx.scene.paint.Color.LIGHTGRAY);

        // Aggiorna la barra di progresso e le etichette del Corano
        double progress = trackerBean.getProgresso();
        quranProgressBar.setProgress(Math.min(progress, 1.0)); // Aggiorna la barra di progresso
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
    }

}
