package org.dnteam.histindex.generators;

import java.sql.Connection;
import java.sql.SQLException;

import org.dnteam.histindex.database.Source;
import org.dnteam.histindex.database.SourceManager;

public class SourceGenerator extends EntityGenerator<Source> {
	
	private final Source source = new Source();
	private boolean definedTitle = false;
	private boolean definedExtraInfo = false;
	
	/** Define Source's title.
	 * @param title new title.
	 * @return this. */
	public SourceGenerator withTitle(String title) {
		source.setTitle(title);
		definedTitle = true;
		return this;
	}
	
	/** Define Source extra information.
	 * @param info new information.
	 * @return this. */
	public SourceGenerator withExtraInfo(String info) {
		source.setExtraInfo(info);
		definedExtraInfo = true;
		return this;
	}

	@Override
	public SourceManager getManager() {
		return SourceManager.getSingleton();
	}

	@Override
	public Source getEntity() {
		if(!definedTitle) {
			source.setTitle("Document about rules on nothing");
		}
		if(!definedExtraInfo) {
			source.setExtraInfo("Without author, probably from the 1750s");
		}
		return source;
	}

	@Override
	protected void persistRelations(Connection conn) throws SQLException {		
	}

}
