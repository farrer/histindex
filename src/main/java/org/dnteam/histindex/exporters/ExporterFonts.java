package org.dnteam.histindex.exporters;

import java.io.IOException;
import java.io.InputStream;

public class ExporterFonts {
	
	private static InputStream normalFont = null;
	private static InputStream italicFont = null;
	private static InputStream boldFont = null;
	private static InputStream boldItalicFont = null;
	
	public static void init(String normal, String italic, String bold, String boldItalic) {
		normalFont = Thread.currentThread().getContextClassLoader().getResourceAsStream(normal);
		italicFont = Thread.currentThread().getContextClassLoader().getResourceAsStream(italic);
		boldFont = Thread.currentThread().getContextClassLoader().getResourceAsStream(bold);
		boldItalicFont = Thread.currentThread().getContextClassLoader().getResourceAsStream(boldItalic);
	}
	
	/** @return {@link #normalFont}. */
	public static InputStream getNormalFont() {
		return normalFont;
	}
	
	/** @return {@link #italicFont}. */
	public static InputStream getItalicFont() {
		return italicFont;
	}
	
	/** @return {@link #boldFont}. */
	public static InputStream getBoldFont() {
		return boldFont;
	}
	
	/** @return {@link #boldItalicFont}. */
	public static InputStream getBoldItalicFont() {
		return boldItalicFont;
	}
	
	public static void finish() {
		/* Note: silencing the exceptions, to make sure all opened fonts are closed, even if on failed. */
		try {
			if(normalFont != null) {
				normalFont.close();
			}
		}
		catch (IOException e) {
		}
		try {
			if(italicFont != null) { 
				italicFont.close();
			}
		}
		catch (IOException e) {
		}
		try {
			if(boldFont != null) {
				boldFont.close();
			}
		}
		catch (IOException e) {
		}
		try {
			if(boldItalicFont != null) {
				boldItalicFont.close();
			}
		}
		catch (IOException e) {
		}
	}

}
