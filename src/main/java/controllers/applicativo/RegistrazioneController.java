package controllers.applicativo;

import engclasses.beans.RegistrazioneBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.dao.PartecipanteDAO;
import engclasses.dao.OrganizzatoreDAO;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.RegistrazioneFallitaException;
import misc.Session;
import model.*;
import java.util.UUID;

import static misc.MessageUtils.mostraMessaggioAttenzione;

public class RegistrazioneController {

    private final Session session;
    private String idUtente;

    public RegistrazioneController(Session session) {
        this.session = session;
    }

    public boolean registraUtente(RegistrazioneBean bean, boolean persistence) throws RegistrazioneFallitaException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {

        // Genera un ID univoco per l'utente
        this.idUtente = UUID.randomUUID().toString();

        // Validazione dei dati
        if (!validaRegistrazione(bean)) {
            return false; // Interrompi il flusso se ci sono errori
        }

        // Registra l'organizzatore o il partecipante
        boolean success;
        if (session.isOrganizzatore()) {
            success = registraOrganizzatore(bean, persistence); // Solo organizzatori
        } else {
            success = registraPartecipante(bean, persistence); // Solo partecipanti
        }

        // Se la registrazione non è andata a buon fine, lancia un'eccezione
        if (!success) {
            throw new RegistrazioneFallitaException("Errore nella registrazione: si è verificato un problema durante il salvataggio.");
        }
        return true;
    }

    private boolean registraPartecipante(RegistrazioneBean bean, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {

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
        return true;
    }

    private boolean registraOrganizzatore(RegistrazioneBean bean, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {

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
        session.setCognomeOrganizzatore(bean.getCognome());
        session.setNomeOrganizzatore(bean.getNome());
        
        // Salva l'organizzatore nel DAO
        OrganizzatoreDAO.aggiungiOrganizzatore(organizzatore, persistence);
        return true;
    }

    public boolean validaRegistrazione(RegistrazioneBean bean) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        StringBuilder errori = new StringBuilder();

        // Recupera utente corrente
        recuperaUtente();

        // Validazione campi
        validaNome(bean.getNome(), errori);
        validaCognome(bean.getCognome(), errori);
        validaUsername(bean.getUsername(), errori);
        validaPassword(bean.getPassword(), bean.getConfirmPassword(), errori);
        validaEmail(bean.getEmail(), errori);

        if (session.isOrganizzatore()) {
            validaTitoloDiStudio(bean.getTitoloDiStudio(), errori);
        }

        // Se ci sono errori, mostra un messaggio e ritorna false
        if (!errori.isEmpty()) {
            mostraMessaggioAttenzione("Risolvi i seguenti errori:", errori.toString());
            return false;
        }

        return true;
    }

    // Metodo per recuperare l'utente corrente
    private void recuperaUtente() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (session.isOrganizzatore()) {
            OrganizzatoreDAO.selezionaOrganizzatore("idUtente", idUtente, session.isPersistence());
        } else {
            PartecipanteDAO.selezionaPartecipante("idUtente", idUtente, session.isPersistence());
        }
    }

    // Metodo per validare il nome
    private void validaNome(String nome, StringBuilder errori) {
        if (nome == null || nome.trim().isEmpty()) {
            errori.append("Il nome non può essere vuoto.\n");
        } else if (!nome.matches("^[a-zA-ZàèéìòùÀÈÉÌÒÙ'\\s]{2,30}$")) {
            errori.append("Il nome deve contenere solo lettere, spazi e apostrofi (2-30 caratteri).\n");
        }
    }

    // Metodo per validare il cognome
    private void validaCognome(String cognome, StringBuilder errori) {
        if (cognome == null || cognome.trim().isEmpty()) {
            errori.append("Il cognome non può essere vuoto.\n");
        } else if (!cognome.matches("^[a-zA-ZàèéìòùÀÈÉÌÒÙ'\\s]{2,30}$")) {
            errori.append("Il cognome deve contenere solo lettere, spazi e apostrofi (2-30 caratteri).\n");
        }
    }

    // Metodo per validare l'username
    private void validaUsername(String username, StringBuilder errori) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (username == null || username.trim().isEmpty()) {
            errori.append("L'username non può essere vuoto.\n");
        } else if (controllaCampo("username", username)) {
            errori.append("L'username è già in uso.\n");
        }
    }

    //  Metodo per validare la password
    private void validaPassword(String password, String confirmPassword, StringBuilder errori) {
        if (password == null || password.trim().isEmpty()) {
            errori.append("La password non può essere vuota.\n");
        }
        if (password != null && !password.equals(confirmPassword)) {
            errori.append("Le password non corrispondono.\n");
        }
    }

    // Metodo per validare l'email
    private void validaEmail(String email, StringBuilder errori) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (email == null || email.trim().isEmpty()) {
            errori.append("L'email non può essere vuota.\n");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            errori.append("L'email non è valida.\n");
        } else if (controllaCampo("email", email)) {
            errori.append("L'email è già in uso.\n");
        }
    }

    // Metodo per validare il titolo di studio (solo per organizzatori)
    private void validaTitoloDiStudio(String titoloDiStudio, StringBuilder errori) {
        if (titoloDiStudio == null || titoloDiStudio.trim().isEmpty()) {
            errori.append("Il titolo di studio è obbligatorio per registrarti come organizzatore.\n");
        } else if (!titoloDiStudio.matches("^[a-zA-Z\\s]{2,50}$")) {
            errori.append("Il titolo di studio deve contenere solo lettere e spazi (2-50 caratteri).\n");
        }
    }

    public boolean controllaCampo(String campo, String valore) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Recupera l'utente (Organizzatore o Partecipante) in base al tipo di sessione
        Utente utente = session.isOrganizzatore()
                ? OrganizzatoreDAO.selezionaOrganizzatore(campo, valore, session.isPersistence())
                : PartecipanteDAO.selezionaPartecipante(campo, valore, session.isPersistence());

        // Se l'utente non esiste, il campo è disponibile
        return utente != null;
    }

}
