package org.dnteam.histindex.frames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.database.EntityManager;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** A basic class for defining {@link Entity} edit (insert/update) frames.
 * @author farrer 
 * 
 * @param T type of the {@link Entity} to edit. */
public abstract class BaseEditFrame<T extends Entity> extends BaseFrame {
	
	/** The database to use */
	protected final Database database;
	/** The frame used */
	protected final Stage stage;
	/** The scene used */
	protected final Scene scene;
	/** If inserting or updating */
	private final T entity;
	/** If someone called us, we should update it after save. */
	private final BaseSelectFrame<T> caller;

	/** Default constructor.
	 * @param title title of the stage to open.
	 * @param entity {@link Entity} to edit, if updating. On inserting just pass null. 
	 * @param caller {@link BaseSelectFrame} instance that called to edit a {@link Entity}, or <code>null</code> 
	 *               if called from menu or shortcut. */
	public BaseEditFrame(Database database, String title, T entity, BaseSelectFrame<T> caller) {
		
		this.database = database;
		this.caller = caller;
		this.entity = (entity == null) ? getManager().createEntity() : entity;
		
		stage = new Stage();
		stage.setTitle(title);
		if(caller != null) {
			stage.initModality(Modality.APPLICATION_MODAL);
		}
		
		BorderPane root = new BorderPane(); 
		
		/* Upper a grid with editable entity elements */
		GridPane grid = new GridPane();
		grid.setVgap(4);
		grid.setPadding(new Insets(5, 5, 5, 5));
		addFields(grid, this.entity);
		root.setTop(grid);
		
		/* Centered bottom with Insert and Cancel Buttons */
		HBox bottom = new HBox();
		bottom.setAlignment(Pos.BOTTOM_CENTER);
		bottom.setSpacing(10);
		
		Button buttonInsert = new Button((this.entity.getId() == 0) ? "Insert" : "Update");
		buttonInsert.setDefaultButton(true);
		buttonInsert.setPrefSize(100, 20);
		buttonInsert.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				onSaveButtonClick();
			}
		});
		
		Button buttonDelete = null;
		if(this.entity.getId() != 0) {
			buttonDelete = new Button("Delete");
			buttonDelete.setPrefSize(100, 20);
			buttonDelete.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent t) {
					onDeleteButtonClick();
				}
			});
		}
		
		
		Button buttonCancel = new Button("Cancel");
		buttonCancel.setCancelButton(true);
		buttonCancel.setPrefSize(100, 20);
		buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				stage.close();
			}
		});
		
		if(this.entity.getId() != 0) {
			bottom.getChildren().addAll(buttonInsert, buttonDelete, buttonCancel);
		}
		else {
			bottom.getChildren().addAll(buttonInsert, buttonCancel);
		}
		root.setBottom(bottom);

		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	/** Save (by insert or update) the current editing entity */
	protected void onSaveButtonClick() {
		
		scene.getRoot().setDisable(true);
		
		/* FIXME: Note that here we aren't doing any concurrency check, as if the user
		 * opened more than one instance for the same entity, the most recently
		 * saved should always persist. It would be a problem if we have two users
		 * accessing the same database and we should do something here if someday
		 * we plan to support it. */
		
		if(populateAndValidateEntity(this.entity)) {
		
			try(Connection conn = DriverManager.getConnection(database.getURL())) {
				doBeforeSave(conn, this.entity);
				if(this.entity.getId() == 0) {
					/* Insert new */
					getManager().insert(conn, this.entity);
				}
				else {
					/* Update existing one */
					getManager().update(conn, this.entity);
				}
				doAfterSave(conn, this.entity);
				/* Close our window */
				stage.close();
				
				/* Update our caller, if any */
				if(caller != null) {
					caller.refresh();
				}
			} catch (SQLException e) {
				showError("Couldn't save (" + e.getMessage() + ")");
			}
		}
		
		scene.getRoot().setDisable(false);
	}
	
	/** Delete the current editing entity */
	protected void onDeleteButtonClick() {
		
		scene.getRoot().setDisable(true);
		
		try(Connection conn = DriverManager.getConnection(database.getURL())) {
			doBeforeDelete(conn, this.entity);
			getManager().delete(conn, this.entity.getId());
			doAfterDelete(conn, this.entity);
			
			/* Close our window */
			stage.close();
				
			/* Update our caller, if any */
			if(caller != null) {
				caller.load(conn);
				caller.refresh();
			}
		} catch (SQLException e) {
			showError("Couldn't delete (" + e.getMessage() + ")");
		}
		
		scene.getRoot().setDisable(false);

	}
	
	/** Add editable elements to the grid, following the style of label at <code>x = 0</code>, 
	 * and editable at <code>x = 1</code>. 
	 * @param grid {@link GridPane} to add elements to.
	 * @param entity current editing entity. */
	public abstract void addFields(GridPane grid, T entity);
	
	/** Populate the {@link Entity} with editing values and check if they are valid for save (insert or update).
	 * @param entity to validate.
	 * @return <code>true</code> if the {@link Entity} could be saved. <code>false</code> if not. */
	public abstract boolean populateAndValidateEntity(T entity);
	
	/** @return {@link EntityManager} of the implementator {@link Entity}. */
	public abstract EntityManager<T> getManager();
	
	/** Called just before the insert/update. Implementors should override this to, for example, delete
	 * no more used relations before update.
	 * @param con connection to use.
	 * @param entity entity that will be saved. */
	public void doBeforeSave(Connection con, T entity) throws SQLException {
	}
	
	/** Called just after the insert/update (but before commit). Implementors should override this to
	 * create and persist all needed relations.
	 * @param con connection to use.
	 * @param entity entity that was saved. */
	public void doAfterSave(Connection con, T entity) throws SQLException {
	}
	
	/** Called just before the delete. Implementors should override this to, for example, delete
	 * all entity relations.
	 * @param con connection to use.
	 * @param entity entity that will be deleted. */
	public void doBeforeDelete(Connection con, T entity) throws SQLException {
	}
	
	/** Called just after the delete.
	 * @param con connection to use.
	 * @param entity entity that was deleted. */
	public void doAfterDelete(Connection con, T entity) throws SQLException {
	}

}
