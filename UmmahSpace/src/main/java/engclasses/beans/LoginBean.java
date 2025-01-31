package engclasses.beans;

/**
 * Classe bean per il trasferimento dei dati di login.
 * Contiene i campi username e password con i rispettivi metodi getter e setter.
 */
public class LoginBean {
    private String username;
    private String password;

    // Getter per l'username
    public String getUsername() {
        return username;
    }

    // Setter per l'username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter per la password
    public String getPassword() {
        return password;
    }

    // Setter per la password
    public void setPassword(String password) {
        this.password = password;
    }
}