package org.dnteam.histindex.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.dnteam.histindex.util.Tuple;

/** Database Table Manager for {@link QuoteKeyword}.
 * @author farrer */
public class QuoteKeywordManager extends ComposedEntityManager<QuoteKeyword> {
	
	public static final String QUOTE_ID = "QuoteId";
	public static final String KEYWORD_ID = "KeywordId";
	
	private static final Tuple<String, String> ID_COLUMNS = new Tuple<String, String>(QUOTE_ID, KEYWORD_ID);
	private static final String TABLE_NAME = "QuoteKeyword";
	private static final String TABLE_ALIAS = "QKW";
	
	private static final QuoteKeywordManager SINGLETON = new QuoteKeywordManager();
	
	private QuoteKeywordManager()	{
	}
	
	/** @return the single instance of {@link QuoteKeywordManager}. */
	public static QuoteKeywordManager getSingleton() {
		return SINGLETON;
	}

	/** {@inheritDoc} */
	@Override
	protected Tuple<String, String> getIdColumns() {
		return ID_COLUMNS;
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getColumns() {
		/* Pure relational entity. */
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected String getTableAlias() {
		return TABLE_ALIAS;
	}

	/** {@inheritDoc} */
	@Override
	protected QuoteKeyword createEntity() {
		return new QuoteKeyword();
	}

	/** {@inheritDoc} */
	@Override
	protected int populateFromResultSet(ResultSet rs, QuoteKeyword qk, int i) throws SQLException {
		return i;
	}

	/** {@inheritDoc} */
	@Override
	protected int prepareStatament(PreparedStatement stmt, QuoteKeyword qk, int i) throws SQLException {
		return i;
	}

	/** Delete all references to an specific {@link Keyword}.
	 * @param conn connection to use.
	 * @param keywordId identifier of the {@link Keyword} to delete its references.
	 * @return number of deleted references.
	 * @throws SQLException */
	public int deleteKeywordFromQuotes(Connection conn, long keywordId) throws SQLException {

		String query = "DELETE FROM " + TABLE_NAME;
		query += " WHERE " + KEYWORD_ID + " = ?";

		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setLong(1, keywordId);
			return stmt.executeUpdate();
		}
	}
	
	/** Delete all references to an specific {@link Quote}.
	 * @param conn connection to use.
	 * @param quoteId identifier of the {@link Keyword} to delete its references.
	 * @return number of deleted references.
	 * @throws SQLException */
	public int deleteKeywordsFromQuote(Connection conn, long quoteId) throws SQLException {

		String query = "DELETE FROM " + TABLE_NAME;
		query += " WHERE " + QUOTE_ID + " = ?";

		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setLong(1, quoteId);
			return stmt.executeUpdate();
		}
	}

	/** Populate all {@link Keyword}s of a {@link Quote}.
	 * @param conn connection to use.
	 * @param quote {@link Quote} to populate.
	 * @throws SQLException */
	public void populateKeywords(Connection conn, Quote quote) throws SQLException {
		KeywordManager keyManager = KeywordManager.getSingleton();

		String query = "SELECT " + keyManager.getColumnsForSelect();
		query += " FROM " + keyManager.getTableName() + " " + keyManager.getTableAlias() +
		         ", " + TABLE_NAME + " " + TABLE_ALIAS;
		query += " WHERE " + TABLE_ALIAS + "." + KEYWORD_ID + " = " + 
		                     keyManager.getTableAlias() + "." + KeywordManager.ID;   
		query += " AND " + TABLE_ALIAS + "." + QUOTE_ID + " = ?";
		query += " ORDER BY " + keyManager.getOrderByColumnsForSelect();
		
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setLong(1, quote.getId());
			try(ResultSet rs = stmt.executeQuery()) {
				quote.clearKeywords();
				while(rs.next()) {
					Keyword key = keyManager.createEntity();
					keyManager.populate(rs, key, 1);
					quote.addKeyword(key);
				}
			}
	   }
	}
	
	/** Populate a {@link List} of quotes with its referenced {@link Keyword}s.
	 * @param conn connection to use.
	 * @param data {@link List} of {@link Quote}s to populate its {@link Keyword}s.
	 * @throws SQLException */
	public void populateKeywords(Connection conn, List<Quote> data) throws SQLException {
		
		if(data.size() == 0) {
			/* Nothing to set. */
			return;
		}
		
		if(data.size() > MAX_WHERE_ELEMENTS) {
			/* We should do more than one database search, to avoid SQL errors of max limit reached. */
			int init = 0;
			int end;
			do {
				end = init + MAX_WHERE_ELEMENTS;
				if(end > data.size()) {
					end = data.size();
				}
				populateKeywords(conn, data.subList(init, end));
				init += MAX_WHERE_ELEMENTS;
			} while(init < data.size());
			
			return;
		}
		
		/* define a hash for quick find. */
		HashMap<Long, Quote> hash = QuoteManager.getSingleton().createHashMap(data);
		
		KeywordManager kManager = KeywordManager.getSingleton();

		String query = "SELECT " + TABLE_ALIAS + "." + QUOTE_ID + ", " + kManager.getColumnsForSelect();
		query += " FROM " + kManager.getTableName() + " " + kManager.getTableAlias() +
		         ", " + TABLE_NAME + " " + TABLE_ALIAS;
		query += " WHERE " + TABLE_ALIAS + "." + KEYWORD_ID + " = " + 
		                     kManager.getTableAlias() + "." + kManager.getIdColumn() + " AND (";
		for(int i = 0; i < data.size(); i++) {
			if(i > 0) {
				query += " OR ";
			}
			query += TABLE_ALIAS + "." + QUOTE_ID + " = ?";
		}
		query += ")";
			
		try(PreparedStatement stmt = conn.prepareStatement(query)) {
			
			/* Define our ids on prepared statament. */
			int i = 1;
			for(Quote quote : data) {
				stmt.setLong(i, quote.getId());
				i++;
			}
			
			/* Fetch our result from database and set them on our books */
			try(ResultSet rs = stmt.executeQuery()) {
				while(rs.next()) {
					long quoteId = rs.getLong(1);
					Keyword key = kManager.createEntity();
					kManager.populate(rs, key, 2);
					
					Quote curQuote = hash.get(quoteId);
					assert(curQuote != null);
					if(curQuote != null) {
						curQuote.addKeyword(key);
					}
				}
			}
	   }
	}

}
