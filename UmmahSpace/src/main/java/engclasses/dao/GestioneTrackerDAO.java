package engclasses.dao;

import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import engclasses.exceptions.CsvProcessingException;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.pattern.Connect;
import model.Tracker;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class GestioneTrackerDAO {

    // Buffer per memorizzare i tracker
    private static final Map<String, Tracker> trackerBuffer = new HashMap<>();
    private static final String ERRORE_AGGIORNAMENTO_DB = "Errore durante l'aggiornamento del database";
    private static final String CSV_FOLDER = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "UmmahSpace";
    private static final String CSV_FILE_PATH = CSV_FOLDER + File.separator + "trackers.csv";
    private static final String ERRORE_CSV = "Errore nel caricamento del Tracker da CSV";
    private static final String[] PREGHIERE = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};

    private GestioneTrackerDAO() {
    }

    public static Tracker getTracker(String idUtente, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        return persistence ? getTrackerFromDb(idUtente) : getTrackerFromBuffer(idUtente);
    }

    public static void saveOrUpdateTracker(Tracker tracker, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            saveOrUpdateTrackerInDb(tracker);
            saveOrUpdateTrackerInCsv(tracker);
        } else {
            saveOrUpdateTrackerInBuffer(tracker);
        }
    }

    private static Tracker getTrackerFromDb(String idUtente) throws DatabaseOperazioneFallitaException, DatabaseConnessioneFallitaException {
        String query = "SELECT letturaCorano, idUtente, goal, progress, haDigiunato, noteDigiuno, fajr, dhuhr, asr, maghrib, isha FROM Tracker WHERE idUtente = ?";
        try (Connection conn = Connect.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idUtente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Tracker tracker = new Tracker(rs.getInt("letturaCorano"), idUtente, rs.getInt("goal"), rs.getDouble("progress"));
                tracker.setHaDigiunato(rs.getBoolean("haDigiunato"));
                tracker.setNoteDigiuno(rs.getString("noteDigiuno"));

                // Recupera lo stato delle preghiere
                for (String preghiera : PREGHIERE) {
                    tracker.setPreghiera(preghiera, rs.getBoolean(preghiera.toLowerCase()));
                }
                return tracker;
            }
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
        return null;
    }

    private static void saveOrUpdateTrackerInDb(Tracker tracker) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        String query = "INSERT INTO Tracker (idUtente, letturaCorano, goal, progress, haDigiunato, noteDigiuno, fajr, dhuhr, asr, maghrib, isha) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE letturaCorano = VALUES(letturaCorano), goal = VALUES(goal), progress = VALUES(progress), haDigiunato = VALUES(haDigiunato), noteDigiuno = VALUES(noteDigiuno), fajr = VALUES(fajr), dhuhr = VALUES(dhuhr), asr = VALUES(asr), maghrib = VALUES(maghrib), isha = VALUES(isha)";

        try (Connection conn = Connect.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tracker.getIdUtente());
            stmt.setInt(2, tracker.getLetturaCorano());
            stmt.setInt(3, tracker.getGoal());
            stmt.setDouble(4, tracker.getProgresso());
            stmt.setBoolean(5, tracker.isHaDigiunato());
            stmt.setString(6, tracker.getNoteDigiuno());

            // Salva lo stato delle preghiere
            int index = 7;
            for (String preghiera : PREGHIERE) {
                stmt.setBoolean(index++, tracker.getPreghiera(preghiera));
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
    }

    private static Tracker getTrackerFromBuffer(String idUtente) {
        return trackerBuffer.get(idUtente);
    }

    private static void saveOrUpdateTrackerInBuffer(Tracker tracker) {
        trackerBuffer.put(tracker.getIdUtente(), tracker);
    }

    public static void saveOrUpdateTrackerInCsv(Tracker tracker) {
        ensureCsvFileExists();
        List<String[]> rows;
        boolean updated = false;
        try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
            rows = reader.readAll();
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i)[0].equals(tracker.getIdUtente())) {
                    rows.set(i, trackerToCsvRow(tracker));
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                rows.add(trackerToCsvRow(tracker));
            }
            try (CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE_PATH))) {
                writer.writeAll(rows);
            }
        } catch (IOException | CsvException e) {
            throw new CsvProcessingException(ERRORE_CSV, e);
        }
    }

    // Ora l'ID è la prima colonna del CSV
    private static String[] trackerToCsvRow(Tracker tracker) {
        String[] row = new String[6 + PREGHIERE.length];
        row[0] = tracker.getIdUtente();
        row[1] = String.valueOf(tracker.getLetturaCorano());
        row[2] = String.valueOf(tracker.getGoal());
        row[3] = String.valueOf(tracker.getProgresso());
        row[4] = String.valueOf(tracker.isHaDigiunato());
        row[5] = tracker.getNoteDigiuno();
        for (int i = 0; i < PREGHIERE.length; i++) {
            row[6 + i] = String.valueOf(tracker.getPreghiera(PREGHIERE[i]));
        }
        return row;
    }

    // Metodo per assicurarsi che il file CSV esista
    private static void ensureCsvFileExists() {
        try {
            Path folderPath = Paths.get(CSV_FOLDER);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            Path filePath = Paths.get(CSV_FILE_PATH);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                try (CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE_PATH))) {
                    writer.writeNext(new String[]{
                            "idUtente", "letturaCorano", "goal", "progress", "haDigiunato", "noteDigiuno",
                            "fajr", "dhuhr", "asr", "maghrib", "isha"
                    });
                }
            }
        } catch (IOException e) {
            throw new CsvProcessingException(ERRORE_CSV, e);
        }
    }
}