package controllers.grafico;

import controllers.applicativo.GestisciProfiloPartecipanteController;
import engclasses.beans.RegistrazioneBean;
import engclasses.dao.PartecipanteDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.Model;
import misc.Session;
import model.Partecipante;

public class GestisciProfiloPartecipanteGUIController {

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

    private String currentUsername;
    private String originalName;
    private String originalSurname;
    private String originalUsername;
    private String originalEmail;

    private final GestisciProfiloPartecipanteController gestisciProfiloPartecipanteController;

    public GestisciProfiloPartecipanteGUIController(Session session) {
        this.session = session;
        this.gestisciProfiloPartecipanteController = new GestisciProfiloPartecipanteController(session);
    }

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> {onBackButtonClicked();});
        saveButton.setOnAction(event -> {onSaveButtonClicked();});
        editButton.setOnAction(event -> {onEditButtonClicked();});

        boolean persistence = session.isPersistence();
        // Recupera i dati dell'utente
        RegistrazioneBean bean = gestisciProfiloPartecipanteController.inizializzaProfilo(session.getIdUtente());
        initializeProfile(
                bean.getNome(),
                bean.getCognome(),
                bean.getUsername(),
                bean.getEmail());
    }

    public void initializeProfile(String name, String surname, String username, String email) {
        this.originalName = name;
        this.originalSurname = surname;
        this.originalUsername = username;
        this.originalEmail = email;

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
    private void onSaveButtonClicked() {
        // Crea un bean con i dati aggiornati
        RegistrazioneBean updatedBean = new RegistrazioneBean();
        updatedBean.setNome(nameField.getText());
        updatedBean.setCognome(surnameField.getText());
        updatedBean.setUsername(usernameField.getText());
        updatedBean.setEmail(emailField.getText());

        GestisciProfiloPartecipanteController profileController = new GestisciProfiloPartecipanteController(session);

        // Verifica se i campi password sono stati modificati
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String username = usernameField.getText();

        boolean success = profileController.aggiornaProfiloPartecipante(updatedBean, currentPassword, newPassword, confirmPassword);

        if (success) {
            currentUsername = updatedBean.getUsername(); // Aggiorna il currentUsername
            initializeProfile(
                    updatedBean.getNome(),
                    updatedBean.getCognome(),
                    updatedBean.getUsername(),
                    updatedBean.getEmail()
            );

            // Logga i nuovi dati alla console
            System.out.println("Profilo aggiornato: ");
            System.out.println("Nome: " + updatedBean.getNome());
            System.out.println("Cognome: " + updatedBean.getCognome());
            System.out.println("Username: " + updatedBean.getUsername());
            System.out.println("Email: " + updatedBean.getEmail());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Conferma");
            alert.setHeaderText(null);
            alert.setContentText("Modifiche salvate con successo!");
            alert.showAndWait();
        } else {
            System.out.println("Errore durante l'aggiornamento dei dati.");
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Modifiche non salvate");
            alert.setHeaderText("Le modifiche sono ancora abilitate.");
            alert.setContentText("Vuoi davvero tornare indietro senza salvare le modifiche?");

            ButtonType buttonYes = new ButtonType("Sì", ButtonBar.ButtonData.YES);
            ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(buttonYes, buttonNo);

            // Gestisci la risposta dell'utente
            alert.showAndWait().ifPresent(response -> {
                if (response == buttonYes) {
                    Stage stage = (Stage) backButton.getScene().getWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                    Model.getInstance().getViewFactory().showMainView(session);
                }
            });
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
        nameField.setEditable(true);
        surnameField.setEditable(true);
        usernameField.setEditable(true);
        emailField.setEditable(true);

        saveButton.setDisable(false);
        backButton.setDisable(false);
        editButton.setDisable(true);
    }

}
