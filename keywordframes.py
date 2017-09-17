import wx
import sqlite3
import os
from database import DataConnection, DatabaseCreator
from baseframes import InsertEditFrame, ListFrame
from components import KeywordSynonymComboBox

# Class for Insert/Edit Keywords
class KeywordFrame(InsertEditFrame):
   def __init__(self, parent, databaseName, keyId=None):
      InsertEditFrame.__init__(self, parent, databaseName, keyId, "Keyword")

   def CreateElements(self, sizer):
      # Name
      sizer.Add(wx.StaticText(self, -1, "Name:"), 0, wx.SHAPED)
      self.name = wx.TextCtrl(self, size=(300,26))
      sizer.Add(self.name, 0, wx.EXPAND)
      # Synonyms
      self.syn = KeywordSynonymComboBox(self, self.conn, sizer)

   def GetInsertQuery(self):
      return "INSERT INTO Keywords(Name, SynId) VALUES(?, ?)"

   def GetValuesAsTuple(self):
      name = self.name.GetLineText(0)
      if(name != ""):
         return (name.lower(), self.syn.GetSelectedId())
      return None

   def GetDeleteQuery(self):
      return "DELETE FROM Keywords WHERE Id=?"
 
   def GetUpdateQuery(self):
      return "UPDATE Keywords set Name=?, SynId=? WHERE Id=?"

   def GetSelectQuery(self):
     return "SELECT k.Name, k2.Name from KEYWORDS k "\
            "LEFT JOIN KEYWORDS k2 ON k.synId = k2.Id "\
            "WHERE k.Id=?"

   def Populate(self, result):
      self.name.SetValue(result[0])
      if(result[1] != None):
         self.syn.SetSelection(result[1])

## The Keyword List Frame
class KeywordListFrame(ListFrame):
   def __init__(self, parent, databaseName):
      ListFrame.__init__(self, parent, databaseName, "Keywords")
      
   def GetColumnsTitleAndWidth(self):
      return (("Id", 50), ("Name", 260), ("Synonym", 200))

   def GetSelectQuery(self):
      return "SELECT k.Id, k.Name, k2.Name from KEYWORDS k "\
             "LEFT JOIN KEYWORDS k2 ON k.synId = k2.Id "\
             "ORDER BY k.Name DESC"

   def OpenEditFrame(self, databaseName, itemId):
      KeywordFrame(self, databaseName, itemId)


