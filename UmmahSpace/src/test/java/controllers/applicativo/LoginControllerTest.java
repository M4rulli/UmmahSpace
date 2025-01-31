package controllers.applicativo;

import engclasses.beans.LoginBean;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.LoginFallitoException;
import engclasses.exceptions.TrackerNonTrovatoException;
import misc.Session;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe LoginController.
 * Questo test verifica il corretto funzionamento del metodo di login per organizzatori e partecipanti,
 * assicurandosi che la sessione venga aggiornata correttamente in caso di successo
 * e che vengano gestiti gli errori in caso di credenziali errate.

 * Autore del test: Marco Marulli
 */
class LoginControllerTest {

    // Variabili statiche per migliorare la leggibilità dei test
    private static final String VALID_USERNAME_ORGANIZZATORE = "Ciao";
    private static final String VALID_PASSWORD_ORGANIZZATORE = "1";

    private static final String INVALID_USERNAME = "partecipanteTest";
    private static final String INVALID_PASSWORD = "passwordErrata";


    /**
     * Test: Effettua il login con credenziali corrette per un organizzatore.
     * - Verifica che il metodo `effettuaLogin` restituisca un oggetto valido.
     * - Controlla che la sessione venga aggiornata con i dati dell'organizzatore.
     */
    @Test
    void testLoginOrganizzatoreCorretto() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, LoginFallitoException, TrackerNonTrovatoException {
        // Creazione della sessione simulata con persistenza attivata
        Session session = new Session(true);
        session.setIsOrganizzatore(true); // Simula un organizzatore

        // Creazione della LoginBean con credenziali corrette
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(VALID_USERNAME_ORGANIZZATORE);
        loginBean.setPassword(VALID_PASSWORD_ORGANIZZATORE);

        // Creazione del LoginController con la sessione
        LoginController loginController = new LoginController(session);

        // Esegui il login e verifica che il risultato non sia nullo
        assertNotNull(loginController.effettuaLogin(loginBean, session.isPersistence()),
                "Il login dovrebbe restituire un GestioneTrackerBean valido.");

        // Verifica che la sessione sia stata aggiornata correttamente
        assertNotNull(session.getIdUtente(), "L'ID utente dovrebbe essere aggiornato.");
        assertNotNull(session.getNomeOrganizzatore(), "Il nome dell'organizzatore dovrebbe essere impostato.");
        assertNotNull(session.getCognomeOrganizzatore(), "Il cognome dell'organizzatore dovrebbe essere impostato.");
    }

    /**
     * Test: Effettua il login con credenziali errate per un partecipante.
     * - Verifica che il metodo `effettuaLogin` generi un'eccezione `LoginFallitoException`.
     * - Controlla che la sessione non venga aggiornata dopo un login fallito.
     */
    @Test
    void testLoginPartecipanteCredenzialiErrate() {
        // Creazione della sessione simulata con persistenza disattivata
        Session session = new Session(false);
        session.setIsOrganizzatore(false); // Simula un partecipante

        // Creazione della LoginBean con credenziali errate
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(INVALID_USERNAME);
        loginBean.setPassword(INVALID_PASSWORD);

        // Creazione del LoginController con la sessione
        LoginController loginController = new LoginController(session);

        // Verifica che venga lanciata un'eccezione LoginFallitoException
        assertThrows(LoginFallitoException.class,
                () -> loginController.effettuaLogin(loginBean, session.isPersistence()),
                "Il login con credenziali errate dovrebbe generare un'eccezione LoginFallitoException.");

        // Verifica che la sessione non sia stata modificata dopo il login fallito
        assertNull(session.getIdUtente(), "L'ID utente non dovrebbe essere impostato dopo un login fallito.");
        assertNull(session.getNome(), "Il nome non dovrebbe essere impostato dopo un login fallito.");
    }
}