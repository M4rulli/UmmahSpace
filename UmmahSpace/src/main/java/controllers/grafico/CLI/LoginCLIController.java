package controllers.grafico.CLI;

import controllers.applicativo.LoginController;
import engclasses.beans.GestioneTrackerBean;
import engclasses.beans.LoginBean;
import engclasses.exceptions.*;
import misc.Session;

import java.util.Scanner;

public class LoginCLIController {

    private final Session session;
    private final Scanner scanner = new Scanner(System.in);
    private static final GestioneTrackerBean ORGANIZZATORE_PLACEHOLDER = new GestioneTrackerBean();

    public LoginCLIController(Session session) {
        this.session = session;
    }

    public void mostraMenuLogin() {
        while (true) {
            System.out.println("\n=== Accedi al tuo spazio spirituale ===");
            System.out.println("1. Effettua Login");
            System.out.println("2. Torna alla Registrazione");
            System.out.println("3. Esci");
            System.out.print("Seleziona un'opzione: ");

            int scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma il newline

            switch (scelta) {
                case 1:
                    try {
                        onLoginClicked();
                    } catch (LoginFallitoException | DatabaseConnessioneFallitaException |
                             DatabaseOperazioneFallitaException | TrackerNonTrovatoException e) {
                        System.out.println("❌ Errore di Login: " + e.getMessage());
                    }
                    break;
                case 2:
                    onHyperLinkRegistrationClicked();
                    break;
                case 3:
                    System.out.println("🔚 Uscita dal programma.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("❌ Scelta non valida. Riprova.");
            }
        }
    }

    private void onHyperLinkRegistrationClicked() {
        // Forza lo stato della persistenza su false
        session.setPersistence(false);
        System.out.println("\n🔄 Reindirizzamento alla registrazione...");
        RegistrazioneCLIController registrazioneCLI = new RegistrazioneCLIController(session);
        registrazioneCLI.mostraMenuRegistrazione();
    }

    private void onLoginClicked() throws LoginFallitoException, DatabaseConnessioneFallitaException,
            DatabaseOperazioneFallitaException, TrackerNonTrovatoException {
        System.out.println("\n📌 Inserisci le tue credenziali:");

        listenOrganizzatoreCLI();

        String username = leggiInput("Username: ");
        String password = leggiInput("Password: ");

        // Crea la bean con i dati prelevati
        LoginBean loginBean = new LoginBean();
        loginBean.setUsername(username);
        loginBean.setPassword(password);

        // Invia i dati al controller applicativo
        LoginController loginController = new LoginController(session);
        GestioneTrackerBean trackerBean = loginController.effettuaLogin(loginBean, session.isPersistence());

        if (trackerBean != null && trackerBean != ORGANIZZATORE_PLACEHOLDER) {
            // Caso: login come partecipante
            session.setCurrentUsername(loginBean.getUsername());
            session.setTracker(trackerBean);
            System.out.println("\n✅ Login effettuato con successo!");
            mostraMenuPrincipale();
        } else if (trackerBean == ORGANIZZATORE_PLACEHOLDER) {
            // Caso: login come organizzatore
            session.setCurrentUsername(loginBean.getUsername());
            System.out.println("\n✅ Login effettuato con successo! (Modalità Organizzatore)");
            mostraMenuPrincipale();
        } else {
            System.out.println("\n❌ Credenziali errate. Riprova.");
        }
    }

    private void mostraMenuPrincipale() throws DatabaseConnessioneFallitaException, TrackerNonTrovatoException, DatabaseOperazioneFallitaException {
        MainViewCLIController mainViewCLIController = new MainViewCLIController(session);
        mainViewCLIController.mostraMenuPrincipale();
    }

    private String leggiInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private void listenOrganizzatoreCLI() {
        System.out.print("\nSei un organizzatore? (sì/no): ");
        String risposta = scanner.nextLine().trim().toLowerCase();

        boolean isOrganizzatore = risposta.equals("si") || risposta.equals("sì");
        session.setIsOrganizzatore(isOrganizzatore);

        System.out.println("🔧 Stato organizzatore impostato a: " + (isOrganizzatore ? "✅ Sì (Organizzatore)" : "❌ No (Partecipante)"));
    }
}