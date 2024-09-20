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

public class Suspect1RoomController {

  // Static fields
  private static ChatCompletionRequest chatCompletionRequest;

  // Static Methods
  private static String loadTemplate(URI filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)));
  }

  // Instance fields
  @FXML private Button crimeSceneButton;
  @FXML private Button grandmaButton;
  @FXML private Button grandsonButton;
  @FXML private Button menuButton;
  @FXML private Button uncleButton;
  @FXML private TextArea suspect1ChatBox;
  @FXML private TextField userChatBox;
  @FXML private Circle sendButton;
  @FXML private Button guessButton;
  @FXML private AnchorPane rootNode;
  @FXML private ImageView backgroundimg;
  @FXML private Label lbltimer;

  private GameStateContext context = GameStateContext.getInstance();
  private boolean firstTime = true;
  private TimerModel countdownTimer;
  private boolean isAtLeastOneClueFound =
      context.isGardenToolFound() || context.isPhoneFound() || context.isNoteFound();

  /** Initializes the suspect 1 room view. */
  @FXML
  public void initialize() {
    // Set the menu visibility
    updateMenuVisibility();

    // Set the timer
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());

    // Run the initialization task
    runInitializationTask();

    // Set the background image to be responsive
    backgroundimg.fitWidthProperty().bind(rootNode.widthProperty());
    backgroundimg.fitHeightProperty().bind(rootNode.heightProperty());
  }

  private void runInitializationTask() {
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() throws IOException, URISyntaxException {
            try {
              checkGuessButton();

              // Load the GPT model configuration
              ApiProxyConfig config = ApiProxyConfig.readConfig();
              chatCompletionRequest =
                  new ChatCompletionRequest(config)
                      .setN(1)
                      .setTemperature(0.2)
                      .setTopP(0.5)
                      .setMaxTokens(100);

              // Load the template for uncle suspect
              URL resourceUrl =
                  PromptEngineering.class.getClassLoader().getResource("prompts/uncle.txt");
              String template = loadTemplate(resourceUrl.toURI());

              // Create and send a system message to the GPT model
              ChatMessage systemMessage = new ChatMessage("system", template);
              runGpt(systemMessage);

              // Initialize prompt text for user input
              if (firstTime) {
                Platform.runLater(() -> userChatBox.setPromptText("Begin interrogating..."));
                firstTime = false;
              }

            } catch (ApiProxyException | IOException | URISyntaxException e) {
              // Append error message to chat
              Platform.runLater(
                  () -> appendChatMessage(new ChatMessage("system", "Error: " + e.getMessage())));
              e.printStackTrace();
            }
            return null;
          }
        };

    // Run task in a new thread
    new Thread(task).start();
  }

  @FXML
  private void onRoom() throws IOException {
    App.setRoot("room");
  }

  @FXML
  private void onSend(MouseEvent event) throws ApiProxyException, IOException {
    // Send the message and record the visit
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }

  @FXML
  private void onGrandsonClick(ActionEvent event) throws IOException {
    App.setRoot("suspect3room");
  }

  @FXML
  private void onGrandmotherClick(ActionEvent event) throws IOException {
    App.setRoot("suspect2room");
  }

  @FXML
  private void onToggleMenu(ActionEvent event) {
    // Toggle the menu visibility
    context.toggleMenuVisibility();
    updateMenuVisibility();
  }

  private void updateMenuVisibility() {
    boolean isMenuVisible = context.isMenuVisible();

    // Update the menu button style based on visibility
    menuButton.setStyle(
        isMenuVisible
            ? "-fx-background-radius: 10 0 0 10; -fx-border-color: black transparent black black;"
            : "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;");

    // Update button visibility
    setButtonVisibility(isMenuVisible);
  }

  private void setButtonVisibility(boolean isVisible) {
    // Set the button visibility
    crimeSceneButton.setVisible(isVisible);
    crimeSceneButton.setManaged(isVisible);

    // Set the button visibility
    grandmaButton.setVisible(isVisible);
    grandmaButton.setManaged(isVisible);

    // Set the button visibility
    grandsonButton.setVisible(isVisible);
    grandsonButton.setManaged(isVisible);

    // Set the button visibility
    uncleButton.setVisible(isVisible);
    uncleButton.setManaged(isVisible);
  }

  @FXML
  private void onGuessClick(ActionEvent event) throws IOException {
    // Set the guess pressed flag and navigate to the guessing scene
    context.setGuessPressed(true);
    App.setRoot("guessingScene");
    context.onGuessClick();
  }

  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    // Send message to GPT model
    chatCompletionRequest.addMessage(msg);
    disableSendButton(true);
    try {
      // Execute the request and get the result
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();

      // Append result to chat and update request
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

  @FXML
  private void appendChatMessage(ChatMessage msg) {
    // Append the chat message to the chat box
    suspect1ChatBox.clear();
    suspect1ChatBox.appendText(msg.getContent() + "\n\n");
  }

  @FXML
  public void onKeyPressed(KeyEvent event) {
    // Send message when enter key is pressed
    if (event.getCode() == KeyCode.ENTER) {
      try {
        sendMessageCode();
      } catch (ApiProxyException | IOException e) {
        appendChatMessage(new ChatMessage("system", "Error: " + e.getMessage()));
        e.printStackTrace();
      }
    }
  }

  private void sendMessageCode() throws ApiProxyException, IOException {
    // Get user message
    String message = userChatBox.getText().trim();
    if (message.isEmpty()) {
      return;
    }

    // Clear user input and wait for response
    userChatBox.clear();
    suspect1ChatBox.clear();
    userChatBox.setPromptText("Waiting for response...");

    // Create a new chat message
    ChatMessage msg = new ChatMessage("user", message);

    // Run the GPT model in a new thread
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() {
            try {
              runGpt(msg);
              // Update the prompt text
              Platform.runLater(() -> userChatBox.setPromptText("Ask another question..."));
            } catch (ApiProxyException e) {
              appendChatMessage(new ChatMessage("system", "Error: " + e.getMessage()));
              e.printStackTrace();
            }
            return null;
          }
        };

    new Thread(task).start();
  }

  private void disableSendButton(boolean disable) {
    sendButton.setDisable(disable);
  }

  private void recordVisit() {
    if (!context.getListOfVisitors().contains("suspect1")) {
      context.addVisitor("suspect1");
    }
  }

  @FXML
  private void checkGuessButton() {
    // Check if the guess button can be enabled
    boolean canGuess =
        context.getListOfVisitors().contains("suspect1")
            && context.getListOfVisitors().contains("suspect2")
            && context.getListOfVisitors().contains("suspect3")
            && isAtLeastOneClueFound;

    // Set the opacity and disable state of the guess button
    guessButton.setOpacity(canGuess ? 0.8 : 0.3);
    guessButton.setDisable(!canGuess);
  }

  @FXML
  private void onEnterKey(ActionEvent event) throws ApiProxyException, IOException {
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }
}
