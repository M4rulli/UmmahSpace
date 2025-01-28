package controllers.grafico;

import controllers.applicativo.IscrizioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.beans.PartecipazioneBean;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import misc.Session;

import java.util.List;

import static misc.MessageUtils.*;

public class PartecipazioniGUIController {

    @FXML
    private VBox partecipazioniContainer;

    private final Session session;

    public PartecipazioniGUIController(Session session) {
        this.session = session;
    }

    @FXML
    public void initialize() {
        // Crea un'istanza del controller applicativo
        IscrizioneEventoController iscrizioneEventoController = new IscrizioneEventoController(session);
        // Recupera i dettagli delle dettagliPartecipazioneUtente dal livello applicativo
        List<EventoBean> dettagliPartecipazioni = iscrizioneEventoController.getDettagliPartecipazioneUtente(session.getIdUtente());

        // Verifica se ci sono dettagli
        if (dettagliPartecipazioni == null || dettagliPartecipazioni.isEmpty()) {
            // Mostra un messaggio placeholder
            Label placeholder = new Label("Non sei iscritto ancora a nessun evento.");
            placeholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #888; -fx-padding: 20; -fx-alignment: center;");
            partecipazioniContainer.getChildren().add(placeholder);
            return;
        }
        // Popola la GUI con gli eventi
        for (EventoBean dettagliPartecipazione  : dettagliPartecipazioni) {
            VBox card = creaCardPartecipazione(dettagliPartecipazione);
            partecipazioniContainer.getChildren().add(card);
        }
    }

    // Metodo per creare una card per una partecipazione
    private VBox creaCardPartecipazione(EventoBean partecipazione) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: transparent; -fx-border-color: #dddddd; "
                + "-fx-border-radius: 10; -fx-padding: 15; ");


        // Associa il bean all'oggetto grafico
        card.setUserData(partecipazione);

        // Titolo dell'evento
        Label titleLabel = new Label(partecipazione.getTitolo());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Orario dell'evento
        Label timeLabel = new Label("Orario: " + partecipazione.getOrario());
        timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        // Data dell'evento
        Label dateLabel = new Label("Data: " + partecipazione.getData());
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");

        // Nome e cognome dell'organizzatore
        Label organizerLabel = new Label("Organizzatore: " + partecipazione.getNomeOrganizzatore() + " " + partecipazione.getCognomeOrganizzatore());
        organizerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");

        // Numero di iscritti e limite partecipanti
        Label participantsLabel = new Label("Partecipanti: " + partecipazione.getIscritti() + "/" + partecipazione.getLimitePartecipanti());
        participantsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");

        // Stato dell'evento (Aperto o Chiuso)
        Label statusLabel = new Label(partecipazione.isChiuso() ? "Chiuso" : "Aperto");
        statusLabel.setStyle(partecipazione.isChiuso()
                ? "-fx-text-fill: red; -fx-font-weight: bold;"
                : "-fx-text-fill: green; -fx-font-weight: bold;");

        // Pulsante per annullare l'iscrizione
        Button detailButton = new Button("Disiscriviti");
        detailButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5 10;");
        detailButton.setOnAction(e -> onCancellaIscrizioneButton(partecipazione));

        // Aggiungi gli elementi alla card
        card.getChildren().addAll(titleLabel, timeLabel, dateLabel, organizerLabel, participantsLabel, statusLabel, detailButton);
        card.setSpacing(10);

        return card;
    }

    private void onCancellaIscrizioneButton(EventoBean evento) {
        // Mostra un messaggio di conferma
        boolean conferma = mostraMessaggioConfermaConScelta(
                "Conferma Cancellazione",
                "Sei sicuro di voler cancellare la tua iscrizione all'evento '" + evento.getTitolo() + "'?"
        );
        if (conferma) {
            // Crea un'istanza del controller applicativo
            IscrizioneEventoController iscrizioneEventoController = new IscrizioneEventoController(session);

            // Chiama il metodo applicativo per rimuovere l'iscrizione
            boolean risultato = iscrizioneEventoController.cancellaIscrizione(evento);

            // Se la cancellazione è riuscita, rimuovi la card dalla GUI
            if (risultato) {
                partecipazioniContainer.getChildren().removeIf(node -> node.getUserData() == evento);
                mostraMessaggioConferma("Cancellazione Riuscita", "Sei stato cancellato con successo dall'evento.");
            } else {
                mostraMessaggioErrore("Errore", "Si è verificato un errore durante la cancellazione dell'iscrizione.");
            }
        }
    }

}

