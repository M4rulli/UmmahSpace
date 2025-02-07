package engclasses.pattern;

/**
 * La classe Model funge da punto centrale per la gestione dell'istanza
 * unica di ViewFactory, implementando il pattern Singleton.
 * Questo consente di garantire che vi sia una sola istanza della classe
 * ViewFactory durante l'intera esecuzione dell'applicazione,.

 * La classe offre un metodo statico sincronizzato per ottenere l'istanza
 * del Model e un metodo per accedere alla ViewFactory associata.
 */

public class Model {
    private static volatile Model model;
    private final ViewFactory viewFactory;

    // Costruttore privato per impedire l'istanziazione diretta
    protected Model() {
        this.viewFactory = new ViewFactory();
    }

    // Metodo Singleton con double-checked locking
    public static Model getInstance() {
        if (model == null) { // Primo controllo senza sincronizzazione
            synchronized (Model.class) { // Blocca l'accesso da altri thread
                if (model == null) { // Secondo controllo per evitare problemi di concorrenza
                    model = new Model();
                }
            }
        }
        return model;
    }

    // Metodo per ottenere la ViewFactory
    public ViewFactory getViewFactory() {
        return viewFactory;
    }
}
