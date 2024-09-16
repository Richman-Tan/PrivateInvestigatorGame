package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import nz.ac.auckland.se206.App;

public class CluePhoneController {

  @FXML private AnchorPane rootPane;
  @FXML private ImageView phoneImageView;
  @FXML private ImageView imageView;

  // Start circles (left-hand side)
  private Circle startCircleRed;
  private Circle startCircleBlue;
  private Circle startCircleGreen;

  // End circles (right-hand side)
  private Circle endCircleRed;
  private Circle endCircleBlue;
  private Circle endCircleGreen;

  // Wires (lines)
  private Line redWire;
  private Line blueWire;
  private Line greenWire;
  private Line activeWire;

  // Flags for each wire connection
  private boolean isRedWireConnected = false;
  private boolean isBlueWireConnected = false;
  private boolean isGreenWireConnected = false;

  // Flag for all connections
  private boolean allConnected = false;

  // Currently active circle for dragging
  private Circle activeStartCircle = null;

  @FXML
  private void initialize() {
    rootPane.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());
    // Load background
    Image backgroundImage =
        new Image(
            CluePhoneController.class
                .getResource("/images/cluephoneimages/Phonecluebg.png")
                .toString());

    createAndBindImageView(backgroundImage);

    Image phoneImage =
        new Image(
            CluePhoneController.class
                .getResource("/images/cluephoneimages/phoneinteraction.gif")
                .toString());
    phoneImageView = new ImageView(phoneImage);
    // Apply a scaling factor (e.g., 0.14 for 14% of the root node size)
    double scaleFactor = 1;
    phoneImageView.setFitWidth(rootPane.getWidth() * scaleFactor);
    phoneImageView.setFitHeight(rootPane.getHeight() * scaleFactor);

    // Make sure the background resizes with the window, but maintain the scaling
    phoneImageView.fitWidthProperty().bind(rootPane.widthProperty().multiply(scaleFactor));
    phoneImageView.fitHeightProperty().bind(rootPane.heightProperty().multiply(scaleFactor));

    // Use AnchorPane constraints to position the ImageView
    double rightMargin = 0.0; // Increase this value to move further from the right
    double verticalOffset = 50.0; // Move it slightly further down from the center

    AnchorPane.setRightAnchor(phoneImageView, rightMargin); // Move it 100px from the right
    AnchorPane.setTopAnchor(
        phoneImageView,
        (rootPane.getHeight() - phoneImageView.getFitHeight()) / 2
            + verticalOffset); // Vertically slightly lower

