package org.dnteam.histindex;

import java.sql.SQLException;

import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.BookManager;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.generators.BookGenerator;
import org.dnteam.histindex.util.StringUtil;

public class BookManagerTestCase extends EntityManagerTestCase<Book> {
	
	@Override
	protected Book createEntity() {
		BookGenerator bg = new BookGenerator();
		return bg.withTitle("Book Title").getEntity();
	}
	
	@Override
	protected void changeEntityValues(Book entity) {
		entity.setTitle("New Title");
	}
	
	@Override
	protected EntityManager<Book> getManager() {
	   return BookManager.getSingleton();
	}
	
	@Override
	protected boolean haveEqualValues(Book ent1, Book ent2) {
		return StringUtil.isEqual(ent1.getTitle(), ent2.getTitle());
	}

	public BookManagerTestCase() throws SQLException {
		super("booktestcase.db");
	}

}
