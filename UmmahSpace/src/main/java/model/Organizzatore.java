package model;


public class Organizzatore extends Utente {

    private final String titoloDiStudio;

    // Costruttore
    public Organizzatore(String idUtente, String nome, String cognome, String username, String email, String password, String titoloDiStudio) {
        super(nome, cognome, username, email, password, idUtente);
        this.titoloDiStudio = titoloDiStudio;
    }

    public String getTitoloDiStudio() {
        return titoloDiStudio;
    }

}
