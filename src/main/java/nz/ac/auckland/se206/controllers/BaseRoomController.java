package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

public abstract class BaseRoomController {

  protected ChatCompletionRequest chatCompletionRequest;
  protected GameStateContext context = GameStateContext.getInstance();
  protected boolean firstTime = true;
  protected TimerModel countdownTimer;
  protected SharedVolumeControl sharedVolumeControl;

  @FXML protected Button crimeSceneButton;
  @FXML protected Button grandmaButton;
  @FXML protected Button grandsonButton;
  @FXML protected Button menuButton;
  @FXML protected Button uncleButton;
  @FXML protected TextArea userChatBox;
  @FXML protected TextArea suspectChatBox;
  @FXML protected Circle sendButton;
  @FXML protected Button guessButton;
  @FXML protected AnchorPane rootNode;
  @FXML protected ImageView backgroundimg;
  @FXML protected Label lbltimer;
  @FXML protected SVGPath volumeOff;
  @FXML protected SVGPath volumeUp;
  @FXML protected SVGPath volumeUpStroke;

  @FXML
  public void initialize() {
    updateMenuVisibility();
    bindTimerToLabel();
    initializeGptModel();
    setResponsiveBackground(backgroundimg, rootNode);
    checkGuessButton();
    try {
      checkVolumeIcon();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Add key event handler for detecting Enter key
    userChatBox.addEventFilter(
        KeyEvent.KEY_PRESSED,
        event -> {
          if (event.getCode() == KeyCode.ENTER) {
            event.consume();
            // Handle the Enter key press action
            ActionEvent actionEvent = new ActionEvent(event.getSource(), event.getTarget());
            try {
              onEnterKey(actionEvent);
            } catch (IOException | ApiProxyException e) {
              e.printStackTrace();
            }
            // Optionally, consume the event to prevent a new line from being added
            // event.consume();
          }
        });
  }

  protected void initializeGptModel() {
    // Create a new task to initialize the GPT model
    Task<Void> task =
        new Task<>() {
          @Override
          protected Void call() throws IOException, URISyntaxException {
            try {
              setupGptRequest();
              // Load the initial prompt
              loadGptPrompt(getInitialPrompt());
              // Set the user chat box prompt with the initial prompt
              if (firstTime) {
                setUserChatBoxPrompt("Begin interrogating...");
                firstTime = false;
              }
              // Error handling
            } catch (ApiProxyException | IOException | URISyntaxException e) {
              appendChatMessage(new ChatMessage("system", "Error: " + e.getMessage()));
            }
            return null;
          }
        };

    new Thread(task).start();
  }

  protected void setupGptRequest() throws IOException, ApiProxyException {
    // Set up the GPT request
    ApiProxyConfig config = ApiProxyConfig.readConfig();
    chatCompletionRequest =
        // Set the parameters for the GPT request
        new ChatCompletionRequest(config)
            .setN(1)
            .setTemperature(0.2)
            .setTopP(0.5)
            .setMaxTokens(100);
  }

  protected void bindTimerToLabel() {
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());
  }

  protected abstract String getInitialPrompt();

  protected abstract void loadGptPrompt(String resourcePath)
      throws URISyntaxException, IOException, ApiProxyException;

  protected void setUserChatBoxPrompt(String text) {
    Platform.runLater(() -> userChatBox.setPromptText(text));
  }

  @FXML
  protected void onRoom() throws IOException {
    App.setRoot("room");
  }

  @FXML
  protected void onSend(MouseEvent event) throws ApiProxyException, IOException {
    handleSendMessage();
  }

  @FXML
  protected void turnVolumeOff() throws IOException {
    volumeOff.setVisible(true);
    volumeUp.setVisible(false);
    volumeUpStroke.setVisible(false);
    SharedVolumeControl.getInstance().setVolumeSetting(false);
  }

  @FXML
  protected void turnVolumeOn() throws IOException {
    volumeOff.setVisible(false);
    volumeUp.setVisible(true);
    volumeUpStroke.setVisible(true);
    SharedVolumeControl.getInstance().setVolumeSetting(true);
  }

  private void checkVolumeIcon() throws IOException {
    if (SharedVolumeControl.getInstance().getVolumeSetting()) {
      turnVolumeOn();
    } else {
      turnVolumeOff();
    }
  }

