package misc;

import controllers.grafico.RegistrazioneGUIController;
import engclasses.dao.PartecipanteDAO;
import engclasses.dao.OrganizzatoreDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Inizializza oggetti condivisi
        Session sessione = new Session(false); // Partenza con buffer
        PartecipanteDAO partecipanteDAO = new PartecipanteDAO();
        OrganizzatoreDAO organizzatoreDAO = new OrganizzatoreDAO();

        // Carica la vista iniziale
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/registrazione.fxml"));
        loader.setControllerFactory(param -> new RegistrazioneGUIController(sessione, partecipanteDAO, organizzatoreDAO));
        Parent root = loader.load();

        // Configura la scena
        Scene scene = new Scene(root);
        primaryStage.setTitle("UmmahSpace - Registrazione");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
