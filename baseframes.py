import wx
import sqlite3
import os
from database import DataConnection
import abc

## A simple frame for insert / edition of items. Used as base class for all
# our editing frames
class InsertEditFrame(wx.Frame, DataConnection):
   __metaclass__ = abc.ABCMeta

   @abc.abstractmethod
   def CreateElements(self, sizer):
      """Create each needed wx element and add it to our sizer """
   
   @abc.abstractmethod
   def GetValuesAsTuple(self):
      """ @return a tuple of with all values to be inserted / updated """
      """ (but without Id) """
 
   @abc.abstractmethod
   def GetInsertQuery(self):
      """ @return a string with the query to insert a new element """

   @abc.abstractmethod
   def GetUpdateQuery(self):
      """ @return a string with the query to update an element by its Id """

   @abc.abstractmethod
   def GetSelectQuery(self):
      """ @return a string with the query to select an element by its Id """

   @abc.abstractmethod
   def Populate(self, result):
      """ Populate its elements with the single result from the select query"""

   @abc.abstractmethod
   def GetDeleteQuery(self):
      """ @return a string with the query to delete an element by its Id """


   ## Constructor
   # @param parent frame's parent
   # @param database database's name
   # @param itemId None while inserting,some id while editing
   # @param itemName name of the item inserting/editing
   def __init__(self, parent, databaseName, itemId, itemName, ssize=(600, 200)):
      self.parent = parent
      self.itemId = itemId
      if(itemId == None):
         frameTitle = "Insert " + itemName
      else:
         frameTitle = "Edit " + itemName
      # Init our parent classes
      DataConnection.__init__(self, databaseName)
      wx.Frame.__init__(self, parent, title = frameTitle, size=ssize)
      self.sizer = wx.BoxSizer(wx.VERTICAL)

      # Tell our child to create its elements
      self.CreateElements(self.sizer)

      # Create our buttons
      if(itemId == None):
         buttonInsert = wx.Button(self, -1, "Insert")
         self.Bind(wx.EVT_BUTTON, self.OnInsert, buttonInsert)
         self.sizer.Add(buttonInsert, 0, wx.ALIGN_CENTER)
      else:
         sizer2 = wx.BoxSizer(wx.HORIZONTAL)
         buttonSave = wx.Button(self, -1, "Save")
         self.Bind(wx.EVT_BUTTON, self.OnEdit, buttonSave)
         sizer2.Add(buttonSave, 0, wx.ALIGN_CENTER)
         buttonDelete = wx.Button(self, -1, "Delete")
         self.Bind(wx.EVT_BUTTON, self.OnDelete, buttonDelete)
         sizer2.Add(buttonDelete, 0, wx.ALIGN_CENTER)
         self.PopulateItem()
         self.sizer.Add(sizer2, 0, wx.ALIGN_CENTER)

      # Finally, fit sizer and open frame
      self.SetSizer(self.sizer)
      self.SetAutoLayout(1)
      self.sizer.Fit(self)
      self.Show(True)

   ## Insert a new item on database.
   def OnInsert(self, event):
      query = self.GetInsertQuery()
      els = self.GetValuesAsTuple()
      if(els != None):
         c = self.conn.cursor()
         c.execute(query, els)
         self.itemId = c.lastrowid 
         self.conn.commit()
         self.CloseConnection()
         self.Close()
      else:
         self.conn.rollback()
         dlg = wx.MessageDialog(self, "You should define all mandatory fields",
                                "Error", wx.OK)
         dlg.ShowModal()
         dlg.Destroy()

   
   ## Update the existent on editing item
   def OnEdit(self, event):
      query = self.GetUpdateQuery()
      els = self.GetValuesAsTuple()
      if(els != None):
         tels = els + (self.itemId,)
         c = self.conn.cursor()
         c.execute(query, tels)
         self.conn.commit()
         self.CloseConnection()
         self.parent.Populate()
         self.Close()
      else:
         self.conn.rollback()
         dlg = wx.MessageDialog(self, "You should define all mandatory fields",
                                "Error", wx.OK)
         dlg.ShowModal()
         dlg.Destroy()

   ## Delete an existent item
   def OnDelete(self, event):
      query = self.GetDeleteQuery()
      c = self.conn.cursor()
      c.execute(query, (self.itemId, ))
      self.conn.commit()
      self.CloseConnection()
      self.parent.Populate()
      self.Close()

   ## Populate editing values with defined itemId
   def PopulateItem(self):
      query = self.GetSelectQuery()
      c = self.conn.cursor()
      c.execute(query, (self.itemId,))
      result = c.fetchone() 
      self.conn.commit()

      # Define result
      self.Populate(result)

