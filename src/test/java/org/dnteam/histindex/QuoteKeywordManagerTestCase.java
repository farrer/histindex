package org.dnteam.histindex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.ComposedEntityManager;
import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.database.Keyword;
import org.dnteam.histindex.database.Quote;
import org.dnteam.histindex.database.QuoteKeyword;
import org.dnteam.histindex.database.QuoteKeywordManager;
import org.dnteam.histindex.generators.BookGenerator;
import org.dnteam.histindex.generators.KeywordGenerator;
import org.dnteam.histindex.generators.QuoteGenerator;

public class QuoteKeywordManagerTestCase extends ComposedEntityManagerTestCase<QuoteKeyword> {

	public QuoteKeywordManagerTestCase() throws SQLException {
		super("quotekeywordtest.db");
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createFirstEntity(Connection conn) throws SQLException {
		return new QuoteGenerator().persist(conn);
	}

	/** {@inheritDoc} */
	@Override
	protected Entity createSecondEntity(Connection conn) throws SQLException {
		return new KeywordGenerator().persist(conn);
	}

	/** {@inheritDoc} */
	@Override
	protected QuoteKeyword createEntity() throws SQLException {
		
		Quote quote;
		Keyword keyword;
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			QuoteGenerator qg = new QuoteGenerator();
			quote = qg.withText("Quote from book")
					     .withBook(new BookGenerator().persist(conn))
					     .withComment("This is a comment about the quote.")
					     .withPage("32-33")
					     .persist(conn);
			
			KeywordGenerator kg = new KeywordGenerator();
			keyword = kg.withName("Information").persist(conn);
		}
		
		return new QuoteKeyword(quote, keyword);
	}

	/** {@inheritDoc} */
	@Override
	protected QuoteKeyword createEntity(Entity first, Entity second) {
		return new QuoteKeyword((Quote) first, (Keyword) second);
	}

	/** {@inheritDoc} */
	@Override
	protected void changeEntityValues(QuoteKeyword entity) {
		/* Nothing to do, as purely composed */
	}

