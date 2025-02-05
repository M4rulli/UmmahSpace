package controllers.grafico.cli;

import controllers.applicativo.GestioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.EventoNonTrovatoException;
import misc.Session;

import java.util.List;
import java.util.Scanner;

public class GestioneListaEventiCLIController {

    private final Session session;
    private final Scanner scanner = new Scanner(System.in);
    private final GestioneEventoController gestioneEventoController;
    private static final String MSG_SCELTA_NON_VALIDA = "❌ Scelta non valida.";

    public GestioneListaEventiCLIController(Session session) {
        this.session = session;
        this.gestioneEventoController = new GestioneEventoController(session);
    }

    public void mostraListaEventi() {
        System.out.println("\n=== 📅 I Tuoi Eventi ===");

        List<EventoBean> eventi;
        try {
            eventi = gestioneEventoController.getEventiOrganizzatore(session.getIdUtente(), session);
        } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException | EventoNonTrovatoException e) {
            System.out.println("⚠️ Errore nel recupero degli eventi: " + e.getMessage());
            return;
        }

        if (eventi.isEmpty()) {
            System.out.println("❌ Non hai ancora creato nessun evento.");
            return;
        }

        int index = 1;
        for (EventoBean evento : eventi) {
            System.out.println("\n" + index + ". 📅 " + evento.getData() + " - " + evento.getTitolo());
            mostraDettagliEvento(evento);
            index++;
        }

        System.out.println("\nCosa vuoi fare?");
        System.out.println("1. Gestisci un evento");
        System.out.println("2. Elimina un evento");
        System.out.println("3. Torna al menu principale");
        System.out.print("Seleziona un'opzione: ");

        int scelta = scanner.nextInt();
        scanner.nextLine(); // Consuma newline

        switch (scelta) {
            case 1 -> gestisciEvento(eventi);
            case 2 -> eliminaEvento(eventi);
            case 3 -> System.out.println("🔙 Ritorno al menu principale...");
            default -> System.out.println(MSG_SCELTA_NON_VALIDA);
        }
    }

    private void mostraDettagliEvento(EventoBean evento) {
        System.out.println("📌 Titolo: " + evento.getTitolo());
        System.out.println("📝 Descrizione: " + evento.getDescrizione());
        System.out.println("🕒 Orario: " + evento.getOrario());
        System.out.println("👥 Partecipanti: " + evento.getIscritti() + "/" + evento.getLimitePartecipanti());

        String stato = !evento.isStato() ? "Chiuso ❌" : (evento.isPieno() ? "Pieno ❌" : "Aperto ✅");
        System.out.println("📌 Stato: " + stato);
    }

    private void gestisciEvento(List<EventoBean> eventi) {
        System.out.print("Inserisci il numero dell'evento da gestire: ");
        int scelta = scanner.nextInt();
        scanner.nextLine();

        if (scelta < 1 || scelta > eventi.size()) {
            System.out.println(MSG_SCELTA_NON_VALIDA);
            return;
        }

        EventoBean evento = eventi.get(scelta - 1);
        session.setIdEvento(evento.getIdEvento());

        System.out.println("🔧 Aprendo la gestione dell'evento...");
        ModificaEventoCLIController modificaController = new ModificaEventoCLIController(session);
        modificaController.mostraMenuModificaEvento();
    }

    private void eliminaEvento(List<EventoBean> eventi) {
        System.out.print("Inserisci il numero dell'evento da eliminare: ");
        int scelta = scanner.nextInt();
        scanner.nextLine();

        if (scelta < 1 || scelta > eventi.size()) {
            System.out.println(MSG_SCELTA_NON_VALIDA);
            return;
        }

        EventoBean evento = eventi.get(scelta - 1);

        System.out.print("⚠️ Confermi l'eliminazione dell'evento '" + evento.getTitolo() + "'? (sì/no): ");
        String conferma = scanner.nextLine().trim().toLowerCase();

        if (!conferma.equals("si") && !conferma.equals("sì")) {
            System.out.println("🔙 Eliminazione annullata.");
            return;
        }

        try {
            boolean successo = gestioneEventoController.eliminaEvento(evento.getIdEvento(), session.getIdUtente());

            if (successo) {
                System.out.println("✅ Evento eliminato con successo!");
            } else {
                System.out.println("❌ Si è verificato un errore durante l'eliminazione.");
            }
        } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException | EventoNonTrovatoException e) {
            System.out.println("⚠️ Errore durante l'eliminazione: " + e.getMessage());
        }
    }
}