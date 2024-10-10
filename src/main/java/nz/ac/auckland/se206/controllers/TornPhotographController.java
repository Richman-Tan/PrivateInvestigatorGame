package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

public class TornPhotographController {

  // Static fields
  private static final String revealText = "Oh???"; // Text to display letter by letter

  private static GameStateContext context = GameStateContext.getInstance();

  @FXML private Pane timerPane; // Pane to hold the timer

  @FXML private AnchorPane puzzlePane; // Pane to hold the puzzle pieces

  @FXML private AnchorPane textbox;

  @FXML private ImageView backgroundwithgame; // ImageView element for the background image

  @FXML private ImageView framebyitself; // ImageView element for the frame image

  @FXML private ImageView imagebg; // ImageView element for the background image

  @FXML private Button goBackButton;

  @FXML private Button flipButton; // Button to flip the image

  @FXML private AnchorPane blackOverlay;

  @FXML private ImageView banners;

  @FXML private ImageView backofphotoimg;

  @FXML private Label revealLabel; // Label for text reveal

  @FXML private ImageView photoframeimg;

  @FXML private ImageView piece1;
  @FXML private ImageView piece2;
  @FXML private ImageView piece3;
  @FXML private ImageView piece4;
  @FXML private ImageView piece5;
  @FXML private ImageView piece6;
  @FXML private ImageView piece7;
  @FXML private ImageView piece8;
  @FXML private ImageView piece9;

  @FXML private ImageView outlinePiece1;
  @FXML private ImageView outlinePiece2;
  @FXML private ImageView outlinePiece3;
  @FXML private ImageView outlinePiece4;
  @FXML private ImageView outlinePiece5;
  @FXML private ImageView outlinePiece6;
  @FXML private ImageView outlinePiece7;
  @FXML private ImageView outlinePiece8;
  @FXML private ImageView outlinePiece9;

  // Variables for drag offset
  private double offsetX;
  private double offsetY;

  // Correct positions for each puzzle piece on the board (target coordinates)
  private final double piece1TargetX = 515;
  private final double piece1TargetY = 170; // done

  private final double piece2TargetX = 480;
  private final double piece2TargetY = 310; // done

  private final double piece3TargetX = 280;
  private final double piece3TargetY = 160; // done

  private final double piece4TargetX = 200;
  private final double piece4TargetY = 217; // done

  private final double piece5TargetX = 335;
  private final double piece5TargetY = 170; // done

  private final double piece6TargetX = 334;
  private final double piece6TargetY = 280; // done

  private final double piece7TargetX = 420;
  private final double piece7TargetY = 200;

  private final double piece8TargetX = 584;
  private final double piece8TargetY = 335; // done

  private final double piece9TargetX = 505;
  private final double piece9TargetY = 236; // done

  private final SVGPath volumeUpStroke = new SVGPath();
  private SVGPath volumeUp = new SVGPath();
  private SVGPath volumeOff = new SVGPath();

  // Boolean flags to check if pieces are correctly placed
  private boolean piece1Correct;
  private boolean piece2Correct;
  private boolean piece3Correct;
  private boolean piece4Correct;
  private boolean piece5Correct;
  private boolean piece6Correct;
  private boolean piece7Correct;
  private boolean piece8Correct;
  private boolean piece9Correct;

  // Threshold to snap pieces into place
  private final double snapDistanceThreshold = 180;

  private TimerModel countdownTimer;

