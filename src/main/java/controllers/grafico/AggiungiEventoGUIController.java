package controllers.grafico;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import misc.Model;
import misc.Session;
import engclasses.beans.EventoBean;
import controllers.applicativo.GestioneEventoController;

import static misc.MessageUtils.mostraMessaggioConferma;

public class AggiungiEventoGUIController {

    @FXML
    private TextField titoloField;

    @FXML
    private TextArea descrizioneField;

    @FXML
    private TextField orarioField1;

    @FXML
    private TextField orarioField2;

    @FXML
    private TextField limitePartecipantiField;

    @FXML
    private Button salvaButton;

    @FXML
    private Button annullaButton;

    private final Session session;
    private final String selectedDate;

    public AggiungiEventoGUIController(Session session, String selectedDate) {
        this.session = session;
        this.selectedDate = selectedDate;
    }

    @FXML
    public void initialize() {
        salvaButton.setOnAction(e -> saveNewEvent());
        annullaButton.setOnAction(e -> {onAnnullaButtonClicked();});
    }

    private void saveNewEvent() {
        // Ottieni i dati dai campi
        String titolo = titoloField.getText().trim();
        String descrizione = descrizioneField.getText().trim();
        String orarioInizio = orarioField1.getText().trim();
        String orarioFine = orarioField2.getText().trim();
        String limitePartecipantiText = limitePartecipantiField.getText().trim();

        // Formatta l'orario
        String orario = orarioInizio + " - " + orarioFine;
        // Crea la bean con i dati di input
        EventoBean evento = new EventoBean();
        evento.setTitolo(titolo);
        evento.setDescrizione(descrizione);
        evento.setOrario(orario);
        evento.setLimitePartecipanti(limitePartecipantiText);
        evento.setData(selectedDate);

        // Chiamata al Controller applicativo
        GestioneEventoController gestioneEventoController = new GestioneEventoController(session);
        boolean sucess = gestioneEventoController.aggiungiEvento(evento);

        if (sucess) {
            mostraMessaggioConferma("Conferma", "Evento aggiunto con successo!");
            Stage currentStage = (Stage) annullaButton.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(currentStage);
        }
    }

    @FXML
    private void onAnnullaButtonClicked() {
        Stage currentStage = (Stage) annullaButton.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(currentStage);
    }
}
