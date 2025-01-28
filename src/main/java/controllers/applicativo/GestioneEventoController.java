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

        // Valida la fascia oraria
        String erroreFasciaOraria = validaFasciaOraria(eventoBean.getOrario(), eventoBean.getData(), session.getIdUtente(), eventoBean.getIdEvento());

        // Combina gli errori
        String errori = erroriData + erroreFasciaOraria;

        // Se ci sono errori nella validazione, mostra un messaggio di avviso e interrompi
        if (!errori.isEmpty()) {
            mostraMessaggioErrore("Errore", errori);
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
            mostraMessaggioErrore("Errore", "ID evento non valido.");
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
        String errori = erroriEvento + erroriData + erroreFasciaOraria;

        // Se ci sono errori nella validazione, mostra un messaggio di avviso e interrompi l'aggiornamento
        if (!errori.isEmpty()) {
            mostraMessaggioErrore("Errore", errori);
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
        StringBuilder errori = new StringBuilder();

        // Validazione dei campi obbligatori
        if (eventoBean.getTitolo() == null || eventoBean.getTitolo().isEmpty()) {
            errori.append("Il titolo dell'evento è obbligatorio.\n");
        }
        if (eventoBean.getDescrizione() == null || eventoBean.getDescrizione().isEmpty()) {
            errori.append("La descrizione dell'evento è obbligatoria.\n");
        } else if (eventoBean.getDescrizione().length() > 500) {
            errori.append("La descrizione non può essere più lunga di 500 caratteri.\n");
        }

        // Validazione dell'orario
        if (eventoBean.getOrario() == null || eventoBean.getOrario().replace(" - ", "").isEmpty()) {
            errori.append("L'orario dell'evento è obbligatorio.\n");
        } else {
            // Controlla se il formato è valido: "HH:mm - HH:mm"
            if (!eventoBean.getOrario().matches("^([01]\\d|2[0-3]):([0-5]\\d) - ([01]\\d|2[0-3]):([0-5]\\d)$")) {
                errori.append("L'orario deve essere nel formato 'HH:mm - HH:mm' (es. 09:00 - 17:00).\n");
            } else {
                // Valida che l'orario di inizio sia precedente all'orario di fine
                String[] orari = eventoBean.getOrario().split(" - ");
                String orarioInizio = orari[0];
                String orarioFine = orari[1];
                if (orarioInizio.compareTo(orarioFine) >= 0) {
                    errori.append("L'orario di inizio deve essere precedente all'orario di fine.\n");
                }
            }
        }

        // Validazione del limite partecipanti
        if (eventoBean.getLimitePartecipanti().trim().isEmpty()) {
            errori.append("Il limite dei partecipanti è obbligatorio.\n");
        } else {
            try {
                int limitePartecipanti = Integer.parseInt(eventoBean.getLimitePartecipanti().trim());
                if (limitePartecipanti <= 0) {
                    errori.append("Il limite dei partecipanti deve essere maggiore di zero.\n");
                } else {
                    eventoBean.setLimitePartecipanti(String.valueOf(limitePartecipanti));
                }
            } catch (NumberFormatException e) {
                errori.append("Il limite dei partecipanti deve essere un numero valido.\n");
            }
        }

        return errori.toString();
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
        String URL_REGEX = "^(https?://)?" + // Schema HTTP o HTTPS (facoltativo)
                "([a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,}" + // Nome dominio
                "(:\\d{1,5})?" + // Porta (opzionale)
                "(\\/\\S*)?$"; // Path o query string (opzionale)

        // Verifica il formato del link
        if (!nuovoLink.matches(URL_REGEX)) {
            errori.append("Il link deve essere valido, es. 'https://example.com'.\n");
        }
        return errori.toString();
    }

}
