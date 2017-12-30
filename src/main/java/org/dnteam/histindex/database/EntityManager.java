package org.dnteam.histindex.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation of a database table manager for an Entity.
 * @author farrer
 *
 * @param <T> Entity which will manage.
 */
public abstract class EntityManager <T extends Entity> {
	
	protected static final int MAX_WHERE_ELEMENTS = 400;

	/** Insert the Entity to its database.
	 * @param db Database to use.
	 * @param entity entity to insert
	 * @throws SQLException */
	public void insert(Database db, T entity) throws SQLException {
		try(Connection conn = DriverManager.getConnection(db.getURL())) {
			insert(conn, entity);
		}
	}
	
	/** Insert the Entity to its database (with an already opened connection).
	 * @param conn connection to use.
	 * @param entity entity to insert
	 * @throws SQLException */
	public void insert(Connection conn, T entity) throws SQLException {
		assert(entity.getId() == 0);
		String query = "INSERT INTO " + getTableName() + " (" ;
		
		String[] cols = getColumns();
		String vals = "";
		int cur = 0;
		for(String col : cols) {
			if(cur > 0) {
				query += ", ";
				vals += ", ";
			}
			query += col;
			vals += "?";
			cur++;
		}
		query += ") VALUES (" + vals + ")";
		 
      try (PreparedStatement stmt = conn.prepareStatement(query)) {
      	 prepareStatament(stmt, entity, 1);
          if(stmt.executeUpdate() != 0)
          {
         	 try(Statement sel = conn.createStatement())
         	 {
         		 try(ResultSet res = sel.executeQuery("SELECT last_insert_rowid()"))
         		 {
         			 if(res.next())
         			 {
         				 entity.setId(res.getLong(1));
         			 }
         			 else
         			 {
         			    assert(false);
         			 }
         		 }
         	 }
          }
      }
	}
	
	/** Update current Entity to its database.
	 * @param db Database to use.
	 * @param entity entity to update
	 * @throws SQLException */
	public void update(Database db, T entity) throws SQLException {
		try(Connection conn = DriverManager.getConnection(db.getURL())) {
			update(conn, entity);
		}
	}
	
	/** Update current Entity to its database (with an already opened connection).
	 * @param conn connection to use.
	 * @param entity entity to update.
	 * @throws SQLException */
	public void update(Connection conn, T entity) throws SQLException {
		assert(entity.getId() != 0);
		String query = "UPDATE " + getTableName() + " SET " ;
		
		String[] cols = getColumns();
		int cur = 1;
		for(String col : cols) {
			if(cur > 1) {
				query += ", ";
			}
			query += col + " = ? ";
			cur++;
		}
		query += " WHERE Id = ?";
		 
      try (PreparedStatement stmt = conn.prepareStatement(query)) {
      	 prepareStatament(stmt, entity, 1);
      	 stmt.setLong(cur, entity.getId());
          int res = stmt.executeUpdate();
          assert(res == 1);
      }
	}
	
	/** Delete an Entity from the database.
	 * @param db Database to use.
	 * @param id identifier of the entty to delete.
	 * @return if deleted or not.
	 * @throws SQLException */
	public boolean delete(Database db, long id) throws SQLException {
		try(Connection conn = DriverManager.getConnection(db.getURL())) {
			return delete(conn, id);
		}
	}
	
	/** Delete an Entity from the database (with an already opened connection).
	 * @param conn connection to use.
	 * @param id identifier of the entty to delete.
	 * @return if deleted or not.
	 * @throws SQLException */
	public boolean delete(Connection conn, long id) throws SQLException {
		assert(id != 0);
		String query = "DELETE FROM " + getTableName() + " WHERE Id = ?";
		 
      try (PreparedStatement stmt = conn.prepareStatement(query)) {
      	 stmt.setLong(1, id);
          return stmt.executeUpdate() == 1;
      }
	}
	
	/** @return String with all columns for selection use (ie: "Alias.id, Alias.column1, ... ") */
	public String getColumnsForSelect() {
		final String tableAlias = getTableAlias();
		String query = tableAlias + ".Id";
		String[] cols = getColumns();
		for(String col : cols) {
			query += ", " + tableAlias + "." + col;
		}
		
		return query;
	}
	
	/** @return String with columns used for Order by */
	public String getOrderByColumnsForSelect() {
		final String tableAlias = getTableAlias();
		String query = "";
		String[] orderCols = getOrderByColumns();
		if(orderCols != null)
		{
			for(String col : orderCols) {
				query += tableAlias + "." + col + ", "; 
			}
		}
		query += tableAlias + ".Id";
		
		return query;
	}
	
	/** Load the single entity from the database. 
	 * @param db database to use.
	 * @param id identifier of the entity to load.
	 * @return new entity with loaded values, or null if none found. 
	 * @throws SQLException */
	public T load(Database db, long id) throws SQLException {
		try(Connection conn = DriverManager.getConnection(db.getURL())) {
			return load(conn, id);
		}
	}
	
