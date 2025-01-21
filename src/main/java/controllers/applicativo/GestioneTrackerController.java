package controllers.applicativo;

import engclasses.beans.GestioneTrackerBean;
import engclasses.dao.GestioneTrackerDAO;
import misc.Session;
import model.Tracker;

public class GestioneTrackerController {

    private final GestioneTrackerDAO trackerDAO;
    private final Session session;

    public GestioneTrackerController(GestioneTrackerDAO trackerDAO, Session session) {
        this.trackerDAO = trackerDAO;
        this.session = session;
    }

    public void addReading(GestioneTrackerBean trackerBean) {

        // Recupera il numero di pagine lette dalla Bean
        int pages = trackerBean.getLetturaCorano();

        // Recupera il Tracker associato all'utente
        Tracker tracker = trackerDAO.getTrackerConId(session.getIdUtente());

        if (tracker == null) {
            throw new IllegalArgumentException("Tracker non trovato per l'utente.");
        }

        int nuovePagine = tracker.getLetturaCorano() + pages;

        if (nuovePagine > 604) {
            throw new IllegalArgumentException("Non puoi leggere più di 604 pagine.");
        }

        // Aggiunge le pagine al Tracker
        tracker.setLetturaCorano(tracker.getLetturaCorano() + pages);

        // Aggiorna il Tracker nella DAO
        trackerDAO.aggiornaTracker(tracker);
    }


    // Metodo per calcolare il progresso
    public double calcolaProgresso(GestioneTrackerBean bean) {

        int goal = bean.getGoal();
        int pagesRead = bean.getLetturaCorano();

        // Calcola il progresso
        double progress = (goal > 0) ? (double) pagesRead / goal : 0.0;
        return Math.min(progress, 1.0); // Limita il progresso a 100%
    }


    // Metodo per aggiornare le preghiere
    public void aggiornaPreghiere(GestioneTrackerBean trackerBean) {
        // Recupera il tracker associato all'utente
        Tracker tracker = trackerDAO.getTrackerConId(session.getIdUtente());

        if (tracker == null) {
            throw new IllegalArgumentException("Tracker non trovato per l'utente.");
        }

        // Aggiorna lo stato delle preghiere
        tracker.setPreghiera("Fajr", trackerBean.getPreghiera("Fajr"));
        tracker.setPreghiera("Dhuhr", trackerBean.getPreghiera("Dhuhr"));
        tracker.setPreghiera("Asr", trackerBean.getPreghiera("Asr"));
        tracker.setPreghiera("Maghrib", trackerBean.getPreghiera("Maghrib"));
        tracker.setPreghiera("Isha", trackerBean.getPreghiera("Isha"));

        // Salva i dati aggiornati nella DAO
        trackerDAO.aggiornaTracker(tracker);
    }


    // Metodo per impostare l'obiettivo giornaliero
    public void setDailyGoal(GestioneTrackerBean trackerBean, String idUtente) {
        // Validazione dei dati
        int goal = trackerBean.getGoal();
        if (goal <= 0) {
            System.err.println("Errore: il goal deve essere maggiore di 0.");
            return;
        }

        // Recupera il tracker associato all'utente
        Tracker tracker = trackerDAO.getTrackerConId(idUtente);

        // Imposta il nuovo obiettivo
        tracker.setGoal(goal);
        System.out.println("Nuovo obiettivo impostato per l'utente con ID " + idUtente + ": " + goal);

        // Aggiorna il tracker nel buffer o database
        try {
            trackerDAO.aggiornaTracker(tracker);
            System.out.println("Tracker aggiornato nel database.");
        } catch (Exception e) {
            System.err.println("Errore durante l'aggiornamento del tracker: " + e.getMessage());
        }
    }

    public void aggiornaDigiuno(GestioneTrackerBean bean) {

        // Recupera il Tracker dalla DAO
        Tracker tracker = trackerDAO.getTrackerConId(session.getIdUtente());
        if (tracker == null) {
            throw new IllegalArgumentException("Tracker non trovato per l'utente.");
        }

        // Aggiorna i dati del Tracker
        tracker.setHaDigiunato(bean.isHaDigiunato());
        tracker.setNoteDigiuno(bean.getNoteDigiuno());
        tracker.setMotivazioniDigiuno(bean.getMotivazioniDigiuno());

        // Salva il Tracker aggiornato nella DAO
        trackerDAO.aggiornaTracker(tracker);
    }


}
