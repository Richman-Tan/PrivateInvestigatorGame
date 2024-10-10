package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;

/**
 * Controller class for the cutscene view. This class is responsible for playing the cutscene video
 * and displaying the text letter by letter.
 */
public class CutSceneController {

  // Static fields
  private static final String revealText = "THE WILL???"; // Text to display letter by letter

  // Instance fields
  @FXML private AnchorPane rootPane;
  @FXML private Pane labelPane; // Pane for the revealLabel
  @FXML private Label timerLabel; // Label for the countdown timer
  private TimerModel countdownTimer;
  private MediaPlayer mediaPlayer; // Declare mediaPlayer as an instance variable
  private Label revealLabel; // Label for text reveal
  private SVGPath volumeUpStroke = new SVGPath();
  private SVGPath volumeUp = new SVGPath();
  private SVGPath volumeOff = new SVGPath();
  private BorderPane dialogPane; // BorderPane for the dialog
  private MediaPlayer gaspPlayer;

  // Static methods
  // No static methods for now

  // Instance methods

  /**
   * Initializes the Cutscene after the associated FXML has been loaded.
   *
   * <p>This method is automatically called by the JavaFX framework when the FXML file for the
   * cutscnene view is loaded. It sets up the initial state of the cutscene controller by
   * configuring UI components, binding properties, and initializing any necessary data structures
   * or event listeners required for the controller's functionality.
   */
  public void initialize() {
    // Load background video using the correct class reference for resource path
    String videoPath =
        CutSceneController.class
            .getResource("/images/startingcutscene.mp4") // Get the resource path
            .toExternalForm(); // Convert the resource path to an external form
    Media media = new Media(videoPath);
    mediaPlayer = new MediaPlayer(media);
    BooleanProperty volumeSettingProperty =
        SharedVolumeControl.getInstance().volumeSettingProperty();

    // Bind the mediaPlayer's volume property using a DoubleBinding
    mediaPlayer
        .volumeProperty()
        .bind(
            Bindings.createDoubleBinding(
                () ->
                    volumeSettingProperty.get()
                        ? 1.0
                        : 0.0, // Use 1.0 for full volume if true, otherwise 0.0 for mute
                volumeSettingProperty));

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

    // Show the volume button
    showVolumeButton();

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

    // Bind the timerLabel to the countdown timer
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());

