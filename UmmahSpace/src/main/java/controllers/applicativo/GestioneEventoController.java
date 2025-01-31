package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.beans.PartecipazioneBean;
import engclasses.dao.GestioneEventoDAO;
import engclasses.dao.PartecipazioneDAO;
import engclasses.exceptions.*;
import engclasses.pattern.BeanFactory;
import engclasses.pattern.Facade;
import misc.Session;
import model.Evento;
import model.Partecipazione;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static engclasses.pattern.BeanFactory.createPartecipazioneBean;


public class GestioneEventoController {

    private final Session session;
    private static final String ERRORE_EVENTO_NON_TROVATO = "L'evento non è stato trovato.";

    public GestioneEventoController(Session session) {
        this.session = session;
    }

    // Metodo per recuperare tutti gli eventi associati a un organizzatore
    public List<EventoBean> getEventiOrganizzatore(String idUtente, Session session) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {
        List<Evento> eventi = GestioneEventoDAO.getEventiByOrganizzatore(idUtente, session.isPersistence());
        List<EventoBean> eventoBeans = new ArrayList<>();

        for (Evento evento : eventi) {
            EventoBean bean = BeanFactory.createEventoBean(evento);
            eventoBeans.add(bean);
        }
        return eventoBeans;
    }

    // Metodo per aggiungere un nuovo evento per un organizzatore
    public void aggiungiEvento(EventoBean eventoBean) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, ValidazioneEventoException {
        String erroriData = validaEvento(eventoBean);
        String erroriLink = validaLink(eventoBean);
        String erroreFasciaOraria = validaFasciaOraria(eventoBean.getOrario(), eventoBean.getData(), session.getIdUtente(), eventoBean.getIdEvento());
        String errori = erroriData + erroreFasciaOraria + erroriLink;


        // Se ci sono errori nella validazione, lancia l'eccezione
        if (!errori.isEmpty()) {
            throw new ValidazioneEventoException(errori);
        }
        Facade.getInstance().aggiungiEventoFacade(eventoBean, session.getIdUtente(), session.isPersistence());
    }


    // Metodo per eliminare un evento
    public boolean eliminaEvento(long idEvento, String idUtente) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {
        if (idEvento <= 0) {
            throw new EventoNonTrovatoException(ERRORE_EVENTO_NON_TROVATO);
        }
        return GestioneEventoDAO.eliminaEvento(idEvento, idUtente, session.isPersistence());
    }

