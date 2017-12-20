package org.dnteam.histindex.frames;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Keyword;
import org.dnteam.histindex.database.KeywordManager;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/** Frame for listing all keywords.
 * @author farrer */
public class KeywordListFrame extends BaseListFrame<Keyword> {

	public KeywordListFrame(Database database) {
		super(database, "Keywords");
	}

	/** {@inheritDoc}	 */
	@Override
	public EntityManager<Keyword> getManager() {
		return KeywordManager.getSingleton();
	}

	/** {@inheritDoc}	 */
	@Override
	protected void defineColumns(TableView<Keyword> table) {
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		TableColumn<Keyword, String> nameCol = new TableColumn<Keyword, String>("Name");		
		nameCol.setCellValueFactory(new PropertyValueFactory<Keyword, String>("name"));
		table.getColumns().add(nameCol);
	}

	/** {@inheritDoc}	 */
	@Override
	protected void openEditFrame(Keyword entity) {
		new KeywordEditFrame(database, entity, this);
	}

}
