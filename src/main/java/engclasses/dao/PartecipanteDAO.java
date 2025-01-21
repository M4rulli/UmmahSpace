package engclasses.dao;

import model.Partecipante;

import java.util.HashMap;
import java.util.Map;

public class PartecipanteDAO {

    // Buffer per memorizzare temporaneamente i partecipanti
    private final Map<String, Partecipante> bufferPartecipanti;

    // Costruttore
    public PartecipanteDAO() {
        this.bufferPartecipanti = new HashMap<>();
    }

    // Metodo per aggiungere un partecipante
    public void aggiungiPartecipante(Partecipante partecipante, boolean persistence) {
        if (persistence) {
            salvaInDb(partecipante);
        } else {
            salvaInBuffer(partecipante);
        }
    }

    private void salvaInBuffer(Partecipante partecipante) {
        bufferPartecipanti.put(partecipante.getUsername(), partecipante);
        System.out.println("Partecipante salvato nel buffer: " + partecipante.getUsername());
    }

    private void salvaInDb(Partecipante partecipante) {
        // Logica per salvare nel database (placeholder)
        System.out.println("Partecipante salvato nel database: " + partecipante.getUsername());
    }

    public Partecipante selezionaPartecipante(String username, boolean persistence) {
        if (persistence) {
            return recuperaDaDb(username);
        } else {
            return bufferPartecipanti.get(username);
        }
    }

    private Partecipante recuperaDaDb(String username) {
        // Logica per recuperare dal database (placeholder)
        System.out.println("Partecipante recuperato dal database: " + username);
        return null; // Placeholder
    }

    public boolean updatePartecipanteUsername(String oldUsername, String newUsername) {
        Partecipante partecipante = bufferPartecipanti.remove(oldUsername);
        if (partecipante == null) {
            System.out.println("Errore: Partecipante non trovato nel buffer con username: " + oldUsername);
            return false;
        }

        partecipante.setUsername(newUsername);
        bufferPartecipanti.put(newUsername, partecipante);

        System.out.println("Username aggiornato nel buffer: " + newUsername);
        return true;
    }

}

