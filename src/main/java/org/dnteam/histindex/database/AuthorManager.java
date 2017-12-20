package org.dnteam.histindex.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Table Manager for {@link Author} entity.
 * @author farrer */
public class AuthorManager extends EntityManager<Author> {
	
	public static final String ID = "Id";
	public static final String NAME = "Name";
	public static final String LAST_NAME = "LastName";
	
	private static final String TABLE_NAME = "Authors";
	private static final String TABLE_ALIAS = "ath";
	private static final String[] TABLE_COLUMNS = {NAME, LAST_NAME};
	private static final String[] TABLE_ORDER_BY = {LAST_NAME, NAME};
	private static final AuthorManager SINGLETON = new AuthorManager();
	
	private AuthorManager() {
	}
	
	/** @return the single instance of {@link AuthorManager} */
	public static AuthorManager getSingleton() {
		return SINGLETON;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getIdColumn() {
		return ID;
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
	public Author createEntity() {
		return new Author();
	}

	/** {@inheritDoc} */
	@Override
	protected int populateFromResultSet(ResultSet rs, Author author, int i) throws SQLException {
		author.setName(rs.getString(i));
		author.setLastName(rs.getString(i + 1));
		return i + 2;
	}

	/** {@inheritDoc} */
	@Override
	protected int prepareStatament(PreparedStatement stmt, Author author, int i) throws SQLException {
		stmt.setString(i, author.getName());
		stmt.setString(i + 1, author.getLastName());
		return i + 2;
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getOrderByColumns() {
		return TABLE_ORDER_BY;
	}

}
