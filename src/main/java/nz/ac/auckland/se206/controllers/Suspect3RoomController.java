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

  // Static fields
  private static ChatCompletionRequest chatCompletionRequest;

  // Static methods
  private static String loadTemplate(URI filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)));
  }

  // Instance fields
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

  private GameStateContext context = GameStateContext.getInstance();
  private boolean firstTime = true;
  private TimerModel countdownTimer;

  private boolean isAtLeastOnceClueFound =
      context.isGardenToolFound() || context.isPhoneFound() || context.isNoteFound();

  // Constructors
  // (no constructors defined, using default constructor)

  // Instance methods

  /** Initializes the suspect 3 room view. */
  @FXML
  public void initialize() {
    // Set the visibility of the menu and other buttons
    updateMenuVisibility();

    // Bind the timer to the label
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());

    // Create a new task to run the GPT model
    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() throws IOException, URISyntaxException {
            try {
              // Run the initialization task
              checkGuessButton();

              // Load the GPT model configuration
              ApiProxyConfig config = ApiProxyConfig.readConfig();
              chatCompletionRequest =
                  new ChatCompletionRequest(config)
                      .setN(1)
                      .setTemperature(0.2)
                      .setTopP(0.5)
                      .setMaxTokens(100);

                      // Load the template for the chat message
              URL resourceUrl =
                  PromptEngineering.class.getClassLoader().getResource("prompts/grandson.txt");
              String template = loadTemplate(resourceUrl.toURI());

              // Run the GPT model with the chat message
              ChatMessage systemMessage = new ChatMessage("system", template);
              runGpt(systemMessage);

              // Set the prompt text of the user chat box
              if (firstTime) {
                userChatBox.setPromptText("Begin interrogating...");
                firstTime = false;
              }

            } catch (ApiProxyException | IOException | URISyntaxException e) {
              e.printStackTrace();
            }
            return null;
          }
        };

        // Create a new thread to run the task
    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();

    // Set the fit width and height of the background image
    backgroundimg.setFitWidth(rootNode.getWidth());
    backgroundimg.setFitHeight(rootNode.getHeight());
    backgroundimg.fitWidthProperty().bind(rootNode.widthProperty());
    backgroundimg.fitHeightProperty().bind(rootNode.heightProperty());
  }

  /** Handles the event when the crime scene button is clicked. */
  @FXML
  private void onRoom() throws IOException {
    App.setRoot("room");
  }

  /** Handles the event when the uncle button is clicked. */
  @FXML
  private void handleUncleClick(ActionEvent event) throws IOException {
    App.setRoot("suspect1room");
  }

  /** Handles the event when the grandma button is clicked. */
  @FXML
  private void handleGrandmotherClick(ActionEvent event) throws IOException {
    App.setRoot("suspect2room");
  }

  /** Toggles the visibility of the menu and other buttons when the menu button is clicked. */
  @FXML
  private void onToggleMenu(ActionEvent event) {
    context.toggleMenuVisibility();
    updateMenuVisibility();
  }

  /** Updates the visibility of the menu and other buttons. */
  private void updateMenuVisibility() {
    // Get the visibility of the menu
    boolean isMenuVisible = context.isMenuVisible();

    // Set the style of the menu button
    if (isMenuVisible) {
      menuButton.setStyle(
          "-fx-background-radius: 10 0 0 10; -fx-border-color: black transparent black black;"
              + " -fx-border-radius: 10 0 0 10; -fx-background-insets: 0;");
    } else {
      menuButton.setStyle(
          "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;"
              + " -fx-background-insets: 0;");
    }

    // Set the visibility of the buttons
    crimeSceneButton.setVisible(isMenuVisible);
    crimeSceneButton.setManaged(isMenuVisible);

    // Set the visibility of the buttons
    grandmaButton.setVisible(isMenuVisible);
    grandmaButton.setManaged(isMenuVisible);

    // Set the visibility of the buttons
    grandsonButton.setVisible(isMenuVisible);
    grandsonButton.setManaged(isMenuVisible);

    // Set the visibility of the buttons
    uncleButton.setVisible(isMenuVisible);
    uncleButton.setManaged(isMenuVisible);
  }

  /** Handles the guess button click event. */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    GameStateContext.getInstance().setGuessPressed(true);
    App.setRoot("guessingScene");
    context.handleGuessClick();
  }

  /** Runs the GPT model with a given chat message. */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {

    // Send the message to the GPT model
    chatCompletionRequest.addMessage(msg);

    // Disable the send button
    disableSendButton(true);
    try {

      // Execute the chat completion request
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();

      // Add the chat message to the chat completion request
      chatCompletionRequest.addMessage(result.getChatMessage());

      // Append the chat message to the chat text area
      appendChatMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      appendChatMessage(new ChatMessage("system", "Error during GPT call: " + e.getMessage()));
      e.printStackTrace();
      return null;
    } finally {
      disableSendButton(false);
    }
  }

  /** Appends a chat message to the chat text area. */
  @FXML
  private void appendChatMessage(ChatMessage msg) {
    // Clear the chat text area
    suspect3ChatBox.clear();
    suspect3ChatBox.appendText(msg.getContent() + "\n\n");
  }

  /** Sends a message to the GPT model. */
  @FXML
  private void onSend(MouseEvent event) throws ApiProxyException, IOException {
    // Send the message to the GPT model
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }

  /** Handles the key pressed event. */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    // If the enter key is pressed, send the message to the GPT model
    if (event.getCode() == KeyCode.ENTER) {
      try {
        sendMessageCode();
      } catch (ApiProxyException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void sendMessageCode() throws ApiProxyException, IOException {
    // Get the message from the user chat box
    String message = userChatBox.getText().trim();
    if (message.isEmpty()) {
      return;
    }

    // Clear the user chat box and the suspect 3 chat box
    userChatBox.clear();
    suspect3ChatBox.clear();

    // Set the prompt text of the user chat box
    userChatBox.setPromptText("Waiting for response...");
    ChatMessage msg = new ChatMessage("user", message);

    // Create a new thread to run the GPT model
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
    thread.start();
  }

  private void disableSendButton(boolean disable) {
    sendButton.setDisable(disable);
  }

  private void recordVisit() {
    // Add the visitor to the list of visitors
    if (GameStateContext.getInstance().getListOfVisitors().isEmpty()
        || !GameStateContext.getInstance().getListOfVisitors().contains("suspect3")) {
      GameStateContext.getInstance().addVisitor("suspect3");
    }
  }

  @FXML
  private void checkGuessButton() {
    // Check if the guess button should be enabled
    if (context.getListOfVisitors().contains("suspect1")
        && context.getListOfVisitors().contains("suspect2")
        && context.getListOfVisitors().contains("suspect3")
        && isAtLeastOnceClueFound) {
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
    // Send the message to the GPT model
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }
}
