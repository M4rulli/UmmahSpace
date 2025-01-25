package misc;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.InputStream;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Inizializza gli oggetti necessari
        Session sessione = new Session(false);

        // Usa la ViewFactory per mostrare la finestra di registrazione
        Model.getInstance().getViewFactory().showRegistration(sessione);
    }

    public static void main(String[] args) {
        launch(args);
    }
}