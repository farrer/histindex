package org.dnteam.histindex.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.dnteam.histindex.util.StringUtil;

/** Database Table Manager for {@link Quote}s.
 * @author farrer */
public class QuoteManager extends EntityManager<Quote> {
	
	public static final String ID = "Id";
	public static final String TEXT = "Text";
	public static final String BOOK_ID = "BookId";
	public static final String SOURCE_ID = "SourceId";
	public static final String PAGE = "Page";
	public static final String COMMENT = "Comment";
	
	private static final QuoteManager SINGLETON = new QuoteManager();
	
	private static final String[] COLUMNS = {TEXT, BOOK_ID, SOURCE_ID, PAGE, COMMENT};
	private static final String TABLE_NAME = "Quotes";
	private static final String TABLE_ALIAS = "QUO";
	private static final String[] ORDER_BY = {BOOK_ID, SOURCE_ID, PAGE};
	
	private QuoteManager() {
	}
	
	/** @return the single instance of {@link QuoteManager}. */
	public static final QuoteManager getSingleton() {
		return SINGLETON;
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getColumns() {
		return COLUMNS;
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
	public Quote createEntity() {
		return new Quote();
	}

	/** {@inheritDoc} */
	@Override
	protected int populateFromResultSet(ResultSet rs, Quote quote, int i) throws SQLException {
		quote.setText(rs.getString(i));
		quote.setBookId(rs.getLong(i + 1));
		quote.setSourceId(rs.getLong(i + 2));
		quote.setPage(rs.getString(i + 3));
		quote.setComment(rs.getString(i + 4));
		return i + 5;
	}

	/** {@inheritDoc} */
	@Override
	protected int prepareStatament(PreparedStatement stmt, Quote quote, int i) throws SQLException {
		stmt.setString(i, quote.getText());
		stmt.setLong(i + 1, quote.getBookId());
		stmt.setLong(i + 2, quote.getSourceId());
		stmt.setString(i + 3, quote.getPage());
		stmt.setString(i + 4, quote.getComment());
		return i + 5;
	}
	
	/** {@inheritDoc} */
	@Override
	protected String[] getOrderByColumns() {
		return ORDER_BY;
	}

	/** {@inheritDoc} */
	@Override
	public String getIdColumn() {
		return ID;
	}
	
	/** Delete all {@link Quote}s from a {@link Book}.
	 * @param conn connection to use.
	 * @param bookId identifier of the {@link Book}.
	 * @return number of deleted Quotes.
	 * @throws SQLException */
	public int deleteAllQuotesFromBook(Connection conn, long bookId) throws SQLException {

		String query = "DELETE FROM " + TABLE_NAME;
		query += " WHERE " + BOOK_ID + " = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setLong(1, bookId);
			return stmt.executeUpdate();
	   }
	}
	
	/** Delete all {@link Quote}s from a {@link Source}.
	 * @param conn connection to use.
	 * @param sourceId identifier of the {@link Source}.
	 * @return number of deleted Quotes.
	 * @throws SQLException */
	public int deleteAllQuotesFromSource(Connection conn, long sourceId) throws SQLException {

		String query = "DELETE FROM " + TABLE_NAME;
		query += " WHERE " + SOURCE_ID + " = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setLong(1, sourceId);
			return stmt.executeUpdate();
	   }
	}
	
