package controllers.grafico.cli;

import controllers.applicativo.GestioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.exceptions.*;
import misc.Session;

import java.util.Scanner;

public class ModificaEventoCLIController {

    private final Session session;
    private final Scanner scanner = new Scanner(System.in);
    private final GestioneEventoController gestioneEventoController;
    private EventoBean eventoCorrente;

    public ModificaEventoCLIController(Session session) {
        this.session = session;
        this.gestioneEventoController = new GestioneEventoController(session);
    }

    public void mostraMenuModificaEvento() {
        try {
            eventoCorrente = gestioneEventoController.inizializzaEvento();
        } catch (EventoNonTrovatoException | DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException e) {
            System.out.println("⚠️ Errore nel recupero dell'evento: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("\n=== ✏️ Modifica Evento ===");
            mostraDettagliEvento(eventoCorrente);
            System.out.println("\n1. Modifica Titolo");
            System.out.println("2. Modifica Descrizione");
            System.out.println("3. Modifica Data");
            System.out.println("4. Modifica Orario");
            System.out.println("5. Modifica Link");
            System.out.println("6. Modifica Limite Partecipanti");
            System.out.println("7. Salva Modifiche");
            System.out.println("8. Chiudi Evento");
            System.out.println("9. Torna al menu principale");
            System.out.print("Seleziona un'opzione: ");

            int scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma newline

            switch (scelta) {
                case 1 -> eventoCorrente.setTitolo(leggiInput("Nuovo Titolo: "));
                case 2 -> eventoCorrente.setDescrizione(leggiInput("Nuova Descrizione: "));
                case 3 -> eventoCorrente.setData(leggiInput("Nuova Data (YYYY-MM-DD): "));
                case 4 -> modificaOrario();
                case 5 -> eventoCorrente.setLink(leggiInput("Nuovo Link: "));
                case 6 -> eventoCorrente.setLimitePartecipanti(leggiInput("Nuovo Limite Partecipanti: "));
                case 7 -> salvaModifiche();
                case 8 -> chiudiEvento();
                case 9 -> {
                    System.out.println("🔙 Tornando al menu principale...");
                    return;
                }
                default -> System.out.println("❌ Scelta non valida.");
            }
        }
    }

    private void mostraDettagliEvento(EventoBean evento) {
        System.out.println("📌 Titolo: " + evento.getTitolo());
        System.out.println("📝 Descrizione: " + evento.getDescrizione());
        System.out.println("📅 Data: " + evento.getData());
        System.out.println("🕒 Orario: " + evento.getOrario());
        System.out.println("🔗 Link: " + evento.getLink());
        System.out.println("👥 Limite Partecipanti: " + evento.getLimitePartecipanti());
    }

    private void modificaOrario() {
        System.out.print("⏰ Inserisci nuovo orario di inizio (HH:mm): ");
        String orarioInizio = scanner.nextLine().trim();
        System.out.print("⏰ Inserisci nuovo orario di fine (HH:mm): ");
        String orarioFine = scanner.nextLine().trim();
        eventoCorrente.setOrario(orarioInizio + " - " + orarioFine);
    }

    private void salvaModifiche() {
        try {
            gestioneEventoController.aggiornaEvento(eventoCorrente, session.getIdEvento());
            System.out.println("✅ Modifiche salvate con successo!");
        } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException |
                 EventoNonTrovatoException | ValidazioneEventoException e) {
            System.out.println("⚠️ Errore nel salvataggio: " + e.getMessage());
        }
    }

    private void chiudiEvento() {
        System.out.print("⚠️ Vuoi davvero chiudere questo evento? (sì/no): ");
        String conferma = scanner.nextLine().trim().toLowerCase();

        if (!conferma.equals("si") && !conferma.equals("sì")) {
            System.out.println("🔙 Chiusura annullata.");
            return;
        }

        try {
            gestioneEventoController.chiudiEvento();
            System.out.println("✅ L'evento è stato chiuso con successo.");
        } catch (Exception e) {
            System.out.println("⚠️ Errore nella chiusura dell'evento: " + e.getMessage());
        }
    }

    private String leggiInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}