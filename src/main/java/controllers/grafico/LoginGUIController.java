package controllers.grafico;

import engclasses.dao.OrganizzatoreDAO;
import engclasses.dao.PartecipanteDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import misc.Model;
import misc.Session;

public class LoginGUIController {

    @FXML
    private Hyperlink registrationLink;
    private final OrganizzatoreDAO organizzatoreDAO;
    private final Session session;
    private final PartecipanteDAO partecipanteDAO;

    public LoginGUIController(Session session, PartecipanteDAO partecipanteDAO, OrganizzatoreDAO organizzatoreDAO) {
        this.partecipanteDAO = partecipanteDAO;
        this.session = session;
        this.organizzatoreDAO = organizzatoreDAO;
    }

    @FXML
    public void initialize() {
        // Imposta il listener sul link per la registrazione
        registrationLink.setOnAction(event -> onHyperLinkRegistrationClicked());
    }


    // Metodo che gestisce il click sull'hyperlink per la registrazione.
    @FXML
    private void onHyperLinkRegistrationClicked() {
        // Usa il Model per aprire la schermata di registrazione
        Model.getInstance().getViewFactory().showRegistration(session, partecipanteDAO, organizzatoreDAO);

        // Chiude la finestra attuale
        Stage stage = (Stage) registrationLink.getScene().getWindow();
        stage.close();
    }
}