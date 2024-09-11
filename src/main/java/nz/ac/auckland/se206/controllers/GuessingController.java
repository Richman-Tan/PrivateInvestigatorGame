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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.speech.FreeTextToSpeech;

public class GuessingController {

  @FXML private AnchorPane rootPane;
  @FXML private ImageView background;

  @FXML private TextArea txtaChat;
  @FXML private TextField txtInput;
  @FXML private Button btnSend;

  private ChatCompletionRequest chatCompletionRequest;

  /**
   * Initializes the chat view.
   *
   * @throws URISyntaxException
   * @throws IOException
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() {
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
}
