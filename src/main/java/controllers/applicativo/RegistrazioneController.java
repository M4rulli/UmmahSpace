package controllers.applicativo;

import controllers.grafico.RegistrazioneGUIController;
import engclasses.beans.RegistrazioneBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.dao.PartecipanteDAO;
import engclasses.dao.OrganizzatoreDAO;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import misc.Session;
import model.Partecipante;
import model.Organizzatore;
import model.Tracker;

import java.util.ArrayList;
import java.util.UUID;

public class RegistrazioneController {

    private final PartecipanteDAO partecipanteDAO;
    private final OrganizzatoreDAO organizzatoreDAO;
    private final Session session;

    public RegistrazioneController(PartecipanteDAO partecipanteDAO, OrganizzatoreDAO organizzatoreDAO, Session session) {
        this.partecipanteDAO = partecipanteDAO;
        this.organizzatoreDAO = organizzatoreDAO;
        this.session = session;
    }

    public boolean registraUtente(RegistrazioneBean bean, boolean persistence) {

        // Validazione dei dati
        if (!validaRegistrazione(bean)) {
            return false; // Interrompi il flusso se ci sono errori
        }

        // Registra il partecipante o l'organizzatore in base alla persistenza
        if (bean.getSeiOrganizzatore()) {
            return registraOrganizzatore(bean, persistence);
        } else {
            return registraPartecipante(bean, persistence);
        }
    }

    private boolean registraPartecipante(RegistrazioneBean bean, boolean persistence) {
        if (partecipanteDAO.selezionaPartecipante(bean.getUsername(), persistence) != null) {
            System.out.println("Username già in uso per un partecipante.");
            return false;
        }

        // Genera un ID univoco per il nuovo partecipante
        String idUtente = UUID.randomUUID().toString();

        Partecipante partecipante = new Partecipante(
                idUtente,  // ID univoco generato
                bean.getNome(),
                bean.getCognome(),
                bean.getUsername(),
                bean.getEmail(),
                bean.getPassword(),
                true
        );

        // Salva l'ID nella sessione
        session.setIdUtente(idUtente);
        session.setCurrentUsername(bean.getUsername());

        // Salva il partecipante nel DAO
        partecipanteDAO.aggiungiPartecipante(partecipante, persistence);

        // Salva il Tracker associato al partecipante nel TrackerDAO
        GestioneTrackerDAO.aggiungiTracker(partecipante.getTrackerSpirituale());

        // Log dell'operazione
        System.out.println("Nuovo partecipante registrato:");
        System.out.println("Tracker creato per l'utente con ID: " + idUtente);
        System.out.println("Username: " + bean.getUsername());
        return true;
    }

    private boolean registraOrganizzatore(RegistrazioneBean bean, boolean persistence) {
        if (organizzatoreDAO.selezionaOrganizzatore(bean.getUsername(), persistence) != null) {
            System.out.println("Username già in uso per un organizzatore.");
            return false;
        }

        String idUtente = UUID.randomUUID().toString();

        Organizzatore organizzatore = new Organizzatore(
                idUtente,
                bean.getNome(),
                bean.getCognome(),
                bean.getUsername(),
                bean.getEmail(),
                bean.getPassword(),
                true,
                new ArrayList<>()
        );

        organizzatoreDAO.aggiungiOrganizzatore(organizzatore, persistence);
        return true;
    }

    public boolean validaRegistrazione(RegistrazioneBean bean) {

        // StringBuilder per accumulare gli errori
        StringBuilder errori = new StringBuilder();

        // Validazione nome
        if (bean.getNome() == null || bean.getNome().trim().isEmpty()) {
            errori.append("Il nome non può essere vuoto.\n");
        }

        // Validazione cognome
        if (bean.getCognome() == null || bean.getCognome().trim().isEmpty()) {
            errori.append("Il cognome non può essere vuoto.\n");
        }

        // Validazione username
        if (bean.getUsername() == null || bean.getUsername().trim().isEmpty()) {
            errori.append("L'username non può essere vuoto.\n");
        }

        // Validazione password
        if (bean.getPassword() == null || bean.getPassword().trim().isEmpty()) {
            errori.append("La password non può essere vuota.\n");
        }

        // Controllo sul matching delle password
        if (bean.getPassword() != null && !bean.getPassword().equals(bean.getConfirmPassword())) {
            errori.append("Le password non corrispondono.\n");
        }
        // Validazione email
        if (bean.getEmail() == null || bean.getEmail().trim().isEmpty()) {
            errori.append("L'email non può essere vuota.\n");
        } else if (!bean.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            errori.append("L'email non è valida.\n");
        }

        // Se ci sono errori, mostra una finestra di dialogo e ritorna false
        if (errori.length() > 0) {
            RegistrazioneGUIController.mostraMessaggioErrore(errori.toString());
            return false;
        }

        // Nessun errore
        return true;
    }

}
