package controllers.grafico;

import controllers.applicativo.GestioneEventoController;
import engclasses.beans.EventoBean;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import misc.Model;
import misc.Session;

public class ModificaEventoGUIController {

    private final Session session;

    @FXML
    private TextField titoloField;
    @FXML
    private TextArea descrizioneArea;
    @FXML
    private TextField dataField;
    @FXML
    private TextField orarioField;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;

    private String originalTitolo;
    private String originalDescrizione;
    private String originalData;
    private String originalOrario;

    private final GestioneEventoController gestioneEventoController;

    public ModificaEventoGUIController(Session session) {
        this.session = session;
        this.gestioneEventoController = new GestioneEventoController(session);
    }

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> onBackButtonClicked());
        saveButton.setOnAction(event -> onSaveButtonClicked());
        editButton.setOnAction(event -> onEditButtonClicked());

        // Recupera i dati dell'evento
        EventoBean evento = gestioneEventoController.inizializzaEvento(session.getIdEvento());
        initializeEvent(
                evento.getTitolo(),
                evento.getDescrizione(),
                evento.getData(),
                evento.getOrario()
        );
    }

    public void initializeEvent(String titolo, String descrizione, String data, String orario) {
        this.originalTitolo = titolo;
        this.originalDescrizione = descrizione;
        this.originalData = data;
        this.originalOrario = orario;

        titoloField.setText(titolo);
        descrizioneArea.setText(descrizione);
        dataField.setText(data);
        orarioField.setText(orario);

        disableEditing();
    }

    @FXML
    private void onEditButtonClicked() {
        enableEditing();
    }

    @FXML
    private void onSaveButtonClicked() {
        // Crea un bean con i dati aggiornati
        EventoBean updatedEvento = new EventoBean();
        updatedEvento.setTitolo(titoloField.getText());
        updatedEvento.setDescrizione(descrizioneArea.getText());
        updatedEvento.setData(dataField.getText());
        updatedEvento.setOrario(orarioField.getText());

        boolean success = gestioneEventoController.aggiornaEvento(updatedEvento, session.getIdEvento());

        if (success) {
            initializeEvent(
                    updatedEvento.getTitolo(),
                    updatedEvento.getDescrizione(),
                    updatedEvento.getData(),
                    updatedEvento.getOrario()
            );

            System.out.println("Evento aggiornato: ");
            System.out.println("Titolo: " + updatedEvento.getTitolo());
            System.out.println("Descrizione: " + updatedEvento.getDescrizione());
            System.out.println("Data: " + updatedEvento.getData());
            System.out.println("Orario: " + updatedEvento.getOrario());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Conferma");
            alert.setHeaderText(null);
            alert.setContentText("Evento modificato con successo!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Si è verificato un errore durante la modifica dell'evento.");
            alert.showAndWait();
        }
        closeWindow();
    }

    @FXML
    private void onBackButtonClicked() {
        if (saveButton.isDisable()) {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(currentStage);
            Model.getInstance().getViewFactory().showMainView(session);
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Modifiche non salvate");
            alert.setHeaderText("Le modifiche sono ancora abilitate.");
            alert.setContentText("Vuoi davvero tornare indietro senza salvare le modifiche?");

            ButtonType buttonYes = new ButtonType("Sì", ButtonBar.ButtonData.YES);
            ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(buttonYes, buttonNo);

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
        titoloField.setEditable(false);
        descrizioneArea.setEditable(false);
        dataField.setEditable(false);
        orarioField.setEditable(false);

        saveButton.setDisable(true);
        backButton.setDisable(false);
        editButton.setDisable(false);
    }

    private void enableEditing() {
        titoloField.setEditable(true);
        descrizioneArea.setEditable(true);
        dataField.setEditable(true);
        orarioField.setEditable(true);

        saveButton.setDisable(false);
        backButton.setDisable(false);
        editButton.setDisable(true);
    }
    private void closeWindow() {
        // Ottieni la finestra corrente e chiudila
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
