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
  @FXML private Pane labelPane;
  @FXML private Label timerLabel;

  private double startX;
  private double startY;
  private TimerModel countdownTimer;

  @FXML
  private void initialize() {
    setupTimerPane();
    setupBackgroundImage();
    setupGardenTool();
    setupLeaves();
    setupGoBackButton();

    // Bind the timer label to the countdown timer
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    // Bind the timer label to the countdown timer and bring the label pane to the front
    timerLabel
        .textProperty()
        .bind(countdownTimer.timeStringProperty()); // Bring the label pane to the front
    labelPane.toFront();
  }

  // Set up the timer pane
  private void setupTimerPane() {

    // Create the timer pane
    Pane timerPane = new Pane();

    // Set the size, opacity, and style of the timer pane
    timerPane.setPrefSize(101, 45);
    timerPane.setOpacity(0.75);
    timerPane.setStyle(
        "-fx-background-color: white;"
            + "-fx-background-radius: 10px;"
            + "-fx-border-radius: 10px;"
            + "-fx-border-color: black;");

    // Set the position of the timer pane
    AnchorPane.setLeftAnchor(timerPane, 10.0);
    AnchorPane.setTopAnchor(timerPane, 10.0);

    // Create the timer label
    Label timerLabel = new Label();
    timerLabel.setFont(new Font(24));
    timerLabel.setAlignment(Pos.CENTER);
    timerLabel.setLayoutX(21.0);
    timerLabel.setLayoutY(8.0);

    // Bind the timer label to the countdown timer
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());

    // Add the timer label to the timer pane
    timerPane.getChildren().add(timerLabel);
    anchorPane.getChildren().add(timerPane);
    timerPane.toFront();
  }

  // Load and bind the background image
  private void setupBackgroundImage() {
    Image backgroundImage =
        new Image(
            BackstoryController.class
                .getResource("/images/cluedrawimages/clueshelf.png")
                .toString());

    // Create and bind the background image
    createAndBindImageView(backgroundImage, false);
  }

  // Load and setup the garden tool image
  private void setupGardenTool() {
    Image gardenToolImage =
        new Image(
            ClueDrawerController.class
                .getResource("/images/cluedrawimages/gardenshears.png")
                .toString());
    // Load the garden tool image
    ImageView gardenToolView = createAndBindImageView(gardenToolImage, false);

    // Set the garden tool image to be draggable
    if (GameStateContext.getInstance().isGardenToolFound()) {
      gardenToolView.setOpacity(0);
    } else {
      // Set the garden tool image to be draggable
      gardenToolView.setOpacity(1);
      gardenToolView.setOnMouseClicked(
          event -> {
            gardenToolView.setOpacity(0);
            GameStateContext.getInstance().setGardenToolFound(true);
            System.out.println("Garden tool clicked");
          });
    }
  }

  // Set up the leaves (draggable)
  private void setupLeaves() {
    // Load the leaf images
    String[] leafImages = {
      "/images/cluedrawimages/leaf1.png",
      "/images/cluedrawimages/leaf2.png",
      "/images/cluedrawimages/leaf3.png",
      "/images/cluedrawimages/leaf4.png",
      "/images/cluedrawimages/leaf5.png",
      "/images/cluedrawimages/leaf6.png"
    };

    // Create and bind the leaf images
    for (String leafPath : leafImages) {
      // Load the leaf image
      Image leafImage = new Image(ClueDrawerController.class.getResource(leafPath).toString());
      ImageView leafView = createAndBindImageView(leafImage, true);
      makeDraggable(leafView);
    }
  }

  // Set up the "Go Back" button
  private void setupGoBackButton() {
    // Create the "Go Back" button
    Button goBackButton = new Button("Go Back");
    goBackButton.setStyle(
        "-fx-background-radius: 10; "
            + "-fx-border-radius: 10; "
            + "-fx-border-color: black; "
            + "-fx-background-color: white; "
            + "-fx-text-fill: black; "
            + "-fx-font-size: 14px;");
    goBackButton.setPrefWidth(100);
    goBackButton.setPrefHeight(40);

    // Set the position of the "Go Back" button
    AnchorPane.setBottomAnchor(goBackButton, 10.0);
    AnchorPane.setRightAnchor(goBackButton, 10.0);
    goBackButton.setOnAction(event -> goBackToRoom());

    // Add hover effect to the button
    goBackButton.setOnMouseEntered(
        e -> {
          goBackButton.setOpacity(0.7);
          goBackButton.setCursor(javafx.scene.Cursor.HAND);
        });

    // Remove effect when mouse exits
    goBackButton.setOnMouseExited(
        e -> {
          goBackButton.setOpacity(1);
          goBackButton.setCursor(javafx.scene.Cursor.DEFAULT);
        });

    // Add the "Go Back" button to the anchor pane
    anchorPane.getChildren().add(goBackButton);
  }

  // Method to go back to the room
  private void goBackToRoom() {
    try {
      App.setRoot("room");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Create and bind an ImageView to the anchorPane
  private ImageView createAndBindImageView(Image image, boolean draggable) {
    // Create the ImageView
    ImageView imageView = new ImageView(image);

    // Bind the ImageView to the anchorPane
    imageView.setFitWidth(anchorPane.getWidth());
    imageView.setFitHeight(anchorPane.getHeight());

    // Bind the ImageView to the anchorPane
    imageView.fitWidthProperty().bind(anchorPane.widthProperty());
    imageView.fitHeightProperty().bind(anchorPane.heightProperty());

    // Add the ImageView to the anchorPane
    anchorPane.getChildren().add(imageView);

    // Make the ImageView draggable
    if (draggable) {
      makeDraggable(imageView);
    }

    return imageView;
  }

  // Make the ImageView draggable
  private void makeDraggable(ImageView imageView) {

    // Set the starting position of the ImageView
    imageView.setOnMousePressed(
        event -> {
          startX = event.getSceneX() - imageView.getX();
          startY = event.getSceneY() - imageView.getY();
        });

    // Set the ImageView to be draggable
    imageView.setOnMouseDragged(
        event -> {
          imageView.setX(event.getSceneX() - startX);
          imageView.setY(event.getSceneY() - startY);
        });
  }
}
