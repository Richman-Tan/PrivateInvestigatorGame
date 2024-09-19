package nz.ac.auckland.se206.controllers;

public class SharedTimerModel {

  private TimerModel countdownTimer;
  private static SharedTimerModel instance;

  /** Constructs a new shared timer model. */
  private SharedTimerModel() {
    countdownTimer = new TimerModel(301); // Set the initial time
  }

  /**
   * Gets the instance of the shared timer model.
   *
   * @return the instance of the shared timer model
   */
  public static SharedTimerModel getInstance() {
    if (instance == null) {
      instance = new SharedTimerModel();
    }
    return instance;
  }

  /**
   * Gets the timer model.
   *
   * @return the timer model
   */
  public TimerModel getTimer() {
    return countdownTimer;
  }

  /** Resets the timer. */
  public void resetTimer() {
    countdownTimer = new TimerModel(301);
  }
}
