package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

// import nz.ac.auckland.se206.GameStateContext;

public class ClueSafeController {

  @FXML private AnchorPane anchorPane;
  @FXML private Label codeDisplay;
  @FXML private ImageView notes;
  private String line = "";
  private DropShadow permShadow = new DropShadow();

  @FXML
  private void initialize() {
    if (notes != null) {
      permShadow.setColor(Color.GOLD); // Customize the hover effect color
      permShadow.setRadius(5); // Customize the shadow effect
      notes.setEffect(permShadow);
      addHoverEffect(notes);

      // Check if the notes have already been found
      if (GameStateContext.getInstance().isNoteFound()) {
        notes.setOpacity(0); // Hide the tool if already found
      } else {
        notes.setOpacity(1); // Show the tool if not found
      }

      // Set mouse click event to mark the notes as found and hide it
      notes.setOnMouseClicked(
          event -> {
            notes.setOpacity(0); // Hide the tool after it's clicked
            System.out.println("Notes clicked");
            GameStateContext.getInstance().setNoteFound(true); // Mark as found in the context
          });
    }
    // Add the "Go Back" button
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

    anchorPane.getChildren().add(goBackButton);

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

  @FXML
  private void onPin(ActionEvent event) {
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
        default:
          // enter
      }
      codeDisplay.setText(line);
    }

    if (pinId.equals("delete")) {
      line = "";
      codeDisplay.setText("ENTER CODE");
    }

    if (pinId.equals("enter")) {
      if (line.equals("019")) {
        try {
          codeDisplay.setText("ENTERED");
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

  private void addHoverEffect(ImageView notes) {
    DropShadow hoverShadow = new DropShadow();
    hoverShadow.setColor(Color.CORNFLOWERBLUE); // Customize the hover effect color
    hoverShadow.setRadius(20); // Customize the shadow effect

    notes.setOnMouseEntered(
        e -> {
          notes.setEffect(hoverShadow); // Apply hover effect when mouse enters
        });

    notes.setOnMouseExited(
        e -> {
          notes.setEffect(permShadow); // Remove effect when mouse exits
        });
  }

  @FXML
  private void onNote(MouseEvent event) {
    try {
      App.setRoot("cluenotes");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
