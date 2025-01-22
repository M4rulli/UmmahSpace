package controllers.grafico;

import controllers.applicativo.IscrizioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.beans.PartecipanteBean;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import misc.Session;

import java.time.LocalDate;

public class DettagliEventoGUIController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label organizerLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label participantsLabel;

    private final EventoBean evento;
    private final Session session;

    public DettagliEventoGUIController(EventoBean evento, Session session) {
        this.evento = evento;
        this.session = session;
    }

    @FXML
    public void initialize() {
        // Popola i campi con i dati dell'evento
        titleLabel.setText(evento.getTitolo());
        descriptionLabel.setText(evento.getDescrizione());
        dateLabel.setText(evento.getData());
        timeLabel.setText(evento.getOrario());
        organizerLabel.setText(evento.getNomeOrganizzatore() + " " + evento.getCognomeOrganizzatore());

        // Stato dell'evento
        if (evento.isChiuso()) {
            statusLabel.setText("Chiuso");
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            registerButton.setDisable(true);
            registerButton.setText("Evento Chiuso");
        } else {
            statusLabel.setText("Aperto");
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }

        // Partecipanti
        participantsLabel.setText("Iscritti: " + evento.getIscritti() + "/" + evento.getLimitePartecipanti());

        // Aggiunge listener ai pulsanti
        registerButton.setOnAction(e -> onRegistratiButton());
        backButton.setOnAction(e -> onBackButton());
    }

    @FXML
    public void onRegistratiButton() {

        IscrizioneEventoController iscrizioneEventoController = new IscrizioneEventoController();

        // Recupera i dati dalla Sessione e dall'evento corrente
        String idUtente = session.getIdUtente();
        long idEvento = evento.getIdEvento();;
        String dataIscrizione = LocalDate.now().toString();

        // Crea la Bean
        PartecipanteBean partecipanteBean = new PartecipanteBean();
        partecipanteBean.setIdUtente(idUtente);
        partecipanteBean.setEmail(session.getCurrentUsername());
        partecipanteBean.setIdEvento(idEvento);
        partecipanteBean.setDataIscrizione(dataIscrizione);

        // Chiamata al Controller Applicativo
        try {
            boolean successo = iscrizioneEventoController.iscriviPartecipante(partecipanteBean);

            if (successo) {
                mostraMessaggioConferma();
            } else {
                mostraMessaggioErrore("Errore durante l'iscrizione all'evento.");
            }
        } catch (Exception e) {
            mostraMessaggioErrore("Errore: " + e.getMessage());
        }
    }

    private void onBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void mostraMessaggioConferma() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Conferma");
        alert.setHeaderText(null);
        alert.setContentText("Iscrizione completata con successo!");
        alert.showAndWait();
    }

    private void mostraMessaggioErrore(String messaggio) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}