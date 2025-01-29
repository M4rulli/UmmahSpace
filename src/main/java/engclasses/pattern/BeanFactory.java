package engclasses.pattern;

import engclasses.beans.EventoBean;
import engclasses.beans.GestioneTrackerBean;
import engclasses.exceptions.EventoNonTrovatoException;
import engclasses.exceptions.TrackerNonTrovatoException;
import model.Evento;
import model.Tracker;

import java.util.List;

public class BeanFactory {

    // Lista delle preghiere predefinite
    private static final List<String> PREGHIERE = List.of("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha");


    private BeanFactory() {}

    //Metodo statico per creare una GestioneTrackerBean da un oggetto Tracker.
    public static GestioneTrackerBean createTrackerBeanFromFactory(Tracker tracker) throws TrackerNonTrovatoException {
        if (tracker == null) {
            throw new TrackerNonTrovatoException("Il tracker fornito è nullo.");
        }
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

    // Metodo statico per creare un EventoBean da un Evento
    public static EventoBean createEventoBean(Evento evento) throws EventoNonTrovatoException {
        if (evento == null) {
            throw new EventoNonTrovatoException("L'evento fornito è nullo.");
        }

        // Creazione e popolamento della bean
        EventoBean bean = new EventoBean();
        bean.setIdEvento(evento.getIdEvento());
        bean.setTitolo(evento.getTitolo());
        bean.setDescrizione(evento.getDescrizione());
        bean.setData(evento.getData());
        bean.setOrario(evento.getOrario());
        bean.setLimitePartecipanti(evento.getLimitePartecipanti());
        bean.setIscritti(evento.getIscritti());
        bean.setLink(evento.getLink());
        bean.setNomeOrganizzatore(evento.getNomeOrganizzatore());
        bean.setCognomeOrganizzatore(evento.getCognomeOrganizzatore());
        bean.setStato(evento.getStato());

        return bean;
    }
}