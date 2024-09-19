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

public class Suspect2RoomController {

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
  @FXML private TextArea suspect2ChatBox;
  @FXML private Circle sendButton;
  @FXML private Button guessButton;
  @FXML private AnchorPane rootNode;
  @FXML private ImageView backgroundimg;
  @FXML private Label lbltimer;

  private ChatCompletionRequest chatCompletionRequest;
  private GameStateContext context = GameStateContext.getInstance();
  private boolean firstTime = true;
  private TimerModel countdownTimer;

  private boolean isAtLeastOneClueFound =
      context.isGardenToolFound() || context.isPhoneFound() || context.isNoteFound();

  /** Initializes the suspect 2 room view. */
  @FXML
  public void initialize() {
    // Set up the chat completion request and menu visibility
    updateMenuVisibility();

    // Bind the timer to the label
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());

    // Run initialization task for GPT model
    initializeGptModel();

    // Set the background image to be responsive
    backgroundimg.fitWidthProperty().bind(rootNode.widthProperty());
    backgroundimg.fitHeightProperty().bind(rootNode.heightProperty());
  }

  private void initializeGptModel() {
    // Create a task to initialize the GPT model
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() throws IOException, URISyntaxException {
            try {
              // Prepare GPT model configuration
              checkGuessButton();

              // Load the GPT model configuration
              ApiProxyConfig config = ApiProxyConfig.readConfig();
              chatCompletionRequest =
                  new ChatCompletionRequest(config)
                      .setN(1)
                      .setTemperature(0.2)
                      .setTopP(0.5)
                      .setMaxTokens(100);

              // Load and run the GPT model
              URL resourceUrl =
                  PromptEngineering.class.getClassLoader().getResource("prompts/grandma.txt");
              String template = loadTemplate(resourceUrl.toURI());

              // Create and send a system message to the GPT model
              ChatMessage systemMessage = new ChatMessage("system", template);
              runGpt(systemMessage);

              // Set the user chat box prompt on first run
              if (firstTime) {
                userChatBox.setPromptText("Begin interrogating...");
                firstTime = false;
              }
            } catch (ApiProxyException | IOException | URISyntaxException e) {
              appendChatMessage(new ChatMessage("system", "Error: " + e.getMessage()));
            }
            return null;
          }
        };

    // Run the task in a separate thread
    new Thread(task).start();
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
  private void handleUncleClick(ActionEvent event) throws IOException {
    App.setRoot("suspect1room");
  }

  @FXML
  private void handleGrandsonClick(ActionEvent event) throws IOException {
    App.setRoot("suspect3room");
  }

  @FXML
  private void onToggleMenu(ActionEvent event) {
    context.toggleMenuVisibility();
    updateMenuVisibility();
  }

  private void updateMenuVisibility() {

    // Update the menu button style
    boolean isMenuVisible = context.isMenuVisible();

    menuButton.setStyle(
        isMenuVisible
            ? "-fx-background-radius: 10 0 0 10; -fx-border-color: black transparent black black;"
            : "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;");

    // Update the menu button style based on visibility
    setButtonVisibility(isMenuVisible);
  }

  private void setButtonVisibility(boolean isVisible) {
    // Set button visibility based on menu visibility
    crimeSceneButton.setVisible(isVisible);
    crimeSceneButton.setManaged(isVisible);

    // Set button visibility based on menu visibility
    grandmaButton.setVisible(isVisible);
    grandmaButton.setManaged(isVisible);

    // Set button visibility based on menu visibility
    grandsonButton.setVisible(isVisible);
    grandsonButton.setManaged(isVisible);

    // Set button visibility based on menu visibility
    uncleButton.setVisible(isVisible);
    uncleButton.setManaged(isVisible);
  }

  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    // Set the guess button opacity
    context.setGuessPressed(true);
    App.setRoot("guessingScene");
    context.handleGuessClick();
  }

  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    // Send a message to the GPT model
    chatCompletionRequest.addMessage(msg);
    disableSendButton(true);

    // Execute the GPT model
    try {
      //  Execute the GPT model
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      // Append the chat message
      appendChatMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      // Handle GPT model error
      appendChatMessage(new ChatMessage("system", "Error during GPT call: " + e.getMessage()));
      return null;
    } finally {
      disableSendButton(false);
    }
  }

  @FXML
  private void appendChatMessage(ChatMessage msg) {
    suspect2ChatBox.clear();
    suspect2ChatBox.appendText(msg.getContent() + "\n\n");
  }

  /**
   * Handle key press event
   *
   * @param event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    // Handle key press event
    if (event.getCode() == KeyCode.ENTER) {
      try {
        // Send message when enter key is pressed
        sendMessageCode();
      } catch (ApiProxyException | IOException e) {
        appendChatMessage(new ChatMessage("system", "Error: " + e.getMessage()));
      }
    }
  }

  /**
   * Send message to GPT model
   *
   * @throws ApiProxyException
   * @throws IOException
   */
  private void sendMessageCode() throws ApiProxyException, IOException {
    // Send message to GPT model
    String message = userChatBox.getText().trim();

    // Check if message is empty
    if (message.isEmpty()) {
      return;
    }

    // Clear user input and wait for response
    userChatBox.clear();
    suspect2ChatBox.clear();
    userChatBox.setPromptText("Waiting for response...");

    // Create a new chat message
    ChatMessage msg = new ChatMessage("user", message);

    // Run GPT model in a separate thread
    new Thread(
            () -> {
              try {
                runGpt(msg);
                // Update the prompt text
                userChatBox.setPromptText("Ask another question...");
              } catch (ApiProxyException e) {
                appendChatMessage(new ChatMessage("system", "Error: " + e.getMessage()));
              }
            })
        .start();
  }

  private void disableSendButton(boolean disable) {
    sendButton.setDisable(disable);
  }

  private void recordVisit() {
    if (!context.getListOfVisitors().contains("suspect2")) {
      context.addVisitor("suspect2");
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

    // Set the guess button opacity
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
