package controllers.applicativo;

import engclasses.beans.GestioneTrackerBean;
import engclasses.dao.GestioneTrackerDAO;
import misc.Session;
import model.Tracker;

public class GestioneTrackerController {

    private final Session session;

    public GestioneTrackerController(Session session) {
        this.session = session;
    }

    public void addReading(GestioneTrackerBean trackerBean) {

        // Recupera il numero di pagine lette dalla Bean
        int pages = trackerBean.getLetturaCorano();

        // Recupera il Tracker associato all'utente
        Tracker tracker = GestioneTrackerDAO.getTracker(session.getIdUtente(), session.isPersistence());

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
        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());
    }


    // Metodo per calcolare il progresso
    public GestioneTrackerBean aggiornaBarraConProgresso(boolean persistence) {

        // Recupera il Tracker dalla DAO
        Tracker tracker = GestioneTrackerDAO.getTracker(session.getIdUtente(), persistence);

        if (tracker == null) {
            throw new IllegalArgumentException("Tracker non trovato per l'utente con ID: " + session.getIdUtente());
        }

        // Calcola il progresso in base ai dati ricevuti
        int goal = tracker.getGoal();
        int pagesRead = tracker.getLetturaCorano();
        double progress = (goal > 0) ? (double) pagesRead / goal : 0.0;

        // Crea e popola la Bean
        GestioneTrackerBean bean = new GestioneTrackerBean();
        bean.setGoal(goal);
        bean.setLetturaCorano(pagesRead);
        bean.setProgresso(Math.min(progress, 1.0)); // Limita il progresso a 100%

        return bean;
    }

    // Metodo per aggiornare le preghiere
    public void aggiornaPreghiere(GestioneTrackerBean trackerBean) {

        // Recupera il tracker associato all'utente
        Tracker tracker = GestioneTrackerDAO.getTracker(session.getIdUtente(), session.isPersistence());

        // Aggiorna lo stato delle preghiere
        tracker.setPreghiera("Fajr", trackerBean.getPreghiera("Fajr"));
        tracker.setPreghiera("Dhuhr", trackerBean.getPreghiera("Dhuhr"));
        tracker.setPreghiera("Asr", trackerBean.getPreghiera("Asr"));
        tracker.setPreghiera("Maghrib", trackerBean.getPreghiera("Maghrib"));
        tracker.setPreghiera("Isha", trackerBean.getPreghiera("Isha"));

        // Salva i dati aggiornati nella DAO
        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());
    }


    // Metodo per impostare l'obiettivo giornaliero
    public void setDailyGoal(GestioneTrackerBean trackerBean) {
        // Validazione dei dati
        int goal = trackerBean.getGoal();
        if (goal <= 0) {
            System.err.println("Errore: il goal deve essere maggiore di 0.");
            return;
        }
        // Recupera il tracker associato all'utente);
        Tracker tracker = GestioneTrackerDAO.getTracker(session.getIdUtente(), session.isPersistence());

        // Imposta il nuovo obiettivo
        tracker.setGoal(goal);
        System.out.println("Nuovo obiettivo impostato per l'utente con ID " + session.getIdUtente() + ": " + goal);

        // Aggiorna il tracker nel buffer o database
        try {
            GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());
            System.out.println("Tracker aggiornato nel database.");
        } catch (Exception e) {
            System.err.println("Errore durante l'aggiornamento del tracker: " + e.getMessage());
        }
    }

    public void aggiornaDigiuno(GestioneTrackerBean bean) {

        // Recupera il Tracker dalla DAO
        Tracker tracker = GestioneTrackerDAO.getTracker(session.getIdUtente(), session.isPersistence());
        if (tracker == null) {
            throw new IllegalArgumentException("Tracker non trovato per l'utente.");
        }

        // Aggiorna i dati del Tracker
        tracker.setHaDigiunato(bean.isHaDigiunato());
        tracker.setNoteDigiuno(bean.getNoteDigiuno());
        tracker.setMotivazioniDigiuno(bean.getMotivazioniDigiuno());

        // Salva il Tracker aggiornato nella DAO
        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());
    }


}
