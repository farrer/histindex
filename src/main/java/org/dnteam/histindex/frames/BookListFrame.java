package org.dnteam.histindex.frames;

import java.sql.Connection;
import java.sql.SQLException;

import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.BookAuthorManager;
import org.dnteam.histindex.database.BookManager;
import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.EntityManager;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/** List frame for {@link Book}s.
 * @author farrer */
public class BookListFrame extends BaseListFrame<Book> {

	/** Constructor.
	 * @param database database to use. */
	public BookListFrame(Database database) {
		super(database, "Books / Articles");
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager<Book> getManager() {
		return BookManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	protected void defineColumns(TableView<Book> table) {
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setPrefWidth(600);
		
		TableColumn<Book, String> titleCol = new TableColumn<Book, String>("Title");		
		titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
		table.getColumns().add(titleCol);
		
		TableColumn<Book, String> authorsCol = new TableColumn<Book, String>("Author(s)");		
		authorsCol.setCellValueFactory(new PropertyValueFactory<Book, String>("authors"));
		table.getColumns().add(authorsCol);
	}

	/** {@inheritDoc} */
	@Override
	protected void openEditFrame(Book entity) {
		new BookEditFrame(database, entity, this);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void loadRelated(Connection conn, ObservableList<Book> data) throws SQLException {
		BookAuthorManager.getSingleton().populateAuthors(conn, data);
	}

}
