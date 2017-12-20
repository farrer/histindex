package org.dnteam.histindex.database;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**  Class representing a Book or Article.
 * @author farrer */
public class Book extends Entity {
	
	/** Book title */
	private String title;
	/** Book's Authors */
	private List<Author> authorList = new ArrayList<Author>();
	/** Author names on a single string to display */
	private String authors;
	/** When authors string is dirty and should be remade */
	private boolean dirty = false;
	
	/** @return {@link #title}  */
	public String getTitle() {
		return title;
	}

	/** Set {@link #title}.
	 * @param title new value for title. */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/** Clear current authors list. */
	public void clearAuthors() {
		authorList.clear();
	}
	
	/** Insert a author to the book's author list
	 * @param author to insert */
	public void addAuthor(Author author) {
		authorList = new ArrayList<Author>();
		authorList.add(author);
		dirty = true;
	}
	
	/** Remove an author from the book's list 
	 * @param author to be removed. */
	public void removeAuthor(Author author) {
		authorList.remove(author);
		dirty = true;
	}
	
	/** Remove all current authors from a book. */
	public void removeAllAuthors() {
		authorList.clear();
	}
	
	/** @return book authors as a text to display */ 
	public String getAuthors() {
		if(dirty) {
			dirty = false;
			authors = "";
			int i = 0;
			for(Author a : authorList) {
				if(i > 0) {
					authors += ";";
				}
				authors += a.getLastName() + ", " + a.getName();
				i++;
			}
		}
		return authors;
	}
	
	/** @return an {@link ObservableList} with a copy of our authors list. */
	public ObservableList<Author> getAuthorsCopy() {
		return FXCollections.observableArrayList(authorList);
	}
	
	/** @return book textual description (authors + title) */
	public String getDescription() {
		return getAuthors() + ". " + getTitle();
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getDescription();
	}

}
