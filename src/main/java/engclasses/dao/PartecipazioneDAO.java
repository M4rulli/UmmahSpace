package engclasses.dao;

import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.pattern.Connect;
import model.Partecipazione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartecipazioneDAO {
    private static final List<Partecipazione> partecipazioniBuffer = new ArrayList<>();

    private PartecipazioneDAO() {}

    public static void salvaPartecipazione(Partecipazione partecipazione, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            // Salva nel database
            aggiungiPartecipazioneDb(partecipazione);
        } else {
            // Salva nel buffer
            aggiungiPartecipazioneBuffer(partecipazione);
        }
    }

    public static void aggiungiPartecipazioneBuffer(Partecipazione partecipazione) {
        partecipazioniBuffer.add(partecipazione);
        }

    public static void aggiungiPartecipazioneDb(Partecipazione partecipazione) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        String query = "INSERT INTO Partecipazioni (idUtente, idEvento, nome, cognome, username, email, dataIscrizione) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, partecipazione.getIdUtente());
            stmt.setLong(2, partecipazione.getIdEvento());
            stmt.setString(3, partecipazione.getNome());
            stmt.setString(4, partecipazione.getCognome());
            stmt.setString(5, partecipazione.getUsername());
            stmt.setString(6, partecipazione.getEmail());
            stmt.setString(7, partecipazione.getDataIscrizione());

            stmt.executeUpdate();
            } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException("Errore durante l'aggiornamento del database", e);
        }
    }

    // Metodo per verificare se un partecipante è iscritto a un evento (database o buffer)
    public static boolean isPartecipanteIscritto(String idUtente, long idEvento, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            // Verifica nel database
            return isPartecipanteIscrittoDb(idUtente, idEvento);
        } else {
            // Verifica nel buffer
            return isPartecipanteIscrittoNelBuffer(idUtente, idEvento);
        }
    }

    // Metodo per verificare se un partecipante è iscritto a uno specifico evento nel database
    public static boolean isPartecipanteIscrittoDb(String idUtente, long idEvento) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        String query = "SELECT COUNT(*) FROM Partecipazioni WHERE idUtente = ? AND idEvento = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idUtente);
            stmt.setLong(2, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Restituisce true se c'è almeno una corrispondenza
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException("Errore durante l'aggiornamento del database", e);
        }
        return false;
    }

    // Metodo per verificare se un partecipante è iscritto a uno specifico evento nel buffer
    private static boolean isPartecipanteIscrittoNelBuffer(String idUtente, long idEvento) {
        for (Partecipazione partecipazione : partecipazioniBuffer) {
            if (partecipazione.getIdUtente().equals(idUtente) && partecipazione.getIdEvento() == idEvento) {
                return true; // L'utente è iscritto a questo specifico evento
            }
        }
        return false; // Nessuna partecipazione trovata per l'evento
    }

    // Metodo hub per recuperare le partecipazioni
    public static List<Partecipazione> recuperaPartecipazioni(String idUtente, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            return recuperaPartecipazioniDaDb(idUtente);
        } else {
            return recuperaPartecipazioniDalBuffer(idUtente);
        }
    }

    // Metodo per recuperare le partecipazioni dal database
    private static List<Partecipazione> recuperaPartecipazioniDaDb(String idUtente) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        String query = "SELECT idUtente, idEvento, nome, cognome, username, email, dataIscrizione " +
                "FROM Partecipazioni WHERE idUtente = ?";
        List<Partecipazione> partecipazioni = new ArrayList<>();

        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idUtente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                partecipazioni.add(new Partecipazione(
                        rs.getString("idUtente"),
                        rs.getLong("idEvento"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("dataIscrizione")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException("Errore durante l'aggiornamento del database", e);
        }
        return partecipazioni;
    }

    private static List<Partecipazione> recuperaPartecipazioniDalBuffer(String idUtente) {
        List<Partecipazione> partecipazioni = new ArrayList<>();
        for (Partecipazione partecipazione : partecipazioniBuffer) {
            if (partecipazione.getIdUtente().equals(idUtente)) {
                partecipazioni.add(partecipazione);
            }
        }
        return partecipazioni;
    }

    // Metodo hub per recuperare le partecipazioni basato sull'ID evento
    public static List<Partecipazione> recuperaPartecipazioniPerEvento(long idEvento, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            return recuperaPartecipazioniDaDbPerEvento(idEvento);
        } else {
            return recuperaPartecipazioniDalBufferPerEvento(idEvento);
        }
    }

    // Metodo per recuperare le partecipazioni dal database basato sull'ID evento
    private static List<Partecipazione> recuperaPartecipazioniDaDbPerEvento(long idEvento) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        String query = "SELECT idUtente, idEvento, nome, cognome, username, email, dataIscrizione " +
                "FROM Partecipazioni WHERE idEvento = ?";
        List<Partecipazione> partecipazioni = new ArrayList<>();

        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, idEvento);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                partecipazioni.add(new Partecipazione(
                        rs.getString("idUtente"),
                        rs.getLong("idEvento"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("dataIscrizione")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException("Errore durante l'aggiornamento del database", e);
        }
        return partecipazioni;
    }

    // Metodo per recuperare le partecipazioni dal buffer basato sull'ID evento
    private static List<Partecipazione> recuperaPartecipazioniDalBufferPerEvento(long idEvento) {
        List<Partecipazione> partecipazioni = new ArrayList<>();
        for (Partecipazione partecipazione : partecipazioniBuffer) {
            if (partecipazione.getIdEvento() == idEvento) {
                partecipazioni.add(partecipazione);
            }
        }
        return partecipazioni;
    }

    public static boolean rimuoviPartecipazione(long idEvento, String idUtente, boolean persistence) throws DatabaseConnessioneFallitaException {
        if (persistence) {
            return rimuoviPartecipazioneDalDb(idEvento, idUtente);
        } else {
            return rimuoviPartecipazioneDalBuffer(idEvento, idUtente);
        }
    }

    private static boolean rimuoviPartecipazioneDalDb(long idEvento, String idUtente) throws DatabaseConnessioneFallitaException {
        String query = "DELETE FROM Partecipazioni WHERE idEvento = ? AND idUtente = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, idEvento);
            stmt.setString(2, idUtente);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private static boolean rimuoviPartecipazioneDalBuffer(long idEvento, String idUtente) {
        // Implementa la logica per rimuovere dal buffer
        return partecipazioniBuffer.removeIf(partecipazione ->
                partecipazione.getIdEvento() == idEvento && partecipazione.getIdUtente().equals(idUtente)
        );
    }
    
}
