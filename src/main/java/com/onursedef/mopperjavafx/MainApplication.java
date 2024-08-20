package com.onursedef.mopperjavafx;

import com.onursedef.mopperjavafx.components.Card;
import com.onursedef.mopperjavafx.components.Modal;
import com.onursedef.mopperjavafx.components.OrganizationCard;
import com.onursedef.mopperjavafx.components.TopBarComponent;
import com.onursedef.mopperjavafx.model.Organizer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        System.setProperty("prism.lcdtext", "false");
        AnchorPane pane = new AnchorPane();
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: #2A4365");
        vbox.setSpacing(15);
        TopBarComponent topBar = new TopBarComponent();
        FlowPane flowPane = new FlowPane(15, 15);
        flowPane.setPadding(new Insets(0, 20, 10, 20));
        vbox.getChildren().add(topBar);
        Card card = new Card("Add New Group", null);
        Modal modal = new Modal(
                null,
                pane,
                flowPane,
                card,
                null,
                stage);
        List<Organizer> organizers = getConnection();
        for (Organizer organizer : organizers) {
            OrganizationCard card1 = new OrganizationCard(organizer, pane, flowPane, stage);
            flowPane.getChildren().add(card1);
        }
        flowPane.getChildren().add(card);
        vbox.getChildren().add(flowPane);
        vbox.setLayoutX(0);
        vbox.setLayoutY(0);
        pane.getChildren().add(vbox);
        // show modal on card click
        card.setOnMouseClicked(_ -> pane.getChildren().add(modal));

        vbox.prefWidthProperty().bind(pane.widthProperty());
        vbox.prefHeightProperty().bind(pane.heightProperty());
        Scene scene = new Scene(pane, 1280, 720);

        loadFonts();

        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        stage.show();
    }

    private static void loadFonts() {
        Font.loadFont(Objects.requireNonNull(MainApplication.class.getResource("assets/fonts/Inter-Black.ttf")).toExternalForm(), 10);
        Font.loadFont(Objects.requireNonNull(MainApplication.class.getResource("assets/fonts/Inter-Bold.ttf")).toExternalForm(), 10);
        Font.loadFont(Objects.requireNonNull(MainApplication.class.getResource("assets/fonts/Inter-ExtraBold.ttf")).toExternalForm(), 10);
        Font.loadFont(Objects.requireNonNull(MainApplication.class.getResource("assets/fonts/Inter-ExtraLight.ttf")).toExternalForm(), 10);
        Font.loadFont(Objects.requireNonNull(MainApplication.class.getResource("assets/fonts/Inter-Light.ttf")).toExternalForm(), 10);
        Font.loadFont(Objects.requireNonNull(MainApplication.class.getResource("assets/fonts/Inter-Medium.ttf")).toExternalForm(), 10);
        Font.loadFont(Objects.requireNonNull(MainApplication.class.getResource("assets/fonts/Inter-Regular.ttf")).toExternalForm(), 10);
        Font.loadFont(Objects.requireNonNull(MainApplication.class.getResource("assets/fonts/Inter-SemiBold.ttf")).toExternalForm(), 10);
        Font.loadFont(Objects.requireNonNull(MainApplication.class.getResource("assets/fonts/Inter-Thin.ttf")).toExternalForm(), 10);
    }

    private List<Organizer> getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + getClass().getResource("db/mopper.db").toExternalForm());

        String query = "SELECT * FROM organizers";
        List<Organizer> organizers = new ArrayList<>();
        try (connection) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                organizers.add(new Organizer(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("extensions"),
                        resultSet.getString("path")
                ));
            }
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(
                    Level.SEVERE,
                    LocalDateTime.now() + ": Could not load organizations from database \n {}", e);
            organizers.clear();
        }

        return organizers;
    }

    private static boolean checkDrivers() {
        try {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());
            return true;
        } catch (ClassNotFoundException | SQLException classNotFoundException) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not start SQLite Drivers");
            return false;
        }
    }

    public static void main(String[] args) {

        launch();
    }
}