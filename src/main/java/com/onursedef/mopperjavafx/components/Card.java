package com.onursedef.mopperjavafx.components;

import com.onursedef.mopperjavafx.OrganizerService;
import com.onursedef.mopperjavafx.model.Organizer;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.sql.Connection;

import static java.sql.DriverManager.getConnection;


public class Card extends VBox {

    public Card(String title, String description) {
        setMaxSize(90 * 1.5, 150 * 1.5);
        AnchorPane rect = new AnchorPane();
        rect.setPrefSize(90 * 1.5, 130 * 1.5);
        rect.setStyle((description != null ? "-fx-background: #2B6CB0; " : "-fx-background-color: #2C5282;") + "-fx-background-radius: 10px");
        ImageView cardImage = new ImageView(new Image(getClass().getResourceAsStream("assets/newOrganizator.png")));
        cardImage.setFitWidth(78 * 1.5);
        cardImage.setFitHeight(78 * 1.5);
        cardImage.setX(6 * 1.5);
        cardImage.setY(10 * 1.5);
        Label cardTitle = new Label(title);
        cardTitle.setLayoutX(14 * 1.5);
        cardTitle.setLayoutY(90 * 1.5);
        cardTitle.setMaxWidth(62 * 1.5);
        cardTitle.setWrapText(true);
        cardTitle.setTextAlignment(TextAlignment.CENTER);
        cardTitle.setStyle("-fx-font-family: Inter; -fx-font-weight: 600; -fx-font-size: 18; -fx-text-fill: #FFF; -fx-text-align: center");
        Label cardDescription = new Label(description);
        cardDescription.setStyle("-fx-font-family: Inter; -fx-font-weight: 600; -fx-font-size: 15;");
        cardDescription.setLayoutX(18 * 1.5);
        setSpacing(7 * 1.5);
        rect.getChildren().addAll(cardImage, cardTitle);
        rect.setOnMouseEntered((event) -> {
            rect.setStyle(description != null ? "-fx-background-color: #2C5282;-fx-background-radius: 10px;" : "-fx-background-color: #2B6CB0;-fx-background-radius: 10px;" );
        });
        rect.setOnMouseExited((event) -> {
            rect.setStyle(description != null ? "-fx-background: #2B6CB0;-fx-background-radius: 10px;" : "-fx-background-color: #2C5282;-fx-background-radius: 10px;");
        });
        getChildren().add(rect);
        if (description != null) {
            getChildren().add(cardDescription);
        }
    }
}
