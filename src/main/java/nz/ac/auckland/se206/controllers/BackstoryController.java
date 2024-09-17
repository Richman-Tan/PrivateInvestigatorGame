package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.states.GameStarted;

public class BackstoryController {

  @FXML private AnchorPane anchorPane;
  private MediaPlayer mediaPlayer;
  private final String sound =
      GameStarted.class.getClassLoader().getResource("sounds/woosh.mp3").toExternalForm();

  // Load images from the images folder
  static final Image image1 =
      new Image(BackstoryController.class.getResource("/images/fileframe1.PNG").toString());
  static final Image image2 =
      new Image(BackstoryController.class.getResource("/images/fileframe2.PNG").toString());
  static final Image image3 =
      new Image(BackstoryController.class.getResource("/images/fileframe3.PNG").toString());
  static final Image image4 =
      new Image(BackstoryController.class.getResource("/images/fileframe4.PNG").toString());
  static final Image image5 =
      new Image(BackstoryController.class.getResource("/images/fileframe5.PNG").toString());
  static final Image image6 =
      new Image(BackstoryController.class.getResource("/images/fileframe6.PNG").toString());
  static final Image image7 =
      new Image(BackstoryController.class.getResource("/images/fileframe7.PNG").toString());
  static final Image image8 =
      new Image(BackstoryController.class.getResource("/images/fileframe8.PNG").toString());
  static final Image image9 =
      new Image(BackstoryController.class.getResource("/images/fileframe9.PNG").toString());
  static final Image image10 =
      new Image(BackstoryController.class.getResource("/images/fileframe10.PNG").toString());

  // Create a group node
  private Group file;

