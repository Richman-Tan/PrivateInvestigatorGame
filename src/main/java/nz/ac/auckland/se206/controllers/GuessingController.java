package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
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

public class GuessingController {

  @FXML private AnchorPane rootPane; // The main container

  @FXML private TextArea txtaChat;
  @FXML private TextField txtInput;
  @FXML private Button btnSend;

  @FXML private Button btnReplay;
  @FXML private StackPane stackPane;

  @FXML private Label lbltimer;

  @FXML private Label lblStory; // The Label for displaying text

  @FXML private ImageView background; // GIF image view created in Scene Builder
  @FXML private ImageView backgroundoverlay; // GIF image view created in Scene Builder
  @FXML private ImageView wires;
  @FXML private ImageView clue1foundimg;

  @FXML private ImageView staticlayer; // GIF image view created programmatically

  @FXML private ProgressIndicator progressIndicator;

  private GameStateContext context = GameStateContext.getInstance();

  private ImageView staticimg1; // GIF image view created programmatically

  private Label selectedLabel = new Label("");

  private TimerModel countdownTimer;

  private ChatCompletionRequest chatCompletionRequest;

  private String guessedsuspect;

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

    txtaChat.setStyle(
        "-fx-border-color: black; "
            + "-fx-background-color: black; "
            + "-fx-text-fill: white; "
            + "-fx-prompt-text-fill: white; "
            + "-fx-font-size: 12px;"
            + "-fx-border-radius: 10px; "
            + "-fx-background-radius: 10px;"
            + "-fx-control-inner-background: black;");
    txtaChat.setEditable(false);

    txtaChat.setOpacity(0);
    btnReplay.setOpacity(0);

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

    Image suspectframe2hover =
        new Image(
            GuessingController.class
                .getResource("/images/guessingimages/suspect2big.png")
                .toString());
    ImageView suspectframes2hover = new ImageView(suspectframe2hover);
    createAndBindImageView(suspectframes2hover);
    suspectframes2hover.setOpacity(0);

