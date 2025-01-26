package engclasses.dao;

import model.Evento;
import misc.Connect;

import java.sql.*;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GestioneEventoDAO {
    private static final List<Evento> eventiBuffer = new ArrayList<>();
    private static final Map<Long, List<String>> partecipantiBuffer = new HashMap<>();

    // Popola inizialmente il buffer con eventi hard-coded
    static {
        eventiBuffer.add(new Evento("Evento 1", "Descrizione 1", "2025-01-01", "10:00", 50, 10, "www.evento1.com", "Mario", "Rossi", true, 1, ""));
        eventiBuffer.add(new Evento("Evento 2", "Descrizione 2", "2025-01-02", "15:00", 30, 5, "www.evento2.com", "Luca", "Bianchi", true, 2, ""));
        eventiBuffer.add(new Evento("Evento 3", "Descrizione 3", "2025-01-03", "09:00", 20, 15, "www.evento3.com", "Anna", "Verdi", true, 3, ""));
        eventiBuffer.add(new Evento("Evento 4", "Descrizione 4", "2025-01-03", "09:00", 20, 20, "www.evento4.com", "Romolo", "Remo", true, 4, ""));
        eventiBuffer.add(new Evento("Evento 5", "Descrizione 5", "2025-01-03", "09:00", 20, 20, "www.evento5.com", "Ciao", "Darwin", true, 5, ""));
    }
    // Recupera tutti gli eventi associati a un organizzatore (dal buffer o database)
    public static List<Evento> getEventiByOrganizzatore(String idUtente, boolean persistence) {
        if (persistence) {
            return recuperaEventiDaDb(idUtente);
        } else {
            return getEventiDalBuffer(idUtente);
        }
    }

    // Aggiunge un nuovo evento (buffer o database)
    public static boolean aggiungiEvento(Evento nuovoEvento, String idUtente, boolean persistence) {
        if (persistence) {
            return salvaEventoInDb(nuovoEvento, idUtente);
        } else {
            return salvaEventoNelBuffer(nuovoEvento, idUtente);
        }
    }

    // Aggiorna un evento esistente (buffer o database)
    public static boolean aggiornaEvento(Evento eventoAggiornato, boolean persistence) {
        if (persistence) {
            return aggiornaEventoInDb(eventoAggiornato);
        } else {
            return aggiornaEventoNelBuffer(eventoAggiornato);
        }
    }

    // Elimina un evento (buffer o database)
    public static boolean eliminaEvento(long idEvento, String idUtente, boolean persistence) {
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
            //if (Objects.equals(evento.getIdOrganizzatore(), idUtente)) {
                eventiPerOrganizzatore.add(evento);
            //}
        }
        return eventiPerOrganizzatore;
    }
    // Recupera un evento per ID (dal buffer o dal database)
    public static Evento getEventoById(long idEvento, boolean persistence) {
        if (persistence) {
            return recuperaEventoDaDb(idEvento);
        } else {
            return getEventoDalBuffer(idEvento);
        }
    }
    // Salva un evento nel buffer
    private static boolean salvaEventoNelBuffer(Evento nuovoEvento, String idUtente) {
        try {
            nuovoEvento.setIdOrganizzatore(idUtente);
            eventiBuffer.add(nuovoEvento);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Aggiorna un evento nel buffer
    private static boolean aggiornaEventoNelBuffer(Evento eventoAggiornato) {
        for (int i = 0; i < eventiBuffer.size(); i++) {
            Evento evento = eventiBuffer.get(i);
            if (evento.getIdEvento() == eventoAggiornato.getIdEvento()) {
                eventiBuffer.set(i, eventoAggiornato);
                return true;
            }
        }
        return false;
    }

    // Elimina un evento dal buffer.
    private static boolean eliminaEventoDalBuffer(long idEvento, String idUtente) {
        for (int i = 0; i < eventiBuffer.size(); i++) {
            Evento evento = eventiBuffer.get(i);
            //if (evento.getIdEvento() == idEvento && Objects.equals(evento.getIdOrganizzatore(), idUtente)) {
                eventiBuffer.remove(i);
                return true; // Ritorna true se l'evento è stato eliminato
            //}
        }
        return false; // Ritorna false se l'evento non è stato trovato o non è stato eliminato
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


    // ------------------ METODI DATABASE ------------------

    // Recupera eventi dal database
    private static List<Evento> recuperaEventiDaDb(String idUtente) {
        List<Evento> eventi = new ArrayList<>();
        String query = "SELECT * FROM Eventi WHERE idOrganizzatore = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idUtente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Evento evento = new Evento(
                            rs.getString("titolo"),
                            rs.getString("descrizione"),
                            rs.getString("data"),
                            rs.getString("ora"),
                            rs.getInt("limitePartecipanti"),
                            rs.getInt("iscritti"),
                            rs.getString("link"),
                            rs.getString("nomeOrganizzatore"),
                            rs.getString("cognomeOrganizzatore"),
                            rs.getBoolean("stato"),
                            rs.getLong("idEvento"),
                            rs.getString("idOrganizzatore")
                    );
                    eventi.add(evento);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventi;
    }

    // Salva un evento nel database
    private static boolean salvaEventoInDb(Evento evento, String idUtente) {
        String query = "INSERT INTO Eventi (idEvento,titolo, descrizione, data, ora, limitePartecipanti, iscritti,stato, idOrganizzatore) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, evento.getIdEvento());
            stmt.setString(2, evento.getTitolo());
            stmt.setString(3, evento.getDescrizione());
            stmt.setString(4, evento.getData());
            stmt.setString(5, evento.getOrario());
            stmt.setInt(6, evento.getLimitePartecipanti());
            stmt.setInt(7, evento.getIscritti());
            stmt.setBoolean(8, evento.getStato());
            stmt.setString(9, idUtente);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Aggiorna un evento nel database
    private static boolean aggiornaEventoInDb(Evento eventoAggiornato) {
        String query = "UPDATE Eventi SET titolo = ?, descrizione = ?, data = ?, ora = ?, limitePartecipanti = ?, iscritti = ?, stato = ? , idOrganizzatore = ?" +
                "WHERE idEvento = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(2, eventoAggiornato.getTitolo());
            stmt.setString(3, eventoAggiornato.getDescrizione());
            stmt.setString(4, eventoAggiornato.getData());
            stmt.setString(5, eventoAggiornato.getOrario());
            stmt.setInt(6, eventoAggiornato.getLimitePartecipanti());
            stmt.setInt(7, eventoAggiornato.getIscritti());
            stmt.setBoolean(8, eventoAggiornato.getStato());
            stmt.setString(9, eventoAggiornato.getIdOrganizzatore());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            } else {
                System.out.println("Errore: Nessuna riga aggiornata. Verifica l'ID Evento.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Errore durante l'aggiornamento dell'evento nel database.");
        return false;
    }

    // Elimina un evento dal database
    private static boolean eliminaEventoDaDb(long idEvento, String idUtente) {
        String query = "DELETE FROM Eventi WHERE idEvento = ? AND idOrganizzatore = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, idEvento);
            stmt.setString(2, idUtente);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                return true;
            } else {
                System.out.println("Errore: Nessuna riga aggiornata. Verifica l'ID Evento.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Errore durante l'eliminazione dell'evento nel database.");
        return false;
    }
    // Recupera un evento dal database tramite ID
    private static Evento recuperaEventoDaDb(long idEvento) {
        String query = "SELECT * FROM Eventi WHERE idEvento = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, idEvento);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Evento evento = new Evento(
                            rs.getString("titolo"),
                            rs.getString("descrizione"),
                            rs.getString("data"),
                            rs.getString("ora"),
                            rs.getInt("limitePartecipanti"),
                            rs.getInt("iscritti"),
                            rs.getString("link"),
                            rs.getString("nomeOrganizzatore"),
                            rs.getString("cognomeOrganizzatore"),
                            rs.getBoolean("stato"),
                            rs.getLong("idEvento"),
                            rs.getString("idOrganizzatore")
                    );
                    return evento;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Evento non trovato nel database
    }
}
