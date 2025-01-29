package controllers.applicativo;

import engclasses.beans.RegistrazioneBean;
import engclasses.dao.OrganizzatoreDAO;
import engclasses.dao.PartecipanteDAO;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.UtenteNonTrovatoException;
import engclasses.exceptions.ValidazioneProfiloException;
import engclasses.pattern.BeanFactory;
import misc.Session;
import model.Partecipante;
import model.Organizzatore;
import model.Utente;

public class GestisciProfiloController {

    private final Session session;
    private static final String ID_UTENTE = "idUtente";
    private static final String USERNAME = "username";

    public GestisciProfiloController(Session session) {
        this.session = session;
    }

    // Metodo per popolare i campi del profilo utente
    public RegistrazioneBean inizializzaProfilo(String idUtente) throws UtenteNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {

        // Recupero dei dati (controlla se è un partecipante o un organizzatore)
        Utente utente;
        if (session.isOrganizzatore()) {
            utente = OrganizzatoreDAO.selezionaOrganizzatore(ID_UTENTE, idUtente, session.isPersistence());
        } else {
            utente = PartecipanteDAO.selezionaPartecipante(ID_UTENTE, idUtente, session.isPersistence());
        }
        if (utente == null) {
            throw new UtenteNonTrovatoException("L'utente con ID " + idUtente + " non è stato trovato.");
        }
        // Creare una bean per il trasferimento
        return BeanFactory.createRegistrazioneBean(utente);
    }

    // Aggiorna i dati del profilo
    public boolean aggiornaProfilo(RegistrazioneBean updatedBean, String currentPassword, String newPassword, String confirmPassword)
            throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, ValidazioneProfiloException {

        boolean persistence = session.isPersistence();
        StringBuilder errori = new StringBuilder();

        // Recupera l'utente corrente
        Utente utente = recuperaUtenteCorrente();

        // Validazioni generali
        validaNome(updatedBean.getNome(), errori);
        validaCognome(updatedBean.getCognome(), errori);
        validaUsername(updatedBean.getUsername(), utente, errori);
        validaEmail(updatedBean.getEmail(), utente, errori);

        // Validazione della password (se necessaria)
        String nuovaPassword = validaPassword(currentPassword, newPassword, confirmPassword, utente, errori);
        updatedBean.setPassword(nuovaPassword != null ? nuovaPassword : utente.getPassword());

        // Se ci sono errori, lancia un'eccezione personalizzata
        if (!errori.isEmpty()) {
            throw new ValidazioneProfiloException(errori.toString());
        }

        // Aggiorna il profilo nel database
        return aggiornaDatiNelDatabase(updatedBean, utente, persistence);
    }


    // Metodo per recuperare l'utente corrente
    private Utente recuperaUtenteCorrente() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        return session.isOrganizzatore()
                ? OrganizzatoreDAO.selezionaOrganizzatore(ID_UTENTE, session.getIdUtente(), session.isPersistence())
                : PartecipanteDAO.selezionaPartecipante(ID_UTENTE, session.getIdUtente(), session.isPersistence());
    }

    // Validazione Nome
    private void validaNome(String nome, StringBuilder errori) {
        if (nome == null || nome.trim().isEmpty()) {
            errori.append("Il nome non può essere vuoto.\n");
        } else if (!nome.matches("^[a-zA-ZàèéìòùÀÈÉÌÒÙ'\\s]{2,30}$")) {
            errori.append("Il nome deve contenere solo lettere, spazi e apostrofi, con una lunghezza compresa tra 2 e 30 caratteri.\n");
        }
    }

    // Validazione Cognome
    private void validaCognome(String cognome, StringBuilder errori) {
        if (cognome == null || cognome.trim().isEmpty()) {
            errori.append("Il cognome non può essere vuoto.\n");
        } else if (!cognome.matches("^[a-zA-ZàèéìòùÀÈÉÌÒÙ'\\s]{2,30}$")) {
            errori.append("Il cognome deve contenere solo lettere, spazi e apostrofi, con una lunghezza compresa tra 2 e 30 caratteri.\n");
        }
    }

    // Validazione Username
    private void validaUsername(String username, Utente utente, StringBuilder errori) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (username == null || username.trim().isEmpty()) {
            errori.append("L'username non può essere vuoto.\n");
        } else if (!username.equals(utente.getUsername()) && controllaCampo(USERNAME, username)) {
            errori.append("L'username è già in uso.\n");
        }
    }

    // Validazione Email
    private void validaEmail(String email, Utente utente, StringBuilder errori) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (email == null || email.trim().isEmpty()) {
            errori.append("L'email non può essere vuota.\n");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errori.append("L'email non è valida. Deve seguire il formato standard (es. esempio@email.com).\n");
        } else if (!email.equals(utente.getEmail()) && controllaCampo("email", email)) {
            errori.append("L'email è già in uso.\n");
        }
    }

    // Metodo per aggiornare i dati nel database
    private boolean aggiornaDatiNelDatabase(RegistrazioneBean updatedBean, Utente utente, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (utente instanceof Partecipante) {
            Partecipante partecipanteAggiornato = new Partecipante(
                    utente.getIdUtente(),
                    updatedBean.getNome(),
                    updatedBean.getCognome(),
                    updatedBean.getUsername(),
                    updatedBean.getEmail(),
                    updatedBean.getPassword(),
                    utente.isStato()
            );
            return PartecipanteDAO.aggiornaPartecipante(partecipanteAggiornato, persistence);
        } else {
            Organizzatore organizzatoreEsistente = OrganizzatoreDAO.selezionaOrganizzatore(ID_UTENTE, session.getIdUtente(), session.isPersistence());
            Organizzatore organizzatoreAggiornato = new Organizzatore(
                    utente.getIdUtente(),
                    updatedBean.getNome(),
                    updatedBean.getCognome(),
                    updatedBean.getUsername(),
                    updatedBean.getEmail(),
                    updatedBean.getPassword(),
                    true,
                    organizzatoreEsistente.getTitoloDiStudio()
            );
            return OrganizzatoreDAO.aggiornaOrganizzatore(organizzatoreAggiornato, persistence);
        }
    }


    // Modifica la password
    // Validazione e cambio password
    private String validaPassword(String currentPassword, String newPassword, String confirmPassword, Utente utente, StringBuilder errori) {
        if (currentPassword != null && !currentPassword.isEmpty() &&
                newPassword != null && !newPassword.isEmpty() &&
                confirmPassword != null && !confirmPassword.isEmpty()) {

            // Verifica la password attuale
            if (!utente.getPassword().equals(currentPassword)) {
                errori.append("La vecchia password non è corretta.\n");
                return null;
            }

            // Verifica corrispondenza tra nuova password e conferma
            if (!newPassword.equals(confirmPassword)) {
                errori.append("La nuova password e la conferma non coincidono.\n");
                return null;
            }

            // Verifica che la nuova password sia diversa dalla attuale
            if (currentPassword.equals(newPassword)) {
                errori.append("La nuova password non può essere uguale a quella attuale.\n");
                return null;
            }

            return newPassword; // Ritorna la nuova password se tutto è valido
        }
        return null;
    }

    public boolean controllaCampo(String campo, String valore) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Recupera l'utente (Organizzatore o Partecipante) in base al tipo di sessione
        Utente utente = session.isOrganizzatore()
                ? OrganizzatoreDAO.selezionaOrganizzatore(campo, valore, session.isPersistence())
                : PartecipanteDAO.selezionaPartecipante(campo, valore, session.isPersistence());

        // Il campo è disponibile per l’utente attuale
        return utente != null;
    }
}
