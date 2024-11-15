package com.project.ummahspace;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarController {

    @FXML
    private Label monthYearLabel;
    @FXML
    private GridPane calendarGrid;

    private YearMonth currentMonth;

    @FXML
    public void initialize() {
        currentMonth = YearMonth.now();
        displayCalendar(currentMonth);
    }

    @FXML
    private void handlePrevMonth() {
        currentMonth = currentMonth.minusMonths(1);
        displayCalendar(currentMonth);
    }

    @FXML
    private void handleNextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        displayCalendar(currentMonth);
    }

    private void displayCalendar(YearMonth month) {
        monthYearLabel.setText(month.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN) + " " + month.getYear());

        calendarGrid.getChildren().clear();

        LocalDate firstOfMonth = month.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;

        int daysInMonth = month.lengthOfMonth();
        int day = 1;
        for (int row = 1; row <= 5; row++) {
            for (int col = 0; col < 7; col++) {
                if ((row == 1 && col < dayOfWeek) || day > daysInMonth) {
                    continue;
                }
                StackPane dayPane = new StackPane(new Label(String.valueOf(day)));
                dayPane.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");
                calendarGrid.add(dayPane, col, row);
                day++;
            }
        }
    }
}
