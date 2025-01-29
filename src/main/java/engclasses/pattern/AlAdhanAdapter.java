package engclasses.pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import engclasses.exceptions.HttpRequestException;
import engclasses.pattern.interfaces.OrarioPreghiereAPI;
import org.json.JSONObject;

/**
 * ADAPTER (Classe) che implementa l'interfaccia "OrarioPreghiereAPI" (il TARGET),
 * ma internamente fa chiamate HTTP all'API AlAdhan (Adaptee esterno)
 * e ne "adatta" il risultato in una mappa di <String, LocalTime>.
 */

public class AlAdhanAdapter implements OrarioPreghiereAPI {

    // Base URL di AlAdhan per ottenere gli orari, es. /v1/timings/{date}?latitude=...&longitude=...&method=...
    private static final String BASE_URL = "https://api.aladhan.com/v1/timings";

    @Override
    public Map<String, LocalTime> getOrarioPreghiere(double latitude, double longitude, LocalDate date) {
        try {

            // Costruiamo la URL con i parametri (es. method=2 -> "Egyptian General Authority")
            String urlString = String.format(
                    "%s/%d-%02d-%02d?latitude=%.6f&longitude=%.6f&method=2",
                    BASE_URL,
                    date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                    latitude, longitude
            );

            URL url = new URL(urlString);

            // Usiamo HttpURLConnection per semplificare la chiamata HTTP GET
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // timeout connessione
            conn.setReadTimeout(5000);    // timeout lettura

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Lettura della risposta in formato JSON (stringa)
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                // Parsing del JSON usando json
                JSONObject json = new JSONObject(sb.toString());
                JSONObject data = json.getJSONObject("data");
                JSONObject timings = data.getJSONObject("timings");

                // Creiamo la mappa con i risultati (chiave: nome preghiera, valore: orario in LocalTime)
                Map<String, LocalTime> result = new HashMap<>();
                // Aggiungiamo le preghiere che ci interessano
                result.put("Fajr",    parseTime(timings.getString("Fajr")));
                result.put("Dhuhr",   parseTime(timings.getString("Dhuhr")));
                result.put("Asr",     parseTime(timings.getString("Asr")));
                result.put("Maghrib", parseTime(timings.getString("Maghrib")));
                result.put("Isha",    parseTime(timings.getString("Isha")));

                conn.disconnect();
                return result;
            } else {
                // Se la risposta non è 200, trattiamo come errore
                conn.disconnect();
                throw new RuntimeException("Errore dalla AlAdhan API, status code: " + responseCode);
            }

        } catch (Exception e) {
            throw new RuntimeException("Errore durante la chiamata AlAdhan API", e);
        }
    }

    /**
     * Converte la stringa "HH:mm" (o "HH:mm (CET)") in un LocalTime.
     * AlAdhan a volte include anche il fuso orario tra parentesi,
     * quindi facciamo uno split e prendiamo solo la parte "HH:mm".
     */

    private LocalTime parseTime(String timeString) {
        String onlyTime = timeString.split(" ")[0]; // es. "05:18"
        String[] parts = onlyTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return LocalTime.of(hour, minute);
    }

    private HttpResponse<String> inviaRichiesta(HttpRequest request, HttpClient client) throws HttpRequestException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Rilancia l'interruzione
            throw new HttpRequestException("Thread interrotto durante la richiesta HTTP", e);
        } catch (IOException e) {
            throw new HttpRequestException("Errore di I/O durante la richiesta HTTP", e);
        }
    }

}


