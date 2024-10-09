package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.states.GameStarted;

public class StartGameController {

  // Static fields
  private static final String door =
      GameStarted.class.getClassLoader().getResource("sounds/doorOpen.mp3").toExternalForm();
  private static final String backgroundMusic =
      GameStarted.class.getClassLoader().getResource("sounds/start.mp3").toExternalForm();

  static final Image image1 =
      new Image(BackstoryController.class.getResource("/images/initialDoor.jpg").toString());
  static final Image image2 =
      new Image(BackstoryController.class.getResource("/images/doorframe2.PNG").toString());
  static final Image image3 =
      new Image(BackstoryController.class.getResource("/images/doorframe3.PNG").toString());
  static final Image image4 =
      new Image(BackstoryController.class.getResource("/images/doorframe4.PNG").toString());
  static final Image image5 =
      new Image(BackstoryController.class.getResource("/images/doorframe5.PNG").toString());
  static final Image image6 =
      new Image(BackstoryController.class.getResource("/images/doorframe6.PNG").toString());
  static final Image image7 =
      new Image(BackstoryController.class.getResource("/images/doorframe7.PNG").toString());
  static final Image image8 =
      new Image(BackstoryController.class.getResource("/images/doorframe8.PNG").toString());
  static final Image image9 =
      new Image(BackstoryController.class.getResource("/images/doorframe9.PNG").toString());

  // Instance fields
  @FXML private AnchorPane rootPane;
  private MediaPlayer mediaPlayer;
  private ImageView imageView;
  private MediaPlayer backgroundPlayer;

  // Constructors

  // Instance methods
  /** Initializes the start view. */
  @FXML
  public void initialize() {
    // Initialize the ImageView with image1 and set it to take up the whole screen
    imageView = new ImageView(image1);
    imageView.setFitWidth(rootPane.getWidth());
    imageView.setFitHeight(rootPane.getHeight());
    imageView.fitWidthProperty().bind(rootPane.widthProperty());
    imageView.fitHeightProperty().bind(rootPane.heightProperty());

    // Add the imageView to the rootPane
    rootPane.getChildren().add(imageView);

    // Load and play the background music
    Media backgroundSound = new Media(backgroundMusic);
    backgroundPlayer = new MediaPlayer(backgroundSound);
    backgroundPlayer.setVolume(0.1); // Set volume to 50%
    BooleanProperty volumeSettingProperty =
        SharedVolumeControl.getInstance().volumeSettingProperty();
    backgroundPlayer
        .volumeProperty()
        .bind(
            Bindings.createDoubleBinding(
                () ->
                    volumeSettingProperty.get() ? 0.1 : 0.0, // Full volume or mute based on setting
                volumeSettingProperty));
    backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the background music
    backgroundPlayer.play();

    // Set up imageView as a button
    setupImageViewAsButton();
  }

  /** Sets up the ImageView to act as a button. */
  private void setupImageViewAsButton() {
    // Set hover effects to change the cursor to a pointer (hand)
    imageView.setOnMouseEntered(
        e -> imageView.setStyle("-fx-cursor: hand;")); // Change to pointer on hover
    imageView.setOnMouseExited(
        e -> imageView.setStyle("-fx-cursor: default;")); // Restore to default on exit

    // Load the audio file
    Media sound = new Media(door);
    mediaPlayer = new MediaPlayer(sound);
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

    // Set click action
    imageView.setOnMouseClicked(
        (var e) -> {
          try {
            mediaPlayer.seek(Duration.seconds(1)); // Seek to start of door sound
            if (!SharedVolumeControl.getInstance().getVolumeSetting()) {
              mediaPlayer.setVolume(0);
            }
            mediaPlayer.play();
            backgroundPlayer.stop(); // Stop background music when door sound plays
            onPlay();
          } catch (IOException | ApiProxyException ex) {
            ex.printStackTrace();
          }
        });
  }

  /*
   * View with image rotation.
   */
  @FXML
  private void onPlay() throws IOException, ApiProxyException {
    // Create a timeline to rotate through the images
    Timeline timeline = new Timeline();
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(0.06), e -> imageView.setImage(image3)));

    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(0.12), e -> imageView.setImage(image4)));

    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(0.18), e -> imageView.setImage(image5)));

    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(0.24), e -> imageView.setImage(image6)));

    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(0.3), e -> imageView.setImage(image7)));

    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(0.36), e -> imageView.setImage(image8)));

    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(0.42), e -> imageView.setImage(image9)));

    // After the timeline, add the fade transition to switch to the backstory view
    timeline.setOnFinished(
        e -> {
          FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000));
          fadeTransition.setNode(rootPane);
          fadeTransition.setFromValue(1);
          fadeTransition.setToValue(0);
          fadeTransition.setOnFinished(
              event -> {
                try {
                  App.setRoot("cutscene");
                } catch (IOException ex) {
                  ex.printStackTrace();
                }
              });
          fadeTransition.play();
        });

    // Start the timeline
    timeline.play();
  }
}
