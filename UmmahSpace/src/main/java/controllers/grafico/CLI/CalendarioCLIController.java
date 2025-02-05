package controllers.grafico.CLI;

import controllers.applicativo.IscrizioneEventoController;
import engclasses.beans.EventoBean;
import engclasses.exceptions.DatabaseConnessioneFallitaException;
import engclasses.exceptions.DatabaseOperazioneFallitaException;
import engclasses.exceptions.EventoNonTrovatoException;
import misc.Session;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class CalendarioCLIController {
    private YearMonth currentMonth;
    private final Scanner scanner = new Scanner(System.in);
    private final Session session;

    private static final String[] MESI = {
            "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
            "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"
    };

    private static final String[] GIORNI_SETTIMANA = {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"};

    public CalendarioCLIController(Session session) {
        this.session = session;
        this.currentMonth = YearMonth.now();
    }

    public void mostraCalendario() {
        while (true) {
            System.out.println("\n📅 Calendario - " + MESI[currentMonth.getMonthValue() - 1] + " " + currentMonth.getYear());
            stampaCalendario();

            System.out.println("\n🔹 Opzioni:");
            System.out.println("1. ⏪ Mese Precedente");
            System.out.println("2. ⏩ Mese Successivo");
            System.out.println("3. 📌 Seleziona un giorno");
            System.out.println("4. 🔙 Torna al Menu Principale");

            System.out.print("\nSeleziona un'opzione: ");
            int scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma il newline

            switch (scelta) {
                case 1:
                    currentMonth = currentMonth.minusMonths(1);
                    break;
                case 2:
                    currentMonth = currentMonth.plusMonths(1);
                    break;
                case 3:
                    System.out.print("\n📅 Inserisci il giorno (1-31): ");
                    int giorno = scanner.nextInt();
                    System.out.print("📆 Inserisci il mese (1-12): ");
                    int mese = scanner.nextInt();
                    System.out.print("📅 Inserisci l'anno: ");
                    int anno = scanner.nextInt();
                    scanner.nextLine(); // Consuma il newline

                    selezionaGiorno(giorno, mese, anno);
                    break;
                case 4:
                    return; // Esce dal loop e torna alla MainView
                default:
                    System.out.println("❌ Scelta non valida. Riprova.");
            }
        }
    }

    private void stampaCalendario() {
        IscrizioneEventoController eventoController = new IscrizioneEventoController(session);
        Map<Integer, List<EventoBean>> eventiDelMese = new HashMap<>();

        try {
            eventiDelMese = eventoController.getEventiDelMese(currentMonth.getMonthValue(), currentMonth.getYear());
        } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException | EventoNonTrovatoException e) {
            System.out.println("⚠️ Errore nel recupero eventi: " + e.getMessage());
        }

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1 = Lun, ..., 7 = Dom
        int daysInMonth = currentMonth.lengthOfMonth();

        System.out.println("\n  " + String.join("  ", GIORNI_SETTIMANA));

        int dayCounter = 1;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                if (row == 0 && col < firstDayOfWeek - 1) {
                    System.out.print("     "); // Spazi vuoti per giorni mancanti
                } else if (dayCounter <= daysInMonth) {
                    boolean hasEvent = eventiDelMese.containsKey(dayCounter);

                    if (hasEvent && !session.isOrganizzatore()) {
                        System.out.printf("🟡%2d ", dayCounter); // Giorni con eventi in oro
                    }
                    else {
                        System.out.printf(" %2d ", dayCounter);
                    }
                    dayCounter++;
                }
            }
            System.out.println();
        }
    }

    private void selezionaGiorno(int giorno, int mese, int anno) {
        System.out.println("\n📅 Selezionato giorno: " + giorno + " " + MESI[mese - 1] + " " + anno);
        String selectedDate = String.format("%04d-%02d-%02d", anno, mese, giorno);

        // Controllo per evitare che un organizzatore selezioni un giorno passato
        LocalDate today = LocalDate.now();
        LocalDate selected = LocalDate.of(anno, mese, giorno);

        if (session.isOrganizzatore() && selected.isBefore(today)) {
            System.out.println("⚠️ Non puoi selezionare una data passata per aggiungere eventi.");
            return;
        }

        // Ottieni gli eventi filtrati direttamente dal controller applicativo
        IscrizioneEventoController applicativoController = new IscrizioneEventoController(session);
        List<EventoBean> eventiDelGiorno;
        try {
            eventiDelGiorno = applicativoController.getEventiPerGiorno(giorno, mese, anno);
        } catch (DatabaseConnessioneFallitaException | DatabaseOperazioneFallitaException | EventoNonTrovatoException e) {
            System.out.println("⚠️ Errore nel recupero eventi: " + e.getMessage());
            return;
        }

        // Se non ci sono eventi in quel giorno
        if (eventiDelGiorno.isEmpty() && !session.isOrganizzatore()) {
            System.out.println("❌ Nessun evento trovato per questa data.");
            return;
        }

        // Salva la lista nella sessione
        session.setEventiDelGiorno(eventiDelGiorno);

        if (session.isOrganizzatore()) {
            // L'organizzatore può gestire gli eventi della data
            AggiungiEventoCLIController aggiungiEventoCLIController = new AggiungiEventoCLIController(session, selectedDate);
            aggiungiEventoCLIController.mostraMenuAggiuntaEvento();
        } else {
            // Il partecipante visualizza solo gli eventi disponibili
            EventiGiornalieriCLIController eventiController = new EventiGiornalieriCLIController(session);
            eventiController.mostraEventiDisponibili();
        }
    }
}