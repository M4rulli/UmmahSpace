package engclasses.dao;

import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import engclasses.exceptions.CsvProcessingException;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.pattern.Connect;
import model.Tracker;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestioneTrackerDAO {

    // Buffer per memorizzare i tracker
    private static final Map<String, Tracker> trackerBuffer = new HashMap<>();
    private static final String ERRORE_AGGIORNAMENTO_DB = "Errore durante l'aggiornamento del database";
    private static final String CSV_FOLDER = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "UmmahSpace";
    private static final String CSV_FILE_PATH = CSV_FOLDER + File.separator + "trackers.csv";
    private static final String ERRORE_CSV = "Errore nel caricamento del Tracker da CSV";

    private GestioneTrackerDAO() {}

    public static Tracker getTracker(String idUtente, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            return getTrackerFromDb(idUtente); // Recupera dal database
        } else {
            return getTrackerFromBuffer(idUtente); // Recupera dal buffer
        }
    }

    public static void saveOrUpdateTracker(Tracker tracker, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            // Scriviamo nel Database
            saveOrUpdateTrackerInDb(tracker);
            // Scriviamo nel CSV
            saveOrUpdateTrackerInCsv(tracker);
        } else {
            saveOrUpdateTrackerInBuffer(tracker);
        }
    }

    private static Tracker getTrackerFromDb(String idUtente) throws DatabaseOperazioneFallitaException, DatabaseConnessioneFallitaException {
        String query = "SELECT letturaCorano, idUtente, goal, progress, haDigiunato, noteDigiuno, " +
                "fajr, dhuhr, asr, maghrib, isha " +
                "FROM Tracker WHERE idUtente = ?";
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
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
        return null;
    }

    private static void saveOrUpdateTrackerInDb(Tracker tracker) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        String query = "INSERT INTO Tracker (idUtente, letturaCorano, goal, progress, haDigiunato, noteDigiuno, fajr, dhuhr, asr, maghrib, isha) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE letturaCorano = VALUES(letturaCorano), " +
                "goal = VALUES(goal), progress = VALUES(progress), haDigiunato = VALUES(haDigiunato), " +
                "noteDigiuno = VALUES(noteDigiuno), fajr = VALUES(fajr), dhuhr = VALUES(dhuhr), asr = VALUES(asr), maghrib = VALUES(maghrib), isha = VALUES(isha)";

        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tracker.getIdUtente());
            stmt.setInt(2, tracker.getLetturaCorano());
            stmt.setInt(3, tracker.getGoal());
            stmt.setDouble(4, tracker.getProgresso());
            stmt.setBoolean(5, tracker.isHaDigiunato());
            stmt.setString(6, tracker.getNoteDigiuno());
            stmt.setBoolean(7, tracker.getPreghiera("Fajr"));
            stmt.setBoolean(8, tracker.getPreghiera("Dhuhr"));
            stmt.setBoolean(9, tracker.getPreghiera("Asr"));
            stmt.setBoolean(10, tracker.getPreghiera("Maghrib"));
            stmt.setBoolean(11, tracker.getPreghiera("Isha"));

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

    // Metodo per salvare o aggiornare un Tracker nel CSV
    public static void saveOrUpdateTrackerInCsv(Tracker tracker) {
        ensureCsvFileExists();

        List<String[]> rows;
        boolean updated = false;
        try {
            // Leggi il file CSV esistente
            try (CSVReader reader = new CSVReader(new FileReader(CSV_FILE_PATH))) {
                rows = reader.readAll();
            }
            // Cerca e aggiorna il tracker esistente
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i)[1].equals(tracker.getIdUtente())) {
                    rows.set(i, trackerToCsvRow(tracker));
                    updated = true;
                    break;
                }
            }
            // Se il tracker non esiste, aggiungilo
            if (!updated) {
                rows.add(trackerToCsvRow(tracker));
            }
            // Scrivi il nuovo contenuto nel CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE_PATH))) {
                writer.writeAll(rows);
            }

        } catch (IOException | CsvException e) {
            throw new CsvProcessingException(ERRORE_CSV, e);
        }
    }

    // Metodo di supporto per convertire un Tracker in una riga CSV
    private static String[] trackerToCsvRow(Tracker tracker) {
        return new String[]{
                String.valueOf(tracker.getLetturaCorano()),
                tracker.getIdUtente(),
                String.valueOf(tracker.getGoal()),
                String.valueOf(tracker.getProgresso()),
                String.valueOf(tracker.isHaDigiunato()),
                tracker.getNoteDigiuno(),
                String.valueOf(tracker.getPreghiera("Fajr")),
                String.valueOf(tracker.getPreghiera("Dhuhr")),
                String.valueOf(tracker.getPreghiera("Asr")),
                String.valueOf(tracker.getPreghiera("Maghrib")),
                String.valueOf(tracker.getPreghiera("Isha"))
        };
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
                            "letturaCorano", "idUtente", "goal", "progress", "haDigiunato", "noteDigiuno",
                            "fajr", "dhuhr", "asr", "maghrib", "isha"
                    });
                }
            }
        } catch (IOException e) {
            throw new CsvProcessingException(ERRORE_CSV, e);
        }
    }


}