  /**
   * Initializes the photograph after the associated FXML has been loaded.
   *
   * <p>This method is automatically called by the JavaFX framework when the FXML file for the photo
   * view is loaded. It sets up the initial state of the photo controller by configuring UI
   * components, binding properties, and initializing any necessary data structures or event
   * listeners required for the controller's functionality.
   */
  @FXML
  public void initialize() {
    // Initialize flags for piece placement
    piece1Correct = false;
    piece2Correct = false;
    piece3Correct = false;
    piece4Correct = false;
    piece5Correct = false;
    piece6Correct = false;
    piece7Correct = false;
    piece8Correct = false;
    piece9Correct = false;

    togglevisabilityofpieces(false);

    createAndBindImageView(imagebg);
    createAndBindImageView(framebyitself);

    imagebg.toFront();
    // set up to front
    framebyitself.toFront();

    // Assuming 'framebyitself' is your ImageView instance
    framebyitself.setOnMouseEntered(
        event -> {
          // Increase the size of the ImageView by scaling it up
          framebyitself.setScaleX(1.1); // Scale 10% larger on X-axis
          framebyitself.setScaleY(1.1); // Scale 10% larger on Y-axis

          // Change the mouse cursor to a hand cursor
          framebyitself.setCursor(Cursor.HAND);

          // Create a DropShadow effect
          DropShadow dropShadow = new DropShadow();
          dropShadow.setRadius(10.0); // Set the radius of the shadow
          dropShadow.setOffsetX(5.0); // Set horizontal offset of the shadow
          dropShadow.setOffsetY(5.0); // Set vertical offset of the shadow
          dropShadow.setColor(Color.color(0.4, 0.4, 0.4)); // Set shadow color (light grey)

          // Apply the DropShadow effect to the ImageView
          framebyitself.setEffect(dropShadow);
        });

    framebyitself.setOnMouseExited(
        event -> {
          // Reset the size to the original scale
          framebyitself.setScaleX(1.0);
          framebyitself.setScaleY(1.0);

          // Reset the cursor to the default cursor
          framebyitself.setCursor(Cursor.DEFAULT);

          // Remove the DropShadow effect
          framebyitself.setEffect(null);
        });

    // Assuming 'framebyitself' is your ImageView instance
    framebyitself.setOnMouseClicked(
        event -> {
          // Show the black overlay and set it to cover the entire pane
          blackOverlay.setVisible(true);
          blackOverlay.toFront();

          // Create a fade transition for the overlay to fade into black
          FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), blackOverlay);
          fadeTransition.setFromValue(0.0); // Start with fully transparent
          fadeTransition.setToValue(1.0); // End with fully opaque (black screen)
          fadeTransition.setOnFinished(
              e -> {
                // Hide framebyitself and imagebg after fading to black
                framebyitself.setVisible(false);
                imagebg.setVisible(false);

                // Set up the game after the fade transition completes
                setupGame();

                // Fade the black overlay back out after setting up the game
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), blackOverlay);
                fadeOut.setFromValue(1.0); // Start with fully opaque
                fadeOut.setToValue(0.0); // End with fully transparent
                fadeOut.setOnFinished(
                    ev -> blackOverlay.setVisible(false)); // Hide the overlay when done
                fadeOut.play();
              });
          fadeTransition.play();
        });

    setupGoBackButton();
    setupTimerPane();
    textbox.toBack();
    // Add the volume button to the label pane and show it
    showVolumeButton();
  }

  /** Method to create and bind the reveal label for the text animation. */
  private void createRevealLabel() {
    revealLabel = new Label();
    revealLabel.setText("");
    revealLabel.setOpacity(1);
    revealLabel.setStyle(
        "-fx-font-size: 40px; -fx-text-fill: red; -fx-font-weight: bold;"); // Style the label

    // Center the label in the pane
    revealLabel.setLayoutX((textbox.getWidth() - revealLabel.getWidth()) / 2 - 50);
    revealLabel.setLayoutY((textbox.getHeight() - revealLabel.getHeight()) / 2 + 190);

    // Add the label to the rootPane
    textbox.getChildren().add(revealLabel);
    revealLabel.toFront();
  }

  /** Method to set up the timer pane. */
  private void setupTimerPane() {
    // Create a new pane to hold the timer
    timerPane = new Pane();

    // Set the size and style of the timer pane
    timerPane.setPrefSize(101, 45);
    timerPane.setOpacity(0.75);

    // Set the style of the timer pane
    timerPane.setStyle(
        "-fx-background-color: white;"
            + "-fx-background-radius: 10px;"
            + "-fx-border-radius: 10px;"
            + "-fx-border-color: black;");

    // Set the position of the timer pane
    AnchorPane.setLeftAnchor(timerPane, 10.0);
    AnchorPane.setTopAnchor(timerPane, 10.0);

    // Create a label for the timer
    Label timerLabel = new Label();
    timerLabel.setFont(new Font(24));
    timerLabel.setAlignment(Pos.CENTER);
    timerLabel.setLayoutX(21.0);
    timerLabel.setLayoutY(8.0);

    //  Bind the timer label to the countdown timer
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());

    // Add the timer label to the timer pane
    timerPane.getChildren().add(timerLabel);
    puzzlePane.getChildren().add(timerPane);
    timerPane.toFront();
  }

  /**
   * Method to toggle the visibility of the puzzle pieces.
   *
   * @param visible
   */
  private void togglevisabilityofpieces(boolean visible) {
    // Set the visibility of the pieces
    piece1.setVisible(visible);

    // Set the visibility of the pieces
    piece2.setVisible(visible);
    piece3.setVisible(visible);
    piece4.setVisible(visible);
    piece5.setVisible(visible);
    piece6.setVisible(visible);
    piece7.setVisible(visible);
    piece8.setVisible(visible);
    piece9.setVisible(visible);
  }

  /** Method to set up the game. */
  private void setupGame() {

    // Set the layout of the pieces
    imagebg.toFront();
    // set up to front

    framebyitself.toFront();

    // Load and bind the background image
    createAndBindImageView(backgroundwithgame);
    createAndBindImageView(photoframeimg);

    togglevisabilityofpieces(true);

    // Set up drag and drop handlers
    setupDragAndDrop(piece1);
    setupDragAndDrop(piece2);
    setupDragAndDrop(piece3);
    setupDragAndDrop(piece4);
    setupDragAndDrop(piece5);
    setupDragAndDrop(piece6);
    setupDragAndDrop(piece7);
    setupDragAndDrop(piece8);
    setupDragAndDrop(piece9);

    // Load and bind the overlay pieces
    createAndBindImageView(outlinePiece1);
    createAndBindImageView(outlinePiece2);
    createAndBindImageView(outlinePiece3);
    createAndBindImageView(outlinePiece4);
    createAndBindImageView(outlinePiece5);
    createAndBindImageView(outlinePiece6);
    createAndBindImageView(outlinePiece7);
    createAndBindImageView(outlinePiece8);
    createAndBindImageView(outlinePiece9);
  }

  /**
   * Method to create and bind an ImageView to the pane.
   *
   * @param image
   */
  private void createAndBindImageView(ImageView image) {
    // If it's outline piece 7, increase the size of the pane height and move it down
    if (image == outlinePiece7) {
      // Unbind the properties first to avoid setting a bound value
      image.fitHeightProperty().unbind();
      image.fitWidthProperty().unbind();

      // Increase the height of the ImageView
      image.setFitHeight(puzzlePane.getHeight() + 100);
      image.setFitWidth(puzzlePane.getWidth());

      // Rebind the properties after making changes
      image.fitHeightProperty().bind(puzzlePane.heightProperty());
      image.fitWidthProperty().bind(puzzlePane.widthProperty());

      // Move the image view down slightly by increasing its layoutY value
      image.setLayoutY(image.getLayoutY() + 5); // Move it 5 pixels down
      image.setLayoutX(image.getLayoutX() - 4); // Move it 4 pixels left
      return;
    }

    // Unbind the properties before modifying them
    image.fitWidthProperty().unbind();
    image.fitHeightProperty().unbind();

    // Set initial properties before rebinding
    image.setFitWidth(puzzlePane.getWidth());
    image.setFitHeight(puzzlePane.getHeight());

    // Rebind properties to maintain responsiveness with the pane
    image.fitWidthProperty().bind(puzzlePane.widthProperty());
    image.fitHeightProperty().bind(puzzlePane.heightProperty());
  }

  /**
   * Method to set up drag and drop handlers for the puzzle pieces.
   *
   * @param piece
   */
  private void setupDragAndDrop(ImageView piece) {
    piece.setOnMousePressed(event -> onPiecePressed(event, piece));
    piece.setOnMouseDragged(event -> onPieceDragged(event, piece));
    piece.setOnMouseReleased(event -> onPieceReleased(event, piece));
  }

  /**
   * Handles the mouse pressed event for puzzle pieces.
   *
   * <p>This method is triggered when the mouse is pressed on a puzzle piece. It calculates the
   * offsets between the current mouse position and the position of the puzzle piece, storing them
   * in {@code offsetX} and {@code offsetY}. This information is used to allow smooth dragging of
   * the piece when the mouse is moved.
   *
   * @param event the mouse event containing information about the mouse action.
   * @param piece the {@code ImageView} representing the puzzle piece that is being pressed.
   */
  private void onPiecePressed(MouseEvent event, ImageView piece) {
    offsetX = event.getSceneX() - piece.getLayoutX();
    offsetY = event.getSceneY() - piece.getLayoutY();
  }

  /**
   * Handles the mouse dragged event for puzzle pieces.
   *
   * <p>This method updates the position of the specified puzzle piece as the mouse is dragged. It
   * calculates the new layout coordinates of the piece based on the current mouse position,
   * adjusting for any predefined offset to ensure smooth dragging.
   *
   * @param event the mouse event containing the current mouse coordinates.
   * @param piece the {@code ImageView} representing the puzzle piece being dragged.
   */
  private void onPieceDragged(MouseEvent event, ImageView piece) {
    piece.setLayoutX(event.getSceneX() - offsetX);
    piece.setLayoutY(event.getSceneY() - offsetY);
  }

  /**
   * Handles the mouse released event for puzzle pieces.
   *
   * <p>This method is triggered when the mouse is released after dragging a puzzle piece. It checks
   * if the released piece is close enough to its target position, allowing it to snap into place.
   * If the piece is positioned correctly, its layout coordinates are updated, and its correctness
   * state is marked as true.
   *
   * @param event the mouse event containing information about the mouse action.
   * @param piece the {@code ImageView} representing the puzzle piece that is being released.
   */
  private void onPieceReleased(MouseEvent event, ImageView piece) {
    // Check if the piece is close enough to its target position to snap into place
    if (piece == piece1 && isCloseToTarget(piece, piece1TargetX, piece1TargetY)) {
      piece.setLayoutX(piece1TargetX);
      piece.setLayoutY(piece1TargetY);
      piece1Correct = true;
      piece.setVisible(false);
      outlinePiece3.setOpacity(1); // done
    } else if (piece == piece2 && isCloseToTarget(piece, piece2TargetX, piece2TargetY)) {
      piece.setLayoutX(piece2TargetX);
      piece.setLayoutY(piece2TargetY);
      piece2Correct = true;
      piece.setVisible(false);
      outlinePiece8.setOpacity(1); // done
    } else if (piece == piece3 && isCloseToTarget(piece, piece3TargetX, piece3TargetY)) {
      piece.setLayoutX(piece3TargetX);
      piece.setLayoutY(piece3TargetY);
      piece3Correct = true;
      piece.setVisible(false);
      outlinePiece1.setOpacity(1); // done
    } else if (piece == piece4 && isCloseToTarget(piece, piece4TargetX, piece4TargetY)) {
      piece.setLayoutX(piece4TargetX);
      piece.setLayoutY(piece4TargetY);
      piece4Correct = true;
      piece.setVisible(false);
      outlinePiece4.setOpacity(1); // done
    } else if (piece == piece5 && isCloseToTarget(piece, piece5TargetX, piece5TargetY)) {
      piece.setLayoutX(piece5TargetX);
      piece.setLayoutY(piece5TargetY);
      piece5Correct = true;
      piece.setVisible(false);
      outlinePiece2.setOpacity(1); // done
    } else if (piece == piece6 && isCloseToTarget(piece, piece6TargetX, piece6TargetY)) {
      piece.setLayoutX(piece6TargetX);
      piece.setLayoutY(piece6TargetY);
      piece6Correct = true;
      piece.setVisible(false);
      outlinePiece7.setOpacity(1); // done
    } else if (piece == piece7 && isCloseToTarget(piece, piece7TargetX, piece7TargetY)) {
      piece.setLayoutX(piece7TargetX);
      piece.setLayoutY(piece7TargetY);
      piece7Correct = true;
      piece.setVisible(false);
      outlinePiece5.setOpacity(1); // done
    } else if (piece == piece8 && isCloseToTarget(piece, piece8TargetX, piece8TargetY)) {
      piece.setLayoutX(piece8TargetX);
      piece.setLayoutY(piece8TargetY);
      piece8Correct = true;
      piece.setVisible(false);
      outlinePiece9.setOpacity(1); // not 1, 3, 8, 4, 2, 7, 5 or __ so must be
    } else if (piece == piece9 && isCloseToTarget(piece, piece9TargetX, piece9TargetY)) {
      piece.setLayoutX(piece9TargetX);
      piece.setLayoutY(piece9TargetY);
      piece9Correct = true;
      piece.setVisible(false);
      outlinePiece6.setOpacity(1);
    }

    // Check if all pieces are correctly placed
    checkIfPuzzleComplete();
  }

  /**
   * Checks if a puzzle piece is close to its target position within a specified threshold.
   *
   * <p>This method determines whether the provided {@code ImageView} piece is within a specified
   * distance from its target position, defined by the coordinates {@code targetX} and {@code
   * targetY}. It uses the {@code snapDistanceThreshold} to define how close the piece needs to be
   * to snap into place.
   *
   * @param piece the {@code ImageView} representing the puzzle piece to check.
   * @param targetX the X-coordinate of the target position.
   * @param targetY the Y-coordinate of the target position.
   * @return {@code true} if the piece is within the snap distance of the target position; {@code
   *     false} otherwise.
   */
  private boolean isCloseToTarget(ImageView piece, double targetX, double targetY) {
    return Math.abs(piece.getLayoutX() - targetX) < snapDistanceThreshold
        && Math.abs(piece.getLayoutY() - targetY) < snapDistanceThreshold;
  }

  /** Method to check if the puzzle is complete. */
  private void checkIfPuzzleComplete() {
    // If all pieces are correctly placed, show a success message
    if (piece1Correct
        && piece2Correct
        && piece3Correct
        && piece4Correct
        && piece5Correct
        && piece6Correct
        && piece7Correct
        && piece8Correct
        && piece9Correct) {

      // Create a delay
      Thread thread =
          new Thread(
              () -> {
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              });

      thread.start();

      context.setGardenToolFound(true);
      createAndBindImageView(banners);

      // Assuming you have a TextField named textbox
      textbox.toFront(); // Bring the TextBox to the front
      textbox.setOpacity(0); // Make the TextBox visible

      // Create a FadeTransition for the TextBox
      FadeTransition fadeIn = new FadeTransition(Duration.millis(500), textbox);
      fadeIn.setFromValue(0.0); // Start fully transparent
      fadeIn.setToValue(1.0); // End fully opaque
      fadeIn.setCycleCount(1); // Play once
      fadeIn.setAutoReverse(false);

      // Start the fade-in animation
      fadeIn.play();

      // set on finished
      fadeIn.setOnFinished(
          e -> {
            createRevealLabel();
            animatetext();
          });
    }
  }

  /** Method to animate the text reveal. */
  private void animatetext() {
    Timeline timeline = new Timeline();
    for (int i = 0; i < revealText.length(); i++) {
      final int index = i;
      KeyFrame keyFrame =
          new KeyFrame(
              Duration.millis(50 * i),
              event -> {
                // Append the next character to the label's text
                revealLabel.setText(revealLabel.getText() + revealText.charAt(index));
                System.out.println(revealText.charAt(index));
              });
      timeline.getKeyFrames().add(keyFrame);
    }

    // Play the animation
    timeline.play();

    // On finish
    timeline.setOnFinished(
        e -> {
          System.out.println("done");

          // Show the black overlay and set it to cover the entire pane
          blackOverlay.setVisible(true);
          blackOverlay.toFront();

          // Create a fade transition for the overlay to fade into black
          FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), blackOverlay);
          fadeTransition.setFromValue(0.0); // Start with fully transparent
          fadeTransition.setToValue(1.0); // End with fully opaque (black screen)

          // Define what happens after fade-in completes
          fadeTransition.setOnFinished(
              event -> {
                // Hide textbox and puzzle pieces
                textbox.setVisible(false);
                togglevisabilityofpieces(false);

                // Create a fade-out transition
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), blackOverlay);
                fadeOut.setFromValue(1.0); // Start with fully opaque
                fadeOut.setToValue(0.0); // End with fully transparent

                // Hide the overlay when done
                fadeOut.setOnFinished(
                    ev -> {
                      blackOverlay.setVisible(false); // Hide overlay after fading out

                      // Set everything opacity to 0 before starting fade-in
                      createAndBindImageView(backofphotoimg);
                      backofphotoimg.toFront();
                      goBackButton.toFront();
                      timerPane.toFront();

                      // Set opacity to 0 for fade-in
                      backofphotoimg.setOpacity(0);
                      goBackButton.setOpacity(0);
                      timerPane.setOpacity(0);

                      // Create a fade-in transition for backofphotoimg, goBackButton, and timerPane
                      FadeTransition fadeIn = new FadeTransition(Duration.seconds(1));

                      // Group all nodes to fade in together
                      fadeIn.setNode(backofphotoimg);
                      fadeIn.setFromValue(0.0); // Start with fully transparent
                      fadeIn.setToValue(1.0); // End with fully opaque

                      // Create a second fade-in transition for the other nodes
                      FadeTransition fadeInButton =
                          new FadeTransition(Duration.seconds(1), goBackButton);
                      fadeInButton.setFromValue(0.0);
                      fadeInButton.setToValue(1.0);

                      FadeTransition fadeInTimer =
                          new FadeTransition(Duration.seconds(1), timerPane);
                      fadeInTimer.setFromValue(0.0);
                      fadeInTimer.setToValue(1.0);

                      // Start the fade-in transitions
                      fadeIn.play();
                      fadeInButton.play();
                      fadeInTimer.play();

                      // Optionally, define what happens after all fade-ins are done
                      fadeIn.setOnFinished(
                          even -> {
                            System.out.println("Fade-in completed for backofphotoimg.");
                          });
                      fadeInButton.setOnFinished(
                          even -> {
                            System.out.println("Fade-in completed for goBackButton.");
                          });
                      fadeInTimer.setOnFinished(
                          even -> {
                            System.out.println("Fade-in completed for timerPane.");
                          });
                    });

                // Start the fade-out transition
                fadeOut.play();
              });

          // Start the fade-in transition
          fadeTransition.play();
        });
  }

  /** Method to set up the go back button. */
  private void setupGoBackButton() {
    // Create a new button for the go back button
    goBackButton = new Button("Go Back");

    // Set the style for the button
    goBackButton.setStyle(
        "-fx-background-radius: 10; "
            + "-fx-border-radius: 10; "
            + "-fx-border-color: black; "
            + "-fx-background-color: white; "
            + "-fx-text-fill: black; "
            + "-fx-font-size: 14px;");

    // Set the size and position for the button
    goBackButton.setPrefWidth(100);
    goBackButton.setPrefHeight(40);

    // Set the position of the button
    AnchorPane.setBottomAnchor(goBackButton, 10.0);
    AnchorPane.setRightAnchor(goBackButton, 10.0);

    // Set the action for the button
    goBackButton.setOnAction(event -> goBackToRoom());

    // Set the hover effect for the button
    goBackButton.setOnMouseEntered(
        e -> {
          goBackButton.setOpacity(0.7);
          goBackButton.setCursor(Cursor.HAND);
        });

    // Set the hover effect for the button
    goBackButton.setOnMouseExited(
        e -> {
          goBackButton.setOpacity(1);
          goBackButton.setCursor(Cursor.DEFAULT);
        });

    // Add the button to the pane
    puzzlePane.getChildren().add(goBackButton);
  }

  /** Method to go back to the room. */
  private void goBackToRoom() {
    try {
      App.setRoot("room");
    } catch (IOException e) {
      e.printStackTrace();
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
    volumeUp.setLayoutX(13);
    volumeUp.setLayoutY(53);
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
    timerPane.getChildren().add(volumeUp);

    // Set the size and position for the SVGPath
    volumeUpStroke.setScaleY(2.0);
    volumeUpStroke.setScaleX(2.0);
    volumeUpStroke.setScaleZ(2.0);
    volumeUpStroke.setLayoutX(19);
    volumeUpStroke.setLayoutY(53);
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
    timerPane.getChildren().add(volumeUpStroke);

    // Set the size and position for the SVGPath
    volumeOff.setScaleY(2.0);
    volumeOff.setScaleX(2.0);
    volumeOff.setScaleZ(2.0);
    volumeOff.setLayoutX(13);
    volumeOff.setLayoutY(53);
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
    timerPane.getChildren().add(volumeOff);
    // Check if the volume icon should be displayed
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

  /** Method to get volume up SVGPath. */
  public SVGPath getVolumeUp() {
    return volumeUp;
  }

  /** Method to set the volume up SVGPath. */
  public void setVolumeUp(SVGPath volumeUp) {
    this.volumeUp = volumeUp;
  }

  /** Method to get the volume up stroke SVGPath. */
  public SVGPath getVolumeOff() {
    return volumeOff;
  }

  /** Method to set the volume off SVGPath. */
  public void setVolumeOff(SVGPath volumeOff) {
    this.volumeOff = volumeOff;
  }
}
