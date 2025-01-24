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

    private Session session;

    public void setSession(Session session) {
        this.session = session;
    }

    @FXML
    public void initialize() {
        salvaButton.setOnAction(e -> saveNewEvent());
        annullaButton.setOnAction(e -> closeWindow());
    }

    private void saveNewEvent() {
        // Ottieni i dati dai campi
        String titolo = titoloField.getText();
        String descrizione = descrizioneField.getText();
        String orario = orarioField.getText();
        String limitePartecipantiText = limitePartecipantiField.getText();

        // Verifica che i dati siano validi
        if (titolo.isEmpty() || descrizione.isEmpty() || orario.isEmpty() || limitePartecipantiText.isEmpty()) {
            mostraMessaggioErrore("Tutti i campi devono essere compilati.");
            return;
        }

        // Prova a convertire il numero di partecipanti in un intero
        int limitePartecipanti = 0;
        try {
            limitePartecipanti = Integer.parseInt(limitePartecipantiText);
            if (limitePartecipanti <= 0) {
                mostraMessaggioErrore("Il numero di partecipanti deve essere maggiore di zero.");
                return;
            }
        } catch (NumberFormatException e) {
            mostraMessaggioErrore("Il numero di partecipanti non è valido.");
            return;
        }

        // Crea un oggetto EventoBean e imposta i valori
        EventoBean nuovoEvento = new EventoBean();
        nuovoEvento.setTitolo(titolo);
        nuovoEvento.setDescrizione(descrizione);
        nuovoEvento.setOrario(orario);
        nuovoEvento.setLimitePartecipanti(limitePartecipanti);

        // Istanzia il controller applicativo
        GestioneEventoController gestioneEventoController = new GestioneEventoController();

        // Passa la Bean al metodo per aggiungere l'evento
        try {
            boolean successo = gestioneEventoController.aggiungiEvento(nuovoEvento, session);

            if (successo) {
                mostraMessaggioConferma("Evento aggiunto con successo!");
                closeWindow();
            } else {
                mostraMessaggioErrore("Errore durante l'aggiunta dell'evento.");
            }
        } catch (Exception e) {
            mostraMessaggioErrore("Errore: " + e.getMessage());
        }
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
