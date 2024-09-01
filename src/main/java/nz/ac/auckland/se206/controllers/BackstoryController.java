package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;

public class BackstoryController {

  @FXML private AnchorPane anchorPane;

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

    anchorPane.setOpacity(0);
    FadeTransition fadeTransition = new FadeTransition();
    fadeTransition.setDuration(Duration.millis(1000));
    fadeTransition.setNode(anchorPane);
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    fadeTransition.play();

    // Desired width and height for the images
    double desiredWidth = 1000;
    double desiredHeight = 800;

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

    // Place the images into new ImageView Nodes and resize them
    final ImageView file1 = new ImageView(image1);
    final ImageView file2 = new ImageView(image2);
    final ImageView file3 = new ImageView(image3);
    final ImageView file4 = new ImageView(image4);
    final ImageView file5 = new ImageView(image5);
    final ImageView file6 = new ImageView(image6);
    final ImageView file7 = new ImageView(image7);
    final ImageView file8 = new ImageView(image8);
    final ImageView file9 = new ImageView(image9);
    final ImageView file10 = new ImageView(image10);

    // Resize images
    file1.setFitWidth(desiredWidth);
    file1.setFitHeight(desiredHeight);

    file2.setFitWidth(desiredWidth);
    file2.setFitHeight(desiredHeight);

    file3.setFitWidth(desiredWidth);
    file3.setFitHeight(desiredHeight);

    file4.setFitWidth(desiredWidth);
    file4.setFitHeight(desiredHeight);

    file5.setFitWidth(desiredWidth);
    file5.setFitHeight(desiredHeight);

    file6.setFitWidth(desiredWidth);
    file6.setFitHeight(desiredHeight);

    file7.setFitWidth(desiredWidth);
    file7.setFitHeight(desiredHeight);

    file8.setFitWidth(desiredWidth);
    file8.setFitHeight(desiredHeight);

    file9.setFitWidth(desiredWidth);
    file9.setFitHeight(desiredHeight);

    file10.setFitWidth(desiredWidth);
    file10.setFitHeight(desiredHeight);

    // Instantiate an object called file and add the first image
    file = new Group(file1);

    // Center the image within the pane
    centerImage(file1);

    // Animate file images in a loop
    Timeline timeline = new Timeline();

