package org.dnteam.histindex.generators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.database.EntityManager;

/** Basic class for generate Entities for TestCases.
 * @author farrer
 * @param <T> {@link Entity} type. */
public abstract class EntityGenerator<T extends Entity> {

	/** @return instance of the Manager used for the Entity */
	public abstract EntityManager<T> getManager();
	/** @return the entity in creation */
	public abstract T getEntity();
	
	/** Persist (insert) the in-creation entity to the database.
	 * @param database to use.
	 * @return Created entity (with its id defined).
	 * @throws SQLException */
	public T persist(Database database) throws SQLException {
		try(Connection conn = DriverManager.getConnection(database.getURL())) {
			return persist(conn);
		}
	}
	
	/** Same as {@link #persist(Database)}, but with an already opened connection. */	
	public T persist(Connection conn) throws SQLException {
		T ent = getEntity();
		getManager().insert(conn, ent);	
		return ent;
	}
	
}
