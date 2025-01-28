package engclasses.beans;

public class EventoBean {
    private long idEvento;
    private String titolo;
    private String descrizione;
    private String data;
    private String orario;
    private String limitePartecipanti;
    private int iscritti;
    private String link;
    private String nomeOrganizzatore;
    private String cognomeOrganizzatore;
    private boolean stato;
    private String idOrganizzatore;

    // Costruttore vuoto
    public EventoBean() {
    }

    // Getter e Setter
    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }

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

    public String getLimitePartecipanti() {
        return limitePartecipanti;
    }

    public void setLimitePartecipanti(String limitePartecipanti) {
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
    public String getIdOrganizzatore() {
        return idOrganizzatore;
    }
    public void setIdOrganizzatore(String idOrganizzatore) {
        this.idOrganizzatore = idOrganizzatore;
    }

    public boolean isStato() {
        return stato;
    }

    public void setStato(boolean stato) {
        this.stato = stato;
    }

    public boolean getStato() {return stato;}

    public boolean isChiuso() {
        return !stato || iscritti >= Integer.parseInt(limitePartecipanti);
    }

}