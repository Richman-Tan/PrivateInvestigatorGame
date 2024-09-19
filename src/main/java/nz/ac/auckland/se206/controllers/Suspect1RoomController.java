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
  private boolean isatleastoncecluefound =
      context.isGardenToolFound() || context.isPhoneFound() || context.isNoteFound();

  // Instance methods

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

    // Set the background image
    backgroundimg.setFitWidth(rootNode.getWidth());
    backgroundimg.setFitHeight(rootNode.getHeight());
    backgroundimg.fitWidthProperty().bind(rootNode.widthProperty());
    backgroundimg.fitHeightProperty().bind(rootNode.heightProperty());
  }

  private void runInitializationTask() {
    // Create a new task
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() throws IOException, URISyntaxException {
            try {
              // Load the template
              checkGuessButton();
              ApiProxyConfig config = ApiProxyConfig.readConfig();
              chatCompletionRequest =
                  new ChatCompletionRequest(config)
                      .setN(1)
                      .setTemperature(0.2)
                      .setTopP(0.5)
                      .setMaxTokens(100);

              // Load the template
              URL resourceUrl =
                  PromptEngineering.class.getClassLoader().getResource("prompts/uncle.txt");
              String template = loadTemplate(resourceUrl.toURI());
              // Create a new chat message
              ChatMessage systemMessage = new ChatMessage("system", template);
              runGpt(systemMessage);

              // Set the prompt text
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

    // Create a new thread
    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
  }

  @FXML
  private void onRoom() throws IOException {
    App.setRoot("room");
  }

  @FXML
  private void onSend(MouseEvent event) throws ApiProxyException, IOException {
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }

  @FXML
  private void handleGrandsonClick(ActionEvent event) throws IOException {
    App.setRoot("suspect3room");
  }

  @FXML
  private void handleGrandmotherClick(ActionEvent event) throws IOException {
    App.setRoot("suspect2room");
  }

  @FXML
  private void onToggleMenu(ActionEvent event) {
    context.toggleMenuVisibility();
    updateMenuVisibility();
  }

  private void updateMenuVisibility() {
    // Set the menu button style
    boolean isMenuVisible = context.isMenuVisible();
    // Set the menu button style
    menuButton.setStyle(
        isMenuVisible
            ? "-fx-background-radius: 10 0 0 10; -fx-border-color:  black transparent black black;"
                + " -fx-border-radius: 10 0 0 10; -fx-background-insets: 0;"
            : "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;"
                + " -fx-background-insets: 0;");
    setButtonVisibility(isMenuVisible);
  }

  private void setButtonVisibility(boolean isMenuVisible) {
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

  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    // Set the guessPressed to true
    context.setGuessPressed(true);
    App.setRoot("guessingScene");
    context.handleGuessClick();
  }

  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    // Append the user message to the chat box
    chatCompletionRequest.addMessage(msg);
    disableSendButton(true);
    try {
      // Execute the GPT call
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      // Append the chat message to the chat box
      chatCompletionRequest.addMessage(result.getChatMessage());
      // Append the chat message to the chat box
      appendChatMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      // Append the error message to the chat box
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
    // If the user presses the enter key, send the message
    if (event.getCode() == KeyCode.ENTER) {
      try {
        // Send the message
        sendMessageCode();
      } catch (ApiProxyException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void sendMessageCode() throws ApiProxyException, IOException {
    // Get the message from the user
    String message = userChatBox.getText().trim();
    if (message.isEmpty()) {
      return;
    }

    // Clear the chat box
    userChatBox.clear();
    suspect1ChatBox.clear();
    userChatBox.setPromptText("Waiting for response...");

    // Create a new chat message
    ChatMessage msg = new ChatMessage("user", message);

    // Run the GPT call in a separate thread
    Thread thread =
        new Thread(
            () -> {
              try {
                // Run the GPT call
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
    if (context.getListOfVisitors().isEmpty()
        || !context.getListOfVisitors().contains("suspect1")) {
      context.addVisitor("suspect1");
    }
  }

  @FXML
  private void checkGuessButton() {
    // Check if the guess button should be enabled
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
    // Send the message
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }
}
