package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.dao.GestioneEventoDAO;
import engclasses.exceptions.*;
import misc.Session;
import model.Evento;
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
    private static final long EVENT_ID = 1004; // ID di un evento esistente
    private static final String UTENTE_ID = "organizzatore123"; // ID di un organizzatore valido

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

    // Crea l'evento prima di ogni test
    @BeforeEach
    void setupEach() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {
        // Elimina l'evento se esiste già per evitare problemi con modifiche o duplicati
        if (GestioneEventoDAO.getEventoById(EVENT_ID, true) != null) {
            controller.eliminaEvento(EVENT_ID, UTENTE_ID);
        }
        Evento evento = new Evento();
        evento.setIdOrganizzatore(UTENTE_ID);
        evento.setIdEvento(EVENT_ID);
        evento.setTitolo("Evento di Test");
        evento.setDescrizione("Descrizione di Test");
        evento.setData("2025-03-15");
        evento.setOrario("15:00 - 17:00");
        evento.setLimitePartecipanti("100");

        GestioneEventoDAO.aggiungiEvento(evento, true);
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
    void testModificaEventoSuccess() {
        EventoBean evento = new EventoBean();
        evento.setIdEvento(EVENT_ID);
        evento.setTitolo("Nuovo Titolo Evento");
        evento.setDescrizione("Descrizione di Test");
        evento.setOrario("23:00 - 23:30");
        evento.setLimitePartecipanti("150");
        evento.setData("2025-05-19");

        assertDoesNotThrow(() -> controller.aggiornaEvento(evento, evento.getIdEvento()));
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

        assertTrue(exception.getMessage().contains("Il titolo dell'evento è obbligatorio")
                        || exception.getMessage().contains("L'orario dell'evento non può essere vuoto"),
                "L'errore deve indicare il fallimento dell'aggiunta.");
    }
}