  protected void handleSendMessage() throws ApiProxyException, IOException {
    sendMessageCode();
    recordVisit();
    checkGuessButton();
  }

  protected void sendMessageCode() throws ApiProxyException, IOException {
    String message = userChatBox.getText().trim();
    if (message.isEmpty()) return;

    clearUserInput();

    ChatMessage msg = new ChatMessage("user", message);
    Task<Void> task = createGptTask(msg);

    new Thread(task).start();
  }

  protected Task<Void> createGptTask(ChatMessage msg) {
    // Create a new task to run the GPT model
    return new Task<>() {
      @Override
      protected Void call() {
        // Run the GPT model and alter the user chat box prompt
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

  protected void clearUserInput() {
    userChatBox.clear();
    suspectChatBox.clear();
    userChatBox.setPromptText("Waiting for response...");
  }

  protected ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    chatCompletionRequest.addMessage(msg);
    disableSendButton(true);

    // Send the message to GPT
    try {
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      ChatMessage response = chatCompletionResult.getChoices().iterator().next().getChatMessage();
      chatCompletionRequest.addMessage(response);
      // Append the response to the chat box
      appendChatMessage(response);

      return response;
    } catch (ApiProxyException e) {
      // Append an error message to the chat box if an exception occurs
      appendChatMessage(new ChatMessage("system", "Error during GPT call: " + e.getMessage()));
      return null;
    } finally {
      disableSendButton(false);
    }
  }

  @FXML
  protected void appendChatMessage(ChatMessage msg) {
    suspectChatBox.clear();
    suspectChatBox.appendText(msg.getContent() + "\n\n");
  }

  protected void disableSendButton(boolean disable) {
    sendButton.setDisable(disable);
  }

  protected void recordVisit() {
    // Implement specific record-keeping logic in subclasses
  }

  @FXML
  protected void checkGuessButton() {
    // Enable the guess button if all suspects have been visited and at least one clue has been
    // found
    boolean canGuess =
        context.getListOfVisitors().contains("suspect1")
            && context.getListOfVisitors().contains("suspect2")
            && context.getListOfVisitors().contains("suspect3")
            && context.isAtLeastOneClueFound();

    // Set the opacity and disable state of the guess button
    guessButton.setOpacity(canGuess ? 0.8 : 0.3);
    guessButton.setDisable(!canGuess);
  }

  protected void setResponsiveBackground(ImageView imageView, AnchorPane pane) {
    // Set the background image to be responsive
    imageView.fitWidthProperty().bind(pane.widthProperty());
    imageView.fitHeightProperty().bind(pane.heightProperty());
  }

  protected void updateMenuVisibility() {
    boolean isMenuVisible = context.isMenuVisible();
    updateMenuButtonStyle(isMenuVisible);
    setButtonVisibility(isMenuVisible);
  }

  protected void updateMenuButtonStyle(boolean isMenuVisible) {
    // Update the style of the menu button based on the visibility of the menu
    menuButton.setStyle(
        isMenuVisible
            ? "-fx-background-radius: 10 0 0 10; -fx-border-color: black transparent black black;"
            : "-fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: black;");
  }

  protected void setButtonVisibility(boolean isVisible) {
    // Set the visibility of the buttons based on the visibility of the menu
    setVisibleAndManaged(crimeSceneButton, isVisible);
    setVisibleAndManaged(grandmaButton, isVisible);
    setVisibleAndManaged(grandsonButton, isVisible);
    setVisibleAndManaged(uncleButton, isVisible);
  }

  protected void setVisibleAndManaged(Button button, boolean isVisible) {
    button.setVisible(isVisible);
    button.setManaged(isVisible);
  }

  @FXML
  protected void onToggleMenu(ActionEvent event) {
    context.toggleMenuVisibility();
    updateMenuVisibility();
  }

  @FXML
  protected void onKeyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.ENTER) {
      try {
        handleSendMessage(); // Handle the action when the Enter key is pressed
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected String loadTemplate(URI filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)));
  }

  @FXML
  protected void onEnterKey(ActionEvent event) throws IOException, ApiProxyException {
    handleSendMessage();
  }

  @FXML
  protected void onSend(ActionEvent event) throws ApiProxyException, IOException {
    handleSendMessage();
  }
}
