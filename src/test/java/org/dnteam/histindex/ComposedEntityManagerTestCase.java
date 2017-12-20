package org.dnteam.histindex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dnteam.histindex.database.ComposedEntity;
import org.dnteam.histindex.database.ComposedEntityManager;
import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.util.Tuple;

import junit.framework.TestCase;

/** Base class for {@link ComposedEntityManager}s test cases implementation.
 * @author farrer
 *
 * @param <T> type of the {@link ComposedEntity}. */
public abstract class ComposedEntityManagerTestCase<T extends ComposedEntity<?, ?>> extends TestCase {
	private Database database;
	
	/** Constructor
	 * @param filename filename (url) of the database. 
	 * @throws SQLException */
	public ComposedEntityManagerTestCase(String filename) throws SQLException {
		this.database = new Database(filename);
		this.database.createSchema();
	}
	
	/** Test insertion of the {@link ComposedEntity}.
	 * @throws SQLException */
	public void testInsert() throws SQLException {
		T entity = createEntity();
		getManager().insert(getDatabase(), entity);
		
	   assert(entity.getKey().getFirst() != 0);
	   assert(entity.getKey().getSecond() != 0);
	}
	
	/** Test selection by if of a {@link ComposedEntity}.
	 * @throws SQLException */
	public void testSelect() throws SQLException	{
		T entity = createEntity();
		getManager().insert(getDatabase(), entity);
		
		T loaded = getManager().load(getDatabase(), entity.getKey());
		
		assert(loaded != null);
		assert(loaded.getKey().getFirst() == entity.getKey().getFirst());
		assert(loaded.getKey().getSecond() == entity.getKey().getSecond());
		assert(haveEqualValues(entity, loaded));
	}
	
	/** Test update of a {@link ComposedEntity}.
	 * @throws SQLException */
	public void testUpdate() throws SQLException	{
		T entity = createEntity();
		getManager().insert(getDatabase(), entity);
		
		changeEntityValues(entity);
		getManager().update(getDatabase(), entity);
		
		T loaded = getManager().load(getDatabase(), entity.getKey());
		
		assert(loaded != null);
		assert(loaded.getKey().getFirst() == entity.getKey().getFirst());
		assert(loaded.getKey().getSecond() == entity.getKey().getSecond());
		assert(haveEqualValues(entity, loaded));
	}
	
	/** Test deletion by Id of a {@link ComposedEntity}.
	 * @throws SQLException */
	public void testDelete() throws SQLException	{
		T entity = createEntity();
		getManager().insert(getDatabase(), entity);
		
		boolean deleted = getManager().delete(getDatabase(), entity);
		assert(deleted);
		
		T loaded = getManager().load(getDatabase(), entity.getKey());
		assert(loaded == null);
	}
	
	/** Test deletion of {@link ComposedEntity}ies by its first and second {@link Entity} id.
	 * @throws SQLException */
	public void testDeleteByEachId() throws SQLException {
		
		Entity ent1a, ent1b, ent1c, ent2a, ent2b, ent2c;
		
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL())) {
			/* Create some composed with the same first */
			ent1a = createFirstEntity(conn);
			ent2a = createSecondEntity(conn);
			ent2b = createSecondEntity(conn);
			ent2c = createSecondEntity(conn);
			
			T composed = createEntity(ent1a, ent2a);
			getManager().insert(conn, composed);
			composed = createEntity(ent1a, ent2b);
			getManager().insert(conn, composed);
			composed = createEntity(ent1a, ent2c);
			getManager().insert(conn, composed);
			
			/* Now,  some with the same second*/
			ent1b = createFirstEntity(conn);
			ent1c = createFirstEntity(conn);
			
			composed = createEntity(ent1b, ent2a);
			getManager().insert(conn, composed);
			composed = createEntity(ent1c, ent2a);
			getManager().insert(conn, composed);

		}
		
		/* Let's delete all related with ent1a */
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL())) {
			int count = getManager().deleteByFirstEntity(conn, ent1a.getId());
			assert(count == 3);
			
			/* Check if none with 1a. */
			T res = getManager().load(conn, new Tuple<Long, Long>(ent1a.getId(), ent2a.getId()));
			assert(res == null);
			res = getManager().load(conn, new Tuple<Long, Long>(ent1a.getId(), ent2b.getId()));
			assert(res == null);
			res = getManager().load(conn, new Tuple<Long, Long>(ent1a.getId(), ent2c.getId()));
			assert(res == null);
		}
		
		/* Let's delete all related with ent2a */
		try(Connection conn = DriverManager.getConnection(getDatabase().getURL())) {
			int count = getManager().deleteBySecondEntity(conn, ent2a.getId());
			assert(count == 2);
			
			/* Check if none with 2a. */
			T res = getManager().load(conn, new Tuple<Long, Long>(ent1b.getId(), ent2a.getId()));
			assert(res == null);
			res = getManager().load(conn, new Tuple<Long, Long>(ent1c.getId(), ent2a.getId()));
			assert(res == null);
		}
		
	}
	
	/** @return the database to use */
	protected Database getDatabase() { return database; };
	
	/** Create an instance of the first Entity */
	protected abstract Entity createFirstEntity(Connection conn) throws SQLException;
	/** Create an instance of the second Entity */
	protected abstract Entity createSecondEntity(Connection conn) throws SQLException;
	
	/** Create and populate a new entry
	 * @return created {@link ComposedEntity} */
	protected abstract T createEntity() throws SQLException;
	
	/** Create and populate a new entry composed with specific First and Second Entitites
	 * @param first {@link Entity} as first component.
	 * @param second {@link Entity} as second component.
	 * @return created {@link ComposedEntity} */
	protected abstract T createEntity(Entity first, Entity second);
	
	/** Change values from an entity (not its Id).
	 * @param entity entity to change its values. */
	protected abstract void changeEntityValues(T entity);
	
	/** @return Manager of the entity */
	protected abstract ComposedEntityManager<T> getManager();
	
	/** Check if two Entity instances have the same values.<br/>
	 * <b>Note:</b> No need to check if entities are null or check its ids. Just its values.
	 * @param ent1 first instance.
	 * @param ent2 second instance.
	 * @return if have the same values or not. */
	protected abstract boolean haveEqualValues(T ent1, T ent2);
	
}
