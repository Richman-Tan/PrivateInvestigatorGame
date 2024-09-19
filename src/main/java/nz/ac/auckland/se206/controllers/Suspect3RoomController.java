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
    updateMenuVisibility();
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());

    Task<Void> task =
        new Task<Void>() {
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
                  PromptEngineering.class.getClassLoader().getResource("prompts/grandson.txt");
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
    boolean isMenuVisible = context.isMenuVisible();

    if (isMenuVisible) {
      menuButton.setStyle(
          "-fx-background-radius: 10 0 0 10; -fx-border-color: black transparent black black;"
              + " -fx-border-radius: 10 0 0 10; -fx-background-insets: 0;");
    } else {
      menuButton.setStyle(
          "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;"
              + " -fx-background-insets: 0;");
    }

    crimeSceneButton.setVisible(isMenuVisible);
    crimeSceneButton.setManaged(isMenuVisible);
    grandmaButton.setVisible(isMenuVisible);
    grandmaButton.setManaged(isMenuVisible);
    grandsonButton.setVisible(isMenuVisible);
    grandsonButton.setManaged(isMenuVisible);
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

  /** Appends a chat message to the chat text area. */
  @FXML
  private void appendChatMessage(ChatMessage msg) {
    suspect3ChatBox.clear();
    suspect3ChatBox.appendText(msg.getContent() + "\n\n");
  }

  /** Sends a message to the GPT model. */
  @FXML
  private void onSend(MouseEvent event) throws ApiProxyException, IOException {
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }

  /** Handles the key pressed event. */
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
    suspect3ChatBox.clear();
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
        && isAtLeastOnceClueFound) {
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
