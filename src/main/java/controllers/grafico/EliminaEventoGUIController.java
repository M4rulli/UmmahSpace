package controllers.grafico;

import controllers.applicativo.GestioneEventoController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import misc.Session;

public class EliminaEventoGUIController {

    @FXML
    private Button confermaButton;

    @FXML
    private Button annullaButton;

    private final long idEvento;
    private final String idOrganizzatore;
    private final Session session;

    // Costruttore che riceve gli id dell'evento e dell'organizzatore, insieme alla sessione
    public EliminaEventoGUIController(long idEvento, String idOrganizzatore, Session session) {
        this.idEvento = idEvento;
        this.idOrganizzatore = idOrganizzatore;
        this.session = session;
    }

    @FXML
    public void initialize() {
        // Event listener per i pulsanti
        confermaButton.setOnAction(e -> onConfermaEliminazione());
        annullaButton.setOnAction(e -> onAnnullaEliminazione());
    }

    @FXML
    public void onConfermaEliminazione() {
        // Crea una istanza del controller applicativo per eliminare l'evento
        GestioneEventoController gestioneEventoController = new GestioneEventoController();

        try {
            // Chiamata al metodo per eliminare l'evento
            boolean successo = gestioneEventoController.eliminaEvento(idEvento, idOrganizzatore);

            // Mostra un messaggio in base al risultato
            if (successo) {
                mostraMessaggioConferma("Evento eliminato con successo!");
            } else {
                mostraMessaggioErrore("Errore durante l'eliminazione dell'evento.");
            }
        } catch (Exception e) {
            mostraMessaggioErrore("Errore: " + e.getMessage());
        }
    }

    @FXML
    private void onAnnullaEliminazione() {
        // Chiudi la finestra senza fare nulla
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
