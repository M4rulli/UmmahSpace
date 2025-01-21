package engclasses.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GestioneTrackerBean {

    private int letturaCorano; // Numero di pagine del Corano lette
    private int giorniDigiuno; // Numero di giorni di digiuno
    private int preghiereComplete; // Numero di preghiere completate
    private int goal;
    private boolean haDigiunato;
    private String noteDigiuno;
    private Set<String> motivazioniDigiuno;
    private String idUtente; // ID univoco dell'utente associato al tracker

    public GestioneTrackerBean() {}

    // Getter e Setter
    public int getLetturaCorano() {
        return letturaCorano;
    }

    public void setLetturaCorano(int letturaCorano) {
        this.letturaCorano = letturaCorano;
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

    public Set<String> getMotivazioniDigiuno() {
        return motivazioniDigiuno;
    }

    public void setMotivazioniDigiuno(Set<String> motivazioniDigiuno) {
        this.motivazioniDigiuno = motivazioniDigiuno;
    }

    private Map<String, Boolean> preghiere = new HashMap<>();

    public void setPreghiera(String nome, boolean completata) {
        preghiere.put(nome, completata);
    }

    public boolean getPreghiera(String nome) {
        return preghiere.getOrDefault(nome, false);
    }


}
