package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.GameStateContext;

public class UpdatedGuessingController {
  // main pane
  @FXML private AnchorPane rootPane;

  @FXML private Pane guessPhotoPane;
  @FXML private Pane verifyCulpritPane;

  @FXML private Rectangle recSuspect1;
  @FXML private Rectangle recSuspect2;
  @FXML private Rectangle recSuspect3;
  @FXML private ImageView confirmedSuspect1;
  @FXML private ImageView confirmedSuspect2;
  @FXML private ImageView confirmedSuspect3;
  @FXML private Label culpritLabel;
  @FXML private Button confirmCulpritButton;
  @FXML private ImageView staticlayer; // GIF image view created programmatically

  @FXML private ImageView staticImage;
  @FXML private ImageView background;
  private String text = "Who is the culprit . . .";
  @FXML private ImageView clue1foundimg;
  private ImageView staticimg1; // GIF image view created programmatically
  private Timeline timeline;
  @FXML private Pane gameOverPane;
  @FXML private Rectangle gameOverRectangle;
  @FXML private Rectangle gameOverRectangle2;
  private int i = 0;
  private int j = 0;
  @FXML private Label correctGuessLbl;
  @FXML private Label incorrectGuessLbl;
  private ArrayList<Object> list;
  @FXML private Label lbltimer;
  @FXML private Label gameOverTxt;
  @FXML private Label reviewLbl;
  @FXML private TextField feedbackField;
  @FXML private Label lblStory; // The Label for displaying text
  private boolean guess = false;
  @FXML private Label incorrectGuessLbl2;
  private boolean stopTimeline = false;

  @FXML private Button confirmExplanationButton;

  @FXML private Rectangle recSus1;
  @FXML private Rectangle recSus2;
  @FXML private Rectangle recSus3;

  private GameStateContext context = GameStateContext.getInstance();
  private Label selectedLabel = new Label("");

  private TimerModel countdownTimer;

  private ChatCompletionRequest chatCompletionRequest;

  private String guessedsuspect;

  /**
   * Initializes the chat view.
   *
   * @throws URISyntaxException
   * @throws IOException
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() {
    confirmCulpritButton.setDisable(true);

    // txtaChat.setStyle(
    //     "-fx-border-color: black; "
    //         + "-fx-background-color: black; "
    //         + "-fx-text-fill: white; "
    //         + "-fx-prompt-text-fill: white; "
    //         + "-fx-font-size: 12px;"
    //         + "-fx-border-radius: 10px; "
    //         + "-fx-background-radius: 10px;"
    //         + "-fx-control-inner-background: black;");
    // txtaChat.setEditable(false);

    // txtaChat.setOpacity(0);
    // btnReplay.setOpacity(0);

    countdownTimer = SharedTimerModel.getInstance().getTimer();
    countdownTimer.reset(61);
    countdownTimer.start();
    lbltimer.textProperty().bind(countdownTimer.timeStringProperty());

    warpText(); // Start the text animation
    createImageView(); // Create the ImageView and add it to the scene
  }

  @FXML
  private void hoverImageGma(MouseEvent event) throws IOException {
    recSuspect2.setVisible(true);
    recSuspect2.setMouseTransparent(true);
  }

  @FXML
  private void hoverImageUncle(MouseEvent event) throws IOException {
    recSuspect1.setVisible(true);
    recSuspect1.setMouseTransparent(true);
  }

  @FXML
  private void hoverImageSon(MouseEvent event) throws IOException {
    recSuspect3.setVisible(true);
    recSuspect3.setMouseTransparent(true);
  }

  @FXML
  private void offHoverImageSon(MouseEvent event) {
    recSuspect3.setVisible(false);
  }

  @FXML
  private void offHoverImageGma(MouseEvent event) {
    recSuspect2.setVisible(false);
  }

  @FXML
  private void offHoverImageUncle(MouseEvent event) {
    recSuspect1.setVisible(false);
  }

  @FXML
  private void clickedImageUncle(MouseEvent event) throws IOException {
    guessedsuspect = "Uncle";
    recSus1.setVisible(true);
    confirmCulpritButton.setDisable(false);
    confirmCulpritButton.setOpacity(1);
  }

  @FXML
  private void clickedImageSon(MouseEvent event) throws IOException {
    guessedsuspect = "Grandson";
    recSus3.setVisible(true);
    confirmCulpritButton.setDisable(false);
    confirmCulpritButton.setOpacity(1);
  }

  @FXML
  private void clickedImageGma(MouseEvent event) throws IOException {
    guessedsuspect = "Grandma";
    recSus2.setVisible(true);
    confirmCulpritButton.setDisable(false);
    confirmCulpritButton.setOpacity(1);
  }

  /**
   * Method to handle when the confirm culprit button is clicked
   *
   * @param event the mouse event
   * @throws IOException if there is an error loading the FXML file
   */
  @FXML
  private void confirmCulprit(MouseEvent event) throws IOException {
    // open new pane to confirm culprit
    guessPhotoPane.setVisible(false);
    verifyCulpritPane.setVisible(true);

    // play gif
    staticImage.setVisible(false);
    playgif();

    switch (guessedsuspect) {
      case "Uncle":
        confirmedSuspect1.setVisible(true);
        culpritLabel.setText("The Uncle");
        break;
      case "Grandma":
        guess = true;
        confirmedSuspect2.setVisible(true);
        culpritLabel.setText("The Widow");
        break;
      case "Grandson":
        confirmedSuspect3.setVisible(true);
        culpritLabel.setText("The Grandson");
        break;
    }
  }

