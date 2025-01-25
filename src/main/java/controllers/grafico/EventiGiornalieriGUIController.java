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
import java.util.List;

public class EventiGiornalieriGUIController {

    @FXML
    private VBox eventContainer;

    private final Session session;

    public EventiGiornalieriGUIController(Session session) {
        this.session = session;
    }

    @FXML
    public void initialize() {

        // Ottieni mese e anno correnti dal controller grafico
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        // Crea un'istanza del controller applicativo
        IscrizioneEventoController applicativoController = new IscrizioneEventoController();

        // Recupera tutti gli eventi del mese corrente
        List<EventoBean> eventi = applicativoController.getTuttiGliEventiDelMese(currentMonth, currentYear);

        // Popola la GUI con gli eventi
        assert eventi != null;
        for (EventoBean evento : eventi) {

            // Crea una sezione card-like per ogni evento
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: transparent; -fx-border-color: #dddddd; "
                    + "-fx-border-radius: 10; -fx-padding: 15; ");

            Label titleLabel = new Label(evento.getTitolo());
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333; ");

            Label timeLabel = new Label("Orario: " + evento.getOrario());
            timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; ");

            Label organizerLabel = new Label("Organizzatore: " + evento.getNomeOrganizzatore() + " " + evento.getCognomeOrganizzatore());
            organizerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");

            // Indicazione dello stato
            Label statusLabel = new Label(evento.isChiuso() ? "Chiuso" : "Aperto");
            statusLabel.setStyle(evento.isChiuso()
                    ? "-fx-text-fill: red; -fx-font-weight: bold;" // Rosso se chiuso
                    : "-fx-text-fill: green; -fx-font-weight: bold;"); // Verde se aperto

            Button detailButton = new Button("Visualizza Dettagli");
            detailButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5 10;");

            detailButton.setOnAction(e -> showEventDetails(evento, session));

            card.getChildren().addAll(titleLabel, timeLabel, organizerLabel, statusLabel, detailButton);
            card.setSpacing(10);

            // Aggiunge la sezione card alla lista
            eventContainer.getChildren().add(card);
        }
        // Aggiunge uno stile globale al contenitore
        eventContainer.setStyle("-fx-spacing: 15; -fx-padding: 10; -fx-background-color: transparent;");
    }

    private void showEventDetails(EventoBean evento, Session session) {
        Model.getInstance().getViewFactory().showEventDetailsView(evento, session);  // Passa la bean dell'evento
    }
}