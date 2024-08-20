module com.onursedef.mopperjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires java.desktop;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.onursedef.mopperjavafx to javafx.fxml;
    exports com.onursedef.mopperjavafx;
}