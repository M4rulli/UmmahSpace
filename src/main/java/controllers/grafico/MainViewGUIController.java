package controllers.grafico;

import engclasses.beans.GestioneTrackerBean;
import engclasses.dao.PartecipanteDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import misc.Model;
import misc.Session;
import model.Partecipante;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainViewGUIController {

    private final Session session;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button profileButton;
    @FXML
    private Label dateLabel;
    @FXML
    private StackPane calendarioContainer; // Contenitore per il calendario
    @FXML
    private StackPane trackerContainer; // Contenitore per il tracker

    public MainViewGUIController(Session session) {
        this.session = session;
    }

    @FXML
    private void initialize() {

        setWelcomeMessage();
        // Configura l'handler
        profileButton.setOnAction(event -> onProfileButtonClicked());
        // Carica la sotto-vista del Calendario
        Model.getInstance().getViewFactory().loadCalendarioView(calendarioContainer, session);
        // Carica la sotto-vista del Tracker
        Model.getInstance().getViewFactory().loadTrackerView(trackerContainer, session);
        // Altri inizializzatori
        aggiornaData();

        // Da rivedere
        GestioneTrackerGUIController gestioneTrackerGUIController = session.getGestioneTrackerGUIController();
        GestioneTrackerBean trackerBean = session.getTracker();
        gestioneTrackerGUIController.aggiornaUIConTracker(trackerBean);
    }

    // Da rivedere
    public void setWelcomeMessage() {
        boolean persistence = session.isPersistence();
        Partecipante partecipante = PartecipanteDAO.selezionaPartecipante("idUtente",session.getIdUtente(), persistence);

        if (partecipante != null) {
            welcomeLabel.setText("Benvenuto, " + partecipante.getNome() + "!");
        } else {
            System.out.println("Errore: Partecipante non trovato.");
        }
    }

    @FXML
    public void onProfileButtonClicked() {
        try {
            // Recupera lo Stage corrente (finestra principale)
            Stage currentStage = (Stage) profileButton.getScene().getWindow();

            // Chiudi la finestra principale
            Model.getInstance().getViewFactory().closeStage(currentStage);

            // Apri la finestra delle impostazioni
            Model.getInstance().getViewFactory().showSettings(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void aggiornaData() {
        // Ottieni la data corrente
        LocalDate dataCorrente = LocalDate.now();

        // Formatta la data (es. "16 Novembre 2024")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ITALIAN);
        String dataFormattata = dataCorrente.format(formatter);

        // Aggiorna la Label con la data formattata
        dateLabel.setText(dataFormattata);
    }

}