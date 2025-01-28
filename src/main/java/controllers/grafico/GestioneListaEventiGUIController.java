package controllers.grafico;

import controllers.applicativo.GestioneEventoController;
import engclasses.beans.EventoBean;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import misc.Model;
import misc.Session;

import java.util.List;

import static misc.MessageUtils.*;

public class GestioneListaEventiGUIController {

    @FXML
    private VBox eventContainer;

    private final Session session;

    public GestioneListaEventiGUIController(Session session) {
        this.session = session;
    }

    @FXML
    public void initialize() {
        // Crea un'istanza del controller applicativo
        GestioneEventoController gestioneEventoController = new GestioneEventoController(session);

        // Recupera tutti gli eventi associati all'organizzatore corrente
        List<EventoBean> eventi = gestioneEventoController.getEventiOrganizzatore(session.getIdUtente(), session);

        // Verifica se ci sono eventi
        if (eventi == null || eventi.isEmpty()) {
            // Mostra un messaggio placeholder
            Label placeholder = new Label("Non hai aggiunto ancora nessun evento.");
            placeholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #888; -fx-padding: 20; -fx-alignment: center;");
            eventContainer.getChildren().add(placeholder);
            return;
        }

        // Popola la GUI con gli eventi
        for (EventoBean evento : eventi) {
            // Crea una sezione card-like per ogni evento
            VBox card = new VBox(10);
            card.setStyle("-fx-spacing: 15; -fx-padding: 10; -fx-background-color: rgba(255, 255, 255, 0.9); "
                    + "-fx-border-radius: 15; -fx-background-radius: 15; "
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.05, 0, 2);");

            // Crea un HBox per visualizzare titolo, data, orario e bottoni
            HBox eventDetails = new HBox(20); // 20 è la distanza tra gli elementi
            eventDetails.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

            // Aggiungi il titolo
            Label titleLabel = new Label(evento.getTitolo());
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

            // Aggiungi la data
            Label dateLabel = new Label("Data: " + (evento.getData() != null ? evento.getData() : "N/A"));
            dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

            // Aggiungi l'orario
            Label timeLabel = new Label("Orario: " + (evento.getOrario() != null ? evento.getOrario() : "N/A"));
            timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

            // Stato dell'evento (Aperto o Chiuso)
            Label statusLabel = new Label(evento.isChiuso() ? "Chiuso" : "Aperto");
            statusLabel.setStyle(evento.isChiuso()
                    ? "-fx-text-fill: red; -fx-font-weight: bold;"
                    : "-fx-text-fill: green; -fx-font-weight: bold;");

            // Contenitore separato per i bottoni
            HBox buttonContainer = new HBox(10); // 10 è la distanza tra i bottoni
            buttonContainer.setStyle("-fx-alignment: center-right;");

            // Usa uno spazio flessibile per separare i bottoni
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS); // Questo forza lo spazio tra i dettagli e i bottoni

            // Crea il bottone "Gestisci"
            Button modificaButton = new Button("Gestisci");
            // Disabilita il bottone se l'evento è chiuso
            modificaButton.setDisable(!evento.getStato());
            modificaButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 10; ");
            modificaButton.setOnAction(e -> onGestisciEventoClicked(evento.getIdEvento()));

            // Crea il bottone "Elimina"
            Button eliminaButton = new Button("Elimina");
            eliminaButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 5 10;");
            eliminaButton.setOnAction(e -> onEliminaEvento(evento));

            // Crea il pulsante report
            Button reportButton = new Button("Report");
            reportButton.setStyle("-fx-background-color: #e0e0e0; " + // Grigio chiaro
                            "-fx-text-fill: #333; " + // Colore testo scuro
                            "-fx-padding: 5 10; "  // Spaziatura interna
            );

            // Azione del pulsante
            reportButton.setOnAction(e -> onReportButtonClicked(evento.getIdEvento()));

            // Aggiungi tutti i componenti all'HBox
            eventDetails.getChildren().addAll(titleLabel, dateLabel, timeLabel, statusLabel, spacer, modificaButton, eliminaButton, reportButton);

            // Aggiungi i dettagli dell'evento alla card
            card.getChildren().add(eventDetails);

            // Imposta il riferimento dell'evento alla card
            card.setUserData(evento);

            // Aggiungi la card al contenitore principale
            eventContainer.getChildren().add(card);
        }

        // Aggiunge uno stile globale al contenitore principale
        eventContainer.setStyle("-fx-spacing: 15; -fx-padding: 10; ");
    }

    // Metodo per gestire l'eliminazione dell'evento
    @FXML
    private void onEliminaEvento(EventoBean evento) {
        // Mostra una finestra di conferma con pulsanti classici
        boolean risposta = mostraMessaggioConfermaConScelta("Conferma Eliminazione", "Sei sicuro di voler eliminare l'evento " + evento.getTitolo() + "?");
        if (risposta) {
            GestioneEventoController gestioneEventoController = new GestioneEventoController(session);
            boolean success = gestioneEventoController.eliminaEvento(evento.getIdEvento(), session.getIdUtente());
            if (success) {
                // Mostra un messaggio di successo
                mostraMessaggioConferma("Eliminazione Completata", "L'evento è stato eliminato con successo.");
                // Rimuovi l'evento dalla GUI
                eventContainer.getChildren().removeIf(node -> node.getUserData() == evento);
            } else {
                mostraMessaggioErrore("Errore","Si è verificato un errore durante l'eliminazione dell'evento." );
            }
        }
    }

    private void onGestisciEventoClicked(long idEvento) {
        // Imposta l'ID evento nella sessione
        session.setIdEvento(idEvento);
        Stage currentStage = (Stage) eventContainer.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(currentStage);
        Model.getInstance().getViewFactory().showModificaEvento(session);
    }

    private void onReportButtonClicked(long idEvento) {
        session.setIdEvento(idEvento);
        Model.getInstance().getViewFactory().showReportView(session);
    }

}