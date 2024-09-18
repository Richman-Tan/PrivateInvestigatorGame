package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;

public class CutSceneController {

  @FXML private AnchorPane rootPane;

  private TimerModel countdownTimer;

  private MediaPlayer mediaPlayer; // Declare mediaPlayer as an instance variable
  private Label revealLabel; // Label for text reveal
  private final String revealText = "THE WILL???"; // Text to display letter by letter

  public void initialize() {

    // Load background video using the correct class reference for resource path
    String videoPath =
        CutSceneController.class
            .getResource("/images/startingcutscene.mp4") // Get the resource path
            .toExternalForm(); // Convert the resource path to an external form
    Media media = new Media(videoPath);
    mediaPlayer = new MediaPlayer(media);
    MediaView mediaView = new MediaView(mediaPlayer);

    mediaView.setPreserveRatio(false); // Don't preserve the video's aspect ratio

    // Set the video to fit the rootPane's width and height
    mediaView.setFitWidth(rootPane.getWidth());
    mediaView.setFitHeight(rootPane.getHeight());

    // Make sure the video resizes with the window
    mediaView.fitWidthProperty().bind(rootPane.widthProperty());
    mediaView.fitHeightProperty().bind(rootPane.heightProperty());

    // Initially set the mediaView to be transparent
    mediaView.setOpacity(0);

    // Add the MediaView to the rootPane
    rootPane.getChildren().add(mediaView);

    // Create a fade-in transition for the video
    FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), mediaView);
    fadeIn.setFromValue(0); // Start fully transparent
    fadeIn.setToValue(1); // End fully visible
    fadeIn.setOnFinished(event -> mediaPlayer.play()); // Start the video once fade-in is complete

    // Start the fade-in transition, then play the video
    fadeIn.play();

    // Set mediaPlayer to play the video once
    mediaPlayer.setCycleCount(1); // Play the video once
    mediaPlayer.setAutoPlay(false); // Wait for fade-in before playing

    // Create the Label dynamically and add it to the rootPane
    createRevealLabel();

    // Trigger after video finishes playing
    mediaPlayer.setOnEndOfMedia(() -> startTextRevealAnimation());

    // Wait for the scene to be set before trying to access the window (Stage)
    rootPane
        .sceneProperty()
        .addListener(
            (observable, oldScene, newScene) -> {
              if (newScene != null) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setOnCloseRequest(event -> handleExit()); // Handle the close request
              }
            });

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
    rootPane.getChildren().add(timerPane);
  }

  // Method to dynamically create the label and center it
  private void createRevealLabel() {
    revealLabel = new Label(); // Create the Label
    revealLabel.setText(""); // Initially empty
    revealLabel.setOpacity(0); // Initially invisible
    revealLabel.setStyle(
        "-fx-font-size: 48px; -fx-text-fill: red; -fx-font-weight: bold;"); // Style the label

    // Add listeners to center the label when its size changes
    revealLabel.widthProperty().addListener((obs, oldVal, newVal) -> centerLabel());
    revealLabel.heightProperty().addListener((obs, oldVal, newVal) -> centerLabel());

    // Add the label to the rootPane
    rootPane.getChildren().add(revealLabel);
  }

  // Center the label in the rootPane based on its size
  private void centerLabel() {
    if (revealLabel.getScene() != null) {
      double centerX = (rootPane.getWidth() - revealLabel.getWidth()) / 2;
      double centerY = (rootPane.getHeight() - revealLabel.getHeight()) / 2;
      revealLabel.setLayoutX(centerX);
      revealLabel.setLayoutY(centerY);
    }
  }

  // Method to start the text reveal animation and zoom effect
  private void startTextRevealAnimation() {
    revealLabel.setOpacity(1); // Make the label visible

    Timeline timeline = new Timeline();
    for (int i = 0; i < revealText.length(); i++) {
      final int index = i;
      KeyFrame keyFrame =
          new KeyFrame(
              Duration.millis(50 * i),
              event -> {
                // Append the next character to the label's text
                revealLabel.setText(revealLabel.getText() + revealText.charAt(index));
                // Recenter the label after each text update
                centerLabel();
              });
      timeline.getKeyFrames().add(keyFrame);
    }

    // Play the text reveal animation
    zoomInAndOut();
    timeline.play();
  }

  // Method to zoom in, pause, and zoom out
  private void zoomInAndOut() {
    // Create a ScaleTransition for zooming in and out
    ScaleTransition zoomIn = new ScaleTransition(Duration.seconds(0.25), rootPane);
    zoomIn.setFromX(1.0);
    zoomIn.setFromY(1.0);
    zoomIn.setToX(2.0); // Zoom in 2x
    zoomIn.setToY(2.0);

    // Zoom out transition
    ScaleTransition zoomOut = new ScaleTransition(Duration.seconds(0.25), rootPane);
    zoomOut.setFromX(2.0);
    zoomOut.setFromY(2.0);
    zoomOut.setToX(1.0); // Return to original size
    zoomOut.setToY(1.0);

    // Pause for 1 second after zooming in
    PauseTransition pause = new PauseTransition(Duration.seconds(1));

    // Play zoom in, pause, then zoom out
    zoomIn.setOnFinished(event -> pause.play());
    pause.setOnFinished(event -> zoomOut.play());
    zoomIn.play();
    zoomOut.setOnFinished(
        event -> {
          // Remove the label
          rootPane.getChildren().remove(revealLabel);

          // Main dialog pane (background)
          BorderPane dialogPane = new BorderPane();
          dialogPane.setStyle(
              "-fx-border-color: black; -fx-border-radius: 20; -fx-border-width: 3;");
          dialogPane.setBackground(
              new Background(
                  new BackgroundFill(
                      Color.rgb(255, 255, 255, 0.8), new CornerRadii(20), Insets.EMPTY)));
          dialogPane.setPadding(new Insets(10));

          // Label for character name
          Label nameLabel = new Label("?????");
          nameLabel.setFont(new Font("Arial", 18));
          nameLabel.setTextFill(Color.BLACK); // Setting the pink color for the name
          nameLabel.setStyle(
              "-fx-font-weight: bold; -fx-background-color: white; -fx-border-color: black;"
                  + " -fx-border-radius: 20; -fx-padding: 5; -fx-border-width: 3;"
                  + " -fx-background-radius: 20;");

          // Label for dialogue text
          Label dialogueLabel = new Label("");
          dialogueLabel.setFont(new Font("Arial", 18));
          dialogueLabel.setTextFill(Color.WHITE);
          dialogueLabel.setStyle("-fx-padding: 5; -fx-text-fill: black;");
          dialogueLabel.setWrapText(true); // Enable text wrapping for the dialogue
          dialogueLabel.setMaxWidth(800); // Set a max width to allow wrapping within the pane

          // Place the nameLabel at the top left
          AnchorPane.setBottomAnchor(nameLabel, 100.0);
          AnchorPane.setLeftAnchor(nameLabel, 10.0);

          // Add the dialogue label to the dialogPane (centered)
          dialogPane.setCenter(dialogueLabel);
          dialogPane.setPrefWidth(600);
          dialogPane.setPrefHeight(100);

          // Set the position of the dialog pane at the bottom of the screen
          AnchorPane.setBottomAnchor(dialogPane, 20.0);
          AnchorPane.setLeftAnchor(dialogPane, 50.0);
          AnchorPane.setRightAnchor(dialogPane, 50.0);

          Button nextButton = new Button("CALL PI MASTERS");
          nextButton.setStyle(
              "-fx-font-weight: bold; -fx-background-color: white; -fx-border-color: black;"
                  + " -fx-border-radius: 20; -fx-padding: 5; -fx-border-width: 3;"
                  + " -fx-background-radius: 20;");
          nextButton.setOnAction(
              e -> {
                // Handle the next button action
                try {
                  // Fade out
                  FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), rootPane);
                  fadeOut.setFromValue(1); // Start fully visible
                  fadeOut.setToValue(0); // End fully transparent
                  fadeOut.play();
                  fadeOut.setOnFinished(
                      t -> {
                        mediaPlayer.stop(); // Stop the video
                        mediaPlayer.dispose(); // Release media player resources
                      });
                  App.setRoot("backstory"); // Transition to the backstory view
                } catch (IOException e1) {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
                } // Transition to the gameplay view
              });

          nextButton.setOnMouseEntered(
              e -> {
                nextButton.setStyle(
                    "-fx-font-weight: bold; -fx-background-color: white; -fx-border-color: black;"
                        + " -fx-border-radius: 20; -fx-padding: 5; -fx-border-width: 3;"
                        + " -fx-background-radius: 20;"
                        + "-fx-cursor: hand;");
                nextButton.setOpacity(0.5);
              }); // Change to pointer on hover

          nextButton.setOnMouseExited(
              e -> {
                nextButton.setStyle(
                    "-fx-font-weight: bold; -fx-background-color: white; -fx-border-color: black;"
                        + " -fx-border-radius: 20; -fx-padding: 5; -fx-border-width: 3;"
                        + " -fx-background-radius: 20;"
                        + "-fx-cursor: default;");
                nextButton.setOpacity(1);
              }); // Restore to default on exit

          // Place the nextButton at the bottom right
          AnchorPane.setBottomAnchor(nextButton, 130.0);
          AnchorPane.setRightAnchor(nextButton, 50.0);

          dialogPane.setOpacity(0);
          nameLabel.setOpacity(0);
          nextButton.setOpacity(0);

          // Add everything to the root pane
          rootPane.getChildren().addAll(dialogPane, nameLabel, nextButton);

          // Create a fade-in transition for the dialog pane
          FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), dialogPane);
          fadeIn.setFromValue(0); // Start fully transparent
          fadeIn.setToValue(1); // End fully visible
          fadeIn.play();

          // Create a fade-in transition for the name label
          FadeTransition fadeInName = new FadeTransition(Duration.seconds(1), nameLabel);
          fadeInName.setFromValue(0); // Start fully transparent
          fadeInName.setToValue(1); // End fully visible
          fadeInName.play();

          // Create a fade-in transition for the next button
          FadeTransition fadeInNext = new FadeTransition(Duration.seconds(1), nextButton);
          fadeInNext.setFromValue(0); // Start fully transparent
          fadeInNext.setToValue(1); // End fully visible
          fadeInNext.play();

          // Set the dialogue text
          String dialogueText =
              "Oh no! The will is missing! Who could've taken it? The FAMILY LAWER is coming in 5"
                  + " minutes!!! If they come, and the will is gone, all his assets"
                  + " will disappear! QUICK call PI masters!";

          // Create a timeline to reveal the text in the dialogueLabel one letter at a time
          Timeline timeline = new Timeline();
          for (int i = 0; i < dialogueText.length(); i++) {
            final int index = i;
            KeyFrame keyFrame =
                new KeyFrame(
                    Duration.millis(20 * i),
                    e -> {
                      // Append the next character to the dialogueLabel's text
                      dialogueLabel.setText(dialogueLabel.getText() + dialogueText.charAt(index));
                    });
            timeline.getKeyFrames().add(keyFrame);
          }

          // Play the text reveal timeline
          timeline.play();
        });
  }

  private void handleExit() {
    // Check if mediaPlayer is not null before stopping and disposing
    if (mediaPlayer != null) {
      try {
        mediaPlayer.stop(); // Stop the video if playing
        mediaPlayer.dispose(); // Release media player resources
      } catch (Exception e) {
        System.out.println("Nothing to stop or dispose.");
      }
    } else {
      System.out.println("mediaPlayer is already null, nothing to stop or dispose.");
    }
    // Perform any additional cleanup here if needed
  }
}