    Image suspectframe3hover =
        new Image(
            GuessingController.class
                .getResource("/images/guessingimages/suspect3big.png")
                .toString());
    ImageView suspectframes3hover = new ImageView(suspectframe3hover);
    createAndBindImageView(suspectframes3hover);
    suspectframes3hover.setOpacity(0);

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
    suspectframes1hover.setOnMouseClicked(
        e -> {
          setframestoopacity0(
              suspectframes1hover,
              suspectframes2hover,
              suspectframes3hover,
              suspectframes1,
              suspectframes2,
              suspectframes3,
              framesImages);
          guessedsuspect = "suspect1";
          onClickedGuess();
        });
    suspectframes1.setOnMouseClicked(
        e -> {
          setframestoopacity0(
              suspectframes1hover,
              suspectframes2hover,
              suspectframes3hover,
              suspectframes1,
              suspectframes2,
              suspectframes3,
              framesImages);
          guessedsuspect = "suspect1";
          onClickedGuess();
        });

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
    suspectframes2hover.setOnMouseClicked(
        e -> {
          setframestoopacity0(
              suspectframes1hover,
              suspectframes2hover,
              suspectframes3hover,
              suspectframes1,
              suspectframes2,
              suspectframes3,
              framesImages);
          guessedsuspect = "suspect2";
          onClickedGuess();
        });
    suspectframes2.setOnMouseClicked(
        e -> {
          setframestoopacity0(
              suspectframes1hover,
              suspectframes2hover,
              suspectframes3hover,
              suspectframes1,
              suspectframes2,
              suspectframes3,
              framesImages);
          guessedsuspect = "suspect2";
          onClickedGuess();
        });

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
    suspectframes3hover.setOnMouseClicked(
        e -> {
          setframestoopacity0(
              suspectframes1hover,
              suspectframes2hover,
              suspectframes3hover,
              suspectframes1,
              suspectframes2,
              suspectframes3,
              framesImages);
          guessedsuspect = "suspect3";
          onClickedGuess();
        });
    suspectframes3.setOnMouseClicked(
        e -> {
          setframestoopacity0(
              suspectframes1hover,
              suspectframes2hover,
              suspectframes3hover,
              suspectframes1,
              suspectframes2,
              suspectframes3,
              framesImages);
          guessedsuspect = "suspect3";
          onClickedGuess();
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

  private void setframestoopacity0(
      ImageView suspectframes1hover,
      ImageView suspectframes2hover,
      ImageView suspectframes3hover,
      ImageView suspectframes1,
      ImageView suspectframes2,
      ImageView suspectframes3,
      ImageView framesImages) {
    suspectframes1hover.setOpacity(0);
    suspectframes2hover.setOpacity(0);
    suspectframes3hover.setOpacity(0);
    suspectframes1.setOpacity(0);
    suspectframes2.setOpacity(0);
    suspectframes3.setOpacity(0);
    framesImages.setOpacity(0);
    lblStory.setOpacity(0);

    // disable the hover
    suspectframes1hover.setDisable(true);
    suspectframes2hover.setDisable(true);
    suspectframes3hover.setDisable(true);
    suspectframes1.setDisable(true);
    suspectframes2.setDisable(true);
    suspectframes3.setDisable(true);
    framesImages.setDisable(true);
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

    // Load the Fira Code font from the resources folder
    Font firaCodeFont =
        Font.loadFont(getClass().getResourceAsStream("/fonts/FiraCode-Regular.ttf"), 20);

    // Check if the font was loaded correctly
    if (firaCodeFont == null) {
      System.out.println("Font not loaded, check the path to the font file.");
      return; // Stop execution if font loading fails
    }

    // Set the font and color for the label
    lblStory.setFont(firaCodeFont);
    lblStory.setStyle("-fx-text-fill: white;");

    // Add the label to the rootPane first
    rootPane.getChildren().add(lblStory);

    // Use Platform.runLater to wait until the layout is applied
    Platform.runLater(
        () -> {
          // Calculate the center X and Y after the label's width and height are set
          double centerX = (rootPane.getWidth() - lblStory.getWidth()) / 2;
          double centerY = (rootPane.getHeight() - lblStory.getHeight()) / 2;

          // Set the label's position to center it
          lblStory.setLayoutX(centerX - 130);
          lblStory.setLayoutY(centerY - 200);
        });
  }

  private void appendChatMessage(ChatMessage msg) {
    // Clear the text area before showing the new message
    txtaChat.clear();

    // Get the message content as a string
    String content = msg.getContent();

    // Create a new StringBuilder to hold the text progressively
    StringBuilder displayedText = new StringBuilder();

    // Create a new Timeline to append the text one letter at a time
    Timeline timeline = new Timeline();

    // Loop through each character of the message and create keyframes to append the characters
    for (int i = 0; i < content.length(); i++) {
      final int index = i;
      KeyFrame keyFrame =
          new KeyFrame(
              Duration.millis(50 * (index + 1)), // Delay based on character position
              event -> {
                // Append the next character to the StringBuilder
                displayedText.append(content.charAt(index));
                // Update the TextArea with the current text
                txtaChat.setText(displayedText.toString());
              });
      timeline.getKeyFrames().add(keyFrame);
    }

    // Play the timeline animation
    timeline.play();
  }

  private ChatMessage runGpt(ChatMessage msg) throws ApiProxyException {
    chatCompletionRequest.addMessage(msg);
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
    }
  }

  @FXML
  private void onSendMessage(ActionEvent event) {
    btnReplay.setOpacity(1);

    String message = txtInput.getText().trim();
    System.out.println("Message: " + message);
    if (message.isEmpty()) {
      return;
    }

    txtInput.clear();

    // Show the ProgressIndicator when the task starts
    progressIndicator.setVisible(true);

    // Create a background task for the GPT request
    Task<ChatMessage> task =
        new Task<ChatMessage>() {
          @Override
          protected ChatMessage call() throws Exception {
            // Initialize the API configuration and ChatCompletionRequest
            ApiProxyConfig config = ApiProxyConfig.readConfig();
            chatCompletionRequest =
                new ChatCompletionRequest(config)
                    .setN(1)
                    .setTemperature(0.2)
                    .setTopP(0.5)
                    .setMaxTokens(100);

            // Load the template from the resource file
            URL resourceUrl =
                PromptEngineering.class.getClassLoader().getResource("prompts/guessing.txt");
            String template = loadTemplate(resourceUrl.toURI());

            // Append the user's message to the end of the template
            String combinedMessage = template + "\n" + message;

            // Create a system message with the combined template and user input
            ChatMessage systemMessage = new ChatMessage("system", combinedMessage);

            // Run GPT and get the response
            return runGpt(systemMessage);
          }
        };

    // On success, update the UI (run on the JavaFX Application Thread)
    task.setOnSucceeded(
        workerStateEvent -> {
          ChatMessage response = task.getValue();
          Platform.runLater(
              () -> {
                appendChatMessage(response); // Append the GPT response to the chat
                progressIndicator.setVisible(false); // Hide the progress indicator
              });
        });

    // On failure, handle the exception (you can also update the UI with an error message)
    task.setOnFailed(
        workerStateEvent -> {
          Throwable throwable = task.getException();
          Platform.runLater(
              () -> {
                appendChatMessage(new ChatMessage("system", "Error: " + throwable.getMessage()));
                progressIndicator.setVisible(false); // Hide the progress indicator on failure
              });
          throwable.printStackTrace();
        });

    // Start the task in a new thread
    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
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

    wires.setFitWidth(rootPane.getWidth());
    wires.setFitHeight(rootPane.getHeight());

    // Make sure the background resizes with the window
    wires.fitWidthProperty().bind(rootPane.widthProperty());
    wires.fitHeightProperty().bind(rootPane.heightProperty());

    // Add a darker drop shadow to the wires
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(5.0);
    dropShadow.setOffsetX(3.0);
    dropShadow.setOffsetY(3.0);

    // Make the shadow darker by reducing the brightness and increasing the opacity
    dropShadow.setColor(Color.color(0.1, 0.1, 0.1, 0.8)); // Darker shadow with 80% opacity

    wires.setEffect(dropShadow);

    if (context.isGardenToolFound()) {
      clue1foundimg.setFitWidth(rootPane.getWidth());
      clue1foundimg.setFitHeight(rootPane.getHeight());

      // Make sure the background resizes with the window
      clue1foundimg.fitWidthProperty().bind(rootPane.widthProperty());
      clue1foundimg.fitHeightProperty().bind(rootPane.heightProperty());

      clue1foundimg.setOpacity(1);
    }

    wires.toBack();
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

    if (guessedsuspect.equals("suspect1")) {
      // Create the label above the TextField
      selectedLabel = new Label("You have selected the WIDOW");
      selectedLabel.setStyle(
          "-fx-text-fill: white; -fx-font-size: 14px;"); // Set text color and size
      selectedLabel.setLayoutX(450.0); // Same X position as TextField to align it
      selectedLabel.setLayoutY(100.0); // Place it above the TextField
      rootPane.getChildren().add(selectedLabel); // Add the label to the rootPane

      // Load the image for the suspect
      Image suspectImage =
          new Image(getClass().getResource("/images/guessingimages/suspectframe1.png").toString());
      ImageView suspectImageView = new ImageView(suspectImage);

      suspectImageView.setPreserveRatio(true);
      // Set the image's size and position
      suspectImageView.setFitWidth(rootPane.getWidth());
      suspectImageView.setFitHeight(rootPane.getHeight());

      suspectImageView.setLayoutX(-120); // Set the X position (move right if needed)
      suspectImageView.setLayoutY(-15); // Set the Y position to move it lower

      // Add the image to the rootPane
      rootPane.getChildren().add(suspectImageView);
    } else if (guessedsuspect.equals("suspect2")) {
      // Create the label above the TextField
      selectedLabel = new Label("You have selected the SON.");
      selectedLabel.setStyle(
          "-fx-text-fill: white; -fx-font-size: 14px;"); // Set text color and size
      selectedLabel.setLayoutX(450.0); // Same X position as TextField to align it
      selectedLabel.setLayoutY(100.0); // Place it above the TextField
      rootPane.getChildren().add(selectedLabel); // Add the label to the rootPane

      // Load the image for the suspect
      Image suspectImage =
          new Image(getClass().getResource("/images/guessingimages/suspectframe2.png").toString());
      ImageView suspectImageView = new ImageView(suspectImage);

      // Set the image's size and position
      suspectImageView.setPreserveRatio(true);
      suspectImageView.setFitWidth(rootPane.getWidth());
      suspectImageView.setFitHeight(rootPane.getHeight());

      suspectImageView.setLayoutX(-230); // Set the X position (move right if needed)
      suspectImageView.setLayoutY(-15); // Set the Y position to move it lower

      // Add the image
      rootPane.getChildren().add(suspectImageView);
    } else if (guessedsuspect.equals("suspect3")) {
      // Create the label above the TextField
      selectedLabel = new Label("You have selected the UNCLE.");
      selectedLabel.setStyle(
          "-fx-text-fill: white; -fx-font-size: 14px;"); // Set text color and size
      selectedLabel.setLayoutX(450.0); // Same X position as TextField to align it
      selectedLabel.setLayoutY(100.0); // Place it above the TextField
      rootPane.getChildren().add(selectedLabel); // Add the label to the rootPane

      // Load the image for the suspect
      Image suspectImage =
          new Image(getClass().getResource("/images/guessingimages/suspectframe3.png").toString());
      ImageView suspectImageView = new ImageView(suspectImage);

      // Set the image's size and position
      suspectImageView.setFitWidth(rootPane.getWidth() - 25);
      suspectImageView.setFitHeight(rootPane.getHeight() - 50);

      // Add the image
      rootPane.getChildren().add(suspectImageView);
    }

    // Create a new TextField dynamically
    txtInput = new TextField();

    // Set the id, layout properties, size, and styles to match the FXML definition
    txtInput.setId("userChatBox"); // Set the fx:id equivalent
    txtInput.setLayoutX(450.0); // Set X position
    txtInput.setLayoutY(130.0); // Set Y position
    txtInput.setPrefHeight(120.0); // Set preferred height
    txtInput.setPrefWidth(226.0); // Set preferred width

    // Set the prompt text (placeholder)
    txtInput.setPromptText("Enter your reasoning here...");

    // Set the CSS style for border, background, and text colors
    txtInput.setStyle(
        "-fx-border-color: black; "
            + // No border color
            "-fx-background-color: black; "
            + // Transparent background
            "-fx-prompt-text-fill: white; "
            + // White color for prompt text
            "-fx-text-fill: white;" // White color for input text
            + "-fx-font-size: 12px;"
            + "-fx-border-radius: 10px; "
            + // Rounded border radius
            "-fx-background-radius: 10px"); // Rounded background radius); // Font size for the
    // input text

    txtInput.setOpacity(0.8);

    // Align the text to the top left
    txtInput.setAlignment(Pos.TOP_LEFT);

    // Add the TextField to the rootPane
    rootPane.getChildren().add(txtInput);

    // Create a new Button dynamically below the TextField
    Button btnSend = new Button("Send");
    btnSend.setLayoutX(450.0); // Align with TextField
    btnSend.setLayoutY(270.0); // Position right below the TextField
    btnSend.setPrefWidth(226.0); // Same width as the TextField

    // Add event handler to the button
    btnSend.setOnAction(
        event -> {
          String userInput = txtInput.getText();
          if (!userInput.isEmpty()) {
            System.out.println("User Input: " + userInput);
            // Add logic to handle the user's input
            stackPane.setOpacity(0.8);
            txtaChat.setOpacity(0.8);
            txtInput.setOpacity(0);
            btnSend.setOpacity(0);

            btnSend.setDisable(true);

            selectedLabel.setOpacity(0);
            onSendMessage(event);
          }
        });

    // Add the Button to the rootPane
    rootPane.getChildren().add(btnSend);

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
    // thread.start();
  }

  @FXML
  private void onReplay(ActionEvent event) throws IOException {

    // Re initalise the context
    GameStateContext.getInstance().reset();

    // Fade out transition
    FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(1000), rootPane);
    fadeOutTransition.setFromValue(1.0);
    fadeOutTransition.setToValue(0.0);
    fadeOutTransition.play();

    App.setRoot("initialScene");
  }
}
