package com.onursedef.mopperjavafx.components;

import com.onursedef.mopperjavafx.model.Organizer;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class OrganizerCard extends VBox {
    private Label cardTitle;

    public OrganizerCard(Organizer organizer, AnchorPane parent, FlowPane grid, Stage stage, Card newOrgCard) {
        setMaxSize(135, 225);
        AnchorPane rect = new AnchorPane();
        rect.setPrefSize(135, 195);
        rect.setStyle("-fx-background-color: #2B6CB0; -fx-background-radius: 10px;");
        ImageView cardImage = new ImageView(new Image(String.valueOf(getClass().getResource("assets/folder.png"))));
        cardImage.setFitWidth(78 * 1.5);
        cardImage.setFitHeight(78 * 1.5);
        cardImage.setX(9);
        cardImage.setY(15);
        Label cardTitle = new Label(organizer.getName());
        cardTitle.setStyle("-fx-text-fill: white; -fx-font-family: Inter; -fx-font-size: 16px; -fx-font-weight: 700;");
        cardTitle.setWrapText(true);
        cardTitle.setLayoutX(21);
        cardTitle.setLayoutY(135);
        cardTitle.setMaxWidth(93);
        cardTitle.setTextAlignment(TextAlignment.CENTER);
        rect.getChildren().addAll(cardImage, cardTitle);
        rect.setOnMouseEntered(e -> rect.setStyle("-fx-background-color: #2C5282; -fx-background-radius: 10px;"));
        rect.setOnMouseExited(e -> rect.setStyle("-fx-background-color: #2B6CB0; -fx-background-radius: 10px;"));
        rect.setOnMouseClicked(e -> {
            Modal modal = new Modal(
                    organizer,
                    parent,
                    grid,
                    newOrgCard,
                    this,
                    stage);
            System.out.println(organizer.toString());
            parent.getChildren().add(modal);
        });
        getChildren().addAll(rect);
    }
}
