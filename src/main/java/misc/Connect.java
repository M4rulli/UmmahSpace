package misc;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * La classe Connect è responsabile della gestione della connessione al database
 * utilizzando il pattern Singleton. Garantisce che l'applicazione utilizzi
 * una singola istanza di connessione durante il ciclo di vita.
 */

public class Connect {
    private static Connect instance = null; // Istanza unica del Singleton
    private Connection conn = null; // Connessione al database

    private Connect() {}


    // Metodo per ottenere l'istanza Singleton della classe Connect.
    public static synchronized Connect getInstance() {
        if (instance == null) {
            instance = new Connect();
        }
        return instance;
    }

    /**
     * Metodo per ottenere la connessione al database. Se la connessione
     * non è ancora stata creata o è chiusa, viene inizializzata utilizzando
     * i parametri forniti nel file `db.properties`.
     */

    public synchronized Connection getConnection() {
        try {
            if (this.conn == null || this.conn.isClosed()) {
                // Carica il file di configurazione
                String dbConfigPath = System.getProperty("user.dir") + "/src/main/java/misc/config/db.properties";
                InputStream input = new FileInputStream(dbConfigPath);
                Properties properties = new Properties();

                properties.load(input);

                String connectionUrl = properties.getProperty("CONNECTION_URL");
                String user = properties.getProperty("USER");
                String password = properties.getProperty("PASSWORD");

                // Inizializza la connessione
                this.conn = DriverManager.getConnection(connectionUrl, user, password);
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la connessione al database", e);
        }
        return this.conn;
    }

    /**
     * Metodo per chiudere la connessione al database. Se la connessione è attiva,
     * viene chiusa e la variabile `conn` viene impostata a null per permettere
     * una futura reinizializzazione.
     */

    public synchronized void closeConnection() {
        if (this.conn != null) {
            try {
                this.conn.close();
                this.conn = null; // Prepara per una nuova connessione
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}