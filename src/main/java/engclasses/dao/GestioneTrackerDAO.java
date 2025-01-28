package engclasses.dao;

import misc.Connect;
import model.Tracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GestioneTrackerDAO {

    // Buffer per memorizzare i tracker
    private static final Map<String, Tracker> trackerBuffer = new HashMap<>();


    public static Tracker getTracker(String idUtente, boolean persistence) {
        if (persistence) {
            return getTrackerFromDb(idUtente); // Recupera dal database
        } else {
            return getTrackerFromBuffer(idUtente); // Recupera dal buffer
        }
    }

    public static void saveOrUpdateTracker(Tracker tracker, boolean persistence) {
        if (persistence) {
            saveOrUpdateTrackerInDb(tracker); // Salva o aggiorna nel database
        } else {
            saveOrUpdateTrackerInBuffer(tracker); // Salva o aggiorna nel buffer
        }
    }

    private static Tracker getTrackerFromDb(String idUtente) {
        String query = "SELECT * FROM Tracker WHERE idUtente = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idUtente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Tracker tracker = new Tracker(
                        rs.getInt("letturaCorano"),
                        idUtente,
                        rs.getInt("goal"),
                        rs.getDouble("progress")
                );
                tracker.setHaDigiunato(rs.getBoolean("haDigiunato"));
                tracker.setNoteDigiuno(rs.getString("noteDigiuno"));

                // Recupera lo stato delle preghiere
                tracker.setPreghiera("Fajr", rs.getBoolean("fajr"));
                tracker.setPreghiera("Dhuhr", rs.getBoolean("dhuhr"));
                tracker.setPreghiera("Asr", rs.getBoolean("asr"));
                tracker.setPreghiera("Maghrib", rs.getBoolean("maghrib"));
                tracker.setPreghiera("Isha", rs.getBoolean("isha"));

                return tracker;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void saveOrUpdateTrackerInDb(Tracker tracker) {
        String query = "INSERT INTO Tracker (idUtente, letturaCorano, goal, progress, haDigiunato, noteDigiuno, fajr, dhuhr, asr, maghrib, isha) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE letturaCorano = VALUES(letturaCorano), " +
                "goal = VALUES(goal), progress = VALUES(progress), haDigiunato = VALUES(haDigiunato), " +
                "noteDigiuno = VALUES(noteDigiuno), fajr = VALUES(fajr), dhuhr = VALUES(dhuhr), asr = VALUES(asr), maghrib = VALUES(maghrib), isha = VALUES(isha)";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tracker.getIdUtente());
            stmt.setInt(2, tracker.getLetturaCorano());
            stmt.setInt(5, tracker.getGoal());
            stmt.setDouble(6,tracker.getProgresso());
            stmt.setBoolean(7, tracker.isHaDigiunato());
            stmt.setString(8, tracker.getNoteDigiuno());
            stmt.setBoolean(9, tracker.getPreghiera("Fajr"));
            stmt.setBoolean(10, tracker.getPreghiera("Dhuhr"));
            stmt.setBoolean(11, tracker.getPreghiera("Asr"));
            stmt.setBoolean(12, tracker.getPreghiera("Maghrib"));
            stmt.setBoolean(13, tracker.getPreghiera("Isha"));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            }
    }

    private static Tracker getTrackerFromBuffer(String idUtente) {
        Tracker tracker = trackerBuffer.get(idUtente);
        if (tracker == null) {
            }
        return tracker;
    }

    private static void saveOrUpdateTrackerInBuffer(Tracker tracker) {
        trackerBuffer.put(tracker.getIdUtente(), tracker);
    }

}
