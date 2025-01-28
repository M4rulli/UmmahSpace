package controllers.applicativo;

import engclasses.beans.RegistrazioneBean;
import engclasses.dao.OrganizzatoreDAO;
import engclasses.dao.PartecipanteDAO;
import javafx.scene.control.Alert;
import misc.Session;
import model.Partecipante;
import model.Organizzatore;
import model.Utente;

import static misc.MessageUtils.mostraMessaggioErrore;

public class GestisciProfiloController {

    private final Session session;

    public GestisciProfiloController(Session session) {
        this.session = session;
    }

    // Metodo per popolare i campi del profilo utente
    public RegistrazioneBean inizializzaProfilo(String idUtente) {

        // Recupero dei dati (controlla se è un partecipante o un organizzatore)
        Utente utente;
        if (session.isOrganizzatore()) {
            utente = OrganizzatoreDAO.selezionaOrganizzatore("idUtente", idUtente, session.isPersistence());
        } else {
            utente = PartecipanteDAO.selezionaPartecipante("idUtente", idUtente, session.isPersistence());
        }

        // Creare una bean per il trasferimento
        RegistrazioneBean bean = new RegistrazioneBean();
        if (utente instanceof Partecipante partecipante) {
            bean.setNome(partecipante.getNome());
            bean.setCognome(partecipante.getCognome());
            bean.setUsername(partecipante.getUsername());
            bean.setEmail(partecipante.getEmail());
        } else if (utente != null) {
            Organizzatore organizzatore = (Organizzatore) utente;
            bean.setNome(organizzatore.getNome());
            bean.setCognome(organizzatore.getCognome());
            bean.setUsername(organizzatore.getUsername());
            bean.setEmail(organizzatore.getEmail());
        }
        return bean;
    }

    // Aggiorna i dati del profilo
    public boolean aggiornaProfilo(RegistrazioneBean updatedBean, String currentPassword, String newPassword, String confirmPassword) {
        boolean persistence = session.isPersistence();
        StringBuilder errori = new StringBuilder();

        // Recupera l'utente corrente
        Utente utente;
        if (session.isOrganizzatore()) {
            utente = OrganizzatoreDAO.selezionaOrganizzatore("idUtente", session.getIdUtente(), session.isPersistence());
        } else {
            utente = PartecipanteDAO.selezionaPartecipante("idUtente", session.getIdUtente(), session.isPersistence());
        }

        // Validazione nome
        if (updatedBean.getNome() == null || updatedBean.getNome().trim().isEmpty()) {
            errori.append("Il nome non può essere vuoto.\n");
        } else if (!updatedBean.getNome().matches("^[a-zA-ZàèéìòùÀÈÉÌÒÙ'\\s]{2,30}$")) {
            errori.append("Il nome deve contenere solo lettere, spazi e apostrofi, con una lunghezza compresa tra 2 e 30 caratteri.\n");
        }

        // Validazione cognome
        if (updatedBean.getCognome() == null || updatedBean.getCognome().trim().isEmpty()) {
            errori.append("Il cognome non può essere vuoto.\n");
        } else if (!updatedBean.getCognome().matches("^[a-zA-ZàèéìòùÀÈÉÌÒÙ'\\s]{2,30}$")) {
            errori.append("Il cognome deve contenere solo lettere, spazi e apostrofi, con una lunghezza compresa tra 2 e 30 caratteri.\n");
        }

        // Validazione username
        if (updatedBean.getUsername() == null || updatedBean.getUsername().trim().isEmpty()) {
            errori.append("L'username non può essere vuoto.\n");
        } else if (!updatedBean.getUsername().equals(utente.getUsername()) && !controllaCampo("username", updatedBean.getUsername())) {
            errori.append("L'username è già in uso.\n");
        }

        // Validazione email
        if (updatedBean.getEmail() == null || updatedBean.getEmail().trim().isEmpty()) {
            errori.append("L'email non può essere vuota.\n");
        } else if (!updatedBean.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errori.append("L'email non è valida. Deve seguire il formato standard (es. esempio@email.com).\n");
        } else if (!updatedBean.getEmail().equals(utente.getEmail()) && !controllaCampo("email", updatedBean.getEmail())) {
            errori.append("L'email è già in uso.\n");
        }

        // Cambio password se necessario
        if (currentPassword != null && !currentPassword.isEmpty() &&
                newPassword != null && !newPassword.isEmpty() &&
                confirmPassword != null && !confirmPassword.isEmpty()) {
            // Verifica che la nuova password sia valida
            String newPasswordValue = changePassword(currentPassword, newPassword, confirmPassword, utente.getUsername());
            if (newPasswordValue == null) {
                return false; // Fallisce se la nuova password non è valida
            }
            updatedBean.setPassword(newPasswordValue); // Imposta la nuova password
        } else {
            // Mantieni la password attuale se non è stata modificata
            updatedBean.setPassword(utente.getPassword());
        }

        // Se ci sono errori, mostra una finestra di dialogo e ritorna false
        if (!errori.isEmpty()) {
            mostraMessaggioErrore("Errore", errori.toString());
            return false;
        }

        // Crea un nuovo modello con i dati aggiornati
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
            if (!PartecipanteDAO.aggiornaPartecipante(partecipanteAggiornato, persistence)) {
                System.out.println("Errore durante l'aggiornamento del partecipante.");
                return false;
            }

        } else {
            Organizzatore organizzatoreEsistente = OrganizzatoreDAO.selezionaOrganizzatore("idUtente", session.getIdUtente(), session.isPersistence());
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

            if (!OrganizzatoreDAO.aggiornaOrganizzatore(organizzatoreAggiornato, persistence)) {
                System.out.println("Errore durante l'aggiornamento dell'organizzatore.");
                return false;
            }
        }
        return true;
    }

    // Modifica la password
    public String changePassword(String currentPassword, String newPassword, String confirmPassword, String username) {

        Utente utente = session.isOrganizzatore()
                ? OrganizzatoreDAO.selezionaOrganizzatore("username", username, session.isPersistence())
                : PartecipanteDAO.selezionaPartecipante("username", username, session.isPersistence());


             // Verifica la password attuale
            if (!utente.getPassword().equals(currentPassword)) {
                mostraMessaggioErrore("Errore", "La vecchia password non è corretta.");
                return null;
            }

            // Verifica corrispondenza tra nuova password e conferma
            if (!newPassword.equals(confirmPassword)) {
                mostraMessaggioErrore("Errore", "La nuova password e la conferma non coincidono.");
                return null;
            }

            // Verifica che la nuova password sia diversa da quella attuale
            if (currentPassword.equals(newPassword)) {
                mostraMessaggioErrore("Errore", "La nuova password non può essere uguale a quella attuale.");
                return null;
            }

            return newPassword; // Ritorna la nuova password se tutto è valido
        }

    public boolean controllaCampo(String campo, String valore) {
        // Recupera l'utente (Organizzatore o Partecipante) in base al tipo di sessione
        Utente utente = session.isOrganizzatore()
                ? OrganizzatoreDAO.selezionaOrganizzatore(campo, valore, session.isPersistence())
                : PartecipanteDAO.selezionaPartecipante(campo, valore, session.isPersistence());

        // Il campo è disponibile per l’utente attuale
        return utente == null;
    }
}
