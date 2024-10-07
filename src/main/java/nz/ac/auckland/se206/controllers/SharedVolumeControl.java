package nz.ac.auckland.se206.controllers;

public class SharedVolumeControl {

  private static SharedVolumeControl instance;
  private static boolean volumeShouldBeOn = true;

  /** Constructs a new shared volume control. */
  private SharedVolumeControl() {}

  /**
   * Gets the instance of the shared volume control.
   *
   * @return the instance of the shared volume control
   */
  public static SharedVolumeControl getInstance() {
    if (instance == null) {
      instance = new SharedVolumeControl();
    }
    return instance;
  }

  /**
   * Gets the volume control.
   *
   * @return the volume control
   */
  public boolean getVolumeSetting() {
    if (volumeShouldBeOn) {
      return true;
    } else {
      return false;
    }
  }

  public void setVolumeSetting(boolean volumeShouldBeOn) {
    SharedVolumeControl.volumeShouldBeOn = volumeShouldBeOn;
  }
}
