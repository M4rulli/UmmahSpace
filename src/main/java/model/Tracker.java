package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Tracker {

    private int letturaCorano; // Numero di pagine lette del Corano
    private final String idUtente; // ID dell'utente associato al tracker
    private int goal;
    private boolean haDigiunato;
    private String noteDigiuno;
    private Set<String> motivazioniDigiuno;
    private double progress;

    // Costruttore completo
    public Tracker(int letturaCorano, String idUtente, int goal, double progress) {
        this.letturaCorano = letturaCorano;
        this.idUtente = idUtente;
        this.goal = goal;
        this.progress = progress;
    }

    // Getter e Setter
    public int getLetturaCorano() {
        return letturaCorano;
    }

    public void setLetturaCorano(int letturaCorano) {
        this.letturaCorano = letturaCorano;
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

    public boolean isHaDigiunato() {
        return haDigiunato;
    }

    public void setHaDigiunato(boolean haDigiunato) {
        this.haDigiunato = haDigiunato;
    }

    public void setNoteDigiuno(String noteDigiuno) {
        this.noteDigiuno = noteDigiuno;
    }

    public String getNoteDigiuno() {
        return noteDigiuno;
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
}
