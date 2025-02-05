package controllers.grafico.CLI;

import controllers.applicativo.GestisciProfiloController;
import engclasses.beans.RegistrazioneBean;
import engclasses.exceptions.*;
import misc.Session;

import java.util.Scanner;

public class GestisciProfiloCLIController {

    private final Session session;
    private final GestisciProfiloController gestisciProfiloController;
    private final Scanner scanner = new Scanner(System.in);

    public GestisciProfiloCLIController(Session session) {
        this.session = session;
        this.gestisciProfiloController = new GestisciProfiloController(session);
    }

    public void mostraMenuGestioneProfilo() {
        try {
            RegistrazioneBean bean = gestisciProfiloController.inizializzaProfilo(session.getIdUtente());

            while (true) {
                System.out.println("\n=== ⚙️ Gestione Profilo ===");
                System.out.println("1. Modifica Nome (Attuale: " + bean.getNome() + ")");
                System.out.println("2. Modifica Cognome (Attuale: " + bean.getCognome() + ")");
                System.out.println("3. Modifica Username (Attuale: " + bean.getUsername() + ")");
                System.out.println("4. Modifica Email (Attuale: " + bean.getEmail() + ")");
                System.out.println("5. Modifica Password");
                System.out.println("6. Salva e Torna al Menù Principale");
                System.out.println("7. Annulla e Torna al Menù Principale");
                System.out.print("Seleziona un'opzione: ");

                int scelta = scanner.nextInt();
                scanner.nextLine(); // Consuma il newline

                switch (scelta) {
                    case 1 -> bean.setNome(leggiInput("Nuovo Nome: "));
                    case 2 -> bean.setCognome(leggiInput("Nuovo Cognome: "));
                    case 3 -> bean.setUsername(leggiInput("Nuovo Username: "));
                    case 4 -> bean.setEmail(leggiInput("Nuova Email: "));
                    case 5 -> modificaPassword(bean);
                    case 6 -> {
                        salvaModifiche(bean);
                        return;
                    }
                    case 7 -> {
                        System.out.println("🔙 Annullamento delle modifiche...");
                        return;
                    }
                    default -> System.out.println("❌ Opzione non valida, riprova.");
                }
            }
        } catch (UtenteNonTrovatoException | DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException |
                 ValidazioneProfiloException e) {
            System.out.println("⚠️ Errore nel recupero del profilo: " + e.getMessage());
        }
    }

    private void modificaPassword(RegistrazioneBean bean) throws DatabaseConnessioneFallitaException, ValidazioneProfiloException, DatabaseOperazioneFallitaException {
        System.out.print("Inserisci la tua password attuale: ");
        String currentPassword = scanner.nextLine().trim();

        System.out.print("Nuova Password: ");
        String newPassword = scanner.nextLine().trim();

        System.out.print("Conferma Nuova Password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("❌ Le password non coincidono.");
            return;
        }

        boolean success = gestisciProfiloController.aggiornaProfilo(bean, currentPassword, newPassword, confirmPassword);

        if (success) {
            System.out.println("✅ Password aggiornata con successo!");
        } else {
            System.out.println("❌ Errore nell'aggiornamento della password.");
        }
    }

    private void salvaModifiche(RegistrazioneBean bean) {
        try {
            boolean success = gestisciProfiloController.aggiornaProfilo(bean, null, null, null);

            if (success) {
                session.setNome(bean.getNome());
                session.setCurrentUsername(bean.getUsername());
                System.out.println("✅ Modifiche salvate con successo!");
            } else {
                System.out.println("❌ Errore nel salvataggio delle modifiche.");
            }
        } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException | ValidazioneProfiloException e) {
            System.out.println("⚠️ Errore nel salvataggio: " + e.getMessage());
        }
    }

    private String leggiInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}