package engclasses.beans;

public class GestioneTrackerBean {

    private int letturaCorano; // Numero di pagine del Corano lette
    private int giorniDigiuno; // Numero di giorni di digiuno
    private int preghiereComplete; // Numero di preghiere completate
    private int goal;

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

    public int getGoal() {
        return this.goal; // Assumendo che il goal sia una proprietà del Tracker
    }

    public void setGoal(int goal) {
        if (goal > 0) {
            this.goal = goal;
        } else {
            System.err.println("Errore: goal deve essere maggiore di zero.");
        }
    }

    // Metodo toString per debug
    @Override
    public String toString() {
        return "GestioneTrackerBean{" +
                "letturaCorano=" + letturaCorano +
                ", giorniDigiuno=" + giorniDigiuno +
                ", preghiereComplete=" + preghiereComplete +
                '}';
    }
}
