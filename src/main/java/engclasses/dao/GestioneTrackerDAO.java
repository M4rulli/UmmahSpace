package engclasses.dao;

import model.Tracker;
import java.util.HashMap;
import java.util.Map;

public class GestioneTrackerDAO {

    // Buffer per memorizzare i tracker
    private static final Map<String, Tracker> trackerBuffer = new HashMap<>();

    // Metodo per ottenere un tracker in base all'ID utente
    public Tracker getTrackerConId(String idUtente) {
        Tracker tracker = trackerBuffer.get(idUtente); // Recupera il tracker dal buffer
        if (tracker != null) {
        } else {
            System.out.println("Tracker non trovato nel buffer.");
        }
        return tracker;
    }

    public static void aggiungiTracker(Tracker tracker) {
        if (trackerBuffer.containsKey(tracker.getIdUtente())) {
            System.out.println("Tracker già esistente per l'utente: " + tracker.getIdUtente());
            return; // Evita di sovrascrivere un tracker già presente
        }

        // Aggiunge il tracker al buffer
        trackerBuffer.put(tracker.getIdUtente(), tracker);
        System.out.println("Tracker aggiunto al buffer per l'utente: " + tracker.getIdUtente());
    }


    // Metodo per aggiornare un tracker esistente
    public void aggiornaTracker(Tracker tracker) {
        if (tracker == null || !trackerBuffer.containsKey(tracker.getIdUtente())) {
            System.out.println("Errore: Tracker non trovato per l'aggiornamento.");
            return;
        }
        trackerBuffer.put(tracker.getIdUtente(), tracker);
        System.out.println("Aggiornamento del tracker completato per l'utente con ID: " + tracker.getIdUtente());
    }


}
