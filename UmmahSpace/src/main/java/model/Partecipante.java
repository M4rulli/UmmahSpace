package model;

public class Partecipante extends Utente {
    private final Tracker trackerSpirituale;

    // Costruttore con Tracker inizializzato a valori di default
    public Partecipante(String idUtente, String nome, String cognome, String username, String email, String password) {
        super(nome, cognome, username, email, password, idUtente);
        this.trackerSpirituale = new Tracker(0, idUtente, 0, 0.0); // Tracker inizializzato con valori default
    }

    public Tracker getTrackerSpirituale() {
        return trackerSpirituale;
    }
}