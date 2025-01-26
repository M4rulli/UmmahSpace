package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.beans.PartecipanteBean;
import engclasses.dao.IscrizioneEventoDAO;
import model.Evento;
import model.IscrizionePartecipante;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IscrizioneEventoController {

    public IscrizioneEventoController() {}

    public Map<Integer, List<EventoBean>> getEventiDelMese(int month, int year) {
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(month, year);
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

    public List<EventoBean> getEventiPerGiorno(int giorno, int mese, int anno) {
        // Ottieni tutti gli eventi del mese e anno specificati
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(mese, anno);

        // Filtra gli eventi per il giorno specifico
        List<EventoBean> eventiDelGiorno = new ArrayList<>();
        for (Evento evento : eventi) {
            LocalDate dataEvento = LocalDate.parse(evento.getData());
            if (dataEvento.getDayOfMonth() == giorno && dataEvento.getMonthValue() == mese && dataEvento.getYear() == anno) {
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
                eventiDelGiorno.add(bean);
            }
        }
        return eventiDelGiorno;
    }

    public boolean iscriviPartecipante(PartecipanteBean partecipanteBean) throws IllegalArgumentException {

        // Recupera i dati dell'evento tramite l'ID (utilizzando il DAO)
        Evento evento = IscrizioneEventoDAO.getEventoById(partecipanteBean.getIdEvento());

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
        IscrizioneEventoDAO.aggiornaEvento(evento);

        // Converti la PartecipanteBean nel modello IscrizionePartecipante
        IscrizionePartecipante iscrizione = new IscrizionePartecipante(
                partecipanteBean.getIdUtente(),
                partecipanteBean.getEmail(),
                partecipanteBean.getIdEvento(),
                partecipanteBean.getDataIscrizione()
        );

        // Salva l'iscrizione nella DAO
        IscrizioneEventoDAO.aggiungiPartecipanteAdEvento(iscrizione);

        System.out.println("Partecipante con ID " + partecipanteBean.getIdUtente() +
                " iscritto all'evento con ID " + partecipanteBean.getIdEvento());
        return true;
    }
}