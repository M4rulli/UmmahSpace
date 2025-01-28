package controllers.applicativo;

import engclasses.beans.EventoBean;
import engclasses.beans.PartecipazioneBean;
import engclasses.dao.GestioneEventoDAO;
import engclasses.dao.PartecipazioneDAO;
import misc.Session;
import model.Evento;
import model.Partecipazione;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static misc.MessageUtils.mostraMessaggioErrore;

public class GestioneEventoController {

    private final Session session;
    private static final String MESSAGGIO_ERRORE = "Errore";

    public GestioneEventoController(Session session) {
        this.session = session;
    }

    // Metodo per recuperare tutti gli eventi associati a un organizzatore
    public List<EventoBean> getEventiOrganizzatore(String idUtente, Session session) {
        List<Evento> eventi = GestioneEventoDAO.getEventiByOrganizzatore(idUtente, session.isPersistence());
        List<EventoBean> eventoBeans = new ArrayList<>();

        for (Evento evento : eventi) {
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

            eventoBeans.add(bean);

        }
        return eventoBeans;
    }

    // Metodo per aggiungere un nuovo evento per un organizzatore
    public boolean aggiungiEvento(EventoBean eventoBean) {

        // Valida i dati forniti nella bean
        String erroriData = validaEvento(eventoBean);

        // Valida il link
        String erroriLink = validaLink(eventoBean);

        // Valida la fascia oraria
        String erroreFasciaOraria = validaFasciaOraria(eventoBean.getOrario(), eventoBean.getData(), session.getIdUtente(), eventoBean.getIdEvento());

        // Combina gli errori
        String errori = erroriData + erroreFasciaOraria + erroriLink;

        // Se ci sono errori nella validazione, mostra un messaggio di avviso e interrompi
        if (!errori.isEmpty()) {
            mostraMessaggioErrore(MESSAGGIO_ERRORE, errori);
            return false;
        }

        // Crea un identificatore per l'evento appena aggiunto
        long idEvento = System.currentTimeMillis(); // Restituisce il numero di millisecondi dal 1 gennaio 1970

        // Creazione del nuovo evento
        Evento nuovoEvento = new Evento();
        nuovoEvento.setTitolo(eventoBean.getTitolo());
        nuovoEvento.setDescrizione(eventoBean.getDescrizione());
        nuovoEvento.setData(eventoBean.getData());
        nuovoEvento.setOrario(eventoBean.getOrario());
        nuovoEvento.setLimitePartecipanti(eventoBean.getLimitePartecipanti());
        nuovoEvento.setIscritti(0);
        nuovoEvento.setLink(eventoBean.getLink());
        nuovoEvento.setNomeOrganizzatore(eventoBean.getNomeOrganizzatore());
        nuovoEvento.setCognomeOrganizzatore(eventoBean.getCognomeOrganizzatore());
        nuovoEvento.setStato(true);
        nuovoEvento.setIdEvento(idEvento);
        nuovoEvento.setIdOrganizzatore(session.getIdUtente());

        session.setIdEvento(idEvento);


        return GestioneEventoDAO.aggiungiEvento(nuovoEvento, session.isPersistence());
    }

    // Metodo per eliminare un evento
    public boolean eliminaEvento(long idEvento, String idUtente) {
        if (idEvento <= 0) {
            mostraMessaggioErrore(MESSAGGIO_ERRORE, "ID evento non valido.");
            return false;
        }
        return GestioneEventoDAO.eliminaEvento(idEvento, idUtente, session.isPersistence());
    }

    // Metodo per popolare i campi dell'evento da modificare
    public EventoBean inizializzaEvento() {

        Evento evento = GestioneEventoDAO.getEventoById(session.getIdEvento(), session.isPersistence());

        // Creare una bean per il trasferimento
        EventoBean bean = new EventoBean();

        bean.setTitolo(evento.getTitolo());
        bean.setDescrizione(evento.getDescrizione());
        bean.setData(evento.getData());
        bean.setOrario(evento.getOrario());
        bean.setLink(evento.getLink());
        bean.setLimitePartecipanti(evento.getLimitePartecipanti());
        return bean;
    }

