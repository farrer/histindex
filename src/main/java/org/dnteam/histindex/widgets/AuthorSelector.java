package org.dnteam.histindex.widgets;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.AuthorManager;
import org.dnteam.histindex.database.EntityManager;

import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;

/** Widget for selecting 'n' {@link Author}s from the database.
 * @author farrer */
public class AuthorSelector extends EntitySelector<Author> {

	/** @see EntitySelector#EntitySelector */
	public AuthorSelector(Connection conn, GridPane grid, int gridX, int gridY, 
			ObservableList<Author> current) throws SQLException {
		super(conn, grid, gridX, gridY, "Author", "Author(s)", "fullName", current);
	}

	/** {@inheritDoc} */
	@Override
	protected EntityManager<Author> getEntityManager() {
		return AuthorManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	protected void loadRelated(Connection conn, List<Author> values) throws SQLException {
	}
	

}
