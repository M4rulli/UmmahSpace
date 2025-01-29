package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.dao.GestioneEventoDAO;
import engclasses.dao.IscrizioneEventoDAO;
import engclasses.dao.PartecipazioneDAO;
import engclasses.exceptions.*;
import engclasses.pattern.BeanFactory;
import engclasses.pattern.Facade;
import misc.Session;
import model.Evento;
import model.Partecipazione;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IscrizioneEventoController {

    private final Session session;

    public IscrizioneEventoController(Session session) {
        this.session = session;
    }

    public Map<Integer, List<EventoBean>> getEventiDelMese(int month, int year) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(month, year, session.isPersistence());
        Map<Integer, List<EventoBean>> eventiDelMese = new HashMap<>();
        for (Evento evento : eventi) {
            LocalDate dataEvento = LocalDate.parse(evento.getData());
            int giorno = dataEvento.getDayOfMonth();
            EventoBean bean = BeanFactory.createEventoBean(evento);
            eventiDelMese.putIfAbsent(giorno, new ArrayList<>());
            eventiDelMese.get(giorno).add(bean);
        }
        return eventiDelMese;
    }

    public List<EventoBean> getEventiPerGiorno(int giorno, int mese, int anno) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {
        // Ottieni tutti gli eventi del mese e anno specificati
        List<Evento> eventi = IscrizioneEventoDAO.getEventiPerMeseAnno(mese, anno, session.isPersistence());
        // Filtra gli eventi per il giorno specifico
        List<EventoBean> eventiDelGiorno = new ArrayList<>();
        for (Evento evento : eventi) {
            LocalDate dataEvento = LocalDate.parse(evento.getData());
            if (dataEvento.getDayOfMonth() == giorno && dataEvento.getMonthValue() == mese && dataEvento.getYear() == anno) {
                // Creazione Bean
                EventoBean bean = BeanFactory.createEventoBean(evento);
                eventiDelGiorno.add(bean);
            }
        }
        return eventiDelGiorno;
    }

    public boolean iscriviPartecipante(long idEvento) throws IscrizioneEventoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, UtenteNonTrovatoException {

        // Verifica se l'ID evento è valido
        if (idEvento <= 0) {
            throw new IscrizioneEventoException("Errore durante l'iscrizione: ID evento non valido.",
                    new EventoNonTrovatoException("ID evento non valido: " + idEvento));
        }

        String errori = validaIscrizione(idEvento);
        // Se ci sono errori, lancia un'eccezione con il messaggio accumulato
        if (!errori.isEmpty()) {
            throw new IscrizioneEventoException(errori);
        }

        // Incrementa il numero di iscritti
        IscrizioneEventoDAO.aggiornaNumeroIscritti(idEvento, 1, session.isPersistence());
        Facade.getInstance().iscriviPartecipanteFacade(idEvento, session.getIdUtente(), session.isPersistence());
        return true;
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
        } catch (Exception e) {return false; // Gestione errore
        }
    }


    public List<EventoBean> getDettagliPartecipazioneUtente(String idUtente) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {
        // Recupera tutte le partecipazioni dell'utente
        List<Partecipazione> partecipazioni = PartecipazioneDAO.recuperaPartecipazioni(idUtente, session.isPersistence());

        // Lista per contenere i dettagli degli eventi
        List<EventoBean> eventiIscritti = new ArrayList<>();

        // Recupera i dettagli di ogni evento associato alle partecipazioni
        for (Partecipazione partecipazione : partecipazioni) {
            Evento evento = GestioneEventoDAO.getEventoById(partecipazione.getIdEvento(), session.isPersistence());
            if (evento != null) {
                EventoBean bean = BeanFactory.createEventoBean(evento);
                // Aggiungi alla lista
                eventiIscritti.add(bean);
            }
        }
        return eventiIscritti;
    }

    private String validaIscrizione(long idEvento) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        StringBuilder errori = new StringBuilder();

        // Controllo se è già iscritto
        if (PartecipazioneDAO.isPartecipanteIscritto(session.getIdUtente(), idEvento, session.isPersistence())) {
            errori.append("Sei già iscritto a questo evento.\n");
        }

        // Recupera l'evento
        Evento evento = GestioneEventoDAO.getEventoById(idEvento, session.isPersistence());
        if (evento == null) {
            errori.append("Evento non trovato per l'ID: ").append(idEvento).append("\n");
        } else {
            // Verifica se l’evento è aperto e se non ha raggiunto il limite partecipanti
            if (!evento.getStato() || evento.getIscritti() >= Integer.parseInt(evento.getLimitePartecipanti())) {
                errori.append("L'evento ha raggiunto il limite di partecipanti o è chiuso.\n");
            }
        }

        return errori.toString();
    }
}