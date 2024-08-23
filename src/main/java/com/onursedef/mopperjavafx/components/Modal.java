package com.onursedef.mopperjavafx.components;

import com.onursedef.mopperjavafx.OrganizerService;
import com.onursedef.mopperjavafx.model.Organizer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Modal extends GridPane {
    private boolean isClosed;

    public Modal(
            Organizer organizerDetail,
            AnchorPane parent,
            FlowPane gridPane,
            Card newOrgCard,
            OrganizerCard orgCard,
            Stage stage) {

        setStyle("-fx-background-color: rgba(0, 0, 0, 0.5)");
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        setMinSize(getWidth(), getHeight());
        prefHeightProperty().bind(parent.heightProperty());
        prefWidthProperty().bind(parent.widthProperty());
        setAlignment(Pos.CENTER);
        AnchorPane modal = new AnchorPane();
        modal.setMinSize(600, 600);
        modal.setStyle("-fx-background-color: #2A4365; -fx-background-radius: 10");
        modal.prefWidthProperty().bind(parent.widthProperty().multiply(0.4));
        modal.prefHeightProperty().bind(parent.heightProperty().multiply(0.6));
        Label modalTitle = new Label(organizerDetail != null ? "Update Organizer" : "Add Organizer");
        modalTitle.setStyle("-fx-text-fill: #FFF; -fx-font-family: Inter; -fx-font-weight: 600; -fx-font-size: 18");
        modal.setPadding(new Insets(16, 16, 16, 16));
        modalTitle.setLayoutX(16);
        modalTitle.setLayoutY(16);
        HBox closeButton = new HBox();
        closeButton.setPrefSize(32,32);
        closeButton.setStyle("-fx-background-color: #2B6CB0; -fx-padding: 0; -fx-cursor: hand; -fx-background-radius: 5");
        ImageView closeButtonImage = new ImageView(new Image(getClass().getResource("assets/x.png").toString()));
        closeButtonImage.setFitWidth(24);
        closeButtonImage.setFitHeight(24);
        closeButton.setLayoutY(16);
        closeButton.setAlignment(Pos.CENTER);
        closeButton.getChildren().add(closeButtonImage);
        AnchorPane.setRightAnchor(closeButton, 0.0);
        modal.getChildren().addAll(modalTitle, closeButton);

        // form
        VBox form = new VBox();
        form.setLayoutY(60);
        form.setSpacing(16);
        form.setPadding(new Insets(16, 16, 16, 16));
        VBox nameInput = new VBox();
        nameInput.setSpacing(8);
        Label nameLabel = new Label("Name");
        nameLabel.setStyle("-fx-text-fill: #FFF; -fx-font-family: Inter; -fx-font-weight: 600; -fx-font-size: 16");
        TextField textInput = new TextField();
        textInput.setPromptText("e.g. Documents");
        if (organizerDetail != null) textInput.setText(organizerDetail.getName());
        textInput.setMinSize(559, 33);
        textInput.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        textInput.setStyle("-fx-background-color: #2C5282; -fx-text-fill: #CBD5E0; -fx-font-size: 14; -fx-font-weight: 600; -fx-font-family: Inter");
        nameInput.getChildren().addAll(nameLabel, textInput);
        form.getChildren().add(nameInput);
        VBox extensionsInput = new VBox();
        extensionsInput.setSpacing(8);
        Label extensionLabel = new Label("Extensions");
        extensionLabel.setStyle("-fx-text-fill: #FFF; -fx-font-family: Inter; -fx-font-weight: 600; -fx-font-size: 16");
        TextField extensionsInputField = new TextField();
        extensionsInputField.setPromptText("e.g. doc, pdf, xls");
        if (organizerDetail != null) extensionsInputField.setText(organizerDetail.getExtensions());
        extensionsInputField.setMinSize(559, 33);
        extensionsInputField.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        extensionsInputField.setStyle("-fx-background-color: #2C5282; -fx-text-fill: #CBD5E0; -fx-font-size: 14; -fx-font-weight: 600; -fx-font-family: Inter");
        extensionsInput.getChildren().addAll(extensionLabel, extensionsInputField);
        form.getChildren().add(extensionsInput);
        VBox pathInput = new VBox();
        pathInput.setSpacing(8);
        Label pathLabel = new Label("Path");
        pathLabel.setStyle("-fx-text-fill: #FFF; -fx-font-family: Inter; -fx-font-weight: 600; -fx-font-size: 16");
        TextField pathInputField = new TextField();
        pathInputField.setPromptText("/path/to/your/folder");
        if (organizerDetail != null) pathInputField.setText(organizerDetail.getPath());
        DirectoryChooser folderChooser = new DirectoryChooser();
        folderChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        Button button = new Button("Choose");
        button.setStyle("-fx-background-color: #2B6CB0; -fx-text-fill: #FFF; -fx-font-size: 14; -fx-font-weight: 600; -fx-font-family: Inter;-fx-cursor: hand");
        button.setOnAction(e -> {
            File selectedDirectory = folderChooser.showDialog(stage);
            if (selectedDirectory == null) return;
            System.out.println(selectedDirectory.getAbsolutePath());
            pathInputField.setText(selectedDirectory.getAbsolutePath());
        });

        pathInputField.setMinSize(559, 33);
        pathInputField.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        pathInputField.setStyle("-fx-background-color: #2C5282; -fx-text-fill: #CBD5E0; -fx-font-size: 14; -fx-font-weight: 600; -fx-font-family: Inter");
        pathInput.getChildren().addAll(pathLabel, pathInputField);
        AnchorPane.setRightAnchor(form, 0.0);
        AnchorPane.setLeftAnchor(form, -10.0);
        form.getChildren().add(pathInput);
        form.getChildren().add(button);

        closeButton.setOnMouseClicked(event -> {
            textInput.clear();
            extensionsInputField.clear();
            pathInputField.clear();
            parent.getChildren().remove(this);
        });

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #2B6CB0; -fx-text-fill: #FFF; -fx-font-size: 14; -fx-font-weight: 600; -fx-font-family: Inter;-fx-cursor: hand");
        saveButton.setPrefSize(100, 33);
        saveButton.setOnMouseClicked(e -> {
            // your save logic here
            System.out.println("Organization saved: " + textInput.getText());

            if (organizerDetail != null) {
                update(organizerDetail.getId(), textInput.getText(), extensionsInputField.getText(), pathInputField.getText());
            } else {
                addNew(textInput.getText(), extensionsInputField.getText(), pathInputField.getText());
            }

            gridPane.getChildren().clear();
            OrganizerService service = new OrganizerService();
            List<Organizer> organizers = service.GetAll();
            for (Organizer organizer : organizers) {
                OrganizerCard card = new OrganizerCard(organizer, parent, gridPane, stage, newOrgCard);
                gridPane.getChildren().add(card);
            }
            gridPane.getChildren().add(newOrgCard);
            parent.getChildren().remove(this);
        });
        AnchorPane.setRightAnchor(saveButton,16.0);
        AnchorPane.setBottomAnchor(saveButton, 16.0);
        modal.getChildren().add(saveButton);
        if (organizerDetail != null) {
            AnchorPane deleteButton = new AnchorPane();
            ImageView trashImage = new ImageView(new Image(String.valueOf(getClass().getResource("assets/trash.png"))));
            trashImage.setFitWidth(32);
            trashImage.setFitHeight(32);
            deleteButton.setPrefSize(36, 36);
            deleteButton.setStyle("-fx-background-color: #dc3333; -fx-text-fill: #FFF; -fx-cursor: hand; -fx-background-radius: 5px;");
            trashImage.setLayoutY(2);
            trashImage.setLayoutX(2);
            deleteButton.setOnMouseClicked(e -> {
                delete(organizerDetail.getId());
                gridPane.getChildren().clear();
                OrganizerService service = new OrganizerService();
                List<Organizer> organizations = service.GetAll();
                for (Organizer organizer : organizations) {
                    OrganizerCard card = new OrganizerCard(organizer, parent, gridPane, stage, newOrgCard);
                    gridPane.getChildren().add(card);
                }
                gridPane.getChildren().add(newOrgCard);
                parent.getChildren().remove(this);
            });
            deleteButton.getChildren().add(trashImage);
            AnchorPane.setBottomAnchor(deleteButton, 16.0);
            AnchorPane.setLeftAnchor(deleteButton, 16.0);
            modal.getChildren().add(deleteButton);
        }

        modal.getChildren().add(form);
        getChildren().add(modal);
    }

    public void addNew(String title, String extensions, String path)
    {
        OrganizerService orgService = new OrganizerService();
        orgService.Add(new Organizer(null, title, extensions, path));
    }

    public void update(int id, String title, String extensions, String path)
    {
        OrganizerService orgService = new OrganizerService();
        orgService.Update(id, title, extensions, path);
    }

    public void delete(int id) {
        OrganizerService orgService = new OrganizerService();
        orgService.Delete(id);
    }
}
