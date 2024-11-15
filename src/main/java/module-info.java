module com.project.ummahspace {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;


    opens com.project.ummahspace to javafx.fxml, javafx.graphics;
    exports com.project.ummahspace;
}
