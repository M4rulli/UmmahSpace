module com.project.ummahspace {
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires org.controlsfx.controls;
    requires java.sql;
    requires java.net.http;
    requires org.json;
    requires org.girod.javafx.svgimage;
    requires io;
    requires kernel;
    requires javafx.swing;
    requires layout;

    exports misc; // Esporta il pacchetto principale per l'applicazione
    exports controllers.grafico;
    exports controllers.applicativo;
    exports engclasses.beans;

    opens controllers.grafico to javafx.fxml; // Permette a JavaFX di accedere ai controller grafici
    opens controllers.applicativo to javafx.fxml; // Se necessario
    opens engclasses.beans to javafx.fxml; // Se necessario

}
