package controllers.grafico;

import controllers.applicativo.IscrizioneEventoController;
import engclasses.beans.EventoBean;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import misc.Model;
import misc.Session;

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
    private LocalDate selectedDate;

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

        IscrizioneEventoController iscrizioneEventoController = new IscrizioneEventoController(session);

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

        // Giorno attuale
        LocalDate today = LocalDate.now();
        int currentDay = today.getDayOfMonth();
        int currentMonthValue = today.getMonthValue();
        int currentYear = today.getYear();

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

                // Calcola se il giorno è cliccabile (>= giorno attuale per organizzatore)
                boolean isClickableDay = (session.isOrganizzatore() && (
                        currentYear < currentMonth.getYear() ||
                                (currentYear == currentMonth.getYear() && currentMonthValue < currentMonth.getMonthValue()) ||
                                (currentYear == currentMonth.getYear() && currentMonthValue == currentMonth.getMonthValue() && day >= currentDay))
                ) || (!session.isOrganizzatore() && !eventiDelGiorno.isEmpty());


                if (session.isOrganizzatore()) {
                    if (isClickableDay) {
                        // Giorni cliccabili: grigi
                        rectangle.setFill(Color.LIGHTGRAY);
                    } else {
                        // Giorni non cliccabili: trasparenti
                        rectangle.setFill(Color.TRANSPARENT);
                    }
                } else if (!eventiDelGiorno.isEmpty()) {
                    // Partecipante: evidenzia giorni con eventi
                    rectangle.setFill(Color.GOLD);
                } else {
                    // Partecipante: nessun evento
                    rectangle.setFill(Color.TRANSPARENT);
                }

                // Rende cliccabili solo i giorni validi
                if (isClickableDay) {
                    attachEventClickAction(day, currentMonth.getMonthValue(), currentMonth.getYear(), stackPane);
                }

                Label dayLabel = new Label(String.valueOf(day));
                    stackPane.getChildren().addAll(rectangle, dayLabel);

                    calendarGrid.add(stackPane, col, row);
                    day++;
                }
            }
    }


    private void attachEventClickAction(int giorno, int mese, int anno, StackPane calendarCell) {
        calendarCell.setOnMouseClicked(e -> {

            // Memorizza la data selezionata
            String selectedDate = String.format("%04d-%02d-%02d", anno, mese, giorno);

            // Ottieni gli eventi filtrati direttamente dal controller applicativo
            IscrizioneEventoController applicativoController = new IscrizioneEventoController(session);
            List<EventoBean> eventiDelGiorno = applicativoController.getEventiPerGiorno(giorno, mese, anno);

            // Salva la lista nella sessione
            session.setEventiDelGiorno(eventiDelGiorno);

            if (session.isOrganizzatore()) {
                // Mostra la finestra per aggiungere gli eventi
                Model.getInstance().getViewFactory().showAggiungiEvento(session, selectedDate);
            } else {
                // Mostra la finestra per i partecipanti con gli eventi disponibili
                Model.getInstance().getViewFactory().showEventiGiornalieri(session);
            }
        });
    }
}
