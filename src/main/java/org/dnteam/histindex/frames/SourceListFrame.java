package org.dnteam.histindex.frames;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Source;
import org.dnteam.histindex.database.SourceManager;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/** Frame for listing {@link Source}s.
 * @author farrer */
public class SourceListFrame extends BaseListFrame<Source> {

	/** Constructor.
	 * @param database database to use. */
	public SourceListFrame(Database database) {
		super(database, "Sources");
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager<Source> getManager() {
		return SourceManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	protected void defineColumns(TableView<Source> table) {
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		TableColumn<Source, String> nameCol = new TableColumn<Source, String>("Title");		
		nameCol.setCellValueFactory(new PropertyValueFactory<Source, String>("title"));
		table.getColumns().add(nameCol);
		
		TableColumn<Source, String> extraCol = new TableColumn<Source, String>("Information");		
		extraCol.setCellValueFactory(new PropertyValueFactory<Source, String>("extraInfo"));
		table.getColumns().add(extraCol);
	}

	/** {@inheritDoc} */
	@Override
	protected void openEditFrame(Source entity) {
		new SourceEditFrame(database, entity, this);
	}

}
