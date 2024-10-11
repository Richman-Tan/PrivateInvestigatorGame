package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;

public class TimeUpController {

  // Static fields
  // none for now

  // Instance fields
  @FXML private AnchorPane anchorPane;

  // Static methods
  // No static methods for now

  // Instance methods

  /**
   * Initializes the Cutscene after the associated FXML has been loaded.
   *
   * <p>This method is automatically called by the JavaFX framework when the FXML file for the
   * cutscnene view is loaded. It sets up the initial state of the cutscene controller by
   * configuring UI components, binding properties, and initializing any necessary data structures
   * or event listeners required for the controller's functionality.
   */
  public void initialize() {

    // Create a fade transition to fade in the root node
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(5000), anchorPane);

    // Set the opacity values
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    fadeTransition.setOnFinished(
        event -> {
          // After the fade transition is complete, navigate to the guessing scene
          try {
            App.setRoot("guessingScene");
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        });
  }
}
