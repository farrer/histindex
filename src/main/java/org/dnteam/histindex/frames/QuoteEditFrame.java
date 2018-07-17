package org.dnteam.histindex.frames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Comparator;

import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.BookAuthorManager;
import org.dnteam.histindex.database.BookManager;
import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Keyword;
import org.dnteam.histindex.database.Quote;
import org.dnteam.histindex.database.QuoteKeyword;
import org.dnteam.histindex.database.QuoteKeywordManager;
import org.dnteam.histindex.database.QuoteManager;
import org.dnteam.histindex.database.Source;
import org.dnteam.histindex.database.SourceManager;
import org.dnteam.histindex.util.StringUtil;
import org.dnteam.histindex.widgets.KeywordSelector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/** Edit frame for {@link Quote}s.
 * @author farrer */
public class QuoteEditFrame extends BaseEditFrame<Quote> {

	private TextArea quoteText;
	private TextField quoteComment;
	private TextField page;
	
	private ChoiceBox<Book> bookSelector;
	private ObservableList<Book> allBooks;
	
	private ChoiceBox<Source> sourceSelector;
	private ObservableList<Source> allSources;
	
	private KeywordSelector keywordSelector;
	
	/** Constructor.
	 * @param database database to use.
	 * @param quote quote to update.
	 * @param caller if was called from a list or not. */
	public QuoteEditFrame(Database database, Quote quote, BaseSelectFrame<Quote> caller) {
		super(database, "Quote", quote, caller);
	}

	/** {@inheritDoc} */
	@Override
	public void addFields(GridPane grid, Quote quote) {

		try(Connection conn = DriverManager.getConnection(database.getURL())) {
			allBooks = FXCollections.observableArrayList(BookManager.getSingleton().loadAll(conn));
			BookAuthorManager.getSingleton().populateAuthors(conn, allBooks);
			allSources = FXCollections.observableArrayList(SourceManager.getSingleton().loadAll(conn));
			keywordSelector = new KeywordSelector(conn, grid, 0, 4, quote.getKeywordsCopy(), false);
			
			/* Populate entity, if not already populated */
			if((quote.getBookId() != 0) && (quote.getBook() == null)) {
				quote.setBook(BookManager.getSingleton().load(conn, quote.getBookId()));
				BookAuthorManager.getSingleton().populateAuthors(conn, quote.getBook());
				QuoteKeywordManager.getSingleton().populateKeywords(conn, quote);
			} else if((quote.getSourceId() != 0) && (quote.getSource() == null)){
				quote.setSource(SourceManager.getSingleton().load(conn, quote.getSourceId()));
				QuoteKeywordManager.getSingleton().populateKeywords(conn, quote);
			}
		} catch (SQLException e) {
			showError("Couldn't load books, sources or keywords (" + e.getMessage() + ")");
		}
		allSources.add(0, null);
		allBooks.add(0, null);
		allBooks.sort(new Comparator<Book>() {
			@Override
			public int compare(Book o1, Book o2) {
				if((o1 != null) && (o2 != null)) {
					return o1.getDescription().compareTo(o2.getDescription());
				}
				if(o1 == null) {
					return -1;
				}
				return 1;
			}
		});
		
		quoteText = new TextArea();
		quoteText.setWrapText(true);
		quoteText.setText(quote.getText());
		grid.add(new Label("Quote: "), 0, 0);
		grid.add(quoteText, 1, 0);
		
		page = new TextField();
		page.setText(quote.getPage());
		grid.add(new Label("Page: "), 0, 1);
		grid.add(page, 1, 1);
		
		bookSelector = new ChoiceBox<Book>(allBooks);
		grid.add(new Label("Book/Article: "), 0, 2);
		grid.add(bookSelector, 1, 2);
		if(quote.getBookId() != 0) {
			bookSelector.setValue(quote.getBook());
		}
		
		sourceSelector = new ChoiceBox<Source>(allSources);
		grid.add(new Label("Source: "), 0, 3);
		grid.add(sourceSelector, 1, 3);
		if(quote.getSourceId() != 0) {
			sourceSelector.setValue(quote.getSource());
		}
		
		quoteComment = new TextField();
		quoteComment.setText(quote.getComment());
		grid.add(new Label("Comment: "), 0, 7);
		grid.add(quoteComment, 1, 7);
	}

	/** {@inheritDoc} */
	@Override
	public boolean populateAndValidateEntity(Quote quote) {
		if(StringUtil.isEmpty(quoteText.getText())) {
			showError("A quote must have its own text.");
			return false;
		}
		
		if( ((bookSelector.getValue() == null) && (sourceSelector.getValue() == null)) ||
			 ((bookSelector.getValue() != null) && (sourceSelector.getValue() != null))	){
			showError("A quote should be either from a book/article or a source.");
			return false;
		}
		
		quote.setText(quoteText.getText());
		quote.setComment(quoteComment.getText());
		quote.setPage(page.getText());
		quote.setBook(bookSelector.getValue());
		quote.setSource(sourceSelector.getValue());
		
		quote.clearKeywords();
		for(Keyword key : keywordSelector.getSelected()) {
			quote.addKeyword(key);
		}
		
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager<Quote> getManager() {
		return QuoteManager.getSingleton();
	}
	
	/** {@inheritDoc} */
	@Override
	public void doBeforeSave(Connection con, Quote quote) throws SQLException {
		if(quote.getId() != 0) {
			/* Updating a quote, must delete it's no more in use keywords.
			 * Note: deleting all, to avoid incoherences (and simplifying our check). */
			QuoteKeywordManager.getSingleton().deleteKeywordsFromQuote(con, quote.getId());
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void doAfterSave(Connection con, Quote quote) throws SQLException {
		QuoteKeywordManager qkManager = QuoteKeywordManager.getSingleton();
		/* Let's insert each keyword */
		for(Keyword key : keywordSelector.getSelected()) {
			QuoteKeyword qk = new QuoteKeyword(quote, key);
			qkManager.insert(con, qk);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void doBeforeDelete(Connection con, Quote quote) throws SQLException {
		/* We should delete all QuoteKeyword information for this quote */
		QuoteKeywordManager.getSingleton().deleteKeywordsFromQuote(con, quote.getId());
	}

}
