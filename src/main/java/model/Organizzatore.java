package model;


public class Organizzatore extends Utente {

    private String titoloDiStudio;

    // Costruttore
    public Organizzatore(String idUtente, String nome, String cognome, String username, String email, String password, Boolean stato, String titoloDiStudio) {
        super(nome, cognome, username, email, password, idUtente, stato);
        this.titoloDiStudio = titoloDiStudio;
    }

    public String getTitoloDiStudio() {
        return titoloDiStudio;
    }

    public void setTitoloDiStudio(String titoloDiStudio) {
        this.titoloDiStudio = titoloDiStudio;
    }

}
