package org.dnteam.histindex.exporters;

import java.io.IOException;
import java.util.Collection;

import org.dnteam.histindex.database.Quote;

import net.sf.nervalreports.core.ReportGenerationException;
import net.sf.nervalreports.core.ReportGenerator;
import net.sf.nervalreports.generators.PDFReportGenerator;

/** An exporter to a .pdf file.
 * @author farrer */
public class PdfExporter extends Exporter {

	public PdfExporter(Collection<Quote> quotes) {
		super(quotes);
	}

	/** {@inheritDoc} */
	@Override
	protected ReportGenerator getGenerator() {
		PDFReportGenerator generator = new PDFReportGenerator();
		generator.setBreakPageInnerTableRow(true);
		
		return generator;
	}

	/** {@inheritDoc} */
	@Override
	protected void setFonts(ReportGenerator generator) throws ReportGenerationException, IOException {
		PDFReportGenerator pdfGenerator = (PDFReportGenerator) generator;
		pdfGenerator.setFonts(ExporterFonts.getNormalFont(), ExporterFonts.getItalicFont(), 
				ExporterFonts.getBoldFont(), ExporterFonts.getBoldItalicFont());
	}

}
