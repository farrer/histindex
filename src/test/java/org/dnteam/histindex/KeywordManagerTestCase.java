package org.dnteam.histindex;

import java.sql.SQLException;

import org.dnteam.histindex.database.Keyword;
import org.dnteam.histindex.database.KeywordManager;
import org.dnteam.histindex.generators.KeywordGenerator;
import org.dnteam.histindex.util.StringUtil;

public class KeywordManagerTestCase extends EntityManagerTestCase<Keyword> {

	public KeywordManagerTestCase() throws SQLException {
		super("keywordtestcase.db");
	}

	@Override
	protected Keyword createEntity() {
		KeywordGenerator kg = new KeywordGenerator();
		return kg.getEntity();
	}

	@Override
	protected void changeEntityValues(Keyword entity) {
		entity.setName("Gold");
	}

	@Override
	protected KeywordManager getManager() {
		return KeywordManager.getSingleton();
	}

	@Override
	protected boolean haveEqualValues(Keyword ent1, Keyword ent2) {
		return StringUtil.isEqual(ent1.getName(), ent2.getName());
	}

}
