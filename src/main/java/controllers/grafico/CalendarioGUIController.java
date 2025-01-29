package controllers.grafico;

import controllers.applicativo.IscrizioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.EventoNonTrovatoException;
import engclasses.exceptions.ViewFactoryException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import engclasses.pattern.Model;
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
    public void initialize() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {
        currentMonth = YearMonth.now();
        updateCalendar();

        previousMonthButton.setOnAction(event -> {
            currentMonth = currentMonth.minusMonths(1);
            try {
                updateCalendar();
            } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException |
                     EventoNonTrovatoException e) {
                throw new RuntimeException(e);
            }
        });

        nextMonthButton.setOnAction(event -> {
            currentMonth = currentMonth.plusMonths(1);
            try {
                updateCalendar();
            } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException |
                     EventoNonTrovatoException e) {
                throw new RuntimeException(e);
            }
        });
    }

    void updateCalendar() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {
        IscrizioneEventoController iscrizioneEventoController = new IscrizioneEventoController(session);

        updateCalendarTitle();
        clearCalendarGrid();
        addDayHeaders();

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1 = Lunedì
        int daysInMonth = currentMonth.lengthOfMonth();

        Map<Integer, List<EventoBean>> eventiDelMese = iscrizioneEventoController.getEventiDelMese(
                currentMonth.getMonthValue(), currentMonth.getYear()
        );

        populateCalendarDays(daysInMonth, dayOfWeek, eventiDelMese);
    }

    // Imposta il titolo del calendario (Mese e Anno)
    private void updateCalendarTitle() {
        monthYearLabel.setText(MESI[currentMonth.getMonthValue() - 1] + " " + currentMonth.getYear());
    }

    // Pulisce la griglia prima di riempirla
    private void clearCalendarGrid() {
        calendarGrid.getChildren().clear();
    }

    // Aggiunge le intestazioni dei giorni della settimana
    private void addDayHeaders() {
        for (int col = 0; col < 7; col++) {
            Label dayLabel = new Label(GIORNI[col]);
            calendarGrid.add(dayLabel, col, 0);
        }
    }

    // Popola la griglia con i giorni del mese
    private void populateCalendarDays(int daysInMonth, int dayOfWeek, Map<Integer, List<EventoBean>> eventiDelMese) {
        int day = 1;
        for (int row = 1; row <= 6; row++) {
            for (int col = 0; col < 7; col++) {
                if ((row == 1 && col < dayOfWeek - 1) || day > daysInMonth) {
                    continue;
                }

                StackPane dayCell = createDayCell(day, eventiDelMese);
                calendarGrid.add(dayCell, col, row);
                day++;
            }
        }
    }

    // Crea la cella del giorno con eventi e colore appropriato
    private StackPane createDayCell(int day, Map<Integer, List<EventoBean>> eventiDelMese) {
        StackPane stackPane = new StackPane();

        Rectangle rectangle = new Rectangle(50, 50);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1.0);

        List<EventoBean> eventiDelGiorno = eventiDelMese.getOrDefault(day, Collections.emptyList());
        boolean isClickableDay = calculateClickableDay(day, eventiDelGiorno);

        if (session.isOrganizzatore()) {
            rectangle.setFill(isClickableDay ? Color.LIGHTGRAY : Color.TRANSPARENT);
        } else {
            rectangle.setFill(eventiDelGiorno.isEmpty() ? Color.TRANSPARENT : Color.GOLD);
        }

        if (isClickableDay) {
            attachEventClickAction(day, currentMonth.getMonthValue(), currentMonth.getYear(), stackPane);
        }

        Label dayLabel = new Label(String.valueOf(day));
        stackPane.getChildren().addAll(rectangle, dayLabel);

        return stackPane;
    }

    // Determina se un giorno è cliccabile (diverso per organizzatori e partecipanti)
    private boolean calculateClickableDay(int day, List<EventoBean> eventiDelGiorno) {
        LocalDate today = LocalDate.now();
        int currentDay = today.getDayOfMonth();
        int currentMonthValue = today.getMonthValue();
        int currentYear = today.getYear();

        if (session.isOrganizzatore()) {
            return (currentYear < currentMonth.getYear()) ||
                    (currentYear == currentMonth.getYear() && currentMonthValue < currentMonth.getMonthValue()) ||
                    (currentYear == currentMonth.getYear() && currentMonthValue == currentMonth.getMonthValue() && day >= currentDay);
        }

        return !eventiDelGiorno.isEmpty();
    }

    private void attachEventClickAction(int giorno, int mese, int anno, StackPane calendarCell) {
        calendarCell.setOnMouseClicked(e -> {

            // Memorizza la data selezionata
            String selectedDate = String.format("%04d-%02d-%02d", anno, mese, giorno);

            // Ottieni gli eventi filtrati direttamente dal controller applicativo
            IscrizioneEventoController applicativoController = new IscrizioneEventoController(session);
            List<EventoBean> eventiDelGiorno;
            try {
                eventiDelGiorno = applicativoController.getEventiPerGiorno(giorno, mese, anno);
            } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException |
                     EventoNonTrovatoException ex) {
                throw new RuntimeException(ex);
            }

            // Salva la lista nella sessione
            session.setEventiDelGiorno(eventiDelGiorno);

            if (session.isOrganizzatore()) {
                // Mostra la finestra per aggiungere gli eventi
                try {
                    Model.getInstance().getViewFactory().showAggiungiEvento(session, selectedDate);
                } catch (ViewFactoryException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                // Mostra la finestra per i partecipanti con gli eventi disponibili
                try {
                    Model.getInstance().getViewFactory().showEventiGiornalieri(session);
                } catch (ViewFactoryException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
