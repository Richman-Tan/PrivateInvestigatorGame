package nz.ac.auckland.se206.utils;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import nz.ac.auckland.se206.controllers.SharedVolumeControl;

public class VolumeControlUtil {

  private SVGPath volumeUpStroke;
  private SVGPath volumeUp;
  private SVGPath volumeOff;
  private Pane timerPane;

  public VolumeControlUtil(Pane timerPane) {
    this.timerPane = timerPane;
    volumeUpStroke = new SVGPath();
    volumeUp = new SVGPath();
    volumeOff = new SVGPath();
  }

  /*
   * Method to initialise and show the volume button
   */
  public void showVolumeButton() {
    // Create new SVGPath for volume button
    volumeUpStroke.setContent(
        "M10.121 12.596A6.48 6.48 0 0 0 12.025 8a6.48 6.48 0 0 0-1.904-4.596l-.707.707A5.48 5.48 0"
            + " 0 1 11.025 8a5.48 5.48 0 0 1-1.61 3.89z");
    volumeUp.setContent(
        "M8.707 11.182A4.5 4.5 0 0 0 10.025 8a4.5 4.5 0 0 0-1.318-3.182L8 5.525A3.5 3.5 0 0 1 9.025"
            + " 8 3.5 3.5 0 0 1 8 10.475zM6.717 3.55A.5.5 0 0 1 7 4v8a.5.5 0 0 1-.812.39L3.825"
            + " 10.5H1.5A.5.5 0 0 1 1 10V6a.5.5 0 0 1 .5-.5h2.325l2.363-1.89a.5.5 0 0 1 .529-.06");
    volumeOff.setContent(
        "M6.717 3.55A.5.5 0 0 1 7 4v8a.5.5 0 0 1-.812.39L3.825 10.5H1.5A.5.5 0 0 1 1 10V6a.5.5 0 0"
            + " 1 .5-.5h2.325l2.363-1.89a.5.5 0 0 1 .529-.06m7.137 2.096a.5.5 0 0 1 0 .708L12.207"
            + " 8l1.647 1.646a.5.5 0 0 1-.708.708L11.5 8.707l-1.646 1.647a.5.5 0 0"
            + " 1-.708-.708L10.793 8 9.146 6.354a.5.5 0 1 1 .708-.708L11.5 7.293l1.646-1.647a.5.5 0"
            + " 0 1 .708 0");

    // Setup volume buttons
    setupVolumeButton(volumeUp, 13, 53, event -> turnVolumeOff());
    setupVolumeButton(volumeUpStroke, 19, 53, event -> turnVolumeOff());
    setupVolumeButton(volumeOff, 13, 53, event -> turnVolumeOn());

    // Check if the volume icon should be displayed
    checkVolumeIcon();
  }

  private void setupVolumeButton(
      SVGPath volumeButton, double layoutX, double layoutY, EventHandler<MouseEvent> onClick) {
    volumeButton.setScaleY(2.0);
    volumeButton.setScaleX(2.0);
    volumeButton.setScaleZ(2.0);
    volumeButton.setLayoutX(layoutX);
    volumeButton.setLayoutY(layoutY);
    volumeButton.setStroke(Color.web("#473931"));
    volumeButton.setFill(Color.web("#ffffff94"));
    volumeButton.setStrokeWidth(0.5);
    volumeButton.setOnMouseClicked(onClick);
    timerPane.getChildren().add(volumeButton);
  }

  /*
   * Method to turn the volume off
   */
  public void turnVolumeOff() {
    SharedVolumeControl.getInstance().setVolumeSetting(false);
    volumeOff.setVisible(true);
    volumeUp.setVisible(false);
    volumeUpStroke.setVisible(false);
  }

  /*
   * Method to turn the volume on
   */
  public void turnVolumeOn() {
    SharedVolumeControl.getInstance().setVolumeSetting(true);
    volumeOff.setVisible(false);
    volumeUp.setVisible(true);
    volumeUpStroke.setVisible(true);
  }

  /*
   * Method to check if the volume icon should be displayed
   */
  private void checkVolumeIcon() {
    if (SharedVolumeControl.getInstance().getVolumeSetting()) {
      turnVolumeOn();
    } else {
      turnVolumeOff();
    }
  }
}
