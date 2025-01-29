package engclasses.dao;

import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import model.Tracker;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe GestioneTrackerDAO.
 * Questo test verifica il corretto funzionamento delle operazioni di recupero e salvataggio dei dati
 * del tracker, sia nel database che nel buffer.

 * Autore del test: Marco Marulli
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GestioneTrackerDAOTest {

    // Variabili statiche per testare ID validi e non validi
    private static final String VALID_USER_ID = "user123";
    private static final String INVALID_USER_ID = "nonEsistente";

    // Dati fittizi per testare il tracker
    private static final int LETTURA_CORANO = 5;
    private static final int GOAL = 10;
    private static final double PROGRESSO = 50.0;
    private static final boolean HA_DIGIUNATO = true;
    private static final String NOTE_DIGIUNO = "Giornata intensa";

    private Tracker testTracker;

    @BeforeAll
    void setup() {
        // Disabilita JavaFX per i test
        System.setProperty("javafx.runningTest", "true");
    }

    @BeforeEach
    void setupEach() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Crea un Tracker di test
        testTracker = new Tracker(LETTURA_CORANO, VALID_USER_ID, GOAL, PROGRESSO);
        testTracker.setHaDigiunato(HA_DIGIUNATO);
        testTracker.setNoteDigiuno(NOTE_DIGIUNO);

        // Imposta dati fittizi per le preghiere
        testTracker.setPreghiera("Fajr", true);
        testTracker.setPreghiera("Dhuhr", true);
        testTracker.setPreghiera("Asr", false);
        testTracker.setPreghiera("Maghrib", true);
        testTracker.setPreghiera("Isha", false);

        // Salva il tracker nel database
        GestioneTrackerDAO.saveOrUpdateTracker(testTracker, true);
    }

    /**
     * Test: Verifica che un tracker esistente venga correttamente recuperato dal database.
     */
    @Test
    void testGetTrackerFromDb() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Recupera il tracker
        Tracker trackerRecuperato = GestioneTrackerDAO.getTracker(VALID_USER_ID, true);

        // Controlla che il tracker non sia nullo
        assertNotNull(trackerRecuperato, "Il tracker recuperato non dovrebbe essere nullo.");

        // Confronta i valori recuperati con quelli attesi
        assertEquals(LETTURA_CORANO, trackerRecuperato.getLetturaCorano(), "La lettura del Corano non è corretta.");
        assertEquals(GOAL, trackerRecuperato.getGoal(), "Il goal non è corretto.");
        assertEquals(PROGRESSO, trackerRecuperato.getProgresso(), "Il progresso non è corretto.");
        assertEquals(HA_DIGIUNATO, trackerRecuperato.isHaDigiunato(), "Lo stato del digiuno non è corretto.");
        assertEquals(NOTE_DIGIUNO, trackerRecuperato.getNoteDigiuno(), "Le note sul digiuno non corrispondono.");
    }

    /**
     * Test: Verifica il comportamento nel caso in cui il tracker non esista nel database.
     */
    @Test
    void testGetTrackerNonEsistente() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Recupera un tracker con un ID non valido
        Tracker trackerRecuperato = GestioneTrackerDAO.getTracker(INVALID_USER_ID, true);

        // Deve essere nullo perché l'utente non esiste
        assertNull(trackerRecuperato, "Il tracker per un utente non esistente dovrebbe essere nullo.");
    }

    /**
     * Test: Salva o aggiorna un tracker nel database e verifica che le modifiche siano persistite.
     */
    @Test
    void testSaveOrUpdateTrackerInDb() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Modifica i valori del tracker
        testTracker.setLetturaCorano(7);
        testTracker.setGoal(15);
        testTracker.setProgresso(80.0);
        testTracker.setHaDigiunato(false);
        testTracker.setNoteDigiuno("Modificato per il test");

        // Salva le modifiche nel database
        GestioneTrackerDAO.saveOrUpdateTracker(testTracker, true);

        // Recupera di nuovo il tracker
        Tracker trackerModificato = GestioneTrackerDAO.getTracker(VALID_USER_ID, true);

        // Verifica che le modifiche siano state salvate
        assertNotNull(trackerModificato, "Il tracker modificato dovrebbe esistere nel database.");
        assertEquals(7, trackerModificato.getLetturaCorano(), "La lettura del Corano non è stata aggiornata correttamente.");
        assertEquals(15, trackerModificato.getGoal(), "Il goal non è stato aggiornato correttamente.");
        assertEquals(80.0, trackerModificato.getProgresso(), "Il progresso non è stato aggiornato correttamente.");
        assertFalse(trackerModificato.isHaDigiunato(), "Lo stato del digiuno non è stato aggiornato.");
        assertEquals("Modificato per il test", trackerModificato.getNoteDigiuno(), "Le note sul digiuno non sono state aggiornate.");
    }

    /**
     * Test: Verifica il recupero di un tracker dal buffer.
     */
    @Test
    void testGetTrackerFromBuffer() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Salva il tracker nel buffer
        GestioneTrackerDAO.saveOrUpdateTracker(testTracker, false);

        // Recupera il tracker dal buffer
        Tracker trackerBuffer = GestioneTrackerDAO.getTracker(VALID_USER_ID, false);

        // Verifica che il tracker sia stato salvato e recuperato correttamente
        assertNotNull(trackerBuffer, "Il tracker dovrebbe essere presente nel buffer.");
        assertEquals(VALID_USER_ID, trackerBuffer.getIdUtente(), "L'ID utente recuperato dal buffer è errato.");
    }

    /**
     * Test: Verifica il comportamento quando si recupera un tracker che non esiste nel buffer.
     */
    @Test
    void testGetTrackerNonEsistenteInBuffer() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Recupera un tracker non salvato nel buffer
        Tracker trackerBuffer = GestioneTrackerDAO.getTracker(INVALID_USER_ID, false);

        // Deve essere nullo perché l'utente non è presente nel buffer
        assertNull(trackerBuffer, "Il tracker inesistente nel buffer dovrebbe essere nullo.");
    }
}