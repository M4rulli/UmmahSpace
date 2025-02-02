package controllers.applicativo;

import engclasses.beans.GestioneTrackerBean;
import engclasses.beans.LoginBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.dao.OrganizzatoreDAO;
import engclasses.dao.PartecipanteDAO;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.LoginFallitoException;
import engclasses.exceptions.TrackerNonTrovatoException;
import engclasses.pattern.BeanFactory;
import misc.Session;
import model.Organizzatore;
import model.Partecipante;
import model.Tracker;

public class LoginController {

    private final Session session;

    public LoginController(Session session) {
        this.session = session;
    }

    public GestioneTrackerBean effettuaLogin(LoginBean loginBean, boolean persistence) throws LoginFallitoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, TrackerNonTrovatoException {

        // Validazione campi di login
        String errori = validaCampiLogin(loginBean);
        if (!errori.isEmpty()) {
            throw new LoginFallitoException(errori);
        }

        GestioneTrackerBean trackerBean;

        // Login in base al ruolo
        if (session.isOrganizzatore()) {
            trackerBean = effettuaLoginOrganizzatore(loginBean, persistence);
        } else {
            trackerBean = effettuaLoginPartecipante(loginBean, persistence);
        }

        return trackerBean;
    }

    // Metodo per validare i campi di login
    private String validaCampiLogin(LoginBean loginBean) {
        StringBuilder errori = new StringBuilder();

        if (loginBean.getUsername() == null || loginBean.getUsername().trim().isEmpty()) {
            errori.append("Il campo username non può essere vuoto.\n");
        }
        if (loginBean.getPassword() == null || loginBean.getPassword().trim().isEmpty()) {
            errori.append("Il campo password non può essere vuoto.\n");
        }
        return errori.toString();
    }

    // Metodo per il login di un organizzatore
    private GestioneTrackerBean effettuaLoginOrganizzatore(LoginBean loginBean, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, LoginFallitoException {
        Organizzatore organizzatore = OrganizzatoreDAO.selezionaOrganizzatore("username", loginBean.getUsername(), persistence);

        if (organizzatore != null && organizzatore.getPassword().equals(loginBean.getPassword())) {
            aggiornaSessioneOrganizzatore(organizzatore);
            return new GestioneTrackerBean();
        }
        throw new LoginFallitoException("Credenziali non valide: controlla username e password.");
    }

    // Metodo per il login di un partecipante
    private GestioneTrackerBean effettuaLoginPartecipante(LoginBean loginBean, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, TrackerNonTrovatoException, LoginFallitoException {
        Partecipante partecipante = PartecipanteDAO.selezionaPartecipante("username", loginBean.getUsername(), persistence);

        if (partecipante != null && partecipante.getPassword().equals(loginBean.getPassword())) {
            aggiornaSessionePartecipante(partecipante);
            return recuperaTracker(partecipante, persistence);
        }
        throw new LoginFallitoException("Credenziali non valide: controlla username e password.");
    }

    // Metodo per aggiornare la sessione dell'organizzatore
    private void aggiornaSessioneOrganizzatore(Organizzatore organizzatore) {
        session.setIdUtente(organizzatore.getIdUtente());
        session.setNome(organizzatore.getNome());
        session.setNomeOrganizzatore(organizzatore.getNome());
        session.setCognomeOrganizzatore(organizzatore.getCognome());
    }

    // Metodo per aggiornare la sessione del partecipante
    private void aggiornaSessionePartecipante(Partecipante partecipante) {
        session.setIdUtente(partecipante.getIdUtente());
        session.setNome(partecipante.getNome());
    }


    // Metodo per recuperare il tracker di un partecipante
    private GestioneTrackerBean recuperaTracker(Partecipante partecipante, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, TrackerNonTrovatoException {
        Tracker tracker = GestioneTrackerDAO.getTracker(partecipante.getIdUtente(), persistence);
        if (tracker != null) {
            return BeanFactory.createTrackerBeanFromFactory(tracker);
        } else {
            throw new TrackerNonTrovatoException("Nessun tracker trovato per il partecipante: " + partecipante.getUsername());
        }
    }
}