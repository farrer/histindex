package org.dnteam.histindex;

import java.sql.SQLException;

import org.dnteam.histindex.database.Source;
import org.dnteam.histindex.database.SourceManager;
import org.dnteam.histindex.generators.SourceGenerator;
import org.dnteam.histindex.util.StringUtil;

public class SourceManagerTestCase extends EntityManagerTestCase<Source> {

	public SourceManagerTestCase() throws SQLException {
		super("sourcetestcase.db");
	}

	@Override
	protected Source createEntity() {
		SourceGenerator sourceGenerator = new SourceGenerator();
		return sourceGenerator.getEntity();
	}

	@Override
	protected void changeEntityValues(Source entity) {
		entity.setTitle("New title");
		entity.setExtraInfo("Other thing");
	}

	@Override
	protected SourceManager getManager() {
		return SourceManager.getSingleton();
	}

	@Override
	protected boolean haveEqualValues(Source ent1, Source ent2) {
		return StringUtil.isEqual(ent1.getTitle(), ent2.getTitle()) && StringUtil.isEqual(ent1.getExtraInfo(), ent2.getExtraInfo());
	}

}
