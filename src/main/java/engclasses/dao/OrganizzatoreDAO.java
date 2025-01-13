package engclasses.dao;

import model.Organizzatore;

import java.util.HashMap;
import java.util.Map;

public class OrganizzatoreDAO {

    // Buffer per memorizzare temporaneamente gli organizzatori
    private final Map<String, Organizzatore> bufferOrganizzatori;

    // Costruttore
    public OrganizzatoreDAO() {
        this.bufferOrganizzatori = new HashMap<>();
    }

    // Metodo per aggiungere un organizzatore
    public void aggiungiOrganizzatore(Organizzatore organizzatore, boolean persistence) {
        if (persistence) {
            salvaInDb(organizzatore);
        } else {
            salvaInBuffer(organizzatore);
        }
    }

    private void salvaInBuffer(Organizzatore organizzatore) {
        bufferOrganizzatori.put(organizzatore.getUsername(), organizzatore);
        System.out.println("Organizzatore salvato nel buffer: " + organizzatore.getUsername());
    }

    private void salvaInDb(Organizzatore organizzatore) {
        // Logica per salvare nel database (placeholder)
        System.out.println("Organizzatore salvato nel database: " + organizzatore.getUsername());
    }

    public Organizzatore selezionaOrganizzatore(String username, boolean persistence) {
        if (persistence) {
            return recuperaDaDb(username);
        } else {
            return bufferOrganizzatori.get(username);
        }
    }

    private Organizzatore recuperaDaDb(String username) {
        // Logica per recuperare dal database (placeholder)
        System.out.println("Organizzatore recuperato dal database: " + username);
        return null; // Placeholder
    }
}
