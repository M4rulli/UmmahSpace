package model;

public class Partecipazione {
    private String idUtente;
    private long idEvento;
    private String nome;
    private String cognome;
    private String username;
    private String email;
    private final String dataIscrizione;

    // Costruttore
    public Partecipazione(String idUtente, long idEvento, String nome, String cognome, String username, String email, String dataIscrizione) {
        this.idUtente = idUtente;
        this.idEvento = idEvento;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.email = email;
        this.dataIscrizione = dataIscrizione;
    }

    // Getter e Setter
    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }

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

    public String getDataIscrizione() {
        return dataIscrizione;
    }

}