## A base frame for list of some database table
class ListFrame(wx.Frame, DataConnection):
   __metaclass__ = abc.ABCMeta

   @abc.abstractmethod
   def GetColumnsTitleAndWidth(self):
      """@return tuple of tuple with each column name and info """
      """((name, width), (name, width) ... )"""

   @abc.abstractmethod
   def GetSelectQuery(self):
      """@return string with the select query to use. First column must"""
      """        be the item's Id (and a Long)"""
         
   @abc.abstractmethod
   def OpenEditFrame(self, databaseName, itemId):
      """ Open a related InsertEditFrame to edit item defined by itemId """

   ## Constructor
   # @param parent frame's parent
   # @param database database's name
   # @param itemName name of the listing items
   def __init__(self, parent, databaseName, itemName, ssize=(540, 400)):
      self.databaseName = databaseName
      DataConnection.__init__(self, databaseName)
      wx.Frame.__init__(self, parent, title = itemName, size=ssize)
      # Create our list view to show items
      self.listView = wx.ListView(self, -1)
      colInfo = self.GetColumnsTitleAndWidth()
      self.totalColumns = len(colInfo)
      cur = 0
      for col in colInfo:
         self.listView.InsertColumn(cur, col[0], width=col[1])
         cur += 1
      # populate it
      self.Populate()

      # Bind events
      self.Bind(wx.EVT_LIST_ITEM_ACTIVATED, self.OnDoubleClick, self.listView)

      # open our frame
      self.Show(True)

   ## Populate the list view with elements
   def Populate(self):
      self.listView.DeleteAllItems()
      query = self.GetSelectQuery()
      c = self.conn.cursor()
      c.execute(query)
      result = c.fetchall() 
      self.conn.commit()
      # Insert all results
      for line in result:
         cur = 0
         for el in line:
            if(cur == 0):
               self.listView.InsertStringItem(0, str(el))
               self.listView.SetItemData(0, el)
            else:
               if(el != None):
                  self.listView.SetStringItem(0, cur, el)
               else:
                  self.listView.SetStringItem(0, cur, "")
            cur += 1

   ## Open an Edit frame for each selection
   def OnDoubleClick(self, event):
      el = self.listView.GetFirstSelected()
      while(el != -1):
         self.OpenEditFrame(self.databaseName, self.listView.GetItemData(el))
         el = self.listView.GetNextSelected(el)


