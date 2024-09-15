package nz.ac.auckland.se206;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.states.GameOver;
import nz.ac.auckland.se206.states.GameStarted;
import nz.ac.auckland.se206.states.GameState;
import nz.ac.auckland.se206.states.Guessing;
import org.yaml.snakeyaml.Yaml;

/**
 * Context class for managing the state of the game. Handles transitions between different game
 * states and maintains game data such as the professions and rectangle IDs.
 */
public class GameStateContext {

  private final String rectIdToGuess;
  private final String professionToGuess;
  private final Map<String, String> rectanglesToProfession;
  private final GameStarted gameStartedState;
  private final Guessing guessingState;
  private final GameOver gameOverState;
  private GameState gameState;
  private List<String> listOfVisitors;

  private static GameStateContext instance;

  private boolean isMenuVisible = false; // Add this variable to manage menu visibility

  // State of whether the garden tool has been found
  private boolean isGardenToolFound = false;

  /** Constructs a new GameStateContext and initializes the game states and professions. */
  public GameStateContext() {
    gameStartedState = new GameStarted(this);
    guessingState = new Guessing(this);
    gameOverState = new GameOver(this);
    listOfVisitors = new ArrayList<>();

    gameState = gameStartedState; // Initial state
    Map<String, Object> obj = null;
    Yaml yaml = new Yaml();
    try (InputStream inputStream =
        GameStateContext.class.getClassLoader().getResourceAsStream("data/professions.yaml")) {
      if (inputStream == null) {
        throw new IllegalStateException("File not found!");
      }
      obj = yaml.load(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }

    @SuppressWarnings("unchecked")
    List<String> professions = (List<String>) obj.get("professions");

    Random random = new Random();
    Set<String> randomProfessions = new HashSet<>();
    while (randomProfessions.size() < 3) {
      String profession = professions.get(random.nextInt(professions.size()));
      randomProfessions.add(profession);
    }

    String[] randomProfessionsArray = randomProfessions.toArray(new String[3]);
    rectanglesToProfession = new HashMap<>();
    rectanglesToProfession.put("rectPerson1", randomProfessionsArray[0]);
    rectanglesToProfession.put("rectPerson2", randomProfessionsArray[1]);
    rectanglesToProfession.put("rectPerson3", randomProfessionsArray[2]);

    int randomNumber = random.nextInt(3);
    rectIdToGuess =
        randomNumber == 0 ? "rectPerson1" : ((randomNumber == 1) ? "rectPerson2" : "rectPerson3");
    professionToGuess = rectanglesToProfession.get(rectIdToGuess);
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
   * Gets the profession to be guessed.
   *
   * @return the profession to guess
   */
  public String getProfessionToGuess() {
    return professionToGuess;
  }

  /**
   * Gets the ID of the rectangle to be guessed.
   *
   * @return the rectangle ID to guess
   */
  public String getRectIdToGuess() {
    return rectIdToGuess;
  }

  /**
   * Gets the profession associated with a specific rectangle ID.
   *
   * @param rectangleId the rectangle ID
   * @return the profession associated with the rectangle ID
   */
  public String getProfession(String rectangleId) {
    return rectanglesToProfession.get(rectangleId);
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

  public void addVisitor(String visitor) {
    // Add visitor to the list of visitors
    listOfVisitors.add(visitor);
  }

  public List getListOfVisitors() {
    // Return the list of visitors
    return listOfVisitors;
  }

  // public static boolean canPlayerMakeGuess() {
  //   return listOfVisitors.size() == 3;
  // }
}
