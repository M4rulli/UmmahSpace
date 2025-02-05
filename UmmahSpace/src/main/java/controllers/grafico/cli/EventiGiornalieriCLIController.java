package controllers.grafico.cli;

import controllers.applicativo.IscrizioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.IscrizioneEventoException;
import engclasses.exceptions.UtenteNonTrovatoException;
import misc.Session;

import java.util.List;
import java.util.Scanner;

public class EventiGiornalieriCLIController {
    private final Session session;
    private final Scanner scanner = new Scanner(System.in);

    public EventiGiornalieriCLIController(Session session) {
        this.session = session;
    }

    public void mostraEventiDisponibili() {
        List<EventoBean> eventi = session.getEventiDelGiorno();

        System.out.println("\n📅 Eventi del giorno selezionato:");

        if (eventi.isEmpty()) {
            System.out.println("❌ Nessun evento disponibile per questa data.");
            return;
        }

        for (int i = 0; i < eventi.size(); i++) {
            System.out.println("\n🔹 Evento #" + (i + 1));
            stampaDettagliEvento(eventi.get(i));
        }

        // Offri la possibilità di iscriversi a un evento
        System.out.println("\n🔹 Inserisci il numero dell'evento a cui vuoi iscriverti (0 per tornare indietro): ");
        int scelta = scanner.nextInt();
        scanner.nextLine(); // Consuma il newline

        if (scelta > 0 && scelta <= eventi.size()) {
            onRegistratiButton(eventi.get(scelta - 1));
        } else if (scelta == 0) {
            System.out.println("🔙 Ritorno al calendario...");
        } else {
            System.out.println("❌ Scelta non valida.");
        }
    }

    private void stampaDettagliEvento(EventoBean evento) {
        System.out.println("📌 Titolo: " + evento.getTitolo());
        System.out.println("📜 Descrizione: " + evento.getDescrizione());
        System.out.println("⏰ Orario: " + evento.getOrario());
        System.out.println("👤 Organizzatore: " + evento.getNomeOrganizzatore() + " " + evento.getCognomeOrganizzatore());
        System.out.println("👥 Partecipanti: " + evento.getIscritti() + "/" + evento.getLimitePartecipanti());

        if (evento.getLink() != null && !evento.getLink().trim().isEmpty()) {
            System.out.println("🔗 Link: " + evento.getLink());
        } else {
            System.out.println("🔗 Nessun link disponibile.");
        }

        if (!evento.isStato()) {
            System.out.println("🔴 Stato: CHIUSO");
        } else if (evento.isPieno()) {
            System.out.println("🔴 Stato: PIENO");
        } else {
            System.out.println("🟢 Stato: APERTO");
        }
    }

    private void onRegistratiButton(EventoBean evento) {
        if (!evento.isStato()) {
            System.out.println("❌ Questo evento è chiuso. Non puoi iscriverti.");
            return;
        }
        if (evento.isPieno()) {
            System.out.println("❌ Questo evento è pieno. Non puoi iscriverti.");
            return;
        }

        System.out.print("❓ Confermi l'iscrizione a \"" + evento.getTitolo() + "\"? (sì/no): ");
        String risposta = scanner.nextLine().trim().toLowerCase();

        if (!risposta.equals("si") && !risposta.equals("sì")) {
            System.out.println("🔙 Iscrizione annullata.");
            return;
        }

        IscrizioneEventoController iscrizioneEventoController = new IscrizioneEventoController(session);
        try {
            boolean successo = iscrizioneEventoController.iscriviPartecipante(evento.getIdEvento());
            if (successo) {
                System.out.println("✅ Iscrizione completata con successo!");
            }
        } catch (IscrizioneEventoException | DatabaseConnessioneFallitaException |
                 DatabaseOperazioneFallitaException | UtenteNonTrovatoException e) {
            System.out.println("❌ Errore: " + e.getMessage());
        }
    }
}