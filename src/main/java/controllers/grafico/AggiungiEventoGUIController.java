package controllers.grafico;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import misc.Session;
import engclasses.beans.EventoBean;
import controllers.applicativo.GestioneEventoController;

public class AggiungiEventoGUIController {

    @FXML
    private TextField titoloField;

    @FXML
    private TextArea descrizioneField;

    @FXML
    private TextField orarioField;

    @FXML
    private TextField limitePartecipantiField;

    @FXML
    private Button salvaButton;

    @FXML
    private Button annullaButton;

    private final Session session;

    public AggiungiEventoGUIController(Session session) {
        this.session = session;
    }

    @FXML
    public void initialize() {
        salvaButton.setOnAction(e -> saveNewEvent());
        annullaButton.setOnAction(e -> closeWindow());
    }

    private void saveNewEvent() {
        // Ottieni i dati dai campi
        String titolo = titoloField.getText().trim();
        String descrizione = descrizioneField.getText().trim();
        String orario = orarioField.getText().trim();
        String limitePartecipantiText = limitePartecipantiField.getText().trim();
        // Crea la bean con i dati di input
        EventoBean evento = new EventoBean();
        evento.setTitolo(titolo);
        evento.setDescrizione(descrizione);
        evento.setOrario(orario);
        evento.setLimitePartecipanti(Integer.parseInt(limitePartecipantiText));


        // Chiamata al Controller applicativo
        GestioneEventoController gestioneEventoController = new GestioneEventoController(session);
        boolean sucess = gestioneEventoController.aggiungiEvento(evento, session.getIdUtente());

        if (sucess) {
            mostraMessaggioConferma("Evento aggiunto con successo!");

            // Logga i nuovi dati alla console
            System.out.println("Profilo aggiornato: ");
            System.out.println("Titolo: "+ evento.getTitolo());
            System.out.println("Descrizione: " + evento.getDescrizione());
            System.out.println("Orario: " + evento.getOrario());
            System.out.println("LimitePartecipanti: " + evento.getLimitePartecipanti());
        } else {
            mostraMessaggioErrore("Errore durante l'aggiunta dell'evento.");
        }
        closeWindow();

    }

    private void closeWindow() {
        // Ottieni la finestra corrente e chiudila
        Stage stage = (Stage) annullaButton.getScene().getWindow();
        stage.close();
    }

    private void mostraMessaggioConferma(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Conferma");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void mostraMessaggioErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}
