import wx
import sqlite3
import os
from database import DataConnection, DatabaseCreator
from baseframes import InsertEditComposedFrame, ListComposedFrame
from components import AuthorListSelector

# Class for Insert/Edit Books
class BookFrame(InsertEditComposedFrame):
   def __init__(self, parent, databaseName, keyId=None):
      InsertEditComposedFrame.__init__(self, parent, databaseName, keyId, 
                                       "Book / Article")

   def CreateElements(self, sizer):
      # Title
      sizer.Add(wx.StaticText(self, -1, "Title:"), 0, wx.SHAPED)
      self.title = wx.TextCtrl(self, size=(300,26))
      sizer.Add(self.title, 0, wx.EXPAND)
      # Authors
      sizer.Add(wx.StaticText(self, -1, "Authors:"), 0, wx.SHAPED)
      self.authors = AuthorListSelector(self, self.conn, sizer)

   def GetValuesAsTuple(self):
      title = self.title.GetLineText(0)
      if(title != ""):
         return (title, )
      return None

   def GetRelatedIds(self):
      return self.authors.GetSelectedIds()

   def GetInsertQuery(self):
      return "INSERT INTO Books(Title) VALUES(?)"

   def GetRelatedInsertQuery(self):
      return "INSERT INTO BookAuthor(BookId, AuthorId) VALUES(?, ?)"

   def GetDeleteQuery(self):
      return "DELETE FROM Books WHERE Id=?"
   
   def GetRelatedDeleteQuery(self):
      return "DELETE FROM BookAuthor WHERE BookId=?"
 
   def GetUpdateQuery(self):
      return "UPDATE Books set Title=? WHERE Id=?"

   def GetSelectQuery(self):
      return "SELECT Title FROM Books WHERE Id=?"

   def GetRelatedSelectQuery(self):
      return "SELECT a.LastName || ', ' || a.Name "\
             "FROM BookAuthor ba, Authors a "\
             "WHERE ba.authorId = a.id AND "\
             "ba.bookId = ?"

   def Populate(self, result):
      self.title.SetValue(result[0])
   
   def PopulateRelated(self, results):
      for ath in results:
         self.authors.Select(ath[0])

## The Book List Frame
class BookListFrame(ListComposedFrame):
   def __init__(self, parent, databaseName):
      ListComposedFrame.__init__(self, parent, databaseName, "Books / Articles")
      
   def GetColumnsTitleAndWidth(self):
      return (("Id", 50), ("Title", 260), ("Authors", 200))

   def GetSelectQuery(self):
      return "SELECT b.Id, b.Title from Books b "\
             "ORDER BY b.Title DESC, b.Id DESC"
   
   def GetRelatedSelectQuery(self):
      return "SELECT ba.bookId, a.LastName || ', ' ||  a.Name "\
             "FROM BookAuthor ba, Authors a, Books b "\
             "WHERE ba.authorId = a.Id "\
             "AND ba.bookId = b.Id "\
             "ORDER BY b.Title DESC, b.Id DESC"

   def OpenEditFrame(self, databaseName, itemId):
      BookFrame(self, databaseName, itemId)


