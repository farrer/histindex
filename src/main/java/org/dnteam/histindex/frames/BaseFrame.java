package org.dnteam.histindex.frames;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/** The base class for all frames */
public class BaseFrame {

	/** Dislay an error message at an Alert.
	 * @param errorMessage message to display. */
	protected void showError(String errorMessage) {
		Alert alert = new Alert(AlertType.ERROR, errorMessage);
		alert.showAndWait();
	}

}
