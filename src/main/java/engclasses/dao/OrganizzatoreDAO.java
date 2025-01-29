package engclasses.dao;

import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import model.Organizzatore;
import engclasses.pattern.Connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OrganizzatoreDAO {

    // Buffer per memorizzare temporaneamente gli organizzatori
    private static final Map<String, Organizzatore> bufferOrganizzatori = new HashMap<>();
    private static final Set<String> CAMPI_VALIDI = Set.of("username", "idUtente", "email");
    private static final String ERRORE_AGGIORNAMENTO_DB = "Errore durante l'aggiornamento del database";


    private OrganizzatoreDAO() {}

    // Aggiunge un organizzatore, scegliendo tra buffer o database in base al flag 'persistence'
    public static void aggiungiOrganizzatore(Organizzatore organizzatore, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            salvaInDb(organizzatore); // Salvataggio nel database
        } else {
            salvaInBuffer(organizzatore); // Salvataggio temporaneo nel buffer
        }
    }

    // Salva un organizzatore nel buffer temporaneo
    private static void salvaInBuffer(Organizzatore organizzatore) {
        bufferOrganizzatori.put(organizzatore.getIdUtente(), organizzatore);
        }

    // Salva un organizzatore nel database
    private static void salvaInDb(Organizzatore organizzatore) throws DatabaseOperazioneFallitaException, DatabaseConnessioneFallitaException {
        String query = "INSERT INTO Organizzatori (idUtente, nome, cognome, username, email, password, stato, titoloDiStudio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            setOrganizzatoreParameters(stmt, organizzatore);

            stmt.executeUpdate();
            } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
    }


    // Seleziona un organizzatore in base al campo specificato (es. username, idUtente)
    public static Organizzatore selezionaOrganizzatore(String campo, String valore, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            return recuperaDaDb(campo, valore); // Recupera dal database
        } else {
            return bufferOrganizzatori.get(valore); // Recupera dal buffer
        }
    }

    // Recupera un organizzatore dal database
    private static Organizzatore recuperaDaDb(String campo, String idUtente) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        // Controlla se il campo è valido
        if (!CAMPI_VALIDI.contains(campo)) {
            throw new IllegalArgumentException("Campo non valido: " + campo);
        }
        String query = "SELECT * FROM Organizzatori WHERE " + campo + " = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idUtente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Organizzatore(
                            rs.getString("idUtente"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getBoolean("stato"),
                            rs.getString("titoloDiStudio")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
        return null; // Nessun organizzatore trovato
    }

    // Aggiorna un organizzatore
    public static boolean aggiornaOrganizzatore(Organizzatore organizzatoreAggiornato, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            return aggiornaInDb(organizzatoreAggiornato);
        } else {
            return aggiornaInBuffer(organizzatoreAggiornato);
        }
    }

    private static boolean aggiornaInDb(Organizzatore organizzatoreAggiornato) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        String query = "UPDATE Organizzatori SET nome = ?, cognome = ?, username = ?, email = ?, password = ? WHERE idUtente = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            setUserParameters(stmt,
                    organizzatoreAggiornato.getNome(),
                    organizzatoreAggiornato.getCognome(),
                    organizzatoreAggiornato.getUsername(),
                    organizzatoreAggiornato.getEmail(),
                    organizzatoreAggiornato.getPassword(),
                    organizzatoreAggiornato.getIdUtente()
            );
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
        return false;
    }


    private static boolean aggiornaInBuffer(Organizzatore organizzatoreAggiornato) {
        String idUtente = organizzatoreAggiornato.getIdUtente();

        if (!bufferOrganizzatori.containsKey(idUtente)) {
            return false;
        }

        bufferOrganizzatori.put(idUtente, organizzatoreAggiornato);
        return true;
    }

    private static void setOrganizzatoreParameters(PreparedStatement stmt, Organizzatore organizzatore) throws SQLException {
        stmt.setString(1, organizzatore.getIdUtente());
        stmt.setString(2, organizzatore.getNome());
        stmt.setString(3, organizzatore.getCognome());
        stmt.setString(4, organizzatore.getUsername());
        stmt.setString(5, organizzatore.getEmail());
        stmt.setString(6, organizzatore.getPassword());
        stmt.setBoolean(7, organizzatore.isStato());
        stmt.setString(8, organizzatore.getTitoloDiStudio());
    }

    private static void setUserParameters(PreparedStatement stmt, String nome, String cognome, String username, String email, String password, String idUtente) throws SQLException {
        stmt.setString(1, nome);
        stmt.setString(2, cognome);
        stmt.setString(3, username);
        stmt.setString(4, email);
        stmt.setString(5, password);
        stmt.setString(6, idUtente);
    }
}
