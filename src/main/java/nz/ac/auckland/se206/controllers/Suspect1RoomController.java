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
import javafx.scene.input.MouseEvent;
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
import nz.ac.auckland.se206.speech.FreeTextToSpeech;

public class Suspect1RoomController {

  @FXML private Button btnMenu;
  @FXML private Button btnCrimeScene;
  @FXML private Button btnGrandma;
  @FXML private Button btnGrandson;
  @FXML private Button btnUncle;
  @FXML private TextArea suspect1ChatBox;
  @FXML private TextField userChatBox;
  @FXML private Circle sendButton;

  @FXML private Label lbltimer;

  private ChatCompletionRequest chatCompletionRequest;
  private GameStateContext context = GameStateContext.getInstance();

  private TimerModel countdownTimer;

  /** Initializes the suspect 1 room view. */
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

              // Initial GPT system message
              ChatMessage systemMessage = new ChatMessage("system", template);
              ChatMessage response = runGpt(systemMessage);

              // Update the UI using Platform.runLater
              Platform.runLater(() -> appendChatMessage(response));

            } catch (ApiProxyException | IOException | URISyntaxException e) {
              // If there is an error, show it in the chat on the JavaFX thread
              Platform.runLater(
                  () ->
                      appendChatMessage(
                          new ChatMessage("system", "Error initializing chat: " + e.getMessage())));
              e.printStackTrace();
            }
            return null;
          }
        };

    // Run the task in a separate thread
    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
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

  @FXML
  private void onSend() {}

  /**
   * Handles the event when the grandson button is clicked.
   *
   * @param event the action event
   * @throws IOException if the root cannot be set
   */
  @FXML
  private void onGrandson(ActionEvent event) throws IOException {
    App.setRoot("suspect3room");
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
      FreeTextToSpeech.speak(result.getChatMessage().getContent());
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
    suspect1ChatBox.appendText(msg.getRole() + ": " + msg.getContent() + "\n\n");
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
    // to adjust
    String message = userChatBox.getText().trim();
    if (message.isEmpty()) {
      return;
    }
    userChatBox.clear();
    ChatMessage msg = new ChatMessage("user", message);
    // appendChatMessage(msg);
    runGpt(msg);
  }

  // Method to disable/enable send button
  private void disableSendButton(boolean disable) {
    sendButton.setDisable(disable);
  }

  private static String loadTemplate(URI filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)));
  }
}
