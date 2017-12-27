package org.dnteam.histindex.generators;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.BookManager;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Keyword;
import org.dnteam.histindex.database.KeywordManager;
import org.dnteam.histindex.database.Quote;
import org.dnteam.histindex.database.QuoteKeyword;
import org.dnteam.histindex.database.QuoteKeywordManager;
import org.dnteam.histindex.database.QuoteManager;
import org.dnteam.histindex.database.Source;
import org.dnteam.histindex.database.SourceManager;

public class QuoteGenerator extends EntityGenerator<Quote> {
	
	/** The entity itself */
	private Quote quote = new Quote();
	private boolean textIsSet = false;
	private boolean pageIsSet = false;
	
	public QuoteGenerator withText(String text) {
		quote.setText(text);
		textIsSet = true;
		return this;
	}
	
	public QuoteGenerator withComment(String text) {
		quote.setComment(text);
		return this;
	}
	
	public QuoteGenerator withPage(String page) {
		quote.setPage(page);
		pageIsSet = true;
		return this;
	}
	
	public QuoteGenerator withBook(Book book) {
		quote.setBook(book);
		return this;
	}
	
	public QuoteGenerator withSource(Source source) {
		quote.setSource(source);
		return this;
	}
	
	public QuoteGenerator withKeywords(Collection<Keyword> keys) {
		for(Keyword key : keys) {
			quote.addKeyword(key);
		}
		return this;
	}
	
	public QuoteGenerator withKeyword(Keyword key) {
		quote.addKeyword(key);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager<Quote> getManager() {
		return QuoteManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	public Quote getEntity() {
		if(!textIsSet) {
			withText("Satanum sum et nihil humanum a me alienum puto.");
		}
		if(!pageIsSet) {
			withPage("98");
		}
		return quote;
	}
	
	/** {@inheritDoc} */
	@Override
	public void persistRelations(Connection conn) throws SQLException {
		if((quote.getBook() != null) && (quote.getBook().getId() == 0)) {
			BookManager.getSingleton().insert(conn, quote.getBook());
		}
		if((quote.getSource() != null) && (quote.getSource().getId() == 0)) {
			SourceManager.getSingleton().insert(conn, quote.getSource());
		}
		for(Keyword key : quote.getKeywordsCopy()) {
			if(key.getId() == 0) {
				/* Insert the keyword */
				KeywordManager.getSingleton().insert(conn, key);
			}
			/* Add the QuoteKeyword relation */
			QuoteKeywordManager.getSingleton().insert(conn, new QuoteKeyword(quote, key));
		}
	}

}
