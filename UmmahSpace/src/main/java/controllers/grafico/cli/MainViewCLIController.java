package controllers.grafico.cli;

import controllers.applicativo.OrarioPreghiereController;
import engclasses.exceptions.*;
import engclasses.pattern.AlAdhanAdapter;
import engclasses.pattern.GeolocalizzazioneIPAdapter;
import misc.Session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class MainViewCLIController {

    private final Session session;
    private final Scanner scanner = new Scanner(System.in);

    public MainViewCLIController(Session session) {
        this.session = session;
    }

    public void mostraMenuPrincipale() {
        while (true) {
            System.out.println("\n=== UmmahSpace ===");
            System.out.println("Benvenuto, " + session.getNome() + "!");
            System.out.println(getMessaggioMotivazionale());
            System.out.println("\n📆 Data attuale: " + getDataFormattata());

            // Mostra il menu con le opzioni disponibili
            System.out.println("\n1. Visualizza Calendario degli Eventi");
            if (!session.isOrganizzatore()) {
                System.out.println("2. Visualizza Tracker Spirituale");
            }
            System.out.println("3. Visualizza I Miei Eventi");
            System.out.println("4. Orari Preghiere");
            System.out.println("5. Impostazioni Profilo");
            System.out.println("6. Logout");
            System.out.println("7. Esci");
            System.out.print("Seleziona un'opzione: ");

            int scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma il newline

            switch (scelta) {
                case 1:
                    mostraCalendario();
                    break;
                case 2:
                    if (!session.isOrganizzatore()) {
                        mostraTrackerSpirituale();
                    } else {
                        System.out.println("❌ Opzione non disponibile per gli organizzatori.");
                    }
                    break;
                case 3:
                    mostraEventi();
                    break;
                case 4:
                    mostraOrariPreghiere();
                    break;
                case 5:
                    mostraProfilo();
                    break;
                case 6:
                    if (effettuaLogout()) return;
                    break;
                case 7:
                    System.out.println("🔚 Uscita dal programma.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("❌ Scelta non valida. Riprova.");
            }
        }
    }

    private void mostraCalendario() {
        System.out.println("\n📆 Mostrando il Calendario degli Eventi...");
        CalendarioCLIController calendario = new CalendarioCLIController(session);
        calendario.mostraCalendario();
    }

    private void mostraTrackerSpirituale()  {
        System.out.println("\n📖 Mostrando il Tracker Spirituale...");
        GestioneTrackerCLIController trackerCLI = new GestioneTrackerCLIController(session);
        trackerCLI.mostraMenuTracker();
    }

    private void mostraEventi() {
        System.out.println("\n📅 Mostrando la lista dei tuoi eventi...");
        if (!session.isOrganizzatore()) {
            PartecipazioniCLIController partecipazioni = new PartecipazioniCLIController(session);
            partecipazioni.mostraPartecipazioni();
        }
        else {
            GestioneListaEventiCLIController gestione = new GestioneListaEventiCLIController(session);
            gestione.mostraListaEventi();
        }
    }

    private void mostraOrariPreghiere() {
        try {
            OrarioPreghiereController orarioPreghiereController = new OrarioPreghiereController(new GeolocalizzazioneIPAdapter(), new AlAdhanAdapter());
            Map.Entry<String, LocalTime> preghieraPassata = orarioPreghiereController.getPreghieraPassata();
            Map.Entry<String, LocalTime> preghieraFutura = orarioPreghiereController.getPreghieraFutura();

            System.out.println("\n🕌 Orari Preghiere:");
            System.out.println("✅ Ultima preghiera: " + (preghieraPassata != null ? preghieraPassata.getKey() + " - " + preghieraPassata.getValue() : "Nessuna preghiera precedente"));
            System.out.println("🔜 Prossima preghiera: " + (preghieraFutura != null ? preghieraFutura.getKey() + " - " + preghieraFutura.getValue() : "Nessuna preghiera successiva"));

        } catch (GeolocalizzazioneFallitaException | HttpRequestException e) {
            System.out.println("❌ Errore nel recupero degli orari delle preghiere: " + e.getMessage());
        }
    }

    private void mostraProfilo() {
        System.out.println("\n⚙️  Aprendo le impostazioni del profilo...");
        GestisciProfiloCLIController gestisciProfiloCLIController = new GestisciProfiloCLIController(session);
        gestisciProfiloCLIController.mostraMenuGestioneProfilo();
    }

    private boolean effettuaLogout() {
        System.out.print("\nSei sicuro di voler effettuare il logout? (sì/no): ");
        String risposta = scanner.nextLine().trim().toLowerCase();

        if (risposta.equals("si") || risposta.equals("sì")) {
            System.out.println("\n🔄 Effettuando il logout...");
            Session newSession = new Session(true); // Resetta la sessione
            LoginCLIController loginCLI = new LoginCLIController(newSession);
            loginCLI.mostraMenuLogin();
            return true; // Uscire dal ciclo e tornare al login
        }
        return false;
    }

    private String getMessaggioMotivazionale() {
        String[] messages = {
                "As-Salamu Alaikum, ricorda: il tempo ben speso è una benedizione.",
                "Ogni evento pianificato è un'opportunità per fare del bene!",
                "Che Allah benedica la tua giornata e i tuoi sforzi.",
                "Pianifica oggi per creare un domani migliore.",
                "Allah ama chi usa saggiamente il proprio tempo. Organizzati al meglio!",
                "Ogni piccolo passo è un passo verso il successo.",
                "Il tempo è prezioso. Pianifica con saggezza e lascia il resto ad Allah."
        };
        Random random = new Random();
        return messages[random.nextInt(messages.length)];
    }

    private String getDataFormattata() {
        LocalDate dataCorrente = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ITALIAN);
        return dataCorrente.format(formatter);
    }
}