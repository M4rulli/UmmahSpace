package controllers.applicativo;

import engclasses.beans.RegistrazioneBean;
import engclasses.dao.PartecipanteDAO;
import engclasses.dao.OrganizzatoreDAO;
import model.Partecipante;
import model.Organizzatore;
import misc.ValidationResult;

import java.util.ArrayList;

public class RegistrazioneController {

    private final PartecipanteDAO partecipanteDAO;
    private final OrganizzatoreDAO organizzatoreDAO;

    public RegistrazioneController(PartecipanteDAO partecipanteDAO, OrganizzatoreDAO organizzatoreDAO) {
        this.partecipanteDAO = partecipanteDAO;
        this.organizzatoreDAO = organizzatoreDAO;
    }

    public boolean registraUtente(RegistrazioneBean bean, boolean persistence) {
        // Valida i dati
        if (!isEmailValid(bean.getEmail())) {
            System.out.println("Email non valida.");
            return false;
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

        Partecipante partecipante = new Partecipante(
                bean.getNome(),
                bean.getCognome(),
                bean.getUsername(),
                bean.getEmail(),
                bean.getPassword(),
                System.currentTimeMillis(),
                true
        );

        partecipanteDAO.aggiungiPartecipante(partecipante, persistence);
        return true;
    }

    private boolean registraOrganizzatore(RegistrazioneBean bean, boolean persistence) {
        if (organizzatoreDAO.selezionaOrganizzatore(bean.getUsername(), persistence) != null) {
            System.out.println("Username già in uso per un organizzatore.");
            return false;
        }

        Organizzatore organizzatore = new Organizzatore(
                bean.getNome(),
                bean.getCognome(),
                bean.getUsername(),
                bean.getEmail(),
                bean.getPassword(),
                System.currentTimeMillis(),
                true,
                new ArrayList<>()
        );

        organizzatoreDAO.aggiungiOrganizzatore(organizzatore, persistence);
        return true;
    }

    public ValidationResult validateRegistrationData(RegistrazioneBean bean, String confirmPassword) {
        ValidationResult result = new ValidationResult();

        // Controllo campi obbligatori
        if (bean.getNome() == null || bean.getNome().isEmpty()) {
            result.addError("Il nome non può essere vuoto.");
        }
        if (bean.getCognome() == null || bean.getCognome().isEmpty()) {
            result.addError("Il cognome non può essere vuoto.");
        }
        if (bean.getUsername() == null || bean.getUsername().isEmpty()) {
            result.addError("Lo username non può essere vuoto.");
        }
        if (bean.getEmail() == null || bean.getEmail().isEmpty()) {
            result.addError("L'email non può essere vuota.");
        }
        if (bean.getPassword() == null || bean.getPassword().isEmpty()) {
            result.addError("La password non può essere vuota.");
        }
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            result.addError("La conferma della password non può essere vuota.");
        }

        // Validazione email
        if (!isEmailValid(bean.getEmail())) {
            result.addError("Email non valida. Inserisci un'email corretta.");
        }

        // Verifica password
        if (!doPasswordsMatch(bean.getPassword(), confirmPassword)) {
            result.addError("Le password non coincidono. Riprova.");
        }

        // Controlla disponibilità dello username
        if (!isUsernameAvailable(bean.getUsername())) {
            result.addError("Lo username è già in uso. Scegli un altro username.");
        }

        return result;
    }

    public boolean isEmailValid(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }

    public boolean doPasswordsMatch(String password, String confirmPassword) {
        return password != null && confirmPassword != null && password.equals(confirmPassword);
    }

    public boolean isUsernameAvailable(String username) {
        return partecipanteDAO.selezionaPartecipante(username, false) == null &&
                organizzatoreDAO.selezionaOrganizzatore(username, false) == null;
    }
    public PartecipanteDAO getPartecipanteDAO() {
        return partecipanteDAO;
    }
}
