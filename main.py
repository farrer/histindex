#!/usr/bin/env python
import wx
import sqlite3
import os
from database import DataConnection, DatabaseCreator
from baseframes import InsertEditFrame, ListFrame
from authorframes import AuthorFrame, AuthorListFrame 
from keywordframes import KeywordFrame, KeywordListFrame 
from sourceframes import SourceFrame, SourceListFrame 
from bookframes import BookFrame, BookListFrame 
from quoteframes import QuoteFrame, QuoteListFrame 
from components import AuthorListSelector, BookListSelector, \
                       KeywordListSelector, SourceListSelector
from searchframe import SearchResultFrame

## The main History Indexer Frame interface
class MainFrame(wx.Frame):
   
   def __init__(self):
      wx.Frame.__init__(self, None, title = "HistIndex", size=(600,450))
      self.CreateStatusBar()
      
      self.databaseName = "data.db"
      addIcon = wx.Bitmap('data/icons/add.png')
      databaseAddIcon = wx.Bitmap('data/icons/database_add.png')
      databaseConnectIcon = wx.Bitmap('data/icons/database_connect.png')
      groupIcon = wx.Bitmap('data/icons/group.png')
      userAddIcon = wx.Bitmap('data/icons/user_add.png')
      bookIcon = wx.Bitmap('data/icons/book.png')
      bookAddIcon = wx.Bitmap('data/icons/book_add.png')
      fontIcon = wx.Bitmap('data/icons/font.png')
      fontAddIcon = wx.Bitmap('data/icons/font_add.png')
      vcardIcon = wx.Bitmap('data/icons/vcard.png')
      vcardAddIcon = wx.Bitmap('data/icons/vcard_add.png')
      quoteIcon = wx.Bitmap('data/icons/comment.png')
      quoteAddIcon = wx.Bitmap('data/icons/comment_add.png')

      # Bind event for refresh screen
      self.Bind(wx.EVT_CHAR_HOOK, self.OnKeyUp)

      # File Menu
      fileMenu = wx.Menu()
      newData = fileMenu.Append(wx.ID_NEW, "&New", "Create a new database")
      openData = fileMenu.Append(wx.ID_OPEN, "&Open", 
                                 "Open an existing database")
      fileMenu.AppendSeparator()
      exitItem = fileMenu.Append(wx.ID_EXIT, "E&xit", "Exit HistIndex")
      # File Menu Events
      self.Bind(wx.EVT_MENU, self.OnNewData, newData)
      self.Bind(wx.EVT_MENU, self.OnOpenData, openData)
      self.Bind(wx.EVT_MENU, self.OnExit, exitItem)

      # Reference Menu
      self.ID_INSERT_REF = 0
      self.ID_LIST_REF = 1
      quoteMenu = wx.Menu()
      quoteIns = wx.MenuItem(quoteMenu, self.ID_INSERT_REF, "&Insert", 
                             "Create a new reference")
      quoteIns.SetBitmap(quoteAddIcon)
      quoteMenu.AppendItem(quoteIns)
      quoteList = wx.MenuItem(quoteMenu, self.ID_LIST_REF, "&List", 
                              "List all references")
      quoteList.SetBitmap(quoteIcon)
      quoteMenu.AppendItem(quoteList)
      # Reference Menu Events
      self.Bind(wx.EVT_MENU, self.OnNewQuote, quoteIns)
      self.Bind(wx.EVT_MENU, self.OnListQuote, quoteList)

      # Keyword Menu
      self.ID_INSERT_KEY = 10
      self.ID_LIST_KEY = 11
      keywordMenu = wx.Menu()
      keyIns = wx.MenuItem(keywordMenu, self.ID_INSERT_KEY, "&Insert", 
                           "Create a new keyword")
      keyIns.SetBitmap(fontAddIcon)
      keywordMenu.AppendItem(keyIns)
      keyList = wx.MenuItem(keywordMenu, self.ID_LIST_KEY, "&List", 
                            "List all keywords")
      keyList.SetBitmap(fontIcon)
      keywordMenu.AppendItem(keyList)
      # Keyword Menu Events
      self.Bind(wx.EVT_MENU, self.OnNewKeyword, keyIns)
      self.Bind(wx.EVT_MENU, self.OnListKeyword, keyList)

      # Author Menu
      self.ID_INSERT_AUTHOR = 20
      self.ID_LIST_AUTHOR = 21
      authorMenu = wx.Menu()
      authorIns = wx.MenuItem(authorMenu, self.ID_INSERT_AUTHOR, "&Insert",
                              "Create a new author")
      authorIns.SetBitmap(userAddIcon)
      authorMenu.AppendItem(authorIns)
      authorList = wx.MenuItem(authorMenu, self.ID_LIST_AUTHOR, "&List", 
                               "List all authors")
      authorList.SetBitmap(groupIcon)
      authorMenu.AppendItem(authorList)
      # Author Menu Events
      self.Bind(wx.EVT_MENU, self.OnNewAuthor, authorIns)
      self.Bind(wx.EVT_MENU, self.OnListAuthor, authorList)

      # Book Menu
      self.ID_INSERT_BOOK = 30
      self.ID_LIST_BOOK = 31
      bookMenu = wx.Menu()
      bookIns = wx.MenuItem(bookMenu, self.ID_INSERT_BOOK, "&Insert", 
              "Create a new book or article")
      bookIns.SetBitmap(bookAddIcon)
      bookMenu.AppendItem(bookIns)
      bookList = wx.MenuItem(bookMenu, self.ID_LIST_BOOK, "&List", 
              "List all books/articles")
      bookList.SetBitmap(bookIcon)
      bookMenu.AppendItem(bookList)
      # Book Menu Events
      self.Bind(wx.EVT_MENU, self.OnNewBook, bookIns)
      self.Bind(wx.EVT_MENU, self.OnListBook, bookList)

      # Source Menu
      self.ID_INSERT_SOURCE = 40
      self.ID_LIST_SOURCE = 41
      sourceMenu = wx.Menu()
      sourceIns = wx.MenuItem(sourceMenu, self.ID_INSERT_SOURCE, 
                              "&Insert", "Create a new source")
      sourceIns.SetBitmap(vcardAddIcon)
      sourceMenu.AppendItem(sourceIns)
      sourceList = wx.MenuItem(sourceMenu, self.ID_LIST_SOURCE, "&List", 
                               "List all sources")
      sourceList.SetBitmap(vcardIcon)
      sourceMenu.AppendItem(sourceList)
      # Source Menu Events
      self.Bind(wx.EVT_MENU, self.OnNewSource, sourceIns)
      self.Bind(wx.EVT_MENU, self.OnListSource, sourceList)

      # The Menu Bar with all previous menus
      menuBar = wx.MenuBar()
      menuBar.Append(fileMenu, "&File")
      menuBar.Append(quoteMenu, "&References")
      menuBar.Append(keywordMenu, "&Keywords")
      menuBar.Append(authorMenu, "&Authors")
      menuBar.Append(bookMenu, "&Books and Articles")
      menuBar.Append(sourceMenu, "&Sources")
      self.SetMenuBar(menuBar)

      # A simple text describing our current database
      sizer = wx.BoxSizer(wx.VERTICAL)
      self.databaseNameText = wx.StaticText(self, -1, 
              "Database: " + self.databaseName)
      sizer.Add(self.databaseNameText, 0, wx.EXPAND)

      hozSizer = wx.BoxSizer(wx.HORIZONTAL)
      dataCon = DataConnection(self.databaseName) 
      self.keywordList = KeywordListSelector(self, dataCon.conn, hozSizer)
      self.authorList = AuthorListSelector(self, dataCon.conn, hozSizer)
      sizer.Add(hozSizer, 1, wx.SHAPED)
      
      hozSizer = wx.BoxSizer(wx.HORIZONTAL)
      self.bookList = BookListSelector(self, dataCon.conn, hozSizer)
      self.sourceList = SourceListSelector(self, dataCon.conn, hozSizer)
      sizer.Add(hozSizer, 1, wx.SHAPED)

      hozSizer = wx.BoxSizer(wx.HORIZONTAL)
      self.searchText = wx.TextCtrl(self, size=(300, 26))
      hozSizer.Add(self.searchText, 1, wx.SHAPED)
      buttonSearch = wx.Button(self, -1, "Search")
      self.Bind(wx.EVT_BUTTON, self.OnSearch, buttonSearch)
      hozSizer.Add(buttonSearch, 0, wx.ALIGN_CENTER)
      sizer.Add(hozSizer, 0, wx.SHAPED)

      sizer.SetSizeHints(self)
      self.SetSizer(sizer)
      self.SetAutoLayout(1)
      #sizer.Fit(self)

      self.Show(True)
      dataCon.CloseConnection()

   def OnExit(self, event):
      self.Close()

   def OnSearch(self, event):
      SearchResultFrame(self, self.databaseName, 
                        self.keywordList.GetSelectedIds(),
                        self.authorList.GetSelectedIds(),
                        self.bookList.GetSelectedIds(),
                        self.sourceList.GetSelectedIds(),
                        self.searchText.GetLineText(0))

   def OnNewData(self, event):
      self.databaseName = "data.db"
      self.databaseNameText.SetLabel("Database: " + self.databaseName)
      db = DatabaseCreator("data.db")
      db.Create()
      dlg = wx.MessageDialog(self, 
              "The database was created.", "Success", wx.OK)
      dlg.ShowModal()
      dlg.Destroy()

   def OnOpenData(self, event):
      dlg = wx.FileDialog(self, "Choose a Database", "", "", "*.db", wx.OPEN)
      if dlg.ShowModal() == wx.ID_OK:
         self.databaseName = os.path.join(dlg.GetDirectory(), dlg.GetFilename())
         self.databaseNameText.SetLabel("Database: " + self.databaseName)
         # Must close all current opened children frames 
         # (to avoid doing things on old database).
         self.CloseChildrenFrames()
         # Reload data for search fields
         self.ReloadSearchFieldsData()
         dlg.Destroy() 

   ## Close all frames children of the main one.
   def CloseChildrenFrames(self):
      children = self.GetChildren() 
      for child in  children:
         child.Close()

   ## Called on key up, usually for F5 fields refresh
   def OnKeyUp(self, event):
      if event.GetKeyCode() == wx.WXK_F5:
         self.ReloadSearchFieldsData()
      else:
         event.Skip()

   ## Reload data for our search fields
   # TODO: we should call this function after inserted or deleted or updated
   #       an author or keyword.
   def ReloadSearchFieldsData(self):
      dataCon = DataConnection(self.databaseName) 
      self.keywordList.Populate(dataCon.conn)
      self.authorList.Populate(dataCon.conn)
      self.bookList.Populate(dataCon.conn)
      self.sourceList.Populate(dataCon.conn)
      dataCon.CloseConnection()

   def OnNewAuthor(self, event):
      AuthorFrame(self, self.databaseName)
   def OnListAuthor(self, event):
      AuthorListFrame(self, self.databaseName)

   def OnNewKeyword(self, event):
      KeywordFrame(self, self.databaseName)
   def OnListKeyword(self, event):
      KeywordListFrame(self, self.databaseName)

   def OnNewSource(self, event):
      SourceFrame(self, self.databaseName)
   def OnListSource(self, event):
      SourceListFrame(self, self.databaseName)

   def OnNewBook(self, event):
      BookFrame(self, self.databaseName)
   def OnListBook(self, event):
      BookListFrame(self, self.databaseName)

   def OnNewQuote(self, event):
      QuoteFrame(self, self.databaseName)
   def OnListQuote(self, event):
      QuoteListFrame(self, self.databaseName)



app = wx.App(False)
mf = MainFrame();
app.MainLoop()

