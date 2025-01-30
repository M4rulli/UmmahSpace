package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.exceptions.*;
import misc.Session;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe GestioneEventiController.
 * Questo test verifica il comportamento del controller nella gestione degli eventi.
 * Vengono testati i flussi di eliminazione, modifica e aggiunta di un evento.
 * Autore del test: Mei Baka
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GestioneEventiControllerTest {

    private GestioneEventoController controller;
    private static final long EVENT_ID = 1001L; // ID di un evento esistente
    private static final String UTENTE_ID = "5145bbfc-dcde-4c98-a42e-2502753feb8c"; // ID di un organizzatore valido

    /**
     * Configurazione iniziale del test.
     * - Inizializza la sessione con un organizzatore.
     */
    @BeforeAll
    void setup() {
        Session session = new Session(true);
        session.setIdUtente(UTENTE_ID);
        controller = new GestioneEventoController(session);
    }

    /**
     * Test: Cancellazione di un evento esistente.
     * Verifica che un organizzatore possa cancellare con successo un evento.
     */
    @Test
    void testEliminaEventoSuccess() throws EventoNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        EventoBean evento = new EventoBean();
        evento.setIdEvento(EVENT_ID);


        boolean result = controller.eliminaEvento(evento.getIdEvento(),UTENTE_ID);
        assertTrue(result, "L'evento deve essere cancellato con successo.");
    }

    /**
     * Test: Modifica di un evento esistente.
     * Verifica che un organizzatore possa modificare un evento con successo.
     */
    @Test
    void testModificaEventoSuccess() throws EventoNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        EventoBean evento = new EventoBean();
        evento.setIdEvento(EVENT_ID);
        evento.setTitolo("Nuovo Titolo Evento");

        boolean result = controller.eliminaEvento(evento.getIdEvento(),UTENTE_ID);
        assertTrue(result, "L'evento deve essere modificato con successo.");
    }

    /**
     * Test: Tentativo fallito di aggiunta di un evento.
     * Verifica che un evento non possa essere aggiunto a causa di un errore (ad esempio, dati mancanti).
     * Il metodo deve lanciare un'`ValidazioneEventoException` con un messaggio di errore adeguato.
     */
    @Test
    void testAggiungiEventoFail() {
        EventoBean evento = new EventoBean(); // Evento senza dati validi

        Exception exception = assertThrows(ValidazioneEventoException.class, () ->
                controller.aggiungiEvento(evento));

        assertTrue(exception.getMessage().contains("Errore durante l'aggiunta dell'evento"),
                "L'errore deve indicare il fallimento dell'aggiunta.");
    }
}
