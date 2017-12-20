package org.dnteam.histindex.frames;

import java.sql.Connection;
import java.sql.SQLException;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.EntityManager;
import org.dnteam.histindex.database.Quote;
import org.dnteam.histindex.database.QuoteManager;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/** List all {@link Quote}s from the database.
 * @author farrer */
public class QuoteListFrame extends BaseListFrame<Quote> {

	public QuoteListFrame(Database database) {
		super(database, "Quotes");
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager<Quote> getManager() {
		return QuoteManager.getSingleton();
	}

	/** {@inheritDoc} */
	@Override
	protected void defineColumns(TableView<Quote> table) {
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setPrefWidth(800);
		
		TableColumn<Quote, String> textCol = new TableColumn<Quote, String>("Text");		
		textCol.setCellValueFactory(new PropertyValueFactory<Quote, String>("text"));
		table.getColumns().add(textCol);
		
		TableColumn<Quote, String> whereCol = new TableColumn<Quote, String>("Where");		
		whereCol.setCellValueFactory(new PropertyValueFactory<Quote, String>("whereDescription"));
		whereCol.setMinWidth(200);
		table.getColumns().add(whereCol);
		
		TableColumn<Quote, String> keysCol = new TableColumn<Quote, String>("Keys");		
		keysCol.setCellValueFactory(new PropertyValueFactory<Quote, String>("keywords"));
		keysCol.setMaxWidth(120);
		keysCol.setMinWidth(60);
		table.getColumns().add(keysCol);
		
		TableColumn<Quote, String> pageCol = new TableColumn<Quote, String>("Page");
		pageCol.setMaxWidth(60);
		pageCol.setMinWidth(40);
		pageCol.setCellValueFactory(new PropertyValueFactory<Quote, String>("page"));
		table.getColumns().add(pageCol);
	}

	/** {@inheritDoc} */
	@Override
	protected void openEditFrame(Quote quote) {
		new QuoteEditFrame(database, quote, this);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void loadRelated(Connection conn, ObservableList<Quote> data) throws SQLException {
		QuoteManager.getSingleton().populateRelatedInfo(conn, data);
	}

}
