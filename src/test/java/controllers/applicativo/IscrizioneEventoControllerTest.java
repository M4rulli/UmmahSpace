package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.dao.PartecipazioneDAO;
import engclasses.exceptions.*;
import misc.Session;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe IscrizioneEventoController.
 * Questo test verifica il comportamento del controller nella gestione delle iscrizioni.
 * Vengono testati i flussi di iscrizione, cancellazione, doppie iscrizioni e casi di errore.

 * Autore del test: Marco Marulli
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IscrizioneEventoControllerTest {

    private IscrizioneEventoController controller;

    private static final long EVENT_ID = 1001L; // ID di un evento disponibile per l'iscrizione
    private static final long EVENT_ID_PIENO = 1002L; // ID di un evento che ha già raggiunto il limite massimo di partecipanti
    private static final String USER_ID = "5145bbfc-dcde-4c98-a42e-2502753feb8c"; // ID di un utente valido

    /**
     * Configurazione iniziale del test.
     * - Inizializza la sessione con un utente reale.
     * - Disabilita JavaFX nei test per evitare problemi con l'interfaccia grafica.
     * - Assicura che l'utente non sia già iscritto all'evento prima di ogni test.
     */
    @BeforeAll
    void setup() throws DatabaseConnessioneFallitaException {
        Session session = new Session(true);
        session.setIdUtente(USER_ID);
        System.setProperty("javafx.runningTest", "true");
        controller = new IscrizioneEventoController(session);

        // Rimuove eventuali iscrizioni precedenti dell'utente all'evento
        PartecipazioneDAO.rimuoviPartecipazione(EVENT_ID, USER_ID, session.isPersistence());
    }

    /**
     * Test: Cancellazione di un'iscrizione esistente.
     * Verifica che un utente possa annullare la propria iscrizione a un evento esistente.
     * Il metodo dovrebbe restituire `true` se l'iscrizione è stata cancellata correttamente.
     */
    @Test
    void testCancellaIscrizioneSuccess() {
        EventoBean evento = new EventoBean();
        evento.setIdEvento(EVENT_ID);

        boolean result = controller.cancellaIscrizione(evento);
        assertTrue(result, "L'iscrizione deve essere cancellata con successo.");
    }

    /**
     * Test: Iscrizione a un evento disponibile.
     * Verifica che un utente possa iscriversi con successo a un evento esistente e non pieno.
     * Il metodo dovrebbe restituire `true` se l'iscrizione è avvenuta correttamente.
     */
    @Test
    void testIscriviPartecipanteSuccess() throws IscrizioneEventoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, UtenteNonTrovatoException {
        boolean result = controller.iscriviPartecipante(EVENT_ID);
        assertTrue(result, "L'iscrizione deve avere successo.");
    }

    /**
     * Test: Tentativo di iscrizione doppia allo stesso evento.
     * Un utente non può iscriversi due volte allo stesso evento.
     * Il metodo dovrebbe generare un'`IscrizioneEventoException` con un messaggio di errore adeguato.
     */
    @Test
    void testIscriviPartecipanteDuplicato() throws IscrizioneEventoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, UtenteNonTrovatoException {
        controller.iscriviPartecipante(EVENT_ID);

        Exception exception = assertThrows(IscrizioneEventoException.class, () ->
                controller.iscriviPartecipante(EVENT_ID));

        assertTrue(exception.getMessage().contains("Sei già iscritto a questo evento."),
                "L'errore deve indicare una doppia iscrizione.");
    }

    /**
     * Test: Tentativo di iscrizione con un ID evento non valido (≤ 0).
     * Verifica che venga lanciata un'`IscrizioneEventoException` con un messaggio adeguato.
     */
    @Test
    void testIscriviPartecipanteIdNonValido() {
        Exception exception = assertThrows(IscrizioneEventoException.class, () ->
                controller.iscriviPartecipante(0)); // ID non valido

        String expectedMessage = "Errore durante l'iscrizione: ID evento non valido.";
        assertTrue(exception.getMessage().contains(expectedMessage),
                "L'errore deve indicare che l'ID evento è non valido.");
    }

    /**
     * Test: Iscrizione a un evento già pieno.
     * Un utente non può iscriversi a un evento che ha raggiunto il numero massimo di partecipanti.
     * Il metodo deve lanciare un'`IscrizioneEventoException` con un messaggio di errore adeguato.
     */
    @Test
    void testIscriviPartecipanteEventoPieno() {
        Exception exception = assertThrows(IscrizioneEventoException.class, () ->
                controller.iscriviPartecipante(EVENT_ID_PIENO));

        assertTrue(exception.getMessage().contains("L'evento ha raggiunto il limite di partecipanti"),
                "L'errore deve indicare che l'evento è pieno.");
    }
}