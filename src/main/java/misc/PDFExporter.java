package misc;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class PDFExporter {

    private PDFExporter() {}

    /**
     * Esporta il contenuto di un nodo JavaFX in un file PDF.
     */
    public static void export(Node rootNode, String outputPath) throws Exception {
        // Nascondi i bottoni
        hideButtons(rootNode, true);

        // Aumenta la risoluzione dello snapshot
        double scale = 2.0; // Fattore di scala per aumentare la risoluzione
        WritableImage scaledImage = new WritableImage(
                (int) (rootNode.getBoundsInParent().getWidth() * scale),
                (int) (rootNode.getBoundsInParent().getHeight() * scale)
        );

        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(javafx.scene.transform.Transform.scale(scale, scale));
        params.setFill(Color.WHITE);

        // Cattura lo snapshot con la scala
        WritableImage snapshot = rootNode.snapshot(params, scaledImage);

        // Ripristina la visibilità dei bottoni
        hideButtons(rootNode, false);

        // Converte l'immagine in BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

        // Salva l'immagine in un array di byte
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        // Usa iText per creare il PDF
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputPath));
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Aggiungi l'immagine al PDF
        ImageData imageData = ImageDataFactory.create(imageBytes);
        Image pdfImage = new Image(imageData);
        document.add(pdfImage);

        // Chiudi il documento
        document.close();
    }

    /**
     * Nasconde o mostra i bottoni in un layout JavaFX.
     */
    private static void hideButtons(Node rootNode, boolean hide) {
        if (rootNode instanceof Parent) {
            for (Node child : ((Parent) rootNode).getChildrenUnmodifiable()) {
                if (child instanceof Button) {
                    child.setVisible(!hide);
                } else if (child instanceof Parent) {
                    hideButtons(child, hide); // Ricorsione per gestire layout nidificati
                }
            }
        }
    }
}