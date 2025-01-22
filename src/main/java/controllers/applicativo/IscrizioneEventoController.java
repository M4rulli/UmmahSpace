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
        new IscrizioneEventoDAO();
        // Recupera gli eventi dal DAO
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(month, year);

        // Mappa per organizzare gli eventi per giorno
        Map<Integer, List<EventoBean>> eventiDelMese = new HashMap<>();

        // Itera sugli eventi
        for (Evento evento : eventi) {

            // Estrae il giorno del mese dalla data
            LocalDate dataEvento = LocalDate.parse(evento.getData());
            int giorno = dataEvento.getDayOfMonth();

            // Crea una nuova Bean
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

            // Aggiunge la Bean alla lista del giorno corrispondente
            eventiDelMese.putIfAbsent(giorno, new ArrayList<>());
            eventiDelMese.get(giorno).add(bean);
        }
        return eventiDelMese;
    }

    public List<EventoBean> getTuttiGliEventiDelMese(int month, int year) {
        // Recupera gli eventi dal DAO
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(month, year);

        // Crea una lista di EventoBean
        List<EventoBean> eventiList = new ArrayList<>();
        for (Evento evento : eventi) {
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
            eventiList.add(bean);
        }
        return eventiList;
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