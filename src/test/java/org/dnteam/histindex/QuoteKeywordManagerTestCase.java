package org.dnteam.histindex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
	
	public void testDeleteKeywordFromQuotes() {
		//TODO
	}

}
