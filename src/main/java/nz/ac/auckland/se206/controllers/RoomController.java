package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController {

  @FXML private Rectangle rectCashier;
  @FXML private Rectangle rectPerson1;
  @FXML private Rectangle rectPerson2;
  @FXML private Rectangle rectPerson3;
  @FXML private Rectangle rectWaitress;
  @FXML private Label lblProfession;
  @FXML private Button btnGuess;
  @FXML private Button btnMenu;
  @FXML private Button btnCrimeScene;
  @FXML private Button btnGrandma;
  @FXML private Button btnGrandson;
  @FXML private Button btnUncle;
  @FXML private AnchorPane rootNode;

  @FXML private Label lbltimer;

  private static boolean isFirstTimeInit = true;
  private static GameStateContext context = GameStateContext.getInstance();

  private TimerModel countdownTimer;

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {
    if (isFirstTimeInit) {

      rootNode.setOpacity(0);
      FadeTransition fadeTransition = new FadeTransition();
      fadeTransition.setDuration(Duration.millis(1000));
      fadeTransition.setNode(rootNode);
      fadeTransition.setFromValue(0);
      fadeTransition.setToValue(1);
      fadeTransition.play();

      TextToSpeech.speak(
          "Chat with the three customers, and guess who is the " + context.getProfessionToGuess());
      isFirstTimeInit = false;
    }
    lblProfession.setText(context.getProfessionToGuess());

    // Set the menu visibility based on the GameStateContext
    updateMenuVisibility();

    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.start();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
  }

  /**
   * Handles mouse clicks on rectangles representing people in the room.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleRectangleClick(MouseEvent event) throws IOException {
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    context.handleRectangleClick(event, clickedRectangle.getId());
  }

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    App.setRoot("guessing");
    context.handleGuessClick();
  }

  /**
   * Handles the inspect uncle button click event.
   *
   * @param event
   * @throws IOException
   */
  @FXML
  private void onUncle(ActionEvent event) throws IOException {
    App.setRoot("suspect1room");
  }

  /**
   * Handles the inspect Grandmother button click event.
   *
   * @param event
   * @throws IOException
   */
  @FXML
  private void onGrandmother(ActionEvent event) throws IOException {
    context.setMenuVisible(true); // Toggle the visibility in the context
    App.setRoot("suspect2room");
  }

  /**
   * Handles the inspect Grandson button click event.
   *
   * @param event
   * @throws IOException
   */
  @FXML
  private void onGrandson(ActionEvent event) throws IOException {
    App.setRoot("suspect3room");
  }

  /**
   * Toggles the menu button when clicked.
   *
   * @param event
   * @throws IOException
   */
  @FXML
  private void onToggleMenu(ActionEvent event) {
    context.toggleMenuVisibility(); // Toggle the visibility in the context
    updateMenuVisibility(); // Update the visibility in the UI
  }

  /**
   * Updates the visibility of the menu buttons based on the isMenuVisible variable in the
   * GameStateContext.
   */
  private void updateMenuVisibility() {
    boolean isMenuVisible = context.isMenuVisible();

    if (isMenuVisible) {
      btnMenu.setStyle(
          "-fx-background-radius: 10 0 0 10; -fx-border-color: black transparent black black;"
              + " -fx-border-radius: 10 0 0 10; -fx-background-insets: 0;");
    } else {
      btnMenu.setStyle(
          "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;"
              + " -fx-background-insets: 0;");
    }

    btnCrimeScene.setVisible(isMenuVisible);
    btnCrimeScene.setManaged(isMenuVisible);

    btnGrandma.setVisible(isMenuVisible);
    btnGrandma.setManaged(isMenuVisible);

    btnGrandson.setVisible(isMenuVisible);
    btnGrandson.setManaged(isMenuVisible);

    btnUncle.setVisible(isMenuVisible);
    btnUncle.setManaged(isMenuVisible);
  }
}
