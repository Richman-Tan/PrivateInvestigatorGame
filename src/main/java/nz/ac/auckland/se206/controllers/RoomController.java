package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.states.GameStarted;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController {

  // Inner Classes (if any would go here)

  // Static Fields
  private static GameStateContext context = GameStateContext.getInstance();

  // Static Methods
  // (If you have static methods, they would go here)

  // Instance Fields
  @FXML private Button menuButton;
  @FXML private Button crimeSceneButton;
  @FXML private Button grandmaButton;
  @FXML private Button grandsonButton;
  @FXML private Button uncleButton;
  @FXML private AnchorPane rootNode;
  @FXML private Button guessButton;
  @FXML private ImageView background;
  @FXML private Label lbltimer;
  @FXML private ImageView clue1;
  @FXML private ImageView clue2;
  @FXML private ImageView clue3;
  @FXML private VBox viewBox;

  private static final String initialaudio =
      GameStarted.class.getClassLoader().getResource("sounds/initialaudio.mp3").toExternalForm();

  private MediaPlayer mediaPlayer;

  private boolean isFirstTimeInit = context.isFirstTimeInit();
  private TimerModel countdownTimer;

  private boolean isatleastoncecluefound =
      context.isGardenToolFound() || context.isPhoneFound() || context.isNoteFound();

  // Create a group node
  private Group drawGroup;

  private SVGPath volumeUpStroke = new SVGPath();
  private SVGPath volumeUp = new SVGPath();
  private SVGPath volumeOff = new SVGPath();

  /** Initializes the room view. */
  @FXML
  public void initialize() {
    // Set the initial opacity of clue1 to 0 (hidden)
    clue1.setOpacity(0);
    clue2.setOpacity(0);
    clue3.setOpacity(0);

    Media initial = new Media(initialaudio);
    mediaPlayer = new MediaPlayer(initial);
    mediaPlayer
        .volumeProperty()
        .bind(
            Bindings.when(SharedVolumeControl.getInstance().volumeSettingProperty())
                .then(1.0) // Full volume when volume is on
                .otherwise(0.0) // Mute when volume is off
            );

    // Check if the guess button should be enabled
    checkGuessButton();

    // Check if the garden tool has been found
    if (context.isGardenToolFound()) {
      // If found, set the opacity of clue1 to 1 (visible)
      clue1.setOpacity(1);
    }

    // Check if the phone has been found
    if (context.isPhoneFound()) {
      // If found, set the opacity of clue2 to 1 (visible)
      clue2.setOpacity(1);
    }

    // Check if the note has been found
    if (context.isNoteFound()) {
      // If found, set the opacity of clue3 to 1 (visible)
      clue3.setOpacity(1);
    }

    showVolumeButton();
    volumeOff.toFront();
    volumeUp.toFront();
    volumeUpStroke.toFront();

    // Load the images
    final Image image1 =
        new Image(RoomController.class.getResource("/images/drawframe1.PNG").toString());
    final Image image2 =
        new Image(RoomController.class.getResource("/images/drawframe2.PNG").toString());
    final Image image3 =
        new Image(RoomController.class.getResource("/images/drawframe3.PNG").toString());
    final Image image4 =
        new Image(RoomController.class.getResource("/images/drawframe4.PNG").toString());
    final Image image5 =
        new Image(RoomController.class.getResource("/images/drawframe5.PNG").toString());

    // Other initialization (fade transition, timer, label updates, etc.)
    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            if (isFirstTimeInit) {

              Platform.runLater(
                  () -> {
                    // Fade in the root node
                    rootNode.setOpacity(0);

                    // Create a fade transition to fade in the root node
                    FadeTransition fadeTransition =
                        new FadeTransition(Duration.millis(1000), rootNode);

                    // Set the opacity values
                    fadeTransition.setFromValue(0);
                    fadeTransition.setToValue(1);
                    fadeTransition.play();
                  });

              Platform.runLater(
                  () -> {
                    if (!SharedVolumeControl.getInstance().getVolumeSetting()) {
                      mediaPlayer.setVolume(0);
                    }
                    mediaPlayer.play();
                  });

              // Set the first time initialization to false
              context.setFirstTimeInit(false);
              isFirstTimeInit = context.isFirstTimeInit();
            }
            return null;
          }
        };

    // Create a new thread and start the task
    Thread thread = new Thread(task);
    thread.setDaemon(true); // Allows the application to exit even if the thread is running
    thread.start();

    // Load background image
    Image backgroundImage =
        new Image(RoomController.class.getResource("/images/Office.jpg").toString());

    // Create the background ImageView and set it to fill the entire pane
    ImageView backgroundImageView = new ImageView(backgroundImage);
    backgroundImageView.setFitWidth(rootNode.getWidth());
    backgroundImageView.setFitHeight(rootNode.getHeight());

    // Make sure the background resizes with the window
    backgroundImageView.fitWidthProperty().bind(rootNode.widthProperty());
    backgroundImageView.fitHeightProperty().bind(rootNode.heightProperty());

    // Create a DropShadow effect
    DropShadow dropShadow = new DropShadow();
    dropShadow.setOffsetX(5); // Smaller offset
    dropShadow.setOffsetY(5);
    dropShadow.setRadius(6); // Reduce blur radius
    dropShadow.setSpread(0.07); // Lower spread for less intensity
    dropShadow.setColor(Color.color(0, 0, 0, 0.4)); // Less opacity for lighter shadow

    // Timer and other UI-related updates
    updateMenuVisibility();

    // Load the images
    final ImageView file1 = createAndBindImageView(image1); // Displayed initially
    final ImageView file2 = createAndBindImageView(image2);
    final ImageView file3 = createAndBindImageView(image3);
    final ImageView file4 = createAndBindImageView(image4);
    final ImageView file5 = createAndBindImageView(image5);

    // Apply the drop shadow effect to the images
    applyDropShadow(dropShadow, file1, file2, file3, file4, file5);

    // Instantiate an object called drawGroup and add the first image
    drawGroup = new Group(file1);

    // Position the image at the bottom-right corner of the AnchorPane
    AnchorPane.setRightAnchor(drawGroup, -10.0); // 10px from the right
    AnchorPane.setBottomAnchor(drawGroup, -10.0); // 10px from the bottom

    // Add the drawGroup to the AnchorPane (rootNode)
    rootNode.getChildren().addAll(backgroundImageView, drawGroup);

    // Set the drawGroup to the back
    drawGroup.toBack();

    // Set the background image to the back
    backgroundImageView.toBack();

    // Set hover effect
    addHoverEffect(drawGroup);

    // Set mouse click behavior to trigger the animation
    drawGroup.setOnMouseClicked(e -> playAnimation(file1, file2, file3, file4, file5));

    // Apply the orange drop shadow effect to the drawGroup
    applyOrangeDropShadow(drawGroup);

    // Timer and other UI-related updates
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();

    // Bind the timer label to the countdown timer
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());

    // Load background image
    Image phoneClueImage =
        new Image(RoomController.class.getResource("/images/phoneclue.png").toString());

    // Create the background ImageView and set an initial size (scale)
    ImageView phoneClueImageView = new ImageView(phoneClueImage);

    // Set whether to preserve the aspect ratio (optional)
    phoneClueImageView.setPreserveRatio(false);

    // Bind the MediaView's fitWidth and fitHeight to a percentage of the rootPane's width and
    // height
    double mediaHorizontalScaleFactor = 0.1; // Adjust this value to control horizontal size
    double mediaVerticalScaleFactor = 0.1; // Adjust this value to control vertical size
    phoneClueImageView
        .fitWidthProperty()
        .bind(rootNode.widthProperty().multiply(mediaHorizontalScaleFactor));
    phoneClueImageView
        .fitHeightProperty()
        .bind(rootNode.heightProperty().multiply(mediaVerticalScaleFactor));

    // Center the MediaView horizontally and vertically
    // Center the MediaView when the screen loads
    double initialCenterX = (rootNode.getWidth() - phoneClueImageView.getFitWidth()) / 2 + 170;
    double initialCenterY = (rootNode.getHeight() - phoneClueImageView.getFitHeight()) / 2 + 80;
    AnchorPane.setLeftAnchor(phoneClueImageView, initialCenterX);
    AnchorPane.setTopAnchor(phoneClueImageView, initialCenterY);

    applyOrangeDropShadow(phoneClueImageView);

    // Add listeners to ensure it stays centered when resized
    rootNode
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              double centerX = (newVal.doubleValue() - phoneClueImageView.getFitWidth()) / 2 + 170;
              AnchorPane.setLeftAnchor(phoneClueImageView, centerX);
            });

    rootNode
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              double centerY = (newVal.doubleValue() - phoneClueImageView.getFitHeight()) / 2 + 80;
              AnchorPane.setTopAnchor(phoneClueImageView, centerY);
            });

    DropShadow hoverShadow = new DropShadow();
    hoverShadow.setColor(Color.CORNFLOWERBLUE); // Customize the hover effect color
    hoverShadow.setRadius(10); // Customize the shadow effect

    // Apply hover effect
    phoneClueImageView.setOnMouseEntered(
        e -> {
          phoneClueImageView.setEffect(hoverShadow); // Apply hover effect when mouse enters
        });
    phoneClueImageView.setOnMouseExited(
        e -> {
          applyOrangeDropShadow(phoneClueImageView); // Remove effect when mouse exits
        });
    phoneClueImageView.setOnMouseClicked(
        e -> {
          try {
            // Handle the phone clue click event
            App.setRoot("cluephone");
          } catch (IOException e1) {
          }
        });

    // Add the ImageView to the root node
    rootNode.getChildren().addAll(phoneClueImageView);

    // Load background image
    Image paintingImage =
        new Image(RoomController.class.getResource("/images/cluepainting.png").toString());

    // Create the background ImageView and set it to fill the entire pane
    ImageView paintingImageView = new ImageView(paintingImage);
    paintingImageView.setFitWidth(rootNode.getWidth());
    paintingImageView.setFitHeight(rootNode.getHeight());

    // Make sure the background resizes with the window
    paintingImageView.fitWidthProperty().bind(rootNode.widthProperty());
    paintingImageView.fitHeightProperty().bind(rootNode.heightProperty());

    // Create a DropShadow effect
    DropShadow dropShadowPainting = new DropShadow();
    dropShadowPainting.setOffsetX(5); // Smaller offset
    dropShadowPainting.setOffsetY(5);
    dropShadowPainting.setRadius(5); // Reduce blur radius
    dropShadowPainting.setSpread(2); // Lower spread for less intensity
    dropShadowPainting.setColor(Color.color(0, 0, 0, 0.5)); // Less opacity for lighter shadow

    // Apply drop shadow effect to the painting image
    applyOrangeDropShadow(paintingImageView);

    // Apply hover effect
    DropShadow hoverShadowPainting = new DropShadow();
    hoverShadowPainting.setColor(Color.CORNFLOWERBLUE); // Customize the hover effect color
    hoverShadowPainting.setRadius(10); // Customize the shadow effect

    paintingImageView.setOnMouseEntered(
        e -> {
          paintingImageView.setEffect(hoverShadowPainting); // Apply hover effect when mouse enters
        });

    paintingImageView.setOnMouseExited(
        e -> {
          applyOrangeDropShadow(paintingImageView);
        });

    paintingImageView.setOnMouseClicked(
        e -> {
          try {
            handleSafeClick();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        });

    // Add the ImageView to the root node
    rootNode.getChildren().addAll(paintingImageView);

    // Load background image
    viewBox.toFront();
  }

  /**
   * Adds a hover effect to a group.
   *
   * @param group
   */
  private void addHoverEffect(Group group) {
    DropShadow hoverShadow = new DropShadow();
    hoverShadow.setColor(Color.CORNFLOWERBLUE); // Customize the hover effect color
    hoverShadow.setRadius(10); // Customize the shadow effect

    group.setOnMouseEntered(
        e -> {
          group.setEffect(hoverShadow); // Apply hover effect when mouse enters
        });

    group.setOnMouseExited(
        e -> {
          applyOrangeDropShadow(group); // Remove effect when mouse exits
        });
  }

  /**
   * Plays the animation of the drawing.
   *
   * @param file1 the first image to display
   * @param file2 the second image to display
   * @param file3 the third image to display
   * @param file4 the fourth image to display
   * @param file5 the fifth image to display
   */
  private void playAnimation(
      ImageView file1, ImageView file2, ImageView file3, ImageView file4, ImageView file5) {
    Timeline timeline = new Timeline();

    // KeyFrames for each image in sequence
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(0.2), event -> showNextImage(drawGroup, file2)));
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(0.4), event -> showNextImage(drawGroup, file3)));
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(0.6), event -> showNextImage(drawGroup, file4)));
    timeline
        .getKeyFrames()
        .add(new KeyFrame(Duration.seconds(0.8), event -> showNextImage(drawGroup, file5)));

    timeline.setCycleCount(1); // Play only once
    timeline.play();

    // 1 sec delay and then on animation finish
    timeline.setOnFinished(
        e -> {
          try {
            // 1 second delay
            Thread.sleep(10);
            App.setRoot("cluetornphotograph");
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        });
  }

  /**
   * Creates an ImageView and binds its width and height to the width and height of the AnchorPane.
   *
   * @param image the image to display in the ImageView
   * @return the ImageView with the image and bound width and height
   */
  private ImageView createAndBindImageView(Image image) {
    ImageView imageView = new ImageView(image);
    imageView
        .fitWidthProperty()
        .bind(rootNode.widthProperty().multiply(1)); // 100% of AnchorPane width
    imageView
        .fitHeightProperty()
        .bind(rootNode.heightProperty().multiply(1)); // 100% of AnchorPane height
    return imageView;
  }

  /**
   * Replaces the current image in the file with the next image.
   *
   * @param file the file to replace the image in
   * @param nextImage the next image to display
   */
  private void showNextImage(Group file, ImageView nextImage) {
    file.getChildren().setAll(nextImage);
  }

  /**
   * Applies a drop shadow effect to a list of ImageViews.
   *
   * @param dropShadow the drop shadow effect to apply
   * @param imageViews the list of ImageViews to apply the effect to
   */
  private void applyDropShadow(DropShadow dropShadow, ImageView... imageViews) {
    for (ImageView imageView : imageViews) {
      imageView.setEffect(dropShadow);
    }
  }

  /**
   * Applies an orange drop shadow effect to a node.
   *
   * @param node
   */
  private void applyOrangeDropShadow(Node node) {
    DropShadow orangedropShadow = new DropShadow();
    orangedropShadow.setOffsetX(0);
    orangedropShadow.setOffsetY(0);
    orangedropShadow.setRadius(15); // Adjust the radius for desired shadow spread
    orangedropShadow.setColor(Color.GOLD);
    node.setEffect(orangedropShadow);
  }

  /**
   * Updates the visibility of the menu buttons based on the isMenuVisible variable in the
   * GameStateContext.
   */
  private void updateMenuVisibility() {
    boolean isMenuVisible = context.isMenuVisible();

    if (isMenuVisible) {
      menuButton.setStyle(
          "-fx-background-radius: 10 0 0 10; -fx-border-color: black transparent black black;"
              + " -fx-border-radius: 10 0 0 10; -fx-background-insets: 0;");
    } else {
      menuButton.setStyle(
          "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;"
              + " -fx-background-insets: 0;");
    }

    // Set visibility and management of other buttons based on isMenuVisible
    crimeSceneButton.setVisible(isMenuVisible);
    crimeSceneButton.setManaged(isMenuVisible);

    grandmaButton.setVisible(isMenuVisible);
    grandmaButton.setManaged(isMenuVisible);

    grandsonButton.setVisible(isMenuVisible);
    grandsonButton.setManaged(isMenuVisible);

    uncleButton.setVisible(isMenuVisible);
    uncleButton.setManaged(isMenuVisible);
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
  }

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onGuessClick(ActionEvent event) throws IOException {
    GameStateContext.getInstance().setGuessPressed(true); // Mark as found in the context
    App.setRoot("guessingScene");
    context.onGuessClick();
  }

  @FXML
  private void handleSafeClick() throws IOException {
    if (context.isNoteFound() || context.isSafeOpen()) {
      App.setRoot("cluesafeopened");
    } else {
      App.setRoot("cluesafe");
    }
  }

  @FXML
  private void onUncleButtonClick(ActionEvent event) throws IOException {
    App.setRoot("suspect1room");
  }

  @FXML
  private void onGrandmotherClick(ActionEvent event) throws IOException {
    context.setMenuVisible(true); // Toggle the visibility in the context
    App.setRoot("suspect2room");
  }

  @FXML
  private void onGrandsonClick(ActionEvent event) throws IOException {
    App.setRoot("suspect3room");
  }

  /**
   * Toggles the menu button when clicked.
   *
   * @param event
   * @throws IOException
   */
  @FXML
  private void onToggleMenu(ActionEvent event) {
    context.toggleMenuVisibility(); // Toggle the visibility in the context
    updateMenuVisibility(); // Update the visibility in the UI
  }

  @FXML
  private void checkGuessButton() {
    if (context.getListOfVisitors().contains("suspect1")
        && context.getListOfVisitors().contains("suspect2")
        && context.getListOfVisitors().contains("suspect3")
        && isatleastoncecluefound) {
      // Enable the guess button
      guessButton.setOpacity(0.8);
      guessButton.setDisable(false);
    } else {
      // Disable the guess button
      guessButton.setOpacity(0.3);
      guessButton.setDisable(true);
    }
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
    rootNode.getChildren().add(volumeUp);

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
    rootNode.getChildren().add(volumeUpStroke);

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
    rootNode.getChildren().add(volumeOff);
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
   * Method to check if the volume should be on or off
   */
  private void checkVolumeIcon() throws IOException {
    if (SharedVolumeControl.getInstance().getVolumeSetting()) {
      turnVolumeOn();
    } else {
      turnVolumeOff();
    }
  }
}