    // Metodo per popolare i campi dell'evento da modificare
    public EventoBean inizializzaEvento() throws EventoNonTrovatoException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        Evento evento = GestioneEventoDAO.getEventoById(session.getIdEvento(), session.isPersistence());
        if (evento == null) {
            throw new EventoNonTrovatoException(ERRORE_EVENTO_NON_TROVATO);
        }
        return BeanFactory.createEventoBean(evento);
    }

    public List<PartecipazioneBean> getPartecipazioniEvento() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, PartecipazioniNonTrovateException {
        // Recupera le partecipazioni dal database o buffer
        List<Partecipazione> partecipazioni = PartecipazioneDAO.recuperaPartecipazioniPerEvento(session.getIdEvento(), session.isPersistence());
        if (partecipazioni.isEmpty()) {
            throw new PartecipazioniNonTrovateException("Impossibile generare il Report");
        }
        // Trasforma la lista di partecipazioni in una lista di bean
        List<PartecipazioneBean> partecipazioneBeans = new ArrayList<>();
        for (Partecipazione partecipazione : partecipazioni) {
            partecipazioneBeans.add(createPartecipazioneBean(partecipazione));
        }
        return partecipazioneBeans;
    }

    public void aggiornaEvento(EventoBean updatedBean, long idEvento) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException, ValidazioneEventoException {

        // Valida i dati forniti nella bean aggiornata
        String erroriEvento = validaEvento(updatedBean);
        String erroriData = validaData(updatedBean);
        String erroriLink = validaLink(updatedBean);
        String erroreFasciaOraria = validaFasciaOraria(updatedBean.getOrario(), updatedBean.getData(), session.getIdUtente(), idEvento);

        // Combina gli errori
        String errori = erroriEvento + erroriData + erroreFasciaOraria + erroriLink;

        // Se ci sono errori nella validazione, mostra un messaggio di avviso e interrompi l'aggiornamento
        if (!errori.isEmpty()) {
            throw new ValidazioneEventoException(errori);
        }

        // Utilizza la Facade per aggiornare l'evento
        Facade.getInstance().aggiornaEventoFacade(updatedBean, idEvento, session.isPersistence());
    }

    // Metodo per aggiornare lo stato di un evento
    public void chiudiEvento() throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, EventoNonTrovatoException {
        // Recupera l'evento dal DAO
        Evento evento = GestioneEventoDAO.getEventoById(session.getIdEvento(), session.isPersistence());
        if (evento == null) {
            throw new EventoNonTrovatoException(ERRORE_EVENTO_NON_TROVATO);
        }

        // Aggiorna lo stato dell'evento a "chiuso"
        evento.setStato(false);

        // Salva le modifiche (nel buffer o nel database)
        GestioneEventoDAO.aggiornaEvento(evento, session.isPersistence());
    }


    private String validaEvento(EventoBean eventoBean) {
        List<String> errori = new ArrayList<>();

        // Validazione campi obbligatori
        validaCampo(eventoBean.getTitolo(), "Il titolo dell'evento", errori);
        validaCampo(eventoBean.getDescrizione(), "La descrizione dell'evento", errori);

        // Validazione descrizione (lunghezza massima)
        validaLunghezza(eventoBean.getDescrizione(), errori);

        // Validazione orario
        validaFormatoOrario(eventoBean.getOrario(), errori);

        // Validazione limite partecipanti
        validaLimitePartecipanti(eventoBean, errori);

        // Restituisce gli errori concatenati
        return String.join("\n", errori);
    }

    // Metodo generico per validare campi obbligatori
    private void validaCampo(String valore, String nomeCampo, List<String> errori) {
        if (valore == null || valore.trim().isEmpty()) {
            errori.add(nomeCampo + " è obbligatorio.");
        }
    }

    // Metodo per validare la lunghezza di un campo
    private void validaLunghezza(String valore, List<String> errori) {
        if (valore != null && valore.length() > 500) {
            errori.add("La descrizione" + " non può essere più lungo di " + 500 + " caratteri.");
        }
    }

    // Metodo per validare il formato dell'orario
    private void validaFormatoOrario(String orario, List<String> errori) {
        if (orario != null && !orario.trim().isEmpty()) {
            String regex = "^([01]\\d|2[0-3]):([0-5]\\d) - ([01]\\d|2[0-3]):([0-5]\\d)$";
            if (!orario.matches(regex)) {
                errori.add("L'orario deve essere nel formato 'HH:mm - HH:mm' (es. 09:00 - 17:00).");
            } else {
                // Validazione che l'orario di inizio sia prima dell'orario di fine
                String[] orari = orario.split(" - ");
                if (orari[0].compareTo(orari[1]) >= 0) {
                    errori.add("L'orario di inizio deve essere precedente all'orario di fine.");
                }
            }
        }
    }

    // Metodo per validare il limite dei partecipanti
    private void validaLimitePartecipanti(EventoBean eventoBean, List<String> errori) {
        String limite = eventoBean.getLimitePartecipanti();
        if (limite == null || limite.trim().isEmpty()) {
            errori.add("Il limite dei partecipanti è obbligatorio.\n");
            return;
        }
        try {
            int numero = Integer.parseInt(limite.trim());
            if (numero <= 0) {
                errori.add("Il limite dei partecipanti deve essere maggiore di zero.\n");
            } else {
                eventoBean.setLimitePartecipanti(String.valueOf(numero));
            }
        } catch (NumberFormatException e) {
            errori.add("Il limite dei partecipanti deve essere un numero valido.\n");
        }
    }

    public String validaData(EventoBean updatedBean) {
        StringBuilder errori = new StringBuilder();

        // Ottieni la data dalla bean
        String nuovaData = updatedBean.getData();

        // Controllo se la data è null o vuota
        if (nuovaData == null || nuovaData.trim().isEmpty()) {
            errori.append("La data dell'evento è obbligatoria.\n");
        } else {
            // Verifica se la data è nel formato corretto "gg-MM-yyyy"
            if (!nuovaData.matches("^\\d{4}-(0\\d|1[0-2])-([0-2]\\d|3[0-1])$")) {
                errori.append("La data deve essere nel formato 'yyyy-MM-dd' (es. 2025-01-01).\n");
            } else {
                // Converte la stringa in LocalDate per ulteriori controlli
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate data = LocalDate.parse(nuovaData, formatter);
                    LocalDate oggi = LocalDate.now();

                    // Verifica che la data non sia nel passato
                    if (data.isBefore(oggi)) {
                        errori.append("La data non può essere nel passato.\n");
                    } else {
                        // Imposta la data se valida
                        updatedBean.setData(nuovaData);
                    }
                } catch (DateTimeParseException e) {
                    errori.append("Errore nel parsing della data: formato non valido.\n");
                }
            }
        }
        // Ritorna eventuali errori come stringa
        return errori.toString();
    }

    private String validaFasciaOraria(String orarioNuovoEvento, String dataNuovoEvento, String idOrganizzatore, long idEventoCorrente) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        StringBuilder errori = new StringBuilder();

        // Controlla se l'orario è nullo, vuoto o esattamente " - "
        if (orarioNuovoEvento == null || orarioNuovoEvento.trim().equals("-") || orarioNuovoEvento.trim().equals(" - ")) {
            errori.append("L'orario dell'evento non può essere vuoto.");
            return errori.toString();
        }

        // Recupera gli eventi per la stessa data e organizzatore
        List<Evento> eventiEsistenti = GestioneEventoDAO.recuperaEventoPerData(dataNuovoEvento, idOrganizzatore, session.isPersistence());
        // Suddivide l'orario in orario di inizio e fine
        String[] orari = orarioNuovoEvento.split(" - ");
        String orarioInizioNuovoEvento = orari[0].trim();
        String orarioFineNuovoEvento = orari[1].trim();

        // Controlla che entrambi gli orari siano presenti e validi
        if (orarioInizioNuovoEvento.isEmpty() || orarioFineNuovoEvento.isEmpty()) {
            errori.append("Entrambi gli orari (inizio e fine) devono essere specificati nel formato HH:mm - HH:mm.\n");
            return errori.toString();
        }

        // Controlla sovrapposizioni con ogni evento esistente
        for (Evento evento : eventiEsistenti) {
            // Escludi l'evento corrente dal controllo
            if (evento.getIdEvento() == idEventoCorrente) {
                continue;
            }

            String[] orariEventoEsistente = evento.getOrario().split(" - ");
            String orarioInizioEsistente = orariEventoEsistente[0];
            String orarioFineEsistente = orariEventoEsistente[1];

            // Controlla se c'è sovrapposizione di orari
            if ((orarioInizioNuovoEvento.compareTo(orarioFineEsistente) < 0) && (orarioFineNuovoEvento.compareTo(orarioInizioEsistente) > 0)) {
                errori.append("La fascia oraria selezionata si sovrappone a un altro evento già esistente: ")
                        .append(evento.getTitolo())
                        .append(" (").append(evento.getOrario()).append(").\n");
            }
        }
        return errori.toString();
    }

    public String validaLink(EventoBean updatedBean) {
        StringBuilder errori = new StringBuilder();

        // Ottieni il link dal bean
        String nuovoLink = updatedBean.getLink();

        // Controllo se il link è null o vuoto (opzionale)
        if (nuovoLink == null || nuovoLink.trim().isEmpty()) {
            // Il link è facoltativo, nessun errore
            return errori.toString();
        }
        try {
            new URL(nuovoLink); // Se non è valido, genera un'eccezione
        } catch (Exception e) {
            errori.append("Il formato del link deve essere valido, es. 'https://example.com'.\n");
        }
        return errori.toString();
    }

}
