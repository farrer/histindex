package org.dnteam.histindex.generators;

import org.dnteam.histindex.database.Keyword;
import org.dnteam.histindex.database.KeywordManager;

public class KeywordGenerator extends EntityGenerator<Keyword> {
	
	private final Keyword keyword = new Keyword();
	boolean nameIsSet = false;
	
	public KeywordGenerator withName(String name) {
		keyword.setName(name);
		nameIsSet = true;
		return this;
	}

	@Override
	public KeywordManager getManager() {
		return KeywordManager.getSingleton();
	}

	@Override
	public Keyword getEntity() {
		if(!nameIsSet) {
			withName("Ivory");
		}
		return keyword;
	}

}
