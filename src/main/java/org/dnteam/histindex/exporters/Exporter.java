package org.dnteam.histindex.exporters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dnteam.histindex.HistIndexProperties;
import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.Book;
import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.database.Keyword;
import org.dnteam.histindex.database.Quote;
import org.dnteam.histindex.database.Source;
import org.dnteam.histindex.util.StringUtil;

import net.sf.nervalreports.core.ReportColors;
import net.sf.nervalreports.core.ReportGenerationException;
import net.sf.nervalreports.core.ReportGenerator;
import net.sf.nervalreports.core.ReportTextAlignment;

/** Basic class for exporters implementation.
 * @author farrer */
public abstract class Exporter { 
	
	/** The list of quotes to export. */
	protected final List<Quote> quotes;
	/** The generator to use. */
	protected final ReportGenerator generator;
	/** The Database used */
	protected final Database database;
	
	/** Constructor.
	 * @param database {@link Database} used.
	 * @param quotes Collection with quotes to export. */
	public Exporter(Database database, Collection<Quote> quotes) {
		this.database = database;
		this.generator = getGenerator();
		this.quotes = new ArrayList<Quote>(quotes);
	}
	
	/** Export the generated to a file. <br/>
	 * <b>Note:</b> should be called after a successful {@link #generate()}.
	 * @param filepath path of the file to export.
	 * @throws ReportGenerationException */
	public void export(String filepath) throws ReportGenerationException {
		generator.saveToFile(filepath);
		generator.release();
	}
	
	/** Release and close our references */
	public void release() {
		generator.release();
	}

	/** Generate the export to the ReportGenerator. But without saving it yet to a file.
	 * @param searchKeywords {@link Keyword}s that was used on search query, if any.
	 * @param searchBooks {@link Book}s that was used on search query, if any.
	 * @param searchAuthors {@link Author}s that was used on search query, if any.
	 * @param searchSources {@link Source}s that was used on search query, if any.
	 * @param searchText String with text that was used on search query, as a like statement.
	 * @throws ReportGenerationException */
	public void generate(Collection<Keyword> searchKeywords, Collection<Book> searchBooks, 
			Collection<Author> searchAuthors, Collection<Source> searchSources, String searchText) 
					throws ReportGenerationException {
		generator.beginDocument();
		
		generator.setMargins(2, 2, 2, 2);
		
		generator.beginDocumentHead();
		try {
			setFonts(generator);
		} catch (IOException e) {
			throw new ReportGenerationException(e);
		}
		if(generator.isHeaderAndFooterDeclarationAtDocumentHead()) {
			generateHeaderAndFooter(generator);
		}
		generator.endDocumentHead();
		
		
		/* Our body */
		generator.beginDocumentBody();
		
			if(!generator.isHeaderAndFooterDeclarationAtDocumentHead()) {
				generateHeaderAndFooter(generator);
			}
		
			/* Search Information */
			generateSearchInformation(searchKeywords, searchBooks, searchAuthors, searchSources, searchText);
			
			/* We should use tables with borders from now on. */
			generator.setTableBorderStyle(0.5f, ReportColors.BLACK);
			
			/* Finally, our data. */
			generateQuotesData();
		
		generator.endDocumentBody();
		
		generator.endDocument();
	}
	
