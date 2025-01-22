package controllers.grafico;

import controllers.applicativo.RegistrazioneController;
import engclasses.beans.RegistrazioneBean;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.Model;
import misc.Session;
import org.controlsfx.control.ToggleSwitch;

public class RegistrazioneGUIController {

    @FXML
    private TextField nomeField;
    @FXML
    private TextField cognomeField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confermaPasswordField;
    @FXML
    private TextField emailField;
    @FXML
    private CheckBox organizzatoreCheckBox;
    @FXML
    private Button registratiButton;
    @FXML
    private ToggleSwitch persistenceSwitch;
    @FXML
    private Hyperlink loginLink;

    private final Session session;
    private boolean persistence = false;

    public RegistrazioneGUIController(Session session) {
        this.session = session;
    }

    @FXML
    private void initialize() {
        registratiButton.setOnAction(event -> onSignUpClicked());
        loginLink.setOnAction(event -> onHyperLinkLoginClicked());
        // Configura il ToggleSwitch
        persistenceSwitch.setOnMouseClicked(event -> togglePersistence());
        persistenceSwitch.setSelected(persistence);
    }

    @FXML
    public void onSignUpClicked() {
        boolean persistence = session.isPersistence();

        // Raccoglie i dati dalla UI
        String nome = nomeField.getText().trim();
        String cognome = cognomeField.getText().trim();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confermaPasswordField.getText();
        String email = emailField.getText();
        boolean isOrganizzatore = organizzatoreCheckBox.isSelected();

        // Crea il bean con i dati di input
        RegistrazioneBean registrazioneBean = new RegistrazioneBean();
        registrazioneBean.setNome(nome);
        registrazioneBean.setCognome(cognome);
        registrazioneBean.setUsername(username);
        registrazioneBean.setPassword(password);
        registrazioneBean.setConfirmPassword(confirmPassword);
        registrazioneBean.setEmail(email);
        registrazioneBean.setSeiOrganizzatore(isOrganizzatore);

        // Chiamata al Controller Applicativo per la registrazione
        RegistrazioneController registrazioneController = new RegistrazioneController(session);
        boolean success = registrazioneController.registraUtente(registrazioneBean, persistence);

        // Aggiorna la UI in base al risultato
        if (!success) {
            return; // Esci dal metodo
        }

        // Se la registrazione ha avuto successo, mostra il messaggio di conferma e cambia scena
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText("Registrazione completata con successo!");
        alert.showAndWait();

        // Cambia scena utilizzando la ViewFactory
        Stage stage = (Stage) registratiButton.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage); // Chiudi la finestra corrente
        Model.getInstance().getViewFactory().showMainView(session); // Mostra la MainView
    }

    @FXML
    private void onHyperLinkLoginClicked() {
        // Forza la persistenza a true
        session.setPersistence(true);
        // Apri la schermata di login tramite il Model
        Model.getInstance().getViewFactory().showLogin(session);

        // Chiudi la finestra attuale
        Stage stage = (Stage) loginLink.getScene().getWindow();
        stage.close();
    }

    // Metodo per mostrare la finestra di dialogo con gli errori
    public static void mostraMessaggioErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Errore");
        alert.setHeaderText("Risolvi i seguenti problemi:");
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    // Metodo per gestire il cambio di stato dello switch
    private void togglePersistence() {
        persistence = persistenceSwitch.isSelected(); // Cambia il valore della persistenza
        session.setPersistence(persistence); // Salva il nuovo valore della persistenza nella sessione
        if (persistence) {
            System.out.println("Il salvataggio ora avviene nel database.");
        } else {
            System.out.println("Il salvataggio ora avviene nel buffer.");
        }
    }
}
