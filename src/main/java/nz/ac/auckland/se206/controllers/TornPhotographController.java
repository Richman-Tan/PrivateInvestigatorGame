package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.App;

public class TornPhotographController {

  @FXML private AnchorPane puzzlePane; // Pane to hold the puzzle pieces

  @FXML private ImageView backgroundwithgame; // ImageView element for the background image

  @FXML private ImageView framebyitself; // ImageView element for the frame image

  @FXML private ImageView imagebg; // ImageView element for the background image

  @FXML private Button goBackButton;

  @FXML private Button flipButton; // Button to flip the image

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

  // State variable to track which side of the photo is visible
  private boolean isFrontVisible = true;

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
          // Perform actions when the image is clicked

          backgroundwithgame.setOpacity(1);

          // Set the layout of the pieces
          imagebg.toBack();

          // set up to front
          framebyitself.toBack();

          imagebg.setVisible(false);
          framebyitself.setVisible(false);

          setupGame();
        });

    setupGoBackButton();
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
      // Create an alert to show the puzzle is complete
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Puzzle Completed");
      alert.setHeaderText("Congratulations!");
      alert.setContentText("You have successfully completed the puzzle.");
      alert.showAndWait();

      setupFlipButton();
    }
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

  private void setupFlipButton() {
    // Create the flip button
    flipButton = new Button("Flip Image");
    flipButton.setStyle(
        "-fx-background-radius: 10; "
            + "-fx-border-radius: 10; "
            + "-fx-border-color: black; "
            + "-fx-background-color: white; "
            + "-fx-text-fill: black; "
            + "-fx-font-size: 14px;");
    flipButton.setPrefWidth(100);
    flipButton.setPrefHeight(40);
    AnchorPane.setTopAnchor(flipButton, 10.0);
    AnchorPane.setLeftAnchor(flipButton, 10.0);

    flipButton.setOnAction(event -> flipPhoto());

    flipButton.setOnMouseEntered(
        e -> {
          flipButton.setOpacity(0.7);
          flipButton.setCursor(Cursor.HAND);
        });

    flipButton.setOnMouseExited(
        e -> {
          flipButton.setOpacity(1);
          flipButton.setCursor(Cursor.DEFAULT);
        });

    puzzlePane.getChildren().add(flipButton);
  }

  private void flipPhoto() {}
}
