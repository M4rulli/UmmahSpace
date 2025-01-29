package misc;

import engclasses.exceptions.ViewFactoryException;
import engclasses.pattern.Model;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws ViewFactoryException {

        Session sessione = new Session(false);
        Model.getInstance().getViewFactory().showRegistration(sessione);
    }

    public static void main(String[] args) {
        launch(args);
    }
}