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
    exports model;
    exports engclasses.exceptions;
    exports engclasses.pattern.interfaces;


    opens controllers.grafico to javafx.fxml;
    opens controllers.applicativo to javafx.fxml;
    opens engclasses.beans to javafx.fxml;
    exports engclasses.pattern;

}
