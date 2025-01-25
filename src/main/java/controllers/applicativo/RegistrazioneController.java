package controllers.applicativo;

import controllers.grafico.RegistrazioneGUIController;
import engclasses.beans.RegistrazioneBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.dao.PartecipanteDAO;
import engclasses.dao.OrganizzatoreDAO;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import misc.Session;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegistrazioneController {

    private final Session session;
    private String idUtente;

    public RegistrazioneController(Session session) {
        this.session = session;
    }

    public boolean registraUtente(RegistrazioneBean bean, boolean persistence) {

        // Genera un ID univoco per l'utente
        this.idUtente = UUID.randomUUID().toString();

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
        System.out.println("Tracker creato per l'utente con ID: " + idUtente);
        return true;
    }

    private boolean registraOrganizzatore(RegistrazioneBean bean, boolean persistence) {

        Organizzatore organizzatore = new Organizzatore(
                idUtente,  // ID univoco generato
                bean.getNome(),
                bean.getCognome(),
                bean.getUsername(),
                bean.getEmail(),
                bean.getPassword(),
                true,
                bean.getTitoloDiStudio()
        );

        // Salva l'ID, l'username, il nome dell'utente e lo stato della persistenza nella sessione
        session.setIdUtente(idUtente);
        session.setCurrentUsername(bean.getUsername());
        session.setNome(bean.getNome());
        
        // Salva l'organizzatore nel DAO
        OrganizzatoreDAO.aggiungiOrganizzatore(organizzatore, persistence);
        return true;
    }

    public boolean validaRegistrazione(RegistrazioneBean bean) {
        // StringBuilder per accumulare gli errori
        StringBuilder errori = new StringBuilder();


        Utente utente;
        if (session.isOrganizzatore()) {
            utente = OrganizzatoreDAO.selezionaOrganizzatore("idUtente", idUtente, session.isPersistence());
        } else {
            utente = PartecipanteDAO.selezionaPartecipante("idUtente", idUtente, session.isPersistence());
        }

        // Validazione nome
        if (bean.getNome() == null || bean.getNome().trim().isEmpty()) {
            errori.append("Il nome non può essere vuoto.\n");
        } else if (!bean.getNome().matches("^[a-zA-ZàèéìòùÀÈÉÌÒÙ'\\s]{2,30}$")) {
            errori.append("Il nome deve contenere solo lettere, spazi e apostrofi, con una lunghezza compresa tra 2 e 30 caratteri.\n");
        }

        // Validazione cognome
        if (bean.getCognome() == null || bean.getCognome().trim().isEmpty()) {
            errori.append("Il cognome non può essere vuoto.\n");
        } else if (!bean.getCognome().matches("^[a-zA-ZàèéìòùÀÈÉÌÒÙ'\\s]{2,30}$")) {
            errori.append("Il cognome deve contenere solo lettere, spazi e apostrofi, con una lunghezza compresa tra 2 e 30 caratteri.\n");
        }

        // Validazione username
        if (bean.getUsername() == null || bean.getUsername().trim().isEmpty()) {
            errori.append("L'username non può essere vuoto.\n");
        } else {
            // Verifica se l'username esiste già
            if (controllaCampo("username", bean.getUsername())) {
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
            if ((controllaCampo("email", bean.getEmail()))) {
                errori.append("L'email è già in uso.\n");
            }
        }

        // Validazione del titolo di studio
        if (session.isOrganizzatore()) {
            String titoloDiStudio = bean.getTitoloDiStudio();
            if (titoloDiStudio == null || titoloDiStudio.trim().isEmpty()) {
                errori.append("Il titolo di studio è obbligatorio per registrarti come organizzatore.\n");
            } else if (!titoloDiStudio.matches("^[a-zA-Z\\s]{2,50}$")) { // Solo lettere e spazi, tra 2 e 50 caratteri
                errori.append("Il titolo di studio deve contenere solo lettere e spazi, con una lunghezza compresa tra 2 e 50 caratteri.\n");
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

    public boolean controllaCampo(String campo, String valore) {
        // Recupera l'utente (Organizzatore o Partecipante) in base al tipo di sessione
        Utente utente = session.isOrganizzatore()
                ? OrganizzatoreDAO.selezionaOrganizzatore(campo, valore, session.isPersistence())
                : PartecipanteDAO.selezionaPartecipante(campo, valore, session.isPersistence());

        // Se l'utente non esiste, il campo è disponibile
        return utente != null;
    }

}