  /**
   * Method to handle when the confirm explanation button is clicked
   *
   * @param event the mouse event
   * @throws IOException if there is an error loading the FXML file
   */
  @FXML
  private void confirmExplanation(MouseEvent event) throws IOException {
    // open new pane to confirm explanation
    verifyCulpritPane.setVisible(false);
    staticimg1.setVisible(false);
    gameOverRectangle.setVisible(true);
    gameOverPane.setVisible(true);
    showGameOver();
  }

  /**
   * Method to handle when the game over pane is shown
   *
   * @param event the mouse event
   * @throws IOException if there is an error loading the FXML file
   */
  @FXML
  private void showGameOver() {
    System.out.println("Entered showGameOver");
    list = new ArrayList<>();
    list.add(gameOverTxt);
    if (guess) {
      list.add(correctGuessLbl);
      list.add(reviewLbl);
      list.add(feedbackField);
    } else {
      list.add(incorrectGuessLbl);
      list.add(incorrectGuessLbl2);
    }
    System.out.println("Entered showGameOver");
    timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0.7),
                event -> {
                  if (j < 4) {
                    Object obj = list.get(j);
                    if (obj instanceof Node) {
                      ((Node) obj).setVisible(true); // Make the current element visible
                    }
                    j++;
                  } else {
                    timeline.stop();
                  }
                }));

    timeline.setCycleCount(Timeline.INDEFINITE); // Loop until all text is shown
    timeline.play(); // Start the animation
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
    staticimg1.setFitWidth(700);
    staticimg1.setFitHeight(900);
    staticimg1.setLayoutX(200); // Set X position
    staticimg1.setLayoutY(0); // Set Y position

    // Add the ImageView to the rootPane (or any other container)
    rootPane.getChildren().add(staticimg1);
  }

  private void staticimages() {
    // Ensure the staticimg1 is anchored to all sides of the AnchorPane (rootPane)
    // AnchorPane.setTopAnchor(staticimg1, 0.0);
    // AnchorPane.setBottomAnchor(staticimg1, 0.0);
    // AnchorPane.setLeftAnchor(staticimg1, 0.0);
    // AnchorPane.setRightAnchor(staticimg1, 0.0);

    // // Bind the width and height of the ImageView to match the rootPane's size
    // staticimg1.fitWidthProperty().bind(rootPane.widthProperty());
    // staticimg1.fitHeightProperty().bind(rootPane.heightProperty());

    // // Center the image in the rootPane
    // staticlayer.setFitWidth(rootPane.getWidth());
    // staticlayer.setFitHeight(rootPane.getHeight());

    // // Make sure the background resizes with the window
    // staticlayer.fitWidthProperty().bind(rootPane.widthProperty());
    // staticlayer.fitHeightProperty().bind(rootPane.heightProperty());

    if (context.isGardenToolFound()) {
      clue1foundimg.setVisible(true);
    }

    // backgroundoverlay.toBack();
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
    if (stopTimeline) {
      System.out.println("Should be Stopped");
      gifPlayTimeline.stop();
    }
  }

  private void playgif() {
    // Load the GIF image once
    Image gifImage =
        new Image(
            GuessingController.class.getResource("/images/guessingimages/static.gif").toString(),
            true // Enable background loading for smoother performance
            );

    // Set the GIF to the ImageView
    staticimg1.setImage(gifImage);
    staticimg1.toBack();

    // Make the ImageView visible and set opacity
    staticimg1.setVisible(true);
    staticimg1.setOpacity(0.75); // Adjust opacity as needed
  }
}
