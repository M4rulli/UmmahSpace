package model;

public class Partecipante extends Utente {
    private Tracker trackerSpirituale;

    // Costruttore con Tracker inizializzato a valori di default
    public Partecipante(String idUtente, String nome, String cognome, String username, String email, String password, Boolean stato) {
        super(nome, cognome, username, email, password, idUtente, stato);
        this.trackerSpirituale = new Tracker(0, 0, 0, idUtente, 0); // Tracker inizializzato con valori default
    }

    // Getter e Setter per il Tracker
    public Tracker getTrackerSpirituale() {
        return trackerSpirituale;
    }

    public void setTrackerSpirituale(Tracker trackerSpirituale) {
        this.trackerSpirituale = trackerSpirituale;
    }

}