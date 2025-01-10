package model;

public class Partecipante extends Utente {
    private Tracker trackerSpirituale;

    // Costruttore
    public Partecipante(String nome, String cognome, String username, String email, String password, Long idUtente, Boolean stato, Tracker trackerSpirituale) {
        super(nome, cognome, username, email, password, idUtente, stato);
        this.trackerSpirituale = trackerSpirituale;
    }

    // Getter e Setter
    public Tracker getTrackerSpirituale() {
        return trackerSpirituale;
    }

    public void setTrackerSpirituale(Tracker trackerSpirituale) {
        this.trackerSpirituale = trackerSpirituale;
    }
}