package org.dnteam.histindex.frames;

import java.sql.Connection;
import java.sql.SQLException;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.QuoteManager;
import org.dnteam.histindex.database.Source;
import org.dnteam.histindex.database.SourceManager;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/** Edit frame for {@link Source}s.
 * @author farrer */
public class SourceEditFrame extends BaseEditFrame<Source> {
	
	private TextField title;
	private TextField extraInfo;

	/** Constructor.
	 * @param database database to use.
	 * @param entity source to edit (null for new one).
	 * @param caller caller list frame, if any. */
	public SourceEditFrame(Database database, Source entity, BaseListFrame<Source> caller) {
		super(database, "Source", entity, caller);
	}

	/** {@inheritDoc} */
	@Override
	public void addFields(GridPane grid, Source source) {
		title = new TextField();
		title.setText(source.getTitle());
		grid.add(new Label("Title: "), 0, 0);
		grid.add(title, 1, 0);
		
		extraInfo = new TextField();
		extraInfo.setText(source.getExtraInfo());
		grid.add(new Label("Extra information: "), 0, 1);
		grid.add(extraInfo, 1, 1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean populateAndValidateEntity(Source source) {
		if(title.getText() == null || title.getText().isEmpty()) {
			showError("Source's title should be defined");
			return false;
		}
		
		source.setTitle(title.getText());
		source.setExtraInfo(extraInfo.getText());
		
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager<Source> getManager() {
		return SourceManager.getSingleton();
	}
	
	/** {@inheritDoc} */
	@Override
	public void doBeforeDelete(Connection con, Source source) throws SQLException {
		/* Should delete Quotes related with this source. */
		QuoteManager.getSingleton().deleteAllQuotesFromSource(con, source.getId());
	}

}
