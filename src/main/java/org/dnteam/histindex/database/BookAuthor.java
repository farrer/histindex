package org.dnteam.histindex.database;

/** The Book-Author relational class.
 * @author farrer */
public class BookAuthor extends ComposedEntity<Book, Author> {

	/** Default Constructor */
	public BookAuthor() {
	}
	
	/** Constructor setting Book and Author */
	public BookAuthor(Book b, Author a) {
		setBook(b);
		setAuthor(a);
	}
	
   public Book getBook() {
   	return getFirstEntity();
   }
   
   public void setBook(Book book) {
   	setFirstEntity(book);
   }
   
   public Author getAuthor() {
   	return getSecondEntity();
   }
   
   public void setAuthor(Author author) {
   	setSecondEntity(author);
   }
   
   public Long getBookId() {
   	return getKey().getFirst();
   }
   
   public Long getAuthorId() {
   	return getKey().getSecond();
   }
   
   @Override
   public BookManager getFirstEntityManager() {
   	return BookManager.getSingleton();
   }
   
   @Override
   public AuthorManager getSecondEntityManager() {
   	return AuthorManager.getSingleton();
   }
}
