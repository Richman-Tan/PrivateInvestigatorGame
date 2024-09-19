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

  // Constructors
  public Suspect1RoomController() {
    // Default constructor if needed
  }

  // Instance methods

  /** Initializes the suspect 1 room view. */
  @FXML
  public void initialize() {
    updateMenuVisibility();
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());
    runInitializationTask();
    backgroundimg.setFitWidth(rootNode.getWidth());
    backgroundimg.setFitHeight(rootNode.getHeight());
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
              ApiProxyConfig config = ApiProxyConfig.readConfig();
              chatCompletionRequest =
                  new ChatCompletionRequest(config)
                      .setN(1)
                      .setTemperature(0.2)
                      .setTopP(0.5)
                      .setMaxTokens(100);

              URL resourceUrl =
                  PromptEngineering.class.getClassLoader().getResource("prompts/uncle.txt");
              String template = loadTemplate(resourceUrl.toURI());
              ChatMessage systemMessage = new ChatMessage("system", template);
              runGpt(systemMessage);

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
    boolean isMenuVisible = context.isMenuVisible();
    menuButton.setStyle(
        isMenuVisible
            ? "-fx-background-radius: 10 0 0 10; -fx-border-color:  black transparent black black;"
                + " -fx-border-radius: 10 0 0 10; -fx-background-insets: 0;"
            : "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;"
                + " -fx-background-insets: 0;");
    setButtonVisibility(isMenuVisible);
  }

  private void setButtonVisibility(boolean isMenuVisible) {
    crimeSceneButton.setVisible(isMenuVisible);
    crimeSceneButton.setManaged(isMenuVisible);
    grandmaButton.setVisible(isMenuVisible);
    grandmaButton.setManaged(isMenuVisible);
    grandsonButton.setVisible(isMenuVisible);
    grandsonButton.setManaged(isMenuVisible);
    uncleButton.setVisible(isMenuVisible);
    uncleButton.setManaged(isMenuVisible);
  }

  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    context.setGuessPressed(true);
    App.setRoot("guessingScene");
    context.handleGuessClick();
  }

  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    chatCompletionRequest.addMessage(msg);
    disableSendButton(true);
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
      disableSendButton(false);
    }
  }

  @FXML
  private void appendChatMessage(ChatMessage msg) {
    suspect1ChatBox.clear();
    suspect1ChatBox.appendText(msg.getContent() + "\n\n");
  }

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
    String message = userChatBox.getText().trim();
    if (message.isEmpty()) {
      return;
    }
    userChatBox.clear();
    suspect1ChatBox.clear();
    userChatBox.setPromptText("Waiting for response...");
    ChatMessage msg = new ChatMessage("user", message);

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
    if (context.getListOfVisitors().isEmpty()
        || !context.getListOfVisitors().contains("suspect1")) {
      context.addVisitor("suspect1");
    }
  }

  @FXML
  private void checkGuessButton() {
    if (context.getListOfVisitors().contains("suspect1")
        && context.getListOfVisitors().contains("suspect2")
        && context.getListOfVisitors().contains("suspect3")
        && isatleastoncecluefound) {
      guessButton.setOpacity(0.8);
      guessButton.setDisable(false);
    } else {
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
