package controllers.grafico;

import controllers.applicativo.IscrizioneEventoController;
import engclasses.beans.EventoBean;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import misc.Session;
import java.util.List;
import java.util.Optional;

import static misc.MessageUtils.*;

public class EventiGiornalieriGUIController {

    @FXML
    private VBox eventContainer;

    private final Session session;

    public EventiGiornalieriGUIController(Session session) {
        this.session = session;
    }

    @FXML
    public void initialize() {
        List<EventoBean> eventi = session.getEventiDelGiorno();

        // Popola la GUI con gli eventi
        for (EventoBean evento : eventi) {
            VBox card = creaCardEvento(evento);
            eventContainer.getChildren().add(card);
        }
    }

    // Metodo per creare una card per un evento
    private VBox creaCardEvento(EventoBean evento) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: transparent; -fx-border-color: #dddddd; "
                + "-fx-border-radius: 10; -fx-padding: 15; ");

        // Associa il bean all'oggetto grafico
        card.setUserData(evento);

        Label titleLabel = new Label(evento.getTitolo());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333; ");

        Label descrizioneLabel = new Label("Descrizione:"+ evento.getDescrizione());
        descrizioneLabel.setStyle("-fx-font-size: 14px; fx-text-fill: #666; ");


        Label timeLabel = new Label("Orario: " + evento.getOrario());
        timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; ");

        Label organizerLabel = new Label("Organizzatore: " + evento.getNomeOrganizzatore() + " " + evento.getCognomeOrganizzatore());
        organizerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");

        Label participantsLabel = new Label("Iscritti: " + evento.getIscritti() + "/" + evento.getLimitePartecipanti());
        participantsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");

        Label linkLabel = new Label("Link: " + evento.getLink());
        linkLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");

        Label statusLabel = new Label(evento.isChiuso() ? "Chiuso" : "Aperto");
        statusLabel.setStyle(evento.isChiuso()
                ? "-fx-text-fill: red; -fx-font-weight: bold;"
                : "-fx-text-fill: green; -fx-font-weight: bold;");

        Button registerButton = new Button("Registrati all'evento");
        registerButton.setDisable(evento.isChiuso());
        registerButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5 10;");
        registerButton.setOnAction(e -> onRegistratiButton(evento));

        card.getChildren().addAll(titleLabel, timeLabel, organizerLabel, participantsLabel, statusLabel, registerButton);
        card.setSpacing(10);

        return card;
    }

    // Metodo per il pulsante di registrazione
    private void onRegistratiButton(EventoBean evento) {
        IscrizioneEventoController iscrizioneEventoController = new IscrizioneEventoController(session);
        // Recupera l'ID dell'evento dal bean
        long idEvento = evento.getIdEvento();

        // Mostra una finestra di conferma
        boolean conferma = mostraMessaggioConfermaConScelta("Conferma Iscrizione", "Vuoi davvero iscriverti a questo evento?");

        if (conferma) {
            // Chiamata al Controller Applicativo se l'utente conferma
            try {
                boolean successo = iscrizioneEventoController.iscriviPartecipante(idEvento);

                if (successo) {
                    mostraMessaggioConferma("Conferma", "Iscrizione completata con successo!");

                    // Chiudi la scena corrente
                    Stage stage = (Stage) eventContainer.getScene().getWindow();
                    stage.close();
                }
            } catch (Exception e) {
                mostraMessaggioErrore("Errore", "Errore: " + e.getMessage());
            }
        }
    }
}