package controllers.grafico;

import engclasses.beans.EventoBean;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import misc.Model;
import misc.Session;

import java.io.IOException;
import java.util.List;

public class EventiOrganizzatoreGUIController {

    @FXML
    private VBox eventContainer;

    private final List<EventoBean> eventiOrganizzatore;
    private final Session session;

    public EventiOrganizzatoreGUIController(List<EventoBean> eventiOrganizzatore, Session session) {
        this.eventiOrganizzatore = eventiOrganizzatore;
        this.session = session;
    }

    @FXML
    public void initialize() {
        // Popola la GUI con gli eventi dell'organizzatore

        for (EventoBean evento : eventiOrganizzatore) {
            VBox card = createEventCard(evento);
            card.setStyle("-fx-background-color: transparent; -fx-border-color: #dddddd; "
                    + "-fx-border-radius: 10; -fx-padding: 15;");
            eventContainer.getChildren().add(card);
        }

        // Aggiungi un bottone per creare nuovi eventi
        Button addEventButton = new Button("Aggiungi");
        addEventButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5 10;");
        addEventButton.setOnAction(e -> showAggiungiEventoView());
        eventContainer.getChildren().add(addEventButton);
    }

    private VBox createEventCard(EventoBean evento) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: transparent; -fx-border-color: #dddddd; "
                + "-fx-border-radius: 10; -fx-padding: 15;");

        Label titleLabel = new Label(evento.getTitolo());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label timeLabel = new Label("Orario: " + evento.getOrario());
        timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Label statusLabel = new Label(evento.isChiuso() ? "Chiuso" : "Aperto");
        statusLabel.setStyle(evento.isChiuso()
                ? "-fx-text-fill: red; -fx-font-weight: bold;" // Rosso se chiuso
                : "-fx-text-fill: green; -fx-font-weight: bold;"); // Verde se aperto

        Button detailButton = new Button("Visualizza Dettagli");
        detailButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5 10;");
        detailButton.setOnAction(e -> showEventDetails(evento));

        card.getChildren().addAll(titleLabel, timeLabel, statusLabel, detailButton);
        card.setSpacing(10);

        return card;
    }

    private void showAggiungiEventoView() {
        showView("/path/to/AggiungiEventoView.fxml", new AggiungiEventoGUIController(), "Aggiungi Nuovo Evento");
    }


    private void showEventDetails(EventoBean evento) {
        Model.getInstance().getViewFactory().showEventDetailsView(evento, session);
    }

    // Metodo generico per caricare una vista
    private void showView(String fxmlPath, Object controller, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(controller);
            Pane root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il caricamento della vista", e);
        }
    }
}