    labelPane.toFront(); // Bring the labelPane to the front
    volumeUp.toFront();
    volumeUpStroke.toFront();
    volumeOff.toFront();
  }

  /**
   * Creates the revealLabel and adds it to the rootPane. The label is initially invisible and
   * empty.
   */
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

  /**
   * Centers the revealLabel in the rootPane. This method should be called whenever the size of the
   */
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

    // Create a new MediaPlayer instance for the gasp sound
    Media gaspSound =
        new Media(getClass().getClassLoader().getResource("sounds/gasp.mp3").toExternalForm());
    gaspPlayer = new MediaPlayer(gaspSound);
    gaspPlayer.play(); // Play the gasp sound

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
    PauseTransition pause = new PauseTransition(Duration.seconds(1.5));

    // Play zoom in, pause, then zoom out
    zoomIn.setOnFinished(event -> pause.play());
    pause.setOnFinished(event -> zoomOut.play());
    zoomIn.play();
    zoomOut.setOnFinished(
        event -> {
          // Remove the label
          rootPane.getChildren().remove(revealLabel);

          // Main dialog pane (background)
          dialogPane = new BorderPane();
          dialogPane.setOpacity(0.75);
          dialogPane.setStyle(
              "-fx-background-color: #c1b8b5; -fx-background-radius: 10px; -fx-border-radius: 10px;"
                  + " -fx-border-color: #3f2218; -fx-border-width: 4px;");
          dialogPane.setBackground(
              new Background(
                  new BackgroundFill(
                      Color.rgb(255, 255, 255, 0.8), new CornerRadii(20), Insets.EMPTY)));
          dialogPane.setPadding(new Insets(10));

          // Label for character name
          Label nameLabel = new Label("?????");
          nameLabel.setFont(new Font("Arial", 18));
          nameLabel.setTextFill(Color.BLACK);
          nameLabel.setStyle(
              "-fx-background-color: #c1b8b5; -fx-background-radius: 10px; -fx-border-radius: 10px;"
                  + " -fx-border-color: #3f2218; -fx-border-width: 4px;");

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
          AnchorPane.setBottomAnchor(dialogPane, 40.0);
          AnchorPane.setLeftAnchor(dialogPane, 50.0);
          AnchorPane.setRightAnchor(dialogPane, 50.0);

          Button nextButton = new Button("CALL PI MASTERS");
          nextButton.setStyle(
              "-fx-font-weight: bold; -fx-background-color: #775E55; -fx-border-color: #3f2218;"
                  + " -fx-border-radius: 20; -fx-padding: 5; -fx-border-width: 3;"
                  + " -fx-background-radius: 20; -fx-text-fill: #c1b8b5;");
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
                    "-fx-font-weight: bold; -fx-background-color: #c1b8b5; -fx-background-radius:"
                        + " 20px; -fx-border-radius: 20px; -fx-border-color: #3f2218;"
                        + " -fx-border-width: 4px;-fx-padding: 5; -fx-border-width: 3; -fx-cursor:"
                        + " hand; -fx-text-fill: #775E55;");
              }); // Change to pointer on hover

          nextButton.setOnMouseExited(
              e -> {
                nextButton.setStyle(
                    "-fx-font-weight: bold; -fx-background-color: #775E55; -fx-background-radius:"
                        + " 20px; -fx-border-radius: 20px; -fx-border-color: #3f2218;"
                        + " -fx-border-width: 4px;-fx-padding: 5; -fx-border-width: 3; -fx-cursor:"
                        + " default; -fx-text-fill: #c1b8b5;");
                nextButton.setOpacity(1);
              }); // Restore to default on exit

          // Place the nextButton at the bottom right
          AnchorPane.setBottomAnchor(nextButton, 20.0);
          AnchorPane.setRightAnchor(nextButton, 50.0);

          dialogPane.setOpacity(0);
          nameLabel.setOpacity(0);
          nextButton.setOpacity(0);

          // Add everything to the root pane
          rootPane.getChildren().addAll(dialogPane, nameLabel, nextButton);

          // Create a fade-in transition for the dialog pane
          FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), dialogPane);
          fadeIn.setFromValue(0); // Start fully transparent
          fadeIn.setToValue(0.75); // End fully visible
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
              "Oh no! The will is missing! Who could've taken it? The FAMILY LAWYER is coming in 5"
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
          timeline.setOnFinished(
              e -> {
                // Enable the next button after the text reveal is complete
                //  showArrow();
                showPhone();
              });
        });
  }

  /**
   * Method to handle the close request. This method is called when the window is closed. It stops
   */
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

  /*
   * Method to show the phone image
   */
  private void showPhone() {
    ImageView phoneImg = new ImageView();

    // Load the image for the arrow (make sure the path is correct)
    Image phone = new Image("/images/phone.png");
    phoneImg.setImage(phone); // Set the image to the ImageView

    // Set the initial size and position for the ImageView
    phoneImg.rotateProperty().set(105);
    phoneImg.setFitWidth(80);
    phoneImg.setFitHeight(56);
    phoneImg.setLayoutX(840); // Set X position
    phoneImg.setLayoutY(525); // Set Y position

    // Set the opacity (1 is fully visible)
    phoneImg.setOpacity(1);

    // Add the ImageView to the rootPane
    rootPane.getChildren().add(phoneImg);
  }

  /*
   * Method to initialise and show the volume button
   */
  private void showVolumeButton() {
    // create new SVGPath for volume button
    volumeUpStroke.setContent(
        "M10.121 12.596A6.48 6.48 0 0 0 12.025 8a6.48 6.48 0 0 0-1.904-4.596l-.707.707A5.48 5.48 0"
            + " 0 1 11.025 8a5.48 5.48 0 0 1-1.61 3.89z");
    volumeUp.setContent(
        "M8.707 11.182A4.5 4.5 0 0 0 10.025 8a4.5 4.5 0 0 0-1.318-3.182L8 5.525A3.5 3.5 0 0 1 9.025"
            + " 8 3.5 3.5 0 0 1 8 10.475zM6.717 3.55A.5.5 0 0 1 7 4v8a.5.5 0 0 1-.812.39L3.825"
            + " 10.5H1.5A.5.5 0 0 1 1 10V6a.5.5 0 0 1 .5-.5h2.325l2.363-1.89a.5.5 0 0 1 .529-.06");
    volumeOff.setContent(
        "M6.717 3.55A.5.5 0 0 1 7 4v8a.5.5 0 0 1-.812.39L3.825 10.5H1.5A.5.5 0 0 1 1 10V6a.5.5 0 0"
            + " 1 .5-.5h2.325l2.363-1.89a.5.5 0 0 1 .529-.06m7.137 2.096a.5.5 0 0 1 0 .708L12.207"
            + " 8l1.647 1.646a.5.5 0 0 1-.708.708L11.5 8.707l-1.646 1.647a.5.5 0 0"
            + " 1-.708-.708L10.793 8 9.146 6.354a.5.5 0 1 1 .708-.708L11.5 7.293l1.646-1.647a.5.5 0"
            + " 0 1 .708 0");

    // Set the size and position for the SVGPath
    volumeUp.setScaleY(2.0);
    volumeUp.setScaleX(2.0);
    volumeUp.setScaleZ(2.0);
    volumeUp.setLayoutX(23);
    volumeUp.setLayoutY(63);
    volumeUp.setStroke(Color.web("#473931"));
    volumeUp.setFill(Color.web("#ffffff94"));
    volumeUp.setStrokeWidth(0.5);
    volumeUp.setOnMouseClicked(
        event -> {
          try {
            turnVolumeOff();
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
    rootPane.getChildren().add(volumeUp);

    // Set the size and position for the SVGPath
    volumeUpStroke.setScaleY(2.0);
    volumeUpStroke.setScaleX(2.0);
    volumeUpStroke.setScaleZ(2.0);
    volumeUpStroke.setLayoutX(29);
    volumeUpStroke.setLayoutY(63);
    volumeUpStroke.setStroke(Color.web("#473931"));
    volumeUpStroke.setFill(Color.web("#ffffff94"));
    volumeUpStroke.setStrokeWidth(0.5);
    volumeUpStroke.setOnMouseClicked(
        event -> {
          try {
            turnVolumeOff();
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
    rootPane.getChildren().add(volumeUpStroke);

    // Set the size and position for the SVGPath
    volumeOff.setScaleY(2.0);
    volumeOff.setScaleX(2.0);
    volumeOff.setScaleZ(2.0);
    volumeOff.setLayoutX(23);
    volumeOff.setLayoutY(63);
    volumeOff.setStroke(Color.web("#473931"));
    volumeOff.setFill(Color.web("#ffffff94"));
    volumeOff.setStrokeWidth(0.5);
    volumeOff.setVisible(false);
    volumeOff.setOnMouseClicked(
        event -> {
          try {
            turnVolumeOn();
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
    rootPane.getChildren().add(volumeOff);

    try {
      checkVolumeIcon();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * Method to turn the volume off
   */
  @FXML
  protected void turnVolumeOff() throws IOException {
    SharedVolumeControl.getInstance().setVolumeSetting(false);
    volumeOff.setVisible(true);
    volumeUp.setVisible(false);
    volumeUpStroke.setVisible(false);
  }

  /*
   * Method to turn the volume on
   */
  @FXML
  protected void turnVolumeOn() throws IOException {
    SharedVolumeControl.getInstance().setVolumeSetting(true);
    volumeOff.setVisible(false);
    volumeUp.setVisible(true);
    volumeUpStroke.setVisible(true);
  }

  /*
   * Method to check if the volume icon should be displayed
   */
  private void checkVolumeIcon() throws IOException {
    if (SharedVolumeControl.getInstance().getVolumeSetting()) {
      turnVolumeOn();
    } else {
      turnVolumeOff();
    }
  }
}
