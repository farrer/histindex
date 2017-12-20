package org.dnteam.histindex.frames;

import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.AuthorManager;
import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.EntityManager;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/** Frame for listing authors.
 * @author farrer */
public class AuthorListFrame extends BaseListFrame<Author> {

	/** Constructor.
	 * @param database database to use. */
	public AuthorListFrame(Database database) {
		super(database, "Authors");
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager<Author> getManager() {
		return AuthorManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	protected void defineColumns(TableView<Author> table) {
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		TableColumn<Author, String> nameCol = new TableColumn<Author, String>("Author");		
		nameCol.setCellValueFactory(new PropertyValueFactory<Author, String>("fullName"));
		table.getColumns().add(nameCol);
	}

	/** {@inheritDoc} */
	@Override
	protected void openEditFrame(Author entity) {
		new AuthorEditFrame(database, entity, this);
	}

}
