package controllers.grafico;

import engclasses.dao.PartecipanteDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import misc.Session;
import model.Partecipante;

public class MainViewGUIController {

    @FXML
    private Label welcomeLabel;

    private final Session session;
    private final PartecipanteDAO partecipanteDAO;
    private final String currentUsername;

    public MainViewGUIController(Session session, PartecipanteDAO partecipanteDAO, String username) {
        this.session = session;
        this.partecipanteDAO = partecipanteDAO;
        this.currentUsername = username;
    }

    public void initialize() {
        setWelcomeMessage(currentUsername);
    }

    public void setWelcomeMessage(String username) {
        boolean persistence = session.isPersistence();
        Partecipante partecipante = partecipanteDAO.selezionaPartecipante(username, persistence);

        if (partecipante != null) {
            welcomeLabel.setText("Benvenuto, " + partecipante.getNome() + "!");
        } else {
            System.out.println("Errore: Partecipante non trovato.");
            welcomeLabel.setText("Errore: Partecipante non trovato.");
        }
    }

    @FXML
    public void onProfileButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestisciProfilo.fxml"));
            loader.setControllerFactory(param -> new GestisciProfiloPartecipanteGUIController(session, partecipanteDAO, currentUsername));

            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestisci Profilo");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