	/** {@inheritDoc} */
	@Override
	protected ComposedEntityManager<QuoteKeyword> getManager() {
		return QuoteKeywordManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	protected boolean haveEqualValues(QuoteKeyword ent1, QuoteKeyword ent2) {
		/* Id was already checked. */
		return true;
	}
	
	public void testDeleteKeywordFromQuotes() throws SQLException {
		Quote quoteA = null;
		Quote quoteB = null;
		List<Keyword> keysA = new ArrayList<Keyword>();
		List<Keyword> keysB = new ArrayList<Keyword>();
		Keyword slaves = null; 
		Keyword changamire = null;
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			slaves = new KeywordGenerator().withName("Slaves").persist(conn);
			changamire = new KeywordGenerator().withName("Changamire").persist(conn);
			keysA.add(slaves);
			keysA.add(changamire);
			keysA.add(new KeywordGenerator().withName("Mocaranga").persist(conn));
			
			keysB.add(new KeywordGenerator().withName("Torwa").persist(conn));
			keysB.add(changamire);
			keysB.add(new KeywordGenerator().withName("Kiteve").persist(conn));
			
			Book bookA = new BookGenerator().persist(conn);
			Book bookB = new BookGenerator().persist(conn);
			quoteA = new QuoteGenerator().withBook(bookA).withKeywords(keysA).persist(conn);
			quoteB = new QuoteGenerator().withBook(bookB).withKeywords(keysB).persist(conn);
		}
		
		/* Delete changamire Keyword */
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			int deleted = QuoteKeywordManager.getSingleton().deleteKeywordFromQuotes(conn, changamire.getId());
			assert(deleted == 2);
		}
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			QuoteKeywordManager.getSingleton().populateKeywords(conn, quoteA);
			QuoteKeywordManager.getSingleton().populateKeywords(conn, quoteB);
		}
		/* Check if no changamire keyword is found (on A and B) and if both have the other keywords */
		assert(quoteA.getKeywordsCopy().size() == 2);
		assert(quoteB.getKeywordsCopy().size() == 2);
		for(Keyword key : quoteA.getKeywordsCopy()) {
			assert(key.getId() != changamire.getId());
		}
		for(Keyword key : quoteB.getKeywordsCopy()) {
			assert(key.getId() != changamire.getId());
		}
		
		/* Delete slaves keyword */
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			int deleted = QuoteKeywordManager.getSingleton().deleteKeywordFromQuotes(conn, slaves.getId());
			assert(deleted == 1);
		}
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			QuoteKeywordManager.getSingleton().populateKeywords(conn, quoteA);
			QuoteKeywordManager.getSingleton().populateKeywords(conn, quoteB);
		}
		/* Check both */
		assert(quoteA.getKeywordsCopy().size() == 1);
		assert(quoteB.getKeywordsCopy().size() == 2);
		for(Keyword key : quoteA.getKeywordsCopy()) {
			assert(key.getId() != slaves.getId());
		}
		for(Keyword key : quoteB.getKeywordsCopy()) {
			assert(key.getId() != slaves.getId());
		}
	}
	
	public void testDeleteKeywordsFromQuote() throws SQLException {
		Quote quoteA = null;
		Quote quoteB = null;
		List<Keyword> keysA = new ArrayList<Keyword>();
		List<Keyword> keysB = new ArrayList<Keyword>();
		Keyword slaves = null; 
		Keyword changamire = null;
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			slaves = new KeywordGenerator().withName("Slaves").persist(conn);
			changamire = new KeywordGenerator().withName("Changamire").persist(conn);
			keysA.add(slaves);
			keysA.add(changamire);
			keysA.add(new KeywordGenerator().withName("Mocaranga").persist(conn));
			
			keysB.add(new KeywordGenerator().withName("Torwa").persist(conn));
			keysB.add(changamire);
			keysB.add(new KeywordGenerator().withName("Kiteve").persist(conn));
			
			Book bookA = new BookGenerator().persist(conn);
			Book bookB = new BookGenerator().persist(conn);
			quoteA = new QuoteGenerator().withBook(bookA).withKeywords(keysA).persist(conn);
			quoteB = new QuoteGenerator().withBook(bookB).withKeywords(keysB).persist(conn);
		}
		
		/* Delete keywords from quoteA */
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			int deleted = QuoteKeywordManager.getSingleton().deleteKeywordsFromQuote(conn, quoteA.getId());
			assert(deleted == 3);
		}
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			QuoteKeywordManager.getSingleton().populateKeywords(conn, quoteA);
			QuoteKeywordManager.getSingleton().populateKeywords(conn, quoteB);
		}
		/* Check if no more keywords on quoteA and if all keywords remained at quoteB */
		assert(quoteA.getKeywordsCopy().size() == 0);
		assert(quoteB.getKeywordsCopy().size() == 3);
		assert(checkIfHaveAll(keysB, quoteB.getKeywordsCopy()));
	}
	
	private boolean checkIfHaveAll(Collection<Keyword> keysToCheck, Collection<Keyword> keys) {
		for(Keyword key : keysToCheck) {
			boolean found = false;
			for(Keyword keyB : keys) {
				if(keyB.getId() == key.getId()) {
					found = true;
					break;
				}
			}
			if(!found) {
				return false;
			}
		}
		return true;
	}
	
	public void testPopulateQuoteKeywords() throws SQLException {
		Quote quoteA = null;
		Quote quoteB = null;
		List<Keyword> keysA = new ArrayList<Keyword>();
		List<Keyword> keysB = new ArrayList<Keyword>();
		Keyword slaves = null; 
		Keyword changamire = null;
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			slaves = new KeywordGenerator().withName("Slaves").persist(conn);
			changamire = new KeywordGenerator().withName("Changamire").persist(conn);
			keysA.add(slaves);
			keysA.add(changamire);
			keysA.add(new KeywordGenerator().withName("Mocaranga").persist(conn));
			
			keysB.add(new KeywordGenerator().withName("Torwa").persist(conn));
			keysB.add(changamire);
			keysB.add(new KeywordGenerator().withName("Kiteve").persist(conn));
			
			Book bookA = new BookGenerator().persist(conn);
			Book bookB = new BookGenerator().persist(conn);
			quoteA = new QuoteGenerator().withBook(bookA).withKeywords(keysA).persist(conn);
			quoteB = new QuoteGenerator().withBook(bookB).withKeywords(keysB).persist(conn);
		}
		quoteA.clearKeywords();
		quoteB.clearKeywords();
		
		/* Check if populated */
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			QuoteKeywordManager.getSingleton().populateKeywords(conn, quoteA);
			QuoteKeywordManager.getSingleton().populateKeywords(conn, quoteB);
		}
		assert(quoteA.getKeywordsCopy().size() == keysA.size());
		assert(quoteB.getKeywordsCopy().size() == keysB.size());
		assert(checkIfHaveAll(keysA, quoteA.getKeywordsCopy()));
		assert(checkIfHaveAll(keysB, quoteB.getKeywordsCopy()));
	}
	
	public void testPopulateQuotesKeywords() throws SQLException {
		Quote quoteA = null;
		Quote quoteB = null;
		List<Keyword> keysA = new ArrayList<Keyword>();
		List<Keyword> keysB = new ArrayList<Keyword>();
		Keyword slaves = null; 
		Keyword changamire = null;
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			slaves = new KeywordGenerator().withName("Slaves").persist(conn);
			changamire = new KeywordGenerator().withName("Changamire").persist(conn);
			keysA.add(slaves);
			keysA.add(changamire);
			keysA.add(new KeywordGenerator().withName("Mocaranga").persist(conn));
			
			keysB.add(new KeywordGenerator().withName("Torwa").persist(conn));
			keysB.add(changamire);
			keysB.add(new KeywordGenerator().withName("Kiteve").persist(conn));
			
			Book bookA = new BookGenerator().persist(conn);
			Book bookB = new BookGenerator().persist(conn);
			quoteA = new QuoteGenerator().withBook(bookA).withKeywords(keysA).persist(conn);
			quoteB = new QuoteGenerator().withBook(bookB).withKeywords(keysB).persist(conn);
		}
		quoteA.clearKeywords();
		quoteB.clearKeywords();
		
		/* Check if populated on a list */
		List<Quote> quotes = new ArrayList<Quote>();
		quotes.add(quoteA);
		quotes.add(quoteB);
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL()))	{
			QuoteKeywordManager.getSingleton().populateKeywords(conn, quotes);
		}
		assert(quoteA.getKeywordsCopy().size() == keysA.size());
		assert(quoteB.getKeywordsCopy().size() == keysB.size());
		assert(checkIfHaveAll(keysA, quoteA.getKeywordsCopy()));
		assert(checkIfHaveAll(keysB, quoteB.getKeywordsCopy()));
	}

}
