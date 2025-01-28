package engclasses.beans;

/**
 * Classe Bean per rappresentare una partecipazione a un evento.
 */
public class PartecipazioneBean {

    private String idUtente;         // ID univoco dell'utente
    private String nome;             // Nome del partecipante
    private String cognome;          // Cognome del partecipante
    private String username;         // Username del partecipante
    private String email;            // Email del partecipante
    private long idEvento;           // ID univoco dell'evento
    private String dataIscrizione;   // Data di iscrizione all'evento

    // Costruttore vuoto
    public PartecipazioneBean() {
    }

    // Getter e Setter
    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
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

    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }

    public String getDataIscrizione() {
        return dataIscrizione;
    }

    public void setDataIscrizione(String dataIscrizione) {
        this.dataIscrizione = dataIscrizione;
    }

    @Override
    public String toString() {
        return "PartecipazioneBean{" +
                "idUtente='" + idUtente + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", idEvento=" + idEvento +
                ", dataIscrizione='" + dataIscrizione + '\'' +
                '}';
    }
}