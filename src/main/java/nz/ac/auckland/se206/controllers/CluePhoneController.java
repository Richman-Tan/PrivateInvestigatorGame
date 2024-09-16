package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class CluePhoneController {

  @FXML private AnchorPane rootPane;

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
    // Load background
    Image backgroundImage =
        new Image(
            BackstoryController.class
                .getResource("/images/cluedrawimages/clueshelf.png")
                .toString());

    createAndBindImageView(backgroundImage);

    // Create and setup the wiring game
    setupGame();
  }

  private void setupGame() {
    // Create start circles (left side)
    startCircleRed = createDraggableCircle(100, 150, Color.RED);
    startCircleBlue = createDraggableCircle(100, 250, Color.BLUE);
    startCircleGreen = createDraggableCircle(100, 350, Color.GREEN);

    // Create end circles (right side)
    endCircleRed = createFixedCircle(600, 150, Color.RED);
    endCircleBlue = createFixedCircle(600, 250, Color.BLUE);
    endCircleGreen = createFixedCircle(600, 350, Color.GREEN);

    // Initialize wires
    redWire = new Line();
    blueWire = new Line();
    greenWire = new Line();

    // Add all circles to the pane
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
