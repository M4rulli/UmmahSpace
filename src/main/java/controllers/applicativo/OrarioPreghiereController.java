package controllers.applicativo;

import interfaces.GeoLocationProvider;
import model.PosizioneGeografica;

public class OrarioPreghiereController {
    private final GeoLocationProvider geoLocationProvider;

    // Iniezione dell'adapter attraverso il costruttore
    public OrarioPreghiereController(GeoLocationProvider geoLocationProvider) {
        this.geoLocationProvider = geoLocationProvider;
    }

    // Metodo per calcolare gli orari delle preghiere
    public void calcolaOrariPreghiere() {
        try {
            // Ottieni la posizione geografica tramite il provider
            PosizioneGeografica posizione = geoLocationProvider.getGeoLocation();

            // Usa latitudine e longitudine per calcolare gli orari delle preghiere
            double latitudine = posizione.getLatitudine();
            double longitudine = posizione.getLongitudine();

            System.out.println("Latitudine: " + latitudine + ", Longitudine: " + longitudine);

            // Esegui il calcolo degli orari (da implementare)
        } catch (Exception e) {
            System.err.println("Errore durante il calcolo degli orari delle preghiere: " + e.getMessage());
        }
    }
}