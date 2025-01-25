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
    private final EventoBean eventoDaModificare;

    @FXML
    private TextField titoloField;
    @FXML
    private TextArea descrizioneArea;
    @FXML
    private TextField dataField;
    @FXML
    private TextField orarioField;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;
    @FXML
    private Button editButton;

    private String originalTitolo;
    private String originalDescrizione;
    private String originalData;
    private String originalOrario;

    private final GestioneEventoController gestioneEventoController;

    public ModificaEventoGUIController(Session session, EventoBean eventoDaModificare) {
        this.session = session;
        this.eventoDaModificare = eventoDaModificare;
        this.gestioneEventoController = new GestioneEventoController(session);
    }

    @FXML
    public void initialize() {
        backButton.setOnAction(event -> { onBackButtonClicked(); });
        saveButton.setOnAction(event -> { onSaveButtonClicked(); });
        editButton.setOnAction(event -> { onEditButtonClicked(); });

        // Carica i dati dell'evento da modificare
        initializeEvent(eventoDaModificare.getTitolo(), eventoDaModificare.getDescrizione(),
                eventoDaModificare.getData(), eventoDaModificare.getOrario());
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

        // Chiama il controller applicativo per aggiornare l'evento
        boolean success = gestioneEventoController.modificaEvento(updatedEvento, session.getIdUtente());

        if (success) {
            // Aggiorna i dati dell'evento nella GUI
            initializeEvent(updatedEvento.getTitolo(), updatedEvento.getDescrizione(),
                    updatedEvento.getData(), updatedEvento.getOrario());

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
    }

    @FXML
    private void onBackButtonClicked() {
        // Mostra la finestra principale senza salvare
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(currentStage);
        Model.getInstance().getViewFactory().showMainView(session);
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
}
