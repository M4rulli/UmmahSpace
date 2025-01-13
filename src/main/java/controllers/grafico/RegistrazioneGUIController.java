package controllers.grafico;

import controllers.applicativo.RegistrazioneController;
import engclasses.beans.RegistrazioneBean;
import engclasses.dao.PartecipanteDAO;
import engclasses.dao.OrganizzatoreDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import misc.Session;
import misc.ValidationResult;
import model.Partecipante;
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
    private TextField emailField;
    @FXML
    private CheckBox organizzatoreCheckBox;
    @FXML
    private Label resultLabel;
    @FXML
    private ToggleSwitch persistenceSwitch;
    @FXML
    private Button infoButton;

    private final RegistrazioneController registrazioneController;
    private final Session session;

    public RegistrazioneGUIController(Session session, PartecipanteDAO partecipanteDAO, OrganizzatoreDAO organizzatoreDAO) {
        this.registrazioneController = new RegistrazioneController(partecipanteDAO, organizzatoreDAO);
        this.session = session;
    }

    @FXML
    public void onSignUpClicked() {
        // Raccoglie i dati dalla UI
        String nome = nomeField.getText().trim();
        String cognome = cognomeField.getText().trim();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        boolean isOrganizzatore = organizzatoreCheckBox.isSelected();

        // Crea il bean con i dati di input
        RegistrazioneBean registrazioneBean = new RegistrazioneBean();
        registrazioneBean.setNome(nome);
        registrazioneBean.setCognome(cognome);
        registrazioneBean.setUsername(username);
        registrazioneBean.setPassword(password);
        registrazioneBean.setEmail(email);
        registrazioneBean.setSeiOrganizzatore(isOrganizzatore);

        // Determina la modalità di persistenza dalla sessione
        boolean persistence = session.isPersistence();

        // Validazione dei dati
        ValidationResult validationResult = registrazioneController.validateRegistrationData(registrazioneBean, password);

        if (!validationResult.isValid()) {
            // Mostra messaggi di errore
            mostraMessaggioErrore(validationResult.getErrorMessages());
            return;
        }

        // Procede con la registrazione
        boolean success = registrazioneController.registraUtente(registrazioneBean, persistence);

        // Aggiorna la UI in base al risultato
        if (success) {
            mostraMessaggioConferma("Registrazione completata con successo!");
            passaAllaScenaPrincipale(username);
        } else {
            mostraMessaggioErrore("Errore durante la registrazione. Verifica i dati inseriti.");
        }
    }

    private void mostraMessaggioErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void mostraMessaggioConferma(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Conferma");
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void passaAllaScenaPrincipale(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            loader.setControllerFactory(param -> new MainViewGUIController(session, registrazioneController.getPartecipanteDAO(), username));
            Parent root = loader.load();

            Stage stage = (Stage) resultLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("UmmahSpace");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        persistenceSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            session.setPersistence(newValue);
            System.out.println("Persistenza: " + (newValue ? "Abilitata" : "Disabilitata"));
        });
    }

    @FXML
    public void onInfoButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazioni su Persistenza");
        alert.setHeaderText(null);
        alert.setContentText("Questo switch permette di scegliere dove salvare i dati:\n"
                + "- ON: I dati verranno salvati in modo permanente su database.\n"
                + "- OFF: I dati verranno salvati temporaneamente in memoria (buffer).");
        alert.showAndWait();
    }
}
