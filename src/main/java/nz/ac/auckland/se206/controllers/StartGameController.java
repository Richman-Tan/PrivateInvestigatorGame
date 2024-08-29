package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;

public class StartGameController {
  @FXML private ImageView doorImage;

  /*
   * Handles the event when the play button is clicked. Transitions to the backstory view.
   */
  @FXML
  private void onPlay() throws IOException, ApiProxyException {
    App.setRoot("room");
  }

  /** Initializes the start view. */
  @FXML
  public void initialize() {}
}
