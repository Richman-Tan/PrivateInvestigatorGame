package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

public class ClueDrawerController {

  @FXML private AnchorPane anchorPane;
  @FXML private Pane labelPane;
  @FXML private Label timerLabel;
  @FXML private GridPane tileGridPane; // Add GridPane to FXML for tiles
  @FXML private StackPane container = new StackPane();
  @FXML private Button goBackButton;

  private Box crestBox; // Placeholder for crest; replace with a 3D model if available
  private Rotate rotateX, rotateY;
  private Translate translate; // Translate variable for positioning
  private double anchorX, anchorY;
  private double anchorAngleX = 0;
  private double anchorAngleY = 0;

  private double startX;
  private double startY;
  private TimerModel countdownTimer;

  // Define the grid size as 2x6
  private static final int ROWS = 2;
  private static final int COLS = 3;
  private Button[][] buttons = new Button[ROWS][COLS];
  private int emptyRow;
  private int emptyCol;

  @FXML
  private void initialize() {
    setupTimerPane();
    setupBackgroundImage();
    setupGardenTool();
    setupLeaves();

    // Initialize sliding game logic
    initializeTileGrid();

    // Bind the timer label to the countdown timer
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();

    // Bind the timer label to the countdown timer and bring the label pane to the front
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());
    labelPane.toFront();

    tileGridPane.toFront();

    // Create and set up the 3D crest object
    crestBox = new Box(100, 150, 5); // Placeholder for crest; replace with a 3D model if needed
    PhongMaterial material = new PhongMaterial();
    material.setDiffuseColor(Color.DARKGREEN);
    material.setSpecularColor(Color.GOLD);
    crestBox.setMaterial(material);

    // Set up rotations
    rotateX = new Rotate(0, Rotate.X_AXIS);
    rotateY = new Rotate(0, Rotate.Y_AXIS);
    crestBox.getTransforms().addAll(rotateX, rotateY);

    // Translate the crest to be visually centered
    translate = new Translate(0, 0, 0); // Centered in local coordinates
    crestBox.getTransforms().add(translate);

    // Create a Group to hold the 3D object
    Group crestGroup = new Group(crestBox);

    // Create a StackPane and add the Group to it
    StackPane stackPane = new StackPane(); // StackPane will automatically center its children
    stackPane.getChildren().add(crestGroup); // Add the Group containing the 3D model

    // Bind StackPane size to AnchorPane size for dynamic resizing
    stackPane.prefWidthProperty().bind(anchorPane.widthProperty());
    stackPane.prefHeightProperty().bind(anchorPane.heightProperty());

    // Set alignment and constraints to center the StackPane in the AnchorPane
    AnchorPane.setTopAnchor(stackPane, 0.0);
    AnchorPane.setBottomAnchor(stackPane, 0.0);
    AnchorPane.setLeftAnchor(stackPane, 0.0);
    AnchorPane.setRightAnchor(stackPane, 0.0);

    // Add the StackPane (with crestGroup) to the mainPane
    anchorPane.getChildren().add(stackPane);

    // Add mouse control for rotating the 3D object
    addMouseControl(crestBox, stackPane);

