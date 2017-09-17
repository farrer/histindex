import wx
import sqlite3
import os
from database import DataConnection, DatabaseCreator
from components import KeywordListSelector, BookComboBox, SourceComboBox
from baseframes import InsertEditComposedFrame, ListComposedFrame

# Class for Insert/Edit Quotes
class QuoteFrame(InsertEditComposedFrame):
   def __init__(self, parent, databaseName, keyId=None):
      InsertEditComposedFrame.__init__(self, parent, databaseName, keyId, 
                                       "Reference")

   def CreateElements(self, sizer):
      # Text
      sizer.Add(wx.StaticText(self, -1, "Text:"), 0, wx.SHAPED)
      self.text = wx.TextCtrl(self, size=(300,26))
      sizer.Add(self.text, 0, wx.EXPAND)
      sizer.Add(wx.StaticText(self, -1, "Page:"), 0, wx.SHAPED)
      self.page = wx.TextCtrl(self, size=(80,26))
      sizer.Add(self.page, 0, wx.EXPAND)
      # Books
      sizer.Add(wx.StaticText(self, -1, "Book/Article:"), 0, wx.SHAPED)
      self.book = BookComboBox(self, self.conn, sizer)
      # Sources
      sizer.Add(wx.StaticText(self, -1, "Source:"), 0, wx.SHAPED)
      self.source = SourceComboBox(self, self.conn, sizer)
      # Keywords
      sizer.Add(wx.StaticText(self, -1, "Keywords:"), 0, wx.SHAPED)
      self.keyList = KeywordListSelector(self, self.conn, sizer) 

   def GetValuesAsTuple(self):
      text = self.text.GetLineText(0)
      page = int(self.page.GetLineText(0)) #FIXME use validator.
      bookId = self.GetBookId()
      sourceId = self.GetSourceId()
      if(text != "" and (bookId != None or sourceId != None) ):
         return (text, page, self.GetBookId(), self.GetSourceId())
      return None

   def GetBookId(self):
      return self.book.GetSelectedId()

   def GetSourceId(self):
      return self.source.GetSelectedId()

   def GetRelatedIds(self):
      return self.keyList.GetSelectedIds()

   def GetInsertQuery(self):
      return "INSERT INTO Quotes(Text, Page, BookId, SourceId) "\
             "VALUES(?, ?, ?, ?)"

   def GetRelatedInsertQuery(self):
      return "INSERT INTO QuoteKeyword(QuoteId, KeywordId) VALUES(?, ?)"

   def GetDeleteQuery(self):
      return "DELETE FROM Quotes WHERE Id=?"
   
   def GetRelatedDeleteQuery(self):
      return "DELETE FROM QuoteKeyword WHERE QuoteId=?"
 
   def GetUpdateQuery(self):
      return "UPDATE Quotes set Text=?, Page=?, BookId=?, SourceId=? "\
             "WHERE Id=?"

   def GetSelectQuery(self):
      return "SELECT q.Text, q.Page, GROUP_CONCAT(a.LastName || ', ' || "\
             "a.Name, ', '), b.Title, s.Title "\
             "FROM Quotes q "\
             "LEFT JOIN Books b, Authors a, BookAuthor ba "\
             "ON q.BookId = b.Id "\
             "AND ba.bookId = b.Id "\
             "AND a.id = ba.authorId "\
             "LEFT JOIN Sources s "\
             "ON q.sourceId = s.Id "\
             "WHERE q.Id=?"

   def Populate(self, result):
      if(result[0] != None):
         self.text.SetValue(result[0])
      if(result[1] != None):
         self.page.SetValue(str(result[1]))
      if(result[3] != None):
         val = ""
         if(result[2] != None):
            val = result[2] + ", "
         val += result[3]
         self.book.SetSelection(val)
      if(result[4] != None):
         self.source.SetValue(result[4])

   def GetRelatedSelectQuery(self):
      return "SELECT k.Name FROM QuoteKeyword qk, Keywords k "\
             "WHERE qk.keywordId = k.id AND "\
             "qk.quoteId = ?"
   
   def PopulateRelated(self, results):
      for res in results:
         self.keyList.Select(res[0])

## The Quote List Frame
class QuoteListFrame(ListComposedFrame):
   def __init__(self, parent, databaseName):
      ListComposedFrame.__init__(self, parent, databaseName, "References",
                                 (750, 400))
      
   def GetColumnsTitleAndWidth(self):
      return (("Id", 50), ("Text", 400), ("Page", 60),
              ("Keywords", 200))

   def GetSelectQuery(self):
      return "SELECT q.Id, q.Text, q.Page "\
             "FROM Quotes q "\
             "ORDER BY q.Id DESC"

   def GetRelatedSelectQuery(self):
      return "SELECT qk.quoteId, k.Name "\
             "FROM QuoteKeyword qk, Keywords k "\
             "WHERE qk.keywordId = k.Id "\
             "ORDER BY qk.quoteId DESC"


   def OpenEditFrame(self, databaseName, itemId):
      QuoteFrame(self, databaseName, itemId)