## A base Insert/Edit frame for entities with an extra relational 1 to n
# relationship (for example, a Book and its Authors).
class InsertEditComposedFrame(InsertEditFrame):
   __metaclass__ = abc.ABCMeta

   @abc.abstractmethod
   def GetRelatedInsertQuery(self):
      """ @return String with query to insert related elements of the """
      """ entity, first element must be entity identifier, second by the """
      """ related identifier """

   @abc.abstractmethod
   def GetRelatedDeleteQuery(self): 
      """ @return string with query to delete all related elements to """
      """ an entity ID """

   @abc.abstractmethod
   def GetRelatedSelectQuery(self):
      """ @return string with query to select entity related elements """
   
   @abc.abstractmethod
   def GetRelatedIds(self):
      """ @return tuple or list with ids related to be inserted at relation """
   
   @abc.abstractmethod
   def PopulateRelated(self, results):
      """ Populate related entities. Results is a list. """

   def __init__(self, parent, databaseName, itemId, itemName, ssize=(600, 200)):
      InsertEditFrame.__init__(self, parent, databaseName, itemId, 
                               itemName, ssize)

   ## Populate item and its relations
   def PopulateItem(self):
      # Get all related
      query = self.GetRelatedSelectQuery()
      c = self.conn.cursor()
      c.execute(query, (self.itemId,))
      result = c.fetchall() 
      self.PopulateRelated(result)
      # Get and populate entity
      InsertEditFrame.PopulateItem(self)

   ## Delete an existent item and all its relations
   def OnDelete(self, event):
      # Delete its relations
      self.DeleteRelations()
      # Delete the Entity
      InsertEditFrame.OnDelete(self, event)

   ## Delete entity relations
   def DeleteRelations(self):
      query = self.GetRelatedDeleteQuery()
      c = self.conn.cursor()
      c.execute(query, (self.itemId, ))

   ## Update the existent on editing item and its relations
   def OnEdit(self, event):
      # Delete all relations
      self.DeleteRelations()
      # Insert all relations
      self.InsertRelations()
      # Update the entity
      InsertEditFrame.OnEdit(self, event)

   ## Insert a new entity and irs relations
   def OnInsert(self, event):
      query = self.GetInsertQuery()
      els = self.GetValuesAsTuple()
      if(els != None):
         c = self.conn.cursor()
         c.execute(query, els)
         self.itemId = c.lastrowid 
         self.InsertRelations()
         self.conn.commit()
         self.CloseConnection()
         self.Close()
      else:
         dlg = wx.MessageDialog(self, "You should define all mandatory fields",
                                "Error", wx.OK)
         dlg.ShowModal()
         dlg.Destroy()

   ## Insert all relations 
   def InsertRelations(self):
      ids = self.GetRelatedIds()
      query = self.GetRelatedInsertQuery()
      c = self.conn.cursor()
      for curId in ids:
         c.execute(query, (self.itemId, curId))

## A base List Frame for entities with an extra relational 1 to n relationship.
class ListComposedFrame(ListFrame):
   
   __metaclass__ = abc.ABCMeta
   
   @abc.abstractmethod
   def GetRelatedSelectQuery(self):
      """ @return string with query to select related elements by entity """
      """ Identifier. First column must be entity ID, second must be  """
      """ a string with the info to be displayed. Must be ordered exactely """
      """ as the GetSelectQuery method """
   
   def __init__(self, parent, databaseName, itemName, ssize=(540, 400)):
      ListFrame.__init__(self, parent, databaseName, itemName, ssize)

   ## Populate the list view with elements
   def Populate(self):
      self.listView.DeleteAllItems()

      # Get Entity
      query = self.GetSelectQuery()
      c = self.conn.cursor()
      c.execute(query)
      result = c.fetchall() 

      # Get related
      query = self.GetRelatedSelectQuery()
      c = self.conn.cursor()
      c.execute(query)
      related = c.fetchall()

      self.conn.commit()
      # Insert all results
      currel = 0
      for line in result:
         cur = 0
         for el in line:
            if(cur == 0):
               self.listView.InsertStringItem(0, str(el))
               self.listView.SetItemData(0, el)
            else:
               if(el != None):
                  if(type(el) is str or type(el) is unicode):
                     self.listView.SetStringItem(0, cur, el)
                  else:
                     self.listView.SetStringItem(0, cur, str(el))
               else:
                  self.listView.SetStringItem(0, cur, "")
            cur += 1
            if(cur == self.totalColumns-1):
               # Must add all relationals
               val = ""
               while(currel < len(related) and related[currel][0] == line[0]):
                  if(val != ""):
                     val += ", " + related[currel][1] 
                  else:
                     val += related[currel][1] 
                  currel += 1
               self.listView.SetStringItem(0, cur, val)

