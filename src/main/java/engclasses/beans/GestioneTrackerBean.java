package engclasses.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GestioneTrackerBean {

    private int letturaCorano; // Numero di pagine del Corano lette
    private int goal;
    private boolean haDigiunato;
    private String noteDigiuno;
    private Set<String> motivazioniDigiuno;
    private double progress;
    private String idUtente; // ID dell'utente

    public GestioneTrackerBean() {}

    // Getter e Setter
    public int getLetturaCorano() {
        return letturaCorano;
    }

    public void setLetturaCorano(int letturaCorano) {
        this.letturaCorano = letturaCorano;
    }

    public int getGoal() {
        return this.goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public boolean isHaDigiunato() {
        return haDigiunato;
    }

    public void setHaDigiunato(boolean haDigiunato) {
        this.haDigiunato = haDigiunato;
    }

    public String getNoteDigiuno() {
        return noteDigiuno;
    }

    public void setNoteDigiuno(String noteDigiuno) {
        this.noteDigiuno = noteDigiuno;
    }

    public void setMotivazioniDigiuno(Set<String> motivazioniDigiuno) {
        this.motivazioniDigiuno = motivazioniDigiuno;
    }

    public Set<String> getMotivazioniDigiuno() {
        return motivazioniDigiuno != null ? motivazioniDigiuno : new HashSet<>();
    }

    private Map<String, Boolean> preghiere = new HashMap<>();

    public void setPreghiera(String nome, boolean completata) {
        preghiere.put(nome, completata);
    }

    public boolean getPreghiera(String nome) {
        return preghiere.getOrDefault(nome, false);
    }

    public double getProgresso() {
        return progress;
    }

    public void setProgresso(double progress) {
        this.progress = progress;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }
}
