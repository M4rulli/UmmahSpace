package controllers.applicativo;

import engclasses.beans.RegistrazioneBean;
import engclasses.dao.PartecipanteDAO;
import misc.Session;
import model.Partecipante;

public class GestisciProfiloPartecipanteController {

    private final PartecipanteDAO partecipanteDAO;
    private final Session session;

    public GestisciProfiloPartecipanteController(PartecipanteDAO partecipanteDAO, Session session) {
        this.partecipanteDAO = partecipanteDAO;
        this.session = session;
    }

    // Recupera i dati del profilo del partecipante
    public RegistrazioneBean getProfileData(String username) {
        boolean persistence = session.isPersistence(); // Determina se usare buffer o DB

        Partecipante partecipante = partecipanteDAO.selezionaPartecipante(username, persistence);
        if (partecipante == null) {
            System.out.println("Nessun profilo trovato per l'utente: " + username);
            return null;
        }

        return convertToBean(partecipante);
    }

    // Aggiorna i dati del profilo, incluso l'username
    public boolean updateProfileData(RegistrazioneBean updatedBean, String oldUsername) {
        boolean persistence = session.isPersistence();

        // Verifica se il partecipante con il vecchio username esiste
        Partecipante partecipante = partecipanteDAO.selezionaPartecipante(oldUsername, persistence);
        if (partecipante == null) {
            System.out.println("Partecipante non trovato con username: " + oldUsername);
            return false;
        }

        // Verifica se l'username è cambiato
        if (!oldUsername.equals(updatedBean.getUsername())) {
            // Controlla se il nuovo username è già utilizzato
            if (partecipanteDAO.selezionaPartecipante(updatedBean.getUsername(), persistence) != null) {
                System.out.println("Errore: Username già in uso: " + updatedBean.getUsername());
                return false;
            }

            // Aggiorna l'username
            if (!partecipanteDAO.updatePartecipanteUsername(oldUsername, updatedBean.getUsername())) {
                System.out.println("Errore durante l'aggiornamento dell'username.");
                return false;
            }

            // Aggiorna il currentUsername nella sessione
            session.setCurrentUsername(updatedBean.getUsername());
            System.out.println("Aggiornamento del buffer completato. Nuovo username: " + updatedBean.getUsername());
        }

        // Aggiorna gli altri dati del partecipante
        partecipante.setNome(updatedBean.getNome());
        partecipante.setCognome(updatedBean.getCognome());
        partecipante.setEmail(updatedBean.getEmail());

        partecipanteDAO.aggiungiPartecipante(partecipante, persistence);

        System.out.println("Profilo aggiornato con successo.");
        return true;
    }


    // Modifica la password del partecipante
    public boolean changePassword(String currentPassword, String newPassword, String confirmPassword, String username) {
        boolean persistence = session.isPersistence();

        Partecipante partecipante = partecipanteDAO.selezionaPartecipante(username, persistence);
        if (partecipante == null || !partecipante.getPassword().equals(currentPassword)) {
            System.out.println("Password attuale non corretta o partecipante non trovato.");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("La nuova password e la conferma non coincidono.");
            return false;
        }

        partecipante.setPassword(newPassword);
        partecipanteDAO.aggiungiPartecipante(partecipante, persistence);

        System.out.println("Password aggiornata con successo.");
        return true;
    }

    // Metodo di supporto per convertire un Partecipante in un RegistrazioneBean
    private RegistrazioneBean convertToBean(Partecipante partecipante) {
        RegistrazioneBean bean = new RegistrazioneBean();
        bean.setNome(partecipante.getNome());
        bean.setCognome(partecipante.getCognome());
        bean.setUsername(partecipante.getUsername());
        bean.setEmail(partecipante.getEmail());
        bean.setPassword(partecipante.getPassword());
        return bean;
    }
}
