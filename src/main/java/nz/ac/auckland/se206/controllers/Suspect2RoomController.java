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
    // Initialize chat completion request and menu visibility
    updateMenuVisibility();
    bindTimerToLabel();
    initializeGptModel();
    setResponsiveBackground(backgroundimg, rootNode);
  }

  private void initializeGptModel() {
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() throws IOException, URISyntaxException {
            try {
              setupGptRequest();
              loadGptPrompt("prompts/grandma.txt");

              if (firstTime) {
                setUserChatBoxPrompt("Begin interrogating...");
                firstTime = false;
              }
            } catch (ApiProxyException | IOException | URISyntaxException e) {
              appendChatMessage(new ChatMessage("system", "Error: " + e.getMessage()));
            }
            return null;
          }
        };

    new Thread(task).start();
  }

  private void setupGptRequest() throws IOException, ApiProxyException {
    ApiProxyConfig config = ApiProxyConfig.readConfig();
    chatCompletionRequest =
        new ChatCompletionRequest(config)
            .setN(1)
            .setTemperature(0.2)
            .setTopP(0.5)
            .setMaxTokens(100);
  }

  private void loadGptPrompt(String resourcePath)
      throws URISyntaxException, IOException, ApiProxyException {
    URL resourceUrl = PromptEngineering.class.getClassLoader().getResource(resourcePath);
    String template = loadTemplate(resourceUrl.toURI());
    ChatMessage systemMessage = new ChatMessage("system", template);
    runGpt(systemMessage);
  }

  private void bindTimerToLabel() {
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());
  }

  private void setUserChatBoxPrompt(String text) {
    Platform.runLater(() -> userChatBox.setPromptText(text));
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
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }

  @FXML
  private void onUncleButtonClick(ActionEvent event) throws IOException {
    App.setRoot("suspect1room");
  }

  @FXML
  private void onGrandsonClick(ActionEvent event) throws IOException {
    App.setRoot("suspect3room");
  }

  @FXML
  private void onToggleMenu(ActionEvent event) {
    context.toggleMenuVisibility();
    updateMenuVisibility();
  }

  private void updateMenuVisibility() {
    boolean isMenuVisible = context.isMenuVisible();
    updateMenuButtonStyle(isMenuVisible);
    setButtonVisibility(isMenuVisible);
  }

  private void updateMenuButtonStyle(boolean isMenuVisible) {
    menuButton.setStyle(
        isMenuVisible
            ? "-fx-background-radius: 10 0 0 10; -fx-border-color: black transparent black black;"
            : "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;");
  }

  private void setButtonVisibility(boolean isVisible) {
    setVisibleAndManaged(crimeSceneButton, isVisible);
    setVisibleAndManaged(grandmaButton, isVisible);
    setVisibleAndManaged(grandsonButton, isVisible);
    setVisibleAndManaged(uncleButton, isVisible);
  }

  private void setVisibleAndManaged(Button button, boolean isVisible) {
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
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      appendChatMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (ApiProxyException e) {
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

  @FXML
  public void onKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      try {
        handleSendMessage();
      } catch (ApiProxyException | IOException e) {
        appendChatMessage(new ChatMessage("system", "Error: " + e.getMessage()));
      }
    }
  }

  private void sendMessageCode() throws ApiProxyException, IOException {
    String message = userChatBox.getText().trim();
    if (message.isEmpty()) return;

    clearUserInput();

    ChatMessage msg = new ChatMessage("user", message);
    Task<Void> task = createGptTask(msg);

    new Thread(task).start();
  }

  private Task<Void> createGptTask(ChatMessage msg) {
    return new Task<>() {
      @Override
      protected Void call() {
        try {
          runGpt(msg);
          setUserChatBoxPrompt("Ask another question...");
        } catch (ApiProxyException e) {
          appendChatMessage(new ChatMessage("system", "Error: " + e.getMessage()));
        }
        return null;
      }
    };
  }

  private void clearUserInput() {
    userChatBox.clear();
    suspect2ChatBox.clear();
    userChatBox.setPromptText("Waiting for response...");
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
    boolean canGuess =
        context.getListOfVisitors().contains("suspect1")
            && context.getListOfVisitors().contains("suspect2")
            && context.getListOfVisitors().contains("suspect3")
            && isAtLeastOneClueFound;

    guessButton.setOpacity(canGuess ? 0.8 : 0.3);
    guessButton.setDisable(!canGuess);
  }

  @FXML
  private void onEnterKey(ActionEvent event) throws ApiProxyException, IOException {
    handleSendMessage();
  }

  private void setResponsiveBackground(ImageView imageView, AnchorPane pane) {
    imageView.fitWidthProperty().bind(pane.widthProperty());
    imageView.fitHeightProperty().bind(pane.heightProperty());
  }
}
