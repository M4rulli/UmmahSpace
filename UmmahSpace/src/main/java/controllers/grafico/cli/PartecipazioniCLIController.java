package controllers.grafico.cli;

import controllers.applicativo.IscrizioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.EventoNonTrovatoException;
import misc.Session;

import java.util.List;
import java.util.Scanner;

public class PartecipazioniCLIController {

    private final Session session;
    private final Scanner scanner = new Scanner(System.in);
    private final IscrizioneEventoController iscrizioneEventoController;

    public PartecipazioniCLIController(Session session) {
        this.session = session;
        this.iscrizioneEventoController = new IscrizioneEventoController(session);
    }

    public void mostraPartecipazioni() {
        System.out.println("\n=== 🎟️ Eventi a cui sei iscritto ===");

        List<EventoBean> dettagliPartecipazioni;
        try {
            dettagliPartecipazioni = iscrizioneEventoController.getDettagliPartecipazioneUtente(session.getIdUtente());
        } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException | EventoNonTrovatoException e) {
            System.out.println("⚠️ Errore nel recupero delle partecipazioni: " + e.getMessage());
            return;
        }

        if (dettagliPartecipazioni.isEmpty()) {
            System.out.println("❌ Non sei iscritto ancora a nessun evento.");
            return;
        }

        int index = 1;
        for (EventoBean evento : dettagliPartecipazioni) {
            System.out.println("\n" + index + ". 📅 " + evento.getData() + " - " + evento.getTitolo());
            mostraDettagliEvento(evento);
            index++;
        }

        System.out.println("\nVuoi disiscriverti da un evento?");
        System.out.println("1. Sì");
        System.out.println("2. No (Torna al menu principale)");
        System.out.print("Seleziona un'opzione: ");

        int scelta = scanner.nextInt();
        scanner.nextLine(); // Consuma newline

        if (scelta == 1) {
            disiscrivitiDaEvento(dettagliPartecipazioni);
        } else {
            System.out.println("🔙 Ritorno al menu principale...");
        }
    }

    private void mostraDettagliEvento(EventoBean evento) {
        System.out.println("📌 Titolo: " + evento.getTitolo());
        System.out.println("📝 Descrizione: " + evento.getDescrizione());
        System.out.println("🕒 Orario: " + evento.getOrario());
        System.out.println("👤 Organizzatore: " + evento.getNomeOrganizzatore() + " " + evento.getCognomeOrganizzatore());
        System.out.println("👥 Partecipanti: " + evento.getIscritti() + "/" + evento.getLimitePartecipanti());

        if (evento.getLink() != null && !evento.getLink().trim().isEmpty()) {
            System.out.println("🔗 Link: " + evento.getLink());
        } else {
            System.out.println("🔗 Nessun link disponibile.");
        }

        System.out.println("📌 Stato: " + (evento.isStato() ? (evento.isPieno() ? "Pieno ❌" : "Aperto ✅") : "Chiuso ❌"));
    }

    private void disiscrivitiDaEvento(List<EventoBean> eventi) {
        System.out.print("Inserisci il numero dell'evento da cui vuoi disiscriverti: ");
        int scelta = scanner.nextInt();
        scanner.nextLine();

        if (scelta < 1 || scelta > eventi.size()) {
            System.out.println("❌ Scelta non valida.");
            return;
        }

        EventoBean evento = eventi.get(scelta - 1);

        System.out.print("⚠️ Confermi la disiscrizione da '" + evento.getTitolo() + "'? (sì/no): ");
        String conferma = scanner.nextLine().trim().toLowerCase();

        if (!conferma.equals("si") && !conferma.equals("sì")) {
            System.out.println("🔙 Disiscrizione annullata.");
            return;
        }

        boolean successo = iscrizioneEventoController.cancellaIscrizione(evento);

        if (successo) {
            System.out.println("✅ Disiscrizione completata con successo!");
        } else {
            System.out.println("❌ Si è verificato un errore durante la disiscrizione.");
        }
    }
}