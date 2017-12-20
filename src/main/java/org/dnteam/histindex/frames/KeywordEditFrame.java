package org.dnteam.histindex.frames;

import java.sql.Connection;
import java.sql.SQLException;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Keyword;
import org.dnteam.histindex.database.KeywordManager;
import org.dnteam.histindex.database.QuoteKeywordManager;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/** Frame used to get user input and insert a new keyword on the database.
 * @author farrer */
public class KeywordEditFrame extends BaseEditFrame<Keyword> {
	
	private TextField name;

	/** Constructor.
	 * @param database database to use.
	 * @param keyword to edit or null for a new keyword insertion. 
	 * @param caller the {@link KeywordListFrame} which called us, or null. */
	public KeywordEditFrame(Database database, Keyword keyword, KeywordListFrame caller) {
		super(database, "Keyword", keyword, caller);
	}
	
	/** {@inheritDoc} */
	@Override
	public void addFields(GridPane grid, Keyword keyword) {
		name = new TextField();
		name.setText(keyword.getName());
		grid.add(new Label("Name: "), 0, 0);
		grid.add(name, 1, 0);
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager<Keyword> getManager() {
		return KeywordManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	public boolean populateAndValidateEntity(Keyword keyword) {
		if(name.getText() == null || name.getText().isEmpty()) {
			showError("Keyword name should be defined");
			return false;
		}
		
		keyword.setName(name.getText());
		return true;
	}
	
	/** {@inheritDoc} */
	@Override
	public void doBeforeDelete(Connection con, Keyword keyword) throws SQLException {
		/* Remove all QuoteKeywords with this keyword. */
		QuoteKeywordManager.getSingleton().deleteKeywordFromQuotes(con, keyword.getId());
	}

}