	/** Generate our headers and footers. As a function as some {@link ReportGenerator}s need it
	 * on document head, while others at the document's body.
	 * @param generator {@link ReportGenerator} to use.
	 * @throws ReportGenerationException while creating the header or footer. */
	protected void generateHeaderAndFooter(ReportGenerator generator) throws ReportGenerationException {
		generator.beginPageHeaderLeft();
			generator.addText("HistIndex ");
			generator.addText(HistIndexProperties.getVersion());
			generator.addSeparatorLine();
		generator.endPageHeaderLeft();
	
		/* Our footer, with generation time, version and pages */
		generator.beginPageFooterLeft();
			generator.addText(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
			generator.addText(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
		generator.endPageFooterLeft();
	
		generator.beginPageFooterRight();
			generator.addCurrentPageNumber();
			generator.addText(" of ");
			generator.addTotalPagesCount();
		generator.endPageFooterRight();
	}
	
	/** Generate information for a list of {@link Entity}ies used at the search.
	 * @param generator Generator to use.
	 * @param entityName name of the entity (example: "Keywords");
	 * @param entities collection of entities.
	 * @throws ReportGenerationException if error while populating the {@link ReportGenerator}. */
	@SuppressWarnings("rawtypes")
	protected void generateSearchInfoForEntity(ReportGenerator generator, 
			String entityName, Collection entities) throws ReportGenerationException {
		generator.setBold(true);
		generator.addText(entityName + ":");
		generator.setBold(false);
		StringBuilder builder = new StringBuilder();
		for(Object o : entities) {
			Entity e = (Entity) o;
			if(builder.length() > 0) {
				builder.append("; ");
			}
			builder.append(e.toString());
		}
		if(builder.length() > 0) {
			generator.addText(builder.toString());
		} else {
			generator.addText("Any");
		}
	}
	
	/** Generate a information about the search that originated this data. */
	protected void generateSearchInformation(Collection<Keyword> searchKeywords, Collection<Book> searchBooks, 
			Collection<Author> searchAuthors, Collection<Source> searchSources, String searchText) throws ReportGenerationException {
		generator.setBold(true);
		generator.addText("Text:");
		generator.setBold(false);
		if(!StringUtil.isEmpty(searchText)) {
			generator.addText(searchText);
		} else {
			generator.addText("*");
		}
		generator.addLineBreak();
		
		generateSearchInfoForEntity(generator, "Keywords", searchKeywords);
		generator.addLineBreak();
		generateSearchInfoForEntity(generator, "Authors", searchAuthors);
		generator.addLineBreak();
		generateSearchInfoForEntity(generator, "Books", searchBooks);
		generator.addLineBreak();
		generateSearchInfoForEntity(generator, "Sources", searchSources);
		generator.addLineBreak();
		generator.setBold(true);
		generator.addText("Database:");
		generator.setBold(false);
		generator.addText(database.getFilename());
	}
	
	/** Generate the tables for our Quotes, grouped by Book/Source. */
	private void generateQuotesData() throws ReportGenerationException {
		long lastBookId = 0;
		long lastSourceId = 0;
		boolean openedTable = false;
		for(Quote quote : quotes) {
			if((lastBookId != quote.getBookId()) || (lastSourceId != quote.getSourceId())) {
				/* We are at a new source of new book */
				if(openedTable) {
					generator.endTable();
					generator.addLineBreak();
				}
				lastBookId = quote.getBookId();
				lastSourceId = quote.getSourceId();					
				
				int delta = Math.round((generator.getPaperFormat().getWidth() - 30) / 8);
				generator.beginTable(3, delta * 5, delta, delta * 2);
				openedTable = true;
				
				/* Our header as the document */
				generator.setBold(true);
				generator.setItalic(true);
				generator.setTextColor(ReportColors.WHITE);
				generator.beginTableHeaderRow();
				generator.setTextAlignment(ReportTextAlignment.CENTER);
				generator.addTableHeaderCell(quote.getWhereDescription(), 3);
				generator.endTableHeaderRow();
				
				/* Second header line with titles */
				generator.setItalic(false);
				generator.beginTableHeaderRow();
				generator.addTableHeaderCell("Text");
				generator.addTableHeaderCell("Page(s)");
				generator.addTableHeaderCell("Keyword(s)");
				generator.endTableHeaderRow();
			}
			
			generator.setBold(false);
			generator.setTextColor(ReportColors.DEFAULT_TEXT_COLOR);
			generator.beginTableRow();
			
			generator.setTextAlignment(ReportTextAlignment.LEFT);
			generator.beginTableCell();
			generator.addText(quote.getText());
			if(!StringUtil.isEmpty(quote.getComment())) {
				generator.setItalic(true);
				generator.addText("[" + quote.getComment() + "]");
				generator.setItalic(false);
			}
			generator.endTableCell();
			
			generator.setTextAlignment(ReportTextAlignment.CENTER);
			if(!StringUtil.isEmpty(quote.getPage())) {
				generator.addTableCell(quote.getPage());
			} else {
				generator.addTableCell("Undefined");
			}
			String keywords = quote.getKeywords();
			if(StringUtil.isEmpty(keywords)) {
				keywords = "None";
			}
			generator.addTableCell(keywords);
			
			generator.endTableRow();
		}
		if(openedTable) {
			generator.endTable();
		}
	}
	
	/**  @return the {@link ReportGenerator} to use at the export process. Only called once. */
	protected abstract ReportGenerator getGenerator();
	
	/** Set any needed font to use. */
	protected abstract void setFonts(ReportGenerator generator) throws ReportGenerationException, IOException ;
}