  /** Initializes the backstory view. */
  @FXML
  public void initialize() {

    // Run the fade transition on the JavaFX Application Thread
    Platform.runLater(
        () -> {
          anchorPane.setOpacity(0);
          FadeTransition fadeTransition = new FadeTransition();
          fadeTransition.setDuration(Duration.millis(1000));
          fadeTransition.setNode(anchorPane);
          fadeTransition.setFromValue(0);
          fadeTransition.setToValue(1);
          fadeTransition.play();
        });

    // Load background image
    Image backgroundImage =
        new Image(
            BackstoryController.class.getResource("/images/blurredcrimescene.png").toString());

    // Create the background ImageView and set it to fill the entire pane
    ImageView backgroundImageView = new ImageView(backgroundImage);
    backgroundImageView.setFitWidth(anchorPane.getWidth());
    backgroundImageView.setFitHeight(anchorPane.getHeight());

    // Make sure the background resizes with the window
    backgroundImageView.fitWidthProperty().bind(anchorPane.widthProperty());
    backgroundImageView.fitHeightProperty().bind(anchorPane.heightProperty());

    // Create a DropShadow effect
    DropShadow dropShadow = new DropShadow();
    dropShadow.setOffsetX(5); // Smaller offset
    dropShadow.setOffsetY(5);
    dropShadow.setRadius(6); // Reduce blur radius
    dropShadow.setSpread(0.07); // Lower spread for less intensity
    dropShadow.setColor(Color.color(0, 0, 0, 0.4)); // Less opacity for lighter shadow

    // Load and prepare ImageView nodes for animation
    final ImageView file1 = createAndBindImageView(image1);
    final ImageView file2 = createAndBindImageView(image2);
    final ImageView file3 = createAndBindImageView(image3);
    final ImageView file4 = createAndBindImageView(image4);
    final ImageView file5 = createAndBindImageView(image5);
    final ImageView file6 = createAndBindImageView(image6);
    final ImageView file7 = createAndBindImageView(image7);
    final ImageView file8 = createAndBindImageView(image8);
    final ImageView file9 = createAndBindImageView(image9);
    final ImageView file10 = createAndBindImageView(image10);

    // Apply the DropShadow effect to each frame
    applyDropShadow(
        dropShadow, file1, file2, file3, file4, file5, file6, file7, file8, file9, file10);

    // Instantiate an object called file and add the first image
    file = new Group(file1);

    // Center the image within the pane
    centerImage(file1);

    // Animate file images in a loop
    Timeline timeline = new Timeline();

    // Add images into the timeline
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(1.4), e -> showNextImage(file, file2)));
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(1.49), e -> showNextImage(file, file3)));
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(1.58), e -> showNextImage(file, file4)));
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(1.67), e -> showNextImage(file, file5)));
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(1.76), e -> showNextImage(file, file6)));
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(1.85), e -> showNextImage(file, file7)));
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(1.94), e -> showNextImage(file, file8)));
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(2.03), e -> showNextImage(file, file9)));
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(2.12), e -> showNextImage(file, file10)));
    timeline.play();

    // Set the cycle count to 1, so it only plays once
    timeline.setCycleCount(1);
    timeline.play();

    // Add the background image first, then the group to the anchorPane
    anchorPane.getChildren().addAll(backgroundImageView, file);

    // Add listeners to resize and recenter images when the AnchorPane size changes
    anchorPane.widthProperty().addListener((obs, oldVal, newVal) -> recenterCurrentImage(file));
    anchorPane.heightProperty().addListener((obs, oldVal, newVal) -> recenterCurrentImage(file));

    // Load the additional image to be shown at the bottom right
    Image additionalImage =
        new Image(
            BackstoryController.class.getResource("/images/magnifyingglassbtn.png").toString());
    ImageView additionalImageView = new ImageView(additionalImage);

    // Bind the size of the additional image to a fraction of the AnchorPane's size
    additionalImageView
        .fitWidthProperty()
        .bind(anchorPane.widthProperty().multiply(0.32)); // 30% of the anchor pane's width
    additionalImageView
        .fitHeightProperty()
        .bind(anchorPane.heightProperty().multiply(0.32)); // 30% of the anchor pane's height
    additionalImageView.setOpacity(0); // Start with the image invisible
    additionalImageView.setDisable(true); // Disable the image to prevent interaction

    // Create the enlarged version of the additional image
    ImageView enlargedImageView = new ImageView(additionalImage);
    enlargedImageView
        .fitWidthProperty()
        .bind(anchorPane.widthProperty().multiply(0.36)); // Slightly larger, 34% of the width
    enlargedImageView
        .fitHeightProperty()
        .bind(anchorPane.heightProperty().multiply(0.36)); // Slightly larger, 34% of the height
    enlargedImageView.setOpacity(0); // Start hidden
    enlargedImageView.setDisable(true); // Disable the image to prevent interaction

    // Add both images to the anchorPane
    anchorPane.getChildren().addAll(additionalImageView, enlargedImageView);

    // Add listeners to position the image closer to the right edge of the anchorPane
    anchorPane
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) -> updateImagePosition(additionalImageView, enlargedImageView));
    anchorPane
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> updateImagePosition(additionalImageView, enlargedImageView));

    // Set the fade transition for the additional image
    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), additionalImageView);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);

    // Trigger the fade-in transition after the timeline completes
    timeline.setOnFinished(e -> fadeIn.play());

    // Load the sound effect
    Media woosh = new Media(sound);
    mediaPlayer = new MediaPlayer(woosh);

    fadeIn.setOnFinished(
        event -> {
          additionalImageView.setDisable(false);
          enlargedImageView.setDisable(false);
        });

    // Set hover event handlers
    setHoverHandlers(additionalImageView, enlargedImageView);

    additionalImageView.setOnMouseClicked(e -> zoomIn(additionalImageView, "room"));
    enlargedImageView.setOnMouseClicked(e -> zoomIn(enlargedImageView, "room"));
  }

  private ImageView createAndBindImageView(Image image) {
    ImageView imageView = new ImageView(image);
    imageView
        .fitWidthProperty()
        .bind(anchorPane.widthProperty().multiply(1.2)); // 80% of AnchorPane width
    imageView
        .fitHeightProperty()
        .bind(anchorPane.heightProperty().multiply(1.2)); // 80% of AnchorPane height
    imageView.setPreserveRatio(true); // Preserve aspect ratio
    imageView.setCache(true);
    imageView.setCacheHint(CacheHint.SPEED);
    return imageView;
  }

  private void applyDropShadow(DropShadow dropShadow, ImageView... imageViews) {
    for (ImageView imageView : imageViews) {
      imageView.setEffect(dropShadow);
    }
  }

  private void showNextImage(Group file, ImageView nextImage) {
    file.getChildren().setAll(nextImage);
    centerImage(nextImage);
  }

  private void recenterCurrentImage(Group file) {
    if (file.getChildren().get(0) instanceof ImageView) {
      centerImage((ImageView) file.getChildren().get(0));
    }
  }

  private void updateImagePosition(ImageView additionalImageView, ImageView enlargedImageView) {
    additionalImageView.setLayoutX(anchorPane.getWidth() - additionalImageView.getFitWidth() + 40);
    enlargedImageView.setLayoutX(anchorPane.getWidth() - enlargedImageView.getFitWidth() + 40);
    additionalImageView.setLayoutY(
        anchorPane.getHeight() - additionalImageView.getFitHeight() + 10);
    enlargedImageView.setLayoutY(anchorPane.getHeight() - enlargedImageView.getFitHeight() + 10);
  }

  private void setHoverHandlers(ImageView additionalImageView, ImageView enlargedImageView) {
    additionalImageView.setOnMouseEntered(
        e -> {
          additionalImageView.setOpacity(0); // Hide the original image
          enlargedImageView.setOpacity(1); // Show the enlarged image
          additionalImageView.setCursor(javafx.scene.Cursor.HAND); // Change cursor to hand
        });

    additionalImageView.setOnMouseExited(
        e -> {
          additionalImageView.setOpacity(1); // Show the original image
          enlargedImageView.setOpacity(0); // Hide the enlarged image
          additionalImageView.setCursor(javafx.scene.Cursor.DEFAULT); // Restore default cursor
        });

    enlargedImageView.setOnMouseEntered(
        e -> {
          additionalImageView.setOpacity(0); // Hide the original image
          enlargedImageView.setOpacity(1); // Show the enlarged image
          enlargedImageView.setCursor(javafx.scene.Cursor.HAND); // Change cursor to hand
        });

    enlargedImageView.setOnMouseExited(
        e -> {
          additionalImageView.setOpacity(1); // Show the original image
          enlargedImageView.setOpacity(0); // Hide the enlarged image
          enlargedImageView.setCursor(javafx.scene.Cursor.DEFAULT); // Restore default cursor
        });
  }

  private void centerImage(ImageView imageView) {
    // Calculate the position to center the image within the anchorPane
    imageView.setX(((anchorPane.getWidth() - imageView.getFitWidth()) / 2) - 50);
    imageView.setY((anchorPane.getHeight() - imageView.getFitHeight()) / 2);
  }

  private void zoomIn(ImageView imageView, String nextScene) {
    // Create a new thread to run the zoom-in logic
    new Thread(
            () -> {
              // All UI updates need to be run on the JavaFX Application Thread
              Platform.runLater(
                  () -> {
                    // Scale transition for zooming in
                    ScaleTransition zoomInTransition =
                        new ScaleTransition(Duration.seconds(1), imageView);
                    zoomInTransition.setToX(6.0); // Scale to 8x the original size
                    zoomInTransition.setToY(6.0); // Scale to 8x the original size
                    zoomInTransition.setCycleCount(1);
                    zoomInTransition.setAutoReverse(false);

                    // Get the current bounds of the ImageView
                    Bounds bounds = imageView.localToScene(imageView.getBoundsInLocal());

                    // Calculate the center positions to move to after scaling
                    double sceneCenterX = anchorPane.getWidth() / 2;
                    double sceneCenterY = anchorPane.getHeight() / 2;

                    double imageCenterX = bounds.getMinX() + bounds.getWidth() / 2;
                    double imageCenterY = bounds.getMinY() + bounds.getHeight() / 2;

                    double translateX = sceneCenterX - imageCenterX + 250;
                    double translateY = sceneCenterY - imageCenterY + 300;

                    // Translate transition for moving to the center of the screen
                    TranslateTransition moveToCenterTransition =
                        new TranslateTransition(Duration.seconds(0.5), imageView);
                    moveToCenterTransition.setByX(translateX);
                    moveToCenterTransition.setByY(translateY);
                    moveToCenterTransition.setCycleCount(1);
                    moveToCenterTransition.setAutoReverse(false);

                    // Rotate transition for rotating the image by 45 degrees
                    RotateTransition rotateTransition =
                        new RotateTransition(Duration.seconds(1), imageView);
                    rotateTransition.setByAngle(20); // Rotate by 20 degrees to the right
                    rotateTransition.setCycleCount(1);
                    rotateTransition.setAutoReverse(false);

                    // Combine the zoom, move, fade out, and rotate transitions in a parallel
                    // transition
                    ParallelTransition parallelTransition =
                        new ParallelTransition(
                            zoomInTransition, moveToCenterTransition, rotateTransition);

                    // Play the combined transitions
                    parallelTransition.play();
                  });
            })
        .start(); // Start the thread

    // Run another thread
    new Thread(
            () -> {
              // All UI updates need to be run on the JavaFX Application Thread
              Platform.runLater(
                  () -> {
                    // Fade transition for fading out the entire scene
                    FadeTransition fadeOutTransition =
                        new FadeTransition(Duration.seconds(0.5), anchorPane);
                    fadeOutTransition.setFromValue(1.0);
                    fadeOutTransition.setToValue(0.0);
                    fadeOutTransition.setCycleCount(1);
                    fadeOutTransition.setAutoReverse(false);

                    // Pause transition to delay the fade-out
                    PauseTransition pauseTransition =
                        new PauseTransition(
                            Duration.seconds(0.5)); // Halfway through the parallel transition

                    // Sequential transition to start the fade-out after the pause
                    SequentialTransition sequentialTransition =
                        new SequentialTransition(pauseTransition, fadeOutTransition);

                    // Add event handler to change the scene after the transitions complete
                    fadeOutTransition.setOnFinished(
                        event -> {
                          try {
                            App.setRoot(nextScene);
                          } catch (IOException e) {
                            e.printStackTrace();
                          }
                        });

                    sequentialTransition.play();
                  });
            })
        .start(); // Start the thread
  }
}
