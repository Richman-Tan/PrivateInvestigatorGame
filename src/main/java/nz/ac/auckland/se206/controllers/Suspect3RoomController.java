package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.prompts.PromptEngineering;

/** Suspect3RoomController is the controller class for the Suspect 3 Room scene. */
public class Suspect3RoomController {

  @FXML private Button crimeSceneButton;
  @FXML private Button grandmaButton;
  @FXML private Button grandsonButton;
  @FXML private Button menuButton;
  @FXML private Button uncleButton;
  @FXML private TextField userChatBox;
  @FXML private TextArea suspect3ChatBox;
  @FXML private Circle sendButton;
  @FXML private Button guessButton;
  @FXML private AnchorPane rootNode;
  @FXML private Label lbltimer;
  @FXML private ImageView backgroundimg;

  private ChatCompletionRequest chatCompletionRequest;
  private GameStateContext context = GameStateContext.getInstance();
  private boolean firstTime = true;
  private TimerModel countdownTimer;

  private boolean isatleastoncecluefound =
      context.isGardenToolFound() || context.isPhoneFound() || context.isNoteFound();

  /** Initializes the suspect 3 room view. */
  @FXML
  public void initialize() {
    // Set initial visibility of the buttons based on the current menu visibility state
    updateMenuVisibility();

    // Initialize the countdown timer and bind its time string property to the label
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());

    // Create a task to perform the initialization in a separate thread
    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() throws IOException, URISyntaxException {
            try {
              checkGuessButton();
              ApiProxyConfig config = ApiProxyConfig.readConfig();
              // Create a chat completion request with the specified parameters
              chatCompletionRequest =
                  new ChatCompletionRequest(config)
                      .setN(1)
                      .setTemperature(0.2)
                      .setTopP(0.5)
                      .setMaxTokens(100);

              // Load the prompt template from a resource file
              URL resourceUrl =
                  PromptEngineering.class.getClassLoader().getResource("prompts/grandson.txt");
              String template = loadTemplate(resourceUrl.toURI());

              // Create system message with the loaded template
              ChatMessage systemMessage = new ChatMessage("system", template);
              runGpt(systemMessage);

              // If this is the first time the room is initialized, set the user chat box prompt
              // text
              if (firstTime == true) {
                userChatBox.setPromptText("Begin interrogating...");
                firstTime = false;
              }

            } catch (ApiProxyException | IOException | URISyntaxException e) {
              // Print any exceptions that occur during initialization
              e.printStackTrace();
            }
            return null;
          }
        };

    // Run the task in a separate thread
    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();

    // Set the background image to fill the entire root node
    backgroundimg.setFitWidth(rootNode.getWidth());
    backgroundimg.setFitHeight(rootNode.getHeight());

    // Bind the image view to the root node to ensure proper resizing
    backgroundimg.fitWidthProperty().bind(rootNode.widthProperty());
    backgroundimg.fitHeightProperty().bind(rootNode.heightProperty());
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

  /**
   * Handles the event when the uncle button is clicked.
   *
   * @param event the action event
   * @throws IOException if the root cannot be set
   */
  @FXML
  private void handleUncleClick(ActionEvent event) throws IOException {
    App.setRoot("suspect1room");
  }

  /**
   * Handles the event when the grandma button is clicked.
   *
   * @param event the action event
   * @throws IOException if the root cannot be set
   */
  @FXML
  private void handleGrandmotherClick(ActionEvent event) throws IOException {
    App.setRoot("suspect2room");
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
      menuButton.setStyle(
          "-fx-background-radius: 10 0 0 10; -fx-border-color:  black transparent black black;"
              + " -fx-border-radius: 10 0 0 10; -fx-background-insets: 0;");
    } else {
      menuButton.setStyle(
          "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;"
              + " -fx-background-insets: 0;");
    }

    // Set visibility and management of other buttons based on isMenuVisible
    crimeSceneButton.setVisible(isMenuVisible);
    crimeSceneButton.setManaged(isMenuVisible);

    grandmaButton.setVisible(isMenuVisible);
    grandmaButton.setManaged(isMenuVisible);

    grandsonButton.setVisible(isMenuVisible);
    grandsonButton.setManaged(isMenuVisible);

    uncleButton.setVisible(isMenuVisible);
    uncleButton.setManaged(isMenuVisible);
  }

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    GameStateContext.getInstance().setGuessPressed(true); // Mark as found in the context
    App.setRoot("guessingScene");
    context.handleGuessClick();
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    chatCompletionRequest.addMessage(msg);
    disableSendButton(true); // Disable send button during processing
    try {
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      chatCompletionRequest.addMessage(result.getChatMessage());
      appendChatMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      appendChatMessage(new ChatMessage("system", "Error during GPT call: " + e.getMessage()));
      e.printStackTrace();
      return null;
    } finally {
      disableSendButton(false); // Re-enable send button after processing
    }
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  @FXML
  private void appendChatMessage(ChatMessage msg) {
    suspect3ChatBox.clear();
    suspect3ChatBox.appendText(msg.getContent() + "\n\n");
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSend(MouseEvent event) throws ApiProxyException, IOException {
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    // Capture the ENTER key
    if (event.getCode() == KeyCode.ENTER) {
      try {
        sendMessageCode();
      } catch (ApiProxyException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void sendMessageCode() throws ApiProxyException, IOException {
    String message = userChatBox.getText().trim();
    if (message.isEmpty()) {
      return;
    }
    userChatBox.clear();
    suspect3ChatBox.clear();
    userChatBox.setPromptText("Waiting for response...");
    ChatMessage msg = new ChatMessage("user", message);

    // Run the GPT model in a separate thread to avoid blocking the UI
    Thread thread =
        new Thread(
            () -> {
              try {
                runGpt(msg);
                userChatBox.setPromptText("Ask another question...");
              } catch (ApiProxyException e) {
                e.printStackTrace();
              }
            });
    thread.start(); // Start the GPT thread
  }

  // Method to disable/enable send button
  private void disableSendButton(boolean disable) {
    sendButton.setDisable(disable);
  }

  private static String loadTemplate(URI filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)));
  }

  private void recordVisit() {
    if (GameStateContext.getInstance().getListOfVisitors().isEmpty()
        || !GameStateContext.getInstance().getListOfVisitors().contains("suspect3")) {
      GameStateContext.getInstance().addVisitor("suspect3");
    }
  }

  @FXML
  private void checkGuessButton() {
    if (context.getListOfVisitors().contains("suspect1")
        && context.getListOfVisitors().contains("suspect2")
        && context.getListOfVisitors().contains("suspect3")
        && isatleastoncecluefound) {
      // Enable the guess button
      guessButton.setOpacity(0.8);
      guessButton.setDisable(false);
    } else {
      // Disable the guess button
      guessButton.setOpacity(0.3);
      guessButton.setDisable(true);
    }
  }

  @FXML
  private void onEnterKey(ActionEvent event) throws ApiProxyException, IOException {
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }
}
