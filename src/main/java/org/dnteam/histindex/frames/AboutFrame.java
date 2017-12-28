package org.dnteam.histindex.frames;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Frame with information about the project (licenses).
 * @author farrer */
public class AboutFrame extends BaseFrame {

	/** Constructor.
	 * @param hostServices Application's {@link HostServices} instance. */
	public AboutFrame(HostServices hostServices) {
		Stage stage = new Stage();
		stage.setTitle("About HistIndex");
		
		VBox root = new VBox();
		
		addInfo(root, "Developer:", "Farrer <farrer@dnteam.org>");
		addInfo(root, "Code license:", "MIT");
		addInfo(root, "Icons:", "Mark James");
		addLink(root, hostServices, "Icons source:", "http://www.famfamfam.com/lab/icons/silk");
		addInfo(root, "Icons license:", "CC-By-SA 3.0");
		
		stage.setScene(new Scene(root));
	    stage.show();
	}
	
	/** Add a textual information to the frame. */
	private void addInfo(VBox root, String title, String info) {
		HBox hoz = new HBox();
		hoz.setSpacing(10);
		
		Label lblTitle = new Label(title);
		lblTitle.setDisable(true);
		Label lblInfo = new Label(info);
		
		hoz.getChildren().addAll(lblTitle, lblInfo);
		
		root.getChildren().add(hoz);
	}
	
	/** Add a link information to the frame. */
	private void addLink(VBox root, HostServices hostServices, String title, String link) {
		HBox hoz = new HBox();
		hoz.setSpacing(10);
		
		Label lblTitle = new Label(title);
		lblTitle.setDisable(true);
		
		Hyperlink lnk = new Hyperlink(link);
		
		/* Set to open link on default browser. */
		lnk.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				hostServices.showDocument(link);
			}});
		lnk.setBorder(null);
		
		hoz.getChildren().addAll(lblTitle, lnk);
		
		root.getChildren().add(hoz);
	}
}
