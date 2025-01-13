package controllers.grafico;

import controllers.applicativo.GestisciProfiloPartecipanteController;
import engclasses.beans.RegistrazioneBean;
import engclasses.dao.PartecipanteDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import misc.Session;
import model.Partecipante;

import java.io.IOException;

public class GestisciProfiloPartecipanteGUIController {

    private String currentUsername;
    private final Session session;
    private final PartecipanteDAO partecipanteDAO;
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
    private Button cancelButton;

    private final GestisciProfiloPartecipanteController profileController;
    private String originalName;
    private String originalSurname;
    private String originalUsername;
    private String originalEmail;
    private Partecipante partecipante; // Campo per il partecipante corrente


    public GestisciProfiloPartecipanteGUIController(Session session, PartecipanteDAO partecipanteDAO, String currentUsername) {
        this.session = session;
        this.partecipanteDAO = partecipanteDAO;
        this.profileController = new GestisciProfiloPartecipanteController(partecipanteDAO, session);
        this.currentUsername = currentUsername;
        this.partecipante = partecipanteDAO.selezionaPartecipante(currentUsername, session.isPersistence());
    }


    @FXML
    public void initialize() {
        boolean persistence = session.isPersistence();
        Partecipante partecipante = partecipanteDAO.selezionaPartecipante(currentUsername, persistence);

        if (partecipante != null) {
            initializeProfile(
                    partecipante.getNome(),
                    partecipante.getCognome(),
                    partecipante.getUsername(),
                    partecipante.getEmail()
            );
        } else {
            System.out.println("Errore: Partecipante non trovato.");
        }
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
        RegistrazioneBean updatedBean = new RegistrazioneBean();
        updatedBean.setNome(nameField.getText());
        updatedBean.setCognome(surnameField.getText());
        updatedBean.setUsername(usernameField.getText());
        updatedBean.setEmail(emailField.getText());

        GestisciProfiloPartecipanteController profileController = new GestisciProfiloPartecipanteController(partecipanteDAO, session);

        // Controlla se l'username è già in uso
        if (!updatedBean.getUsername().equals(partecipante.getUsername())) {
            boolean usernameExists = partecipanteDAO.selezionaPartecipante(updatedBean.getUsername(), session.isPersistence()) != null;
            if (usernameExists) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Username già in uso");
                alert.setContentText("L'username scelto è già utilizzato. Scegli un altro username.");
                alert.showAndWait();
                return; // Interrompi il salvataggio
            }
        }

        boolean success = profileController.updateProfileData(updatedBean, currentUsername);

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
            tornaAllaScenaPrincipale();
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
                    tornaAllaScenaPrincipale();
                }
            });
        }
    }


    private void tornaAllaScenaPrincipale() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));

            // Configura manualmente il controller
            loader.setControllerFactory(param -> new MainViewGUIController(session, partecipanteDAO, currentUsername));

            Parent root = loader.load();

            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("UmmahSpace");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disableEditing() {
        nameField.setEditable(false);
        surnameField.setEditable(false);
        usernameField.setEditable(false);
        emailField.setEditable(false);

        saveButton.setDisable(true);
        cancelButton.setDisable(false);
        editButton.setDisable(false);
    }

    private void enableEditing() {
        nameField.setEditable(true);
        surnameField.setEditable(true);
        usernameField.setEditable(true);
        emailField.setEditable(true);

        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        editButton.setDisable(true);
    }


    private boolean hasChanges() {
        return !nameField.getText().equals(originalName) ||
                !surnameField.getText().equals(originalSurname) ||
                !usernameField.getText().equals(originalUsername) ||
                !emailField.getText().equals(originalEmail);
    }

}