	/** Load the single entity from the database, with an already opened connection.
	 * @param conn connection to use.
	 * @param id identifier of the entity to load
	 * @return new entity with loaded values, or null if none found.
	 * @throws SQLException */
	public T load(Connection conn, long id) throws SQLException {
		/* Define the Select query */
		final String tableAlias = getTableAlias();
		String query = "SELECT " +  getColumnsForSelect();
		query += " FROM " + getTableName() + " " + tableAlias;
		query += " WHERE " + tableAlias + ".Id = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setLong(1, id);
			try(ResultSet rs = stmt.executeQuery()) {
				if(rs.next()) {
					T entity = createEntity();
					populate(rs, entity, 1);
					return entity;
				}
				return null;
			}
	   }
	}
	
	/** Load all elements from the table.
	 * @param database database to use.
	 * @return List with all loaded Entities.
	 * @throws SQLException */
	public List<T> loadAll(Database database) throws SQLException {
		try(Connection conn = DriverManager.getConnection(database.getURL())) {
			return loadAll(conn);
		}
	}
	
	/** Load all elements from the table.
	 * @param conn connection to use.
	 * @return List with all loaded Entities.
	 * @throws SQLException */
	public List<T> loadAll(Connection conn) throws SQLException {
		return loadAll(conn, true);
	}
		
	/** Load all elements from the table, ordering by defined elements, or just by identifier. 
	 * @param conn connection to use.
	 * @param useFullOrderBy if should order by defined elements (<code>true</code>) or
	 *        only by its identifier (<code>false</code>).
	 * @return The loaded list.
	 * @throws SQLException on error. */
	public List<T> loadAll(Connection conn, boolean useFullOrderBy) throws SQLException {
		final String tableAlias = getTableAlias();
		String query = "SELECT " + getColumnsForSelect();
		query += " FROM " + getTableName() + " " + tableAlias;
		query += " ORDER BY ";
		if(useFullOrderBy) {
			query += getOrderByColumnsForSelect();
		} else {
			query += tableAlias + ".Id";
		}
		
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			try(ResultSet rs = stmt.executeQuery()) {
				List<T> list = new ArrayList<T>();
				while(rs.next()) {
					T entity = createEntity();
					populate(rs, entity, 1);
					list.add(entity);
				}
				return list;
			}
	   }
	}

	/** Load all entities wich identifiers are defined at the list.
	 * @param conn connection to use.
	 * @param ids identifiers of the Entities to load.
	 * @return List of loaded entities.
	 * @throws SQLException. */
	public List<T> load(Connection conn, List<Long> ids) throws SQLException {
		
		if(ids.size() == 0) {
			return new ArrayList<T>(0);
		}
		if(ids.size() > MAX_WHERE_ELEMENTS) {
			/* Must split the populate action between N searches and merge the results */
			List<T> totalResults = new ArrayList<T>();
			int init = 0;
			int end;
			do {
				end = init + MAX_WHERE_ELEMENTS;
				if(end > ids.size()) {
					end = ids.size();
				}
				totalResults.addAll(load(conn, ids.subList(init, end)));
				init += MAX_WHERE_ELEMENTS;
			} while(init + MAX_WHERE_ELEMENTS < ids.size());

			return totalResults;
		}
		
		final String tableAlias = getTableAlias();
		final String idColumn = getIdColumn();
		
		String query = "SELECT " + getColumnsForSelect();
		query += " FROM " + getTableName() + " " + tableAlias;
		query += " WHERE ";
		for(int i=0; i < ids.size(); i++)
		{
			if(i > 0) {
				query += " OR ";
			}
			query += tableAlias + "." + idColumn + " = ?"; 
		}
		query += " ORDER BY ";
		query += getOrderByColumnsForSelect();
		
		try(PreparedStatement stmt = conn.prepareStatement(query)){
			int i = 1;
			for(Long id : ids) {
				stmt.setLong(i, id);
				i++;
			}
			try(ResultSet rs = stmt.executeQuery()) {
				List<T> list = new ArrayList<T>();
				while(rs.next()) {
					T entity = createEntity();
					populate(rs, entity, 1);
					list.add(entity);
				}
				return list;
			}
	   }
	}
	
	/** Populate values from a ResultSet.
	 * @param rs result set to get values from.
	 * @param T entity to populate with values from result set.
	 * @param firstIndex index of the first related to this entity element on the result set (usually 1).
	 * @return next index on the result set to read.
	 * @throws SQLException */
	public int populate(ResultSet rs, T entity, int firstIndex) throws SQLException {
		entity.setId(rs.getLong(firstIndex));
		return populateFromResultSet(rs, entity, firstIndex + 1);
	}
	
	/** Create a hash map, accessible by Entity identifier from a collection.
	 * @param collection collection with entities to create a hash map from.
	 * @return {@link HashMap} created. */
	public HashMap<Long, T> createHashMap(Collection<T> collection) {
		
		HashMap<Long, T> hash = new HashMap<Long, T>();
		
		for(T e : collection) {
			hash.put(e.getId(), e);
		}
		
		return hash;
	}
	
	/** @return vector with all column names, excluding the id */
	protected abstract String[] getColumns();
	
	/** @return columns to default order by. No need to set id, as will always use it as last. 
	 * If null will only order by id. */
	protected abstract String[] getOrderByColumns();
	
	/** @return the database table name of the entity. */
	protected abstract String getTableName();
	
	/** @return the database table alias of the entity. */
	protected abstract String getTableAlias();
	
	/** Column name of the Identifier */
	public abstract String getIdColumn();
	
	/** @return a new created entity */ 
	public abstract T createEntity();
	
	/** Populate Entity values from the result set. Start at index i, and do not touch ids (already populated).
	 * @param rs result set to use 
	 * @param T entity to populate with values from result set.
	 * @param i initial index of the result set to read.
	 * @return next index of the result set to read. */
	protected abstract int populateFromResultSet(ResultSet rs, T entity, int i)  throws SQLException;
	
	/** Prepare a PreparedStatament for insert/update, defining its values. Start at index i and don't define
	 * the identifier.
	 * @param stmt {@link PreparedStatement} to define.
	 * @param entity to prepare its insert or update statement.
	 * @param i initial index to set
	 * @return next index to set  */
	protected abstract int prepareStatament(PreparedStatement stmt, T entity, int i) throws SQLException;
	
}
