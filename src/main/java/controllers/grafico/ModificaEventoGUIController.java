package controllers.grafico;

import controllers.applicativo.GestioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.EventoNonTrovatoException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import engclasses.pattern.Model;
import misc.Session;
import javafx.scene.control.Button;

import static misc.MessageUtils.*;

public class ModificaEventoGUIController {

    private final Session session;

    @FXML
    private TextField titoloField;
    @FXML
    private TextArea descrizioneArea;
    @FXML
    private TextField dataField;
    @FXML
    private TextField orarioInizioField;
    @FXML
    private TextField orarioFineField;
    @FXML
    private TextField linkField;
    @FXML
    private TextField limitePartecipantiField;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;
    @FXML
    private Button chiudiEventoButton;

    private final GestioneEventoController gestioneEventoController;

    public ModificaEventoGUIController(Session session) {
        this.session = session;
        this.gestioneEventoController = new GestioneEventoController(session);
    }

    @FXML
    public void initialize() throws EventoNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        backButton.setOnAction(event -> onBackButtonClicked());
        saveButton.setOnAction(event -> {
            try {
                onSaveButtonClicked();
            } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException e) {
                throw new RuntimeException(e);
            }
        });
        editButton.setOnAction(event -> onEditButtonClicked());
        chiudiEventoButton.setOnAction(event -> onChiudiEventoClicked());

        // Recupera i dati dell'evento
        EventoBean evento = gestioneEventoController.inizializzaEvento();
        initializeEvent(
                evento.getTitolo(),
                evento.getDescrizione(),
                evento.getData(),
                evento.getOrario(),
                evento.getLink(),
                evento.getLimitePartecipanti()
        );
    }

    public void initializeEvent(String titolo, String descrizione, String data, String orario, String link, String limitePartecipanti) {

        String[] orariDivisi = orario.split(" - ");
        String orarioInizio = orariDivisi[0];
        String orarioFine = orariDivisi[1];

        titoloField.setText(titolo);
        descrizioneArea.setText(descrizione);
        dataField.setText(data);
        orarioInizioField.setText(orarioInizio);
        orarioFineField.setText(orarioFine);
        linkField.setText(link);
        limitePartecipantiField.setText(limitePartecipanti);

        disableEditing();
    }

    @FXML
    private void onEditButtonClicked() {
        enableEditing();
    }

    @FXML
    private void onSaveButtonClicked() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Crea un bean con i dati aggiornati
        EventoBean updatedEvento = new EventoBean();
        updatedEvento.setTitolo(titoloField.getText());
        updatedEvento.setDescrizione(descrizioneArea.getText());
        updatedEvento.setData(dataField.getText());
        String orarioInizio = orarioInizioField.getText().trim();
        String orarioFine = orarioFineField.getText().trim();
        String orarioCombinato = orarioInizio + " - " + orarioFine;
        updatedEvento.setOrario(orarioCombinato);
        updatedEvento.setLink(linkField.getText().trim());
        updatedEvento.setLimitePartecipanti(limitePartecipantiField.getText().trim());

        boolean success = gestioneEventoController.aggiornaEvento(updatedEvento, session.getIdEvento());

        if (success) {
            initializeEvent(
                    updatedEvento.getTitolo(),
                    updatedEvento.getDescrizione(),
                    updatedEvento.getData(),
                    updatedEvento.getOrario(),
                    updatedEvento.getLink(),
                    updatedEvento.getLimitePartecipanti()
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Conferma");
            alert.setHeaderText(null);
            alert.setContentText("Evento modificato con successo!");
            alert.showAndWait();
        }
    }

    @FXML
    private void onBackButtonClicked() {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        if (saveButton.isDisable()) {
            Model.getInstance().getViewFactory().closeStage(currentStage);
            Model.getInstance().getViewFactory().showMainView(session);
        } else {
            boolean conferma = mostraMessaggioConfermaConScelta("Modifiche non salvate","Le modifiche sono ancora abilitate. Vuoi davvero tornare indietro senza salvare le modifiche?");
                if (conferma) {
                    Stage stage = (Stage) backButton.getScene().getWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                    Model.getInstance().getViewFactory().showMainView(session);
                }
        }
    }

    private void disableEditing() {
        titoloField.setEditable(false);
        descrizioneArea.setEditable(false);
        dataField.setEditable(false);
        orarioFineField.setEditable(false);
        orarioInizioField.setEditable(false);
        linkField.setEditable(false);
        limitePartecipantiField.setEditable(false);

        saveButton.setDisable(true);
        backButton.setDisable(false);
        editButton.setDisable(false);
    }

    private void enableEditing() {
        titoloField.setEditable(true);
        descrizioneArea.setEditable(true);
        dataField.setEditable(true);
        orarioFineField.setEditable(true);
        orarioInizioField.setEditable(true);
        linkField.setEditable(true);
        limitePartecipantiField.setEditable(true);

        saveButton.setDisable(false);
        backButton.setDisable(false);
        editButton.setDisable(true);
    }

    public void onChiudiEventoClicked() {
        boolean conferma = mostraMessaggioConfermaConScelta(
                "Conferma Chiusura",
                "Vuoi davvero chiudere questo evento? L'operazione è irreversibile."
        );
        if (conferma) {
            try {
                // Usa il metodo applicativo per chiudere l'evento
                gestioneEventoController.chiudiEvento();

                mostraMessaggioConferma("Evento Chiuso", "L'evento è stato chiuso con successo.");

                // Chiudi la finestra
                Stage stage = (Stage) titoloField.getScene().getWindow();
                Model.getInstance().getViewFactory().showMainView(session);
                stage.close();
            } catch (Exception e) {
                mostraMessaggioErrore("Errore","Errore durante la chiusura dell'evento: " + e.getMessage());
            }
        }
    }
}
