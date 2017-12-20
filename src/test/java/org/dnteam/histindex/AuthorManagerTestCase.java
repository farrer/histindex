package org.dnteam.histindex;

import java.sql.SQLException;

import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.AuthorManager;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.generators.AuthorGenerator;
import org.dnteam.histindex.util.StringUtil;

public class AuthorManagerTestCase extends EntityManagerTestCase<Author> {

	public AuthorManagerTestCase() throws SQLException {
		super("authortestcase.db");
	}

	@Override
	protected Author createEntity() {
		AuthorGenerator ag = new AuthorGenerator();
		return ag.withName("Felisberto das").withLastName("Couves").getEntity();
	}

	@Override
	protected void changeEntityValues(Author author) {
		author.setName("Felix das");
		author.setLastName("Alfaces");
	}

	@Override
	protected EntityManager<Author> getManager() {
		return AuthorManager.getSingleton();
	}

	@Override
	protected boolean haveEqualValues(Author ent1, Author ent2) {
		return StringUtil.isEqual(ent1.getName(), ent2.getName()) && StringUtil.isEqual(ent1.getLastName(), ent2.getLastName());
	}

}
