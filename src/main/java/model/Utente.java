package model;
import java.util.UUID;

public abstract class Utente {
    protected String nome;
    protected String cognome;
    protected String username;
    protected String email;
    protected String password;
    protected String idUtente;
    protected Boolean stato;

    // Costruttore
    public Utente(String nome, String cognome, String username, String email, String password, String idUtente, Boolean stato) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.email = email;
        this.password = password;
        this.idUtente = idUtente; // Genera un UUID univoco
        this.stato = stato;
    }

    // Getter e Setter
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdUtente() {
       return idUtente;
    }

    public boolean isStato() {
        return stato;
    }
}
