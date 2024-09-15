package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController {

  @FXML private Button btnGuess;
  @FXML private Button btnMenu;
  @FXML private Button btnCrimeScene;
  @FXML private Button btnGrandma;
  @FXML private Button btnGrandson;
  @FXML private Button btnUncle;
  @FXML private AnchorPane rootNode;

  @FXML private ImageView background;

  @FXML private Label lbltimer;

  @FXML private ImageView clue1;

  private static boolean isFirstTimeInit = true;
  private static GameStateContext context = GameStateContext.getInstance();

  private TimerModel countdownTimer;

  // Create a group node
  private Group drawGroup;

  @FXML
  public void initialize() {

    // Set the initial opacity of clue1 to 0 (hidden)
    clue1.setOpacity(0);

    // Check if the garden tool has been found
    if (context.isGardenToolFound()) {
      // If found, set the opacity of clue1 to 1 (visible)
      clue1.setOpacity(1);
    }

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
    if (isFirstTimeInit) {
      rootNode.setOpacity(0);
      FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), rootNode);
      fadeTransition.setFromValue(0);
      fadeTransition.setToValue(1);
      fadeTransition.play();

      // TextToSpeech.speak("Chat with the three customers, and guess who is the " +
      // context.getProfessionToGuess());
      isFirstTimeInit = false;
    }

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

    final ImageView file1 = createAndBindImageView(image1); // Displayed initially
    final ImageView file2 = createAndBindImageView(image2);
    final ImageView file3 = createAndBindImageView(image3);
    final ImageView file4 = createAndBindImageView(image4);
    final ImageView file5 = createAndBindImageView(image5);

    applyDropShadow(dropShadow, file1, file2, file3, file4, file5);

    // Instantiate an object called drawGroup and add the first image
    drawGroup = new Group(file1);

    // Position the image at the bottom-right corner of the AnchorPane
    AnchorPane.setRightAnchor(drawGroup, -10.0); // 10px from the right
    AnchorPane.setBottomAnchor(drawGroup, -10.0); // 10px from the bottom

    // Add the drawGroup to the AnchorPane (rootNode)
    rootNode.getChildren().addAll(backgroundImageView, drawGroup);

    drawGroup.toBack();
    backgroundImageView.toBack();

    // Set hover effect
    addHoverEffect(drawGroup);

    // Set mouse click behavior to trigger the animation
    drawGroup.setOnMouseClicked(e -> playAnimation(file1, file2, file3, file4, file5));

    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());

    // Load background image
    Image phoneClueImage =
        new Image(RoomController.class.getResource("/images/phoneclue.png").toString());

    // Create the background ImageView and set an initial size (scale)
    ImageView phoneClueImageView = new ImageView(phoneClueImage);

    // Apply a scaling factor (e.g., 0.14 for 14% of the root node size)
    double scaleFactor = 0.14;
    phoneClueImageView.setFitWidth(rootNode.getWidth() * scaleFactor);
    phoneClueImageView.setFitHeight(rootNode.getHeight() * scaleFactor);

    // Make sure the background resizes with the window, but maintain the scaling
    phoneClueImageView.fitWidthProperty().bind(rootNode.widthProperty().multiply(scaleFactor));
    phoneClueImageView.fitHeightProperty().bind(rootNode.heightProperty().multiply(scaleFactor));

    // Use AnchorPane constraints to position the ImageView
    double rightMargin = 280.0; // Increase this value to move further from the right
    double verticalOffset = 70.0; // Move it slightly further down from the center

    AnchorPane.setRightAnchor(phoneClueImageView, rightMargin); // Move it 100px from the right
    AnchorPane.setTopAnchor(
        phoneClueImageView,
        (rootNode.getHeight() - phoneClueImageView.getFitHeight()) / 2
            + verticalOffset); // Vertically slightly lower

    // Bind TopAnchor to keep the image vertically responsive as the window resizes
    phoneClueImageView
        .fitHeightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              AnchorPane.setTopAnchor(
                  phoneClueImageView,
                  (rootNode.getHeight() - newVal.doubleValue()) / 2 + verticalOffset);
            });

    DropShadow hoverShadow = new DropShadow();
    hoverShadow.setColor(Color.CORNFLOWERBLUE); // Customize the hover effect color
    hoverShadow.setRadius(10); // Customize the shadow effect

    phoneClueImageView.setOnMouseEntered(
        e -> {
          phoneClueImageView.setEffect(hoverShadow); // Apply hover effect when mouse enters
        });
    phoneClueImageView.setOnMouseExited(
        e -> {
          phoneClueImageView.setEffect(null); // Remove effect when mouse exits
        });

    // Add the ImageView to the root node
    rootNode.getChildren().addAll(phoneClueImageView);
  }

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
          group.setEffect(null); // Remove effect when mouse exits
        });
  }

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
            App.setRoot("cluedrawer");
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        });
  }

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

  private void showNextImage(Group file, ImageView nextImage) {
    file.getChildren().setAll(nextImage);
  }

  private void applyDropShadow(DropShadow dropShadow, ImageView... imageViews) {
    for (ImageView imageView : imageViews) {
      imageView.setEffect(dropShadow);
    }
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
   * Handles mouse clicks on rectangles representing people in the room.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleRectangleClick(MouseEvent event) throws IOException {
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    context.handleRectangleClick(event, clickedRectangle.getId());
  }

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    GameStateContext.getInstance().setGuessPressed(true); // Mark as found in the context
    App.setRoot("guessing");
    context.handleGuessClick();
  }

  /**
   * Handles the inspect uncle button click event.
   *
   * @param event
   * @throws IOException
   */
  @FXML
  private void onUncle(ActionEvent event) throws IOException {
    App.setRoot("suspect1room");
  }

  /**
   * Handles the inspect Grandmother button click event.
   *
   * @param event
   * @throws IOException
   */
  @FXML
  private void onGrandmother(ActionEvent event) throws IOException {
    context.setMenuVisible(true); // Toggle the visibility in the context
    App.setRoot("suspect2room");
  }

  /**
   * Handles the inspect Grandson button click event.
   *
   * @param event
   * @throws IOException
   */
  @FXML
  private void onGrandson(ActionEvent event) throws IOException {
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

  /**
   * Updates the visibility of the menu buttons based on the isMenuVisible variable in the
   * GameStateContext.
   */
  private void updateMenuVisibility() {
    boolean isMenuVisible = context.isMenuVisible();

    if (isMenuVisible) {
      btnMenu.setStyle(
          "-fx-background-radius: 10 0 0 10; -fx-border-color: black transparent black black;"
              + " -fx-border-radius: 10 0 0 10; -fx-background-insets: 0;");
    } else {
      btnMenu.setStyle(
          "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;"
              + " -fx-background-insets: 0;");
    }

    btnCrimeScene.setVisible(isMenuVisible);
    btnCrimeScene.setManaged(isMenuVisible);

    btnGrandma.setVisible(isMenuVisible);
    btnGrandma.setManaged(isMenuVisible);

    btnGrandson.setVisible(isMenuVisible);
    btnGrandson.setManaged(isMenuVisible);

    btnUncle.setVisible(isMenuVisible);
    btnUncle.setManaged(isMenuVisible);
  }
}
