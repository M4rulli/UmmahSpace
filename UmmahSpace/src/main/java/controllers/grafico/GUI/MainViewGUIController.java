package controllers.grafico.GUI;

import controllers.applicativo.OrarioPreghiereController;
import engclasses.exceptions.GeolocalizzazioneFallitaException;
import engclasses.exceptions.HttpRequestException;
import engclasses.exceptions.ViewFactoryException;
import engclasses.pattern.AlAdhanAdapter;
import engclasses.pattern.GeolocalizzazioneIPAdapter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import engclasses.pattern.Model;
import misc.Session;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static misc.MessageUtils.mostraMessaggioConfermaConScelta;

public class MainViewGUIController {

    private final Random random = new Random();
    private final Session session;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label subMessageLabel;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label dateLabel;
    @FXML
    private StackPane calendarioContainer; // Contenitore per il calendario
    @FXML
    private StackPane trackerContainer; // Contenitore per il tracker
    @FXML
    private StackPane eventiContainer; // Contenitore per il tracker
    @FXML
    private Tab trackerTab;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab eventiTab;
    @FXML
    private Label preghieraPassataLabel;
    @FXML
    private Label preghieraFuturaLabel;
    @FXML
    private VBox box1;
    @FXML
    private VBox box2;
    @FXML
    private VBox box3;


    public MainViewGUIController(Session session) {
        this.session = session;
    }

    @FXML
    private void initialize() throws GeolocalizzazioneFallitaException, HttpRequestException, ViewFactoryException {
        HBox.setHgrow(box1, Priority.ALWAYS);
        HBox.setHgrow(box2, Priority.ALWAYS);
        HBox.setHgrow(box3, Priority.ALWAYS);
        // Imposta le preghiere
        inizializzaOrariPreghiere();
        // Imposta il messaggio di benvenuto
        setWelcomeMessage();
        // Configura l'handler per il bottone del profilo e del logout
        profileButton.setOnAction(event -> {
            try {
                onProfileButtonClicked();
            } catch (ViewFactoryException e) {
                throw new RuntimeException(e);
            }
        });
        logoutButton.setOnAction(event -> {
            try {
                onLogoutButtonClicked();
            } catch (ViewFactoryException e) {
                throw new RuntimeException(e);
            }
        });
        // Carica la sotto-vista del Calendario
        Model.getInstance().getViewFactory().loadCalendarioView(calendarioContainer, session);
        // Carica la sotto-vista del Tracker
        Model.getInstance().getViewFactory().loadTrackerView(trackerContainer, session);
        // Inizializza la data corrente
        aggiornaData();
        // Rimuovi il tab "Tracker Spirituale" per gli organizzatori
        if (session.isOrganizzatore()) {
            tabPane.getTabs().remove(trackerTab);
        }
        // Aggiungi il listener per la tab "I Miei Eventi"
        if (eventiTab != null) {
            eventiTab.setOnSelectionChanged(event -> {
                if (eventiTab.isSelected()) {
                    try {
                        Model.getInstance().getViewFactory().loadListaEventiView(eventiContainer, session);
                    } catch (ViewFactoryException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    public void setWelcomeMessage() {
        // Imposta il messaggio principale
        welcomeLabel.setText("Benvenuto, " + session.getNome() + "!");

        // Array dei messaggi motivazionali
        String[] messages = {
                "As-Salamu Alaikum, ricorda: il tempo ben speso è una benedizione.",
                "Ogni evento pianificato è un'opportunità per fare del bene!",
                "Che Allah benedica la tua giornata e i tuoi sforzi.",
                "Pianifica oggi per creare un domani migliore.",
                "Allah ama chi usa saggiamente il proprio tempo. Organizzati al meglio!",
                "Ogni piccolo passo è un passo verso il successo.",
                "Clicca sulle celle del calendario per scoprire nuove opportunità.",
                "Il tempo è prezioso. Pianifica con saggezza e lascia il resto ad Allah."
        };

        // Genera un messaggio casuale
        String randomMessage = messages[random.nextInt(messages.length)];
        subMessageLabel.setText(randomMessage);

        // Combina il messaggio casuale con quello principale
        subMessageLabel.setText(randomMessage);
    }


    @FXML
    public void onProfileButtonClicked() throws ViewFactoryException {
        Stage currentStage = (Stage) profileButton.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(currentStage);
        Model.getInstance().getViewFactory().showSettings(session);
    }

    // Metodo per gestire il click sul pulsante
    private void onLogoutButtonClicked() throws ViewFactoryException {
        boolean conferma = mostraMessaggioConfermaConScelta("Conferma Logout", "Sei sicuro di voler effettuare il logout?");
        if (conferma) {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Session newSession = new Session(true); // Istanzia una nuova sessione
            Model.getInstance().getViewFactory().showLogin(newSession);
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

    @FXML
    public void inizializzaOrariPreghiere() throws GeolocalizzazioneFallitaException, HttpRequestException {
        // Instanzia il controller applicativo
        OrarioPreghiereController orarioPreghiereController = new OrarioPreghiereController(new GeolocalizzazioneIPAdapter(), new AlAdhanAdapter());

        // Ottieni preghiera passata e futura
        Map.Entry<String, LocalTime> preghieraPassata = orarioPreghiereController.getPreghieraPassata();
        Map.Entry<String, LocalTime> preghieraFutura = orarioPreghiereController.getPreghieraFutura();

        // Aggiorna i label
        aggiornaPreghiere(preghieraPassata, preghieraFutura);
    }

    private void aggiornaPreghiere(Map.Entry<String, LocalTime> preghieraPassata, Map.Entry<String, LocalTime> preghieraFutura) {
        // Aggiorna la preghiera passata
        if (preghieraPassata != null) {
            preghieraPassataLabel.setText(preghieraPassata.getKey() + " - " + preghieraPassata.getValue().toString());
        } else {
            preghieraPassataLabel.setText("Nessuna preghiera precedente");
        }

        // Aggiorna la preghiera futura
        if (preghieraFutura != null) {
            preghieraFuturaLabel.setText(preghieraFutura.getKey() + " - " + preghieraFutura.getValue().toString());
        } else {
            preghieraFuturaLabel.setText("Nessuna preghiera successiva");
        }
    }

}