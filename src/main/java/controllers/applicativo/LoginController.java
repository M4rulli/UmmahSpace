package controllers.applicativo;

import engclasses.beans.GestioneTrackerBean;
import engclasses.beans.LoginBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.dao.OrganizzatoreDAO;
import engclasses.dao.PartecipanteDAO;
import engclasses.pattern.GestioneTrackerBeanFactory;
import misc.Session;
import model.Organizzatore;
import model.Partecipante;
import model.Tracker;
import static misc.MessageUtils.mostraMessaggioErrore;

public class LoginController {

    private final Session session;

    private static final String LOGIN_ERROR_MESSAGE = "Errore di Login";

    public LoginController(Session session) {
        this.session = session;
    }

    public GestioneTrackerBean effettuaLogin(LoginBean loginBean, boolean persistence) {
        // Validazione campi di login
        String errori = validaCampiLogin(loginBean);
        if (!errori.isEmpty()) {
            mostraMessaggioErrore(LOGIN_ERROR_MESSAGE, errori);
            return null;
        }

        // Login in base al ruolo
        if (session.isOrganizzatore()) {
            return effettuaLoginOrganizzatore(loginBean, persistence);
        } else {
            return effettuaLoginPartecipante(loginBean, persistence);
        }
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
    private GestioneTrackerBean effettuaLoginOrganizzatore(LoginBean loginBean, boolean persistence) {
        Organizzatore organizzatore = OrganizzatoreDAO.selezionaOrganizzatore("username", loginBean.getUsername(), persistence);

        if (organizzatore != null && organizzatore.getPassword().equals(loginBean.getPassword())) {
            aggiornaSessioneOrganizzatore(organizzatore);
            return new GestioneTrackerBean();
        }

        mostraMessaggioErrore(LOGIN_ERROR_MESSAGE, "Credenziali non valide: controlla username e password.");
        return null;
    }

    // Metodo per il login di un partecipante
    private GestioneTrackerBean effettuaLoginPartecipante(LoginBean loginBean, boolean persistence) {
        Partecipante partecipante = PartecipanteDAO.selezionaPartecipante("username", loginBean.getUsername(), persistence);

        if (partecipante != null && partecipante.getPassword().equals(loginBean.getPassword())) {
            aggiornaSessionePartecipante(partecipante);
            return recuperaTracker(partecipante, persistence);
        }

        mostraMessaggioErrore(LOGIN_ERROR_MESSAGE, "Credenziali non valide: controlla username e password.");
        return null;
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
    private GestioneTrackerBean recuperaTracker(Partecipante partecipante, boolean persistence) {
        Tracker tracker = GestioneTrackerDAO.getTracker(partecipante.getIdUtente(), persistence);

        if (tracker != null) {
            return GestioneTrackerBeanFactory.createTrackerBeanFromFactory(tracker);
        } else {
            return null;
        }
    }


}