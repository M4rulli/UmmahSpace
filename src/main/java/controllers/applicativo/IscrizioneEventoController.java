package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.dao.GestioneEventoDAO;
import engclasses.dao.IscrizioneEventoDAO;
import engclasses.dao.PartecipazioneDAO;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.EventoNonTrovatoException;
import engclasses.exceptions.IscrizioneEventoException;
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

import static misc.MessageUtils.mostraMessaggioErrore;

public class IscrizioneEventoController {

    private final Session session;
    private static final String ERRORE = "Errore";

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

    public boolean iscriviPartecipante(long idEvento) throws IscrizioneEventoException {
        try {
            // Verifica se il partecipante è già iscritto a questo specifico evento
            if (PartecipazioneDAO.isPartecipanteIscritto(session.getIdUtente(), idEvento, session.isPersistence())) {
                mostraMessaggioErrore(ERRORE, "Sei già iscritto a questo evento.");
                return false;
            }

            // Recupera i dati dell'evento dal DAO
            Evento evento = GestioneEventoDAO.getEventoById(idEvento, session.isPersistence());
            if (evento == null) {
                throw new EventoNonTrovatoException("Evento non trovato per l'ID: " + idEvento);
            }

            // Verifica il limite di partecipanti
            if (!evento.getStato() || evento.getIscritti() >= Integer.parseInt(evento.getLimitePartecipanti())) {
                mostraMessaggioErrore(ERRORE, "L'evento ha raggiunto il limite di partecipanti o è chiuso.");
                return false;
            }

            // Incrementa il numero di iscritti
            IscrizioneEventoDAO.aggiornaNumeroIscritti(idEvento, 1, session.isPersistence());
            Facade.getInstance().iscriviPartecipanteFacade(idEvento, session.getIdUtente(), session.isPersistence());
            return true;

        }  catch (Exception e) {
            throw new IscrizioneEventoException("Errore durante l'iscrizione del partecipante.", e);
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
}