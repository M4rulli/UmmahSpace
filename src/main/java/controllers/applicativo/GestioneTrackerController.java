package controllers.applicativo;

import engclasses.beans.GestioneTrackerBean;
import engclasses.dao.GestioneTrackerDAO;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.TrackerNonTrovatoException;
import engclasses.pattern.BeanFactory;
import misc.Session;
import model.Tracker;
import java.util.List;

public class GestioneTrackerController {

    private final Session session;
    private static final List<String> PREGHIERE = List.of("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha");

    public GestioneTrackerController(Session session) {
        this.session = session;
    }

    // Aggiunge una lettura
    public GestioneTrackerBean aggiungiLettura(GestioneTrackerBean trackerBean) throws TrackerNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        Tracker tracker = recuperaTracker();

        int nuovePagine = getNuovePagine(tracker, trackerBean.getLetturaCorano());

        tracker.setLetturaCorano(nuovePagine);
        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());

        return BeanFactory.createTrackerBeanFromFactory(tracker);
    }

    private static int getNuovePagine(Tracker tracker, int pages) {
        int goal = tracker.getGoal();
        int nuovePagine = tracker.getLetturaCorano() + pages;

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
        return nuovePagine;
    }

    // Metodo per calcolare il progresso
    public GestioneTrackerBean aggiornaBarraConProgresso() throws TrackerNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        Tracker tracker = recuperaTracker();

        int goal = tracker.getGoal();
        int pagesRead = tracker.getLetturaCorano();
        double progress = (goal > 0) ? (double) pagesRead / goal : 0.0;
        progress = Math.min(progress, 1.0);

        tracker.setProgresso(progress);
        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());

        return BeanFactory.createTrackerBeanFromFactory(tracker);
    }

    // Metodo per aggiornare le preghiere
    public GestioneTrackerBean aggiornaPreghiere(GestioneTrackerBean trackerBean) throws TrackerNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        Tracker tracker = recuperaTracker();

        for (String preghiera : PREGHIERE) {
            tracker.setPreghiera(preghiera, trackerBean.getPreghiera(preghiera));
        }

        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());

        return BeanFactory.createTrackerBeanFromFactory(tracker);
    }

    // Metodo per impostare l'obiettivo giornaliero
    public GestioneTrackerBean setObiettivoGiornaliero(GestioneTrackerBean trackerBean) throws TrackerNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        Tracker tracker = recuperaTracker();

        tracker.setGoal(trackerBean.getGoal());
        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());

        return BeanFactory.createTrackerBeanFromFactory(tracker);
    }

    // Metodo per aggiornare lo stato del digiuno
    public GestioneTrackerBean aggiornaDigiuno(GestioneTrackerBean bean) throws TrackerNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        Tracker tracker = recuperaTracker();

        tracker.setHaDigiunato(bean.isHaDigiunato());
        tracker.setNoteDigiuno(bean.getNoteDigiuno());
        tracker.setMotivazioniDigiuno(bean.getMotivazioniDigiuno());

        GestioneTrackerDAO.saveOrUpdateTracker(tracker, session.isPersistence());

        return BeanFactory.createTrackerBeanFromFactory(tracker);
    }

    // Metodo per recuperare il Tracker
    private Tracker recuperaTracker() throws TrackerNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        Tracker tracker = GestioneTrackerDAO.getTracker(session.getIdUtente(), session.isPersistence());
        if (tracker == null) {
            throw new TrackerNonTrovatoException("Tracker non trovato per l'utente: " + session.getIdUtente());
        }
        return tracker;
    }
}