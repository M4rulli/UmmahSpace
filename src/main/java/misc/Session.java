package misc;

public class Session {

    private String currentUsername; // Username dell'utente attualmente connesso
    private boolean persistence; // Modalità di persistenza (buffer o database)

    public Session(boolean persistence) {
        this.persistence = persistence;
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

}
