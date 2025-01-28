package controllers.applicativo;

import engclasses.beans.GestioneTrackerBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.pattern.GestioneTrackerBeanFactory;
import misc.Session;
import model.Tracker;
import java.util.List;

public class GestioneTrackerController  {


    private final Session session;


    private static final List<String> PREGHIERE = List.of("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha");
    private static final String TRACKER_NOT_FOUND_MESSAGE = "Tracker non trovato per l'utente.";

    public GestioneTrackerController(Session session) {
        this.session = session;
    }

    public GestioneTrackerBean aggiungiLettura(GestioneTrackerBean trackerBean) {
        // Recupera il numero di pagine lette dalla Bean
        int pages = trackerBean.getLetturaCorano();

        // Recupera il Tracker associato all'utente
        Tracker tracker = GestioneTrackerDAO.getTracker(session.getIdUtente(), session.isPersistence());
        if (tracker == null) {
            throw new IllegalArgumentException(TRACKER_NOT_FOUND_MESSAGE);
        }

        // Recupera l'obiettivo giornaliero
        int goal = tracker.getGoal();

        // Calcola il nuovo totale delle pagine lette
        int nuovePagine = tracker.getLetturaCorano() + pages;

        // Controlla che il totale delle pagine lette non superi l'obiettivo giornaliero
        if (goal == 0) {
            throw new IllegalArgumentException("Obiettivo di lettura non inizializzato. Imposta prima un obiettivo.");
        }
        if (goal > 0 && nuovePagine > goal) {
            throw new IllegalArgumentException("Non puoi leggere più del tuo obiettivo giornaliero di " + goal + " pagine.");
        }

        // Verifica che il totale non superi il limite massimo (604)
        if (nuovePagine > 604) {
            throw new IllegalArgumentException("Non puoi leggere più di 604 pagine.");
        }

        // Aggiunge le pagine al Tracker
        tracker.setLetturaCorano(nuovePagine);

        // Aggiorna il Tracker nella DAO
        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());

        // Usa la factory per creare una bean aggiornata
        return GestioneTrackerBeanFactory.createTrackerBeanFromFactory(tracker);
    }

    // Metodo per calcolare il progresso
    public GestioneTrackerBean aggiornaBarraConProgresso(boolean persistence) {

        // Recupera il Tracker dalla DAO
        Tracker tracker = GestioneTrackerDAO.getTracker(session.getIdUtente(), persistence);
        if (tracker == null) {
            throw new IllegalArgumentException(TRACKER_NOT_FOUND_MESSAGE);
        }

        // Calcola il progresso
        int goal = tracker.getGoal();
        int pagesRead = tracker.getLetturaCorano();
        double progress = (goal > 0) ? (double) pagesRead / goal : 0.0;
        progress = Math.min(progress, 1.0); // Limita il progresso a 100%

        // Aggiorna lo stato del progresso
        tracker.setProgresso(progress);

        // Aggiorna il tracker nel buffer o database
        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());

        // Usa la factory per creare una bean aggiornata
        return GestioneTrackerBeanFactory.createTrackerBeanFromFactory(tracker);
    }

    // Metodo per aggiornare le preghiere
    public GestioneTrackerBean aggiornaPreghiere(GestioneTrackerBean trackerBean) {

        // Recupera il tracker associato all'utente
        Tracker tracker = GestioneTrackerDAO.getTracker(session.getIdUtente(), session.isPersistence());
        if (tracker == null) {
            throw new IllegalArgumentException(TRACKER_NOT_FOUND_MESSAGE);
        }

        // Aggiorna lo stato delle preghiere
        for (String preghiera : PREGHIERE) {
            tracker.setPreghiera(preghiera, trackerBean.getPreghiera(preghiera));
        }

        // Salva i dati aggiornati nella DAO
        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());

        // Usa la factory per creare e restituire una bean aggiornata
        return GestioneTrackerBeanFactory.createTrackerBeanFromFactory(tracker);
    }


    // Metodo per impostare l'obiettivo giornaliero
    public GestioneTrackerBean setObiettivoGiornaliero(GestioneTrackerBean trackerBean) {
        int goal = trackerBean.getGoal();

        // Recupera il tracker associato all'utente
        Tracker tracker = GestioneTrackerDAO.getTracker(session.getIdUtente(), session.isPersistence());

        // Imposta il nuovo obiettivo
        tracker.setGoal(goal);

        // Aggiorna il tracker nel buffer o database
        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());

        // Usa la factory per creare una bean aggiornata
        return GestioneTrackerBeanFactory.createTrackerBeanFromFactory(tracker);
    }

    public GestioneTrackerBean aggiornaDigiuno(GestioneTrackerBean bean) {

        // Recupera il Tracker dalla DAO
        Tracker tracker = GestioneTrackerDAO.getTracker(session.getIdUtente(), session.isPersistence());

        // Aggiorna i dati del Tracker
        tracker.setHaDigiunato(bean.isHaDigiunato());
        tracker.setNoteDigiuno(bean.getNoteDigiuno());
        tracker.setMotivazioniDigiuno(bean.getMotivazioniDigiuno());

        // Salva il Tracker aggiornato nella DAO
        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());

        // Usa la factory per creare la GestioneTrackerBean aggiornata
        return GestioneTrackerBeanFactory.createTrackerBeanFromFactory(tracker);
    }
}
