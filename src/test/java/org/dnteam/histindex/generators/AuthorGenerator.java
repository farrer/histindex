package org.dnteam.histindex.generators;

import java.sql.Connection;
import java.sql.SQLException;

import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.AuthorManager;

public class AuthorGenerator extends EntityGenerator<Author> {
	
	private final Author author = new Author();
	boolean nameIsSet = false;
	boolean lastNameIsSet = false;
	
	public AuthorGenerator withName(String name) {
		author.setName(name);
		nameIsSet = true;
		return this;
	}
	
	public AuthorGenerator withLastName(String surname) {
		author.setLastName(surname);
		lastNameIsSet = true;
		return this;
	}

	@Override
	public AuthorManager getManager() {
		return AuthorManager.getSingleton();
	}

	@Override
	public Author getEntity() {
		if(!nameIsSet) {
			withName("João");
		}
		if(!lastNameIsSet) {
			withLastName("Horteleiro");
		}
		return author;
	}

	@Override
	protected void persistRelations(Connection conn) throws SQLException {
	}

}
