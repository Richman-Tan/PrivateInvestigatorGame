package nz.ac.auckland.se206.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

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
  }

  private void centerImage(ImageView imageView) {
    // Calculate the position to center the image within the anchorPane
    imageView.setX(((anchorPane.getWidth() - imageView.getFitWidth()) / 2) - 50);
    imageView.setY((anchorPane.getHeight() - imageView.getFitHeight()) / 2);
  }
}
