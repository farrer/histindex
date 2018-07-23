package org.dnteam.histindex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Keyword;
import org.dnteam.histindex.database.Quote;
import org.dnteam.histindex.database.QuoteManager;
import org.dnteam.histindex.database.Source;
import org.dnteam.histindex.generators.AuthorGenerator;
import org.dnteam.histindex.generators.BookGenerator;
import org.dnteam.histindex.generators.KeywordGenerator;
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
					new ArrayList<Keyword>(), false, true, bookList, new ArrayList<Author>(), 
					true, new ArrayList<Source>(), "");
			assert(quotes.size() == 0);
			
			bookList.clear();
			bookList.add(bookB);
			quotes = QuoteManager.getSingleton().search(conn, 
					new ArrayList<Keyword>(), false, true, bookList, new ArrayList<Author>(), 
					true, new ArrayList<Source>(), "");
			assert(quotes.size() == 2);
			
			ArrayList<Source> sourceList = new ArrayList<Source>();
			sourceList.add(sourceA);
			quotes = QuoteManager.getSingleton().search(conn, 
					new ArrayList<Keyword>(), false, true, new ArrayList<Book>(), new ArrayList<Author>(),
					true, sourceList, "");
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
					new ArrayList<Keyword>(), false, true, bookList, new ArrayList<Author>(), 
					true, new ArrayList<Source>(), "");
			assert(quotes.size() == 3);
			
			ArrayList<Source> sourceList = new ArrayList<Source>();
			sourceList.add(sourceA);
			quotes = QuoteManager.getSingleton().search(conn, 
					new ArrayList<Keyword>(), false, true, new ArrayList<Book>(), new ArrayList<Author>(),
					true, sourceList, "");
			assert(quotes.size() == 0);
			
			sourceList.clear();
			sourceList.add(sourceB);
			quotes = QuoteManager.getSingleton().search(conn, 
					new ArrayList<Keyword>(), false, true, new ArrayList<Book>(), new ArrayList<Author>(), 
					true, sourceList, "");
			assert(quotes.size() == 3);
		}
	}
	
	/** Test for function {@link QuoteManager#search}.
	 * @throws SQLException	 */
	public void testSearch() throws SQLException {
		
		Keyword ivory, gold, mutapa;
		Author authorA, authorB, authorC, authorD;
		Book bookA, bookB, bookC;
		Source sourceA, sourceB;
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			/* Define some keywords */
			ivory = new KeywordGenerator().withName("ivory").persist(conn);
			gold = new KeywordGenerator().withName("gold").persist(conn);
			mutapa = new KeywordGenerator().withName("Mutapa").persist(conn);
			
			/* Define some authors */
			authorA = new AuthorGenerator().withLastName("A").withName("Author").persist(conn);
			authorB = new AuthorGenerator().withLastName("B").withName("Author").persist(conn);
			authorC = new AuthorGenerator().withLastName("C").withName("Author").persist(conn);
			authorD = new AuthorGenerator().withLastName("D").withName("Author").persist(conn);
			
			/* Define some books with previous authors */
			bookA = new BookGenerator().withAuthor(authorA).withAuthor(authorD).persist(conn);
			bookB = new BookGenerator().withAuthor(authorA).withAuthor(authorB).persist(conn);
			bookC = new BookGenerator().withAuthor(authorA).withAuthor(authorC).persist(conn);
			
			/* Finally, some sources */
			sourceA = new SourceGenerator().persist(conn);
			sourceB = new SourceGenerator().persist(conn);
		
			/* Create our quotes */
			new QuoteGenerator().withBook(bookA).withKeyword(mutapa).withPage("12-13").persist(conn);
			new QuoteGenerator().withBook(bookA).withKeyword(mutapa).withKeyword(gold).withPage("13").persist(conn);
			new QuoteGenerator().withBook(bookA).withKeyword(mutapa).withPage("20").persist(conn);
			new QuoteGenerator().withBook(bookA).withKeyword(ivory).withPage("22-26").persist(conn);
			
			new QuoteGenerator().withBook(bookB).withKeyword(gold).withKeyword(ivory).withPage("93").persist(conn);
			new QuoteGenerator().withBook(bookB).withKeyword(gold).withPage("95-96").withText("Alluvial gold exists.").persist(conn);
			
			new QuoteGenerator().withBook(bookC).withKeyword(ivory).withPage("70").persist(conn);
			new QuoteGenerator().withBook(bookC).withKeyword(ivory).withKeyword(mutapa).withPage("82").persist(conn);
			new QuoteGenerator().withBook(bookC).withKeyword(mutapa).withPage("78").persist(conn);
			
			new QuoteGenerator().withSource(sourceA).withKeyword(mutapa).withPage("12").persist(conn);
			new QuoteGenerator().withSource(sourceA).withKeyword(mutapa).withKeyword(gold).withPage("14").persist(conn);
			
			new QuoteGenerator().withSource(sourceB).withKeyword(ivory).persist(conn);
			new QuoteGenerator().withSource(sourceB).withKeyword(ivory).withKeyword(mutapa).persist(conn);
			new QuoteGenerator().withSource(sourceB).withKeyword(mutapa).persist(conn);
			new QuoteGenerator().withSource(sourceB).withKeyword(gold).withText("Gold is gathered on mines.").persist(conn);
		}
		
		/* Finally, let's test our search */
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			Collection<Quote> res;
			Collection<Keyword> keys = new ArrayList<Keyword>();
			keys.add(mutapa);
			res = QuoteManager.getSingleton().search(conn, keys, false, true, new ArrayList<Book>(), new ArrayList<Author>(), 
					true, new ArrayList<Source>(), "");
			assert(res.size() == 9);
			
			keys.add(ivory);
			res = QuoteManager.getSingleton().search(conn, keys, false, true, new ArrayList<Book>(), new ArrayList<Author>(), 
					true, new ArrayList<Source>(), "");
			assert(res.size() == 13);
			res = QuoteManager.getSingleton().search(conn, keys, true, true, new ArrayList<Book>(), new ArrayList<Author>(), 
					true, new ArrayList<Source>(), "");
			assert(res.size() == 2);
			
			keys.add(gold);
			res = QuoteManager.getSingleton().search(conn, keys, false, true, new ArrayList<Book>(), new ArrayList<Author>(), 
					true, new ArrayList<Source>(), "");
			assert(res.size() == 15);
			res = QuoteManager.getSingleton().search(conn, keys, true, true, new ArrayList<Book>(), new ArrayList<Author>(), 
					true, new ArrayList<Source>(), "");
			assert(res.size() == 0);
			
			Collection<Book> books = new ArrayList<Book>();
			books.add(bookA);
			res = QuoteManager.getSingleton().search(conn, new ArrayList<Keyword>(), false, true, books, new ArrayList<Author>(), 
					true, new ArrayList<Source>(), "");
			/* Check result size */
			assert(res.size() == 4);
			int lastPage = -1;
			/* Check if all quotes are from this book and page order. */
			for(Quote quote : res) {
				assert(quote.getBookId() == bookA.getId());
				assert(lastPage <= quote.getPageAsNumber());
				lastPage = quote.getPageAsNumber();
			}
			
			/* BookA and Mutapa */
			keys.clear();
			keys.add(mutapa);
			res = QuoteManager.getSingleton().search(conn, keys, false, true, books, new ArrayList<Author>(), 
					true, new ArrayList<Source>(), "");
			assert(res.size() == 3);
			
			/* BookA and BookC and Mutapa */
			books.add(bookC);
			res = QuoteManager.getSingleton().search(conn, keys, false, true, books, new ArrayList<Author>(), 
					true, new ArrayList<Source>(), "");
			assert(res.size() == 5);
			
			/* SourceA */
			Collection<Source> sources = new ArrayList<Source>();
			sources.add(sourceA);
			res = QuoteManager.getSingleton().search(conn, new ArrayList<Keyword>(), false, true, new ArrayList<Book>(), 
					new ArrayList<Author>(), true, sources, "");
			assert(res.size() == 2);
			
			/* AuthorD */
			Collection<Author> authors = new ArrayList<Author>();
			authors.add(authorD);
			res = QuoteManager.getSingleton().search(conn, new ArrayList<Keyword>(), false, true, new ArrayList<Book>(), 
					authors, true, new ArrayList<Source>(), "");
			assert(res.size() == 4);
			
			/* Text filter */
			res = QuoteManager.getSingleton().search(conn, new ArrayList<Keyword>(), false, true, new ArrayList<Book>(), 
					new ArrayList<Author>(), true, new ArrayList<Source>(), "mine");
			assert(res.size() == 1);
			
			/* Get all */
			res = QuoteManager.getSingleton().search(conn, new ArrayList<Keyword>(), false, true, new ArrayList<Book>(), 
					new ArrayList<Author>(), true, new ArrayList<Source>(), "");
			/* Note: >= as the previous tests should inserted some quotes on database also. */
			assert(res.size() >= 15);
		}
	}
	
	/** Test for function {@link QuoteManager#populateRelatedInfo}.
	 * @throws SQLException */
	public void testPopulateRelatedInfo() throws SQLException {
		Keyword ivory, gold, mutapa;
		Author authorA, authorB, authorC, authorD;
		Book bookA, bookB, bookC;
		Source sourceA, sourceB;
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			/* Define some keywords */
			ivory = new KeywordGenerator().withName("ivory").persist(conn);
			gold = new KeywordGenerator().withName("gold").persist(conn);
			mutapa = new KeywordGenerator().withName("Mutapa").persist(conn);
			
			/* Define some authors */
			authorA = new AuthorGenerator().withLastName("A").withName("Author").persist(conn);
			authorB = new AuthorGenerator().withLastName("B").withName("Author").persist(conn);
			authorC = new AuthorGenerator().withLastName("C").withName("Author").persist(conn);
			authorD = new AuthorGenerator().withLastName("D").withName("Author").persist(conn);
			
			/* Define some books with previous authors */
			bookA = new BookGenerator().withAuthor(authorA).withAuthor(authorD).persist(conn);
			bookB = new BookGenerator().withAuthor(authorA).withAuthor(authorB).persist(conn);
			bookC = new BookGenerator().withAuthor(authorA).withAuthor(authorC).persist(conn);
			
			/* Finally, some sources */
			sourceA = new SourceGenerator().persist(conn);
			sourceB = new SourceGenerator().persist(conn);
		
			/* Create our quotes */
			new QuoteGenerator().withBook(bookA).withKeyword(mutapa).withPage("12-13").persist(conn);
			new QuoteGenerator().withBook(bookA).withKeyword(mutapa).withKeyword(gold).withPage("13").persist(conn);
			new QuoteGenerator().withBook(bookA).withKeyword(mutapa).withPage("20").persist(conn);
			new QuoteGenerator().withBook(bookA).withKeyword(ivory).withPage("22-26").persist(conn);
			
			new QuoteGenerator().withBook(bookB).withKeyword(gold).withKeyword(ivory).withPage("93").persist(conn);
			new QuoteGenerator().withBook(bookB).withKeyword(gold).withPage("95-96").withText("Alluvial gold exists.").persist(conn);
			
			new QuoteGenerator().withBook(bookC).withKeyword(ivory).withPage("70").persist(conn);
			new QuoteGenerator().withBook(bookC).withKeyword(ivory).withKeyword(mutapa).withPage("82").persist(conn);
			new QuoteGenerator().withBook(bookC).withKeyword(mutapa).withPage("78").persist(conn);
			
			new QuoteGenerator().withSource(sourceA).withKeyword(mutapa).withPage("12").persist(conn);
			new QuoteGenerator().withSource(sourceA).withKeyword(mutapa).withKeyword(gold).withPage("14").persist(conn);
			
			new QuoteGenerator().withSource(sourceB).withKeyword(ivory).persist(conn);
			new QuoteGenerator().withSource(sourceB).withKeyword(ivory).withKeyword(mutapa).persist(conn);
			new QuoteGenerator().withSource(sourceB).withKeyword(mutapa).persist(conn);
			new QuoteGenerator().withSource(sourceB).withKeyword(gold).withText("Gold is gathered on mines.").persist(conn);
		}
		
		List<Quote> quotes = null;
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			/* Get all quotes from database (note: should return more than we created on this test, as should
			 * return quotes created on previous tests). */
			quotes = QuoteManager.getSingleton().search(conn, new ArrayList<Keyword>(), false, true, new ArrayList<Book>(), 
					new ArrayList<Author>(), true, new ArrayList<Source>(), "");
			QuoteManager.getSingleton().populateRelatedInfo(conn, quotes);
		}
		
		/* Check if populated */
		for(Quote quote : quotes) {
			if(quote.getBookId() != 0) {
				assert(quote.getBook() != null);
				assert(quote.getBook().getId() == quote.getBookId());
				if(quote.getBook().getId() == bookA.getId()) {
					assert(quote.getBook().getAuthorsCopy().size() == 2);
					assert(quote.getBook().getAuthorsCopy().indexOf(authorA) != -1);
					assert(quote.getBook().getAuthorsCopy().indexOf(authorD) != -1);
					assert(quote.getKeywordsCopy().size() > 0);
				}
			}
			if(quote.getSourceId() != 0) {
				assert(quote.getSource() != null);
				assert(quote.getSource().getId() == quote.getSourceId());
			}
		}
	}

}
