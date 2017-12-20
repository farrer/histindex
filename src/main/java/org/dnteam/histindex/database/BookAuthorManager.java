package org.dnteam.histindex.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import org.dnteam.histindex.util.Tuple;

/** Relation for defining authors of books.
 * @author farrer */
public class BookAuthorManager extends ComposedEntityManager<BookAuthor> {
	
	public static final String BOOK_ID = "BookId";
	public static final String AUTHOR_ID = "AuthorId";
	
	private static final Tuple<String, String> ID_COLUMNS = new Tuple<String, String>(BOOK_ID, AUTHOR_ID);
	
	private static final String TABLE_NAME = "BookAuthor";
	private static final String TABLE_ALIAS = "boau";
	private static final BookAuthorManager SINGLETON = new BookAuthorManager();
	
	private BookAuthorManager() {
	}
	
	/** @return the single instance of {@link BookAuthorManager}. */
	public static BookAuthorManager getSingleton() {
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
	protected BookAuthor createEntity() {
		return new BookAuthor();
	}

	/** {@inheritDoc} */
	@Override
	protected int populateFromResultSet(ResultSet rs, BookAuthor entity, int i) throws SQLException {
		return i;
	}

	/** {@inheritDoc} */
	@Override
	protected int prepareStatament(PreparedStatement stmt, BookAuthor entity, int i) throws SQLException {
		return i;
	}
	
	//public List<Book> loadBooksFromAuthorId(long authorId) {
	//}
	
	/** Load all Authors of a Book.
	 * @param conn Connection to use.
	 * @param bookId its Id.
	 * @throws SQLException */
	public void populateAuthors(Connection conn, Book book) throws SQLException {
		AuthorManager authorManager = AuthorManager.getSingleton();

		String query = "SELECT " + authorManager.getColumnsForSelect();
		query += " FROM " + authorManager.getTableName() + " " + authorManager.getTableAlias() +
		         ", " + TABLE_NAME + " " + TABLE_ALIAS;
		query += " WHERE " + TABLE_ALIAS + "." + AUTHOR_ID + " = " + 
		                     authorManager.getTableAlias() + "." + AuthorManager.ID;   
		query += " AND " + TABLE_ALIAS + "." + BOOK_ID + " = ?";
		query += " ORDER BY " + authorManager.getOrderByColumnsForSelect();
		
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setLong(1, book.getId());
			try(ResultSet rs = stmt.executeQuery()) {
				book.clearAuthors();
				while(rs.next()) {
					Author author = authorManager.createEntity();
					authorManager.populate(rs, author, 1);
					book.addAuthor(author);
				}
			}
	   }
	}
	
	/** Delete all BookAuthors from a Book.
	 * @param conn Connection to use.
	 * @param bookId its Id.
	 * @return total elements deleted.
	 * @throws SQLException */
	public int deleteAuthorsFromBook(Connection conn, long bookId) throws SQLException {

		String query = "DELETE FROM " + TABLE_NAME;
		query += " WHERE " + BOOK_ID + " = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setLong(1, bookId);
			return stmt.executeUpdate();
	   }
	}
	
	/** Delete all BookAuthors with an specific Author.
	 * @param conn Connection to use.
	 * @param authorId identifier of the {@link Author}.
	 * @return number of deleted {@link BookAuthor}s.
	 * @throws SQLException */
	public int deleteAuthorFromBooks(Connection conn, long authorId) throws SQLException {
		
		String query = "DELETE FROM " + TABLE_NAME;
		query += " WHERE " + AUTHOR_ID + " = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setLong(1, authorId);
			return stmt.executeUpdate();
	   }
	}
	
	/** Populate authors for a book list (with a single query on database).
	 * @param conn connection to use.
	 * @param data list with books to populate its authors.
	 * @throws SQLException */
	public void populateAuthors(Connection conn, Collection<Book> data) throws SQLException {
		
		if(data.size() == 0) {
			/* Nothing to set. */
			return;
		}
		
		/* define a hash for quick find. */
		HashMap<Long, Book> hash = BookManager.getSingleton().createHashMap(data);
		
		AuthorManager authorManager = AuthorManager.getSingleton();

		String query = "SELECT " + TABLE_ALIAS + "." + BOOK_ID + ", " + authorManager.getColumnsForSelect();
		query += " FROM " + authorManager.getTableName() + " " + authorManager.getTableAlias() +
		         ", " + TABLE_NAME + " " + TABLE_ALIAS;
		query += " WHERE " + TABLE_ALIAS + "." + AUTHOR_ID + " = " + 
		                     authorManager.getTableAlias() + "." + AuthorManager.ID + " AND (";
		for(int i = 0; i < data.size(); i++) {
			if(i > 0) {
				query += " OR ";
			}
			query += TABLE_ALIAS + "." + BOOK_ID + " = ?";
		}
		query += ")";
		
		
		try(PreparedStatement stmt = conn.prepareStatement(query)) {
			
			/* Define our ids on prepared statament. */
			int i = 1;
			for(Book book : data) {
				stmt.setLong(i, book.getId());
				i++;
			}
			
			/* Fetch our result from database and set them on our books */
			try(ResultSet rs = stmt.executeQuery()) {
				while(rs.next()) {
					long bookId = rs.getLong(1);
					Author author = authorManager.createEntity();
					authorManager.populate(rs, author, 2);
					
					Book curBook = (Book) hash.get(bookId);
					assert(curBook != null);
					if(curBook != null) {
						curBook.addAuthor(author);
					}
				}
			}
	   }
	}

}
