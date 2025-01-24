package controllers.grafico;

import controllers.applicativo.GestioneEventoController;
import engclasses.beans.EventoBean;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import misc.Session;

public class ModificaEventoGUIController {

    @FXML
    private TextField titoloField;

    @FXML
    private TextField orarioField;

    @FXML
    private TextArea descrizioneField;

    @FXML
    private TextField limitePartecipantiField;

    @FXML
    private TextField iscrittiField;

    @FXML
    private TextField linkField;

    @FXML
    private Button salvaButton;

    @FXML
    private Button annullaButton;

    private final EventoBean evento;
    private final Session session;

    public ModificaEventoGUIController(EventoBean evento, Session session) {
        this.evento = evento;
        this.session = session;
    }

    @FXML
    public void initialize() {
        // Precompila i campi con i dati dell'evento
        titoloField.setText(evento.getTitolo());
        orarioField.setText(evento.getOrario());
        descrizioneField.setText(evento.getDescrizione());
        limitePartecipantiField.setText(String.valueOf(evento.getLimitePartecipanti()));
        iscrittiField.setText(String.valueOf(evento.getIscritti()));

        // Configura i pulsanti
        salvaButton.setOnAction(e -> salvaModifiche());
        annullaButton.setOnAction(e -> chiudiFinestra());
    }

    private void salvaModifiche() {
        // Crea un'istanza della Bean per l'evento
        EventoBean eventoModificato = new EventoBean();
        try {
            // Popola la Bean con i dati dai campi
            eventoModificato.setIdEvento(evento.getIdEvento()); // Mantieni l'ID esistente
            eventoModificato.setTitolo(titoloField.getText());
            eventoModificato.setOrario(orarioField.getText());
            eventoModificato.setDescrizione(descrizioneField.getText());

            // Controlla che il limite di partecipanti sia maggiore di zero
            int limitePartecipanti = Integer.parseInt(limitePartecipantiField.getText());
            if (limitePartecipanti <= 0) {
                mostraMessaggioErrore("Il limite di partecipanti deve essere maggiore di zero.");
                return;
            }
            eventoModificato.setLimitePartecipanti(limitePartecipanti);

            // Controlla che il numero di iscritti non superi il limite e non sia negativo
            int iscritti = Integer.parseInt(iscrittiField.getText());
            if (iscritti < 0) {
                mostraMessaggioErrore("Il numero di partecipanti non può essere negativo.");
                return;
            }
            if (iscritti > limitePartecipanti) {
                mostraMessaggioErrore("Il numero di partecipanti non può superare il limite di partecipanti.");
                return;
            }
            eventoModificato.setIscritti(iscritti);


            // Istanzia il controller applicativo
            GestioneEventoController gestioneEventoController = new GestioneEventoController();

            // Passa la Bean al metodo per modificare l'evento
            boolean successo = gestioneEventoController.modificaEvento(eventoModificato);

            if (successo) {
                mostraMessaggioConferma("Modifica effettuata con successo!");
                chiudiFinestra();
            } else {
                mostraMessaggioErrore("Errore durante la modifica dell'evento.");
            }
        } catch (NumberFormatException e) {
            mostraMessaggioErrore("Verifica che i campi numerici siano corretti.");
        } catch (Exception e) {
            mostraMessaggioErrore("Errore: " + e.getMessage());
        }
    }

    private void chiudiFinestra() {
        // Ottieni la finestra corrente e chiudila
        Stage stage = (Stage) salvaButton.getScene().getWindow();
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
