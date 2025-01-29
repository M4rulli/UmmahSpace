package engclasses.pattern.interfaces;


// Il Target dell'adapter. L’interfaccia che il Client utilizza per accedere alla geolocalizzazione.

import engclasses.exceptions.GeolocalizzazioneFallitaException;
import engclasses.exceptions.HttpRequestException;
import model.PosizioneGeografica;

public interface GeolocalizzazioneAPI {
    PosizioneGeografica getGeolocalizzazione() throws GeolocalizzazioneFallitaException, HttpRequestException;
}
