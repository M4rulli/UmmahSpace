package misc;

import controllers.grafico.cli.RegistrazioneCLIController;
import engclasses.exceptions.ViewFactoryException;
import engclasses.pattern.Model;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("cli")) {
            avviaCLI();
        } else {
            launch(args); // Avvia la GUI
        }
    }

    @Override
    public void start(Stage primaryStage) throws ViewFactoryException {
        Session sessione = new Session(false);
        Model.getInstance().getViewFactory().showRegistration(sessione);
    }

    private static void avviaCLI() {
        Session sessione = new Session(false);
        RegistrazioneCLIController cliController = new RegistrazioneCLIController(sessione);
        cliController.mostraMenuRegistrazione();
    }
}