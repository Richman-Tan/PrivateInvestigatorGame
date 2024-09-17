package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.controllers.SharedTimerModel;
import nz.ac.auckland.se206.controllers.TimerModel;
import nz.ac.auckland.se206.states.GameOver;
import nz.ac.auckland.se206.states.GameStarted;
import nz.ac.auckland.se206.states.GameState;
import nz.ac.auckland.se206.states.Guessing;

/**
 * Context class for managing the state of the game. Handles transitions between different game
 * states and maintains game data such as the professions and rectangle IDs.
 */
public class GameStateContext {

  private final GameStarted gameStartedState;
  private final Guessing guessingState;
  private final GameOver gameOverState;
  private GameState gameState;
  private List<String> listOfVisitors;

  private static GameStateContext instance;
  // Add this variable to manage first time initialization
  private boolean firstTimeInit;
  // Add this variable to manage menu visibility
  private boolean isMenuVisible;
  // State of whether the garden tool shas been found
  private boolean isGardenToolFound;
  // State of whether the note has been found
  private boolean isNoteFound;
  // State of whether the safe has been opened
  private boolean isSafeOpen;
  // State of wheter the phone has been found.
  private boolean isPhoneFound;
  // State of whether the guess has been pressed.
  private boolean isGuessPressed;

  private TimerModel countdownTimer;

  /** Constructs a new GameStateContext and initializes the game states. */
  public GameStateContext() {
    gameStartedState = new GameStarted(this);
    guessingState = new Guessing(this);
    gameOverState = new GameOver(this);
    listOfVisitors = new ArrayList<>();

    gameState = gameStartedState; // Initial state

    this.firstTimeInit = true;
    this.isMenuVisible = false;
    this.isGardenToolFound = false;
    this.isNoteFound = false;
    this.isSafeOpen = false;
    this.isPhoneFound = false;
    this.isGuessPressed = false;
  }

  // Static method to get the single instance of GameStateContext
  public static GameStateContext getInstance() {
    if (instance == null) {
      instance = new GameStateContext();
    }
    return instance;
  }

  /**
   * Sets the current state of the game.
   *
   * @param state the new state to set
   */
  public void setState(GameState state) {
    this.gameState = state;
  }

  /**
   * Gets the initial game started state.
   *
   * @return the game started state
   */
  public GameState getGameStartedState() {
    return gameStartedState;
  }

  /**
   * Gets the guessing state.
   *
   * @return the guessing state
   */
  public GameState getGuessingState() {
    return guessingState;
  }

  /**
   * Gets the game over state.
   *
   * @return the game over state
   */
  public GameState getGameOverState() {
    return gameOverState;
  }

  /**
   * Handles the event when a rectangle is clicked.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @param rectangleId the ID of the clicked rectangle
   * @throws IOException if there is an I/O error
   */
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {
    gameState.handleRectangleClick(event, rectangleId);
  }

  /**
   * Handles the event when the guess button is clicked.
   *
   * @throws IOException if there is an I/O error
   */
  public void handleGuessClick() throws IOException {
    gameState.handleGuessClick();
  }

  /**
   * Gets the isMenuVisible variable.
   *
   * @return
   */
  public boolean isMenuVisible() {
    return isMenuVisible;
  }

  /**
   * Sets the isMenuVisible variable.
   *
   * @param menuVisible
   */
  public void setMenuVisible(boolean menuVisible) {
    this.isMenuVisible = menuVisible;
  }

  /** Toggles the visibility of the menu. */
  public void toggleMenuVisibility() {
    this.isMenuVisible = !this.isMenuVisible;
  }

  // Getter for garden tool state
  public boolean isGardenToolFound() {
    return isGardenToolFound;
  }

  // Setter for garden tool state
  public void setGardenToolFound(boolean found) {
    this.isGardenToolFound = found;
  }

  // Getter for safe state
  public boolean isSafeOpen() {
    return isSafeOpen;
  }

  // Setter for safe state
  public void setSafeOpen(boolean open) {
    this.isSafeOpen = open;
  }

  // Getter for note state
  public boolean isNoteFound() {
    return isNoteFound;
  }

  // Setter for note state
  public void setNoteFound(boolean found) {
    this.isNoteFound = found;
  }

  public void addVisitor(String visitor) {
    // Add visitor to the list of visitors
    listOfVisitors.add(visitor);
  }

  @SuppressWarnings("rawtypes")
  public List getListOfVisitors() {
    // Return the list of visitors
    return listOfVisitors;
  }

  // Getter for guess pressed state
  public boolean isGuessPressed() {
    return isGuessPressed;
  }

  // Setter for guess pressed state
  public void setGuessPressed(boolean pressed) {
    this.isGuessPressed = pressed;
  }

  /**
   * Resets the game state to the initial game started state.
   *
   * @throws IOException if there is an I/O error
   */
  public void reset() throws IOException {
    // Reset instance
    isMenuVisible = false;
    isGardenToolFound = false;
    isPhoneFound = false;
    isNoteFound = false;
    isSafeOpen = false;
    isGuessPressed = false;
    firstTimeInit = true;
    gameState = gameStartedState;
    listOfVisitors.clear();

    countdownTimer = SharedTimerModel.getInstance().getTimer();

    countdownTimer.resetI();
    countdownTimer.stop();
    SharedTimerModel.getInstance().resetTimer();
  }

  public boolean isPhoneFound() {
    return isPhoneFound;
  }

  public void setPhoneFound(boolean b) {
    this.isPhoneFound = b;
  }

  public boolean isFirstTimeInit() {
    return firstTimeInit;
  }

  public void setFirstTimeInit(boolean firstTimeInit) {
    this.firstTimeInit = firstTimeInit;
  }
}
