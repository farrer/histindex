package org.dnteam.histindex.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** A quick abstraction for keeping and accessing our database */
public class Database {

	private static String DRIVER = "jdbc:sqlite:";

	/** Filename (with path) of the database to use */
	private String filename;
	/** Jdbc url to use */
	private String url;

	/** Constructor
	 * @param filename filename (with path) of the SQlite database to use */
	public Database(String filename) {
		this.filename = filename;
		this.url = DRIVER + this.filename;
	}
	
	/** @return {@link #url} */
	public String getURL() {
	   return url;
	}
	
	/** @return {@link #filename} */
	public String getFilename() {
		return filename;
	}

	/** Create the database schema for a new database */
	public void createSchema() throws SQLException {

		try (Connection c = DriverManager.getConnection(url); 
				Statement stmt = c.createStatement()) {

			/* Authors */
			stmt.execute("DROP TABLE IF EXISTS Authors;");
			stmt.execute("CREATE TABLE IF NOT EXISTS "
			      + "Authors(Id INTEGER PRIMARY KEY AUTOINCREMENT, Name VarChar(255), LastName VarChar(255));");
			/* Books */
			stmt.execute("DROP TABLE IF EXISTS Books;");
			stmt.execute("CREATE TABLE IF NOT EXISTS "
					+ "Books(Id INTEGER PRIMARY KEY AUTOINCREMENT, Title VarChar(255));");
			/* Sources */
			stmt.execute("DROP TABLE IF EXISTS Sources;");
			stmt.execute("CREATE TABLE IF NOT EXISTS "
					+ "Sources(Id INTEGER PRIMARY KEY AUTOINCREMENT, Title VarChar(255), ExtraInfo VarChar(255));");
			/* BookAuthor */
			stmt.execute("DROP TABLE IF EXISTS BookAuthor;");
			stmt.execute("CREATE TABLE IF NOT EXISTS "
					+ "BookAuthor(BookId INTEGER, AuthorId INTEGER, "
					+ "FOREIGN KEY(BookId) REFERENCES Books(Id), FOREIGN KEY(AuthorId) REFERENCES Authors(Id));");
			/* Keywords */
			stmt.execute("DROP TABLE IF EXISTS Keywords;");
			stmt.execute("CREATE TABLE IF NOT EXISTS "
					+ "Keywords(Id INTEGER PRIMARY KEY AUTOINCREMENT, Name VarChar(255));");
			/* Quotes */
			stmt.execute("DROP TABLE IF EXISTS Quotes;");
			stmt.execute("CREATE TABLE IF NOT EXISTS "
					+ "Quotes(Id INTEGER PRIMARY KEY AUTOINCREMENT, Text, BookId INTEGER, SourceId INTEGER, Page TEXT, Comment TEXT, "
					+ "FOREIGN KEY(BookId) REFERENCES Books(Id), FOREIGN KEY(SourceId) REFERENCES Sources(Id));");
			/* QuoteKeyword */
			stmt.execute("DROP TABLE IF EXISTS QuoteKeyword;");
			stmt.execute("CREATE TABLE IF NOT EXISTS "
					+ "QuoteKeyword(QuoteId INTEGER, KeywordId INTEGER, "
				   + "FOREIGN KEY(QuoteId) REFERENCES Quotes(Id), FOREIGN KEY (KeywordId) REFERENCES Keywords(Id));");
		}

	}

}
