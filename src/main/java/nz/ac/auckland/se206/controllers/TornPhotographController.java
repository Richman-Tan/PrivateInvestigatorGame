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

  @FXML
  private ImageView piece1,
      piece2,
      piece3,
      piece4,
      piece5,
      piece6,
      piece7,
      piece8,
      piece9; // ImageView elements for the torn pieces
  @FXML
  private ImageView outlinePiece1,
      outlinePiece2,
      outlinePiece3,
      outlinePiece4,
      outlinePiece5,
      outlinePiece6,
      outlinePiece7,
      outlinePiece8,
      outlinePiece9; // ImageView elements for the overlay pieces

  // Variables for drag offset
  private double offsetX, offsetY;

  // Correct positions for each puzzle piece on the board (target coordinates)
  private double piece1TargetX = 515, piece1TargetY = 170; // done
  private double piece2TargetX = 480, piece2TargetY = 310; // done
  private double piece3TargetX = 280, piece3TargetY = 160; // done
  private double piece4TargetX = 200, piece4TargetY = 217; // done
  private double piece5TargetX = 335, piece5TargetY = 170; // done
  private double piece6TargetX = 334, piece6TargetY = 280; // done
  private double piece7TargetX = 420, piece7TargetY = 200;
  private double piece8TargetX = 584, piece8TargetY = 335; // done
  private double piece9TargetX = 505, piece9TargetY = 236; // done

  // Boolean flags to check if pieces are correctly placed
  private boolean piece1Correct,
      piece2Correct,
      piece3Correct,
      piece4Correct,
      piece5Correct,
      piece6Correct,
      piece7Correct,
      piece8Correct,
      piece9Correct;

  // Threshold to snap pieces into place
  private final double SNAP_THRESHOLD = 200;

  private TimerModel countdownTimer;

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
  }

  // Method to dynamically create the label and center it
  private void createRevealLabel() {
    revealLabel = new Label(); // Create the Label
    revealLabel.setText(""); // Initially empty
    revealLabel.setOpacity(1); // Initially invisible
    revealLabel.setStyle(
        "-fx-font-size: 40px; -fx-text-fill: red; -fx-font-weight: bold;"); // Style the label

    // Center the label in the pane
    revealLabel.setLayoutX((textbox.getWidth() - revealLabel.getWidth()) / 2 - 50);
    revealLabel.setLayoutY((textbox.getHeight() - revealLabel.getHeight()) / 2 + 190);

    // Add the label to the rootPane
    textbox.getChildren().add(revealLabel);
    revealLabel.toFront(); // Bring the label to the front
  }

  // Set up the timer pane
  private void setupTimerPane() {
    timerPane = new Pane();
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
    timerLabel.setFont(new Font(24));
    timerLabel.setAlignment(Pos.CENTER);
    timerLabel.setLayoutX(21.0);
    timerLabel.setLayoutY(8.0);

    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());

    timerPane.getChildren().add(timerLabel);
    puzzlePane.getChildren().add(timerPane);
    timerPane.toFront();
  }

  private void togglevisabilityofpieces(boolean visible) {
    piece1.setVisible(visible);
    piece2.setVisible(visible);
    piece3.setVisible(visible);
    piece4.setVisible(visible);
    piece5.setVisible(visible);
    piece6.setVisible(visible);
    piece7.setVisible(visible);
    piece8.setVisible(visible);
    piece9.setVisible(visible);
  }

  private void setupGame() {

    // Set the layout of the pieces
    imagebg.toFront();
    // set up to front

    framebyitself.toFront();

    // Load and bind the background image
    createAndBindImageView(backgroundwithgame);

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

  private void setupDragAndDrop(ImageView piece) {
    piece.setOnMousePressed(event -> onPiecePressed(event, piece));
    piece.setOnMouseDragged(event -> onPieceDragged(event, piece));
    piece.setOnMouseReleased(event -> onPieceReleased(event, piece));
  }

  private void onPiecePressed(MouseEvent event, ImageView piece) {
    offsetX = event.getSceneX() - piece.getLayoutX();
    offsetY = event.getSceneY() - piece.getLayoutY();
  }

  private void onPieceDragged(MouseEvent event, ImageView piece) {
    piece.setLayoutX(event.getSceneX() - offsetX);
    piece.setLayoutY(event.getSceneY() - offsetY);
  }

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

  private boolean isCloseToTarget(ImageView piece, double targetX, double targetY) {
    return Math.abs(piece.getLayoutX() - targetX) < SNAP_THRESHOLD
        && Math.abs(piece.getLayoutY() - targetY) < SNAP_THRESHOLD;
  }

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

  // Method to start the text reveal animation and zoom effect
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

  private void setupGoBackButton() {
    goBackButton = new Button("Go Back");
    goBackButton.setStyle(
        "-fx-background-radius: 10; "
            + "-fx-border-radius: 10; "
            + "-fx-border-color: black; "
            + "-fx-background-color: white; "
            + "-fx-text-fill: black; "
            + "-fx-font-size: 14px;");
    goBackButton.setPrefWidth(100);
    goBackButton.setPrefHeight(40);
    AnchorPane.setBottomAnchor(goBackButton, 10.0);
    AnchorPane.setRightAnchor(goBackButton, 10.0);
    goBackButton.setOnAction(event -> goBackToRoom());
    goBackButton.setOnMouseEntered(
        e -> {
          goBackButton.setOpacity(0.7);
          goBackButton.setCursor(javafx.scene.Cursor.HAND);
        });
    goBackButton.setOnMouseExited(
        e -> {
          goBackButton.setOpacity(1);
          goBackButton.setCursor(javafx.scene.Cursor.DEFAULT);
        });
    puzzlePane.getChildren().add(goBackButton);
  }

  private void goBackToRoom() {
    try {
      App.setRoot("room");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
