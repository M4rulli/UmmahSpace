package controllers.grafico.cli;

import controllers.applicativo.RegistrazioneController;
import engclasses.beans.RegistrazioneBean;
import engclasses.exceptions.*;
import misc.Session;
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
                             DatabaseOperazioneFallitaException e) {
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

    private void onSignUpClicked() throws RegistrazioneFallitaException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        System.out.println("\n📌 Inserisci i tuoi dati per la registrazione o digita 'back' per tornare indietro:");

        if (!listenOrganizzatoreCLI()) return;

        try {
            RegistrazioneBean registrazioneBean = creaRegistrazioneBean();
            eseguiRegistrazione(registrazioneBean);
        } catch (NullPointerException ignored) {
            System.out.println("🔄 Operazione annullata, ritorno al menu.");
        }
    }

    /**
     * Metodo per leggere i campi obbligatori in modo unificato.
     */
    private String leggiCampoObbligatorio(String messaggio) {
        String valore;
        while (true) {
            valore = leggiInput(messaggio);
            if (valore.equalsIgnoreCase("back")) return null;
            if (!valore.trim().isEmpty()) return valore;
            System.out.println("❌ Questo campo è obbligatorio. Riprova.");
        }
    }

    /**
     * Metodo per gestire il titolo di studio solo per gli organizzatori.
     */
    private String leggiTitoloDiStudioSeOrganizzatore() {
        if (!session.isOrganizzatore()) return null;
        return leggiCampoObbligatorio("Titolo di Studio (obbligatorio): ");
    }

    /**
     * Crea e popola il bean di registrazione con i dati forniti dall'utente.
     */
    private RegistrazioneBean creaRegistrazioneBean() {
        String nome = leggiCampoObbligatorio("Nome: ");
        String cognome = leggiCampoObbligatorio("Cognome: ");
        String username = leggiCampoObbligatorio("Username: ");
        String password = leggiCampoObbligatorio("Password: ");
        String confirmPassword = leggiCampoObbligatorio("Conferma Password: ");
        String email = leggiCampoObbligatorio("Email: ");
        String titoloDiStudio = leggiTitoloDiStudioSeOrganizzatore();

        if (nome == null || cognome == null || username == null || password == null || confirmPassword == null || email == null)
            throw new NullPointerException();

        RegistrazioneBean registrazioneBean = new RegistrazioneBean();
        registrazioneBean.setNome(nome);
        registrazioneBean.setCognome(cognome);
        registrazioneBean.setUsername(username);
        registrazioneBean.setPassword(password);
        registrazioneBean.setConfirmPassword(confirmPassword);
        registrazioneBean.setEmail(email);
        registrazioneBean.setTitoloDiStudio(titoloDiStudio);

        return registrazioneBean;
    }

    /**
     * Gestisce la registrazione chiamando il controller applicativo.
     */
    private void eseguiRegistrazione(RegistrazioneBean registrazioneBean) throws RegistrazioneFallitaException, DatabaseConnessioneFallitaException, DatabaseOperazioneFallitaException {
        RegistrazioneController registrazioneController = new RegistrazioneController(session);

        if (registrazioneController.registraUtente(registrazioneBean, session.isPersistence())) {
            System.out.println("\n✅ Registrazione completata con successo!\n");
            new MainViewCLIController(session).mostraMenuPrincipale();
        } else {
            System.out.println("\n❌ Registrazione fallita. Riprova.");
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