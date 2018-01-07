package org.dnteam.histindex.frames;

import java.sql.Connection;

import org.dnteam.histindex.database.Entity;

/** Basic frame for selection of a Entity of a list of entities to edit it with a {@link BaseEditFrame}.
 * @author farrer */
public abstract class BaseSelectFrame<T extends Entity> extends BaseFrame {
	
	/** Open its Edit frame for the current selected item (if any).
	 * @param selected {@link Entity} to open its edit frame. If <code>null</code> will not do anything. */
	protected void openEditForSelection(T selected) {
		if(selected != null) {
			openEditFrame(selected);
		}
	}
	
	/** Load (or reload) our values from database, populating our TableView.
	 * @param conn {@link Connection} to use. */
	public abstract void load(Connection conn);
	
	/** Refresh the list of elements for selection. */
	public abstract void refresh();
	
	/** Open the EditFrame for a selected {@link Entity}.
	 * @param entity instance of {@link Entity} to open its edit frame. */
	protected abstract void openEditFrame(T entity);

}
