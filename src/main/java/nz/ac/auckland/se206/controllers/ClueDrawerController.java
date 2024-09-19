package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

public class ClueDrawerController {

  @FXML private AnchorPane anchorPane;

  private double startX;
  private double startY;

  // Get timer
  private TimerModel countdownTimer;

  @FXML
  private void initialize() {

    // Create a Pane for the timer
    Pane timerPane = new Pane();
    timerPane.setPrefSize(101, 45); // Set the preferred size
    timerPane.setOpacity(0.75); // Set the opacity
    timerPane.setStyle(
        "-fx-background-color: white;"
            + "-fx-background-radius: 10px;"
            + "-fx-border-radius: 10px;"
            + "-fx-border-color: black;");

    // Position the timerPane
    AnchorPane.setLeftAnchor(timerPane, 10.0); // Set position using AnchorPane
    AnchorPane.setTopAnchor(timerPane, 10.0); // Set top anchor

    // Create a label for the timer
    Label timerLabel = new Label();
    timerLabel.setText("Label"); // Default text (will be updated by the timer)
    timerLabel.setFont(new Font(24)); // Set font size
    timerLabel.setAlignment(Pos.CENTER); // Align the text to the center
    timerLabel.setLayoutX(21.0); // Set the label's X position inside the Pane
    timerLabel.setLayoutY(8.0); // Set the label's Y position inside the Pane

    // Bind the timerLabel to the countdown timer
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());

    // Add the label to the Pane
    timerPane.getChildren().add(timerLabel);

    // Add the timerPane to the rootPane
    anchorPane.getChildren().add(timerPane);

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

    // Create the ImageView for the garden tool
    ImageView imageView = new ImageView(gardentool);
    imageView.setFitWidth(anchorPane.getWidth());
    imageView.setFitHeight(anchorPane.getHeight());
    imageView.fitWidthProperty().bind(anchorPane.widthProperty());
    imageView.fitHeightProperty().bind(anchorPane.heightProperty());

    // Check if the garden tool has already been found
    if (GameStateContext.getInstance().isGardenToolFound()) {
      imageView.setOpacity(0); // Hide the tool if already found
    } else {
      imageView.setOpacity(1); // Show the tool if not found
    }

    // Add the garden tool ImageView to the anchorPane
    anchorPane.getChildren().add(imageView);

    // Set mouse click event to mark the garden tool as found and hide it
    imageView.setOnMouseClicked(
        event -> {
          imageView.setOpacity(0); // Hide the tool after it's clicked
          System.out.println("Garden tool clicked");
          GameStateContext.getInstance().setGardenToolFound(true); // Mark as found in the context
        });

    // Adding the movable leaves
    createAndBindleaves(
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf1.png").toString()));
    createAndBindleaves(
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf2.png").toString()));
    createAndBindleaves(
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf3.png").toString()));
    createAndBindleaves(
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf4.png").toString()));
    createAndBindleaves(
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf5.png").toString()));
    createAndBindleaves(
        new Image(
            ClueDrawerController.class.getResource("/images/cluedrawimages/leaf6.png").toString()));

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

    timerPane.toFront();
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

  /**
   * Creates a new ImageView from the given image and binds it to the anchor pane. The ImageView is
   * also made draggable.
   *
   * @param image the image to display
   */
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
