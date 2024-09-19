package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

// import nz.ac.auckland.se206.GameStateContext;

public class ClueSafeController {

  @FXML private AnchorPane anchorPane;
  @FXML private Label codeDisplay;
  @FXML private Group notes;
  @FXML private Group note1;
  @FXML private Group note2;
  @FXML private Group note3;
  @FXML private Pane safecontent;
  private String line = "";
  private DropShadow permShadow = new DropShadow();
  Button goBackButton = new Button("Go Back");

  boolean middleNote = false;
  boolean backNote = false;

  // Get timer
  private TimerModel countdownTimer;

  @FXML
  private void initialize() {

    // Create a Pane for the timer
    Pane timerPane = new Pane();
    timerPane.setPrefSize(101, 45); // Set the preferred size
    timerPane.setOpacity(0.75); // Set the opacity
    timerPane.setStyle(
        "-fx-background-color: white;"
            + "-fx-background-radius: 10px;"
            + "-fx-border-radius: 10px;"
            + "-fx-border-color: black;");

    // Position the timerPane
    AnchorPane.setLeftAnchor(timerPane, 10.0); // Set position using AnchorPane
    AnchorPane.setTopAnchor(timerPane, 10.0); // Set top anchor

    // Create a label for the timer
    Label timerLabel = new Label();
    timerLabel.setText("Label"); // Default text (will be updated by the timer)
    timerLabel.setFont(new Font(24)); // Set font size
    timerLabel.setAlignment(Pos.CENTER); // Align the text to the center
    timerLabel.setLayoutX(21.0); // Set the label's X position inside the Pane
    timerLabel.setLayoutY(8.0); // Set the label's Y position inside the Pane

    // Bind the timerLabel to the countdown timer
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    timerLabel.textProperty().bind(countdownTimer.timeStringProperty());

    // Add the label to the Pane
    timerPane.getChildren().add(timerLabel);

    // Add the timerPane to the rootPane
    anchorPane.getChildren().add(timerPane);

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

    timerPane.toFront();
  }

  /**
   * } Updates the code display based on the pin clicked.
   *
   * @param event the ActionEvent triggered by clicking a pin button
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

  @FXML
  private void onNote(MouseEvent event) {
    goBackButton.toBack();
    safecontent.toBack();
    notes.toBack(); // Hide the tool after it's clicked
    System.out.println("Notes clicked");
    GameStateContext.getInstance().setNoteFound(true); // Mark as found in the context
  }

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
}
