package org.dnteam.histindex.exporters;

import java.io.InputStream;

public class ExporterFonts {
	
	private static String normalFont = null;
	private static String italicFont = null;
	private static String boldFont = null;
	private static String boldItalicFont = null;
	
	public static void init(String normal, String italic, String bold, String boldItalic) {
		normalFont = normal;
		italicFont = italic;
		boldFont = bold;
		boldItalicFont = boldItalic;
	}
	
	/** @return {@link #normalFont}. */
	public static InputStream getNormalFont() {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(normalFont);
	}
	
	/** @return {@link #italicFont}. */
	public static InputStream getItalicFont() {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(italicFont);
	}
	
	/** @return {@link #boldFont}. */
	public static InputStream getBoldFont() {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(boldFont);
	}
	
	/** @return {@link #boldItalicFont}. */
	public static InputStream getBoldItalicFont() {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(boldItalicFont);
	}
	
	public static void finish() {
		/* Note: PDFBox which will receive the InputStreams will always close them */
	}

}
