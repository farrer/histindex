import wx
import sqlite3
import os
from database import DataConnection, DatabaseCreator
from baseframes import InsertEditFrame, ListFrame

# Class for Insert/Edit Sources Frame
class SourceFrame(InsertEditFrame):
   def __init__(self, parent, databaseName, authorId=None):
      InsertEditFrame.__init__(self, parent, databaseName, authorId, "Source")

   def CreateElements(self, sizer):
      self.title = wx.TextCtrl(self, size=(300,26))
      self.info = wx.TextCtrl(self)
      sizer.Add(wx.StaticText(self, -1, "Title:"), 0, wx.SHAPED)
      sizer.Add(self.title, 0, wx.EXPAND)
      sizer.Add(wx.StaticText(self, -1, "Extra info:"), 0, wx.SHAPED)
      sizer.Add(self.info, 0, wx.EXPAND)

   def GetInsertQuery(self):
      return "INSERT into Sources(Title, ExtraInfo) values(?, ?)"

   def GetValuesAsTuple(self):
      title = self.title.GetLineText(0)
      info = self.info.GetLineText(0)
      if((title != "") and (info != "")):
         return (title, info)
      return None
 
   def GetUpdateQuery(self):
      return "UPDATE Sources set Title=?, ExtraInfo=? WHERE Id=?"

   def GetSelectQuery(self):
      return "SELECT Title, ExtraInfo from Sources WHERE Id=?"

   def Populate(self, result):
      self.title.SetValue(result[0])
      self.info.SetValue(result[1])

   def GetDeleteQuery(self):
      return "DELETE FROM Sources WHERE Id=?"

## The Sources List Frame
class SourceListFrame(ListFrame):
   def __init__(self, parent, databaseName):
      ListFrame.__init__(self, parent, databaseName, "Sources")
      
   def GetColumnsTitleAndWidth(self):
      return (("Id", 50), ("Title", 200), ("Extra Info", 260))

   def GetSelectQuery(self):
      return "SELECT Id, Title, ExtraInfo from Sources "\
             "ORDER BY Title DESC, ExtraInfo DESC"

   def OpenEditFrame(self, databaseName, itemId):
      SourceFrame(self, databaseName, itemId)


