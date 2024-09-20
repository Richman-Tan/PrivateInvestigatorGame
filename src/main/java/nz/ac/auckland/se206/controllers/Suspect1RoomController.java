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
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.prompts.PromptEngineering;

public class Suspect1RoomController {

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
    setResponsiveBackground(backgroundimg, rootNode);
  }

  private void runInitializationTask() {
    // Create a new task
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() throws IOException, URISyntaxException {
            try {
              // Set up the GPT request
              checkGuessButton();

              // Load the initial prompt
              setupGptRequest();

              //  Load the initial prompt
              loadInitialPrompt();

              // Set the user chat box prompt
              if (firstTime) {
                setUserChatBoxPrompt("Begin interrogating...");
                firstTime = false;
              }

            } catch (ApiProxyException | IOException | URISyntaxException e) {
              appendSystemError(e);
            }
            return null;
          }
        };

    // Run task in a new thread
    new Thread(task).start();
  }

  private void setupGptRequest() throws IOException, ApiProxyException {
    // Set up the GPT request
    ApiProxyConfig config = ApiProxyConfig.readConfig();
    chatCompletionRequest =
        new ChatCompletionRequest(config)
            .setN(1)
            .setTemperature(0.2)
            .setTopP(0.5)
            .setMaxTokens(100);
  }

  private void loadInitialPrompt() throws URISyntaxException, IOException, ApiProxyException {
    // Load the initial prompt
    URL resourceUrl = PromptEngineering.class.getClassLoader().getResource("prompts/uncle.txt");
    String template = loadTemplate(resourceUrl.toURI());

    // Send the initial prompt to GPT
    ChatMessage systemMessage = new ChatMessage("system", template);
    runGpt(systemMessage);
  }

  private void appendSystemError(Exception e) {
    // Append the error message to the chat box
    Platform.runLater(
        () -> appendChatMessage(new ChatMessage("system", "Error: " + e.getMessage())));
    e.printStackTrace();
  }

  @FXML
  private void onRoom() throws IOException {
    App.setRoot("room");
  }

  @FXML
  private void onSend(MouseEvent event) throws ApiProxyException, IOException {
    handleSendMessage();
  }

  private void handleSendMessage() throws ApiProxyException, IOException {
    // Send the message to GPT
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
    // Update the menu visibility
    boolean isMenuVisible = context.isMenuVisible();
    updateMenuButtonStyle(isMenuVisible);
    setButtonVisibility(isMenuVisible);
  }

  private void updateMenuButtonStyle(boolean isMenuVisible) {
    // Update the menu button style
    menuButton.setStyle(
        isMenuVisible
            ? "-fx-background-radius: 10 0 0 10; -fx-border-color: black transparent black black;"
            : "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;");
  }

  private void setButtonVisibility(boolean isVisible) {
    // Set the button visibility
    setVisibleAndManaged(crimeSceneButton, isVisible);
    setVisibleAndManaged(grandmaButton, isVisible);
    setVisibleAndManaged(grandsonButton, isVisible);
    setVisibleAndManaged(uncleButton, isVisible);
  }

  private void setVisibleAndManaged(Button button, boolean isVisible) {
    // Set the button visibility and managed state
    button.setVisible(isVisible);
    button.setManaged(isVisible);
  }

  @FXML
  private void onGuessClick(ActionEvent event) throws IOException {
    context.setGuessPressed(true);
    App.setRoot("guessingScene");
    context.onGuessClick();
  }

  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    chatCompletionRequest.addMessage(msg);
    disableSendButton(true);

    try {
      ChatCompletionResult result = chatCompletionRequest.execute();
      ChatMessage response = result.getChoices().iterator().next().getChatMessage();

      chatCompletionRequest.addMessage(response);
      appendChatMessage(response);

      return response;
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
    // Check if the user pressed the enter key
    if (event.getCode() == KeyCode.ENTER) {
      try {
        // Handle the send message event
        handleSendMessage();
      } catch (ApiProxyException | IOException e) {
        appendSystemError(e);
      }
    }
  }

  private void sendMessageCode() throws ApiProxyException, IOException {
    // Get the message from the user
    String message = userChatBox.getText().trim();
    // Check if the message is empty
    if (message.isEmpty()) return;

    // Clear the user input
    clearUserInput();

    // Send the message to GPT
    ChatMessage msg = new ChatMessage("user", message);
    Task<Void> task = createGptTask(msg);

    // Run the task in a new thread
    new Thread(task).start();
  }

  private Task<Void> createGptTask(ChatMessage msg) {
    // Create a new task
    return new Task<>() {
      @Override
      protected Void call() {
        try {
          // Run the GPT model
          runGpt(msg);
          // Set the user chat box prompt
          setUserChatBoxPrompt("Ask another question...");
        } catch (ApiProxyException e) {
          appendSystemError(e);
        }
        return null;
      }
    };
  }

  private void clearUserInput() {
    // Clear the user input
    userChatBox.clear();
    suspect1ChatBox.clear();
    userChatBox.setPromptText("Waiting for response...");
  }

  private void disableSendButton(boolean disable) {
    sendButton.setDisable(disable);
  }

  private void recordVisit() {
    // Add the visitor to the list of visitors
    if (!context.getListOfVisitors().contains("suspect1")) {
      context.addVisitor("suspect1");
    }
  }

  @FXML
  private void checkGuessButton() {
    // Check if the user can guess
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
    handleSendMessage();
  }

  private void setUserChatBoxPrompt(String text) {
    Platform.runLater(() -> userChatBox.setPromptText(text));
  }

  private void setResponsiveBackground(ImageView imageView, AnchorPane pane) {
    // Set the image view to be responsive
    imageView.fitWidthProperty().bind(pane.widthProperty());
    imageView.fitHeightProperty().bind(pane.heightProperty());
  }
}
