package misc;

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

    public String getCurrentUsername() {
        return this.currentUsername;
    }

    public String getIdUtente() {
        return idUtente;
    }

    // Setter per l'ID Utente
    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

}
