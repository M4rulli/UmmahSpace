package model;

public class Tracker {
    // Attributi privati
    private int letturaCorano;
    private int giorniDigiuno;
    private int preghiereComplete;
    private long idTracker;
    private long idUtente;

    // Costruttore vuoto
    public Tracker() {
    }

    // Costruttore parametrico
    public Tracker(int letturaCorano, int giorniDigiuno, int preghiereComplete, long idTracker, long idUtente) {
        this.letturaCorano = letturaCorano;
        this.giorniDigiuno = giorniDigiuno;
        this.preghiereComplete = preghiereComplete;
        this.idTracker = idTracker;
        this.idUtente = idUtente;
    }

    // Getter e Setter
    public int getLetturaCorano() {
        return letturaCorano;
    }

    public void setLetturaCorano(int letturaCorano) {
        this.letturaCorano = letturaCorano;
    }

    public int getGiorniDigiuno() {
        return giorniDigiuno;
    }

    public void setGiorniDigiuno(int giorniDigiuno) {
        this.giorniDigiuno = giorniDigiuno;
    }

    public int getPreghiereComplete() {
        return preghiereComplete;
    }

    public void setPreghiereComplete(int preghiereComplete) {
        this.preghiereComplete = preghiereComplete;
    }

    public long getIdTracker() {
        return idTracker;
    }

    public void setIdTracker(long idTracker) {
        this.idTracker = idTracker;
    }

    public long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(long idUtente) {
        this.idUtente = idUtente;
    }

    // Metodo toString per rappresentazione leggibile
    @Override
    public String toString() {
        return "Tracker{" +
                "letturaCorano=" + letturaCorano +
                ", giorniDigiuno=" + giorniDigiuno +
                ", preghiereComplete=" + preghiereComplete +
                ", idTracker=" + idTracker +
                ", idUtente=" + idUtente +
                '}';
    }
}