    // Add listeners to debug the size changes
    anchorPane
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              System.out.println("AnchorPane Width: " + newVal);
            });
    anchorPane
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              System.out.println("AnchorPane Height: " + newVal);
            });
    stackPane
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              System.out.println("StackPane Width: " + newVal);
            });
    stackPane
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              System.out.println("StackPane Height: " + newVal);
            });

    setupGoBackButton();
  }

  private void addMouseControl(Box box, Node sceneNode) {
    sceneNode.setOnMousePressed(
        event -> {
          anchorX = event.getSceneX();
          anchorY = event.getSceneY();
          anchorAngleX = rotateX.getAngle();
          anchorAngleY = rotateY.getAngle();
        });

    sceneNode.setOnMouseDragged(
        event -> {
          rotateX.setAngle(anchorAngleX - (anchorY - event.getSceneY()));
          rotateY.setAngle(anchorAngleY + (anchorX - event.getSceneX()));
        });
  }

  // Initialize and draw the grid for the sliding game with an almost solved configuration
  private void initializeTileGrid() {
    // Create a container to hold the GridPane and center it on the screen
    container.setStyle(
        "-fx-background-color: #f0f0f0; -fx-border-color: gray; -fx-border-width: 3px;");
    container.setPadding(new Insets(10));
    container.setAlignment(Pos.CENTER); // Center the GridPane inside the container
    container.setMaxSize(
        400, 400); // Set a maximum size for the container to avoid filling the screen

    // Set the size of the GridPane
    tileGridPane.setAlignment(Pos.CENTER); // Center the GridPane inside the container
    tileGridPane.setPrefSize(240, 160); // Set preferred size of the GridPane based on ROWS and COLS

    // Add the tileGridPane to the container
    container.getChildren().add(tileGridPane);

    // Center the container within the AnchorPane
    AnchorPane.setTopAnchor(container, null);
    AnchorPane.setBottomAnchor(container, null);
    AnchorPane.setLeftAnchor(container, null);
    AnchorPane.setRightAnchor(container, null);

    // Set specific constraints to center the container
    AnchorPane.setTopAnchor(container, (anchorPane.getHeight() - container.getMaxHeight()) / 2);
    AnchorPane.setLeftAnchor(container, (anchorPane.getWidth() - container.getMaxWidth()) / 2);

    // Listen to changes in AnchorPane's width and height to keep it centered
    anchorPane
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              AnchorPane.setLeftAnchor(
                  container, (newVal.doubleValue() - container.getMaxWidth()) / 2);
            });

    anchorPane
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              AnchorPane.setTopAnchor(
                  container, (newVal.doubleValue() - container.getMaxHeight()) / 2);
            });

    // Add the container to the AnchorPane
    anchorPane.getChildren().add(container);

    // Initialize the grid in a solved state
    emptyRow = ROWS - 1;
    emptyCol = COLS - 1;

    List<Integer> tiles = new ArrayList<>();
    for (int i = 1; i < ROWS * COLS; i++) {
      tiles.add(i);
    }
    tiles.add(0); // Empty space at the end

    // Place the buttons in the solved order
    int index = 0;
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLS; j++) {
        int tileValue = tiles.get(index++);
        Button button = createTileButton(tileValue);
        buttons[i][j] = button;
        tileGridPane.add(button, j, i); // Place buttons in the GridPane

        if (tileValue == 0) { // Empty space
          emptyRow = i;
          emptyCol = j;
        }
      }
    }

    // Randomize the grid slightly by making a few valid moves
    randomizeGrid(4); // Make 10 random moves from the solved state
  }

  // Perform a specified number of random moves to shuffle the grid
  private void randomizeGrid(int moves) {
    for (int i = 0; i < moves; i++) {
      // Get all possible moves from the current empty space position
      List<int[]> validMoves = getValidMoves(emptyRow, emptyCol);

      // Choose a random valid move
      int[] move = validMoves.get((int) (Math.random() * validMoves.size()));
      int newRow = move[0];
      int newCol = move[1];

      // Swap the empty space with the chosen tile
      swapTiles(newRow, newCol);
    }
  }

  // Swap the empty space with the tile at the specified position
  private void swapTiles(int newRow, int newCol) {
    // Swap the button texts and visibility
    buttons[emptyRow][emptyCol].setText(buttons[newRow][newCol].getText());
    buttons[emptyRow][emptyCol].setVisible(true);
    buttons[newRow][newCol].setText("");
    buttons[newRow][newCol].setVisible(false);

    // Update the empty space position
    emptyRow = newRow;
    emptyCol = newCol;
  }

  // Get a list of valid moves from the specified empty space position
  private List<int[]> getValidMoves(int row, int col) {
    List<int[]> moves = new ArrayList<>();

    // Check all four possible directions (up, down, left, right) for valid moves
    if (row > 0) moves.add(new int[] {row - 1, col}); // Up
    if (row < ROWS - 1) moves.add(new int[] {row + 1, col}); // Down
    if (col > 0) moves.add(new int[] {row, col - 1}); // Left
    if (col < COLS - 1) moves.add(new int[] {row, col + 1}); // Right

    return moves;
  }

  // Creates a button representing a tile
  // Creates a button representing a tile with 3D effect, shadow, and hover highlight
  private Button createTileButton(int value) {
    Button button = new Button(value == 0 ? "" : String.valueOf(value));
    button.setMinSize(80, 80);
    button.setMaxSize(80, 80);

    // 3D effect using background color, borders, and shadow
    button.setStyle(
        "-fx-font-size: 18px; "
            + "-fx-background-color: lightblue; "
            + "-fx-background-radius: 10; "
            + "-fx-border-radius: 10; "
            + "-fx-border-width: 2px; "
            + "-fx-border-color: darkblue; "
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 10, 0.5, 0, 2);");

    // Set hover highlight effect
    button.setOnMouseEntered(
        event -> {
          button.setStyle(
              "-fx-font-size: 18px; "
                  + "-fx-background-color: deepskyblue; " // Lighter blue for hover
                  + "-fx-background-radius: 10; "
                  + "-fx-border-radius: 10; "
                  + "-fx-border-width: 2px; "
                  + "-fx-border-color: darkblue; "
                  + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 10, 0.5, 0, 2);");
        });

    button.setOnMouseExited(
        event -> {
          button.setStyle(
              "-fx-font-size: 18px; "
                  + "-fx-background-color: lightblue; "
                  + "-fx-background-radius: 10; "
                  + "-fx-border-radius: 10; "
                  + "-fx-border-width: 2px; "
                  + "-fx-border-color: darkblue; "
                  + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 10, 0.5, 0, 2);");
        });

    // Add mouse event handlers to make the tile draggable
    button.setOnMousePressed(
        event -> {
          // Only allow dragging if the button is adjacent to the empty space
          int row = GridPane.getRowIndex(button);
          int col = GridPane.getColumnIndex(button);

          if (isAdjacentToEmpty(row, col)) {
            startX = event.getSceneX();
            startY = event.getSceneY();
          } else {
            // Prevent dragging by consuming the event if not adjacent
            event.consume();
          }
        });

    button.setOnMouseDragged(
        event -> {
          // Check if the tile is allowed to be dragged (only if it is adjacent to the empty space)
          int row = GridPane.getRowIndex(button);
          int col = GridPane.getColumnIndex(button);

          if (isAdjacentToEmpty(row, col)) {
            double offsetX = event.getSceneX() - startX;
            double offsetY = event.getSceneY() - startY;

            // Temporarily move the button to the drag position
            button.setTranslateX(offsetX);
            button.setTranslateY(offsetY);
          }
        });

    button.setOnMouseReleased(
        event -> {
          // Get the current row and column of the button
          int row = GridPane.getRowIndex(button);
          int col = GridPane.getColumnIndex(button);

          // Check if the tile can be moved to the empty space
          if (isAdjacentToEmpty(row, col)) {
            // Swap the positions of the button and the empty space
            buttons[emptyRow][emptyCol].setText(button.getText());
            buttons[emptyRow][emptyCol].setVisible(true);
            button.setText("");
            button.setVisible(false);

            // Update the empty space position
            emptyRow = row;
            emptyCol = col;

            // Reset translation values
            button.setTranslateX(0);
            button.setTranslateY(0);

            // Check if the player has won the game
            if (isWin()) {
              System.out.println("You won!");
              Platform.runLater(
                  () -> {
                    // set the opacity of the grid to 0
                    tileGridPane.setOpacity(0);
                    container.setOpacity(0);
                  });
            }
          } else {
            // Reset translation if not a valid move
            button.setTranslateX(0);
            button.setTranslateY(0);
          }
        });

    return button;
  }

  // Checks if the tile is adjacent to the empty space
  private boolean isAdjacentToEmpty(int row, int col) {
    return (Math.abs(row - emptyRow) == 1 && col == emptyCol)
        || (Math.abs(col - emptyCol) == 1 && row == emptyRow);
  }

  // Checks if the tiles are in the correct order
  private boolean isWin() {
    int value = 1;
    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLS; j++) {
        if (i == ROWS - 1 && j == COLS - 1) {
          return true; // Empty space at the end
        }
        if (!buttons[i][j].getText().equals(String.valueOf(value++))) {
          return false;
        }
      }
    }
    return true;
  }

  // Set up the timer pane
  private void setupTimerPane() {
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
    timerLabel.setFont(new Font(24));
    timerLabel.setAlignment(Pos.CENTER);
    timerLabel.setLayoutX(21.0);
    timerLabel.setLayoutY(8.0);

    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());

    timerPane.getChildren().add(timerLabel);
    anchorPane.getChildren().add(timerPane);
    timerPane.toFront();
  }

  // Load and bind the background image
  private void setupBackgroundImage() {
    Image backgroundImage =
        new Image(
            BackstoryController.class
                .getResource("/images/cluedrawimages/clueshelf.png")
                .toString());
    createAndBindImageView(backgroundImage, false);
  }

  private void setupGardenTool() {
    Image gardenToolImage =
        new Image(
            ClueDrawerController.class
                .getResource("/images/cluedrawimages/gardenshears.png")
                .toString());
    ImageView gardenToolView = createAndBindImageView(gardenToolImage, false);
    if (GameStateContext.getInstance().isGardenToolFound()) {
      gardenToolView.setOpacity(0);
    } else {
      gardenToolView.setOpacity(1);
      gardenToolView.setOnMouseClicked(
          event -> {
            gardenToolView.setOpacity(0);
            GameStateContext.getInstance().setGardenToolFound(true);
            System.out.println("Garden tool clicked");
          });
    }
  }

  private void setupLeaves() {
    String[] leafImages = {
      "/images/cluedrawimages/leaf1.png",
      "/images/cluedrawimages/leaf2.png",
      "/images/cluedrawimages/leaf3.png",
      "/images/cluedrawimages/leaf4.png",
      "/images/cluedrawimages/leaf5.png",
      "/images/cluedrawimages/leaf6.png"
    };
    for (String leafPath : leafImages) {
      Image leafImage = new Image(ClueDrawerController.class.getResource(leafPath).toString());
      ImageView leafView = createAndBindImageView(leafImage, true);
      makeDraggable(leafView);
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
    anchorPane.getChildren().add(goBackButton);
  }

  private void goBackToRoom() {
    try {
      App.setRoot("room");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ImageView createAndBindImageView(Image image, boolean draggable) {
    ImageView imageView = new ImageView(image);
    imageView.setFitWidth(anchorPane.getWidth());
    imageView.setFitHeight(anchorPane.getHeight());
    imageView.fitWidthProperty().bind(anchorPane.widthProperty());
    imageView.fitHeightProperty().bind(anchorPane.heightProperty());
    anchorPane.getChildren().add(imageView);
    if (draggable) {
      makeDraggable(imageView);
    }
    return imageView;
  }

  private void makeDraggable(ImageView imageView) {
    imageView.setOnMousePressed(
        event -> {
          startX = event.getSceneX() - imageView.getX();
          startY = event.getSceneY() - imageView.getY();
        });
    imageView.setOnMouseDragged(
        event -> {
          imageView.setX(event.getSceneX() - startX);
          imageView.setY(event.getSceneY() - startY);
        });
  }
}