    public List<PartecipazioneBean> getPartecipazioniEvento() {
        // Recupera le partecipazioni dal database o buffer
        List<Partecipazione> partecipazioni = PartecipazioneDAO.recuperaPartecipazioniPerEvento(session.getIdEvento(), session.isPersistence());
        if (partecipazioni.isEmpty()) {
            throw new IllegalArgumentException("Non sono state trovate partecipazioni per l'evento specificato.");
        }

        // Trasforma la lista di partecipazioni in una lista di bean
        List<PartecipazioneBean> partecipazioneBeans = new ArrayList<>();
        for (Partecipazione partecipazione : partecipazioni) {
            PartecipazioneBean bean = new PartecipazioneBean();
            bean.setNome(partecipazione.getNome());
            bean.setCognome(partecipazione.getCognome());
            bean.setEmail(partecipazione.getEmail());
            bean.setDataIscrizione(partecipazione.getDataIscrizione());
            partecipazioneBeans.add(bean);
        }
        return partecipazioneBeans;
    }

    public boolean aggiornaEvento(EventoBean updatedBean, long idEvento) {

        // Recupera l'evento corrente dal database o dal buffer
        Evento eventoEsistente = GestioneEventoDAO.getEventoById(idEvento, session.isPersistence());

        // Valida i dati forniti nella bean aggiornata
        String erroriEvento = validaEvento(updatedBean);

        // Valida la data
        String erroriData = validaData(updatedBean);

        // Valida il link
        String erroriLink = validaLink(updatedBean);

        // Valida la fascia oraria
        String erroreFasciaOraria = validaFasciaOraria(updatedBean.getOrario(), updatedBean.getData(), session.getIdUtente(), idEvento);

        // Combina gli errori
        String errori = erroriEvento + erroriData + erroreFasciaOraria + erroriLink;

        // Se ci sono errori nella validazione, mostra un messaggio di avviso e interrompi l'aggiornamento
        if (!errori.isEmpty()) {
            mostraMessaggioErrore(MESSAGGIO_ERRORE, errori);
            return false;
        }

        // Crea un nuovo oggetto Evento con i dati aggiornati
        Evento eventoAggiornato = new Evento(
                updatedBean.getTitolo(),
                updatedBean.getDescrizione(),
                updatedBean.getData(),
                updatedBean.getOrario(),
                updatedBean.getLimitePartecipanti(),
                eventoEsistente.getIscritti(),
                eventoEsistente.getLink(),
                eventoEsistente.getNomeOrganizzatore(),
                eventoEsistente.getCognomeOrganizzatore(),
                eventoEsistente.getStato(),
                eventoEsistente.getIdEvento(),
                eventoEsistente.getIdOrganizzatore()
        );

        // Salva l'evento aggiornato nel database o nel buffer
        GestioneEventoDAO.aggiornaEvento(eventoAggiornato, session.isPersistence());

        // L'aggiornamento è stato completato con successo
        return true;
    }

    // Metodo per aggiornare lo stato di un evento
    public void chiudiEvento() {
        // Recupera l'evento dal DAO
        Evento evento = GestioneEventoDAO.getEventoById(session.getIdEvento(), session.isPersistence());
        if (evento == null) {
            throw new IllegalArgumentException("Evento non trovato per l'ID: " + session.getIdEvento());
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
        validaCampo(eventoBean.getOrario(), "L'orario dell'evento", errori);

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
            errori.add("Il limite dei partecipanti è obbligatorio.");
            return;
        }
        try {
            int numero = Integer.parseInt(limite.trim());
            if (numero <= 0) {
                errori.add("Il limite dei partecipanti deve essere maggiore di zero.");
            } else {
                eventoBean.setLimitePartecipanti(String.valueOf(numero));
            }
        } catch (NumberFormatException e) {
            errori.add("Il limite dei partecipanti deve essere un numero valido.");
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

    private String validaFasciaOraria(String orarioNuovoEvento, String dataNuovoEvento, String idOrganizzatore, long idEventoCorrente) {
        StringBuilder errori = new StringBuilder();
        // Recupera gli eventi per la stessa data e organizzatore
        List<Evento> eventiEsistenti = GestioneEventoDAO.recuperaEventoPerData(dataNuovoEvento, idOrganizzatore, session.isPersistence());
        // Suddivide l'orario in orario di inizio e fine
        String[] orari = orarioNuovoEvento.split(" - ");
        String orarioInizioNuovoEvento = orari[0];
        String orarioFineNuovoEvento = orari[1];

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

        // Regex per validare il formato base di un URL
        String urlRegex = "^(https?://)?"
                + "([a-zA-Z0-9\\-.]+\\.)+[a-zA-Z]{2,}"  // Nome dominio
                + "(:\\d{1,5})?"                          // Porta (opzionale)
                + "(/\\S*)?$";                          // Path o query string (opzionale)

        // Verifica il formato del link
        if (!nuovoLink.matches(urlRegex)) {
            errori.append("Il link deve essere valido, es. 'https://example.com'.\n");
        }
        return errori.toString();
    }

}
