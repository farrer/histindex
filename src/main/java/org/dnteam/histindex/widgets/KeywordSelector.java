package org.dnteam.histindex.widgets;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Keyword;
import org.dnteam.histindex.database.KeywordManager;

import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;

/** Widget for selecting 'n' {@link Keyword}s from the database.
 * @author farrer */
public class KeywordSelector extends EntitySelector<Keyword> {

	public KeywordSelector(Connection conn, GridPane grid, int gridX, int gridY,
	      ObservableList<Keyword> current, boolean andOrRadio) throws SQLException {
		super(conn, grid, gridX, gridY, "Keyword", "Keyword(s)", "name", current, andOrRadio);
	}

	/** {@inheritDoc} */
	@Override
	protected EntityManager<Keyword> getEntityManager() {
		return KeywordManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	protected void loadRelated(Connection conn, List<Keyword> values) throws SQLException {
	}

}
