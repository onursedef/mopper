package com.onursedef.mopperjavafx;
import com.onursedef.mopperjavafx.components.Card;
import com.onursedef.mopperjavafx.components.Modal;
import com.onursedef.mopperjavafx.components.OrganizerCard;
import com.onursedef.mopperjavafx.components.TopBarComponent;
import com.onursedef.mopperjavafx.model.Organizer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
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
        OrganizerService service = new OrganizerService();
        List<Organizer>  organizers = service.GetAll();
        for (Organizer organizer : organizers) {
            OrganizerCard card1 = new OrganizerCard(organizer, pane, flowPane, stage, card);
            flowPane.getChildren().add(card1);
        }
        flowPane.getChildren().add(card);
        vbox.getChildren().add(flowPane);
        vbox.setLayoutX(0);
        vbox.setLayoutY(0);
        pane.getChildren().add(vbox);
        // show modal on card click
        card.setOnMouseClicked(e -> pane.getChildren().add(modal));

        vbox.prefWidthProperty().bind(pane.widthProperty());
        vbox.prefHeightProperty().bind(pane.heightProperty());
        Scene scene = new Scene(pane, 1280, 720);
        loadFonts();

        SystemTray();

        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Mopper - File Organizer");
        stage.getIcons().add(
                new Image(getClass().getResource("logo.png").toExternalForm())
        );
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

    private void SystemTray() {
        try {
            final PopupMenu menu = new PopupMenu();
            final URL url = MainApplication.class.getResource("assets/logo.png");
            java.awt.Image image = Toolkit.getDefaultToolkit().getImage(url);
            final TrayIcon trayIcon = new TrayIcon(image);
            final SystemTray tray = SystemTray.getSystemTray();

            MenuItem start = new MenuItem("Start");
            MenuItem exit = new MenuItem("Exit");

            menu.add(start);
            menu.addSeparator();
            menu.add(exit);

            trayIcon.setPopupMenu(menu);
            trayIcon.setToolTip("Mopper - File Organizer Tool");

            start.addActionListener(e -> {
                FolderService service = new FolderService();
                service.createFolders();
                service.moveFiles();
            });

            exit.addActionListener(e -> {
                // Stop Application
                Platform.exit();
                System.exit(0);
            });

            tray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main() {
        FolderService service = new FolderService();
        launch();
        while(true) {
            service.createFolders();
            service.moveFiles();
            try {
                Thread.sleep((long) 3.6e+6);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}