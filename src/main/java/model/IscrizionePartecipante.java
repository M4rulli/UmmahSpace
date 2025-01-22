package model;

public class IscrizionePartecipante {

    private String idUtente;
    private String email;
    private long idEvento;
    private String dataIscrizione;


    // Costruttore completo
    public IscrizionePartecipante(String idUtente, String email, long idEvento, String dataIscrizione) {
        this.idUtente = idUtente;
        this.email = email;
        this.idEvento = idEvento;
        this.dataIscrizione = dataIscrizione;
    }

    // Getter e Setter
    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
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
}