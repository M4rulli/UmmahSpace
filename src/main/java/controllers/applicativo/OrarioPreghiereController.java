package controllers.applicativo;

import engclasses.exceptions.GeolocalizzazioneFallitaException;
import engclasses.exceptions.HttpRequestException;
import engclasses.pattern.interfaces.GeolocalizzazioneAPI;
import engclasses.pattern.interfaces.OrarioPreghiereAPI;
import model.PosizioneGeografica;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class OrarioPreghiereController {

    private List<Map.Entry<String, LocalTime>> orariOrdinati; // Lista ordinata delle preghiere

    /**
     * Costruttore della classe OrarioPreghiereController.
     * Ottiene automaticamente la posizione geografica utilizzando l'adapter fornito, poi la calcola con l'API degli orari.
     */
    public OrarioPreghiereController(GeolocalizzazioneAPI geoProvider, OrarioPreghiereAPI orarioPreghiere) throws GeolocalizzazioneFallitaException, HttpRequestException {
        // Ottieni la posizione geografica tramite l'adapter
        PosizioneGeografica posizione = geoProvider.getGeolocalizzazione();
        double latitude = posizione.getLatitudine();
        double longitude = posizione.getLongitudine();

        // Usa i dati di lat/long per ottenere gli orari delle preghiere per la data corrente
        Map<String, LocalTime> orariCalcolati = orarioPreghiere.getOrarioPreghiere(latitude, longitude, LocalDate.now());

        // Ordina gli orari calcolati
        inizializzaOrariOrdinati(orariCalcolati);
    }


    // Metodo per ordinare le preghiere
    private void inizializzaOrariOrdinati(Map<String, LocalTime> orariPreghiere) {
        orariOrdinati = orariPreghiere.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()) // Ordina per valore (LocalTime)
                .toList();
    }

    /**
     * Ritorna l'ultima preghiera passata rispetto all'orario attuale.
     */
    public Map.Entry<String, LocalTime> getPreghieraPassata() {
        LocalTime orarioAttuale = LocalTime.now(); // Ottieni l'orario attuale

        // Cicla in ordine inverso
        for (int i = orariOrdinati.size() - 1; i >= 0; i--) {
            Map.Entry<String, LocalTime> entry = orariOrdinati.get(i);
            if (entry.getValue().isBefore(orarioAttuale)) {
                return entry; // Restituisce la prima preghiera passata
            }
        }
        // Se nessuna preghiera è passata, restituisci l'ultima della giornata (ciclo)
        return orariOrdinati.get(orariOrdinati.size() - 1);
    }

    /**
     * Ritorna la prossima preghiera futura rispetto all'orario attuale.
     */
    public Map.Entry<String, LocalTime> getPreghieraFutura() {
        LocalTime orarioAttuale = LocalTime.now(); // Ottieni l'orario attuale

        // Cicla in ordine normale
        for (Map.Entry<String, LocalTime> entry : orariOrdinati) {
            if (entry.getValue().isAfter(orarioAttuale)) {
                return entry; // Restituisce la prima preghiera futura
            }
        }
        // Se nessuna preghiera è futura, restituisci la prima della giornata (ciclo)
        return orariOrdinati.get(0);
    }

}