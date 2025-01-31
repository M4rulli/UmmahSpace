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


    private static final int MESE_NON_VALIDO = 8;
    private static final int ANNO_NON_VALIDO = 2022;
    private static final int MESE_VALIDO = 2;
    private static final int ANNO_VALIDO = 2025;


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
