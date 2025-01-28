package engclasses.dao;

import model.Partecipante;
import engclasses.pattern.Connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PartecipanteDAO {

    // Buffer per memorizzare temporaneamente i partecipanti
    private static final Map<String, Partecipante> bufferPartecipanti = new HashMap<>();
    private static final Set<String> CAMPI_VALIDI = Set.of("username", "idUtente", "email");

    private PartecipanteDAO() {}

    // Aggiunge un partecipante, scegliendo tra buffer o database in base al flag 'persistence'
    public static void aggiungiPartecipante(Partecipante partecipante, boolean persistence) {
        if (persistence) {
            salvaInDb(partecipante); // Salvataggio nel database
        } else {
            salvaInBuffer(partecipante); // Salvataggio temporaneo nel buffer
        }
    }

    // Salva un partecipante nel buffer temporaneo
    private static void salvaInBuffer(Partecipante partecipante) {
        bufferPartecipanti.put(partecipante.getIdUtente(), partecipante);
    }

    // Salva un partecipante nel database
    private static void salvaInDb(Partecipante partecipante) {
        String query = "INSERT INTO Partecipanti (idUtente, nome, cognome, username, email, password, stato) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, partecipante.getIdUtente());
            stmt.setString(2, partecipante.getNome());
            stmt.setString(3, partecipante.getCognome());
            stmt.setString(4, partecipante.getUsername());
            stmt.setString(5, partecipante.getEmail());
            stmt.setString(6, partecipante.getPassword());
            stmt.setBoolean(7, partecipante.isStato());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Seleziona un partecipante in base al campo specificato (es. username, email, idUtente)
    public static Partecipante selezionaPartecipante(String campo, String valore, boolean persistence) {
        if (persistence) {
            // Recupera dal database
            return recuperaDaDb(campo, valore);
        } else {
            return bufferPartecipanti.get(valore);
        }
    }

    // Recupera un partecipante dal database in base al campo specificato
    private static Partecipante recuperaDaDb(String campo, String valore) {
        // Controlla se il campo è valido
        if (!CAMPI_VALIDI.contains(campo)) {
            throw new IllegalArgumentException("Campo non valido: " + campo);
        }
        String query = "SELECT * FROM Partecipanti WHERE " + campo + " = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query,
                     ResultSet.TYPE_SCROLL_INSENSITIVE, // Permette di scorrere il ResultSet in modo arbitrario
                     ResultSet.CONCUR_READ_ONLY)) {     // Solo lettura del ResultSet

            // Imposta il parametro della query
            stmt.setString(1, valore);
            ResultSet rs = stmt.executeQuery();

            // Se il partecipante esiste, lo crea e lo restituisce
            if (rs.first()) {
                return new Partecipante(
                        rs.getString("idUtente"),  // ID Utente
                        rs.getString("nome"),     // Nome
                        rs.getString("cognome"),  // Cognome
                        rs.getString("username"), // Username
                        rs.getString("email"),    // Email
                        rs.getString("password"), // Password
                        rs.getBoolean("stato")    // Stato
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean aggiornaPartecipante(Partecipante partecipanteAggiornato, boolean persistence) {
        if (persistence) {
            return aggiornaInDb(partecipanteAggiornato);
        } else {
            return aggiornaInBuffer(partecipanteAggiornato);
        }
    }

    private static boolean aggiornaInDb(Partecipante partecipanteAggiornato) {
        String query = "UPDATE partecipanti SET nome = ?, cognome = ?, username = ?, email = ?, password = ? WHERE idUtente = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Se la password è null, recuperala dal database
            if (partecipanteAggiornato.getPassword() == null) {
                String queryRecuperaPassword = "SELECT password FROM partecipanti WHERE idUtente = ?";
                try (PreparedStatement stmtRecuperaPassword = conn.prepareStatement(queryRecuperaPassword)) {
                    stmtRecuperaPassword.setString(1, partecipanteAggiornato.getIdUtente());
                    ResultSet rs = stmtRecuperaPassword.executeQuery();
                    if (rs.next()) {
                        partecipanteAggiornato.setPassword(rs.getString("password"));
                    } else {
                        return false; // Interrompi l'aggiornamento se il partecipante non è trovato
                    }
                }
            }

            // Imposta i parametri della query per l'aggiornamento
            stmt.setString(1, partecipanteAggiornato.getNome());
            stmt.setString(2, partecipanteAggiornato.getCognome());
            stmt.setString(3, partecipanteAggiornato.getUsername());
            stmt.setString(4, partecipanteAggiornato.getEmail());
            stmt.setString(5, partecipanteAggiornato.getPassword());
            stmt.setString(6, partecipanteAggiornato.getIdUtente());

            // Esegue l'update
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean aggiornaInBuffer(Partecipante partecipanteAggiornato) {
        String username = partecipanteAggiornato.getIdUtente();

        if (!bufferPartecipanti.containsKey(username)) {
            return false;
        }

        bufferPartecipanti.put(username, partecipanteAggiornato);
        return true;
    }
}