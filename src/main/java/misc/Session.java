package misc;

import engclasses.beans.EventoBean;
import engclasses.beans.GestioneTrackerBean;

import java.util.List;

/**
 * La classe Session rappresenta lo stato corrente di una sessione utente
 * all'interno dell'applicazione. Fornisce le informazioni chiave sull'utente
 * connesso, come il nome utente, l'ID dell'utente, e la modalità di
 * persistenza attualmente selezionata (buffer o database).

 * Questa classe viene utilizzata per tracciare i dettagli della sessione
 * attiva, semplificando la gestione e il recupero delle informazioni
 * relative all'utente.
 */

public class Session {

    private String nome;
    private String currentUsername; // Username dell'utente attualmente connesso
    private boolean persistence; // Modalità di persistenza (buffer o database)
    private String idUtente;
    private GestioneTrackerBean trackerBean; // Oggetto per gestire il tracker
    private boolean isOrganizzatore;
    private String nomeOrganizzatore;
    private String cognomeOrganizzatore;
    private long idEvento;
    private List<EventoBean> eventiDelGiorno;

    public Session(boolean persistence) {
        this.persistence = persistence;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {return nome;}

    public boolean isPersistence() {
        return persistence;
    }

    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
    }

    public void setCurrentUsername(String newUsername) {
        this.currentUsername = newUsername;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public String getCurrentUsername() {
        return this.currentUsername;
    }

    public void setTracker(GestioneTrackerBean trackerBean) {
        this.trackerBean = trackerBean;
    }

    public GestioneTrackerBean getTracker() {
        return trackerBean;
    }

    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }

    public boolean isOrganizzatore() {
        return isOrganizzatore;
    }

    public String getNomeOrganizzatore() { return nomeOrganizzatore; }

    public void setNomeOrganizzatore(String nomeOrganizzatore) { this.nomeOrganizzatore = nomeOrganizzatore; }

    public String getCognomeOrganizzatore() { return cognomeOrganizzatore; }

    public void setCognomeOrganizzatore (String cognomeOrganizzatore) { this.cognomeOrganizzatore = cognomeOrganizzatore;  }

    public void setIsOrganizzatore(boolean organizzatore) {
        isOrganizzatore = organizzatore;
    }

    public List<EventoBean> getEventiDelGiorno() {
        return eventiDelGiorno;
    }

    public void setEventiDelGiorno(List<EventoBean> eventiDelGiorno) {
        this.eventiDelGiorno = eventiDelGiorno;
    }
}
