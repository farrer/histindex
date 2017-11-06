import wx
import sqlite3
import os
import io
import datetime
from database import DataConnection
from quoteframes import QuoteFrame

## A frame which searches and display our quote results.
class SearchResultFrame(wx.Frame, DataConnection): 

   ## Constructor
   ## \param parent parent Frame, if any.
   ## \param databaseName filename of current database
   ## \param keywordIds list of keyword identifiers
   ## \param authorIds list of author identifiers
   ## \param bookIds list of book identifiers
   ## \param sourceIds list of source identifiers
   ## \param searchText any setence to search inner the quotes text.
   def __init__(self, parent, databaseName, keywordIds, authorIds, bookIds, 
                sourceIds, searchText):
      # Define ourselves
      self.parent = parent
      self.databaseName = databaseName
      self.keywords = keywordIds
      self.authors = authorIds
      self.books = bookIds
      self.sources = sourceIds
      self.searchText = searchText
      
      # Init our parent classes
      DataConnection.__init__(self, databaseName)
      wx.Frame.__init__(self, parent, title = "Search Result") 
      self.sizer = wx.BoxSizer(wx.VERTICAL)

      # Menu for search export
      searchMenu = wx.Menu()
      exportSearch = searchMenu.Append(wx.ID_SAVEAS, "E&xport", 
                                       "Export search results")
      self.Bind(wx.EVT_MENU, self.OnExport, exportSearch)

      menuBar = wx.MenuBar()
      menuBar.Append(searchMenu, "&Search")
      self.SetMenuBar(menuBar)

      # A Total displayer
      self.totalText = wx.StaticText(self, -1, "Total:")
      self.sizer.Add(self.totalText, 0, wx.EXPAND)

      # Create our list view to show quotes
      self.totalSize = 800
      self.halfSize = self.totalSize / 2 - 15
      self.listView = wx.ListView(self, -1)
      self.listView.InsertColumn(0, "Quote", width=self.halfSize)
      self.listView.InsertColumn(1, "Reference", width=self.halfSize-40)
      self.listView.InsertColumn(2, "Page", width=40)

      self.Search()
      self.sizer.Add(self.listView, 0, wx.EXPAND)

      # The selected quote text
      self.text = wx.TextCtrl(self,  size=(self.totalSize, 200), 
              style=wx.TE_MULTILINE |  wx.TE_READONLY)
      self.sizer.Add(self.text, 0, wx.EXPAND)

      # Bind events
      self.Bind(wx.EVT_LIST_ITEM_ACTIVATED, self.OnDoubleClick, self.listView)
      self.Bind(wx.EVT_LIST_ITEM_SELECTED, self.OnSelected, self.listView)

      # open our frame
      self.SetSizer(self.sizer)
      self.SetAutoLayout(1)
      self.sizer.Fit(self)
      self.Show(True)

   ## Redo the search. 
   ## \note called Populate for compatibility.
   def Populate(self):
      self.listView.DeleteAllItems()
      self.Search()

   ## Do the search for quotes with the defined filters.
   def Search(self):
      query = "SELECT q.id, q.text, q.page, q.bookId, q.sourceId " \
              "FROM Quotes q "
      values = []

      # Filter by keywords
      ## FIXME: synonimes!
      if(len(self.keywords) > 0):
         query += "INNER JOIN QuoteKeyword qk ON q.id = qk.quoteId AND " 
         query += "qk.keywordId IN ("
         first = True
         for key in self.keywords:
            values.append(key);
            if(not first):
               query += ", ?"
            else:
               query += "?"
               first = False
         query += ") "

      # Filter by Author
      if(len(self.authors) > 0):
         query += "INNER JOIN BookAuthor ba ON q.bookId = ba.bookId AND "
         query += "ba.authorId IN ("
         first = True
         for key in self.authors:
            values.append(key);
            if(not first):
               query += ", ?"
            else:
               query += "?"
               first = False
         query += ") "

      where = False
      # Filter by search text like
      if((self.searchText != "") and (self.searchText != None)):
         where = True
         query += "WHERE text LIKE ?"
         values.append("%"+self.searchText+"%")

      # Filter by book list
      if(len(self.books) > 0):
         if(not where):
            query += "WHERE "
            where = True
         else:
            query += "AND "
         query += "q.bookId IN ( " 
         first = True
         for key in self.books:
            values.append(key);
            if(not first):
               query += ", ?"
            else:
               query += "?"
               first = False
         query += ") "

      # Filter by source list
      if(len(self.sources) > 0):
         if(not where):
            query += "WHERE "
            where = True
         else:
            query += "AND "
         query += "q.sourceId IN ( " 
         first = True
         for key in self.sources:
            values.append(key);
            if(not first):
               query += ", ?"
            else:
               query += "?"
               first = False
         query += ") "
      query += "ORDER BY q.bookId, q.sourceId, q.page DESC, q.Id DESC"

      c = self.conn.cursor()
      c.execute(query, tuple(values))
      result = c.fetchall() 
      self.conn.commit()

      # Set our total info
      self.totalText.SetLabel("Total: " + str(len(result)))

      ## Define related Ids
      self.DefineBookIds(result)
      self.DefineSourceIds(result)

      ## Retrive book and source information
      self.bookInfo = self.SearchBooks()
      self.sourceInfo = self.SearchSources()

      # Insert all results
      curBook = 0
      curSource = 0
      for line in result:
         self.listView.InsertStringItem(0, line[1])
         self.listView.SetItemData(0, long(line[0]))
         # Check if use book information.
         if(line[3] != None):
            # Positionate to desired book on vector
            while(self.bookInfo[curBook][0] < line[3]):
               curBook += 1
            self.listView.SetStringItem(0, 1, self.bookInfo[curBook][1])
         # Check source info
         elif(line[4] != None):
            while(self.sourceInfo[curSource][0] < line[4]):
               curSource += 1
            self.listView.SetStringItem(0, 1, self.sourceInfo[curSource][1])
         # Page
         if(line[2] != None):
            self.listView.SetStringItem(0, 2, str(line[2]))

   ## Define the book ids. Note: will always redefine, as the ones received
   ## from parent window often aren't at the same order as the resulted ones.
   def DefineBookIds(self, result):
      last = -1
      for line in result:
         if((line[3] != None) and (line[3] != last)):
            self.books.append(line[3])
            last = line[3]
   
   ## Define the source ids
   def DefineSourceIds(self, result):
      last = -1
      for line in result:
         if((line[4] != None) and (line[4] != last)):
            self.sources.append(line[4])
            last = line[4]

   ## Search all books
   def SearchBooks(self):
      query = "SELECT b.id, "\
              "       GROUP_CONCAT(a.lastName || ', ' || a.name, ', ') "\
              "       || ', ' || b.title FROM Books b "\
              "INNER JOIN BookAuthor ba ON ba.bookId = b.id "\
              "INNER JOIN Authors a ON a.id = ba.authorId "\
              "WHERE b.id IN("
      first = True
      for key in self.books:
         if(not first):
            query += ", ?"
         else:
            query += "?"
            first = False
      query += ") "
      query += "GROUP BY b.id ORDER BY b.id"

      if(not first):
         c = self.conn.cursor()
         c.execute(query, tuple(self.books))
         result = c.fetchall() 
         self.conn.commit()
         return result
      return []

   ## @return Sources list
   def SearchSources(self):
      query = "SELECT s.id, s.title FROM Sources s ORDER BY s.id "\
              "WHERE s.id IN("
      first = True
      for key in self.sources:
         if(not first):
            query += ", ?"
         else:
            query += "?"
            first = False
      query += ") "
      query += "ORDER BY s.id"

      if(not first):
         c = self.conn.cursor()
         c.execute(query, tuple(self.sources))
         result = c.fetchall() 
         self.conn.commit()
         return result

      return []


   ## Open an Edit frame for each selection
   def OnDoubleClick(self, event):
      el = self.listView.GetFirstSelected()
      while(el != -1):
         QuoteFrame(self, self.databaseName, self.listView.GetItemData(el))
         el = self.listView.GetNextSelected(el)

   ## Show the quote with multiple lines.
   def OnSelected(self, event):
      el = self.listView.GetFirstSelected()
      while(el != -1):
         self.text.SetValue(self.listView.GetItemText(el))
         el = self.listView.GetNextSelected(el)

   ## Called for search result export
   def OnExport(self, event):
      dlg = wx.FileDialog(self, "Export search result", "", "", 
                          "*.txt", wx.SAVE)
      if dlg.ShowModal() == wx.ID_OK:
         self.Export(os.path.join(dlg.GetDirectory(), dlg.GetFilename()))
         dlg.Destroy()


   def Export(self, filename):
      with io.open(filename, "w", encoding="utf-8") as f:
         # Save export information
         f.write(u"database: " + self.databaseName + u"\n")
         f.write(u"exported at: " + str(datetime.datetime.now()) + u"\n")
         if len(self.keywords) > 0:
            # TODO: get keywords names, instead of ids
            f.write(u"Keywords: " + str(self.keywords) + u"\n")
         if self.searchText != None:
            f.write(u"Search string: *")
            f.write(self.searchText)
            f.write(u"*\n")

         # Save each result (already grouped by book/source)
         i = 0
         last = ""
         while i < self.listView.GetItemCount():
            ref = self.listView.GetItemText(i, 1)
            if ref != last:
               last = ref
               f.write(u"\n")
               f.write(ref)
               f.write(u"\n")
            f.write(u"   page: " + self.listView.GetItemText(i, 2))
            f.write(u"\n\t")
            f.write(self.listView.GetItemText(i, 0))
            f.write(u"\n")
            i += 1

