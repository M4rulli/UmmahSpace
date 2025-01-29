package controllers.grafico;

import controllers.applicativo.GestisciProfiloController;
import engclasses.beans.RegistrazioneBean;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.UtenteNonTrovatoException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import engclasses.pattern.Model;
import misc.Session;

import static misc.MessageUtils.mostraMessaggioConferma;
import static misc.MessageUtils.mostraMessaggioConfermaConScelta;

public class GestisciProfiloGUIController {

    private final Session session;

    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;

    private final GestisciProfiloController gestisciProfiloController;

    public GestisciProfiloGUIController(Session session) {
        this.session = session;
        this.gestisciProfiloController = new GestisciProfiloController(session);
    }

    @FXML
    public void initialize() throws UtenteNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        backButton.setOnAction(event -> onBackButtonClicked());
        saveButton.setOnAction(event -> {
            try {
                onSaveButtonClicked();
            } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException e) {
                throw new RuntimeException(e);
            }
        });
        editButton.setOnAction(event -> onEditButtonClicked());

        // Recupera i dati dell'utente
        RegistrazioneBean bean = gestisciProfiloController.inizializzaProfilo(session.getIdUtente());
        initializeProfile(
                bean.getNome(),
                bean.getCognome(),
                bean.getUsername(),
                bean.getEmail());
    }

    public void initializeProfile(String name, String surname, String username, String email) {
        nameField.setText(name);
        surnameField.setText(surname);
        usernameField.setText(username);
        emailField.setText(email);
        disableEditing();
    }

    @FXML
    private void onEditButtonClicked() {
        enableEditing();
    }

    @FXML
    private void onSaveButtonClicked() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Crea un bean con i dati aggiornati
        RegistrazioneBean updatedBean = new RegistrazioneBean();
        updatedBean.setNome(nameField.getText());
        updatedBean.setCognome(surnameField.getText());
        updatedBean.setUsername(usernameField.getText());
        updatedBean.setEmail(emailField.getText());

        GestisciProfiloController profileController = new GestisciProfiloController(session);

        // Verifica se i campi password sono stati modificati
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        boolean success = profileController.aggiornaProfilo(updatedBean, currentPassword, newPassword, confirmPassword);

        if (success) {
            initializeProfile(
                    updatedBean.getNome(),
                    updatedBean.getCognome(),
                    updatedBean.getUsername(),
                    updatedBean.getEmail()
            );
            mostraMessaggioConferma("Conferma", "Modifiche salvate con successo!");
        }
    }

    @FXML
    private void onBackButtonClicked() {
        // Controlla se le modifiche sono abilitate
        if (saveButton.isDisable()) { // Nessuna modifica abilitata

            Stage currentStage = (Stage) backButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(currentStage); // Chiudi la finestra corrente
            Model.getInstance().getViewFactory().showMainView(session);

        } else { // Modifiche abilitate, mostra un avviso
            boolean conferma = mostraMessaggioConfermaConScelta("Modifiche non salvate","Le modifiche sono ancora abilitate. Vuoi davvero tornare indietro senza salvare le modifiche?");
            if (conferma) {
                Stage stage = (Stage) backButton.getScene().getWindow();
                Model.getInstance().getViewFactory().closeStage(stage);
                Model.getInstance().getViewFactory().showMainView(session);
            }
        }
    }

    private void disableEditing() {
        nameField.setEditable(false);
        surnameField.setEditable(false);
        usernameField.setEditable(false);
        emailField.setEditable(false);

        saveButton.setDisable(true);
        backButton.setDisable(false);
        editButton.setDisable(false);
    }

    private void enableEditing() {
        // Permette la modifica solo se l'utente NON è un organizzatore
        if (!session.isOrganizzatore()) {
            nameField.setEditable(true);
            surnameField.setEditable(true);
        }
        usernameField.setEditable(true);
        emailField.setEditable(true);

        saveButton.setDisable(false);
        backButton.setDisable(false);
        editButton.setDisable(true);
    }

}
