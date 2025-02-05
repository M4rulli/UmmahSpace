package controllers.grafico.CLI;

import controllers.applicativo.RegistrazioneController;
import engclasses.beans.RegistrazioneBean;
import engclasses.exceptions.*;
import misc.Session;

import java.util.Objects;
import java.util.Scanner;

public class RegistrazioneCLIController {

    private final Session session;
    private final Scanner scanner = new Scanner(System.in);

    public RegistrazioneCLIController(Session session) {
        this.session = session;
    }

    public void mostraMenuRegistrazione() {
        while (true) { // 🌟 Loop principale del menu
            System.out.println("\n=== Benvenuto su UmmahSpace, registrati o accedi per continuare. ===");
            System.out.println("1. Registrati");
            System.out.println("2. Accedi");
            System.out.println("3. Attiva/Disattiva Persistenza (" + session.isPersistence() + ")");
            System.out.println("4. Esci");
            System.out.print("Seleziona un'opzione: ");

            int scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma il newline

            switch (scelta) {
                case 1:
                    try {
                        onSignUpClicked();
                    } catch (RegistrazioneFallitaException | DatabaseConnessioneFallitaException |
                             DatabaseOperazioneFallitaException | ViewFactoryException e) {
                        System.out.println("❌ Errore durante la registrazione: " + e.getMessage());
                    }
                    break;
                case 2:
                    onHyperLinkLoginClicked();
                    return; // 🔄 Torna al menu login senza reinvocare `mostraMenuRegistrazione()`
                case 3:
                    togglePersistenceCLI();
                    break; // 🔄 Il loop ristamperà il menu senza richiamarlo manualmente
                case 4:
                    System.out.println("🔚 Uscita dal programma.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("❌ Scelta non valida. Riprova.");
            }
        }
    }

    private void onSignUpClicked() throws RegistrazioneFallitaException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException, ViewFactoryException {
        boolean registrazioneRiuscita;
        while (true) {
            System.out.println("\n📌 Inserisci i tuoi dati per la registrazione o digita 'back' per tornare indietro:");

            if (!listenOrganizzatoreCLI()) return;

            String nome = leggiInput("Nome: ");
            if (nome.equals("back")) return; // 🔄 Torna al menu senza reinvocarlo

            String cognome = leggiInput("Cognome: ");
            if (cognome.equals("back")) return;

            String username = leggiInput("Username: ");
            if (username.equals("back")) return;

            String password = leggiInput("Password: ");
            if (password.equals("back")) return;

            String confirmPassword = leggiInput("Conferma Password: ");
            if (confirmPassword.equals("back")) return;

            String email = leggiInput("Email: ");
            if (email.equals("back")) return;

            String titoloDiStudio = null;
            if (session.isOrganizzatore()) {
                while (titoloDiStudio == null || titoloDiStudio.trim().isEmpty()) {
                    titoloDiStudio = leggiInput("Titolo di Studio (obbligatorio): ");

                    if (titoloDiStudio.equalsIgnoreCase("back")) return; // 🔄 Torna al menu principale

                    if (titoloDiStudio.trim().isEmpty()) {
                        System.out.println("❌ Il titolo di studio è obbligatorio per gli organizzatori. Riprova.");
                    }
                }
            }

            // Creazione del bean per la registrazione
            RegistrazioneBean registrazioneBean = new RegistrazioneBean();
            registrazioneBean.setNome(nome);
            registrazioneBean.setCognome(cognome);
            registrazioneBean.setUsername(username);
            registrazioneBean.setPassword(password);
            registrazioneBean.setConfirmPassword(confirmPassword);
            registrazioneBean.setEmail(email);
            registrazioneBean.setTitoloDiStudio(session.isOrganizzatore() && !Objects.requireNonNull(titoloDiStudio).isEmpty() ? titoloDiStudio : null);

            // Chiamata al Controller Applicativo per la registrazione
            RegistrazioneController registrazioneController = new RegistrazioneController(session);

            try {
                registrazioneRiuscita = registrazioneController.registraUtente(registrazioneBean, session.isPersistence());

                if (registrazioneRiuscita) {
                    System.out.println("\n✅ Registrazione completata con successo!\n");
                    MainViewCLIController mainViewCLIController = new MainViewCLIController(session);
                    mainViewCLIController.mostraMenuPrincipale();
                    return;
                } else {
                    System.out.println("\n❌ Registrazione fallita. Riprova.");
                }

            } catch (RegistrazioneFallitaException | DatabaseConnessioneFallitaException |
                     DatabaseOperazioneFallitaException | TrackerNonTrovatoException e) {
                System.out.println("\n❌ Errore durante la registrazione: " + e.getMessage());
                System.out.println("🔄 Riprova inserendo i dati correttamente.");
            }
        }
    }

    public void onHyperLinkLoginClicked() {
        session.setPersistence(true);
        System.out.println("\n🔄 Reindirizzamento al login...");
        LoginCLIController loginCLI = new LoginCLIController(session);
        loginCLI.mostraMenuLogin();
    }

    private String leggiInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("back")) {
            System.out.println("\n🔄 Ritorno al menu principale...");
            return "back";
        }
        return input;
    }

    private boolean listenOrganizzatoreCLI() {
        System.out.print("\nSei un organizzatore? (sì/no): ");
        String risposta = scanner.nextLine().trim().toLowerCase();

        if (risposta.equals("back")) return false;

        boolean isOrganizzatore = risposta.equals("si") || risposta.equals("sì");
        session.setIsOrganizzatore(isOrganizzatore);

        System.out.println("🔧 Stato organizzatore impostato a: " + (isOrganizzatore ? "✅ Sì" : "❌ No"));
        return true;
    }

    private void togglePersistenceCLI() {
        System.out.print("\nVuoi attivare la persistenza? (sì/no): ");
        String risposta = scanner.nextLine().trim().toLowerCase();

        boolean nuovaPersistenza = risposta.equals("si") || risposta.equals("sì");
        session.setPersistence(nuovaPersistenza);

        System.out.println("🔧 Persistenza impostata a: " + (nuovaPersistenza ? "✅ Attiva (Database)" : "❌ Disattiva (Buffer/CSV)"));
    }
}