    // Bind TopAnchor to keep the image vertically responsive as the window resizes
    phoneImageView
        .fitHeightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              AnchorPane.setTopAnchor(
                  phoneImageView,
                  (rootPane.getHeight() - newVal.doubleValue()) / 2 + verticalOffset);
            });

    rootPane.getChildren().add(phoneImageView);

    // Create arrow button on the left side of the phoneImage
    Button arrowButton =
        new Button("Turn phone around"); // Unicode left arrow, you can also use an image
    arrowButton.setStyle(
        "-fx-background-radius: 10; "
            + "-fx-border-radius: 10; "
            + "-fx-border-color: black; "
            + "-fx-background-color: white; "
            + "-fx-text-fill: black; "
            + "-fx-font-size: 13px; "
            + "-fx-background-insets: 0; "
            + "-fx-border-insets: 0;"); // Larger font and clear border for visibility
    arrowButton.setPrefWidth(200); // Set a fixed width
    arrowButton.setPrefHeight(50); // Set a fixed height

    // Position the button on the left side
    AnchorPane.setLeftAnchor(arrowButton, 20.0); // Set 20px from the left
    AnchorPane.setTopAnchor(arrowButton, 20.0); // Center the button vertically

    arrowButton.setOnAction(e -> handleArrowButtonClick()); // Define action on click
    arrowButton.getStyleClass().add("button"); // Apply the style class from CSS
    arrowButton.setOpacity(0.8);

    rootPane.getChildren().add(arrowButton); // Add the button to the rootPane

    Button goBackButton = new Button("Go Back");
    goBackButton.setStyle(
        "-fx-background-radius: 10; "
            + "-fx-border-radius: 10; "
            + "-fx-border-color: black; "
            + "-fx-background-color: white; "
            + "-fx-text-fill: black; "
            + "-fx-font-size: 14px; "
            + "-fx-background-insets: 0; "
            + "-fx-border-insets: 0;");
    goBackButton.setPrefWidth(100);
    goBackButton.setPrefHeight(40);

    // Position the button at the bottom-right corner
    AnchorPane.setBottomAnchor(goBackButton, 10.0); // 10px from the bottom
    AnchorPane.setRightAnchor(goBackButton, 10.0); // 10px from the right

    goBackButton.getStyleClass().add("button"); // Apply the style class from CSS
    goBackButton.setOpacity(0.8); // Set opacity to 0.8
    // Add the button to the anchorPane
    rootPane.getChildren().add(goBackButton);

    // Set the action when the button is clicked
    goBackButton.setOnAction(
        event -> {
          try {
            App.setRoot("room");
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }

  private void handleArrowButtonClick() {
    System.out.println("Arrow button clicked!");

    // Set opacity of phoneImageView to 0 (assuming phoneImageView is defined earlier)
    phoneImageView.setOpacity(0);

    // Load the back of the phone image
    Image backofPhone =
        new Image(
            CluePhoneController.class
                .getResource("/images/cluephoneimages/backofphone.png")
                .toString());

    imageView = new ImageView(backofPhone);

    // Preserve the aspect ratio to avoid squashing
    imageView.setPreserveRatio(false);

    // Bind the ImageView's fitWidth and fitHeight to the rootPane's width and height properties
    imageView.setFitWidth(rootPane.getWidth());
    imageView.setFitHeight(rootPane.getHeight());

    // Bind the ImageView's fitWidth to a percentage of the rootPane's width (e.g., 80% of the
    // rootPane width)
    double horizontalScaleFactor = 0.6; // Change this value to adjust the horizontal size
    imageView.fitWidthProperty().bind(rootPane.widthProperty().multiply(horizontalScaleFactor));
    imageView.fitHeightProperty().bind(rootPane.heightProperty());

    // Use AnchorPane constraints to position the ImageView
    double rightMargin = 200.0; // Increase this value to move further from the right
    AnchorPane.setRightAnchor(imageView, rightMargin); // Move it 100px from the right

    // Add the imageView to the rootPane
    rootPane.getChildren().add(imageView);

    // Create and setup the wiring game
    setupGame();
  }

  private void setupGame() {
    // Create start circles (left side)
    startCircleRed = createDraggableCircle(0, 0, Color.RED);
    startCircleBlue = createDraggableCircle(0, 0, Color.BLUE);
    startCircleGreen = createDraggableCircle(0, 0, Color.GREEN);

    // Create end circles (right side)
    endCircleRed = createFixedCircle(0, 0, Color.RED);
    endCircleBlue = createFixedCircle(0, 0, Color.BLUE);
    endCircleGreen = createFixedCircle(0, 0, Color.GREEN);

    // Bind start circle positions to the imageView's size and position, using layoutXProperty and
    // fitWidthProperty
    startCircleRed
        .centerXProperty()
        .bind(imageView.layoutXProperty().add(imageView.fitWidthProperty().multiply(0.3)));
    startCircleRed
        .centerYProperty()
        .bind(imageView.layoutYProperty().add(imageView.fitHeightProperty().multiply(0.39)));

    startCircleBlue
        .centerXProperty()
        .bind(imageView.layoutXProperty().add(imageView.fitWidthProperty().multiply(0.3)));
    startCircleBlue
        .centerYProperty()
        .bind(imageView.layoutYProperty().add(imageView.fitHeightProperty().multiply(0.53)));

    startCircleGreen
        .centerXProperty()
        .bind(imageView.layoutXProperty().add(imageView.fitWidthProperty().multiply(0.3)));
    startCircleGreen
        .centerYProperty()
        .bind(imageView.layoutYProperty().add(imageView.fitHeightProperty().multiply(0.67)));

    // Bind end circle positions to the imageView's size and position
    endCircleRed
        .centerXProperty()
        .bind(imageView.layoutXProperty().add(imageView.fitWidthProperty().multiply(0.7).add(120)));
    endCircleRed
        .centerYProperty()
        .bind(imageView.layoutYProperty().add(imageView.fitHeightProperty().multiply(0.39)));

    endCircleBlue
        .centerXProperty()
        .bind(imageView.layoutXProperty().add(imageView.fitWidthProperty().multiply(0.7).add(120)));
    endCircleBlue
        .centerYProperty()
        .bind(imageView.layoutYProperty().add(imageView.fitHeightProperty().multiply(0.53)));

    endCircleGreen
        .centerXProperty()
        .bind(imageView.layoutXProperty().add(imageView.fitWidthProperty().multiply(0.7).add(120)));
    endCircleGreen
        .centerYProperty()
        .bind(imageView.layoutYProperty().add(imageView.fitHeightProperty().multiply(0.67)));

    // Add the circles to the pane
    rootPane
        .getChildren()
        .addAll(
            startCircleRed,
            startCircleBlue,
            startCircleGreen,
            endCircleRed,
            endCircleBlue,
            endCircleGreen);
  }

  // Create start circle with dragging functionality
  private Circle createDraggableCircle(double x, double y, Color color) {
    Circle circle = new Circle(x, y, 10, color);
    circle.setOnMousePressed(event -> onStartDrag(event, circle));
    circle.setOnMouseDragged(this::onDrag);
    circle.setOnMouseReleased(this::onEndDrag);
    return circle;
  }

  // Create fixed end circle (no dragging)
  private Circle createFixedCircle(double x, double y, Color color) {
    Circle circle = new Circle(x, y, 10, color);
    return circle;
  }

  // Called when dragging starts
  private void onStartDrag(MouseEvent event, Circle circle) {
    activeStartCircle = circle;

    // Create a new line to represent the wire while dragging
    activeWire = new Line();
    activeWire.setStrokeWidth(5);
    activeWire.setStrokeLineCap(StrokeLineCap.ROUND);
    activeWire.setStroke(circle.getFill());

    activeWire.setStartX(circle.getCenterX());
    activeWire.setStartY(circle.getCenterY());

    rootPane.getChildren().add(activeWire);
  }

  // Called while dragging
  private void onDrag(MouseEvent event) {
    if (activeWire != null) {
      activeWire.setEndX(event.getX());
      activeWire.setEndY(event.getY());
    }
  }

  // Called when dragging ends
  private void onEndDrag(MouseEvent event) {
    if (activeWire != null && activeStartCircle != null) {
      // Check if the user dropped on a valid end circle
      Circle endCircle = getMatchingEndCircle(activeStartCircle, event.getX(), event.getY());
      if (endCircle != null) {
        // Snap the wire to the correct position
        activeWire.setEndX(endCircle.getCenterX());
        activeWire.setEndY(endCircle.getCenterY());

        // Set the corresponding wire flag to true and assign the wire
        if (activeStartCircle == startCircleRed && endCircle == endCircleRed) {
          isRedWireConnected = true;
          redWire = activeWire;
        } else if (activeStartCircle == startCircleBlue && endCircle == endCircleBlue) {
          isBlueWireConnected = true;
          blueWire = activeWire;
        } else if (activeStartCircle == startCircleGreen && endCircle == endCircleGreen) {
          isGreenWireConnected = true;
          greenWire = activeWire;
        }

        // Check if all wires are connected
        checkAllConnections();
      } else {
        // If not on a valid end circle, remove the wire
        rootPane.getChildren().remove(activeWire);
      }
    }
    // Clear the current active wire
    activeWire = null;
    activeStartCircle = null;
  }

  // Check if all wires are connected
  private void checkAllConnections() {
    if (isRedWireConnected && isBlueWireConnected && isGreenWireConnected) {
      allConnected = true;
      System.out.println("All wires are connected!");

      // Set a 0.5 second delay before the phone rings
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      imageView.setOpacity(0);

      // Load the phone ringing image
      Image phoneRingingImage =
          new Image(
              CluePhoneController.class
                  .getResource("/images/cluephoneimages/phonewvidimg.png")
                  .toString());
      ImageView phoneRingingImageView = new ImageView(phoneRingingImage);

      phoneRingingImageView.setPreserveRatio(true);

      // Apply a scaling factor (e.g., 0.14 for 14% of the root node size)
      double scaleFactor = 1;
      phoneRingingImageView.setFitWidth(rootPane.getWidth() * scaleFactor);
      phoneRingingImageView.setFitHeight(rootPane.getHeight() * scaleFactor);

      // Make sure the background resizes with the window, but maintain the scaling
      phoneRingingImageView.fitWidthProperty().bind(rootPane.widthProperty().multiply(scaleFactor));
      phoneRingingImageView
          .fitHeightProperty()
          .bind(rootPane.heightProperty().multiply(scaleFactor));

      // Use AnchorPane constraints to position the ImageView
      double rightMargin = 0.0; // Increase this value to move further from the right
      double verticalOffset = 50.0; // Move it slightly further down from the center

      AnchorPane.setRightAnchor(phoneRingingImageView, rightMargin); // Move it 100px from the right
      AnchorPane.setTopAnchor(
          phoneRingingImageView,
          (rootPane.getHeight() - phoneRingingImageView.getFitHeight()) / 2
              + verticalOffset); // Vertically slightly lower

      // Bind TopAnchor to keep the image vertically responsive as the window resizes
      phoneRingingImageView
          .fitHeightProperty()
          .addListener(
              (obs, oldVal, newVal) -> {
                AnchorPane.setTopAnchor(
                    phoneRingingImageView,
                    (rootPane.getHeight() - newVal.doubleValue()) / 2 + verticalOffset);
              });

      rootPane.getChildren().add(phoneRingingImageView);

      // Load the MP4 video
      String videoPath =
          CluePhoneController.class
              .getResource("/images/cluephoneimages/clueaudiofile.mp4") // Get the resource path
              .toExternalForm(); // Convert the resource path to an external form
      Media media = new Media(videoPath);
      MediaPlayer mediaPlayer = new MediaPlayer(media);
      MediaView mediaView = new MediaView(mediaPlayer);

      // Set the MediaView size to fit within a portion of the rootPane
      mediaView.setFitWidth(rootPane.getWidth() * 0.74); // 60% of rootPane width
      mediaView.setFitHeight(rootPane.getHeight() * 0.74); // 60% of rootPane height
      mediaView.setPreserveRatio(true); // Preserve the aspect ratio

      // Center the media view in the rootPane with an additional shift to the right
      double additionalRightShift = 295.0; // Move 100 pixels further to the right
      double verticalOffsetMedia = 39.0; // Move it slightly further down from the center
      AnchorPane.setTopAnchor(
          mediaView, (rootPane.getHeight() - mediaView.getFitHeight()) / 2 + verticalOffsetMedia);
      AnchorPane.setLeftAnchor(
          mediaView, (rootPane.getWidth() - mediaView.getFitWidth()) / 2 + additionalRightShift);

      // Make the mediaView responsive to window resizing and maintain the shift to the right
      rootPane
          .heightProperty()
          .addListener(
              (obs, oldVal, newVal) -> {
                AnchorPane.setTopAnchor(
                    mediaView,
                    (newVal.doubleValue() - mediaView.getFitHeight()) / 2 + verticalOffsetMedia);
              });
      rootPane
          .widthProperty()
          .addListener(
              (obs, oldVal, newVal) -> {
                AnchorPane.setLeftAnchor(
                    mediaView,
                    (newVal.doubleValue() - mediaView.getFitWidth()) / 2 + additionalRightShift);
              });

      // Add the MediaView to the rootPane
      rootPane.getChildren().add(mediaView);

      // Load the phone ringing image
      Image overlay =
          new Image(
              CluePhoneController.class
                  .getResource("/images/cluephoneimages/overlay.png")
                  .toString());
      ImageView overlayImageView = new ImageView(overlay);

      overlayImageView.setPreserveRatio(true);

      // Apply a scaling factor (e.g., 0.14 for 14% of the root node size)
      overlayImageView.setFitWidth(rootPane.getWidth() * scaleFactor);
      overlayImageView.setFitHeight(rootPane.getHeight() * scaleFactor);

      // Make sure the background resizes with the window, but maintain the scaling
      overlayImageView.fitWidthProperty().bind(rootPane.widthProperty().multiply(scaleFactor));
      overlayImageView.fitHeightProperty().bind(rootPane.heightProperty().multiply(scaleFactor));

      AnchorPane.setRightAnchor(overlayImageView, rightMargin); // Move it 100px from the right
      AnchorPane.setTopAnchor(
          overlayImageView,
          (rootPane.getHeight() - overlayImageView.getFitHeight()) / 2
              + verticalOffset); // Vertically slightly lower

      // Bind TopAnchor to keep the image vertically responsive as the window resizes
      overlayImageView
          .fitHeightProperty()
          .addListener(
              (obs, oldVal, newVal) -> {
                AnchorPane.setTopAnchor(
                    overlayImageView,
                    (rootPane.getHeight() - newVal.doubleValue()) / 2 + verticalOffset);
              });

      rootPane.getChildren().add(overlayImageView);

      // Create a circular play/pause button
      Button playPauseButton = new Button("▶");
      playPauseButton.setStyle(
          "-fx-background-radius: 200px; -fx-background-color: #FFFFFF; -fx-font-size: 18px;");
      playPauseButton.setPrefSize(50, 50); // Set size for circular button

      // Position the button in the center of the media
      AnchorPane.setTopAnchor(playPauseButton, (rootPane.getHeight() - 50) / 2);
      AnchorPane.setLeftAnchor(playPauseButton, (rootPane.getWidth() - 50) / 2);

      // Make the button responsive to the rootPane size changes
      rootPane
          .heightProperty()
          .addListener(
              (obs, oldVal, newVal) -> {
                AnchorPane.setTopAnchor(playPauseButton, (newVal.doubleValue() - 50) / 2);
              });
      rootPane
          .widthProperty()
          .addListener(
              (obs, oldVal, newVal) -> {
                AnchorPane.setLeftAnchor(playPauseButton, (newVal.doubleValue() - 50) / 2);
              });

      // Toggle between play and pause
      playPauseButton.setOnAction(
          e -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
              mediaPlayer.pause();
              playPauseButton.setText("▶"); // Change button text to "Play"
            } else {
              mediaPlayer.play();
              playPauseButton.setText("⏸"); // Change button text to "Pause"
            }
          });

      // Initially show the button
      playPauseButton.setVisible(true);

      // Show the play/pause button when the video is paused
      mediaPlayer.setOnPaused(() -> playPauseButton.setVisible(true));
      mediaPlayer.setOnEndOfMedia(() -> playPauseButton.setVisible(true));

      // Add the play/pause button to the rootPane
      rootPane.getChildren().add(playPauseButton);

      // Set opacity of all wires to 0 when all are connected
      redWire.setOpacity(0);
      blueWire.setOpacity(0);
      greenWire.setOpacity(0);

      // Set opacity of all circles to 0 when all are connected
      startCircleRed.setOpacity(0);
      startCircleBlue.setOpacity(0);
      startCircleGreen.setOpacity(0);
      endCircleRed.setOpacity(0);
      endCircleBlue.setOpacity(0);
      endCircleGreen.setOpacity(0);
    }
  }

  // Check if the drag ends on a valid end circle with the same color
  private Circle getMatchingEndCircle(Circle startCircle, double x, double y) {
    Circle targetEndCircle = null;

    if (startCircle == startCircleRed && isInsideCircle(endCircleRed, x, y)) {
      targetEndCircle = endCircleRed;
    } else if (startCircle == startCircleBlue && isInsideCircle(endCircleBlue, x, y)) {
      targetEndCircle = endCircleBlue;
    } else if (startCircle == startCircleGreen && isInsideCircle(endCircleGreen, x, y)) {
      targetEndCircle = endCircleGreen;
    }

    return targetEndCircle;
  }

  // Helper to check if a point (x, y) is inside a circle
  private boolean isInsideCircle(Circle circle, double x, double y) {
    double dx = x - circle.getCenterX();
    double dy = y - circle.getCenterY();
    double distance = Math.sqrt(dx * dx + dy * dy);
    return distance <= circle.getRadius();
  }

  /*
   * Creating and binding background images
   */
  private void createAndBindImageView(Image image) {
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(rootPane.getWidth());
    imageView.setFitHeight(rootPane.getHeight());

    imageView.fitWidthProperty().bind(rootPane.widthProperty());
    imageView.fitHeightProperty().bind(rootPane.heightProperty());

    rootPane.getChildren().add(imageView);
  }
}
