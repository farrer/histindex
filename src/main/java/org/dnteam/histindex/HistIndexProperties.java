package org.dnteam.histindex;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class HistIndexProperties {

	private static Properties properties = new Properties();
	
	public static void init() {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("histindex.properties");
		if(stream != null) {
			try { 
				properties.load(stream);
				stream.close();
			}
			catch(IOException e) {
				Alert alert = new Alert(AlertType.ERROR, "Couldn't load properties file!");
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR, "Couldn't load properties file!");
			alert.showAndWait();
		}
	}
	
	/** @return HistIndex version */
	public static String getVersion() {
		return properties.getProperty("histindex.version");
	}
}
