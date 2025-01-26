package model;

public class PosizioneGeografica {
    private double latitudine;
    private double longitudine;

    public PosizioneGeografica(double latitudine, double longitudine) {
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }
}
