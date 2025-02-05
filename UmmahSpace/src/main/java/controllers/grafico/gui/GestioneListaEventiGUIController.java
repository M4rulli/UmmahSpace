package controllers.grafico.gui;

import controllers.applicativo.GestioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.exceptions.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import engclasses.pattern.Model;
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
    public void initialize() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {
        // Crea un'istanza del controller applicativo
        GestioneEventoController gestioneEventoController = new GestioneEventoController(session);

        // Recupera tutti gli eventi associati all'organizzatore corrente
        List<EventoBean> eventi = gestioneEventoController.getEventiOrganizzatore(session.getIdUtente(), session);

        // Verifica se ci sono eventi
        if (eventi == null || eventi.isEmpty()) {
            mostraPlaceholder();
            return;
        }

        // Popola la GUI con gli eventi
        for (EventoBean evento : eventi) {
            eventContainer.getChildren().add(creaEventoCard(evento));
        }

        // Aggiunge uno stile globale al contenitore principale
        eventContainer.setStyle("-fx-spacing: 15; -fx-padding: 10;");
    }

    /**
     * Metodo che crea una card per un evento.
     */
    private VBox creaEventoCard(EventoBean evento) {
        VBox card = new VBox(10);
        card.setStyle("-fx-spacing: 15; -fx-padding: 10; -fx-background-color: rgba(255, 255, 255, 0.9); "
                + "-fx-border-radius: 15; -fx-background-radius: 15; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.05, 0, 2);");

        HBox eventDetails = new HBox(20);
        eventDetails.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

        Label titleLabel = creaLabel(evento.getTitolo(), "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        Label dateLabel = creaLabel("Data: " + (evento.getData() != null ? evento.getData() : "N/A"), "-fx-font-size: 14px; -fx-text-fill: #666;");
        Label timeLabel = creaLabel("Orario: " + (evento.getOrario() != null ? evento.getOrario() : "N/A"), "-fx-font-size: 14px; -fx-text-fill: #666;");


        Label statusLabel = new Label();
        if (!evento.isStato()) { // Se l'evento è stato chiuso manualmente (stato = false)
            statusLabel.setText("Chiuso");
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else if (evento.isPieno()) { // Se l'evento è ancora attivo (stato = true) ma è pieno
            statusLabel.setText("Pieno");
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else { // Se l'evento è ancora attivo (stato = true) e non è pieno, allora è aperto
            statusLabel.setText("Aperto");
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button modificaButton = creaBottone("Gestisci", "#4CAF50", evento.isStato(), e -> {
            try {
                onGestisciEventoClicked(evento.getIdEvento());
            } catch (ViewFactoryException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button eliminaButton = creaBottone("Elimina", "#f44336", true, e -> {
            try {
                onEliminaEvento(evento);
            } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException |
                     EventoNonTrovatoException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button reportButton = creaBottone("Report", "#8e8e8e", true, e -> {
            try {
                onReportButtonClicked(evento.getIdEvento());
            } catch (ViewFactoryException | PartecipazioniNonTrovateException ex) { mostraMessaggioErrore("Errore", "Impossibile generare il report, nessuna partecipazione esistente per l'evento selezionato.");
                throw new RuntimeException(ex);
            }
        });

        eventDetails.getChildren().addAll(titleLabel, dateLabel, timeLabel, statusLabel, spacer, modificaButton, eliminaButton, reportButton);
        card.getChildren().add(eventDetails);
        card.setUserData(evento);

        return card;
    }

    /**
     * Metodo di utilità per creare un Label con stile personalizzato.
     */
    private Label creaLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    /**
     * Metodo di utilità per creare un Button con stile e azione personalizzata.
     */
    private Button creaBottone(String text, String color, boolean enabled, EventHandler<ActionEvent> action) {
        Button button = new Button(text);
        button.setDisable(!enabled);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 5 10;");
        button.setOnAction(action);
        return button;
    }

    /**
     * Mostra un messaggio placeholder quando non ci sono eventi.
     */
    private void mostraPlaceholder() {
        Label placeholder = new Label("Non hai aggiunto ancora nessun evento.");
        placeholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #888; -fx-padding: 20; -fx-alignment: center;");
        eventContainer.getChildren().add(placeholder);
    }

    // Metodo per gestire l'eliminazione dell'evento
    @FXML
    private void onEliminaEvento(EventoBean evento) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {
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


    private void onGestisciEventoClicked(long idEvento) throws ViewFactoryException {
        // Imposta l'ID evento nella sessione
        session.setIdEvento(idEvento);
        Stage currentStage = (Stage) eventContainer.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(currentStage);
        Model.getInstance().getViewFactory().showModificaEvento(session);
    }

    private void onReportButtonClicked(long idEvento) throws ViewFactoryException {
        session.setIdEvento(idEvento);
        Model.getInstance().getViewFactory().showReportView(session);
    }

}