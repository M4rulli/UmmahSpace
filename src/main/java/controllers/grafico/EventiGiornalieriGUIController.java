package controllers.grafico;

import controllers.applicativo.IscrizioneEventoController;
import engclasses.beans.EventoBean;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import misc.Model;
import misc.Session;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EventiGiornalieriGUIController {

    @FXML
    private VBox eventContainer;

    private final Session session;
    private final List<EventoBean> eventi;

    public EventiGiornalieriGUIController(Session session, List<EventoBean> eventi) {
        this.session = session;
        this.eventi = eventi;
    }

    @FXML
    public void initialize() {
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

        Label titleLabel = new Label(evento.getTitolo());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333; ");

        Label timeLabel = new Label("Orario: " + evento.getOrario());
        timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; ");

        Label organizerLabel = new Label("Organizzatore: " + evento.getNomeOrganizzatore() + " " + evento.getCognomeOrganizzatore());
        organizerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");

        Label statusLabel = new Label(evento.isChiuso() ? "Chiuso" : "Aperto");
        statusLabel.setStyle(evento.isChiuso()
                ? "-fx-text-fill: red; -fx-font-weight: bold;"
                : "-fx-text-fill: green; -fx-font-weight: bold;");

        Button detailButton = new Button("Visualizza Dettagli");
        detailButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5 10;");
        detailButton.setOnAction(e -> showEventDetails(evento, session));

        card.getChildren().addAll(titleLabel, timeLabel, organizerLabel, statusLabel, detailButton);
        card.setSpacing(10);

        return card;
    }

    private void showEventDetails(EventoBean evento, Session session) {
        Model.getInstance().getViewFactory().showEventDetailsView(evento, session);  // Passa la bean dell'evento
    }
}