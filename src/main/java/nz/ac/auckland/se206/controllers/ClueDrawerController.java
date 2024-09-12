package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

// nz.ac.auckland.se206.controllers.ClueDrawerController

public class ClueDrawerController {

  @FXML private AnchorPane anchorPane;

  @FXML
  private void initialize() {
    Image backgroundImage =
        new Image(BackstoryController.class.getResource("/images/cluedrawcloseup.png").toString());

    // Create the background ImageView and set it to fill the entire pane
    ImageView backgroundImageView = new ImageView(backgroundImage);
    backgroundImageView.setFitWidth(anchorPane.getWidth());
    backgroundImageView.setFitHeight(anchorPane.getHeight());

    // Make sure the background resizes with the window
    backgroundImageView.fitWidthProperty().bind(anchorPane.widthProperty());
    backgroundImageView.fitHeightProperty().bind(anchorPane.heightProperty());

    // Add the background image first, then the group to the anchorPane
    anchorPane.getChildren().addAll(backgroundImageView);
  }
}
