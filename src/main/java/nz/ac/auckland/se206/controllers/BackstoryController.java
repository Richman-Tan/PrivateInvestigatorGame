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
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.states.GameStarted;

public class BackstoryController {

  // Static fields
  private static final String sound =
      GameStarted.class.getClassLoader().getResource("sounds/woosh.mp3").toExternalForm();
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

  // Instance fields
  @FXML private AnchorPane anchorPane;
  private MediaPlayer mediaPlayer;
  private TimerModel countdownTimer;
  private Group file;

  // Constructors (if any)

  // Instance methods
  /** Initializes the backstory view. */
  @FXML
  public void initialize() {
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

    Image backgroundImage =
        new Image(
            BackstoryController.class.getResource("/images/blurredcrimescene.png").toString());

    ImageView backgroundImageView = new ImageView(backgroundImage);
    backgroundImageView.setFitWidth(anchorPane.getWidth());
    backgroundImageView.setFitHeight(anchorPane.getHeight());
    backgroundImageView.fitWidthProperty().bind(anchorPane.widthProperty());
    backgroundImageView.fitHeightProperty().bind(anchorPane.heightProperty());

    DropShadow dropShadow = new DropShadow();
    dropShadow.setOffsetX(5);
    dropShadow.setOffsetY(5);
    dropShadow.setRadius(6);
    dropShadow.setSpread(0.07);
    dropShadow.setColor(Color.color(0, 0, 0, 0.4));

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

    applyDropShadow(
        dropShadow, file1, file2, file3, file4, file5, file6, file7, file8, file9, file10);

    file = new Group(file1);
    centerImage(file1);

    Timeline timeline = new Timeline();
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
    timeline.setCycleCount(1);
    timeline.play();

    anchorPane.getChildren().addAll(backgroundImageView, file);
    anchorPane.widthProperty().addListener((obs, oldVal, newVal) -> recenterCurrentImage(file));
    anchorPane.heightProperty().addListener((obs, oldVal, newVal) -> recenterCurrentImage(file));

    Image additionalImage =
        new Image(
            BackstoryController.class.getResource("/images/magnifyingglassbtn.png").toString());
    ImageView additionalImageView = new ImageView(additionalImage);
    additionalImageView.fitWidthProperty().bind(anchorPane.widthProperty().multiply(0.32));
    additionalImageView.fitHeightProperty().bind(anchorPane.heightProperty().multiply(0.32));
    additionalImageView.setOpacity(0);
    additionalImageView.setDisable(true);

    ImageView enlargedImageView = new ImageView(additionalImage);
    enlargedImageView.fitWidthProperty().bind(anchorPane.widthProperty().multiply(0.36));
    enlargedImageView.fitHeightProperty().bind(anchorPane.heightProperty().multiply(0.36));
    enlargedImageView.setOpacity(0);
    enlargedImageView.setDisable(true);

    anchorPane.getChildren().addAll(additionalImageView, enlargedImageView);
    anchorPane
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) -> updateImagePosition(additionalImageView, enlargedImageView));
    anchorPane
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> updateImagePosition(additionalImageView, enlargedImageView));

    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), additionalImageView);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);
    timeline.setOnFinished(e -> fadeIn.play());

    Media woosh = new Media(sound);
    mediaPlayer = new MediaPlayer(woosh);

    fadeIn.setOnFinished(
        event -> {
          additionalImageView.setDisable(false);
          enlargedImageView.setDisable(false);
        });

    setHoverHandlers(additionalImageView, enlargedImageView);
    additionalImageView.setOnMouseClicked(e -> zoomIn(additionalImageView, "room"));
    enlargedImageView.setOnMouseClicked(e -> zoomIn(enlargedImageView, "room"));

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
    timerLabel.setText("Label");
    timerLabel.setFont(new Font(24));
    timerLabel.setAlignment(Pos.CENTER);
    timerLabel.setLayoutX(21.0);
    timerLabel.setLayoutY(8.0);

    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());

    timerPane.getChildren().add(timerLabel);
    anchorPane.getChildren().add(timerPane);
  }

  // Inner classes (if any)

  // Static methods (if any)

  /**
   * Creates an ImageView and binds its width and height to the width and height of the anchorPane.
   * The ImageView is then returned.
   *
   * @param image
   * @return
   */
  private ImageView createAndBindImageView(Image image) {
    ImageView imageView = new ImageView(image);
    imageView.fitWidthProperty().bind(anchorPane.widthProperty().multiply(1.2));
    imageView.fitHeightProperty().bind(anchorPane.heightProperty().multiply(1.2));
    imageView.setPreserveRatio(true);
    imageView.setCache(true);
    imageView.setCacheHint(CacheHint.SPEED);
    return imageView;
  }

  /**
   * Applies a drop shadow effect to the specified imageViews.
   *
   * @param dropShadow
   * @param imageViews
   */
  private void applyDropShadow(DropShadow dropShadow, ImageView... imageViews) {
    for (ImageView imageView : imageViews) {
      imageView.setEffect(dropShadow);
    }
  }

  /**
   * Shows the next image in the file.
   *
   * @param file
   * @param nextImage
   */
  private void showNextImage(Group file, ImageView nextImage) {
    file.getChildren().setAll(nextImage);
    centerImage(nextImage);
  }

  /**
   * Recenters the current image in the file.
   *
   * @param file
   */
  private void recenterCurrentImage(Group file) {
    if (file.getChildren().get(0) instanceof ImageView) {
      centerImage((ImageView) file.getChildren().get(0));
    }
  }

  /**
   * Updates the position of the additional and enlarged image views.
   *
   * @param additionalImageView
   * @param enlargedImageView
   */
  private void updateImagePosition(ImageView additionalImageView, ImageView enlargedImageView) {
    additionalImageView.setLayoutX(anchorPane.getWidth() - additionalImageView.getFitWidth() + 40);
    enlargedImageView.setLayoutX(anchorPane.getWidth() - enlargedImageView.getFitWidth() + 40);
    additionalImageView.setLayoutY(
        anchorPane.getHeight() - additionalImageView.getFitHeight() + 10);
    enlargedImageView.setLayoutY(anchorPane.getHeight() - enlargedImageView.getFitHeight() + 10);
  }

  /**
   * Sets the hover handlers for the additional and enlarged image views.
   *
   * @param additionalImageView
   * @param enlargedImageView
   */
  private void setHoverHandlers(ImageView additionalImageView, ImageView enlargedImageView) {
    additionalImageView.setOnMouseEntered(
        e -> {
          additionalImageView.setOpacity(0);
          enlargedImageView.setOpacity(1);
          additionalImageView.setCursor(javafx.scene.Cursor.HAND);
        });

    additionalImageView.setOnMouseExited(
        e -> {
          additionalImageView.setOpacity(1);
          enlargedImageView.setOpacity(0);
          additionalImageView.setCursor(javafx.scene.Cursor.DEFAULT);
        });

    enlargedImageView.setOnMouseEntered(
        e -> {
          additionalImageView.setOpacity(0);
          enlargedImageView.setOpacity(1);
          enlargedImageView.setCursor(javafx.scene.Cursor.HAND);
        });

    enlargedImageView.setOnMouseExited(
        e -> {
          additionalImageView.setOpacity(1);
          enlargedImageView.setOpacity(0);
          enlargedImageView.setCursor(javafx.scene.Cursor.DEFAULT);
        });
  }

  /**
   * Centers the specified image view in the anchor pane.
   *
   * @param imageView
   */
  private void centerImage(ImageView imageView) {
    imageView.setX(((anchorPane.getWidth() - imageView.getFitWidth()) / 2) - 50);
    imageView.setY((anchorPane.getHeight() - imageView.getFitHeight()) / 2);
  }

  /**
   * Zooms in on the specified image view and transitions to the next scene.
   *
   * @param imageView
   * @param nextScene
   */
  private void zoomIn(ImageView imageView, String nextScene) {
    mediaPlayer.seek(Duration.seconds(1));
    mediaPlayer.play();

    new Thread(
            () -> {
              Platform.runLater(
                  () -> {
                    ScaleTransition zoomInTransition =
                        new ScaleTransition(Duration.seconds(1), imageView);
                    zoomInTransition.setToX(6.0);
                    zoomInTransition.setToY(6.0);
                    zoomInTransition.setCycleCount(1);
                    zoomInTransition.setAutoReverse(false);

                    Bounds bounds = imageView.localToScene(imageView.getBoundsInLocal());

                    double sceneCenterX = anchorPane.getWidth() / 2;
                    double sceneCenterY = anchorPane.getHeight() / 2;

                    double imageCenterX = bounds.getMinX() + bounds.getWidth() / 2;
                    double imageCenterY = bounds.getMinY() + bounds.getHeight() / 2;

                    double translateX = sceneCenterX - imageCenterX + 250;
                    double translateY = sceneCenterY - imageCenterY + 300;

                    TranslateTransition moveToCenterTransition =
                        new TranslateTransition(Duration.seconds(0.5), imageView);
                    moveToCenterTransition.setByX(translateX);
                    moveToCenterTransition.setByY(translateY);
                    moveToCenterTransition.setCycleCount(1);
                    moveToCenterTransition.setAutoReverse(false);

                    RotateTransition rotateTransition =
                        new RotateTransition(Duration.seconds(1), imageView);
                    rotateTransition.setByAngle(20);
                    rotateTransition.setCycleCount(1);
                    rotateTransition.setAutoReverse(false);

                    ParallelTransition parallelTransition =
                        new ParallelTransition(
                            zoomInTransition, moveToCenterTransition, rotateTransition);

                    parallelTransition.play();
                  });
            })
        .start();

    new Thread(
            () -> {
              Platform.runLater(
                  () -> {
                    FadeTransition fadeOutTransition =
                        new FadeTransition(Duration.seconds(0.5), anchorPane);
                    fadeOutTransition.setFromValue(1.0);
                    fadeOutTransition.setToValue(0.0);
                    fadeOutTransition.setCycleCount(1);
                    fadeOutTransition.setAutoReverse(false);

                    PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.5));

                    SequentialTransition sequentialTransition =
                        new SequentialTransition(pauseTransition, fadeOutTransition);

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
        .start();
  }
}
