package controllers.applicativo;

import engclasses.beans.GestioneTrackerBean;
import engclasses.dao.GestioneTrackerDAO;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.Circle;
import misc.Session;
import model.Tracker;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class GestioneTrackerController {

    @FXML
    private ProgressBar quranProgressBar;

    @FXML
    private Label goalLabel;

    private final GestioneTrackerDAO trackerDAO;
    private final Session session;



    public GestioneTrackerController(GestioneTrackerDAO trackerDAO, Session session) {
        this.trackerDAO = trackerDAO;
        this.session = session;
    }


    public void addReading(int pages) {
        Tracker tracker = trackerDAO.getTrackerConId(session.getIdUtente());

        if (tracker == null) {
            throw new IllegalArgumentException("Tracker non trovato per l'utente.");
        }

        int nuovePagine = tracker.getLetturaCorano() + pages;

        if (nuovePagine > 604) {
            throw new IllegalArgumentException("Non puoi leggere più di 604 pagine.");
        }

        tracker.setLetturaCorano(nuovePagine);
        trackerDAO.aggiornaTracker(tracker);
    }


    // Metodo per aggiornare le preghiere
    public void aggiornaPreghiere(String idUtente, String preghiera) {
        Tracker tracker = trackerDAO.getTrackerConId(idUtente);
        if (tracker == null) {
            System.out.println("Errore: Tracker non trovato per l'utente con ID " + idUtente);
            return;
        }

        // Aumenta il conteggio delle preghiere completate
        tracker.setPreghiereComplete(tracker.getPreghiereComplete() + 1);

        trackerDAO.aggiornaTracker(tracker);
        System.out.println("Preghiera aggiornata per l'utente con ID " + idUtente);
    }

    // Metodo per aggiornare la lettura del Corano
    public void aggiornaLetturaCorano(String idUtente, int pagine) {
        Tracker tracker = trackerDAO.getTrackerConId(idUtente);
        if (tracker == null) {
            System.out.println("Errore: Tracker non trovato per l'utente con ID " + idUtente);
            return;
        }

        // Aggiorna il conteggio delle pagine lette
        tracker.setLetturaCorano(tracker.getLetturaCorano() + pagine);

        trackerDAO.aggiornaTracker(tracker);
        System.out.println("Lettura del Corano aggiornata per l'utente con ID " + idUtente);
    }

    // Metodo per aggiornare i giorni di digiuno
    public void aggiornaGiorniDigiuno(String idUtente, int giorni) {
        Tracker tracker = trackerDAO.getTrackerConId(idUtente);
        if (tracker == null) {
            System.out.println("Errore: Tracker non trovato per l'utente con ID " + idUtente);
            return;
        }

        // Aggiorna il conteggio dei giorni di digiuno
        tracker.setGiorniDigiuno(tracker.getGiorniDigiuno() + giorni);

        trackerDAO.aggiornaTracker(tracker);
        System.out.println("Giorni di digiuno aggiornati per l'utente con ID " + idUtente);
    }

    // Metodo per aggiornare il progresso della barra
    public void updateProgress(Tracker tracker, ProgressBar quranProgressBar, Label goalLabel) {

        if (tracker != null) {
            int goal = tracker.getGoal();
            int letturaCorano = tracker.getLetturaCorano();

            // Calcola il progresso
            double progress = goal > 0 ? (double) letturaCorano / goal : 0;

            // Aggiorna la ProgressBar
            quranProgressBar.setProgress(progress);

            // Aggiorna l'etichetta dell'obiettivo
            goalLabel.setText("Obiettivo giornaliero: " + goal + " pagine");
        }
    }

    // Metodo per impostare l'obiettivo giornaliero
    public void setDailyGoal(int goal, String idUtente) {
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

    // Metodo per aggiornare il bean del tracker
    public GestioneTrackerBean getTrackerData(String idUtente) {
        Tracker tracker = trackerDAO.getTrackerConId(idUtente);

        if (tracker == null) {
            throw new IllegalArgumentException("Tracker non trovato per l'utente.");
        }

        GestioneTrackerBean bean = new GestioneTrackerBean();
        bean.setLetturaCorano(tracker.getLetturaCorano());
        bean.setGiorniDigiuno(tracker.getGiorniDigiuno());
        bean.setPreghiereComplete(tracker.getPreghiereComplete());
        bean.setGoal(tracker.getGoal());

        return bean;
    }


}
