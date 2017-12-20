package org.dnteam.histindex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.BookAuthor;
import org.dnteam.histindex.database.BookAuthorManager;
import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.generators.AuthorGenerator;
import org.dnteam.histindex.generators.BookGenerator;

public class BookAuthorManagerTestCase extends ComposedEntityManagerTestCase<BookAuthor> {

	public BookAuthorManagerTestCase() throws SQLException {
		super("bookauthortest.db");
	}

	@Override
	protected BookAuthor createEntity() throws SQLException {
		Book book = null;
		Author author = null;
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			AuthorGenerator ag = new AuthorGenerator();
			author = ag.withName("Josualdo").withLastName("Souza Lima").persist(conn);
			BookGenerator bg = new BookGenerator();
			book = bg.withTitle("BMS progressive music").withAuthor(author).persist(conn);
		}
		
		return new BookAuthor(book, author);
	}
	
	@Override
	protected BookAuthor createEntity(Entity first, Entity second) {
		BookAuthor ba =  new BookAuthor();
		ba.setFirstEntity((Book) first);
		ba.setSecondEntity((Author) second);
		return ba;
	}

	@Override
	protected void changeEntityValues(BookAuthor entity) {
      /* Nothing to do, as purely composed */
	}

	@Override
	protected BookAuthorManager getManager() {
		return BookAuthorManager.getSingleton();
	}

	@Override
	protected boolean haveEqualValues(BookAuthor ent1, BookAuthor ent2) {
		/* Id is already checked. */
		return true;
	}

	@Override
	protected Entity createFirstEntity(Connection conn) throws SQLException {
		BookGenerator bg = new BookGenerator(); 
		return bg.persist(conn);
	}

	@Override
	protected Entity createSecondEntity(Connection conn) throws SQLException {
		AuthorGenerator ag = new AuthorGenerator();
		return ag.persist(conn);
	}
	
	public void testDeleteAuthorsFromBook() {
		//TODO
	}
	
	public void testDeleteAuthorFromBooks() {
		//TODO
	}
	
	public void testLoadAuthorsFromBookId() {
		//TODO
	}

}
