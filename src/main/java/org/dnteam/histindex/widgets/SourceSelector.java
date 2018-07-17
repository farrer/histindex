package org.dnteam.histindex.widgets;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Source;
import org.dnteam.histindex.database.SourceManager;

import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;

/** Widget for selecting 'n' {@link Source}s from the database.
 * @author farrer */
public class SourceSelector extends EntitySelector<Source> {

	public SourceSelector(Connection conn, GridPane grid, int gridX, int gridY,
	      ObservableList<Source> current) throws SQLException {
		super(conn, grid, gridX, gridY, "Source", "Source(s)", "title", current, false);
	}

	/** {@inheritDoc} */
	@Override
	protected EntityManager<Source> getEntityManager() {
		return SourceManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	protected void loadRelated(Connection conn, List<Source> values) throws SQLException {
	}

}
