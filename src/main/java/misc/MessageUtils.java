package misc;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class MessageUtils {

    /**
     * Mostra un messaggio di conferma con il testo fornito..
     */
    public static void mostraMessaggioConferma(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null); // Nessun header
        alert.setContentText(messaggio); // Mostra il messaggio
        alert.showAndWait(); // Attende l'interazione dell'utente
    }

    /**
     * Mostra un messaggio di attenzione con il testo fornito..
     */
    public static void mostraMessaggioAttenzione(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titolo);
        alert.setHeaderText(null); // Nessun header
        alert.setContentText(messaggio); // Mostra il messaggio
        alert.showAndWait(); // Attende l'interazione dell'utente
    }

    /**
     * Mostra un messaggio di errore con il testo fornito.
     */
    public static void mostraMessaggioErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null); // Nessun header
        alert.setContentText(messaggio); // Mostra il messaggio
        alert.showAndWait(); // Attende l'interazione dell'utente
    }

    /**
     * Mostra una finestra di conferma (OK/Annulla) con il testo fornito
     */
    public static boolean mostraMessaggioConfermaConScelta(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null); // Nessun header
        alert.setContentText(messaggio); // Mostra il messaggio
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}