package interfaces;


// Il Target dell'adapter. L’interfaccia che il Client utilizza per accedere alla geolocalizzazione.

import model.PosizioneGeografica;

public interface GeoLocationProvider {
    PosizioneGeografica getGeoLocation();
}
