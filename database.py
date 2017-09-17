#!/usr/bin/env python
import sqlite3

## Simple Class for database connection, closing it on del.
class DataConnection:
   ## Constructor
   # @param databaseName sqlite3 database name to connect to
   def __init__(self, databaseName):
      self.conn = sqlite3.connect(databaseName)

   ## Close the opened sqlite3 database connection
   def CloseConnection(self):
      if(self.conn != None):
         self.conn.close()
         self.conn = None

   ## Destructor
   def __del__(self):
      self.CloseConnection()

## Simple class to create our needed database schema
class DatabaseCreator(DataConnection):
   def __init__(self, databaseName):
      DataConnection.__init__(self, databaseName)

   ## Create the database schema, if not already created.
   def Create(self):
      c = self.conn.cursor()

      # Authors
      c.execute("CREATE TABLE IF NOT EXISTS Authors(Id INTEGER PRIMARY KEY AUTOINCREMENT, Name VarChar(255), LastName VarChar(255));")
      # Books
      c.execute("CREATE TABLE IF NOT EXISTS Books(Id INTEGER PRIMARY KEY AUTOINCREMENT, Title VarChar(255));")
      # Sources
      c.execute("CREATE TABLE IF NOT EXISTS Sources(Id INTEGER PRIMARY KEY AUTOINCREMENT, Title VarChar(255), ExtraInfo VarChar(255));")
      # BookAuthor
      c.execute("CREATE TABLE IF NOT EXISTS BookAuthor(BookId Int, AuthorId Int, FOREIGN KEY(BookId) REFERENCES Books(Id), FOREIGN KEY(AuthorId) REFERENCES Authors(Id));")
      # Keywords
      c.execute("CREATE TABLE IF NOT EXISTS Keywords(Id INTEGER PRIMARY KEY AUTOINCREMENT, Name VarChar(255), SynId int, FOREIGN KEY(SynId) REFERENCES Keywords(Id));")
      # Quotes
      c.execute("CREATE TABLE IF NOT EXISTS Quotes(Id INTEGER PRIMARY KEY AUTOINCREMENT, Text, BookId INTEGER, SourceId INTEGER, Page INTEGER, FOREIGN KEY(BookId) REFERENCES Books(Id), FOREIGN KEY(SourceId) REFERENCES Sources(Id));")
      # QuoteKeyword
      c.execute("CREATE TABLE IF NOT EXISTS QuoteKeyword(QuoteId Int, KeywordId Int, FOREIGN KEY(QuoteId) REFERENCES Quotes(Id), FOREIGN KEY (KeywordId) REFERENCES Keywords(Id));")

      self.conn.commit()
      self.CloseConnection()