	/** Search for {@link Quote}s, filtering by all its relations.
	 * @param conn {@link Connection} to use.
	 * @param keywords {@link Keyword}s to filter, if any.
	 * @param books {@link Book}s to filter, if any.
	 * @param authors {@link Author}s to filter, if any.
	 * @param sources {@link Source}s to filter, if any.
	 * @param text String with text to filter, as a like statement.
	 * @return List of {@link Quote}s found.
	 * @throws SQLException */
	public List<Quote> search(Connection conn, Collection<Keyword> keywords, Collection<Book> books, 
			Collection<Author> authors, Collection<Source> sources, String text) throws SQLException {
		
		QuoteKeywordManager qkm = QuoteKeywordManager.getSingleton();
		BookAuthorManager bam = BookAuthorManager.getSingleton();
		
		String query = "SELECT DISTINCT " + getColumnsForSelect() + 
				" FROM " + getTableName() + " " + getTableAlias() + " ";
		
		if((keywords != null) && (!keywords.isEmpty())) {
			query += " INNER JOIN " + qkm.getTableName() + " " + qkm.getTableAlias() +
					" ON " + qkm.getTableAlias() + "." + QuoteKeywordManager.QUOTE_ID + " = " + getTableAlias() + "." + ID;
		}
		
		if((authors != null) && (!authors.isEmpty())) {
			query += " INNER JOIN " + bam.getTableName() + " " + bam.getTableAlias() +
					" ON " + bam.getTableAlias() + "." + BookAuthorManager.BOOK_ID + " = " + getTableAlias() + "." + BOOK_ID;
		}
		
		boolean whereDefined = false;
		if((books != null) && (!books.isEmpty())) {
			whereDefined = true;
			query += " WHERE " + getTableAlias() + "." + BOOK_ID + createInClause(books.size());
		}
		
		if((keywords != null) && (!keywords.isEmpty())) {
			if(!whereDefined) {
				query += " WHERE ";
				whereDefined = true;
			} else {
				query += "AND ";
			}
			query += qkm.getTableAlias() + "." + QuoteKeywordManager.KEYWORD_ID + createInClause(keywords.size());
		}
		
		if((authors != null) && (!authors.isEmpty())) {
			if(!whereDefined) {
				query += " WHERE ";
				whereDefined = true;
			} else {
				query += "AND ";
			}
			query += bam.getTableAlias() + "." + BookAuthorManager.AUTHOR_ID + createInClause(authors.size());
		}
		
		if((sources != null) && (!sources.isEmpty())) {
			if(!whereDefined) {
				query += " WHERE ";
				whereDefined = true;
			} else {
				query += "AND ";
			}
			query += getTableAlias() + "." + SOURCE_ID + createInClause(sources.size());
		}
		
		if(!StringUtil.isEmpty(text)) {
			if(!whereDefined) {
				query += " WHERE ";
				whereDefined = true;
			} else {
				query += "AND ";
			}
			query += getTableAlias() + "." + TEXT + " LIKE ?";
		}
		
		
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			
			/* Define our filter values, at the same order of the query. */
			int i = 1;
			if((books != null) && (!books.isEmpty())) {
				for(Book book : books) {
					stmt.setLong(i, book.getId());
					i++;
				}
			}
			if((keywords != null) && (!keywords.isEmpty())) {
				for(Keyword key : keywords) {
					stmt.setLong(i, key.getId());
					i++;
				}
			}
			if((authors != null) && (!authors.isEmpty())) {
				for(Author author : authors) {
					stmt.setLong(i, author.getId());
					i++;
				}
			}
			if((sources != null) && (!sources.isEmpty())) {
				for(Source source : sources) {
					stmt.setLong(i, source.getId());
					i++;
				}
			}
			if(!StringUtil.isEmpty(text)) {
				stmt.setString(i, "%" + text + "%");
			}
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Quote> list = new ArrayList<Quote>();
				while(rs.next()) {
					Quote quote = createEntity();
					populate(rs, quote, 1);
					list.add(quote);
				}
				return list;
			}
	   }
		
	}
	
	/** Create a "IN(?, ?, ...)" clause for a selection use.
	 * @param totalElements total elementes to add to the in.
	 * @return String with the created clause. */
	private String createInClause(int totalElements) {
		String in = " IN (";
		for(int cur = 0; cur < totalElements; cur++) {
			if(cur > 0) {
				in += ", ?";
			} else {
				in += "?";
			}
		}
		in += ")";
		
		return in;
	}
	
	/** Populate all related information for a list of Quotes.
	 * @param conn connection to use.
	 * @param data list with all quotes to load its related information.
	 * @throws SQLException */
	public void populateRelatedInfo(Connection conn, List<Quote> data) throws SQLException {
		
		/* Construct the list of bookids and sourceids to load */
		TreeSet<Long> bookIds = new TreeSet<Long>();
		TreeSet<Long> sourceIds = new TreeSet<Long>();
		
		for(Quote quote : data) {
			if(quote.getBookId() != 0) {
				bookIds.add(quote.getBookId());
			}
			if(quote.getSourceId() != 0) {
				sourceIds.add(quote.getSourceId());
			}
		}
		
		/* Load our books with authors */
		BookManager bookManager = BookManager.getSingleton();
		List<Book> bookList = bookManager.load(conn, new ArrayList<Long>(bookIds));
		BookAuthorManager.getSingleton().populateAuthors(conn, bookList);
		HashMap<Long, Book> books = bookManager.createHashMap(bookList);
		
		/* Load our sources */
		SourceManager sourceManager = SourceManager.getSingleton();
		HashMap<Long, Source> sources = sourceManager.createHashMap(sourceManager.load(conn, new ArrayList<Long>(sourceIds)));
		
		/* Let's populate our quotes with its books and sources */
		for(Quote quote : data) {
			if(quote.getBookId() != 0) {
				quote.setBook(books.get(quote.getBookId()));
			}
			
			if(quote.getSourceId() != 0) {
				quote.setSource(sources.get(quote.getSourceId()));
			}
		}
		
		/* Populate its Keywords */
		QuoteKeywordManager.getSingleton().populateKeywords(conn, data);
		
		//FIXME: this is ugly and inefficient, but our page must be a string... ouch!
		sort(data);
	}
	
	/** Sort a list of quotes.
	 * @param quotes list to sort. */
	private void sort(List<Quote> quotes) {
		quotes.sort(new Comparator<Quote>() {

			@Override
			public int compare(Quote o1, Quote o2) {
				if(o1.getBook() != null && o2.getBook() != null) {
					int res = o1.getBook().getDescription().compareTo(o2.getBook().getDescription());
					if(res != 0) {
						return res;
					}
				}
				else if(o1.getBook() != null) {
					return -1;
				}
				else if(o2.getBook() != null) {
					return 1;
				}
				
				if(!StringUtil.isEmpty(o1.getPage()) && !StringUtil.isEmpty(o2.getPage())) {
					int res = o1.getPageAsNumber() - o2.getPageAsNumber();
					if(res != 0) {
						return res;
					}
						
					return res;
				}
				
				return (int) o1.getId() - (int) o2.getId();
			}
		});
	}

}
