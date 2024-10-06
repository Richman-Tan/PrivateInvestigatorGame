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

  // Set up the timer pane
  private void setupTimerPane() {
    Pane timerPane = new Pane();
    timerPane.setPrefSize(101, 45);
    timerPane.setOpacity(0.75);
    timerPane.setStyle(
        "-fx-background-color: white;"
            + "-fx-background-radius: 10px;"
            + "-fx-border-radius: 10px;"
            + "-fx-border-color: black;");
    AnchorPane.setLeftAnchor(timerPane, 10.0);
    AnchorPane.setTopAnchor(timerPane, 10.0);

    Label timerLabel = new Label();
    timerLabel.setFont(new Font(24));
    timerLabel.setAlignment(Pos.CENTER);
    timerLabel.setLayoutX(21.0);
    timerLabel.setLayoutY(8.0);

    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());

    timerPane.getChildren().add(timerLabel);
    anchorPane.getChildren().add(timerPane);
    timerPane.toFront();
  }

  // Load and bind the background image
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
    photoFrameImageView.setOnMouseExited(
        e -> {
          photoFrameImageView.setOpacity(1);
          photoFrameImageView.setCursor(javafx.scene.Cursor.DEFAULT);
        });
  }

  private void setupGoBackButton() {
    goBackButton = new Button("Go Back");
    goBackButton.setStyle(
        "-fx-background-radius: 10; "
            + "-fx-border-radius: 10; "
            + "-fx-border-color: black; "
            + "-fx-background-color: white; "
            + "-fx-text-fill: black; "
            + "-fx-font-size: 14px;");
    goBackButton.setPrefWidth(100);
    goBackButton.setPrefHeight(40);
    AnchorPane.setBottomAnchor(goBackButton, 10.0);
    AnchorPane.setRightAnchor(goBackButton, 10.0);
    goBackButton.setOnAction(event -> goBackToRoom());
    goBackButton.setOnMouseEntered(
        e -> {
          goBackButton.setOpacity(0.7);
          goBackButton.setCursor(javafx.scene.Cursor.HAND);
        });
    goBackButton.setOnMouseExited(
        e -> {
          goBackButton.setOpacity(1);
          goBackButton.setCursor(javafx.scene.Cursor.DEFAULT);
        });
    anchorPane.getChildren().add(goBackButton);
  }

  private void goBackToRoom() {
    try {
      App.setRoot("room");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ImageView createAndBindImageView(Image image, boolean draggable) {
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(anchorPane.getWidth());
    imageView.setFitHeight(anchorPane.getHeight());
    imageView.fitWidthProperty().bind(anchorPane.widthProperty());
    imageView.fitHeightProperty().bind(anchorPane.heightProperty());
    anchorPane.getChildren().add(imageView);
    if (draggable) {
      makeDraggable(imageView);
    }
    return imageView;
  }

  private void makeDraggable(ImageView imageView) {
    imageView.setOnMousePressed(
        event -> {
          startX = event.getSceneX() - imageView.getX();
          startY = event.getSceneY() - imageView.getY();
        });
    imageView.setOnMouseDragged(
        event -> {
          imageView.setX(event.getSceneX() - startX);
          imageView.setY(event.getSceneY() - startY);
        });
  }
}
