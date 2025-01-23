package misc;

import engclasses.beans.GestioneTrackerBean;
import model.Tracker;

import java.util.List;

public class GestioneTrackerBeanFactory {

    // Lista delle preghiere predefinite
    private static final List<String> PREGHIERE = List.of("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha");

    /**
     Metodo statico per creare una GestioneTrackerBean da un oggetto Tracker.
     */

    public static GestioneTrackerBean createTrackerBeanFromFactory(Tracker tracker) {

        // Creazione e popolamento della bean
        GestioneTrackerBean bean = new GestioneTrackerBean();
        bean.setLetturaCorano(tracker.getLetturaCorano());
        bean.setGoal(tracker.getGoal());
        bean.setHaDigiunato(tracker.isHaDigiunato());
        bean.setNoteDigiuno(tracker.getNoteDigiuno());
        bean.setMotivazioniDigiuno(tracker.getMotivazioniDigiuno());
        bean.setIdUtente(tracker.getIdUtente());
        bean.setProgresso(tracker.getProgresso());

        // Popolamento delle preghiere
        PREGHIERE.forEach(preghiera ->
                bean.setPreghiera(preghiera, tracker.getPreghiera(preghiera))
        );
        return bean;
    }
}