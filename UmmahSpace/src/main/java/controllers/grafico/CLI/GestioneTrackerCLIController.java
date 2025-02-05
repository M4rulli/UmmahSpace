package controllers.grafico.CLI;

import controllers.applicativo.GestioneTrackerController;
import engclasses.beans.GestioneTrackerBean;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.TrackerNonTrovatoException;
import misc.Session;

import java.util.*;

public class GestioneTrackerCLIController {

    private final Session session;
    private final Scanner scanner = new Scanner(System.in);

    public GestioneTrackerCLIController(Session session) {
        this.session = session;
    }

    public void mostraMenuTracker() {
        while (true) {
            System.out.println("\n=== Tracker Spirituale ===");
            mostraProgressoLettura();

            System.out.println("\n1. Aggiungi Lettura Corano");
            System.out.println("2. Imposta Obiettivo di Lettura");
            System.out.println("3. Segna Preghiere");
            System.out.println("4. Registra Digiuno");
            System.out.println("5. Torna al Menu Principale");
            System.out.print("Seleziona un'opzione: ");

            int scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma il newline

            switch (scelta) {
                case 1:
                    aggiungiLettura();
                    break;
                case 2:
                    impostaObiettivo();
                    break;
                case 3:
                    salvaPreghiere();
                    break;
                case 4:
                    salvaDigiuno();
                    break;
                case 5:
                    return; // Torna al menu principale
                default:
                    System.out.println("❌ Scelta non valida. Riprova.");
            }
        }
    }

    private void mostraProgressoLettura() {
        try {
            GestioneTrackerController trackerController = new GestioneTrackerController(session);
            GestioneTrackerBean trackerBean = trackerController.aggiornaBarraConProgresso();

            int pagineLette = trackerBean.getLetturaCorano();
            int obiettivo = trackerBean.getGoal();
            double progresso = trackerBean.getProgresso();

            System.out.println("\n📖 Lettura del Corano");
            System.out.println("📄 Pagine lette: " + pagineLette + " / " + obiettivo);
            System.out.println("📊 Progresso: " + generaBarraProgresso(progresso));

        } catch (TrackerNonTrovatoException | DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException e) {
            System.out.println("❌ Errore nel recupero del progresso: " + e.getMessage());
        }
    }

    private String generaBarraProgresso(double progresso) {
        int lunghezzaBarra = 20; // Numero di blocchi nella barra
        int riempiti = (int) (progresso * lunghezzaBarra);
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < lunghezzaBarra; i++) {
            if (i < riempiti) {
                barra.append("█"); // Blocco pieno
            } else {
                barra.append("-"); // Blocco vuoto
            }
        }
        barra.append("] ").append((int) (progresso * 100)).append("%");
        return barra.toString();
    }

    private void aggiungiLettura() {
        System.out.print("\n📖 Quante pagine hai letto oggi? ");
        int pagine = scanner.nextInt();
        scanner.nextLine();

        if (pagine <= 0) {
            System.out.println("❌ Il numero di pagine deve essere maggiore di zero.");
            return;
        }

        try {
            GestioneTrackerBean trackerBean = new GestioneTrackerBean();
            trackerBean.setLetturaCorano(pagine);

            GestioneTrackerController trackerController = new GestioneTrackerController(session);
            GestioneTrackerBean updatedBean = trackerController.aggiungiLettura(trackerBean);
            session.setTracker(updatedBean);
            System.out.println("✅ Lettura aggiunta con successo!");
        } catch (Exception e) {
            System.out.println("❌ Errore durante l'aggiornamento: " + e.getMessage());
        }
    }

    private void impostaObiettivo() {
        System.out.print("\n🎯 Imposta il tuo obiettivo di lettura (pagine al giorno): ");
        int goal = scanner.nextInt();
        scanner.nextLine();

        if (goal <= 0 || goal > 604) {
            System.out.println("❌ L'obiettivo deve essere tra 1 e 604 pagine.");
            return;
        }

        try {
            GestioneTrackerBean trackerBean = new GestioneTrackerBean();
            trackerBean.setGoal(goal);

            GestioneTrackerController trackerController = new GestioneTrackerController(session);
            GestioneTrackerBean updatedBean = trackerController.setObiettivoGiornaliero(trackerBean);
            session.setTracker(updatedBean);
            System.out.println("✅ Obiettivo impostato con successo!");
        } catch (Exception e) {
            System.out.println("❌ Errore durante l'aggiornamento: " + e.getMessage());
        }
    }

    private void salvaPreghiere() {
        System.out.println("\n🙏 Segna le preghiere che hai eseguito oggi:");
        List<String> preghiere = List.of("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha");
        Map<String, Boolean> statiPreghiere = new HashMap<>();

        for (String preghiera : preghiere) {
            System.out.print(preghiera + " (sì/no): ");
            String risposta = scanner.nextLine().trim().toLowerCase();
            statiPreghiere.put(preghiera, risposta.equals("si") || risposta.equals("sì"));
        }

        try {
            GestioneTrackerBean trackerBean = new GestioneTrackerBean();
            statiPreghiere.forEach(trackerBean::setPreghiera);

            GestioneTrackerController trackerController = new GestioneTrackerController(session);
            GestioneTrackerBean updatedBean = trackerController.aggiornaPreghiere(trackerBean);
            session.setTracker(updatedBean);
            System.out.println("✅ Preghiere salvate con successo!");
        } catch (Exception e) {
            System.out.println("❌ Errore durante l'aggiornamento: " + e.getMessage());
        }
    }

    private void salvaDigiuno() {
        System.out.print("\n🌙 Hai digiunato oggi? (sì/no): ");
        boolean haDigiunato = scanner.nextLine().trim().equalsIgnoreCase("sì");

        System.out.print("✍️ Note sul digiuno (opzionale): ");
        String note = scanner.nextLine().trim();

        Set<String> motivazioni = new HashSet<>();
        if (haDigiunato) {
            System.out.print("📌 È un digiuno Sunnah? (sì/no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("sì")) motivazioni.add("Sunnah");

            System.out.print("📌 È un recupero di un digiuno? (sì/no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("sì")) motivazioni.add("Recupero di un digiuno");

            System.out.print("📌 È un digiuno volontario specifico? (sì/no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("sì")) motivazioni.add("Volontario Specifico");

            System.out.print("📌 È un digiuno volontario generale? (sì/no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("sì")) motivazioni.add("Volontario Generale");
        }

        try {
            GestioneTrackerBean trackerBean = new GestioneTrackerBean();
            trackerBean.setHaDigiunato(haDigiunato);
            trackerBean.setNoteDigiuno(note);
            trackerBean.setMotivazioniDigiuno(motivazioni);

            GestioneTrackerController trackerController = new GestioneTrackerController(session);
            GestioneTrackerBean updatedBean = trackerController.aggiornaDigiuno(trackerBean);
            session.setTracker(updatedBean);
            System.out.println("✅ Digiuno salvato con successo!");
        } catch (Exception e) {
            System.out.println("❌ Errore durante l'aggiornamento: " + e.getMessage());
        }
    }
}