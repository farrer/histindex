import wx
import sqlite3
import os
from database import DataConnection, DatabaseCreator
from baseframes import InsertEditFrame, ListFrame

# Class for Insert/Edit Authors Frame
class AuthorFrame(InsertEditFrame):
   def __init__(self, parent, databaseName, authorId=None):
      InsertEditFrame.__init__(self, parent, databaseName, authorId, "Author")

   def CreateElements(self, sizer):
      self.firstName = wx.TextCtrl(self, size=(300,26))
      self.lastName = wx.TextCtrl(self)
      sizer.Add(wx.StaticText(self, -1, "First name:"), 0, wx.SHAPED)
      sizer.Add(self.firstName, 0, wx.EXPAND)
      sizer.Add(wx.StaticText(self, -1, "Last name:"), 0, wx.SHAPED)
      sizer.Add(self.lastName, 0, wx.EXPAND)

   def GetInsertQuery(self):
      return "INSERT into Authors(Name, LastName) values(?, ?)"

   def GetValuesAsTuple(self):
      first = self.firstName.GetLineText(0)
      last = self.lastName.GetLineText(0)
      if((first != "") and (last != "")):
         return (first, last)
      return None
 
   def GetUpdateQuery(self):
      return "UPDATE Authors set Name=?, LastName=? WHERE Id=?"

   def GetSelectQuery(self):
      return "SELECT Name, LastName from Authors WHERE Id=?"

   def Populate(self, result):
      self.firstName.SetValue(result[0])
      self.lastName.SetValue(result[1])

   def GetDeleteQuery(self):
      return "DELETE FROM Authors WHERE Id=?"

## The Authors List Frame
class AuthorListFrame(ListFrame):
   def __init__(self, parent, databaseName):
      ListFrame.__init__(self, parent, databaseName, "Authors")
      
   def GetColumnsTitleAndWidth(self):
      return (("Id", 50), ("First Name", 260), ("Last Name", 200))

   def GetSelectQuery(self):
      return "SELECT Id, Name, LastName from Authors "\
             "ORDER BY LastName DESC, Name DESC"

   def OpenEditFrame(self, databaseName, itemId):
      AuthorFrame(self, databaseName, itemId)


