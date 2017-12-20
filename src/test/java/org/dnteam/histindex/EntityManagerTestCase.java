package org.dnteam.histindex;

import java.sql.SQLException;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.database.EntityManager;

import junit.framework.TestCase;

/** Base class for {@link EntityManager}s test case implementations.
 * @author farrer
 *
 * @param <T> type of the {@link Entity}. */
public abstract class EntityManagerTestCase<T extends Entity> extends TestCase {
	private Database database;
	
	/** Constructor.
	 * @param filename URL of the database to use.
	 * @throws SQLException	 */
	public EntityManagerTestCase(String filename) throws SQLException {
		this.database = new Database(filename);
		this.database.createSchema();
	}
	
	/** Test insertion of a {@link Entity}. */
	public void testInsert() throws SQLException {
		T entity = createEntity();
		getManager().insert(getDatabase(), entity);
		
	   assert(entity.getId() != 0);
	}
	
	/** Test selection of an {@link Entity} by its Id. */
	public void testSelect() throws SQLException	{
		T entity = createEntity();
		getManager().insert(getDatabase(), entity);
		
		T loaded = getManager().load(getDatabase(), entity.getId());
		
		assert(loaded != null);
		assert(loaded.getId() == entity.getId());
		assert(haveEqualValues(entity, loaded));
	}
	
	/** Test update of a modified {@link Entity}.
	 * @throws SQLException	 */
	public void testUpdate() throws SQLException	{
		T entity = createEntity();
		getManager().insert(getDatabase(), entity);
		
		changeEntityValues(entity);
		getManager().update(getDatabase(), entity);
		
		T loaded = getManager().load(getDatabase(), entity.getId());
		
		assert(loaded != null);
		assert(loaded.getId() == entity.getId());
		assert(haveEqualValues(entity, loaded));
	}
	
	/** Test deletion of a {@link Entity} by its Id. 
	 * @throws SQLException	 */
	public void testDelete() throws SQLException	{
		T entity = createEntity();
		getManager().insert(getDatabase(), entity);
		
		boolean deleted = getManager().delete(getDatabase(), entity.getId());
		assert(deleted);
		
		T loaded = getManager().load(getDatabase(), entity.getId());
		assert(loaded == null);
	}
	
	/** @return {@link Database} to use */
	protected Database getDatabase() { return database; };
	
	/** Create and populate a new entry
	 * @return created entry. */
	protected abstract T createEntity();
	
	/** Change values from an entity (not its Id).
	 * @param entity entity to change its values. */
	protected abstract void changeEntityValues(T entity);
	
	/** @return Manager of the entity */
	protected abstract EntityManager<T> getManager();
	
	/** Check if two Entity instances have the same values.<br/>
	 * <b>Note:</b> No need to check if entities are null or check its ids. Just its values.
	 * @param ent1 first instance.
	 * @param ent2 second instance.
	 * @return if have the same values or not. */
	protected abstract boolean haveEqualValues(T ent1, T ent2);
	
}
