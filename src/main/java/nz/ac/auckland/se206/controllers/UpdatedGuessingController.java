package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.GameStateContext;

public class UpdatedGuessingController {
  // main pane
  @FXML private AnchorPane rootPane;

  @FXML private Pane guessPhotoPane;
  @FXML private Pane verifyCulpritPane;

  @FXML private Rectangle recSuspect1;
  @FXML private Rectangle recSuspect2;
  @FXML private Rectangle recSuspect3;
  @FXML private ImageView confirmedSuspect1;
  @FXML private ImageView confirmedSuspect2;
  @FXML private ImageView confirmedSuspect3;
  @FXML private Label culpritLabel;

  @FXML private Label lbltimer;

  private GameStateContext context = GameStateContext.getInstance();
  private Label selectedLabel = new Label("");

  private TimerModel countdownTimer;

  private ChatCompletionRequest chatCompletionRequest;

  private String guessedsuspect;

  @FXML
  private void hoverImageGma(MouseEvent event) throws IOException {
    recSuspect2.setVisible(true);
    recSuspect2.setMouseTransparent(true);
  }

  @FXML
  private void hoverImageUncle(MouseEvent event) throws IOException {
    recSuspect1.setVisible(true);
    recSuspect1.setMouseTransparent(true);
  }

  @FXML
  private void hoverImageSon(MouseEvent event) throws IOException {
    recSuspect3.setVisible(true);
    recSuspect3.setMouseTransparent(true);
  }

  @FXML
  private void offHoverImageSon(MouseEvent event) {
    recSuspect3.setVisible(false);
  }

  @FXML
  private void offHoverImageGma(MouseEvent event) {
    recSuspect2.setVisible(false);
  }

  @FXML
  private void offHoverImageUncle(MouseEvent event) {
    recSuspect1.setVisible(false);
  }

  @FXML
  private void clickedImageUncle(MouseEvent event) throws IOException {
    guessedsuspect = "Uncle";
    recSuspect1.setVisible(true);
  }

  @FXML
  private void clickedImageSon(MouseEvent event) throws IOException {
    guessedsuspect = "Grandson";
    recSuspect3.setVisible(true);
  }

  @FXML
  private void clickedImageGma(MouseEvent event) throws IOException {
    guessedsuspect = "Grandma";
    recSuspect2.setVisible(true);
  }

  @FXML
  private void confirmCulprit(MouseEvent event) throws IOException {
    // open new pane to confirm culprit
    guessPhotoPane.setVisible(false);
    verifyCulpritPane.setVisible(true);
    switch (guessedsuspect) {
      case "Uncle":
        confirmedSuspect1.setVisible(true);
        culpritLabel.setText("The Uncle");
        break;
      case "Grandma":
        confirmedSuspect2.setVisible(true);
        culpritLabel.setText("The Widow");
        break;
      case "Grandson":
        confirmedSuspect3.setVisible(true);
        culpritLabel.setText("The Grandson");
        break;
    }
  }

  @FXML
  private void confirmExplanation(MouseEvent event) {
    // open new pane to confirm explanation
  }
}
