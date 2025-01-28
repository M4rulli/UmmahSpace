package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.dao.GestioneEventoDAO;
import engclasses.dao.IscrizioneEventoDAO;
import engclasses.dao.PartecipanteDAO;
import engclasses.dao.PartecipazioneDAO;
import misc.Session;
import model.Evento;
import model.Partecipante;
import model.Partecipazione;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static misc.MessageUtils.mostraMessaggioErrore;

public class IscrizioneEventoController {

    private final Session session;

    public IscrizioneEventoController(Session session) {
        this.session = session;
    }

    public Map<Integer, List<EventoBean>> getEventiDelMese(int month, int year) {
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(month, year, session.isPersistence());
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
            bean.setLink(evento.getLink());
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
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(mese, anno, session.isPersistence());
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
                bean.setLink(evento.getLink());
                bean.setNomeOrganizzatore(evento.getNomeOrganizzatore());
                bean.setCognomeOrganizzatore(evento.getCognomeOrganizzatore());
                bean.setStato(evento.getStato());
                eventiDelGiorno.add(bean);
            }
        }
        return eventiDelGiorno;
    }

    public boolean iscriviPartecipante(long idEvento) {
        try {

            // Verifica se il partecipante è già iscritto a questo specifico evento
            if (PartecipazioneDAO.isPartecipanteIscritto(session.getIdUtente(), idEvento, session.isPersistence())) {
                mostraMessaggioErrore("Errore","Sei già iscritto a questo evento.");
                return false;
            }

            // Recupera i dati del partecipante dal DAO
            Partecipante partecipante = PartecipanteDAO.selezionaPartecipante("idUtente", session.getIdUtente(), session.isPersistence());
            if (partecipante == null) {
                mostraMessaggioErrore("Errore", "Partecipante non trovato per l'ID: " + session.getIdUtente());
                return false;
            }

            // Recupera i dati dell'evento dal DAO
            Evento evento = GestioneEventoDAO.getEventoById(idEvento, session.isPersistence());
            if (evento == null) {
                mostraMessaggioErrore("Errore", "Evento non trovato per l'ID: " + idEvento);
                return false;
            }

            // Verifica il limite di partecipanti
            if (!evento.getStato() || evento.getIscritti() >= Integer.parseInt(evento.getLimitePartecipanti())) {
                mostraMessaggioErrore("Errore", "L'evento ha raggiunto il limite di partecipanti o è chiuso.");
                return false;
            }

            // Incrementa il numero di iscritti
            IscrizioneEventoDAO.aggiornaNumeroIscritti(idEvento, 1, session.isPersistence());

            // Crea l'oggetto Partecipazione
            Partecipazione partecipazione = new Partecipazione(
                    partecipante.getIdUtente(),
                    idEvento,
                    partecipante.getNome(),
                    partecipante.getCognome(),
                    partecipante.getUsername(),
                    partecipante.getEmail(),
                    LocalDate.now().toString()
            );

            // Salva la partecipazione
            PartecipazioneDAO.salvaPartecipazione(partecipazione, session.isPersistence());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'iscrizione del partecipante.", e);
        }
    }

    public boolean cancellaIscrizione(EventoBean eventoBean) {
        try {
            // Recupera l'ID dell'evento e dell'utente
            long idEvento = eventoBean.getIdEvento();
            String idUtente = session.getIdUtente();

            // Rimuove la partecipazione dal database o buffer
            boolean rimozioneEffettuata = PartecipazioneDAO.rimuoviPartecipazione(idEvento, idUtente, session.isPersistence());

            if (!rimozioneEffettuata) {
                return false; // Errore nella rimozione
            }

            // Aggiorna il numero di iscritti (-1)
            IscrizioneEventoDAO.aggiornaNumeroIscritti(idEvento, -1, session.isPersistence());

            return true; // Operazione riuscita
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Gestione errore
        }
    }


    public List<EventoBean> getDettagliPartecipazioneUtente(String idUtente) {
        // Recupera tutte le partecipazioni dell'utente
        List<Partecipazione> partecipazioni = PartecipazioneDAO.recuperaPartecipazioni(idUtente, session.isPersistence());

        // Lista per contenere i dettagli degli eventi
        List<EventoBean> eventiIscritti = new ArrayList<>();

        // Recupera i dettagli di ogni evento associato alle partecipazioni
        for (Partecipazione partecipazione : partecipazioni) {
            Evento evento = GestioneEventoDAO.getEventoById(partecipazione.getIdEvento(), session.isPersistence());
            if (evento != null) {
                EventoBean eventoBean = new EventoBean();
                eventoBean.setIdEvento(evento.getIdEvento());
                eventoBean.setTitolo(evento.getTitolo());
                eventoBean.setDescrizione(evento.getDescrizione());
                eventoBean.setData(evento.getData());
                eventoBean.setOrario(evento.getOrario());
                eventoBean.setLink(evento.getLink());
                eventoBean.setNomeOrganizzatore(evento.getNomeOrganizzatore());
                eventoBean.setCognomeOrganizzatore(evento.getCognomeOrganizzatore());
                eventoBean.setLimitePartecipanti(evento.getLimitePartecipanti());
                eventoBean.setIscritti(evento.getIscritti());
                eventoBean.setStato(evento.getStato());

                // Aggiungi alla lista
                eventiIscritti.add(eventoBean);
            }
        }
        return eventiIscritti;
    }
}