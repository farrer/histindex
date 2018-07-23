package org.dnteam.histindex.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Database table manager for {@link Source}
 * @author farrer */
public class SourceManager extends EntityManager<Source> {
	
	public static final String ID = "Id";
	public static final String TITLE = "Title";
	public static final String YEAR = "Year";
	public static final String EXTRA_INFO = "ExtraInfo";
	
	/** The single instance of SourceManager */
	private static final SourceManager SINGLETON =  new SourceManager();
	/** Our columns */
	private static final String[] COLUMNS = {TITLE, YEAR, EXTRA_INFO};
	private static final String[] ORDER_BY = {YEAR, TITLE};
	/** Table name */
	private static final String TABLE_NAME = "Sources";
	/** Alias to use on queries */
	private static final String TABLE_ALIAS = "SRC";

	private SourceManager() {
	}
	
	/** @return the single instance of SourceManager */
	public static final SourceManager getSingleton() {
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
	protected String[] getOrderByColumns() {
		return ORDER_BY;
	}

	/** {@inheritDoc} */
	@Override
	public Source createEntity() {
		return new Source();
	}

	/** {@inheritDoc} */
	@Override
	protected int populateFromResultSet(ResultSet rs, Source entity, int i) throws SQLException {
		entity.setTitle(rs.getString(i));
		entity.setYear(rs.getInt(i + 1));
		entity.setExtraInfo(rs.getString(i + 2));
		return i + 3;
	}

	/** {@inheritDoc} */
	@Override
	protected int prepareStatament(PreparedStatement stmt, Source entity, int i) throws SQLException {
		stmt.setString(i, entity.getTitle());
		stmt.setInt(i + 1, entity.getYear());
		stmt.setString(i + 2, entity.getExtraInfo());
		return i + 3;
	}

	/** {@inheritDoc} */
	@Override
	public String getIdColumn() {
		return ID;
	}

}
