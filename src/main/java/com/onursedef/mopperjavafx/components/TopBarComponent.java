package com.onursedef.mopperjavafx.components;

import com.onursedef.mopperjavafx.FolderService;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class TopBarComponent extends AnchorPane {
    private final ImageView image;
    private final Label title;
    private final Label subtitle;

    public TopBarComponent() {
        Image readImage = new Image(getClass().getResourceAsStream("assets/mopper.png"));
        image = new ImageView(readImage);
        image.setFitWidth(31);
        image.setFitHeight(51);
        image.setX(20);
        image.setY(10);
        title = new Label("MOPPER");
        subtitle = new Label("File Organizer Tool");
        title.setStyle("-fx-text-fill: #90CDF4; -fx-font-family: Inter; -fx-font-weight: 700; -fx-font-size: 24");
        title.setLayoutX(55);
        title.setLayoutY(13);
        subtitle.setStyle("-fx-text-fill: #4299E1; -fx-font-family: Inter; -fx-font-weight: 700; -fx-font-size: 16");
        subtitle.setLayoutX(55);
        subtitle.setLayoutY(39);
        Button startButton = new Button("Start");
        startButton.setStyle("-fx-background-color: #2B6CB0; -fx-text-fill: #FFF; -fx-font-size: 14; -fx-font-weight: 600; -fx-font-family: Inter;-fx-cursor: hand");
        startButton.setPadding(new Insets(10, 20, 10, 20));
        startButton.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        startButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        AnchorPane.setRightAnchor(startButton, 20.0);
        AnchorPane.setTopAnchor(startButton, 15.0);
        startButton.setOnMouseClicked(e -> {
            FolderService service = new FolderService();
            service.createFolders();
            service.moveFiles();
        });

        setStyle("-fx-background-color: #1A365D");
        setPrefSize(getWidth(), 70);
        getChildren().addAll(image, title, subtitle, startButton);
    }
}
