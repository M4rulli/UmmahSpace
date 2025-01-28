package controllers.applicativo;

import controllers.grafico.GestioneTrackerGUIController;
import controllers.grafico.LoginGUIController;
import engclasses.beans.GestioneTrackerBean;
import engclasses.beans.LoginBean;
import engclasses.dao.GestioneEventoDAO;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.dao.OrganizzatoreDAO;
import engclasses.dao.PartecipanteDAO;
import javafx.scene.control.Alert;
import misc.GestioneTrackerBeanFactory;
import misc.Model;
import misc.Session;
import model.Organizzatore;
import model.Partecipante;
import model.Tracker;
import model.Utente;

import java.util.List;

import static misc.MessageUtils.mostraMessaggioErrore;

public class LoginController {

    private final Session session;
    private String utenteId; // Per salvare l'ID dell'utente loggato
    private String nome;     // Per salvare il nome dell'utente loggato

    public LoginController(Session session) {
        this.session = session;
    }

    public GestioneTrackerBean effettuaLogin(LoginBean loginBean, boolean persistence) {

        // Validazione dei campi
        StringBuilder errori = new StringBuilder();
        if (loginBean.getUsername() == null || loginBean.getUsername().trim().isEmpty()) {
            errori.append("Il campo username non può essere vuoto.\n");
        }
        if (loginBean.getPassword() == null || loginBean.getPassword().trim().isEmpty()) {
            errori.append("Il campo password non può essere vuoto.\n");
        }
        if (!errori.isEmpty()) {
            // Mostra gli errori accumulati
            mostraMessaggioErrore("Errore di Login", errori.toString());
            return null;
        }

        // Verifica se è un organizzatore
        if (session.isOrganizzatore()) {
            Organizzatore organizzatore = OrganizzatoreDAO.selezionaOrganizzatore("username", loginBean.getUsername(), persistence);
            if (organizzatore != null && organizzatore.getPassword().equals(loginBean.getPassword())) {
                session.setIdUtente(organizzatore.getIdUtente());
                session.setNome(organizzatore.getNome());
                return null; // Gli organizzatori non hanno tracker
            }

        } else {
            // Altrimenti è un partecipante
            Partecipante partecipante = PartecipanteDAO.selezionaPartecipante("username", loginBean.getUsername(), persistence);
            if (partecipante != null && partecipante.getPassword().equals(loginBean.getPassword())) {
                session.setIdUtente(partecipante.getIdUtente());
                session.setNome(partecipante.getNome());
                // Recupera il tracker per il partecipante
                Tracker tracker = GestioneTrackerDAO.getTracker(partecipante.getIdUtente(), persistence);
                if (tracker != null) {
                    return GestioneTrackerBeanFactory.createTrackerBeanFromFactory(tracker);
                } else {
                    System.out.println("Nessun tracker trovato per il partecipante con ID: " + partecipante.getIdUtente());
                }
            }
        }

        // Se l'utente non è trovato o le credenziali non sono valide
        mostraMessaggioErrore("Errore di Login", "Credenziali non valide: controlla username e password.");
        return null;
    }
}