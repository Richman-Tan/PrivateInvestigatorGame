package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

public class ClueSafeController {

  // Inner classes (none in this case)

  // Static fields
  // (none in this case)

  // Static methods
  // (none in this case)

  // Instance fields
  @FXML private AnchorPane anchorPane;
  @FXML private Label codeDisplay;
  @FXML private Group notes;
  @FXML private Group note1;
  @FXML private Group note2;
  @FXML private Group note3;
  @FXML private Pane safecontent;
  @FXML private Label timerLabel;
  @FXML private Pane labelPane;
  private String line = "";
  private DropShadow permShadow = new DropShadow();
  private Button goBackButton = new Button("Go Back");
  private boolean middleNote = false;
  private boolean backNote = false;
  private SVGPath volumeUpStroke = new SVGPath();
  private SVGPath volumeUp = new SVGPath();
  private SVGPath volumeOff = new SVGPath();

  // Get timer
  private TimerModel countdownTimer;

  // Constructors
  // (default constructor is implied)

  // Instance methods

  /**
   * Initializes the ClueSafeController after the associated FXML has been loaded.
   *
   * <p>This method is automatically called by the JavaFX framework when the FXML file for the
   * ClueSafe view is loaded. It sets up the initial state of the ClueSafe controller by configuring
   * UI components, binding properties, and initializing any necessary data structures or event
   * listeners required for the controller's functionality.
   */
  @FXML
  private void initialize() {
    // Bind the timerLabel to the countdown timer
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());

    if (GameStateContext.getInstance().isSafeOpen()) {
      safecontent.toFront();
      permShadow.setColor(Color.GOLD); // Customize the hover effect color
      permShadow.setRadius(5); // Customize the shadow effect
      notes.setEffect(permShadow);
      note1.setEffect(permShadow);
      note2.setEffect(permShadow);
      note3.setEffect(permShadow);
      addHoverEffect(notes);
      addHoverEffect(note1);
      addHoverEffect(note2);
      addHoverEffect(note3);

      // Check if the notes have already been found
      if (GameStateContext.getInstance().isNoteFound()) {
        notes.toBack();
      } else {
        notes.toFront();
      }
    }

    // Add the "Go Back" button
    goBackButton.setStyle(
        "-fx-background-color: #c1b8b5; -fx-background-radius: 10px; -fx-border-radius: 10px;"
            + " -fx-border-color: #3f2218; -fx-border-width: 4px; -fx-text-fill:"
            + " black;-fx-font-size: 14px; -fx-background-insets: 0; -fx-border-insets: 0;");
    goBackButton.setOpacity(0.75);
    goBackButton.setPrefWidth(100);
    goBackButton.setPrefHeight(40);

    // Add hover effect to the button
    goBackButton.setOnMouseEntered(
        e -> {
          // Apply custom styles for hover
          goBackButton.setStyle(
              " -fx-background-color: #775E55; -fx-background-radius:"
                  + " 10px; -fx-border-radius: 10px; -fx-border-color: #3f2218;"
                  + " -fx-border-width: 4px;-fx-padding: 5; -fx-border-width: 3; -fx-cursor:"
                  + " hand; -fx-text-fill: #c1b8b5;"
                  + "-fx-font-size: 14px; "
                  + "-fx-background-insets: 0; "
                  + "-fx-border-insets: 0;");
          goBackButton.setOpacity(0.75);
        });

    // Set up mouse exit effect for the goBack button
    goBackButton.setOnMouseExited(
        e -> {
          goBackButton.setStyle(
              "-fx-background-color: #c1b8b5; -fx-background-radius: 10px; -fx-border-radius: 10px;"
                  + " -fx-border-color: #3f2218; -fx-border-width: 4px; -fx-text-fill:"
                  + " black;-fx-font-size: 14px; -fx-background-insets: 0; -fx-border-insets:"
                  + " 0; -fx-padding: 5; -fx-border-width: 3; -fx-cursor: default;");
          goBackButton.setOpacity(0.75);
          //goBackButton.setOpacity(1); // Reset opacity to 1
          //goBackButton.setCursor(javafx.scene.Cursor.DEFAULT); // Reset cursor
        });

    // Position the button at the bottom-right corner
    AnchorPane.setBottomAnchor(goBackButton, 10.0); // 10px from the bottom
    AnchorPane.setRightAnchor(goBackButton, 10.0); // 10px from the right

    anchorPane.getChildren().add(goBackButton);
    goBackButton.toFront();

