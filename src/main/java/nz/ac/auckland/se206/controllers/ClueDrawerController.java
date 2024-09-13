package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

// nz.ac.auckland.se206.controllers.ClueDrawerController

public class ClueDrawerController {

  @FXML private AnchorPane anchorPane;

  private double startX;
  private double startY;

  @FXML
  private void initialize() {
    // Load background and garden tool images
    Image backgroundImage =
        new Image(
            BackstoryController.class
                .getResource("/images/cluedrawimages/clueshelf.png")
                .toString());

    Image gardentool =
        new Image(
            ClueDrawerController.class
                .getResource("/images/cluedrawimages/gardenshears.png")
                .toString());

    createAndBindImageView(backgroundImage);

    // Add a on clicked event for the garden tool
    ImageView imageView = new ImageView(gardentool);
    imageView.setFitWidth(anchorPane.getWidth());
    imageView.setFitHeight(anchorPane.getHeight());

    imageView.fitWidthProperty().bind(anchorPane.widthProperty());
    imageView.fitHeightProperty().bind(anchorPane.heightProperty());

    anchorPane.getChildren().add(imageView);
    imageView.setOnMouseClicked(
        event -> {
          // Set opacity to 0
          imageView.setOpacity(0);
          System.out.println("Garden tool clicked");
        });

    // Adding the movable leaf
    Image leaf1 =
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf1.png").toString());

    // Adding the movable leaf
    Image leaf2 =
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf2.png").toString());

    Image leaf3 =
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf3.png").toString());

    Image leaf4 =
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf4.png").toString());

    Image leaf5 =
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf5.png").toString());

    Image leaf6 =
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf6.png").toString());

    createAndBindleaves(leaf1);
    createAndBindleaves(leaf2);
    createAndBindleaves(leaf3);
    createAndBindleaves(leaf4);
    createAndBindleaves(leaf5);
    createAndBindleaves(leaf6);
  }

  // Method to make the ImageView draggable
  private void makeDraggable(ImageView imageView) {
    imageView.setOnMousePressed(
        event -> {
          // Record the initial position when mouse is pressed
          startX = event.getSceneX() - imageView.getX();
          startY = event.getSceneY() - imageView.getY();
        });

    imageView.setOnMouseDragged(
        event -> {
          // Update the position of the ImageView as the mouse is dragged
          imageView.setX(event.getSceneX() - startX);
          imageView.setY(event.getSceneY() - startY);
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

  private void createAndBindleaves(Image image) {
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(anchorPane.getWidth());
    imageView.setFitHeight(anchorPane.getHeight());

    imageView.fitWidthProperty().bind(anchorPane.widthProperty());
    imageView.fitHeightProperty().bind(anchorPane.heightProperty());

    anchorPane.getChildren().add(imageView);
    makeDraggable(imageView);
  }
}
