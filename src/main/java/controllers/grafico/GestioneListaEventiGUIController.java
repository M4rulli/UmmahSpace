package controllers.grafico;

import controllers.applicativo.GestioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.dao.GestioneEventoDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import misc.Model;
import misc.Session;

import java.io.IOException;
import java.util.List;

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
        List<EventoBean> eventi = gestioneEventoController.getEventiOrganizzatore(session.getIdUtente());

        // Popola la GUI con gli eventi
        for (EventoBean evento : eventi) {
            // Crea una sezione card-like per ogni evento
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; "
                    + "-fx-border-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");

            // Crea un HBox per visualizzare titolo, data, orario e bottoni
            HBox eventDetails = new HBox(20); // 20 è la distanza tra gli elementi
            eventDetails.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

            // Aggiungi il titolo
            Label titleLabel = new Label(evento.getTitolo());
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

            // Aggiungi la data
            Label dateLabel = new Label(evento.getData());
            dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

            // Aggiungi l'orario
            Label timeLabel = new Label(evento.getOrario());
            timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

            // Crea il bottone "Modifica"
            Button modificaButton = new Button("Modifica");
            modificaButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 10;");
            modificaButton.setOnAction(e -> onModificaEvento(evento));

            // Crea il bottone "Elimina"
            Button eliminaButton = new Button("Elimina");
            eliminaButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 5 10;");
            eliminaButton.setOnAction(e -> onEliminaEvento(evento));

            // Aggiungi tutti i componenti all'HBox
            eventDetails.getChildren().addAll(titleLabel, dateLabel, timeLabel, modificaButton, eliminaButton);

            // Aggiungi i dettagli dell'evento alla card
            card.getChildren().add(eventDetails);

            // Imposta il riferimento dell'evento alla card
            card.setUserData(evento);

            // Aggiungi la card al contenitore principale
            eventContainer.getChildren().add(card);
        }

        // Aggiunge uno stile globale al contenitore
        eventContainer.setStyle("-fx-spacing: 15; -fx-padding: 10;");
    }

    // Metodo per gestire l'eliminazione dell'evento
    @FXML
    private void onEliminaEvento(EventoBean evento) {
        // Mostra una finestra di conferma
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Sei sicuro di voler eliminare questo evento?");
        alert.setContentText("Titolo: " + evento.getTitolo());

        ButtonType buttonConferma = new ButtonType("Conferma");
        ButtonType buttonAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonConferma, buttonAnnulla);

        alert.showAndWait().ifPresent(button -> {
            if (button == buttonConferma) {
                // Chiamata al Controller Applicativo per eliminare l'evento
                GestioneEventoController gestioneEventoController = new GestioneEventoController(session);
                boolean success = gestioneEventoController.eliminaEvento(evento.getIdEvento(), session.getIdUtente());

                // Gestisce il risultato dell'operazione
                if (success) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Eliminazione completata");
                    successAlert.setHeaderText(null);

                    // Rimuovi l'evento dalla GUI
                    eventContainer.getChildren().removeIf(node -> node.getUserData() == evento);
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Errore");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Si è verificato un errore durante l'eliminazione dell'evento.");
                    errorAlert.showAndWait();
                }
            }
        });
    }

    // Metodo per gestire la modifica dell'evento
    private void onModificaEvento(EventoBean evento) {
    }
}