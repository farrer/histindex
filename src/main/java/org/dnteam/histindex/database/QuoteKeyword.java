package org.dnteam.histindex.database;

/** A {@link Quote} - {@link Keyword} relational entity.
 * @Keyword farrer */
public class QuoteKeyword extends ComposedEntity<Quote, Keyword> {
	
	/** Default Contructor. */
	public QuoteKeyword() {
	}
	
	/** Constructor setting Quote and Keyword */
	public QuoteKeyword(Quote b, Keyword a) {
		setQuote(b);
		setKeyword(a);
	}
	
   public Quote getQuote() {
   	return getFirstEntity();
   }
   
   public void setQuote(Quote Quote) {
   	setFirstEntity(Quote);
   }
   
   public Keyword getKeyword() {
   	return getSecondEntity();
   }
   
   public void setKeyword(Keyword Keyword) {
   	setSecondEntity(Keyword);
   }
   
   public Long getQuoteId() {
   	return getKey().getFirst();
   }
   
   public Long getKeywordId() {
   	return getKey().getSecond();
   }

	@Override
	public EntityManager<Quote> getFirstEntityManager() {
		return QuoteManager.getSingleton();
	}

	@Override
	public EntityManager<Keyword> getSecondEntityManager() {
		return KeywordManager.getSingleton();
	}

}
