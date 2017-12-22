package org.dnteam.histindex.exporters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dnteam.histindex.HistIndexProperties;
import org.dnteam.histindex.database.Quote;
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
	
	/** Constructor.
	 * @param quotes Collection with quotes to export. */
	public Exporter(Collection<Quote> quotes) {
		generator = getGenerator();
		this.quotes = new ArrayList<Quote>(quotes);
	}
	
	/** Export the generated to a file. <br/>
	 * <b>Note:</b> should be called after a successful {@link #generate()}.
	 * @param filepath path of the file to export.
	 * @throws ReportGenerationException */
	public void export(String filepath) throws ReportGenerationException {
		generator.saveToFile(filepath);
	}

	/** Generate the export to the ReportGenerator. But without saving it yet to a file.
	 * @throws ReportGenerationException */
	public void generate() throws ReportGenerationException {
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
		
			generator.addText("First line");
		
			if(!generator.isHeaderAndFooterDeclarationAtDocumentHead()) {
				generateHeaderAndFooter(generator);
			}
			generator.setTableBorderStyle(0.5f, ReportColors.BLACK);
		
			/* Finally, our data. */
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
					generator.addLineBreak();
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
		
		generator.endDocumentBody();
		
		generator.endDocument();
	}
	
	protected void generateHeaderAndFooter(ReportGenerator generator) throws ReportGenerationException {
		generator.beginPageHeaderCenter();
		generator.endPageHeaderCenter();
	
		/* Our footer, with generation time, version and pages */
		generator.beginPageFooterLeft();
			generator.addText(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			generator.addText("HistIndex ");
			generator.addText(HistIndexProperties.getVersion());
		generator.endPageFooterLeft();
	
		generator.beginPageFooterRight();
			generator.addCurrentPageNumber();
			generator.addText(" of ");
			generator.addTotalPagesCount();
		generator.endPageFooterRight();
	}
	
	/**  @return the {@link ReportGenerator} to use at the export process. Only called once. */
	protected abstract ReportGenerator getGenerator();
	
	/** Set any needed font to use. */
	protected abstract void setFonts(ReportGenerator generator) throws ReportGenerationException, IOException ;
}
