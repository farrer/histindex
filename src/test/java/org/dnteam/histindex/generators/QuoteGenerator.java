package org.dnteam.histindex.generators;

import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Quote;
import org.dnteam.histindex.database.QuoteManager;
import org.dnteam.histindex.database.Source;

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


	@Override
	public EntityManager<Quote> getManager() {
		return QuoteManager.getSingleton();
	}

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

}
