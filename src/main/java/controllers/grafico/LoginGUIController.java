package controllers.grafico;

import controllers.applicativo.LoginController;
import engclasses.beans.GestioneTrackerBean;
import engclasses.beans.LoginBean;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.Session;
import engclasses.pattern.Model;

import static misc.MessageUtils.mostraMessaggioConferma;

public class LoginGUIController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registrationLink;

    @FXML
    private CheckBox organizzatoreCheckbox;

    private final Session session;
    private static final GestioneTrackerBean ORGANIZZATORE_PLACEHOLDER = new GestioneTrackerBean();


    public LoginGUIController(Session session) {
        this.session = session;
    }

    @FXML
    private void initialize() {
        // Assegna l'azione al link di registrazione
        registrationLink.setOnAction(event -> onHyperLinkRegistrationClicked());

        // Assegna l'azione al bottone di login
        loginButton.setOnAction(event -> onLoginClicked());

        // Listener per la checkbox
        listenOrganizzatoreCheckBox();
    }

    @FXML
    private void onHyperLinkRegistrationClicked() {
        // Forza lo stato della persistenza su false
        session.setPersistence(false);

        // Cambia scena a Registrazione tramite il Model
        Model.getInstance().getViewFactory().showRegistration(session);

        // Chiude la scena attuale
        Stage stage = (Stage) registrationLink.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onLoginClicked() {
        // Preleva i campi username e password dalla GUI
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Crea una bean con i campi prelevati
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(username);
        loginBean.setPassword(password);

        // Invia i dati al controller applicativo
        LoginController loginController = new LoginController(session);
        GestioneTrackerBean trackerBean = loginController.effettuaLogin(loginBean, session.isPersistence());

        if (trackerBean != null && trackerBean != ORGANIZZATORE_PLACEHOLDER) {
            // Caso: login come partecipante
            session.setCurrentUsername(username);
            session.setTracker(trackerBean); // Salva il tracker nella sessione;

            // Mostra messaggio di successo e passa alla MainView
            mostraMessaggioConferma("Successo", "Login effettuato con successo!");
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage); // Chiudi la finestra corrente
            Model.getInstance().getViewFactory().showMainView(session); // Mostra la MainView

        } else if (trackerBean == ORGANIZZATORE_PLACEHOLDER) {
            // Caso: login come organizzatore
            session.setCurrentUsername(username);

            // Mostra messaggio di successo e passa alla MainView
            mostraMessaggioConferma("Successo", "Login effettuato con successo!");
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage); // Chiudi la finestra corrente
            Model.getInstance().getViewFactory().showMainView(session); // Mostra la MainView
        }
    }

    private void listenOrganizzatoreCheckBox() {
        organizzatoreCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            session.setIsOrganizzatore(newValue); // Aggiorna lo stato nella sessione
        });
    }

}