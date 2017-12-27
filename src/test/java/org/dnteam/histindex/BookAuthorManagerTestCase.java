package org.dnteam.histindex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.BookAuthor;
import org.dnteam.histindex.database.BookAuthorManager;
import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.generators.AuthorGenerator;
import org.dnteam.histindex.generators.BookGenerator;

/** TestCase for {@link BookAuthorManager}.
 * @author farrer */
public class BookAuthorManagerTestCase extends ComposedEntityManagerTestCase<BookAuthor> {

	public BookAuthorManagerTestCase() throws SQLException {
		super("bookauthortest.db");
	}

	/** {@inheritDoc} */
	@Override
	protected BookAuthor createEntity() throws SQLException {
		Book book = null;
		Author author = null;
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			AuthorGenerator ag = new AuthorGenerator();
			author = ag.withName("Josualdo").withLastName("Souza Lima").persist(conn);
			BookGenerator bg = new BookGenerator();
			book = bg.withTitle("BMS progressive music").persist(conn);
		}
		book.addAuthor(author);
		
		return new BookAuthor(book, author);
	}
	
	/** {@inheritDoc} */
	@Override
	protected BookAuthor createEntity(Entity first, Entity second) {
		BookAuthor ba =  new BookAuthor();
		ba.setFirstEntity((Book) first);
		ba.setSecondEntity((Author) second);
		return ba;
	}

	/** {@inheritDoc} */
	@Override
	protected void changeEntityValues(BookAuthor entity) {
      /* Nothing to do, as purely composed */
	}

	/** {@inheritDoc} */
	@Override
	protected BookAuthorManager getManager() {
		return BookAuthorManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	protected boolean haveEqualValues(BookAuthor ent1, BookAuthor ent2) {
		/* Id is already checked. */
		return true;
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createFirstEntity(Connection conn) throws SQLException {
		BookGenerator bg = new BookGenerator(); 
		return bg.persist(conn);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createSecondEntity(Connection conn) throws SQLException {
		AuthorGenerator ag = new AuthorGenerator();
		return ag.persist(conn);
	}
	
	/** Test for function {@link BookAuthorManager#deleteAuthorsFromBook(Connection, long)}.
	 * @throws SQLException */
	public void testDeleteAuthorsFromBook() throws SQLException {
		Author authorA, authorB, authorC;
		Book bookA, bookB;
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			authorA = new AuthorGenerator().withLastName("A").withName("Author").persist(conn);
			authorB = new AuthorGenerator().withLastName("B").withName("Author").persist(conn);
			authorC = new AuthorGenerator().withLastName("C").withName("Author").persist(conn);
			
			bookA = new BookGenerator().withAuthor(authorA).withAuthor(authorB).withAuthor(authorC).persist(conn);
			bookB = new BookGenerator().withAuthor(authorB).withAuthor(authorC).persist(conn);
		}
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			int res = BookAuthorManager.getSingleton().deleteAuthorsFromBook(conn, bookA.getId());
			assert(res == 3);
			
			bookB.removeAllAuthors();
			BookAuthorManager.getSingleton().populateAuthors(conn, bookB);
			assert(bookB.getAuthorsCopy().size() == 2);
			assert(bookB.getAuthorsCopy().get(0).getId() == authorB.getId() || 
					bookB.getAuthorsCopy().get(0).getId() == authorC.getId());
			assert(bookB.getAuthorsCopy().get(1).getId() == authorB.getId() || 
					bookB.getAuthorsCopy().get(1).getId() == authorC.getId());
		}
	}
	
	/** Test for function {@link BookAuthorManager#deleteAuthorFromBooks(Connection, long)}.
	 * @throws SQLException */
	public void testDeleteAuthorFromBooks() throws SQLException {
		Author authorA, authorB, authorC;
		Book bookA, bookB;
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			authorA = new AuthorGenerator().withLastName("A").withName("Author").persist(conn);
			authorB = new AuthorGenerator().withLastName("B").withName("Author").persist(conn);
			authorC = new AuthorGenerator().withLastName("C").withName("Author").persist(conn);
			
			bookA = new BookGenerator().withAuthor(authorA).withAuthor(authorB).withAuthor(authorC).persist(conn);
			bookB = new BookGenerator().withAuthor(authorB).withAuthor(authorC).persist(conn);
		}
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			int res = BookAuthorManager.getSingleton().deleteAuthorFromBooks(conn, authorB.getId());
			assert(res == 2);
			
			bookB.removeAllAuthors();
			BookAuthorManager.getSingleton().populateAuthors(conn, bookB);
			assert(bookB.getAuthorsCopy().size() == 1);
			assert(bookB.getAuthorsCopy().get(0).getId() == authorC.getId());
			
			bookA.removeAllAuthors();
			BookAuthorManager.getSingleton().populateAuthors(conn, bookA);
			assert(bookA.getAuthorsCopy().size() == 2);
			assert(bookA.getAuthorsCopy().get(0).getId() == authorA.getId());
		}
	}
	
	/** Test for function {@link BookAuthorManager#populateAuthors(Connection, Book)}
	 * @throws SQLException */
	public void testPopulateAuthorsForBook() throws SQLException {
		Author authorA, authorB;
		Book bookA, bookB;
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			authorA = new AuthorGenerator().withLastName("A").withName("Author").persist(conn);
			authorB = new AuthorGenerator().withLastName("B").withName("Author").persist(conn);
			
			bookA = new BookGenerator().withAuthor(authorA).withAuthor(authorB).persist(conn);
			bookB = new BookGenerator().withAuthor(authorB).persist(conn);
		}
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			bookA.removeAllAuthors();
			BookAuthorManager.getSingleton().populateAuthors(conn, bookA);
			assert(bookA.getAuthorsCopy().size() == 2);
			assert(bookA.getAuthorsCopy().get(0).getId() == authorB.getId() || 
					bookA.getAuthorsCopy().get(0).getId() == authorA.getId());
			assert(bookA.getAuthorsCopy().get(1).getId() == authorB.getId() || 
					bookA.getAuthorsCopy().get(1).getId() == authorA.getId());
			
			bookB.removeAllAuthors();
			BookAuthorManager.getSingleton().populateAuthors(conn, bookB);
			assert(bookB.getAuthorsCopy().size() == 1);
			assert(bookB.getAuthorsCopy().get(0).getId() == authorB.getId());
		}
	}
	
	/** Test for function {@link BookAuthorManager#populateAuthors(Connection, java.util.Collection)}.
	 * @throws SQLException */
	public void testPopulateAuthorsForBooks() throws SQLException {
		Author authorA, authorB;
		Book bookA, bookB;
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			authorA = new AuthorGenerator().withLastName("A").withName("Author").persist(conn);
			authorB = new AuthorGenerator().withLastName("B").withName("Author").persist(conn);
			
			bookA = new BookGenerator().withAuthor(authorA).withAuthor(authorB).persist(conn);
			bookB = new BookGenerator().withAuthor(authorB).persist(conn);
		}
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			bookA.removeAllAuthors();
			bookB.removeAllAuthors();
			List<Book> books = new ArrayList<Book>(2);
			books.add(bookA);
			books.add(bookB);
			BookAuthorManager.getSingleton().populateAuthors(conn, books);
			assert(bookA.getAuthorsCopy().size() == 2);
			assert(bookA.getAuthorsCopy().get(0).getId() == authorB.getId() || 
					bookA.getAuthorsCopy().get(0).getId() == authorA.getId());
			assert(bookA.getAuthorsCopy().get(1).getId() == authorB.getId() || 
					bookA.getAuthorsCopy().get(1).getId() == authorA.getId());
			assert(bookB.getAuthorsCopy().size() == 1);
			assert(bookB.getAuthorsCopy().get(0).getId() == authorB.getId());
		}
	}

}
