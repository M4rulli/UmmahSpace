package engclasses.dao;

import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import model.Evento;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe GestioneEventiDAO.
 * Questo test verifica il corretto funzionamento delle operazioni di recupero e salvataggio dei dati
 * relativi agli eventi, sia nel database che nel buffer.
 * Autore del test: Mei Baka
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GestioneEventiDAOTest {

    // Costanti per identificare un evento valido e uno non esistente
    private static final long VALID_EVENT_ID = 1001;
    private static final long INVALID_EVENT_ID = 9999;
    private static final String VALID_ORGANIZER_ID = "organizer123";

    // Dati fittizi per un evento di test
    private static final String TITOLO = "La Dunya e la Akhira";
    private static final String DESCRIZIONE = "Benefici di questa e l'altra vita";
    private static final String DATA = "2025-02-10";
    private static final String ORARIO = "14:00";
    private static final String LIMITE_PARTECIPANTI = "20";
    private static final int ISCRITTI = 5;
    private static final String LINK = "http...";
    private static final String NOME_ORGANIZZATORE = "Mario";
    private static final String COGNOME_ORGANIZZATORE = "Rossi";
    private static final boolean STATO = true;

    private Evento testEvento;

    @BeforeEach
    void setupEach() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Crea un evento di test con dati fittizi
        testEvento = new Evento(TITOLO, DESCRIZIONE, DATA, ORARIO, LIMITE_PARTECIPANTI, ISCRITTI, LINK,
                NOME_ORGANIZZATORE, COGNOME_ORGANIZZATORE, STATO, VALID_EVENT_ID, VALID_ORGANIZER_ID);

        // Salva l'evento nel database
        GestioneEventoDAO.aggiungiEvento(testEvento, true);
    }

    /**
     * Test: Verifica che un evento esistente venga correttamente recuperato dal database tramite ID
     */
    @Test
    void testGetEventoFromDb() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Recupera l'evento tramite ID
        Evento eventoRecuperato = GestioneEventoDAO.getEventoById(VALID_EVENT_ID, true);

        // Controlla che l'evento non sia nullo
        assertNotNull(eventoRecuperato, "L'evento recuperato non dovrebbe essere nullo.");

        // Confronta i valori recuperati con quelli attesi
        assertEquals(TITOLO, eventoRecuperato.getTitolo(), "Il titolo dell'evento non corrisponde.");
        assertEquals(DESCRIZIONE, eventoRecuperato.getDescrizione(), "La descrizione dell'evento non corrisponde.");
        assertEquals(DATA, eventoRecuperato.getData(), "La data dell'evento non corrisponde.");
        assertEquals(ORARIO, eventoRecuperato.getOrario(), "L'orario dell'evento non corrisponde.");
    }

    /**
     * Test: Verifica il comportamento quando si cerca di recuperare un evento inesistente.
     */
    @Test
    void testGetEventoNonEsistente() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Recupera un evento con un ID non valido
        Evento eventoRecuperato = GestioneEventoDAO.getEventoById(INVALID_EVENT_ID, true);

        // Deve essere nullo perché l'evento non esiste
        assertNull(eventoRecuperato, "L'evento non esistente dovrebbe essere nullo.");
    }

    /**
     * Test: Modifica i dettagli di un evento e verifica che le modifiche siano salvate correttamente.
     */
    @Test
    void testUpdateEventoInDb() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Modifica alcuni dettagli dell'evento
        testEvento.setTitolo("Workshop Avanzato di Disegno");
        testEvento.setDescrizione("Corso avanzato per migliorare le tecniche di disegno");
        testEvento.setIscritti(10);

        // Salva le modifiche nel database
        GestioneEventoDAO.aggiornaEvento(testEvento, true);

        // Recupera l'evento aggiornato
        Evento eventoModificato = GestioneEventoDAO.getEventoById(VALID_EVENT_ID, true);

        // Verifica che le modifiche siano state salvate
        assertNotNull(eventoModificato, "L'evento modificato dovrebbe esistere nel database.");
        assertEquals("Workshop Avanzato di Disegno", eventoModificato.getTitolo(), "Il titolo non è stato aggiornato correttamente.");
        assertEquals("Corso avanzato per migliorare le tecniche di disegno", eventoModificato.getDescrizione(), "La descrizione non è stata aggiornata correttamente.");
        assertEquals(10, eventoModificato.getIscritti(), "Il numero di iscritti non è stato aggiornato correttamente.");
    }

    /**
     * Test: Verifica il recupero di un evento dal buffer.
     */
    @Test
    void testGetEventoFromBuffer() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Salva l'evento nel buffer
        GestioneEventoDAO.aggiungiEvento(testEvento, false);

        // Recupera l'evento dal buffer
        Evento eventoBuffer = GestioneEventoDAO.getEventoById(VALID_EVENT_ID, false);

        // Verifica che l'evento sia stato salvato e recuperato correttamente
        assertNotNull(eventoBuffer, "L'evento dovrebbe essere presente nel buffer.");
        assertEquals(VALID_EVENT_ID, eventoBuffer.getIdEvento(), "L'ID evento recuperato dal buffer è errato.");
    }

    /**
     * Test: Verifica il comportamento quando si recupera un evento che non esiste nel buffer.
     */
    @Test
    void testGetEventoNonEsistenteInBuffer() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Recupera un evento non salvato nel buffer
        Evento eventoBuffer = GestioneEventoDAO.getEventoById(INVALID_EVENT_ID, false);

        // Deve essere nullo perché l'evento non è presente nel buffer
        assertNull(eventoBuffer, "L'evento inesistente nel buffer dovrebbe essere nullo.");
    }
}
