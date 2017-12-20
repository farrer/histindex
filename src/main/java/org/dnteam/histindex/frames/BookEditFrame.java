package org.dnteam.histindex.frames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.BookAuthor;
import org.dnteam.histindex.database.BookAuthorManager;
import org.dnteam.histindex.database.BookManager;
import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.QuoteManager;
import org.dnteam.histindex.util.StringUtil;
import org.dnteam.histindex.widgets.AuthorSelector;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/** Edit Frame for Books. 
 * @author farrer */
public class BookEditFrame extends BaseEditFrame<Book> {
	
	private TextField title;
	private AuthorSelector authors;

	public BookEditFrame(Database database, Book entity, BaseListFrame<Book> caller) {
		super(database, "Book / Article", entity, caller);
	}

	/** {@inheritDoc} */
	@Override
	public void addFields(GridPane grid, Book book) {
		
		title = new TextField();
		title.setText(book.getTitle());
		grid.add(new Label("Title: "), 0, 0);
		grid.add(title, 1, 0);
		
		/* Let's try to load available authors */
		try(Connection conn = DriverManager.getConnection(database.getURL())) {
			authors = new AuthorSelector(conn, grid, 0, 1, book.getAuthorsCopy());
		} catch (SQLException e) {
			showError("Couldn't load authors (" + e.getMessage() + ")");
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean populateAndValidateEntity(Book book) {
		if(StringUtil.isEmpty(title.getText())) {
			showError("A book/article should have a title.");
			return false;
		}
		if(authors.getSelected().isEmpty()) {
			showError("A book/article should have at least one author.");
			return false;
		}
		
		book.setTitle(title.getText());
		book.removeAllAuthors();
		for(Author author : authors.getSelected()) {
			book.addAuthor(author);
		}
		
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager<Book> getManager() {
		return BookManager.getSingleton();
	}
	
	/** {@inheritDoc} */
	@Override
	public void doBeforeSave(Connection con, Book book) throws SQLException {
		if(book.getId() != 0) {
			/* Updating a book: must delete its no more in use authors.
			 * Note: deleting all, to avoid incoherences (and simplifying our check). */
			BookAuthorManager.getSingleton().deleteAuthorsFromBook(con, book.getId());
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void doAfterSave(Connection con, Book book) throws SQLException {
		BookAuthorManager baManager = BookAuthorManager.getSingleton();
		/* Let's insert each author */
		for(Author author : authors.getSelected()) {
			BookAuthor ba = new BookAuthor(book, author);
			baManager.insert(con, ba);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void doBeforeDelete(Connection con, Book book) throws SQLException {
		
		/* We should delete all BookAuthor information for this book */
		BookAuthorManager.getSingleton().deleteAuthorsFromBook(con, book.getId());
		
		/* And all Quotes related with this book. */
		QuoteManager.getSingleton().deleteAllQuotesFromBook(con, book.getId());
	}

}
