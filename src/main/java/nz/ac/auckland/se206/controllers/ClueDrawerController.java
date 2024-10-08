package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import nz.ac.auckland.se206.App;

public class ClueDrawerController {

  @FXML private AnchorPane anchorPane;
  @FXML private Pane labelPane;
  @FXML private Label timerLabel;
  @FXML private GridPane tileGridPane; // Add GridPane to FXML for tiles
  @FXML private StackPane container = new StackPane();
  @FXML private Button goBackButton;
  private double startX;
  private double startY;
  private TimerModel countdownTimer;

  /** Initializes the ClueDrawerController. */
  @FXML
  private void initialize() {
    setupTimerPane();
    setupBackgroundImage();

    // Bind the timer label to the countdown timer
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();

    // Bind the timer label to the countdown timer and bring the label pane to the front
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());
    labelPane.toFront();

    tileGridPane.toFront();

    setupGoBackButton();
  }

  /** Sets up the timer pane. */
  private void setupTimerPane() {
    // Create a timer pane
    Pane timerPane = new Pane();

    // Set the size and style of the timer pane
    timerPane.setPrefSize(101, 45);
    timerPane.setOpacity(0.75);

    // Set the style of the timer pane
    timerPane.setStyle(
        "-fx-background-color: white;"
            + "-fx-background-radius: 10px;"
            + "-fx-border-radius: 10px;"
            + "-fx-border-color: black;");

    // Set the position of the timer pane
    AnchorPane.setLeftAnchor(timerPane, 10.0);
    AnchorPane.setTopAnchor(timerPane, 10.0);

    // Create a label for the timer
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

    // Add the timer pane to the anchor pane
    anchorPane.getChildren().add(timerPane);

    // Bring the timer pane to the front
    timerPane.toFront();
  }

  /** Sets up the background image. */
  private void setupBackgroundImage() {
    Image backgroundImage =
        new Image(
            BackstoryController.class
                .getResource("/images/cluedrawimages/photoframebg.png")
                .toString());
    createAndBindImageView(backgroundImage, false);

    Image photoFrameImage =
        new Image(
            BackstoryController.class
                .getResource("/images/cluedrawimages/photoframonly.png")
                .toString());
    ImageView photoFrameImageView = createAndBindImageView(photoFrameImage, false);

    // add a drop shadow effect to the photo frame
    photoFrameImageView.setEffect(new DropShadow(10, Color.BLACK));

    // set on mouse entered effects
    photoFrameImageView.setOnMouseEntered(
        e -> {
          photoFrameImageView.setOpacity(0.7);
          photoFrameImageView.setCursor(javafx.scene.Cursor.HAND);
        });

    // set on mouse exited effects
    photoFrameImageView.setOnMouseExited(
        e -> {
          photoFrameImageView.setOpacity(1);
          photoFrameImageView.setCursor(javafx.scene.Cursor.DEFAULT);
        });
  }

  /** Sets up the go back button. */
  private void setupGoBackButton() {
    // Create a go back button
    goBackButton = new Button("Go Back");

    // Set the style of the go back button
    goBackButton.setStyle(
        "-fx-background-radius: 10; "
            + "-fx-border-radius: 10; "
            + "-fx-border-color: black; "
            + "-fx-background-color: white; "
            + "-fx-text-fill: black; "
            + "-fx-font-size: 14px;");

    // Set the position of the go back button
    goBackButton.setPrefWidth(100);
    goBackButton.setPrefHeight(40);

    // Set the position of the go back button
    AnchorPane.setBottomAnchor(goBackButton, 10.0);
    AnchorPane.setRightAnchor(goBackButton, 10.0);

    // Set the action of the go back button
    goBackButton.setOnAction(event -> goBackToRoom());

    // Set the hover effects of the go back button
    goBackButton.setOnMouseEntered(
        e -> {
          goBackButton.setOpacity(0.7);
          goBackButton.setCursor(javafx.scene.Cursor.HAND);
        });

    // Set the hover effects of the go back button
    goBackButton.setOnMouseExited(
        e -> {
          goBackButton.setOpacity(1);
          goBackButton.setCursor(javafx.scene.Cursor.DEFAULT);
        });

    // Add the go back button to the anchor pane
    anchorPane.getChildren().add(goBackButton);
  }

  /** Goes back to the room scene. */
  private void goBackToRoom() {
    try {
      App.setRoot("room");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates an image view and binds it to the anchor pane.
   *
   * @param image
   * @param draggable
   * @return
   */
  private ImageView createAndBindImageView(Image image, boolean draggable) {
    // Create an ImageView with the image and bind it to the anchor pane
    ImageView imageView = new ImageView(image);

    // Set the image to take up the whole screen
    imageView.setFitWidth(anchorPane.getWidth());
    imageView.setFitHeight(anchorPane.getHeight());

    // Bind the image to the anchor pane
    imageView.fitWidthProperty().bind(anchorPane.widthProperty());
    imageView.fitHeightProperty().bind(anchorPane.heightProperty());
    anchorPane.getChildren().add(imageView);

    // Make the image draggable if required
    if (draggable) {
      makeDraggable(imageView);
    }

    // Return the image view
    return imageView;
  }

  /**
   * Makes an image view draggable.
   *
   * @param imageView
   */
  private void makeDraggable(ImageView imageView) {
    // Set up the image view to be draggable
    imageView.setOnMousePressed(
        event -> {
          startX = event.getSceneX() - imageView.getX();
          startY = event.getSceneY() - imageView.getY();
        });
    // Set up the image view to be draggable
    imageView.setOnMouseDragged(
        event -> {
          imageView.setX(event.getSceneX() - startX);
          imageView.setY(event.getSceneY() - startY);
        });
  }
}
