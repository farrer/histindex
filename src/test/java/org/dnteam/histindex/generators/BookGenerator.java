package org.dnteam.histindex.generators;

import java.sql.Connection;
import java.sql.SQLException;

import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.AuthorManager;
import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.BookAuthor;
import org.dnteam.histindex.database.BookAuthorManager;
import org.dnteam.histindex.database.BookManager;
import org.dnteam.histindex.database.EntityManager;

public class BookGenerator extends EntityGenerator<Book> {
	
	/** The entity itself */
	private Book book = new Book();
	private boolean titleIsSet = false;
	
	/** Define book's title.
	 * @param title new title.
	 * @return this. */
	public BookGenerator withTitle(String title) {
		book.setTitle(title);
		titleIsSet = true;
		return this;
	}
	
	/** Insert an author to the book.
	 * <b>Note:</b> the author/book reation won't persist, unless explicitely called for that.
	 * @param author to insert.
	 * @return this. */
	public BookGenerator withAuthor(Author author) {
		book.addAuthor(author);
		return this;
	}

	@Override
	public EntityManager<Book> getManager() {
		return BookManager.getSingleton();
	}

	@Override
	public Book getEntity() {
		if(!titleIsSet) {
			withTitle("Livro das Hortências");
		}
		return book;
	}

	@Override
	protected void persistRelations(Connection conn) throws SQLException {
		for(Author a : book.getAuthorsCopy()) {
			if(a.getId() == 0) {
				AuthorManager.getSingleton().insert(conn, a);
			}
			BookAuthorManager.getSingleton().insert(conn, new BookAuthor(book, a));
		}
		
	}

}
