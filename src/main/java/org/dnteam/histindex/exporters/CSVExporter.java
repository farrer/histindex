package org.dnteam.histindex.exporters;

import java.io.IOException;
import java.util.Collection;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.Quote;

import net.sf.nervalreports.core.ReportGenerationException;
import net.sf.nervalreports.core.ReportGenerator;
import net.sf.nervalreports.generators.CSVReportGenerator;

/** Exporter for Comma Separated Values files.
 * @author farrer */
public class CSVExporter extends Exporter {

	/** Constructor.
	 * @param database database to use.
	 * @param quotes list of {@link Quote}s to export. */
	public CSVExporter(Database database, Collection<Quote> quotes) {
		super(database, quotes);
	}

	/** {@inheritDoc} */
	@Override
	protected ReportGenerator getGenerator() {
		return new CSVReportGenerator();
	}

	/** {@inheritDoc} */
	@Override
	protected void setFonts(ReportGenerator generator) throws ReportGenerationException, IOException {
	}

}
