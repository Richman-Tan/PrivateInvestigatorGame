package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.se206.App;

public class Suspect1RoomController {

  @FXML private Button btnRight;

  @FXML private AnchorPane rightAnchorPane;

  @FXML
  public void initialize() {}

  @FXML
  private void onRight() throws IOException {
    App.setRoot("room");
  }
}
