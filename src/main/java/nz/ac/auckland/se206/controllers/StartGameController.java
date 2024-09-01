package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;

public class StartGameController {
  @FXML private ImageView doorImage;

  @FXML private AnchorPane rootPane;

  /*
   * Handles the event when the play button is clicked. Transitions to the backstory view.
   */
  @FXML
  private void onPlay() throws IOException, ApiProxyException {
    FadeTransition fadeTransition = new FadeTransition();
    fadeTransition.setDuration(javafx.util.Duration.millis(1000));
    fadeTransition.setNode(rootPane);
    fadeTransition.setFromValue(1);
    fadeTransition.setToValue(0);
    fadeTransition.setOnFinished(
        e -> {
          try {
            App.setRoot("backstory");
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        });
    fadeTransition.play();
  }

  /** Initializes the start view. */
  @FXML
  public void initialize() {}
}
