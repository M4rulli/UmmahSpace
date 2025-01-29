package engclasses.pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private static final String BASE_URL = "https://api.aladhan.com/v1/timings";

    @Override
    public Map<String, LocalTime> getOrarioPreghiere(double latitude, double longitude, LocalDate date) throws HttpRequestException {
        try {
            HttpURLConnection conn = getHttpURLConnection(latitude, longitude, date);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                JSONObject timings = getJsonObject(conn);

                Map<String, LocalTime> result = new HashMap<>();
                result.put("Fajr", parseTime(timings.getString("Fajr")));
                result.put("Dhuhr", parseTime(timings.getString("Dhuhr")));
                result.put("Asr", parseTime(timings.getString("Asr")));
                result.put("Maghrib", parseTime(timings.getString("Maghrib")));
                result.put("Isha", parseTime(timings.getString("Isha")));

                conn.disconnect();
                return result;
            } else {
                conn.disconnect();
                throw new HttpRequestException("Errore dalla AlAdhan API, status code: " + responseCode);
            }
        } catch (IOException e) {
            throw new HttpRequestException("Errore durante la chiamata AlAdhan API", e);
        }
    }

    private static HttpURLConnection getHttpURLConnection(double latitude, double longitude, LocalDate date) throws IOException {
        String urlString = String.format(
                "%s/%d-%02d-%02d?latitude=%.6f&longitude=%.6f&method=2",
                BASE_URL,
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                latitude, longitude
        );

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        return conn;
    }

    private static JSONObject getJsonObject(HttpURLConnection conn) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        JSONObject json = new JSONObject(sb.toString());
        JSONObject data = json.getJSONObject("data");
        return data.getJSONObject("timings");
    }


    private LocalTime parseTime(String timeString) {
        String onlyTime = timeString.split(" ")[0];
        String[] parts = onlyTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return LocalTime.of(hour, minute);
    }
}