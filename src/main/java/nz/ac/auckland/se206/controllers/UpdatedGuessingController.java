package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class UpdatedGuessingController {
  // main pane
  @FXML private AnchorPane rootPane;

  @FXML private Pane guessPhotoPane;

  @FXML private Label lbltimer;

  @FXML
  private void hoverImage(MouseEvent event) {
    System.out.println("hovered");
  }

  @FXML
  private void clickedImage(MouseEvent event) {
    System.out.println("clicked");
  }
}
