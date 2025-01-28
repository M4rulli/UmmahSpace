package controllers.grafico;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import engclasses.beans.EventoBean;
import engclasses.beans.PartecipazioneBean;
import controllers.applicativo.GestioneEventoController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import misc.Model;
import misc.Session;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static misc.MessageUtils.mostraMessaggioConferma;
import static misc.MessageUtils.mostraMessaggioErrore;
import static utils.PDFExporter.export;

public class ReportGUIController {

    @FXML
    private Label titoloEventoLabel;
    @FXML
    private Label descrizioneEventoLabel;
    @FXML
    private Label eventTitleLabel;
    @FXML
    private Label eventDateLabel;
    @FXML
    private Label reportTimestampLabel;
    @FXML
    private TableView<PartecipazioneBean> participantsTable;
    @FXML
    private TableColumn<PartecipazioneBean, String> nameColumn;
    @FXML
    private TableColumn<PartecipazioneBean, String> surnameColumn;
    @FXML
    private TableColumn<PartecipazioneBean, String> emailColumn;
    @FXML
    private TableColumn<PartecipazioneBean, String> registrationDateColumn;
    @FXML
    private LineChart<String, Number> registrationLineChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Button printButton;
    @FXML
    private Button backButton;
    @FXML
    private VBox rootVBox;

    private final GestioneEventoController eventoController;

    public ReportGUIController(Session session) {
        this.eventoController = new GestioneEventoController(session);
    }

    @FXML
    public void initialize() {
        // Configura le colonne della tabella
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("Cognome"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("Email"));
        registrationDateColumn.setCellValueFactory(new PropertyValueFactory<>("DataIscrizione"));

        // Popola i campi dell'evento
        popolaCampiEvento();
        // Popola la tabella dei partecipanti
        popolaTabellaPartecipanti();
        // Configura il grafico
        popolaGraficoPartecipazioni();

        backButton.setOnAction(event -> onBackButtonClicked());
        printButton.setOnAction(event -> onPrintButtonClicked());
    }

    private void popolaCampiEvento() {
        EventoBean evento = eventoController.inizializzaEvento(); // Recupera i dettagli dell'evento
        eventTitleLabel.setText("Titolo Evento: " + evento.getTitolo());
        eventDateLabel.setText("Data Evento: " + evento.getData());
        reportTimestampLabel.setText("Generato il: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private void popolaTabellaPartecipanti() {
        List<PartecipazioneBean> partecipazioni = eventoController.getPartecipazioniEvento();
        ObservableList<PartecipazioneBean> observableList = FXCollections.observableArrayList(partecipazioni);
        participantsTable.setItems(observableList);
    }

    private void popolaGraficoPartecipazioni() {
        List<PartecipazioneBean> partecipazioni = eventoController.getPartecipazioniEvento();

        // Mappa per contare il numero di iscritti per data
        Map<String, Integer> iscrizioniPerData = new TreeMap<>(); // Ordine cronologico

        for (PartecipazioneBean partecipazione : partecipazioni) {
            String dataIscrizione = partecipazione.getDataIscrizione();
            iscrizioniPerData.put(dataIscrizione, iscrizioniPerData.getOrDefault(dataIscrizione, 0) + 1);
        }

        // Crea la serie per il grafico
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Iscrizioni nel Tempo");

        // Mantieni un conteggio cumulativo
        int iscrittiTotali = 0;
        for (Map.Entry<String, Integer> entry : iscrizioniPerData.entrySet()) {
            iscrittiTotali += entry.getValue();
            serie.getData().add(new XYChart.Data<>(entry.getKey(), iscrittiTotali));
        }

        // Aggiungi la serie al grafico
        registrationLineChart.getData().clear();
        registrationLineChart.getData().add(serie);
    }


    @FXML private void onBackButtonClicked() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
    }

    @FXML private void onPrintButtonClicked() {
        try {
            // Recupera i dettagli dell'evento dal controller applicativo
            EventoBean evento = eventoController.inizializzaEvento(); // Metodo per ottenere i dettagli dell'evento

            // Crea un nome file basato sul titolo dell'evento
            String safeTitle = evento.getTitolo()
                    .replaceAll("[^a-zA-Z0-9\\s]", "") // Rimuove caratteri speciali
                    .replace(" ", "_"); // Sostituisce spazi con "_"
            String outputPath = "report_" + safeTitle + ".pdf";

            // Chiamata al metodo di stampa
            export(rootVBox, outputPath); // rootVBox è il nodo principale da catturare

            // Mostra un messaggio di successo
            mostraMessaggioConferma("PDF Generato", "Il report è stato salvato con successo come " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
            mostraMessaggioErrore("Errore", "Si è verificato un errore durante la generazione del PDF.");
            }
    }
}
