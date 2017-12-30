package org.dnteam.histindex.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dnteam.histindex.util.Tuple;

/**
 * Implementation of a database table manager for a {@link ComposedEntity}.
 * @author farrer
 *
 * @param <T> Entity which will manage.
 */
public abstract class ComposedEntityManager<T extends ComposedEntity<?, ?>> {
	
	protected static final int MAX_WHERE_ELEMENTS = 400;

	/** Insert the Entity to its database.
	 * @param db Database to use.
	 * @param entity entity to insert
	 * @throws SQLException */
	public void insert(Database db, T entity) throws SQLException {
		try (Connection conn = DriverManager.getConnection(db.getURL())) {
			insert(conn, entity);
		}
	}

	/** Insert the Entity to its database (with an already opened connection).
	 * @param conn connection to use.
	 * @param entity entity to insert
	 * @throws SQLException */
	public void insert(Connection conn, T entity) throws SQLException {

		/* Check if with valid keys */
		Tuple<Long, Long> key = entity.getKey();
		assert (key.getFirst() != null && key.getFirst() != 0);
		assert (key.getSecond() != null && key.getSecond() != 0);

		Tuple<String, String> keyCols = getIdColumns();
		assert (!keyCols.getFirst().isEmpty());
		assert (!keyCols.getSecond().isEmpty());

		/*Define statment with our composed identifier */
		String query = "INSERT INTO " + getTableName() + " (" + keyCols.getFirst() + ", " + keyCols.getSecond();
		String vals = "?, ?";

		/* Define other columns, if any */
		String[] cols = getColumns();
		if(cols != null) {
			for (String col : cols) {
				query += ", " + col;
				vals += ", ?" ;
			}
		}
		query += ") VALUES (" + vals + ")";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setLong(1, key.getFirst());
			stmt.setLong(2, key.getSecond());
			prepareStatament(stmt, entity, 3);
			int res = stmt.executeUpdate();
			assert(res == 1);
		}
	}

	/** Update current Entity to its database. 
	 * @param db Database to use.
	 * @param entity entity to update
	 * @throws SQLException */
	public void update(Database db, T entity) throws SQLException {
		try (Connection conn = DriverManager.getConnection(db.getURL())) {
			update(conn, entity);
		}
	}

	/** Update current Entity to its database (with an already opened connection).
	 * @param conn connection to use.
	 * @param entity entity to update.
	 * @throws SQLException */
	public void update(Connection conn, T entity) throws SQLException {
		
		String[] cols = getColumns();
		if(cols == null) {
			/* Nothing besides its ID, nothing to update (should delete and create a new
			 * if desire to update ids). */
			return;
		}
		
		/* Check if with valid keys */
		Tuple<Long, Long> key = entity.getKey();
		assert (key.getFirst() != null && key.getFirst() != 0);
		assert (key.getSecond() != null && key.getSecond() != 0);

		Tuple<String, String> keyCols = getIdColumns();
		assert (!keyCols.getFirst().isEmpty());
		assert (!keyCols.getSecond().isEmpty());
		
		String query = "UPDATE " + getTableName() + " SET ";

		
		int cur = 1;
		for (String col : cols) {
			if (cur > 1) {
				query += ", ";
			}
			query += col + " = ? ";
			cur++;
		}
		query += " WHERE " + keyCols.getFirst() + " = ? AND " + keyCols.getSecond() + " = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			prepareStatament(stmt, entity, 1);
			stmt.setLong(cur, key.getFirst());
			stmt.setLong(cur + 1, key.getSecond());
			int res = stmt.executeUpdate();
			assert (res == 1);
		}
	}

	/** Delete ComposedEntities from the database, based on its first Entity Id.
	 * @param db Database to use.
	 * @param id identifier of the first {@link Entity} of the {@link ComposedEntity} to delete.
	 * @return how many deleted items.
	 * @throws SQLException */
	public int deleteByFirstEntity(Database db, long id) throws SQLException {
		try (Connection conn = DriverManager.getConnection(db.getURL())) {
			return deleteByFirstEntity(conn, id);
		}
	}
	
	/** @see #deleteByFirstEntity(Database, long) */
	public int deleteByFirstEntity(Connection conn, long id) throws SQLException {
		Tuple<String, String> keyCols = getIdColumns();
		assert (!keyCols.getFirst().isEmpty());

		return delete(conn, id, keyCols.getFirst());
	}
	
	/** Delete ComposedEntities from the database, based on its second Entity Id.
	 * @param db Database to use.
	 * @param id identifier of the second {@link Entity} of the {@link ComposedEntity} to delete.
	 * @return how many deleted items.
	 * @throws SQLException */
	public int deleteBySecondEntity(Database db, long id) throws SQLException {
		try (Connection conn = DriverManager.getConnection(db.getURL())) {
			return deleteBySecondEntity(conn, id);
		}
	}
	
	/** @see #deleteBySecondEntity(Database, long) */
	public int deleteBySecondEntity(Connection conn, long id) throws SQLException {
		Tuple<String, String> keyCols = getIdColumns();
		assert (!keyCols.getSecond().isEmpty());

		return delete(conn, id, keyCols.getSecond());
	}
	
	/** Delete all ComposedEntities with its 'colName' identifier of a defined value.
	 * @param conn connection to use.
	 * @param id identifier value.
	 * @param colName name of the identifier column.
	 * @return number of entitites delete.
	 * @throws SQLException. */
	protected int delete(Connection conn, long id, String colName) throws SQLException {
		assert (id != 0);
		String query = "DELETE FROM " + getTableName() + " WHERE " + colName + " = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate();
		}
	}
	
	/** Delete ComposedEntities from the database, based on its first Entity Id.
	 * @param db Database to use.
	 * @param entity {@link ComposedEntity} to delete.
	 * @return if deleted or not.
	 * @throws SQLException */
	public boolean delete(Database db, T entity) throws SQLException {
		try (Connection conn = DriverManager.getConnection(db.getURL())) {
			return delete(conn, entity);
		}
	}
	
	/** @see #delete(Database, T) */
	public boolean delete(Connection conn, T entity) throws SQLException {

		/* Check if with valid keys */
		Tuple<Long, Long> key = entity.getKey();
		assert (key.getFirst() != null && key.getFirst() != 0);
		assert (key.getSecond() != null && key.getSecond() != 0);

		Tuple<String, String> keyCols = getIdColumns();
		assert (!keyCols.getFirst().isEmpty());
		assert (!keyCols.getSecond().isEmpty());
		
		String query = "DELETE FROM " + getTableName() + " WHERE " + keyCols.getFirst() + " = ? AND " + keyCols.getSecond() + " = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setLong(1, key.getFirst());
			stmt.setLong(2, key.getSecond());
			return stmt.executeUpdate() == 1;
		}
		
	}

	/** Load the single {@link ComposedEntity} from the database.
	 * @param db database to use.
	 * @param id identifier of the {@link ComposedEntity} to load.
	 * @return new entity with loaded values, or null if none found.
	 * @throws SQLException */
	public T load(Database db, Tuple<Long, Long> id) throws SQLException {
		try (Connection conn = DriverManager.getConnection(db.getURL())) {
			return load(conn, id);
		}
	}

	/** Load the single {@link ComposedEntity} from the database, with an already opened connection.
	 * @param conn connection to use.
	 * @param id identifier of the {@link ComposedEntity} to load
	 * @return new entity with loaded values, or null if none found.
	 * @throws SQLException */
	public T load(Connection conn, Tuple<Long, Long> id) throws SQLException {

		Tuple<String, String> keyCols = getIdColumns();
		assert (!keyCols.getFirst().isEmpty());
		assert (!keyCols.getSecond().isEmpty());
		
		/* Define the Select query */
		final String tableAlias = getTableAlias();
		String query = "SELECT " + tableAlias + "." + keyCols.getFirst() + ", " + tableAlias + "." + keyCols.getSecond();
		String[] cols = getColumns();
		if(cols != null) {
			for (String col : cols) {
				query += ", " + tableAlias + ". " + col;
			}
		}
		query += " FROM " + getTableName() + " " + tableAlias;
		query += " WHERE " + tableAlias + "." + keyCols.getFirst() + " = ? AND " + tableAlias + "." + keyCols.getSecond() + " = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setLong(1, id.getFirst());
			stmt.setLong(2, id.getSecond());
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					T entity = createEntity();
					populate(rs, entity);
					return entity;
				}
				return null;
			}
		}
	}

	/** Populate values from a ResultSet.
	 * @param rs result set to get values from.
	 * @param T entity to populate with values from result set.
	 * @throws SQLException */
	private void populate(ResultSet rs, T entity) throws SQLException {
		entity.setKey(new Tuple<Long, Long>(rs.getLong(1), rs.getLong(2)));
		populateFromResultSet(rs, entity, 3);
	}

	/** @return tuple with column names of the id */
	protected abstract Tuple<String, String> getIdColumns();

	/** @return vector with all column names, excluding the ids */
	protected abstract String[] getColumns();

	/** @return the database table name of the entity. */
	protected abstract String getTableName();

	/** @return the database table alias of the entity. */
	protected abstract String getTableAlias();

	/** @return a new created entity */
	protected abstract T createEntity();

	/** Populate Entity values from the result set. Start at index i, and do not set ids.
	 * @param rs result set to use
	 * @param T entity to populate with values from result set.
	 * @param i initial index to read
	 * @return next index to read. */
	protected abstract int populateFromResultSet(ResultSet rs, T entity, int i) throws SQLException;

	/**
	 * Prepare a PreparedStatament for insert/update, defining its values. Start at
	 * index i and don't define the identifier.
	 * @param stmt {@link PreparedStatement} to define.
	 * @param entity to prepare its insert or update statement.
	 * @param i first index to set.
	 * @return next index to ser.	 */
	protected abstract int prepareStatament(PreparedStatement stmt, T entity, int i) throws SQLException;

}
