package org.dnteam.histindex.widgets;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.database.EntityManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
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
	private ToggleGroup andOrToggle = null;
	private RadioButton andRadioButton;
	private RadioButton orRadioButton;

	/** Constructor.
	 * @param conn {@link Connection} to use for loading all values from database.
	 * @param grid Grid to add the selector to (it will use 2,2 spaces).
	 * @param gridX x coordinate on grid to add the selector into.
	 * @param gridY y coordinate on grid to add the selector into.
	 * @param entityName name of the {@link Entity} of the selector.
	 * @param entityPlural plural form of the {@link Entity} name.
	 * @param entityFieldName field of the {@link Entity} to show at the table.
	 * @param current current selected entities. if none, pass an empty list.
	 * @param andOrRadio true to use a "AND or OR" radio button.
	 * @throws SQLException */
	public EntitySelector(Connection conn, GridPane grid, int gridX, int gridY, String entityName,
			String entityPlural, String entityFieldName, ObservableList<T> current,
			boolean andOrRadio) throws SQLException {
		
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
		
		int rowSpan;
		if(!andOrRadio) {
			table.setPrefHeight(200);
			rowSpan = 2;
		} else {
			table.setPrefHeight(180);
			rowSpan = 1;
		}
		
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
		grid.add(table, gridX, gridY, 2, rowSpan);
		
		if(andOrRadio) {
			andOrToggle = new ToggleGroup();
		    andRadioButton = new RadioButton("All");
		    andRadioButton.setToggleGroup(andOrToggle);
		    orRadioButton = new RadioButton("Any");
		    orRadioButton.setToggleGroup(andOrToggle);
		    orRadioButton.setSelected(true);
		    grid.add(andRadioButton, gridX, gridY + 1);
			grid.add(orRadioButton, gridX + 1, gridY + 1);
		}
		
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
		grid.add(new Label("Add " + entityName + ":"), gridX, gridY + 2);
		grid.add(selector, gridX + 1, gridY + 2);
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
	
	/** @return if radio button AND is selected */
	public boolean isAndSelected() {
		if(andOrToggle != null) {
			return andRadioButton.isSelected();
		}
		
		return false;
	}
	
	/** @return if radio button OR is selected */
	public boolean isOrSelected() {
		if(andOrToggle != null) {
			return orRadioButton.isSelected();
		}
		
		return true;
	}
	
	/** Enable the selector for use. */
	public void enable() {
		selector.setDisable(false);
		table.setDisable(false);
		if(andOrToggle != null) {
			andRadioButton.setDisable(false);
			orRadioButton.setDisable(false);
		}
	}
	
	/** Enable the selector for use. */
	public void disable() {
		selector.setDisable(true);
		table.setDisable(true);
		if(andOrToggle != null) {
			andRadioButton.setDisable(true);
			orRadioButton.setDisable(true);
		}
	}
	
	/** @return {@link EntityManager} for the implementor's {@link Entity}. */
	protected abstract EntityManager<T> getEntityManager();
	
	/** Load related needed information for the implementor's {@link Entity} being useful.
	 * For example, for a Book, should load its Authors.
	 * @param conn {@link Connection} to use.
	 * @param values loaded values to load its related information. */
	protected abstract void loadRelated(Connection conn, List<T> values) throws SQLException;

}
