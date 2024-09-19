package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

public class TimerModel {

  // Static fields
  private static int i = 0;
  private static int timerEnded = 0; // Flag to indicate if the timer has ended

    /*
   * Checks if the timer has ended.
   *
   * @return true if the timer has ended, false otherwise
   */
  public static int hasEnded() {
    return timerEnded;
  }

  // Instance fields
  private GameStateContext gameStateContext = GameStateContext.getInstance();
  private TimerModel countdownTimer;
  private Timer timer;
  private int timeInSeconds;
  private StringProperty timeString;

  // Constructors
  public TimerModel(int initialTimeInSeconds) {
    this.timeInSeconds = initialTimeInSeconds;
    this.timeString = new SimpleStringProperty(formatTime(initialTimeInSeconds));
  }

  /*
   * Starts the timer.
   */
  public void start() {
    stop(); // Ensure any existing timer is cancelled before starting a new one
    timer = new Timer(true); // Run timer as a daemon thread
    TimerTask task =
        new TimerTask() {
          @Override
          public void run() {
            if (timeInSeconds > 0) {
              timeInSeconds--;
              Platform.runLater(() -> timeString.set(formatTime(timeInSeconds)));
              System.out.println(timeInSeconds);
            } else {
              timer.cancel();
              Platform.runLater(
                  () -> {
                    if (i == 1
                        || gameStateContext.isGuessPressed()) { // Mark as found in the context
                      timeString.set("Over!");
                      timerEnded++; // Set the flag when the timer ends
                    } else {
                      i++;
                      timeString.set("Time!");
                      timerEnded++; // Set the flag when the timer ends

                      System.out.println(timerEnded);

                      // Create a PauseTransition for the desired delay duration (e.g., 2 seconds)
                      PauseTransition pause = new PauseTransition(Duration.seconds(2));

                      try {
                        App.setRoot("guessingScene");
                      } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                      }
                      pause.setOnFinished(
                          event -> {
                            // Reset and start the timer after the delay
                            countdownTimer = SharedTimerModel.getInstance().getTimer();
                            countdownTimer.reset(61);
                            countdownTimer.start();
                          });

                      // Start the pause transition
                      pause.play();
                    }
                  });
            }
          }
        };
    timer.scheduleAtFixedRate(task, 0, 1000);
  }

  /*
   * Stops the timer.
   */
  public void stop() {
    if (timer != null) {
      timer.cancel();
    }
  }

  /*
   * Resets the timer.
   */
  public void reset(int newTimeInSeconds) {
    stop();
    this.timeInSeconds = newTimeInSeconds;
    timerEnded = 1;
    timeString.set(formatTime(newTimeInSeconds));
  }

  /*
   * Gets the time string property.
   *
   * @return the time string property
   */
  public StringProperty timeStringProperty() {
    return timeString;
  }

  /*
   * Formats the time in seconds to a string.
   *
   * @param totalSeconds the total time in seconds
   * @return the formatted time string
   */
  private String formatTime(int totalSeconds) {
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;
    return String.format("%02d:%02d", minutes, seconds);
  }

  /*
   * Gets the time in seconds.
   *
   * @return the time in seconds
   */
  public void resetI() {
    i = 0;
  }
}
