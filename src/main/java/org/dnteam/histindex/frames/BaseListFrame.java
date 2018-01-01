package org.dnteam.histindex.frames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.database.EntityManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** A basic class for defining list frames for a {@link Entity}.
 * @author farrer
 *
 * @param T type of the {@link Entity} to list values. */
public abstract class BaseListFrame <T extends Entity> extends BaseFrame {
	
	/** The database to use */
	protected final Database database;
	/** The frame used */
	protected final Stage stage;
	/** The tableview which contains our entities */
	private final TableView<T> table;

	/** Constructor.
	 * @param database database to use.
	 * @param title title of the frame (usually the Entity's name). */
	public BaseListFrame(Database database, String title) {
		this.database = database;
		
		stage = new Stage();
		stage.setTitle(title);
		
		VBox root = new VBox();
		
		table = new TableView<T>();
		table.setTooltip(new Tooltip("Double-click on item to edit it."));
		table.setOnMousePressed(new EventHandler<MouseEvent>() {
		    public void handle(MouseEvent event) {
		        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
		      	  openEditForSelection();
		        }
		    }
		});
		table.setOnKeyReleased(new EventHandler<KeyEvent>() {
		    public void handle(KeyEvent event) {
		        if (event.getCode() == KeyCode.ENTER) {
		      	  openEditForSelection();
		        }
		    }
		});
		populate();
		root.getChildren().addAll(table);
		
		stage.setScene(new Scene(root));
		stage.show();
	}
	
	/** Open its Edit frame for the current selected item on table (if any). */
	private void openEditForSelection() {
		T selected = table.getSelectionModel().getSelectedItem();
		if(selected != null) {
			openEditFrame(selected);
		}
	}
	
	/** Populate the {@link TableView} with {@link Entity} elements.*/
	protected void populate() {
		defineColumns(table);
		try(Connection conn = DriverManager.getConnection(database.getURL())) {
			load(conn);
		} catch (SQLException e) {
			showError("Couldn't load (" + e.getMessage() + ")");
		}	
	}
	
	/** Load our values from database, populating our TableView.
	 * @param conn {@link Connection} to use. */
	public void load(Connection conn) {
		try {
			ObservableList<T> data = FXCollections.observableArrayList(getManager().loadAll(database));
			loadRelated(conn, data);
			table.setItems(data);
		} catch (SQLException e) {
			showError("Couldn't load (" + e.getMessage() + ")");
		}
	}
	
	/** Load related data from the database for the result from default fetchAll query.
	 * This should be overridden by implementors, to allow direct load of related entities,
	 * needed to display at the table (for example, for Books, should use this functions to
	 * load all of its authors).
	 * @param conn connection to use.
	 * @param data list with all loaded entities. */
	protected void loadRelated(Connection conn, ObservableList<T> data) throws SQLException {
	}
	
	/** @return {@link EntityManager} of the implementor {@link Entity}. */
	public abstract EntityManager<T> getManager();
	
	/** Define all columns (but do not populate them) of the {@link TableView} to use.
	 * @param table {@link TableView} to define its columns. */
	protected abstract void defineColumns(TableView<T> table);
	
	/** Open the EditFrame for a selected {@link Entity}.
	 * @param entity instance of {@link Entity} to open its edit frame. */
	protected abstract void openEditFrame(T entity);

}
