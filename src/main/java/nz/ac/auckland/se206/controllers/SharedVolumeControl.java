package nz.ac.auckland.se206.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SharedVolumeControl {

  private static SharedVolumeControl instance;
  private BooleanProperty volumeSetting = new SimpleBooleanProperty(true);

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

  public BooleanProperty volumeSettingProperty() {
    return volumeSetting;
  }

  /**
   * Gets the volume control.
   *
   * @return the volume control
   */
  public boolean getVolumeSetting() {
    if (volumeSetting.get()) {
      return true;
    } else {
      return false;
    }
  }

  public void setVolumeSetting(boolean volumeOn) {
    this.volumeSetting.set(volumeOn);
  }
}
