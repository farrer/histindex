package org.dnteam.histindex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Keyword;
import org.dnteam.histindex.database.Quote;
import org.dnteam.histindex.database.QuoteManager;
import org.dnteam.histindex.database.Source;
import org.dnteam.histindex.generators.BookGenerator;
import org.dnteam.histindex.generators.QuoteGenerator;
import org.dnteam.histindex.generators.SourceGenerator;
import org.dnteam.histindex.util.StringUtil;

/** Test case of {@link QuoteManager}.
 * @author farrer */
public class QuoteManagerTestCase extends EntityManagerTestCase<Quote> {

	public QuoteManagerTestCase() throws SQLException {
		super("quotetest.db");
	}

	/** {@inheritDoc} */
	@Override
	protected Quote createEntity() {
		QuoteGenerator qg = new QuoteGenerator();
		return qg.getEntity();
	}

	/** {@inheritDoc} */
	@Override
	protected void changeEntityValues(Quote quote) {
		quote.setPage("32");
		quote.setText("Another quoted text");
		quote.setComment("This should be useful.");	
	}
	
	/** {@inheritDoc} */
	@Override
	protected EntityManager<Quote> getManager() {
		return QuoteManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	protected boolean haveEqualValues(Quote ent1, Quote ent2) {
		return ent1.getBookId() == ent2.getBookId() && ent1.getSourceId() == ent2.getSourceId() && 
				StringUtil.isEqual(ent1.getText(), ent2.getText()) && 
				StringUtil.isEqual(ent1.getPage(), ent2.getPage()) &&
				StringUtil.isEqual(ent1.getComment(), ent2.getComment());
	}
	
	/** Test for function {@link QuoteManager#deleteAllQuotesFromBook}.
	 * @throws SQLException */
	public void testDeleteAllQuotesFromBook() throws SQLException {
		Book bookA;
		Book bookB;
		Source sourceA;
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			bookA = new BookGenerator().persist(conn);
			bookB = new BookGenerator().persist(conn);
			
			new QuoteGenerator().withBook(bookA).persist(conn);
			new QuoteGenerator().withBook(bookA).persist(conn);
			new QuoteGenerator().withBook(bookA).persist(conn);
			
			new QuoteGenerator().withBook(bookB).persist(conn);
			new QuoteGenerator().withBook(bookB).persist(conn);
			
			sourceA = new SourceGenerator().persist(conn);
			new QuoteGenerator().withSource(sourceA).persist(conn);
			new QuoteGenerator().withSource(sourceA).persist(conn);
			new QuoteGenerator().withSource(sourceA).persist(conn);
			new QuoteGenerator().withSource(sourceA).persist(conn);
		}
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			int count = QuoteManager.getSingleton().deleteAllQuotesFromBook(conn, bookA.getId());
			assert(count == 3);
		}
		
		/* Check with all other quotes from other books or sources remained intact. */
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			ArrayList<Book> bookList = new ArrayList<Book>();
			bookList.add(bookA);
			Collection<Quote> quotes = QuoteManager.getSingleton().search(conn, 
					new ArrayList<Keyword>(), bookList,	new ArrayList<Author>(), new ArrayList<Source>(), "");
			assert(quotes.size() == 0);
			
			bookList.clear();
			bookList.add(bookB);
			quotes = QuoteManager.getSingleton().search(conn, 
					new ArrayList<Keyword>(), bookList,	new ArrayList<Author>(), new ArrayList<Source>(), "");
			assert(quotes.size() == 2);
			
			ArrayList<Source> sourceList = new ArrayList<Source>();
			sourceList.add(sourceA);
			quotes = QuoteManager.getSingleton().search(conn, 
					new ArrayList<Keyword>(), new ArrayList<Book>(),	new ArrayList<Author>(), sourceList, "");
			assert(quotes.size() == 4);
		}
	}
	
	/** Test for function {@link QuoteManager#deleteAllQuotesFromSource(Connection, long)}.
	 * @throws SQLException */
	public void testDeleteAllQuotesFromSource() throws SQLException {
		Book bookA;
		Source sourceA;
		Source sourceB;
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			bookA = new BookGenerator().persist(conn);			
			new QuoteGenerator().withBook(bookA).persist(conn);
			new QuoteGenerator().withBook(bookA).persist(conn);
			new QuoteGenerator().withBook(bookA).persist(conn);

			sourceA = new SourceGenerator().persist(conn);
			new QuoteGenerator().withSource(sourceA).persist(conn);
			new QuoteGenerator().withSource(sourceA).persist(conn);
			new QuoteGenerator().withSource(sourceA).persist(conn);
			new QuoteGenerator().withSource(sourceA).persist(conn);
			
			sourceB = new SourceGenerator().persist(conn);
			new QuoteGenerator().withSource(sourceB).persist(conn);
			new QuoteGenerator().withSource(sourceB).persist(conn);
			new QuoteGenerator().withSource(sourceB).persist(conn);
		}
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			int count = QuoteManager.getSingleton().deleteAllQuotesFromSource(conn, sourceA.getId());
			assert(count == 4);
		}
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			
			ArrayList<Book> bookList = new ArrayList<Book>();
			bookList.add(bookA);
			Collection<Quote> quotes = QuoteManager.getSingleton().search(conn, 
					new ArrayList<Keyword>(), bookList,	new ArrayList<Author>(), new ArrayList<Source>(), "");
			assert(quotes.size() == 3);
			
			ArrayList<Source> sourceList = new ArrayList<Source>();
			sourceList.add(sourceA);
			quotes = QuoteManager.getSingleton().search(conn, 
					new ArrayList<Keyword>(), new ArrayList<Book>(),	new ArrayList<Author>(), sourceList, "");
			assert(quotes.size() == 0);
			
			sourceList.clear();
			sourceList.add(sourceB);
			quotes = QuoteManager.getSingleton().search(conn, 
					new ArrayList<Keyword>(), new ArrayList<Book>(),	new ArrayList<Author>(), sourceList, "");
			assert(quotes.size() == 3);
		}
	}
	
	public void testSearch() throws SQLException {
		//TODO
	}
	
	public void testPopulateRelatedInfo() throws SQLException {
		//TODO
	}

}