    // Set the action when the button is clicked
    goBackButton.setOnAction(
        event -> {
          try {
            App.setRoot("room");
          } catch (IOException e) {
            e.printStackTrace();
          }
        });

    labelPane.toFront();
    // Add the volume button to the label pane and show it
    showVolumeButton();
  }

  /**
   * Adds a hover effect to the specified image group.
   *
   * <p>This method applies a drop shadow effect to the given {@code Group} when the mouse pointer
   * enters the area of the image. The shadow's color and radius can be customized to create a
   * visually appealing hover effect, enhancing user interaction. The effect is removed when the
   * mouse pointer exits the area of the image.
   *
   * @param image the {@code Group} representing the image to which the hover effect will be
   *     applied.
   */
  private void addHoverEffect(Group image) {
    DropShadow hoverShadow = new DropShadow();
    hoverShadow.setColor(Color.CORNFLOWERBLUE); // Customize the hover effect color
    hoverShadow.setRadius(20); // Customize the shadow effect

    image.setOnMouseEntered(
        e -> {
          image.setEffect(hoverShadow); // Apply hover effect when mouse enters
        });

    image.setOnMouseExited(
        e -> {
          image.setEffect(permShadow); // Remove effect when mouse exits
        });
  }

  /**
   * Handles the pin button clicks within the user interface.
   *
   * <p>This method is invoked when a pin button is clicked. It retrieves the clicked button,
   * determines its identifier, and processes the action associated with that specific pin. This
   * allows for dynamic interactions with the user interface based on which pin was activated.
   *
   * @param event the action event triggered by clicking a pin button.
   */
  @FXML
  private void onPin(ActionEvent event) {
    // Get the clicked pin
    Button clickedPin = (Button) event.getSource();
    String pinId = clickedPin.getId();
    if (line.length() < 3) {
      switch (pinId) {
        case "zero":
          line = line + "0";
          break;
        case "one":
          line = line + "1";
          break;
        case "two":
          line = line + "2";
          break;
        case "three":
          line = line + "3";
          break;
        case "four":
          line = line + "4";
          break;
        case "five":
          line = line + "5";
          break;
        case "six":
          line = line + "6";
          break;
        case "seven":
          line = line + "7";
          break;
        case "eight":
          line = line + "8";
          break;
        case "nine":
          line = line + "9";
          break;
      }
      codeDisplay.setText(line);
    }

    // Check to delete code
    if (pinId.equals("delete")) {
      line = "";
      codeDisplay.setText("ENTER CODE");
    }

    // Check if the entered code is correct
    if (pinId.equals("enter")) {
      if (line.equals("019")) {
        try {
          codeDisplay.setText("ENTERED");
          GameStateContext.getInstance().setSafeOpen(true);
          App.setRoot("cluesafeopened");
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        line = "";
        codeDisplay.setText("INCORRECT");
      }
    }
  }

  /**
   * Handles the note click event within the user interface.
   *
   * <p>This method is triggered when a note is clicked. It manages the visibility of various UI
   * elements by sending them to the back of the scene, effectively hiding them from view.
   * Additionally, it logs a message to the console and updates the game state to indicate that the
   * note has been found.
   *
   * @param event the mouse event triggered by clicking the note.
   */
  @FXML
  private void onNote(MouseEvent event) {
    goBackButton.toBack();
    safecontent.toBack();
    notes.toBack(); // Hide the tool after it's clicked
    System.out.println("Notes clicked");
    GameStateContext.getInstance().setNoteFound(true); // Mark as found in the context
  }

  /**
   * Handles the page click event within the user interface.
   *
   * <p>This method is triggered when a page (or note) is clicked. It identifies which page was
   * clicked and updates the corresponding state flags. The clicked page is brought to the front of
   * the scene, ensuring that it is visible to the user. If both the middle note and the back note
   * are clicked, the "Go Back" button is also brought to the front.
   *
   * @param event the mouse event triggered by clicking on a page.
   */
  @FXML
  private void onPage(MouseEvent event) {
    // Set mouse click event to bring the notes to front
    Group page = (Group) event.getSource();
    switch (page.getId()) {
      case "note2":
        middleNote = true;
        break;
      case "note3":
        backNote = true;
        break;
    }
    page.toFront();
    if (middleNote && backNote) {
      goBackButton.toFront();
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
    labelPane.getChildren().add(volumeUp);

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
    labelPane.getChildren().add(volumeUpStroke);

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
    labelPane.getChildren().add(volumeOff);
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
}
