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

public class Suspect3RoomController {

  @FXML private Button btnMenu;
  @FXML private Button btnCrimeScene;
  @FXML private Button btnGrandma;
  @FXML private Button btnGrandson;
  @FXML private Button btnUncle;
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

  /** Initializes the suspect 2 room view. */
  @FXML
  public void initialize() {
    // Set initial visibility of the buttons
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

              // Initial GPT system message
              ChatMessage systemMessage = new ChatMessage("system", template);
              runGpt(systemMessage);
              if (firstTime == true) {
                userChatBox.setPromptText("Begin interrogating...");
                firstTime = false;
              }

            } catch (ApiProxyException | IOException | URISyntaxException e) {
              e.printStackTrace();
            }
            return null;
          }
        };

    // Run the task in a separate thread
    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();

    backgroundimg.setFitWidth(rootNode.getWidth());
    backgroundimg.setFitHeight(rootNode.getHeight());

    // Make sure the background resizes with the window
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
  private void onUncle(ActionEvent event) throws IOException {
    App.setRoot("suspect1room");
  }

  /**
   * Handles the event when the grandma button is clicked.
   *
   * @param event the action event
   * @throws IOException if the root cannot be set
   */
  @FXML
  private void onGrandma(ActionEvent event) throws IOException {
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
      btnMenu.setStyle(
          "-fx-background-radius: 10 0 0 10; -fx-border-color:  black transparent black black;"
              + " -fx-border-radius: 10 0 0 10; -fx-background-insets: 0;");
    } else {
      btnMenu.setStyle(
          "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;"
              + " -fx-background-insets: 0;");
    }

    // Set visibility and management of other buttons based on isMenuVisible
    btnCrimeScene.setVisible(isMenuVisible);
    btnCrimeScene.setManaged(isMenuVisible);

    btnGrandma.setVisible(isMenuVisible);
    btnGrandma.setManaged(isMenuVisible);

    btnGrandson.setVisible(isMenuVisible);
    btnGrandson.setManaged(isMenuVisible);

    btnUncle.setVisible(isMenuVisible);
    btnUncle.setManaged(isMenuVisible);
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
    App.setRoot("guessing");
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
        && context.getListOfVisitors().contains("suspect3")) {
      // Enable the guess button
      guessButton.setOpacity(0.8);
      guessButton.setDisable(false);
    } else {
      // Disable the guess button
      guessButton.setOpacity(0.3);
      guessButton.setDisable(true);
    }
  }
}