    // Add images into the timeline
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.seconds(1.4),
                e -> {
                  file.getChildren().setAll(file2);
                  centerImage(file2);
                }));
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.seconds(1.49),
                e -> {
                  file.getChildren().setAll(file3);
                  centerImage(file3);
                }));
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.seconds(1.58),
                e -> {
                  file.getChildren().setAll(file4);
                  centerImage(file4);
                }));
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.seconds(1.67),
                e -> {
                  file.getChildren().setAll(file5);
                  centerImage(file5);
                }));
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.seconds(1.76),
                e -> {
                  file.getChildren().setAll(file6);
                  centerImage(file6);
                }));
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.seconds(1.85),
                e -> {
                  file.getChildren().setAll(file7);
                  centerImage(file7);
                }));
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.seconds(1.94),
                e -> {
                  file.getChildren().setAll(file8);
                  centerImage(file8);
                }));
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.seconds(2.03),
                e -> {
                  file.getChildren().setAll(file9);
                  centerImage(file9);
                }));
    timeline
        .getKeyFrames()
        .add(
            new KeyFrame(
                Duration.seconds(2.12),
                e -> {
                  file.getChildren().setAll(file10);
                  centerImage(file10);
                }));
    timeline.play();

    // Set the cycle count to 1, so it only plays once
    timeline.setCycleCount(1);
    timeline.play();

    // Add the background image first, then the group to the anchorPane
    anchorPane.getChildren().addAll(backgroundImageView, file);

    // Add a listener to resize and recenter images when the anchorPane size changes
    anchorPane
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (file.getChildren().get(0) instanceof ImageView) {
                centerImage((ImageView) file.getChildren().get(0));
              }
            });
    anchorPane
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              if (file.getChildren().get(0) instanceof ImageView) {
                centerImage((ImageView) file.getChildren().get(0));
              }
            });

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

    // Create the enlarged version of the additional image
    ImageView enlargedImageView = new ImageView(additionalImage);
    enlargedImageView
        .fitWidthProperty()
        .bind(anchorPane.widthProperty().multiply(0.36)); // Slightly larger, 34% of the width
    enlargedImageView
        .fitHeightProperty()
        .bind(anchorPane.heightProperty().multiply(0.36)); // Slightly larger, 34% of the height
    enlargedImageView.setOpacity(0); // Start hidden

    // Add both images to the anchorPane
    anchorPane.getChildren().addAll(additionalImageView, enlargedImageView);

    // Add listeners to position the image closer to the right edge of the anchorPane
    anchorPane
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              additionalImageView.setLayoutX(
                  anchorPane.getWidth()
                      - additionalImageView.getFitWidth()
                      + 40); // 5px padding from the right
              enlargedImageView.setLayoutX(
                  anchorPane.getWidth()
                      - enlargedImageView.getFitWidth()
                      + 40); // Align enlarged image with original
            });

    anchorPane
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              additionalImageView.setLayoutY(
                  anchorPane.getHeight()
                      - additionalImageView.getFitHeight()
                      + 10); // 10px padding from the bottom
              enlargedImageView.setLayoutY(
                  anchorPane.getHeight()
                      - enlargedImageView.getFitHeight()
                      + 10); // Align enlarged image with original
            });

    // Set the fade transition for the additional image
    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), additionalImageView);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);

    // Trigger the fade-in transition after the timeline completes
    timeline.setOnFinished(e -> fadeIn.play());

    // Set hover event handlers
    additionalImageView.setOnMouseEntered(
        e -> {
          additionalImageView.setOpacity(0); // Hide the original image
          enlargedImageView.setOpacity(1); // Show the enlarged image
        });

    additionalImageView.setOnMouseExited(
        e -> {
          additionalImageView.setOpacity(1); // Show the original image
          enlargedImageView.setOpacity(0); // Hide the enlarged image
        });

    // Ensure the enlarged image responds to hover events as well
    enlargedImageView.setOnMouseExited(
        e -> {
          additionalImageView.setOpacity(1); // Show the original image
          enlargedImageView.setOpacity(0); // Hide the enlarged image
        });

    // Ensure the enlarged image responds to hover events as well
    enlargedImageView.setOnMouseEntered(
        e -> {
          additionalImageView.setOpacity(0); // Show the original image
          enlargedImageView.setOpacity(1); // Hide the enlarged image
        });

    additionalImageView.setOnMouseClicked(
        e -> {
          // Load the next scene after the zoom-in effect
          zoomIn(additionalImageView, "room");
        });

    enlargedImageView.setOnMouseClicked(
        e -> {
          // Load the next scene after the zoom-in effect
          zoomIn(enlargedImageView, "room");
        });
  }

  private void centerImage(ImageView imageView) {
    // Calculate the position to center the image within the anchorPane
    imageView.setX(((anchorPane.getWidth() - imageView.getFitWidth()) / 2) - 50);
    imageView.setY((anchorPane.getHeight() - imageView.getFitHeight()) / 2);
  }

  private void zoomIn(ImageView imageView, String nextScene) {
    // Scale transition for zooming in
    ScaleTransition zoomInTransition = new ScaleTransition(Duration.seconds(1), imageView);
    zoomInTransition.setToX(50.0); // Scale to 2x the original size
    zoomInTransition.setToY(50.0); // Scale to 2x the original size
    zoomInTransition.setCycleCount(1);
    zoomInTransition.setAutoReverse(false);

    // Get the current bounds of the ImageView
    Bounds bounds = imageView.localToScene(imageView.getBoundsInLocal());

    // Calculate the center positions to move to after scaling
    double sceneCenterX = anchorPane.getWidth() / 2;
    double sceneCenterY = anchorPane.getHeight() / 2;

    double imageCenterX = bounds.getMinX() + bounds.getWidth() / 2;
    double imageCenterY = bounds.getMinY() + bounds.getHeight() / 2;

    double translateX = sceneCenterX - imageCenterX + 750;
    double translateY = sceneCenterY - imageCenterY + 750;

    // Translate transition for moving to the center of the screen
    TranslateTransition moveToCenterTransition =
        new TranslateTransition(Duration.seconds(1), imageView);
    moveToCenterTransition.setByX(translateX);
    moveToCenterTransition.setByY(translateY);
    moveToCenterTransition.setCycleCount(1);
    moveToCenterTransition.setAutoReverse(false);

    // Rotate transition for rotating the image by 45 degrees
    RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), imageView);
    rotateTransition.setByAngle(20); // Rotate by 10 degrees to the right
    rotateTransition.setCycleCount(1);
    rotateTransition.setAutoReverse(false);

    // Fade transition for fading out the entire scene
    FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(1), anchorPane);
    fadeOutTransition.setFromValue(1.0);
    fadeOutTransition.setToValue(0.0);
    fadeOutTransition.setCycleCount(1);
    fadeOutTransition.setAutoReverse(false);

    // Combine the zoom, move, fade out, and rotate transitions in a parallel transition
    ParallelTransition parallelTransition =
        new ParallelTransition(
            zoomInTransition, moveToCenterTransition, rotateTransition, fadeOutTransition);

    // Add event handler to change the scene after the transitions complete
    parallelTransition.setOnFinished(
        event -> {
          try {
            App.setRoot(nextScene);
          } catch (IOException e) {
            e.printStackTrace();
          }
        });

    // Play the combined transitions
    parallelTransition.play();
  }
}
