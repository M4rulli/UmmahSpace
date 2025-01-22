package misc;

/**
 * La classe Model funge da punto centrale per la gestione dell'istanza
 * unica di ViewFactory, implementando il pattern Singleton.
 * Questo consente di garantire che vi sia una sola istanza della classe
 * ViewFactory durante l'intera esecuzione dell'applicazione,.

 * La classe offre un metodo statico sincronizzato per ottenere l'istanza
 * del Model e un metodo per accedere alla ViewFactory associata.
 */

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;

    // Costruttore privato
    private Model() {
        this.viewFactory = new ViewFactory();
    }

    // Metodo per ottenere l'istanza singleton
    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    // Getter per la ViewFactory
    public ViewFactory getViewFactory() {
        return viewFactory;
    }
}
