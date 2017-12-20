package org.dnteam.histindex.frames;

import java.sql.Connection;
import java.sql.SQLException;

import org.dnteam.histindex.database.Author;
import org.dnteam.histindex.database.AuthorManager;
import org.dnteam.histindex.database.BookAuthorManager;
import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.EntityManager;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/** Edit frame for {@link Author}s.
 * @author farrer */
public class AuthorEditFrame extends BaseEditFrame<Author> {
	
	private TextField name;
	private TextField lastName;

	/** Constructor.
	 * @param database database to use.
	 * @param entity Author to edit or null for new one.
	 * @param caller if opened by a Author list. */
	public AuthorEditFrame(Database database, Author entity, AuthorListFrame caller) {
		super(database, "Author", entity, caller);
	}

	/** {@inheritDoc} */
	@Override
	public void addFields(GridPane grid, Author author) {
		name = new TextField();
		name.setText(author.getName());
		grid.add(new Label("Name: "), 0, 0);
		grid.add(name, 1, 0);
		
		lastName = new TextField();
		lastName.setText(author.getLastName());
		grid.add(new Label("Last name: "), 0, 1);
		grid.add(lastName, 1, 1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean populateAndValidateEntity(Author author) {
		if(name.getText() == null || name.getText().isEmpty()) {
			showError("Author's name should be defined");
			return false;
		}
		
		if(lastName.getText() == null || lastName.getText().isEmpty()) {
			showError("Author's last name should be defined");
			return false;
		}
		
		author.setName(name.getText());
		author.setLastName(lastName.getText().toUpperCase());
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager<Author> getManager() {
		return AuthorManager.getSingleton();
	}
	
	/** {@inheritDoc} */
	@Override
	public void doBeforeDelete(Connection con, Author author) throws SQLException {
		/* We should delete all BookAuthor information for this author */
		BookAuthorManager.getSingleton().deleteAuthorFromBooks(con, author.getId());
	}

}
