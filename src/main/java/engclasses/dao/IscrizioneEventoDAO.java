package engclasses.dao;

import misc.Connect;
import model.Evento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IscrizioneEventoDAO {
    private static final List<Evento> eventiBuffer = new ArrayList<>();


    // Popola inizialmente il buffer con eventi hard-coded
    static {
        eventiBuffer.add(new Evento("Evento 1", "Descrizione 1", "2025-01-01", "10:00", "50", 10, "www.evento1.com", "Mario", "Rossi", true, 1, ""));
        eventiBuffer.add(new Evento("Evento 2", "Descrizione 2", "2025-01-02", "15:00", "30", 5, "www.evento2.com", "Luca", "Bianchi", true, 2, ""));
        eventiBuffer.add(new Evento("Evento 3", "Descrizione 3", "2025-01-03", "09:00", "20", 15, "www.evento3.com", "Anna", "Verdi", true, 3, ""));
        eventiBuffer.add(new Evento("Evento 4", "Descrizione 4", "2025-01-03", "09:00", "20", 20, "www.evento4.com", "Romolo", "Remo", true, 4, ""));
        eventiBuffer.add(new Evento("Evento 5", "Descrizione 5", "2025-01-03", "09:00", "20", 20, "www.evento5.com", "Ciao", "Darwin", true, 5, ""));
    }

    // Metodo per ottenere tutti gli eventi (buffer o database)
    public static List<Evento> getEventiPerMeseAnno(int mese, int anno, boolean persistence) {
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
        for (Evento evento : eventiBuffer) {
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
    public static List<Evento> getEventiPerMeseAnnoDb(int mese, int anno) {
        List<Evento> eventiPerMeseAnno = new ArrayList<>();
        String query = "SELECT * FROM Eventi WHERE MONTH(data) = ? AND YEAR(data) = ?";

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
            e.printStackTrace();
        }
        return eventiPerMeseAnno;
    }


    public static void aggiornaNumeroIscritti(long idEvento, int incremento, boolean persistence) {
        if (persistence) {
            aggiornaNumeroIscrittiNelDb(idEvento, incremento);
        } else {
            aggiornaNumeroIscrittiNelBuffer(idEvento, incremento);
        }
    }

    private static void aggiornaNumeroIscrittiNelDb(long idEvento, int incremento) {
        String query = "UPDATE Eventi SET iscritti = iscritti + ? WHERE idEvento = ?";
        try (Connection conn = Connect.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, incremento); // Incremento (può essere positivo o negativo)
            stmt.setLong(2, idEvento); // ID dell'evento

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'aggiornamento del numero di iscritti nel database.", e);
        }
    }

    private static void aggiornaNumeroIscrittiNelBuffer(long idEvento, int incremento) {
        for (Evento evento : eventiBuffer) { // Itera direttamente sulla lista
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


