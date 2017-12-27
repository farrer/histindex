package org.dnteam.histindex.database;

import java.util.LinkedList;
import java.util.List;

import org.dnteam.histindex.util.StringUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** A quote from a {@link Book} or from a {@link Source}.
 * @author farrer */
public class Quote extends Entity {

	/** The quoted text */
	private String text;
	/** The quote commentary */
	private String comment;
	/** Page(s) where the text is */
	private String page = "";
	/** Identifier of the book which the quote is from (if any) */
	private long bookId;
	/** Book which the quote is from (if any) */
	private Book book;
	/** Identifier of the source from which the quote is (if any) */
	private long sourceId;
	/** Source from which the quote is (if any) */
	private Source source;
	/** {@link Keyword} list. */
	private List<Keyword> keywords = new LinkedList<Keyword>();
	/** If #page is dirty and need to reset #pageAsNumber */
	private boolean pageIsDirty = true;
	/** Integer value for 'page' */
	private int pageAsNumber = 0;
	
	/** @return {@link #text} */
	public String getText() {
		return text;
	}
	
	/** Set {@link #text}
	 * @param text new {@link #text} */
	public void setText(String text) {
		this.text = text;
	}
	
	/** @return {@link #comment} */
	public String getComment() {
		return comment;
	}
	
	/** Set {@link #comment}
	 * @param comment new {@link #comment} */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public int getPageAsNumber() {
		if(pageIsDirty) {
			String val = "";
			for(Character c : page.toCharArray()) {
				if(!Character.isDigit(c)) {
					break;
				}
				val += c;
			}
			if(!StringUtil.isEmpty(val)) {
				Integer.decode(val);
			} else {
				pageAsNumber = 0;
			}
			pageIsDirty = false;
		}
		
		return pageAsNumber;
	}
	
	/** @return {@link #page}. */
	public String getPage() {
		return page;
	}
	
	/** Set {@link #page}.
	 * @param page new {@link #page} value. */
	public void setPage(String page) {
		this.page = (page != null) ? page : "";
		this.pageIsDirty = true;
	}
	
	/** @return {@link #bookId}. */
	public long getBookId() {
		return bookId;
	}
	
	/** Set related book identifier. <br/>
	 * <b>Note:<b/> if {@link #book} is set to a distinct book, it will be nullified.
	 * @param bookId new {@link #bookId}. */
	public void setBookId(Long bookId) {
		this.bookId = bookId;
		if(this.book != null && this.book.getId() != bookId) {
			this.book = null;
		}
	}
	
	/** @return {@link #book} */
	public Book getBook() {
		return book;
	}
	
	/** Set {@link #book} (and {@link #bookId}).
	 * @param book new {@link #book} value. */
	public void setBook(Book book) {
		this.book = book;
		this.bookId = (book != null) ? book.getId() : 0;
	}
	
	/** @return {@link #sourceId}. */
	public long getSourceId() {
		return sourceId;
	}
	
	/** Set related source identifier. <br/>
	 * <b>Note:<b/> if {@link #source} is set to a distinct source, it will be nullified.
	 * @param sourceId new {@link #sourceId}. */
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId; 
		if(this.source != null && this.source.getId() != sourceId) {
			this.source = null;
		}
	}
	
	/** @return {@link #source}. */
	public Source getSource() {
		return source;
	}
	
	/** Set {@link #source} (and {@link #sourceId}).
	 * @param source new {@link #source} value. */
	public void setSource(Source source) {
		this.source = source;
		this.sourceId = (source != null) ? source.getId() : 0;
	}
	
	/** @return description about the book or source from the quote is from. */
	public String getWhereDescription() {
		if(book != null) {
			return book.getDescription();
		}
		if(source != null) {
			return source.getTitle();
		}
		
		return "?";
	}
	
	/** Clear current {@link #keywords}. */
	public void clearKeywords() {
		keywords.clear();
	}
	
	/** Add a {@link Keyword} to the quote.
	 * @param key {@link Keyword} to add. */
	public void addKeyword(Keyword key) {
		keywords.add(key);
	}
	
	/** Remove a {@link Keyword} from the quote.
	 * @param key {@link Keyword} to remove. */
	public void removeKeyword(Keyword key) {
		keywords.remove(key);
	}
	
	/** @return A {@link String} with all keywords descriptions of the quote. */
	public String getKeywords() {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for(Keyword key : keywords) {
			if(i > 0) {
				builder.append(", ");
			}
			builder.append(key.getName());
			i++;
		}
		return builder.toString();
	}
	
	/** @return a copy of {@link #keywords}. */
	public ObservableList<Keyword> getKeywordsCopy() {
		return FXCollections.observableArrayList(keywords);
	}

}
