package engclasses.dao;

import model.Organizzatore;
import misc.Connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrganizzatoreDAO {

    // Buffer per memorizzare temporaneamente gli organizzatori
    private static final Map<String, Organizzatore> bufferOrganizzatori = new HashMap<>();

    // Costruttore
    public OrganizzatoreDAO() {}

    // Aggiunge un organizzatore, scegliendo tra buffer o database in base al flag 'persistence'
    public static void aggiungiOrganizzatore(Organizzatore organizzatore, boolean persistence) {
        if (persistence) {
            salvaInDb(organizzatore); // Salvataggio nel database
        } else {
            salvaInBuffer(organizzatore); // Salvataggio temporaneo nel buffer
        }
    }

    // Salva un organizzatore nel buffer temporaneo
    private static void salvaInBuffer(Organizzatore organizzatore) {
        bufferOrganizzatori.put(organizzatore.getIdUtente(), organizzatore);
        System.out.println("Organizzatore salvato nel buffer: " + organizzatore.getIdUtente());
    }

    // Salva un organizzatore nel database
    private static void salvaInDb(Organizzatore organizzatore) {
        String query = "INSERT INTO Organizzatori (idUtente, nome, cognome, username, email, password, stato) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, organizzatore.getIdUtente());
            stmt.setString(2, organizzatore.getNome());
            stmt.setString(3, organizzatore.getCognome());
            stmt.setString(4, organizzatore.getUsername());
            stmt.setString(5, organizzatore.getEmail());
            stmt.setString(6, organizzatore.getPassword());
            stmt.setBoolean(7, organizzatore.isStato());

            stmt.executeUpdate();
            System.out.println("Organizzatore salvato nel database: " + organizzatore.getIdUtente());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Seleziona un organizzatore in base al campo specificato (es. username, idUtente)
    public static Organizzatore selezionaOrganizzatore(String campo, String valore, boolean persistence) {
        if (persistence) {
            return recuperaDaDb(campo, valore); // Recupera dal database
        } else {
            return bufferOrganizzatori.get(valore); // Recupera dal buffer
        }
    }

    // Recupera un organizzatore dal database in base al campo specificato
    private static Organizzatore recuperaDaDb(String campo, String valore) {
        String query = "SELECT * FROM Organizzatori WHERE " + campo + " = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query,
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY)) {

            stmt.setString(1, valore);
            ResultSet rs = stmt.executeQuery();

            if (rs.first()) {
                return new Organizzatore(
                        rs.getString("idUtente"),  // ID Utente
                        rs.getString("nome"),     // Nome
                        rs.getString("cognome"),  // Cognome
                        rs.getString("username"), // Username
                        rs.getString("email"),    // Email
                        rs.getString("password"), // Password
                        rs.getBoolean("stato")   // Stato
                );
            }
            System.out.println("Organizzatore non trovato nel database con " + campo + ": " + valore);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Aggiorna un organizzatore
    public static boolean aggiornaOrganizzatore(Organizzatore organizzatoreAggiornato, boolean persistence) {
        if (persistence) {
            return aggiornaInDb(organizzatoreAggiornato);
        } else {
            return aggiornaInBuffer(organizzatoreAggiornato);
        }
    }

    private static boolean aggiornaInDb(Organizzatore organizzatoreAggiornato) {
        String query = "UPDATE Organizzatori SET nome = ?, cognome = ?, username = ?, email = ?, password = ? WHERE idUtente = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, organizzatoreAggiornato.getNome());
            stmt.setString(2, organizzatoreAggiornato.getCognome());
            stmt.setString(3, organizzatoreAggiornato.getUsername());
            stmt.setString(4, organizzatoreAggiornato.getEmail());
            stmt.setString(5, organizzatoreAggiornato.getPassword());
            stmt.setString(6, organizzatoreAggiornato.getIdUtente());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            } else {
                System.out.println("Errore: Nessuna riga aggiornata. Verifica l'ID Utente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Errore durante l'aggiornamento dell'organizzatore nel database.");
        return false;
    }

    private static boolean aggiornaInBuffer(Organizzatore organizzatoreAggiornato) {
        String idUtente = organizzatoreAggiornato.getIdUtente();

        if (!bufferOrganizzatori.containsKey(idUtente)) {
            System.out.println("Errore: Organizzatore non trovato nel buffer.");
            return false;
        }

        bufferOrganizzatori.put(idUtente, organizzatoreAggiornato);
        System.out.println("Organizzatore aggiornato nel buffer: " + idUtente);
        return true;
    }
}
