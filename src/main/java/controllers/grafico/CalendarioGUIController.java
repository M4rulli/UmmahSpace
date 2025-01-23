package controllers.grafico;

import controllers.applicativo.IscrizioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.dao.IscrizioneEventoDAO;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import misc.Model;
import misc.Session;
import model.Evento;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class CalendarioGUIController {

    @FXML
    private Button previousMonthButton;

    @FXML
    private Button nextMonthButton;

    @FXML
    private Label monthYearLabel;

    @FXML
    private GridPane calendarGrid;

    private YearMonth currentMonth;

    // Nomi dei mesi e giorni
    private static final String[] MESI = {
            "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
            "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"
    };

    private static final String[] GIORNI = {
            "Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"
    };

    private final Session session;

    public CalendarioGUIController(Session session) {
        this.session = session;
    }

    @FXML
    public void initialize() {
        currentMonth = YearMonth.now();
        updateCalendar();

        previousMonthButton.setOnAction(event -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendar();
        });

        nextMonthButton.setOnAction(event -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar();
        });
    }

    void updateCalendar() {

        IscrizioneEventoController iscrizioneEventoController = new IscrizioneEventoController();

        // Aggiorna il titolo con il mese e l'anno
        monthYearLabel.setText(MESI[currentMonth.getMonthValue() - 1] + " " + currentMonth.getYear());

        // Pulisce la griglia prima di riempirla
        calendarGrid.getChildren().clear();

        // Aggiunge i giorni della settimana come intestazioni
        for (int col = 0; col < 7; col++) {
            Label dayLabel = new Label(GIORNI[col]);
            calendarGrid.add(dayLabel, col, 0); // Riga 0 per le intestazioni
        }

        // Ottiene il primo giorno del mese e i dettagli del mese corrente
        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1 = Lunedì
        int daysInMonth = currentMonth.lengthOfMonth();

        // Recupera eventi
        Map<Integer, List<EventoBean>> eventiDelMese = iscrizioneEventoController.getEventiDelMese(
                currentMonth.getMonthValue(), currentMonth.getYear()
        );

        // Popola la griglia con rettangoli per i giorni
        int day = 1;
        for (int row = 1; row <= 6; row++) { // Riga 1 per i giorni
            for (int col = 0; col < 7; col++) {
                if ((row == 1 && col < dayOfWeek - 1) || day > daysInMonth) {
                    continue;
                }

                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle();
                rectangle.setWidth(50);  // Larghezza rettangolo
                rectangle.setHeight(50); // Altezza rettangolo
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(1.0);

                // Recupera gli eventi del giorno specifico
                List<EventoBean> eventiDelGiorno = eventiDelMese.getOrDefault(day, Collections.emptyList());

                if (!eventiDelGiorno.isEmpty()) {
                    // Imposta il rettangolo su oro se ci sono eventi
                    rectangle.setFill(Color.GOLD);
                    attachEventClickAction(eventiDelGiorno, stackPane); // Aggiunge il click handler
                } else {
                    rectangle.setFill(Color.TRANSPARENT);
                }

                Label dayLabel = new Label(String.valueOf(day));
                stackPane.getChildren().addAll(rectangle, dayLabel);

                calendarGrid.add(stackPane, col, row);
                day++;
            }
        }
    }

    // Listener alla Cella
    private void attachEventClickAction(List<EventoBean> eventi, StackPane calendarCell) {
        if (!eventi.isEmpty()) {
            calendarCell.setOnMouseClicked(e -> {
                // Apri la vista con la lista degli eventi
                Model.getInstance().getViewFactory().showEventiGiornalieri(session);
            });
        }
    }
}