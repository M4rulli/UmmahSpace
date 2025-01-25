package controllers.applicativo;

import controllers.grafico.RegistrazioneGUIController;
import engclasses.beans.RegistrazioneBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.dao.PartecipanteDAO;
import engclasses.dao.OrganizzatoreDAO;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import misc.Session;
import model.Evento;
import model.Partecipante;
import model.Organizzatore;
import model.Tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegistrazioneController {

    private final Session session;

    public RegistrazioneController(Session session) {
        this.session = session;
    }

    public boolean registraUtente(RegistrazioneBean bean, boolean persistence) {

        // Validazione dei dati
        if (!validaRegistrazione(bean)) {
            return false; // Interrompi il flusso se ci sono errori
        }

        // Registra l'organizzatore o il partecipante
        if (session.isOrganizzatore()) {
            return registraOrganizzatore(bean, persistence); // Solo organizzatori
        } else {
            return registraPartecipante(bean, persistence); // Solo partecipanti
        }
    }

    private boolean registraPartecipante(RegistrazioneBean bean, boolean persistence) {

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

        // Salva l'ID, l'username, il nome dell'utente e lo stato della persistenza nella sessione
        session.setIdUtente(idUtente);
        session.setCurrentUsername(bean.getUsername());
        session.setNome(bean.getNome());

        // Salva il partecipante nel DAO
        PartecipanteDAO.aggiungiPartecipante(partecipante, persistence);

        // Salva il Tracker associato al partecipante nel TrackerDAO
        GestioneTrackerDAO.saveOrUpdateTracker(partecipante.getTrackerSpirituale(), session.isPersistence());

        // Log dell'operazione
        System.out.println("Nuovo partecipante registrato con username: " + bean.getUsername());
        System.out.println("Tracker creato per l'utente con ID: " + idUtente);
        return true;
    }

    // Da inserire
    private boolean registraOrganizzatore(RegistrazioneBean bean, boolean persistence) {

        // Genera un ID univoco per il nuovo organizzatore
        String idUtente = UUID.randomUUID().toString();

        Organizzatore organizzatore = new Organizzatore(
                idUtente,  // ID univoco generato
                bean.getNome(),
                bean.getCognome(),
                bean.getUsername(),
                bean.getEmail(),
                bean.getPassword(),
                true     // Indica che è un organizzatore
        );

        // Salva l'ID, l'username, il nome dell'utente e lo stato della persistenza nella sessione
        session.setIdUtente(idUtente);
        session.setCurrentUsername(bean.getUsername());
        session.setNome(bean.getNome());
        
        // Salva l'organizzatore nel DAO
        OrganizzatoreDAO.aggiungiOrganizzatore(organizzatore, persistence);

        // Log dell'operazione
        System.out.println("Nuovo organizzatore registrato con username: " + bean.getUsername());
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
        } else {
            // Verifica se l'username esiste già
            if (PartecipanteDAO.selezionaPartecipante("username", bean.getUsername(), session.isPersistence()) != null) {
                errori.append("L'username è già in uso.\n");
            }
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
        } else {
            // Verifica se l'email esiste già
            if (PartecipanteDAO.selezionaPartecipante("email", bean.getEmail(), session.isPersistence()) != null) {
                errori.append("L'email è già in uso.\n");
            }
        }

        // Se ci sono errori, mostra una finestra di dialogo e ritorna false
        if (!errori.isEmpty()) {
            RegistrazioneGUIController.mostraMessaggioErrore(errori.toString());
            return false;
        }
        // Nessun errore
        return true;
    }

}
