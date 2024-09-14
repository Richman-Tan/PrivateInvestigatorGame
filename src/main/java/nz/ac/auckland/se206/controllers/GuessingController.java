package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.speech.FreeTextToSpeech;

public class GuessingController {

  @FXML private AnchorPane rootPane; // The main container

  @FXML private TextArea txtaChat;
  @FXML private TextField txtInput;
  @FXML private Button btnSend;

  @FXML private Label lbltimer;

  @FXML private Label lblStory; // The Label for displaying text

  @FXML private ImageView background; // GIF image view created in Scene Builder
  @FXML private ImageView backgroundoverlay; // GIF image view created in Scene Builder

  @FXML private ImageView staticlayer; // GIF image view created programmatically

  private ImageView staticimg1; // GIF image view created programmatically

  private TimerModel countdownTimer;

  private ChatCompletionRequest chatCompletionRequest;

  private Timeline timeline;
  private String text = "Who is the culprit . . .";
  private int i = 0;

  /**
   * Initializes the chat view.
   *
   * @throws URISyntaxException
   * @throws IOException
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() {
    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.reset(61);
    countdownTimer.start();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());

    // Center the image in the rootPane
    backgroundoverlay.setFitWidth(rootPane.getWidth());
    backgroundoverlay.setFitHeight(rootPane.getHeight());

    // Make sure the background resizes with the window
    backgroundoverlay.fitWidthProperty().bind(rootPane.widthProperty());
    backgroundoverlay.fitHeightProperty().bind(rootPane.heightProperty());

    // Center the image in the rootPane
    background.setFitWidth(rootPane.getWidth());
    background.setFitHeight(rootPane.getHeight());

    // Make sure the background resizes with the window
    background.fitWidthProperty().bind(rootPane.widthProperty());
    background.fitHeightProperty().bind(rootPane.heightProperty());

    Image framesImage =
        new Image(
            GuessingController.class.getResource("/images/guessingimages/frames.png").toString());

    ImageView framesImages = new ImageView(framesImage);

    // Set the background image to the ImageView
    framesImages.setFitWidth(rootPane.getWidth());
    framesImages.setFitHeight(rootPane.getHeight());

    // Make sure the background resizes with the window
    framesImages.fitWidthProperty().bind(rootPane.widthProperty());
    framesImages.fitHeightProperty().bind(rootPane.heightProperty());
    // add

    Image suspectframe1 =
        new Image(
            GuessingController.class
                .getResource("/images/guessingimages/suspectframe1.png")
                .toString());
    Image suspectframe2 =
        new Image(
            GuessingController.class
                .getResource("/images/guessingimages/suspectframe2.png")
                .toString());
    Image suspectframe3 =
        new Image(
            GuessingController.class
                .getResource("/images/guessingimages/suspectframe3.png")
                .toString());

    ImageView suspectframes1 = new ImageView(suspectframe1);
    ImageView suspectframes2 = new ImageView(suspectframe2);
    ImageView suspectframes3 = new ImageView(suspectframe3);

    rootPane.getChildren().add(framesImages);
    createAndBindImageView(suspectframes1);
    createAndBindImageView(suspectframes2);
    createAndBindImageView(suspectframes3);

    Image suspectframe1hover =
        new Image(
            GuessingController.class
                .getResource("/images/guessingimages/suspect1big.png")
                .toString());
    ImageView suspectframes1hover = new ImageView(suspectframe1hover);
    createAndBindImageView(suspectframes1hover);
    suspectframes1hover.setOpacity(0);

    // Create a hover effect for the ImageView
    suspectframes1.setOnMouseEntered(
        event -> {
          suspectframes1hover.setOpacity(1);
        });
    suspectframes1.setOnMouseExited(
        e -> {
          suspectframes1hover.setOpacity(0);
        });
    suspectframes1hover.setOnMouseExited(
        e -> {
          suspectframes1hover.setOpacity(0);
        });
    suspectframes1hover.setOnMouseEntered(
        e -> {
          suspectframes1hover.setOpacity(1);
        });

    Image suspectframe2hover =
        new Image(
            GuessingController.class
                .getResource("/images/guessingimages/suspect2big.png")
                .toString());
    ImageView suspectframes2hover = new ImageView(suspectframe2hover);
    createAndBindImageView(suspectframes2hover);
    suspectframes2hover.setOpacity(0);

    // Create a hover effect for the ImageView
    suspectframes2.setOnMouseEntered(
        event -> {
          suspectframes2hover.setOpacity(1);
        });
    suspectframes2.setOnMouseExited(
        e -> {
          suspectframes2hover.setOpacity(0);
        });
    suspectframes2hover.setOnMouseExited(
        e -> {
          suspectframes2hover.setOpacity(0);
        });
    suspectframes2hover.setOnMouseEntered(
        e -> {
          suspectframes2hover.setOpacity(1);
        });

    Image suspectframe3hover =
        new Image(
            GuessingController.class
                .getResource("/images/guessingimages/suspect3big.png")
                .toString());
    ImageView suspectframes3hover = new ImageView(suspectframe3hover);
    createAndBindImageView(suspectframes3hover);
    suspectframes3hover.setOpacity(0);

    // Create a hover effect for the ImageView
    suspectframes3.setOnMouseEntered(
        event -> {
          suspectframes1hover.setOpacity(1);
        });
    suspectframes3.setOnMouseExited(
        e -> {
          suspectframes1hover.setOpacity(0);
        });
    suspectframes3hover.setOnMouseExited(
        e -> {
          suspectframes3hover.setOpacity(0);
        });
    suspectframes3hover.setOnMouseEntered(
        e -> {
          suspectframes3hover.setOpacity(1);
        });

    // add hover effect
    addHoverEffect(suspectframes3hover);
    addHoverEffect(suspectframes2hover);
    addHoverEffect(suspectframes1hover);

    createLabel(); // Create the label and add it to the scene
    warpText(); // Start the text animation
    createImageView(); // Create the ImageView and add it to the scene
    staticimages(); // Start the GIF playback every 5 seconds
  }

  private void addHoverEffect(ImageView group) {
    DropShadow hoverShadow = new DropShadow();
    hoverShadow.setColor(Color.CORNFLOWERBLUE); // Customize the hover effect color
    hoverShadow.setRadius(10); // Customize the shadow effect
  }

  private void createAndBindImageView(ImageView imageView) {
    imageView.setFitWidth(rootPane.getWidth());
    imageView.setFitHeight(rootPane.getHeight());
    imageView.fitWidthProperty().bind(rootPane.widthProperty());
    imageView.fitHeightProperty().bind(rootPane.heightProperty());
    rootPane.getChildren().add(imageView);
  }

  private void createLabel() {
    // Creating a new label programmatically
    lblStory = new Label("");

    // Set the initial position for the label
    lblStory.setLayoutX(550); // Adjust X position as needed
    lblStory.setLayoutY(100); // Adjust Y position as needed

    // Set the color and font size of the label text
    lblStory.setStyle("-fx-text-fill: white; -fx-font-size: 30px;");

    // Add the label to the rootPane
    rootPane.getChildren().add(lblStory);
  }

  private void appendChatMessage(ChatMessage msg) {
    // Clear the text area before showing the new message
    txtaChat.clear();
    // Show only the latest message
    txtaChat.appendText(msg.getContent() + "\n\n");
  }

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

  @FXML
  private void onSendMessage(ActionEvent event) {
    String message = txtInput.getText().trim();
    System.out.println("Message: " + message);
    if (message.isEmpty()) {
      return;
    }

    txtInput.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);

    try {
      runGpt(msg);
    } catch (ApiProxyException e) {
      appendChatMessage(new ChatMessage("system", "Error sending message: " + e.getMessage()));
    }
  }

  // Method to disable/enable send button
  private void disableSendButton(boolean disable) {
    btnSend.setDisable(disable);
  }

  private static String loadTemplate(URI filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)));
  }

  private void warpText() {
    timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.2),
                event -> {
                  if (i < text.length()) {
                    lblStory.setText(text.substring(0, i + 1));
                    i++;
                  } else {
                    timeline.stop();
                    flashLastDot(); // Start flashing the last dot
                  }
                }));

    timeline.setCycleCount(Timeline.INDEFINITE); // Loop until all text is shown
    timeline.play(); // Start the animation
  }

  private void flashLastDot() {
    // Create a new Timeline for flashing the last '.'
    Timeline flashTimeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.5),
                event -> {
                  // Get the current text and toggle the last dot's visibility
                  String currentText = lblStory.getText();
                  if (currentText.endsWith(".")) {
                    lblStory.setText(
                        currentText.substring(0, currentText.length() - 1)); // Hide dot
                  } else {
                    lblStory.setText(currentText + "."); // Show dot
                  }
                }));

    flashTimeline.setCycleCount(Timeline.INDEFINITE); // Keep flashing indefinitely
    flashTimeline.play(); // Start the flashing animation
  }

  private void createImageView() {
    // Create the ImageView programmatically
    staticimg1 = new ImageView();

    // Set the initial size and position for the ImageView
    staticimg1.setFitWidth(200);
    staticimg1.setFitHeight(200);
    staticimg1.setLayoutX(100); // Set X position
    staticimg1.setLayoutY(100); // Set Y position

    // Add the ImageView to the rootPane (or any other container)
    rootPane.getChildren().add(staticimg1);
  }

  private void staticimages() {
    // Ensure the staticimg1 is anchored to all sides of the AnchorPane (rootPane)
    AnchorPane.setTopAnchor(staticimg1, 0.0);
    AnchorPane.setBottomAnchor(staticimg1, 0.0);
    AnchorPane.setLeftAnchor(staticimg1, 0.0);
    AnchorPane.setRightAnchor(staticimg1, 0.0);

    // Bind the width and height of the ImageView to match the rootPane's size
    staticimg1.fitWidthProperty().bind(rootPane.widthProperty());
    staticimg1.fitHeightProperty().bind(rootPane.heightProperty());

    // Center the image in the rootPane
    staticlayer.setFitWidth(rootPane.getWidth());
    staticlayer.setFitHeight(rootPane.getHeight());

    // Make sure the background resizes with the window
    staticlayer.fitWidthProperty().bind(rootPane.widthProperty());
    staticlayer.fitHeightProperty().bind(rootPane.heightProperty());

    // Move the image one layer back

    backgroundoverlay.toBack();
    staticimg1.toBack();
    staticlayer.toBack();
    background.toBack();

    Timeline gifPlayTimeline =
        new Timeline(
            // KeyFrame 1: Show the GIF (make it visible and set opacity to 1)
            new KeyFrame(
                Duration.seconds(0), // Start immediately
                event -> {
                  // Reset the GIF by loading it again
                  Image gifImage =
                      new Image(
                          GuessingController.class
                              .getResource("/images/guessingimages/static.gif")
                              .toString());
                  staticimg1.setImage(gifImage); // Set the GIF image to staticimg1
                  staticimg1.setVisible(true); // Show the ImageView
                  staticimg1.setOpacity(0.75); // Fully visible
                }),
            // KeyFrame 2: Hide the GIF after 2 seconds
            new KeyFrame(
                Duration.seconds(2), // After 2 seconds
                event -> {
                  staticimg1.setVisible(false); // Hide the ImageView
                  staticimg1.setOpacity(0); // Set opacity to 0 (fully hidden)
                }),
            // KeyFrame 3: Wait for 8 seconds before the next cycle
            new KeyFrame(
                Duration.seconds(
                    10) // After 10 seconds total (2 seconds visible + 8 seconds hidden)
                ));

    // Set the cycle count to indefinite, so it repeats
    gifPlayTimeline.setCycleCount(Timeline.INDEFINITE);

    // Start the GIF animation timeline
    gifPlayTimeline.play();
  }

  @FXML
  private void onClickedGuess() {

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
                  PromptEngineering.class.getClassLoader().getResource("prompts/guessing.txt");
              String template = loadTemplate(resourceUrl.toURI());

              // Initial GPT system message
              ChatMessage systemMessage = new ChatMessage("system", template);
              ChatMessage response = runGpt(systemMessage);

              // Update the UI using Platform.runLater
              Platform.runLater(() -> appendChatMessage(response));

            } catch (ApiProxyException | IOException | URISyntaxException e) {
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
}
