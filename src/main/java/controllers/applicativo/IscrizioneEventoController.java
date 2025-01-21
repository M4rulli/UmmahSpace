package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.beans.PartecipanteBean;
import engclasses.dao.IscrizioneEventoDAO;
import model.Evento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IscrizioneEventoController {
    private final IscrizioneEventoDAO iscrizioneEventoDAO;


    public IscrizioneEventoController(IscrizioneEventoDAO iscrizioneEventoDAO) {
        this.iscrizioneEventoDAO = iscrizioneEventoDAO;
    }

    public Map<Integer, List<EventoBean>> getEventiDelMese(int month, int year) {
        List<Evento> eventi = iscrizioneEventoDAO.getEventiPerMeseAnno(month, year);
        Map<Integer, List<EventoBean>> eventiDelMese = new HashMap<>();

        for (Evento evento : eventi) {
            LocalDate dataEvento = LocalDate.parse(evento.getData());
            int giorno = dataEvento.getDayOfMonth();

            EventoBean bean = new EventoBean();
            bean.setIdEvento(evento.getIdEvento());
            bean.setTitolo(evento.getTitolo());
            bean.setDescrizione(evento.getDescrizione());
            bean.setData(evento.getData());
            bean.setOrario(evento.getOrario());
            bean.setLimitePartecipanti(evento.getLimitePartecipanti());
            bean.setIscritti(evento.getIscritti());
            bean.setNomeOrganizzatore(evento.getNomeOrganizzatore());
            bean.setCognomeOrganizzatore(evento.getCognomeOrganizzatore());
            bean.setStato(evento.getStato());

            eventiDelMese.putIfAbsent(giorno, new ArrayList<>());
            eventiDelMese.get(giorno).add(bean);
        }
        return eventiDelMese;
    }

    public boolean iscriviPartecipante(PartecipanteBean partecipanteBean) throws IllegalArgumentException {

        // Recupera i dati dell'evento tramite l'ID (utilizzando il DAO)
        Evento evento = iscrizioneEventoDAO.getEventoById(partecipanteBean.getIdEvento());

        if (evento == null) {
            throw new IllegalArgumentException("Evento non trovato per l'ID: " + partecipanteBean.getIdEvento());
        }

        // Controlla lo stato dell'evento e il numero di iscritti
        if (!evento.getStato()) {
            throw new IllegalArgumentException("L'evento è chiuso.");
        }

        if (evento.getIscritti() >= evento.getLimitePartecipanti()) {
            throw new IllegalArgumentException("L'evento ha raggiunto il limite di partecipanti.");
        }

        // Aggiorna l'evento nella DAO
        iscrizioneEventoDAO.aggiornaEvento(evento);

        // Salva l'iscrizione del partecipante nella DAO.   DA RIVEDERE!!!!!!
        iscrizioneEventoDAO.aggiungiPartecipanteAdEvento(partecipanteBean);

        System.out.println("Partecipante con ID " + partecipanteBean.getIdUtente() +
                " iscritto all'evento con ID " + partecipanteBean.getIdEvento());
        return true;
    }

}