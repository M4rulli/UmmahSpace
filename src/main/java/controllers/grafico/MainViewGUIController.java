package controllers.grafico;

import engclasses.dao.GestioneTrackerDAO;
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
    private final PartecipanteDAO partecipanteDAO;
    private final String currentUsername;
    private final GestioneTrackerDAO trackerDAO;

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


    public MainViewGUIController(Session session, PartecipanteDAO partecipanteDAO, String username) {
        this.session = session;
        this.partecipanteDAO = partecipanteDAO;
        this.currentUsername = username;
        this.trackerDAO = new GestioneTrackerDAO();
    }

    @FXML
    private void initialize() {
        setWelcomeMessage(currentUsername);
        // Configurare l'handler manualmente
        profileButton.setOnAction(event -> onProfileButtonClicked());

        // Carica la sotto-vista del Calendario
        Model.getInstance().getViewFactory().loadCalendarioView(calendarioContainer, session);
        // Carica la sotto-vista del Tracker
        Model.getInstance().getViewFactory().loadTrackerView(trackerContainer, session, partecipanteDAO, currentUsername, trackerDAO);
        // Altri inizializzatori
        aggiornaData();
    }

    public void setWelcomeMessage(String username) {
        boolean persistence = session.isPersistence();
        Partecipante partecipante = partecipanteDAO.selezionaPartecipante(username, persistence);

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
            Model.getInstance().getViewFactory().showSettings(session, partecipanteDAO, currentUsername);
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