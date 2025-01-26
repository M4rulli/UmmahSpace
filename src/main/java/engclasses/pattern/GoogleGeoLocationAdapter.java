package engclasses.pattern;

import interfaces.GeoLocationProvider; // Target interface
import model.PosizioneGeografica; // Modello per rappresentare una posizione geografica

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

/**
 * Classe GoogleGeoLocationAdapter.
 * Implementa il pattern Adapter per adattare l'interfaccia di Google Geolocation API
 * (Adaptee) all'interfaccia standard dell'applicazione GeoLocationProvider (Target).
 */

public class GoogleGeoLocationAdapter implements GeoLocationProvider {

    /**
     * Metodo per ottenere la geolocalizzazione utilizzando l'API di Google.
     * Implementa l'interfaccia GeoLocationProvider, rendendo il sistema client
     * indipendente dai dettagli della Google API.
     */

    @Override
    public PosizioneGeografica getGeoLocation() {
        try {
            // Chiave API per l'accesso all'API di Google
            String apiKey = "API_KEY";
            String url = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + apiKey;

            // Corpo della richiesta per considerare l'IP del client
            String requestBody = "{\"considerIp\": true}";

            // Creazione della richiesta HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Client HTTP per inviare la richiesta
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parsing della risposta JSON per ottenere latitudine e longitudine
            JSONObject jsonObject = new JSONObject(response.body());
            JSONObject location = jsonObject.getJSONObject("location");

            double latitude = location.getDouble("lat");
            double longitude = location.getDouble("lng");

            // Restituisce la posizione geografica come oggetto PosizioneGeografica
            return new PosizioneGeografica(latitude, longitude);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nella geolocalizzazione con Google API", e);
        }
    }
}