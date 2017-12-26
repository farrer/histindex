package org.dnteam.histindex.widgets;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.database.EntityManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/** Basic class for creating a selector of 'n' elements of a {@link Entity} from the database.
 * @author farrer */
public abstract class EntitySelector<T extends Entity> {
	
	private ComboBox<T> selector;
	private ObservableList<T> allValues;
	private TableView<T> table;
	private ObservableList<T> curSelected;

	/** Constructor.
	 * @param conn {@link Connection} to use for loading all values from database.
	 * @param grid Grid to add the selector to (it will use 2,2 spaces).
	 * @param gridX x coordinate on grid to add the selector into.
	 * @param gridY y coordinate on grid to add the selector into.
	 * @param entityName name of the {@link Entity} of the selector.
	 * @param entityPlural plural form of the {@link Entity} name.
	 * @param entityFieldName field of the {@link Entity} to show at the table.
	 * @param current current selected entities. if none, pass an empty list.
	 * @throws SQLException */
	public EntitySelector(Connection conn, GridPane grid, int gridX, int gridY, String entityName,
			String entityPlural, String entityFieldName, ObservableList<T> current) throws SQLException {
		
		/* Set our current selected */
		curSelected = current;
		
		/* Load all values */
		allValues = FXCollections.observableArrayList(getEntityManager().loadAll(conn));
		loadRelated(conn, allValues);
		
		/* Create and define our 'selected table' */
		table = new TableView<T>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		TableColumn<T, String> mainCol = new TableColumn<T, String>(entityName);		
		mainCol.setCellValueFactory(new PropertyValueFactory<T, String>(entityFieldName));
		table.getColumns().add(mainCol);
		
		table.setPrefHeight(200);
		
		table.setTooltip(new Tooltip("Select and press 'delete' key to remove it from list."));
		table.setOnKeyReleased(new EventHandler<KeyEvent>() {
		    public void handle(KeyEvent event) {
		        if (event.getCode() == KeyCode.DELETE) {
		      	  removeSelectedFromTable();
		        }
		    }
		});
		table.setOnMousePressed(new EventHandler<MouseEvent>() {
		    public void handle(MouseEvent event) {
		        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
		        	removeSelectedFromTable();
		        }
		    }
		});
		table.setItems(curSelected);
		grid.add(table, gridX, gridY, 2, 1);
		
		selector = new ComboBox<T>(allValues);
		selector.setTooltip(new Tooltip("Select to add it to the list"));
		selector.setOnHidden(new EventHandler<Event>() {
			public void handle(Event event) {
				/* Add the selection to our table */
				T selected = selector.getValue();
				if((selected != null) && (!curSelected.contains(selected))) {
					curSelected.add(selected);
				}
			}
		});
		grid.add(new Label("Add " + entityName + ":"), gridX, gridY + 1);
		grid.add(selector, gridX + 1, gridY + 1);
	}
	
	/** Remove current selected item from table. */
	private void removeSelectedFromTable() {
		T selected = table.getSelectionModel().getSelectedItem();
		if(selected != null) {
			curSelected.remove(selected);
		}
	}
	
	/** Set the width used by the component.
	 * @param x width. */
	public void setWidth(double x) {
		selector.setMinWidth(x);
		selector.setMaxWidth(x);
	}
	
	/** @return current selected {@link Entity}ies. */
	public ObservableList<T> getSelected() {
		return curSelected;
	}
	
	/** @return {@link EntityManager} for the implementor's {@link Entity}. */
	protected abstract EntityManager<T> getEntityManager();
	
	/** Load related needed information for the implementor's {@link Entity} being useful.
	 * For example, for a Book, should load its Authors.
	 * @param conn {@link Connection} to use.
	 * @param values loaded values to load its related information. */
	protected abstract void loadRelated(Connection conn, Collection<T> values) throws SQLException;

}
