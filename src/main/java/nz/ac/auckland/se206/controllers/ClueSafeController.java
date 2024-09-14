package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.App;
// import nz.ac.auckland.se206.GameStateContext;

public class ClueSafeController {

  @FXML private AnchorPane anchorPane;

  @FXML
  private void initialize() {
    // Load background and garden tool images
    Image backgroundImage =
        new Image(
            BackstoryController.class
                .getResource("/images/cluesafeimages/clueSafe.jpg")
                .toString());

    createAndBindImageView(backgroundImage);


    // Add the "Go Back" button
    Button goBackButton = new Button("Go Back");
    goBackButton.setStyle(
        "-fx-background-radius: 10; "
            + "-fx-border-radius: 10; "
            + "-fx-border-color: black; "
            + "-fx-background-color: white; "
            + "-fx-text-fill: black; "
            + "-fx-font-size: 14px; "
            + "-fx-background-insets: 0; "
            + "-fx-border-insets: 0;");
    goBackButton.setPrefWidth(100);
    goBackButton.setPrefHeight(40);

    // Position the button at the bottom-right corner
    AnchorPane.setBottomAnchor(goBackButton, 10.0); // 10px from the bottom
    AnchorPane.setRightAnchor(goBackButton, 10.0); // 10px from the right

    // Add the button to the anchorPane
    anchorPane.getChildren().add(goBackButton);

    // Set the action when the button is clicked
    goBackButton.setOnAction(
        event -> {
          try {
            App.setRoot("room");
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }

  /*
   * Creating images
   */
  private void createAndBindImageView(Image image) {
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(anchorPane.getWidth());
    imageView.setFitHeight(anchorPane.getHeight());

    imageView.fitWidthProperty().bind(anchorPane.widthProperty());
    imageView.fitHeightProperty().bind(anchorPane.heightProperty());

    anchorPane.getChildren().add(imageView);
  }

}
