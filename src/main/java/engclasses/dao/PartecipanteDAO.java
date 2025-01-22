package engclasses.dao;

import model.Partecipante;
import misc.Connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PartecipanteDAO {

    // Buffer per memorizzare temporaneamente i partecipanti
    private static Map<String, Partecipante> bufferPartecipanti = new HashMap<>();

    // Costruttore
    public PartecipanteDAO() {}

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
        bufferPartecipanti.put(partecipante.getUsername(), partecipante);
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
            return recuperaDaDb(campo, valore); // Recupera dal database
        } else {
            // Cerca nel buffer
            for (Partecipante partecipante : bufferPartecipanti.values()) {
                if ((campo.equals("idUtente") && partecipante.getIdUtente().equals(valore)) ||
                        (campo.equals("username") && partecipante.getUsername().equals(valore)) ||
                        (campo.equals("email") && partecipante.getEmail().equals(valore))) {
                    return partecipante;
                }
            }
            System.out.println("Partecipante non trovato nel buffer con " + campo + ": " + valore);
            return null;
        }
    }

    // Recupera un partecipante dal database in base al campo specificato
    private static Partecipante recuperaDaDb(String campo, String valore) {
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
            System.out.println("Partecipante non trovato nel database con " + campo + ": " + valore);
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
                        System.out.println("Errore: Partecipante non trovato con ID: " + partecipanteAggiornato.getIdUtente());
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

            // Debug: stampa i dati da aggiornare
            System.out.println("Dati aggiornamento:");
            System.out.println("Nome: " + partecipanteAggiornato.getNome());
            System.out.println("Cognome: " + partecipanteAggiornato.getCognome());
            System.out.println("Username: " + partecipanteAggiornato.getUsername());
            System.out.println("Email: " + partecipanteAggiornato.getEmail());
            System.out.println("Password: " + partecipanteAggiornato.getPassword());
            System.out.println("ID Utente: " + partecipanteAggiornato.getIdUtente());

            // Esegue l'update
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Partecipante aggiornato nel database: " + partecipanteAggiornato.getUsername());
                return true;
            } else {
                System.out.println("Errore: Nessuna riga aggiornata. Verifica l'ID Utente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Errore durante l'aggiornamento del partecipante nel database.");
        return false;
    }

    private static boolean aggiornaInBuffer(Partecipante partecipanteAggiornato) {
        String username = partecipanteAggiornato.getUsername();

        if (!bufferPartecipanti.containsKey(username)) {
            System.out.println("Errore: Partecipante non trovato nel buffer.");
            return false;
        }

        bufferPartecipanti.put(username, partecipanteAggiornato);
        System.out.println("Partecipante aggiornato nel buffer: " + username);
        return true;
    }
}