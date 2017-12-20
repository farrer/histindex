package org.dnteam.histindex.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Database Table Manager for {@link Keyword}s.
 * @author farrer */
public class KeywordManager extends EntityManager<Keyword> {
	
	public static final String ID = "Id";
	public static final String NAME = "Name";
	
	/** The single instance of KeywordManager */
	private static final KeywordManager SINGLETON = new KeywordManager();
	/** Our columns */
	private static final String[] COLUMNS = {NAME};
	
	private static final String TABLE_NAME = "Keywords";
	private static final String TABLE_ALIAS = "KWD";
	
	private KeywordManager() {
	}
	
	/** @return the single instance of {@link KeywordManager}. */
	public static KeywordManager getSingleton() {
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
	public Keyword createEntity() {
		return new Keyword();
	}
	
	/** {@inheritDoc} */
	@Override
	protected int populateFromResultSet(ResultSet rs, Keyword entity, int i) throws SQLException {
		entity.setName(rs.getString(i));
		return i + 1;
	}

	/** {@inheritDoc} */
	@Override
	protected int prepareStatament(PreparedStatement stmt, Keyword entity, int i) throws SQLException {
		stmt.setString(i, entity.getName());
		return i + 1;
	}
	
	/** {@inheritDoc} */
	@Override
	protected String[] getOrderByColumns() {
		return COLUMNS;
	}

	/** {@inheritDoc} */
	@Override
	public String getIdColumn() {
		return ID;
	}

}
