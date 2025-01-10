package model;

import java.util.List;

public class Organizzatore extends Utente {
    private List<Evento> listaEventi;

    // Costruttore
    public Organizzatore(String nome, String cognome, String username, String email, String password, Long idUtente, Boolean stato, List<Evento> listaEventi) {
        super(nome, cognome, username, email, password, idUtente, stato);
        this.listaEventi = listaEventi;
    }

    // Getter e Setter
    public List<Evento> getListaEventi() {

        return listaEventi;
    }

    public void setListaEventi(List<Evento> listaEventi) {
        this.listaEventi = listaEventi;
    }
}
