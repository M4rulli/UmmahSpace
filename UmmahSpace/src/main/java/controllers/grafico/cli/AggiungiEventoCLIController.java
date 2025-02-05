package controllers.grafico.cli;

import controllers.applicativo.GestioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.ValidazioneEventoException;
import misc.Session;

import java.util.Scanner;

public class AggiungiEventoCLIController {
    private final Session session;
    private final String selectedDate;
    private final Scanner scanner = new Scanner(System.in);

    public AggiungiEventoCLIController(Session session, String selectedDate) {
        this.session = session;
        this.selectedDate = selectedDate;
    }

    public void mostraMenuAggiuntaEvento() {
        System.out.println("\n📝 Creazione di un nuovo evento per " + selectedDate);

        System.out.print("📌 Titolo: ");
        String titolo = scanner.nextLine().trim();
        if (titolo.equalsIgnoreCase("back")) return;

        System.out.print("📜 Descrizione: ");
        String descrizione = scanner.nextLine().trim();
        if (descrizione.equalsIgnoreCase("back")) return;

        System.out.print("⏰ Orario inizio (HH:MM): ");
        String orarioInizio = scanner.nextLine().trim();
        if (orarioInizio.equalsIgnoreCase("back")) return;

        System.out.print("⏰ Orario fine (HH:MM): ");
        String orarioFine = scanner.nextLine().trim();
        if (orarioFine.equalsIgnoreCase("back")) return;

        System.out.print("🔗 Link (opzionale, premi Invio per saltare): ");
        String link = scanner.nextLine().trim();

        System.out.print("👥 Limite partecipanti: ");
        int limitePartecipanti;
        try {
            limitePartecipanti = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Errore: Il limite partecipanti deve essere un numero.");
            return;
        }

        // Creazione del bean evento
        EventoBean evento = new EventoBean();
        evento.setTitolo(titolo);
        evento.setDescrizione(descrizione);
        evento.setOrario(orarioInizio + " - " + orarioFine);
        evento.setLink(link.isEmpty() ? null : link);
        evento.setLimitePartecipanti(String.valueOf(limitePartecipanti));
        evento.setData(selectedDate);
        evento.setNomeOrganizzatore(session.getNomeOrganizzatore());
        evento.setCognomeOrganizzatore(session.getCognomeOrganizzatore());

        // Invocazione del controller applicativo
        try {
            GestioneEventoController gestioneEventoController = new GestioneEventoController(session);
            gestioneEventoController.aggiungiEvento(evento);
            System.out.println("✅ Evento aggiunto con successo!");
        } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException | ValidazioneEventoException e) {
            System.out.println("❌ Errore durante la creazione dell'evento: " + e.getMessage());
        }
    }
}