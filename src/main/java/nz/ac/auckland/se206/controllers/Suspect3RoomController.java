package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.application.Platform;
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

public class Suspect3RoomController {

  // Static fields
  private static ChatCompletionRequest chatCompletionRequest;

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

  /** Initializes the suspect 3 room view. */
  @FXML
  public void initialize() {
    updateMenuVisibility();

    // Bind timer to label
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());

    // Load the chat messages
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() throws IOException, URISyntaxException {
            try {

              // Record visit
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

              // Run GPT with the message
              ChatMessage systemMessage = new ChatMessage("system", template);
              runGpt(systemMessage);

              // Set the prompt text
              if (firstTime) {
                Platform.runLater(() -> userChatBox.setPromptText("Begin interrogating..."));
                firstTime = false;
              }
            } catch (ApiProxyException | IOException | URISyntaxException e) {
              e.printStackTrace();
            }
            return null;
          }
        };

    // Run task on a new thread
    new Thread(task).start();

    // Set the responsive background image
    backgroundimg.fitWidthProperty().bind(rootNode.widthProperty());
    backgroundimg.fitHeightProperty().bind(rootNode.heightProperty());
  }

  /** Handles the guess button click event. */
  @FXML
  private void onGuessClick(ActionEvent event) throws IOException {
    // Set the guess button pressed
    GameStateContext.getInstance().setGuessPressed(true);
    App.setRoot("guessingScene");
    context.onGuessClick();
  }

  /** Toggles the visibility of the menu and other buttons when the menu button is clicked. */
  @FXML
  private void onToggleMenu(ActionEvent event) {
    // Toggle the menu visibility
    context.toggleMenuVisibility();
    updateMenuVisibility();
  }

  /** Updates the visibility of the menu and other buttons. */
  private void updateMenuVisibility() {

    // Get the menu visibility
    boolean isMenuVisible = context.isMenuVisible();

    // Update the menu button style based on visibility
    String style =
        isMenuVisible
            ? "-fx-background-radius: 10 0 0 10; -fx-border-color: black transparent black black;"
                + " -fx-background-insets: 0;"
            : "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;"
                + " -fx-background-insets: 0;";
    menuButton.setStyle(style);

    // Update visibility of all buttons
    updateButtonVisibility(crimeSceneButton, isMenuVisible);
    updateButtonVisibility(grandmaButton, isMenuVisible);
    updateButtonVisibility(grandsonButton, isMenuVisible);
    updateButtonVisibility(uncleButton, isMenuVisible);
  }

  /** Helper method to update the visibility and management of buttons. */
  private void updateButtonVisibility(Button button, boolean isVisible) {
    button.setVisible(isVisible);
    button.setManaged(isVisible);
  }

  /** Runs the GPT model with a given chat message. */
  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    // Add the message to the chat completion request
    chatCompletionRequest.addMessage(msg);
    disableSendButton(true);

    try {
      // Execute the chat completion request
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();

      // Add the chat message to the chat completion request
      chatCompletionRequest.addMessage(result.getChatMessage());
      appendChatMessage(result.getChatMessage());

      // Return the chat message
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
    Platform.runLater(
        () -> {
          suspect3ChatBox.clear();
          suspect3ChatBox.appendText(msg.getContent() + "\n\n");
        });
  }

  /** Sends a message to the GPT model. */
  @FXML
  private void onSend(MouseEvent event) throws ApiProxyException, IOException {
    // Send the message to the GPT model
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }

  /** Handles the key pressed event (for enter key). */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      try {
        sendMessageCode();
      } catch (ApiProxyException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void sendMessageCode() throws ApiProxyException, IOException {
    // Get the message from the user
    String message = userChatBox.getText().trim();
    if (message.isEmpty()) return;

    // Clear the chat box and set the prompt text
    userChatBox.clear();
    suspect3ChatBox.clear();
    userChatBox.setPromptText("Waiting for response...");

    // Send the message to the GPT model
    ChatMessage msg = new ChatMessage("user", message);
    new Thread(
            () -> {
              try {
                // Run the GPT model
                runGpt(msg);
                Platform.runLater(() -> userChatBox.setPromptText("Ask another question..."));
              } catch (ApiProxyException e) {
                e.printStackTrace();
              }
            })
        .start();
  }

  private void disableSendButton(boolean disable) {
    sendButton.setDisable(disable);
  }

  /** Handles the event when the uncle button is clicked. */
  @FXML
  private void onUncleButtonClick(ActionEvent event) throws IOException {
    // Set the root to the uncle room
    App.setRoot("suspect1room");
  }

  /** Handles the event when the grandma button is clicked. */
  @FXML
  private void onGrandmotherClick(ActionEvent event) throws IOException {
    App.setRoot("suspect2room");
  }

  private void recordVisit() {
    if (!GameStateContext.getInstance().getListOfVisitors().contains("suspect3")) {
      GameStateContext.getInstance().addVisitor("suspect3");
    }
  }

  /** Checks if the guess button should be enabled. */
  @FXML
  private void checkGuessButton() {
    // Check if the guess button should be enabled
    boolean canGuess =
        context.getListOfVisitors().contains("suspect1")
            && context.getListOfVisitors().contains("suspect2")
            && context.getListOfVisitors().contains("suspect3")
            && isAtLeastOnceClueFound;

    // Set the guess button opacity and disable state
    guessButton.setOpacity(canGuess ? 0.8 : 0.3);
    guessButton.setDisable(!canGuess);
  }

  /** Handles enter key event for sending messages. */
  @FXML
  private void onEnterKey(ActionEvent event) throws ApiProxyException, IOException {
    // Send the message to the GPT model
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }

  /** Handles the event when the crime scene button is clicked. */
  @FXML
  private void onRoom() throws IOException {
    App.setRoot("room");
  }
}
