package engclasses.dao;

import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import model.Evento;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe IscrizioneEventoDAO.
 * Verifica il corretto funzionamento del recupero e aggiornamento degli eventi nel database e nel buffer.
 * Autore del test: Mei Baka
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IscrizioneEventoDAOTest {

    private static final long VALID_EVENT_ID = 1001;
    private static final int MESE_NON_VALIDO = 8;
    private static final int ANNO_NON_VALIDO = 2022;
    private static final int MESE_VALIDO = 2;
    private static final int ANNO_VALIDO = 2025;
    private static final String VALID_ORGANIZER_ID = "organizer12";

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

    @BeforeEach
    void setupEach() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Crea un evento di test con dati fittizi
        Evento testEvento = new Evento(TITOLO, DESCRIZIONE, DATA, ORARIO, LIMITE_PARTECIPANTI, ISCRITTI, LINK,
                NOME_ORGANIZZATORE, COGNOME_ORGANIZZATORE, STATO, VALID_EVENT_ID, VALID_ORGANIZER_ID);

        // Salva l'evento nel database
        GestioneEventoDAO.aggiungiEvento(testEvento, true);
    }

    /**
     * Test: Verifica il comportamento quando si recuperano eventi per un mese e anno che esistono nel database.
     */

    @Test
    void testGetEventiPerMeseAnnoDalDb() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Recupera gli eventi dal database
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(MESE_VALIDO, ANNO_VALIDO, true);

        // Verifica che la lista non sia nulla
        assertNotNull(eventi, "La lista degli eventi non dovrebbe essere nulla.");

        // Verifica che gli eventi abbiano il mese e l'anno entrambi corretti
        for (Evento evento : eventi) {
            assertTrue(evento.getData().contains("2025-02"), "La data dell'evento non corrisponde al mese e anno richiesto.");
        }
    }

    /**
     * Test: Verifica il comportamento quando si recuperano eventi per un mese e anno che esistono nel buffer.
     */

    @Test
    void testGetEventiPerMeseAnnoDalBuffer() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Recupera gli eventi dal buffer
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(MESE_VALIDO, ANNO_VALIDO, false);

        // Verifica che la lista non sia nulla
        assertNotNull(eventi, "La lista degli eventi nel buffer non dovrebbe essere nulla.");

        // Verifica che gli eventi abbiano il mese e l'anno entrambi corretti
        for (Evento evento : eventi) {
            assertTrue(evento.getData().contains("2025-02"), "La data dell'evento nel buffer non corrisponde.");
        }
    }

    /**
     * Test: Verifica il comportamento quando si aggiorna il numero di iscritti di un evento che esiste nel database.
     */

    @Test
    void testAggiornaNumeroIscrittiNelDb() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Incrementa il numero di iscritti
        IscrizioneEventoDAO.aggiornaNumeroIscritti(VALID_EVENT_ID, 1, true);

        // Recupera nuovamente l'evento
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(MESE_VALIDO, ANNO_VALIDO, true);
        //NON SO SE VA BENE
        Evento eventoModificato = eventi.stream().filter(e -> e.getIdEvento() == VALID_EVENT_ID).findFirst().orElse(null);

        assertNotNull(eventoModificato, "L'evento non dovrebbe essere nullo.");
        assertEquals(6, eventoModificato.getIscritti(), "Il numero di iscritti non è stato aggiornato correttamente.");
    }

    /**
     * Test: Verifica il comportamento quando si aggiorna il numero di iscritti di un evento che esiste nel buffer.
     */

    @Test
    void testAggiornaNumeroIscrittiNelBuffer() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Incrementa il numero di iscritti nel buffer
        IscrizioneEventoDAO.aggiornaNumeroIscritti(VALID_EVENT_ID, 1, false);

        // Recupera l'evento dal buffer
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(MESE_VALIDO, ANNO_VALIDO, false);
        //NON SO SE VA BENE
        Evento eventoModificato = eventi.stream().filter(e -> e.getIdEvento() == VALID_EVENT_ID).findFirst().orElse(null);

        assertNotNull(eventoModificato, "L'evento nel buffer non dovrebbe essere nullo.");
        assertEquals(6, eventoModificato.getIscritti(), "Il numero di iscritti nel buffer non è stato aggiornato correttamente.");
    }
    /**
     * Test: Verifica il comportamento quando si recuperano eventi per un mese e anno che non esistono nel buffer.
     */
    @Test
    void testGetEventiPerMeseAnnoNonEsistentiNelBuffer() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Recupera gli eventi per un mese e anno che non esistono nel buffer
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(MESE_NON_VALIDO, ANNO_NON_VALIDO, false);

        // Verifica che la lista degli eventi sia nulla
        assertNotNull(eventi, "La lista degli eventi nel buffer non dovrebbe essere nulla.");
    }


}
