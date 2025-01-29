package engclasses.dao;

import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import model.Evento;
import engclasses.pattern.Connect;

import java.sql.*;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GestioneEventoDAO {

    private static final List<Evento> eventiBuffer = new ArrayList<>();
    private static final String ERRORE_AGGIORNAMENTO_DB = "Errore durante l'aggiornamento del database";

    private GestioneEventoDAO() {

    }

    // Recupera tutti gli eventi associati a un organizzatore (dal buffer o database)
    public static List<Evento> getEventiByOrganizzatore(String idUtente, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            return recuperaEventiDaDb(idUtente);
        } else {
            return getEventiDalBuffer(idUtente);
        }
    }

    // Aggiunge un nuovo evento (buffer o database)
    public static boolean aggiungiEvento(Evento nuovoEvento, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            return salvaEventoInDb(nuovoEvento);
        } else {
            return salvaEventoNelBuffer(nuovoEvento);
        }
    }

    // Aggiorna un evento esistente (buffer o database)
    public static void aggiornaEvento(Evento eventoAggiornato, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            aggiornaEventoInDb(eventoAggiornato);
        } else {
            aggiornaEventoNelBuffer(eventoAggiornato);
        }
    }

    // Recupera tutti gli eventi in base ad una data specifica (buffer o database)
    public static List<Evento> recuperaEventoPerData(String data, String idOrganizzatore, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            return getEventoByDataDb(data, idOrganizzatore);
        } else {
            return getEventiByData(data, idOrganizzatore);
    }}

    // Elimina un evento (buffer o database)
    public static boolean eliminaEvento(long idEvento, String idUtente, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            return eliminaEventoDaDb(idEvento, idUtente);
        } else {
            return eliminaEventoDalBuffer(idEvento, idUtente);
        }
    }

    // Recupera un evento dal buffer tramite ID
    private static List<Evento> getEventiDalBuffer(String idUtente) {
        List<Evento> eventiPerOrganizzatore = new ArrayList<>();
        for (Evento evento : eventiBuffer) {
            if (Objects.equals(evento.getIdOrganizzatore(), idUtente)) {
                eventiPerOrganizzatore.add(evento);
            }
        }
        return eventiPerOrganizzatore;
    }

    // Recupera un evento per ID (dal buffer o dal database)
    public static Evento getEventoById(long idEvento, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            return recuperaEventoDaDb(idEvento);
        } else {
            return getEventoDalBuffer(idEvento);
        }
    }

    // Salva un evento nel buffer
    private static boolean salvaEventoNelBuffer(Evento nuovoEvento) {
        try {
            eventiBuffer.add(nuovoEvento);
            return true;
        } catch (Exception e) {return false;
        }
    }

    // Aggiorna un evento nel buffer
    private static void aggiornaEventoNelBuffer(Evento eventoAggiornato) {
        for (int i = 0; i < eventiBuffer.size(); i++) {
            Evento evento = eventiBuffer.get(i);
            if (evento.getIdEvento() == eventoAggiornato.getIdEvento()) {
                eventiBuffer.set(i, eventoAggiornato);
                return;
            }
        }
    }

    // Elimina un evento dal buffer.
    private static boolean eliminaEventoDalBuffer(long idEvento, String idUtente) {
        for (int i = 0; i < eventiBuffer.size(); i++) {
            Evento evento = eventiBuffer.get(i);
            if (evento.getIdEvento() == idEvento && Objects.equals(evento.getIdOrganizzatore(), idUtente)) {
                eventiBuffer.remove(i);
                return true;
            }
        }
        return false;
    }

    // Recupera un evento dal buffer tramite ID
    private static Evento getEventoDalBuffer(long idEvento) {
        for (Evento evento : eventiBuffer) {
            if (evento.getIdEvento() == idEvento) {
                return evento;
            }
        }
        return null;  // Non trovato nel buffer
    }

    // Recupera un evento per una data specifica
    public static List<Evento> getEventiByData(String data, String idOrganizzatore) {
        List<Evento> eventiFiltrati = new ArrayList<>();

        // Itera su tutti gli eventi nel buffer
        for (Evento evento : eventiBuffer) {
            // Confronta la data e l'idOrganizzatore
            if (evento.getData().equals(data) && evento.getIdOrganizzatore().equals(idOrganizzatore)) {
                eventiFiltrati.add(evento);
            }
        }
        return eventiFiltrati;
    }


    // ------------------ METODI DATABASE ------------------

    // Recupera eventi dal database basandoci sull'id dell'organizzatore
    private static List<Evento> recuperaEventiDaDb(String idUtente) throws DatabaseOperazioneFallitaException, DatabaseConnessioneFallitaException {
        List<Evento> eventi = new ArrayList<>();
        String query = "SELECT titolo, descrizione, data, orario, limitePartecipanti, iscritti, link, " +
                "nomeOrganizzatore, cognomeOrganizzatore, stato, idEvento, idUtente " +
                "FROM Eventi WHERE idUtente = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idUtente);
            // Esegue la query
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventi.add(fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
        return eventi;
    }

    // Salva un evento nel database
    private static boolean salvaEventoInDb(Evento evento) throws DatabaseOperazioneFallitaException, DatabaseConnessioneFallitaException {
        String query = "INSERT INTO Eventi (idEvento, idUtente, titolo, descrizione, data, orario, link, nomeOrganizzatore, cognomeOrganizzatore, limitePartecipanti, iscritti, stato) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, evento.getIdEvento());
            stmt.setString(2, evento.getIdOrganizzatore());   // idUtente
            stmt.setString(3, evento.getTitolo());
            stmt.setString(4, evento.getDescrizione());
            stmt.setString(5, evento.getData());
            stmt.setString(6, evento.getOrario());
            stmt.setString(7, evento.getLink());
            stmt.setString(8, evento.getNomeOrganizzatore());
            stmt.setString(9, evento.getCognomeOrganizzatore());
            stmt.setString(10, evento.getLimitePartecipanti());
            stmt.setInt(11, evento.getIscritti());
            stmt.setBoolean(12, evento.getStato());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
    }

    private static void aggiornaEventoInDb(Evento eventoAggiornato) throws DatabaseOperazioneFallitaException, DatabaseConnessioneFallitaException {
        String query = "UPDATE Eventi SET titolo = ?, descrizione = ?, data = ?, orario = ?, link = ?, limitePartecipanti = ?, iscritti = ?, stato = ?, idUtente = ? " +
                "WHERE idEvento = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Imposta i parametri nella query
            stmt.setString(1, eventoAggiornato.getTitolo());
            stmt.setString(2, eventoAggiornato.getDescrizione());
            stmt.setString(3, eventoAggiornato.getData());
            stmt.setString(4, eventoAggiornato.getOrario());
            stmt.setString(5, eventoAggiornato.getLink());
            stmt.setString(6, eventoAggiornato.getLimitePartecipanti());
            stmt.setInt(7, eventoAggiornato.getIscritti());
            stmt.setBoolean(8, eventoAggiornato.getStato());
            stmt.setString(9, eventoAggiornato.getIdOrganizzatore());
            stmt.setLong(10, eventoAggiornato.getIdEvento());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
    }

    // Elimina un evento dal database
    private static boolean eliminaEventoDaDb(long idEvento, String idUtente) throws DatabaseOperazioneFallitaException, DatabaseConnessioneFallitaException {
        String query = "DELETE FROM Eventi WHERE idEvento = ? AND idUtente = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, idEvento);
            stmt.setString(2, idUtente);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        } return false;
    }
    
    // Recupera un evento dal database tramite ID
    private static Evento recuperaEventoDaDb(long idEvento) throws DatabaseOperazioneFallitaException, DatabaseConnessioneFallitaException {
        String query = "SELECT titolo, descrizione, data, orario, limitePartecipanti, iscritti, link, " +
                "nomeOrganizzatore, cognomeOrganizzatore, stato, idEvento, idUtente " +
                "FROM Eventi WHERE idEvento = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, idEvento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return fromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
        return null;  // Evento non trovato nel database
    }

    // Recupera eventi da una data specifica dal database
    private static List<Evento> getEventoByDataDb(String data, String idOrganizzatore) throws DatabaseOperazioneFallitaException, DatabaseConnessioneFallitaException {
        String query = "SELECT titolo, descrizione, data, orario, limitePartecipanti, iscritti, link, " +
                "nomeOrganizzatore, cognomeOrganizzatore, stato, idEvento, idUtente " +
                "FROM Eventi WHERE data = ? AND idUtente = ?";
        List<Evento> eventi = new ArrayList<>();
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Imposta i parametri per la query
            stmt.setString(1, data); // La data come stringa in formato 'dd-MM-yyyy'
            stmt.setString(2, idOrganizzatore);

            // Esegue la query
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventi.add(fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
        return eventi;
    }

    /**
     * Crea un oggetto Evento a partire da un ResultSet.
     * Questo metodo viene utilizzato per evitare codice duplicato durante la costruzione di eventi
     * recuperati dal database.
     */
    private static Evento fromResultSet(ResultSet rs) throws SQLException {
        return new Evento(
                rs.getString("titolo"),
                rs.getString("descrizione"),
                rs.getString("data"),
                rs.getString("orario"),
                rs.getString("limitePartecipanti"),
                rs.getInt("iscritti"),
                rs.getString("link"),
                rs.getString("nomeOrganizzatore"),
                rs.getString("cognomeOrganizzatore"),
                rs.getBoolean("stato"),
                rs.getLong("idEvento"),
                rs.getString("idUtente")
        );
    }
}
