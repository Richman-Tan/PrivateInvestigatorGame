package nz.ac.auckland.se206.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SharedVolumeControl {

  private static SharedVolumeControl instance;

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

  private final BooleanProperty volumeSetting = new SimpleBooleanProperty(true);

  /** Constructs a new shared volume control. */
  private SharedVolumeControl() {}

  /**
   * Gets the volume control property.
   *
   * @return the volume control property
   */
  public BooleanProperty volumeSettingProperty() {
    return volumeSetting;
  }

  /**
   * Gets the volume control.
   *
   * @return the volume control
   */
  public boolean getVolumeSetting() {
    return volumeSetting.get();
  }

  /**
   * Sets the volume control.
   *
   * @param volumeOn the volume control
   */
  public void setVolumeSetting(boolean volumeOn) {
    this.volumeSetting.set(volumeOn);
  }
}
