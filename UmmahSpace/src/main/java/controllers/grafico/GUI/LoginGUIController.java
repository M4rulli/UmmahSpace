package controllers.grafico.GUI;

import controllers.applicativo.LoginController;
import engclasses.beans.GestioneTrackerBean;
import engclasses.beans.LoginBean;
import engclasses.exceptions.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.Session;
import engclasses.pattern.Model;

import static misc.MessageUtils.mostraMessaggioConferma;
import static misc.MessageUtils.mostraMessaggioErrore;

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
        registrationLink.setOnAction(event -> {
            try {
                onHyperLinkRegistrationClicked();
            } catch (ViewFactoryException e) {
                throw new RuntimeException(e);
            }
        });

        // Assegna l'azione al bottone di login
        loginButton.setOnAction(event -> {
            try {
                onLoginClicked();
            } catch (LoginFallitoException | DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException |
                     TrackerNonTrovatoException | ViewFactoryException e) {
                mostraMessaggioErrore("Errore di Login", e.getMessage());
                throw new RuntimeException(e);
            }
        });

        // Listener per la checkbox
        listenOrganizzatoreCheckBox();
    }

    @FXML
    private void onHyperLinkRegistrationClicked() throws ViewFactoryException {
        // Forza lo stato della persistenza su false
        session.setPersistence(false);

        // Cambia scena a Registrazione tramite il Model
        Model.getInstance().getViewFactory().showRegistration(session);

        // Chiude la scena attuale
        Stage stage = (Stage) registrationLink.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onLoginClicked() throws LoginFallitoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, TrackerNonTrovatoException, ViewFactoryException {
        // Preleva i campi username e password dalla GUI
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Crea una bean con i campi prelevati
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(username);
        loginBean.setPassword(password);

        // Invia i dati al controller applicativo
        LoginController loginController = new LoginController(session);
        GestioneTrackerBean trackerBean = loginController.effettuaLogin(loginBean, session.isPersistence());

        if (trackerBean != null && trackerBean != ORGANIZZATORE_PLACEHOLDER) {
            // Caso: login come partecipante
            session.setCurrentUsername(loginBean.getUsername());
            session.setTracker(trackerBean);

            // Mostra messaggio di successo e passa alla MainView
            mostraMessaggioConferma("Successo", "Login effettuato con successo!");
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showMainView(session);

        } else if (trackerBean == ORGANIZZATORE_PLACEHOLDER) {
            // Caso: login come organizzatore
            session.setCurrentUsername(loginBean.getUsername());

            // Mostra messaggio di successo e passa alla MainView
            mostraMessaggioConferma("Successo", "Login effettuato con successo!");
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showMainView(session);
        }
    }

    private void listenOrganizzatoreCheckBox() {
        organizzatoreCheckbox.selectedProperty().addListener((observable, oldValue, newValue) ->
                session.setIsOrganizzatore(newValue));
    }

}