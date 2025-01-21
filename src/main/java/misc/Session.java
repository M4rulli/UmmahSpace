package misc;

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

    private String currentUsername; // Username dell'utente attualmente connesso
    private boolean persistence; // Modalità di persistenza (buffer o database)
    private String idUtente;


    public Session(boolean persistence) {
        this.persistence = persistence;
        this.idUtente = idUtente;
    }

    public boolean isPersistence() {
        return persistence;
    }

    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
    }

    public void setCurrentUsername(String newUsername) {
        this.currentUsername = newUsername;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public String getCurrentUsername() {
        return this.currentUsername; // Assumendo che currentUsername sia una proprietà già definita
    }

    // Setter per l'ID Utente
    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

}
