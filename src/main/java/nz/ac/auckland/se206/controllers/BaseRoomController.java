package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
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

  @FXML protected TextArea userChatBox;
  @FXML protected TextArea suspectChatBox;
  @FXML protected Circle sendButton;
  @FXML protected Button guessButton;
  @FXML protected AnchorPane rootNode;
  @FXML protected ImageView backgroundimg;
  @FXML protected Label lbltimer;

  @FXML private ImageView basemapimg;
  @FXML private Label lblareastatus;
  @FXML private ImageView widowiconimg;
  @FXML private ImageView menuclosedimg;
  @FXML private ImageView brothericonimg;
  @FXML private ImageView grandsoniconimg;
  @FXML private ImageView topofmenubtn;
  @FXML private ImageView crimesceneiconimg;
  @FXML private Pane timerpane;

  @FXML
  public void initialize() {
    updateMenuVisibility();
    bindTimerToLabel();
    initializeGptModel();
    setResponsiveBackground(backgroundimg, rootNode);
    checkGuessButton();

    // Set initial position of basemapimg off the screen (below)
    basemapimg.setTranslateY(rootNode.getHeight()); // Set translateY to move it out of view

    countdownTimer
        .timeStringProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              checkiftimeris4minleft();
            });

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

    basemapimg.fitWidthProperty().bind(rootNode.widthProperty());
    basemapimg.fitHeightProperty().bind(rootNode.heightProperty());

    // Add mouse event handlers for icon ImageViews
    widowiconimg.setOnMouseEntered(this::onMouseEnteredIcon);
    widowiconimg.setOnMouseExited(this::onMouseExitedIcon);
    widowiconimg.setOnMouseClicked(this::onIconClicked);

    brothericonimg.setOnMouseEntered(this::onMouseEnteredIcon);
    brothericonimg.setOnMouseExited(this::onMouseExitedIcon);
    brothericonimg.setOnMouseClicked(this::onIconClicked);

    grandsoniconimg.setOnMouseEntered(this::onMouseEnteredIcon);
    grandsoniconimg.setOnMouseExited(this::onMouseExitedIcon);
    grandsoniconimg.setOnMouseClicked(this::onIconClicked);

    // Add mouse event handlers for the closed menu button
    menuclosedimg.setOnMouseEntered(this::onMouseEnteredMenuClosed);
    menuclosedimg.setOnMouseExited(this::onMouseExitedMenuClosed);
    menuclosedimg.setOnMouseClicked(this::onToggleMenu);

    // Add mouse event handlers for the top of menu button
    topofmenubtn.setOnMouseEntered(this::onMouseEnteredTopOfMenu);
    topofmenubtn.setOnMouseExited(this::onMouseExitedTopOfMenu);
    topofmenubtn.setOnMouseClicked(this::onToggleMenuOff);

    // Add mouse event handlers for the crime scene icon
    crimesceneiconimg.setOnMouseEntered(this::onMouseEnteredIcon);
    crimesceneiconimg.setOnMouseExited(this::onMouseExitedIcon);
    crimesceneiconimg.setOnMouseClicked(
        event -> {
          try {
            onRoom(event);
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }

  private void checkiftimeris4minleft() {
    if (countdownTimer
        .timeStringProperty()
        .get()
        .equals("01:00")) { // When the time reaches 1 minute left
      startFlashingAnimation(timerpane);
    }
  }

  private void startFlashingAnimation(Pane pane) {
    // Store the existing style to restore it later after flashing
    String originalStyle = pane.getStyle();

    // Create a Timeline to flash the pane between styles
    Timeline flashTimeline = new Timeline();

    // Define the CSS styles to use during the animation
    String flashOnStyle =
        "-fx-background-color: #FF0000; -fx-background-radius: 10px; -fx-border-radius: 10px;"
            + " -fx-border-width: 4px; -fx-border-color: #FF0000;";
    String flashOffStyle =
        "-fx-background-color: #ADD8E6; -fx-background-radius: 10px; -fx-border-radius: 10px;"
            + " -fx-border-width: 4px; -fx-border-color: #ADD8E6;";

    // Alternate between red and light blue with border and background radius
    KeyFrame flashOn =
        new KeyFrame(
            Duration.seconds(0),
            new KeyValue(pane.styleProperty(), flashOnStyle) // Set the style to flash on
            );
    KeyFrame flashOff =
        new KeyFrame(
            Duration.seconds(0.5),
            new KeyValue(pane.styleProperty(), flashOffStyle) // Set the style to flash off
            );
    KeyFrame flashOnAgain =
        new KeyFrame(
            Duration.seconds(1),
            new KeyValue(pane.styleProperty(), flashOnStyle) // Set the style to flash on again
            );
    KeyFrame flashOffAgain =
        new KeyFrame(
            Duration.seconds(1.5),
            new KeyValue(pane.styleProperty(), flashOffStyle) // Set the style to flash off again
            );

    // Add keyframes to the timeline
    flashTimeline.getKeyFrames().addAll(flashOn, flashOff, flashOnAgain, flashOffAgain);

    // Repeat the flash sequence for 3 cycles
    flashTimeline.setCycleCount(2);

    // Create a Timeline for the shaking effect
    Timeline shakeTimeline = new Timeline();

    // Create shake keyframes for horizontal movement
    KeyFrame moveRight =
        new KeyFrame(Duration.millis(50), new KeyValue(pane.translateXProperty(), 10));
    KeyFrame moveLeft =
        new KeyFrame(Duration.millis(100), new KeyValue(pane.translateXProperty(), -10));
    KeyFrame moveCenter =
        new KeyFrame(Duration.millis(150), new KeyValue(pane.translateXProperty(), 0));

    // Add keyframes to the shake timeline and set it to repeat during the flashing duration
    shakeTimeline.getKeyFrames().addAll(moveRight, moveLeft, moveCenter);
    shakeTimeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely

    // Create a DropShadow effect for the glowing red border
    DropShadow redGlow = new DropShadow();
    redGlow.setColor(Color.RED);
    redGlow.setRadius(30); // Set the glow radius
    redGlow.setSpread(0.7); // How much the color spreads out

    // Add the glowing effect before starting the animation
    pane.setEffect(redGlow);

    // Play the shake timeline in parallel with the flash timeline
    flashTimeline.setOnFinished(
        event -> {
          pane.setStyle(originalStyle); // Restore the original pane style
          pane.setEffect(null); // Remove the glow effect
          shakeTimeline.stop(); // Stop shaking after the flash ends
          pane.setTranslateX(0); // Reset the pane's position
        });

    // Start the shake and flash animations
    shakeTimeline.play();
    flashTimeline.play();
  }

  @FXML
  protected void onMouseEnteredTopOfMenu(MouseEvent event) {
    // change cursor
    rootNode.getScene().setCursor(javafx.scene.Cursor.HAND);
    lblareastatus.setText("Close Menu?");
  }

  @FXML
  protected void onMouseExitedTopOfMenu(MouseEvent event) {
    // change cursor
    rootNode.getScene().setCursor(javafx.scene.Cursor.DEFAULT);

    // if its in the suspect1room controller set the status to the uncle
    if (this instanceof Suspect1RoomController) {
      lblareastatus.setText("You are in the: Uncle's Room");
    } else if (this instanceof Suspect2RoomController) {
      lblareastatus.setText("You are in the: Widow's Room");
    } else if (this instanceof Suspect3RoomController) {
      lblareastatus.setText("You are in the: Grandson's Room");
    }
  }

  @FXML
  protected void onToggleMenuOff(MouseEvent event) {
    context.toggleMenuVisibility();
    updateMenuVisibility();
  }

  @FXML
  protected void onMouseEnteredMenuClosed(MouseEvent event) {
    // change cursor
    rootNode.getScene().setCursor(javafx.scene.Cursor.HAND);
    lblareastatus.setText("Open Menu?");

    // expand
    menuclosedimg.setScaleX(1.1);
    menuclosedimg.setScaleY(1.1);
  }

  @FXML
  protected void onMouseExitedMenuClosed(MouseEvent event) {
    // change cursor
    rootNode.getScene().setCursor(javafx.scene.Cursor.DEFAULT);

    if (this instanceof Suspect1RoomController) {
      lblareastatus.setText("You are in the: Uncle's Room");
    } else if (this instanceof Suspect2RoomController) {
      lblareastatus.setText("You are in the: Widow's Room");
    } else if (this instanceof Suspect3RoomController) {
      lblareastatus.setText("You are in the: Grandson's Room");
    }

    // shrink
    menuclosedimg.setScaleX(1);
    menuclosedimg.setScaleY(1);
  }

  @FXML
  protected void onToggleMenu(MouseEvent event) {
    context.toggleMenuVisibility();
    basemapimg.toFront();
    topofmenubtn.toFront();
    lblareastatus.toFront();
    widowiconimg.toFront();
    grandsoniconimg.toFront();
    brothericonimg.toFront();
    menuclosedimg.toBack();
    userChatBox.toFront();
    suspectChatBox.toFront();
    guessButton.toFront();
    sendButton.toFront();
    lbltimer.toFront();
    crimesceneiconimg.toFront();
  }

  @FXML
  protected void onMouseEnteredIcon(MouseEvent event) {
    ImageView icon = (ImageView) event.getSource();
    icon.setOpacity(0.7); // Change opacity on hover

    if (icon.getId().equals("widowiconimg")) {
      lblareastatus.setText("Visit the Widow");
    } else if (icon.getId().equals("brothericonimg")) {
      lblareastatus.setText("Visit the Uncle");
    } else if (icon.getId().equals("grandsoniconimg")) {
      lblareastatus.setText("Visit the Grandson");
    }
  }

  @FXML
  protected void onMouseExitedIcon(MouseEvent event) {
    ImageView icon = (ImageView) event.getSource();
    icon.setOpacity(1.0); // Restore opacity

    if (this instanceof Suspect1RoomController) {
      lblareastatus.setText("You are in the: Uncle's Room");
    } else if (this instanceof Suspect2RoomController) {
      lblareastatus.setText("You are in the: Widow's Room");
    } else if (this instanceof Suspect3RoomController) {
      lblareastatus.setText("You are in the: Grandson's Room");
    }
  }

  @FXML
  protected void onIconClicked(MouseEvent event) {
    ImageView icon = (ImageView) event.getSource();
    System.out.println("Clicked on: " + icon.getId()); // Replace with desired action
    switch (icon.getId()) {
      case "widowiconimg" -> {
        try {
          App.setRoot("suspect2room");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      case "brothericonimg" -> {
        try {
          App.setRoot("suspect1room");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      case "grandsoniconimg" -> {
        try {
          App.setRoot("suspect3room");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      default -> {}
    }
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
    System.out.println(
        "Countdown Timer initialized: "
            + (countdownTimer != null)); // Check if countdownTimer is null

    lbltimer
        .textProperty()
        .bind(countdownTimer.timeStringProperty()); // Ensure this binding is correct

    // Attach listener
    countdownTimer
        .timeStringProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              checkiftimeris4minleft();
            });
  }

  protected abstract String getInitialPrompt();

  protected abstract void loadGptPrompt(String resourcePath)
      throws URISyntaxException, IOException, ApiProxyException;

  protected void setUserChatBoxPrompt(String text) {
    Platform.runLater(() -> userChatBox.setPromptText(text));
  }

  @FXML
  protected void onRoom(MouseEvent event) throws IOException {
    App.setRoot("room");
  }

  @FXML
  protected void onSend(MouseEvent event) throws ApiProxyException, IOException {
    handleSendMessage();
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

    if (isMenuVisible) {
      // Set z-order for visible menu
      basemapimg.toFront();
      lblareastatus.toFront();
      widowiconimg.toFront();
      brothericonimg.toFront();
      grandsoniconimg.toFront();
      topofmenubtn.toFront();
      crimesceneiconimg.toFront();

      // Check which controller it is in a set each icon to have a slightly lower opacity depending
      // on which controller

      System.out.println(this.getClass().getName());
      if (this instanceof Suspect1RoomController) {
        widowiconimg.setOpacity(0.7);
      } else if (this instanceof Suspect2RoomController) {
        brothericonimg.setOpacity(0.7);
      } else if (this instanceof Suspect3RoomController) {
        grandsoniconimg.setOpacity(0.7);
      }

      // Hide the closed menu icon behind everything else
      menuclosedimg.toBack();
    } else {
      // If the menu is not visible, bring everything to the back except the closed menu icon
      basemapimg.toBack();
      lblareastatus.toBack();
      widowiconimg.toBack();
      brothericonimg.toBack();
      grandsoniconimg.toBack();
      topofmenubtn.toBack();
      crimesceneiconimg.toBack();

      // Bring the closed menu icon to the front
      menuclosedimg.toFront();
    }
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
