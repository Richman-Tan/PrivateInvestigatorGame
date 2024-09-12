package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

public class Suspect2RoomController {

  @FXML private Button btnMenu;
  @FXML private Button btnCrimeScene;
  @FXML private Button btnGrandma;
  @FXML private Button btnGrandson;
  @FXML private Button btnUncle;
  @FXML private TextField userChatBox;
  @FXML private TextArea suspect2ChatBox;
  @FXML private Circle sendButton;

  @FXML private Label lbltimer;

  private GameStateContext context = GameStateContext.getInstance();

  private TimerModel countdownTimer;

  /** Initializes the suspect 2 room view. */
  @FXML
  public void initialize() {
    // Set initial visibility of the buttons
    updateMenuVisibility();

    countdownTimer = SharedTimerModel.getInstance().getTimer();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());
  }

  /**
   * Handles the event when the crime scene button is clicked.
   *
   * @throws IOException if the root cannot be set
   */
  @FXML
  private void onRoom() throws IOException {
    App.setRoot("room");
  }

  @FXML
  private void onSend() {}

  /**
   * Handles the event when the uncle button is clicked.
   *
   * @param event the action event
   * @throws IOException if the root cannot be set
   */
  @FXML
  private void onUncle(ActionEvent event) throws IOException {
    App.setRoot("suspect1room");
  }

  /**
   * Handles the event when the grandma button is clicked.
   *
   * @param event the action event
   * @throws IOException if the root cannot be set
   */
  @FXML
  private void onGrandson(ActionEvent event) throws IOException {
    App.setRoot("suspect3room");
  }

  /**
   * Toggles the visibility of the menu and other buttons when the menu button is clicked.
   *
   * @param event the action event
   */
  @FXML
  private void onToggleMenu(ActionEvent event) {
    context.toggleMenuVisibility(); // Toggle the visibility in the context
    updateMenuVisibility(); // Update the visibility in the UI
  }

  /**
   * Updates the visibility of the menu and other buttons based on the isMenuVisible variable in the
   * GameStateContext.
   */
  private void updateMenuVisibility() {
    boolean isMenuVisible = context.isMenuVisible();

    if (isMenuVisible) {
      btnMenu.setStyle(
          "-fx-background-radius: 10 0 0 10; -fx-border-color:  black transparent black black;"
              + " -fx-border-radius: 10 0 0 10; -fx-background-insets: 0;");
    } else {
      btnMenu.setStyle(
          "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;"
              + " -fx-background-insets: 0;");
    }

    // Set visibility and management of other buttons based on isMenuVisible
    btnCrimeScene.setVisible(isMenuVisible);
    btnCrimeScene.setManaged(isMenuVisible);

    btnGrandma.setVisible(isMenuVisible);
    btnGrandma.setManaged(isMenuVisible);

    btnGrandson.setVisible(isMenuVisible);
    btnGrandson.setManaged(isMenuVisible);

    btnUncle.setVisible(isMenuVisible);
    btnUncle.setManaged(isMenuVisible);
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
}
