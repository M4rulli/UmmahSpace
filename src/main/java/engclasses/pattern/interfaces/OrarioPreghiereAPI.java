package engclasses.pattern.interfaces;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

/**
 * TARGET (Interfaccia) del pattern Adapter.

 * La nostra applicazione vuole un metodo "getPrayerTimes" che,
 * dato lat, long e la data, restituisce una mappa con gli orari
 * delle preghiere (chiave = nome preghiera, valore = orario).
 */

public interface OrarioPreghiereAPI {

    /**
     * Calcola o recupera gli orari delle preghiere per la latitudine,
     * longitudine e data specificate.
     */

    Map<String, LocalTime> getOrarioPreghiere(double latitude, double longitude, LocalDate date);
}