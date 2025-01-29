package engclasses.pattern;

import engclasses.exceptions.GeolocalizzazioneFallitaException;
import engclasses.exceptions.HttpRequestException;
import engclasses.pattern.interfaces.GeolocalizzazioneAPI;
import model.PosizioneGeografica;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

/**
 * Adapter per utilizzare l'API di ip-api.com per ottenere la geolocalizzazione basata sull'IP.
 */
public class GeolocalizzazioneIPAdapter implements GeolocalizzazioneAPI {

    @Override
    public PosizioneGeografica getGeolocalizzazione() throws GeolocalizzazioneFallitaException, HttpRequestException {
        // URL dell'API di ip-api
        String url = "http://ip-api.com/json/";

        // Creazione della richiesta HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Invia la richiesta utilizzando un client HTTP
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = inviaRichiesta(request, client);

        // Parsing della risposta JSON
        JSONObject jsonObject = new JSONObject(response.body());

        // Controlla se la richiesta è andata a buon fine
        if (!"success".equals(jsonObject.getString("status"))) {
            throw new GeolocalizzazioneFallitaException("Errore nell'ottenere la geolocalizzazione: " + jsonObject.getString("message"));
        }

        // Ottieni latitudine e longitudine dalla risposta
        double latitude = jsonObject.getDouble("lat");
        double longitude = jsonObject.getDouble("lon");

        // Restituisce l'oggetto PosizioneGeografica
        return new PosizioneGeografica(latitude, longitude);

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
