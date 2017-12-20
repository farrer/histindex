package org.dnteam.histindex;

import java.sql.SQLException;

import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Quote;
import org.dnteam.histindex.database.QuoteManager;
import org.dnteam.histindex.generators.QuoteGenerator;
import org.dnteam.histindex.util.StringUtil;

public class QuoteManagerTestCase extends EntityManagerTestCase<Quote> {

	public QuoteManagerTestCase() throws SQLException {
		super("quotetest.db");
	}

	@Override
	protected Quote createEntity() {
		QuoteGenerator qg = new QuoteGenerator();
		return qg.getEntity();
	}

	@Override
	protected void changeEntityValues(Quote quote) {
		quote.setPage("32");
		quote.setText("Another quoted text");
		quote.setComment("This should be useful.");	
	}

	@Override
	protected EntityManager<Quote> getManager() {
		return QuoteManager.getSingleton();
	}

	@Override
	protected boolean haveEqualValues(Quote ent1, Quote ent2) {
		return ent1.getBookId() == ent2.getBookId() && ent1.getSourceId() == ent2.getSourceId() && 
				StringUtil.isEqual(ent1.getText(), ent2.getText()) && 
				StringUtil.isEqual(ent1.getPage(), ent2.getPage()) &&
				StringUtil.isEqual(ent1.getComment(), ent2.getComment());
	}
	
	public void testDeleteAllQuotesFromBook() {
		//TODO
	}
	
	public void testDeleteAllQuotesFromSource() {
		//TODO
	}

}
