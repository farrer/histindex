package org.dnteam.histindex.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Database table manager for {@link Entity} {@link Book}.
 * @author farrer */
public class BookManager extends EntityManager<Book> {
	
	public static final String ID = "id";
	public static final String TITLE = "Title";

	private static final String TABLE_NAME = "BOOKS";
	private static final String TABLE_ALIAS = "bk";
	private static final String[] TABLE_COLUMNS = {TITLE};
	
	private static final BookManager SINGLETON = new BookManager();
	
	/** Avoid instances besides the singleton */
	private BookManager() { 
	}
	
	/** @return singleton instance of BookManager */
	public static BookManager getSingleton() {
		return SINGLETON;
	}
	
	/** {@inheritDoc} */
	@Override
	protected String[] getColumns() {
		return TABLE_COLUMNS;
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
	public Book createEntity() {
		return new Book();
	}

	/** {@inheritDoc} */
	@Override
	protected int populateFromResultSet(ResultSet rs, Book book, int i) throws SQLException {
		book.setTitle(rs.getString(i));
		return i + 1;
	}

	/** {@inheritDoc} */
	@Override
	protected int prepareStatament(PreparedStatement stmt, Book book, int i) throws SQLException {
		stmt.setString(i, book.getTitle());
		return i + 1;
	}
	
	/** {@inheritDoc} */
	@Override
	protected String[] getOrderByColumns() {
		return TABLE_COLUMNS;
	}

	/** {@inheritDoc} */
	@Override
	public String getIdColumn() {
		return ID;
	}

}
