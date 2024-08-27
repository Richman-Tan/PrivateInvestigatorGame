package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.App;

public class Suspect2RoomController {

  @FXML private Button btnRight;

  @FXML private AnchorPane rightAnchorPane;

  @FXML
  public void initialize() {}

  /**
   * This method is called when the user clicks on the right button. It will take the user to the
   * room screen.
   *
   * @throws IOException
   */
  @FXML
  private void onLeft() throws IOException {
    App.setRoot("room");
  }
}
