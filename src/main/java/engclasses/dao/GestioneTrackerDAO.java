package engclasses.dao;

import model.Tracker;

import java.util.HashMap;
import java.util.Map;

public class GestioneTrackerDAO {

    // Buffer per memorizzare i tracker
    private static final Map<String, Tracker> trackerBuffer = new HashMap<>();


    // Metodo per ottenere un tracker in base all'ID utente
    public Tracker getTrackerConId(String idUtente) {
        System.out.println("Tentativo di recupero del tracker per l'utente con ID: " + idUtente);
        Tracker tracker = trackerBuffer.get(idUtente); // Recupera il tracker dal buffer
        if (tracker != null) {
            System.out.println("Tracker trovato: " + tracker);
        } else {
            System.out.println("Tracker non trovato nel buffer.");
        }
        return tracker;
    }

    // Metodo per creare un nuovo tracker per un utente
    public Tracker creaTracker(String idUtente) {
        if (trackerBuffer.containsKey(idUtente)) {
            System.out.println("Tracker già esistente per l'utente: " + idUtente);
            return trackerBuffer.get(idUtente);
        }

        // Crea un nuovo tracker con valori iniziali
        Tracker nuovoTracker = new Tracker(0, 0, 0, idUtente, 0);

        // Aggiunge il tracker al buffer
        trackerBuffer.put(idUtente, nuovoTracker);
        System.out.println("Tracker aggiunto al buffer: " + trackerBuffer);
        return nuovoTracker;
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

    // Metodo toString per debug del buffer
    @Override
    public String toString() {
        return "GestioneTrackerDAO{" +
                "trackerBuffer=" + trackerBuffer +
                '}';
    }
}
