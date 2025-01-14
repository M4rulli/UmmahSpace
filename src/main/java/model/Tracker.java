package model;

public class Tracker {

    private int letturaCorano; // Numero di pagine lette del Corano
    private int giorniDigiuno; // Numero di giorni di digiuno
    private int preghiereComplete; // Numero di preghiere completate
    private final String idUtente; // ID dell'utente associato al tracker
    private int goal;

    // Costruttore completo
    public Tracker(int letturaCorano, int giorniDigiuno, int preghiereComplete, String idUtente, int goal) {
        this.letturaCorano = letturaCorano;
        this.giorniDigiuno = giorniDigiuno;
        this.preghiereComplete = preghiereComplete;
        this.idUtente = idUtente;
        this.goal = goal;
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

    public String getIdUtente() {
        return idUtente;
    }

    public int getGoal() {
        return this.goal; // Assumendo che il goal sia una proprietà del Tracker
    }

    public void setGoal(int goal) {
        if (goal <= 0) {
            throw new IllegalArgumentException("L'obiettivo giornaliero deve essere maggiore di zero.");
        }
        this.goal = goal;
    }

    // Metodo toString per debug
    @Override
    public String toString() {
        return "Tracker{" +
                "letturaCorano=" + letturaCorano +
                ", giorniDigiuno=" + giorniDigiuno +
                ", preghiereComplete=" + preghiereComplete +
                ", idUtente=" + idUtente +
                '}';
    }

}
