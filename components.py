import wx
import sqlite3
import abc

## Base class for implementation of a wx.ComboBox for a database Entity.
class BaseEntityComboBox:
   __metaclass__ = abc.ABCMeta

   @abc.abstractmethod
   def GetSelectQuery(self):
      """ @return string with Query to select Id and Label from Entity """
  
   ## Constructor
   # @param frame where to insert combo box into
   # @param dataConnection to use for select values
   # @para sizer to place combo box into.
   def __init__(self, frame, dataConnection, sizer):
      self.entityDict = {}
      self.entities = []
      self.combo = wx.ComboBox(frame, -1, choices=self.entities)
      sizer.Add(self.combo, 0, wx.EXPAND)
      self.Populate(dataConnection)

   ## @return the selected entity Id or None
   def GetSelectedId(self):
      if(self.entityDict.has_key(self.combo.GetStringSelection())):
         return self.entityDict[self.combo.GetStringSelection()][0]
      return None

   ## Populate combo box choices with values from database
   def Populate(self, dataConnection):
      self.entityDict = {}
      self.entities = []
      c = dataConnection.cursor()
      c.execute(self.GetSelectQuery())
      result = c.fetchall()
      dataConnection.commit()
      for res in result:
         self.entityDict[res[1]] = (res[0], len(self.entities))
         self.entities.append(res[1])
      self.combo.Set(self.entities)

   ## Set current selected by its name
   def SetSelection(self, name):
      if(self.entityDict.has_key(name)):
         self.combo.SetSelection(self.entityDict[name][1])
      else:
         print "Warning: couldn't find: ", name

## A combo box for books
class BookComboBox(BaseEntityComboBox):

   def __init__(self, frame, dataConnection, sizer):
      BaseEntityComboBox.__init__(self, frame, dataConnection, sizer)

   def GetSelectQuery(self):
      return "SELECT b.Id, GROUP_CONCAT(a.lastName || ', ' || a.name, ', ') "\
             "             || ', ' || b.Title AS info "\
             "FROM Books b "\
             "INNER JOIN BookAuthor ba ON ba.bookId = b.id "\
             "INNER JOIN Authors a ON a.id = ba.authorId "\
             "GROUP BY b.id "\
             "ORDER BY info"

## A ComboBox for Sources
class SourceComboBox(BaseEntityComboBox):
   def __init__(self, frame, dataConnection, sizer):
      BaseEntityComboBox.__init__(self, frame, dataConnection, sizer)

   def GetSelectQuery(self):
      return "SELECT Id, Title FROM Sources ORDER BY Title ASC"

## A ComboBox for Keyword Synonyms
class KeywordSynonymComboBox(BaseEntityComboBox):
   def __init__(self, frame, dataConnection, sizer):
      BaseEntityComboBox.__init__(self, frame, dataConnection, sizer)

   def GetSelectQuery(self):
      return "SELECT Id, Name FROM Keywords Where SynId Is NULL "\
             "ORDER BY Name ASC"

## Base implementation for a wx.CheckBoxList for a database Entity.
class BaseEntityListSelector:
   __metaclass__ = abc.ABCMeta

   @abc.abstractmethod
   def GetSelectQuery(self):
      """ @return string with Query to select Id and Label from Entity """

   ## Constructor
   # \param frame wx.Frame where to create the selector
   # \param dataConnection sqlite3 connection to use
   # \param sizer sizer where the selector will be 
   def __init__(self, frame, dataConnection, sizer, size=(300, 250)):
      self.entityDict = {}
      self.entities = []
      self.selector = wx.CheckListBox(frame, -1, choices=self.entities,
         size=size)
      self.Populate(dataConnection)
      sizer.Add(self.selector, 1, wx.EXPAND)

   ## Populate values to the selector
   # dataConnection sqlite3 connection to use
   def Populate(self, dataConnection):
      self.selector.Clear()
      self.entityDict = {}
      self.entities = []
      c = dataConnection.cursor()
      c.execute(self.GetSelectQuery())
      result = c.fetchall() 
      dataConnection.commit()
      for res in result:
         # Define our dictionary of Name per (Id, list index)
         self.entityDict[res[1]] = (res[0], len(self.entities))
         # Set our list to combo
         self.entities.append(res[1])
      self.selector.Set(self.entities)

   ## @return a list of selected Entity identifiers
   def GetSelectedIds(self):
      checked = self.selector.GetCheckedStrings()
      ids = []
      for it in checked:
         ids.append(self.entityDict[it][0])
      return ids

   ## Select an entity on the list by its name
   def Select(self, name):
      if(self.entityDict.has_key(name)):
         self.selector.Check(self.entityDict[name][1])


## A wx.CheckListBox implementation for Keywords
class KeywordListSelector(BaseEntityListSelector):

   def __init__(self, frame, dataConnection, sizer):
      BaseEntityListSelector.__init__(self, frame, dataConnection, sizer)

   def GetSelectQuery(self):
      return "SELECT Id, Name FROM Keywords ORDER BY Name ASC"

## A wx.CheckListBox implementation for Authors
class AuthorListSelector(BaseEntityListSelector):

   def __init__(self, frame, dataConnection, sizer):
      BaseEntityListSelector.__init__(self, frame, dataConnection, sizer)

   def GetSelectQuery(self):
      return "SELECT Id, LastName || ', ' || Name FROM Authors "\
                "ORDER BY LastName ASC, Name ASC"

## A wx.CheckListBox implementation for Books
class BookListSelector(BaseEntityListSelector):

   def __init__(self, frame, dataConnection, sizer):
      BaseEntityListSelector.__init__(self, frame, dataConnection, sizer)

   def GetSelectQuery(self):
      return "SELECT b.Id, b.Title "\
             "FROM Books b "\
             "ORDER BY b.Title ASC "

## A wx.CheckListBox implementation for Sources
class SourceListSelector(BaseEntityListSelector):

   def __init__(self, frame, dataConnection, sizer):
      BaseEntityListSelector.__init__(self, frame, dataConnection, sizer)

   def GetSelectQuery(self):
      return "SELECT Id, Title FROM Sources ORDER BY Title ASC "


