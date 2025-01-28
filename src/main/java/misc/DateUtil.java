package misc;

import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;

/**
 * La classe DateUtil fornisce utilità per lavorare con le date
 * sincronizzando il calendario gregoriano con quello islamico (Hijri).
 * Questa classe include metodi per ottenere una rappresentazione formattata
 * della data odierna in entrambi i calendari.
 */

public class DateUtil {

    private DateUtil() {}

    /**
     * Ottiene la data odierna sia nel calendario gregoriano che nel calendario Hijri.
     * Il formato delle date è sincronizzato e restituisce una stringa leggibile.
     *
     * @return Una stringa che rappresenta la data odierna nel formato:
     *         "Data: [data gregoriana] / [data Hijri]"
     *         Esempio: "Data: 13 Gennaio 2025 / Jumada al-Thani 1446"
     */

    public static String getSynchronizedDate() {
        // Ottiene la data odierna nel calendario gregoriano
        LocalDate todayGregorian = LocalDate.now();

        // Ottiene la data odierna nel calendario islamico (Hijri)
        HijrahDate todayHijri = HijrahDate.now();

        // Formatta la data gregoriana come "dd MMMM yyyy"
        String formattedGregorian = todayGregorian.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

        // Formatta la data Hijri come "MMMM yyyy"
        String formattedHijri = todayHijri.format(DateTimeFormatter.ofPattern("MMMM yyyy"));

        // Combina entrambe le date in un'unica stringa leggibile
        return "Data: " + formattedGregorian + " / " + formattedHijri;
    }
}
