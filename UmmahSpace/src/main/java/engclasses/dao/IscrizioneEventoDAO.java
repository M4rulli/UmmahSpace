package engclasses.dao;

import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.pattern.Connect;
import model.Evento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IscrizioneEventoDAO {

    private static final String ERRORE_AGGIORNAMENTO_DB = "Errore durante l'aggiornamento del database";

    private IscrizioneEventoDAO() { }

    // Metodo per ottenere tutti gli eventi (buffer o database)
    public static List<Evento> getEventiPerMeseAnno(int mese, int anno, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            // Recupera dal database
            return getEventiPerMeseAnnoDb(mese, anno);
        } else {
            // Recupera dal buffer
            return getEventiPerMeseAnnoBuffer(mese, anno);
        }
    }

    // Metodo per ottenere tutti gli eventi nel buffer
    public static List<Evento> getEventiPerMeseAnnoBuffer(int mese, int anno) {
        List<Evento> eventiPerMeseAnno = new ArrayList<>();
        for (Evento evento : GestioneEventoDAO.getEventiBuffer()) {
            String[] dataSplit = evento.getData().split("-");
            int eventoAnno = Integer.parseInt(dataSplit[0]);
            int eventoMese = Integer.parseInt(dataSplit[1]);

            if (eventoAnno == anno && eventoMese == mese) {
                eventiPerMeseAnno.add(evento);
            }
        }
        return eventiPerMeseAnno;
    }

    // Metodo per ottenere tutti gli eventi per un mese e anno dal database
    public static List<Evento> getEventiPerMeseAnnoDb(int mese, int anno) throws DatabaseOperazioneFallitaException, DatabaseConnessioneFallitaException {
        List<Evento> eventiPerMeseAnno = new ArrayList<>();
        String query = "SELECT idEvento, idUtente, titolo, descrizione, data, orario, link, " +
                "nomeOrganizzatore, cognomeOrganizzatore, limitePartecipanti, iscritti, stato " +
                "FROM Eventi WHERE MONTH(data) = ? AND YEAR(data) = ?";

        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Imposta i parametri della query
            stmt.setInt(1, mese);
            stmt.setInt(2, anno);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Evento evento = new Evento();
                    evento.setIdEvento(rs.getLong("idEvento"));
                    evento.setIdOrganizzatore(rs.getString("idUtente"));
                    evento.setTitolo(rs.getString("titolo"));
                    evento.setDescrizione(rs.getString("descrizione"));
                    evento.setData(rs.getString("data"));
                    evento.setOrario(rs.getString("orario"));
                    evento.setLink(rs.getString("link"));
                    evento.setNomeOrganizzatore(rs.getString("nomeOrganizzatore"));
                    evento.setCognomeOrganizzatore(rs.getString("cognomeOrganizzatore"));
                    evento.setLimitePartecipanti(rs.getString("limitePartecipanti"));
                    evento.setIscritti(rs.getInt("iscritti"));
                    evento.setStato(rs.getBoolean("stato"));

                    eventiPerMeseAnno.add(evento);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
        return eventiPerMeseAnno;
    }


    public static void aggiornaNumeroIscritti(long idEvento, int incremento, boolean persistence) throws DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        if (persistence) {
            aggiornaNumeroIscrittiNelDb(idEvento, incremento);
        } else {
            aggiornaNumeroIscrittiNelBuffer(idEvento, incremento);
        }
    }

    private static void aggiornaNumeroIscrittiNelDb(long idEvento, int incremento) throws DatabaseOperazioneFallitaException, DatabaseConnessioneFallitaException {
        String query = "UPDATE Eventi SET iscritti = iscritti + ? WHERE idEvento = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, incremento); // Incremento (può essere positivo o negativo)
            stmt.setLong(2, idEvento); // ID dell'evento

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperazioneFallitaException(ERRORE_AGGIORNAMENTO_DB, e);
        }
    }

    private static void aggiornaNumeroIscrittiNelBuffer(long idEvento, int incremento) {
        for (Evento evento : GestioneEventoDAO.getEventiBuffer()) { // Itera direttamente sulla lista
            if (evento.getIdEvento() == idEvento) {
                int nuoviIscritti = evento.getIscritti() + incremento;
                if (nuoviIscritti < 0) {
                    throw new IllegalArgumentException("Il numero di iscritti non può essere negativo.");
                }
                evento.setIscritti(nuoviIscritti); // Aggiorna il numero di iscritti
                return;
            }
        }
    }
}


