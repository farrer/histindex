package org.dnteam.histindex.widgets;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.BookAuthorManager;
import org.dnteam.histindex.database.BookManager;
import org.dnteam.histindex.database.EntityManager;

import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;

/** Widget for selecting 'n' {@link Book}s from the database.
 * @author farrer */
public class BookSelector extends EntitySelector<Book> {

	public BookSelector(Connection conn, GridPane grid, int gridX, int gridY, 
	      ObservableList<Book> current) throws SQLException {
		super(conn, grid, gridX, gridY, "Book/Article", "Book(s)/Article(s)", "description", current, false);
	}

	/** {@inheritDoc} */
	@Override
	protected EntityManager<Book> getEntityManager() {
		return BookManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	protected void loadRelated(Connection conn, List<Book> values) throws SQLException {
		BookAuthorManager.getSingleton().populateAuthors(conn, values);
	}

}
