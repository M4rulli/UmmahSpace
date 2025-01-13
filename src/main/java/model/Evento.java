package model;


public class Evento {
    // Attributi privati
    private String titolo;
    private String descrizione;
    private String data;
    private String orario;
    private int limitePartecipanti;
    private int iscritti;
    private String link;
    private String nomeOrganizzatore;
    private String cognomeOrganizzatore;
    private boolean stato;
    private long idEvento;

    // Costruttore vuoto (necessario per alcuni framework e inizializzazioni manuali)
    public Evento() {
    }

    // Costruttore con tutti i campi
    public Evento(String titolo, String descrizione, String data, String orario, int limitePartecipanti, int iscritti,
                  String link, String nomeOrganizzatore, String cognomeOrganizzatore, boolean stato, long idEvento) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.data = data;
        this.orario = orario;
        this.limitePartecipanti = limitePartecipanti;
        this.iscritti = iscritti;
        this.link = link;
        this.nomeOrganizzatore = nomeOrganizzatore;
        this.cognomeOrganizzatore = cognomeOrganizzatore;
        this.stato = stato;
        this.idEvento = idEvento;
    }

    // Getter e Setter
    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOrario() {
        return orario;
    }

    public void setOrario(String orario) {
        this.orario = orario;
    }

    public int getLimitePartecipanti() {
        return limitePartecipanti;
    }

    public void setLimitePartecipanti(int limitePartecipanti) {
        this.limitePartecipanti = limitePartecipanti;
    }

    public int getIscritti() {
        return iscritti;
    }

    public void setIscritti(int iscritti) {
        this.iscritti = iscritti;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getNomeOrganizzatore() {
        return nomeOrganizzatore;
    }

    public void setNomeOrganizzatore(String nomeOrganizzatore) {
        this.nomeOrganizzatore = nomeOrganizzatore;
    }

    public String getCognomeOrganizzatore() {
        return cognomeOrganizzatore;
    }

    public void setCognomeOrganizzatore(String cognomeOrganizzatore) {
        this.cognomeOrganizzatore = cognomeOrganizzatore;
    }

    public boolean getStato() {
        return stato;
    }

    public void setStato(boolean stato) {
        this.stato = stato;
    }

    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }

    // Metodo toString per rappresentare l'entità in formato leggibile
    @Override
    public String toString() {
        return "Evento{" +
                "titolo='" + titolo + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", data='" + data + '\'' +
                ", orario='" + orario + '\'' +
                ", limitePartecipanti=" + limitePartecipanti +
                ", iscritti=" + iscritti +
                ", link='" + link + '\'' +
                ", nomeOrganizzatore='" + nomeOrganizzatore + '\'' +
                ", cognomeOrganizzatore='" + cognomeOrganizzatore + '\'' +
                ", stato=" + stato +
                ", idEvento=" + idEvento +
                '}';
    }
}
