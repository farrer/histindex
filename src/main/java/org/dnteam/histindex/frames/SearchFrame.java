package org.dnteam.histindex.frames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.Quote;
import org.dnteam.histindex.database.QuoteManager;
import org.dnteam.histindex.widgets.AuthorSelector;
import org.dnteam.histindex.widgets.BookSelector;
import org.dnteam.histindex.widgets.KeywordSelector;
import org.dnteam.histindex.widgets.SourceSelector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchFrame extends BaseFrame {
	
	private final Database database;
	private Stage stage;
	private KeywordSelector keywords;
	private AuthorSelector authors;
	private BookSelector books;
	private SourceSelector sources;
	private TextField text;
	private Scene searchScene;
	private ObservableList<Quote> resultList;
	private Scene resultScene;
	
	/** Cosntructor.
	 * @param database {@link Database} to use. */
	public SearchFrame(Database database) {
		this.database = database;
		
		try {
			stage = new Stage();
			stage.setTitle("Search");

			searchScene = createSearchScene();
			resultScene = createResultScene();

			stage.setScene(searchScene);
			stage.show();

		} catch(SQLException e) {
			showError("Couldn't load information for search: '" + e.getMessage() + "'");
		}
	}
	
	/** Create the scene used for create a new search.
	 * @return Created Scene.
	 * @throws SQLException */
	private Scene createSearchScene() throws SQLException {
		BorderPane root = new BorderPane(); 
		
		GridPane grid = new GridPane();
		grid.setVgap(2);
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.setHgap(2);
		
		VBox vert = new VBox();
		
		HBox textBox = new HBox();
		textBox.setPadding(new Insets(5, 5, 5, 2));
		textBox.setAlignment(Pos.CENTER_LEFT);
		text = new TextField();
		text.setMinWidth(745);
		Label textLabel = new Label("Text: ");
		textBox.getChildren().addAll(textLabel, text);
		
		try(Connection conn = DriverManager.getConnection(database.getURL())) {
			keywords = new KeywordSelector(conn, grid, 1, 2, FXCollections.observableArrayList());
			keywords.setWidth(300);
			authors = new AuthorSelector(conn, grid, 4, 2, FXCollections.observableArrayList());
			authors.setWidth(300);
			books = new BookSelector(conn, grid, 1, 6, FXCollections.observableArrayList());
			books.setWidth(300);
			sources = new SourceSelector(conn, grid, 4, 6, FXCollections.observableArrayList());
			sources.setWidth(300);
		}
		
		vert.getChildren().addAll(textBox, grid);
		root.setCenter(vert);
		
		
		HBox bottom = new HBox();
		bottom.setAlignment(Pos.BOTTOM_CENTER);
		bottom.setSpacing(10);
		
		Button buttonSearch = new Button("Search");
		buttonSearch.setDefaultButton(true);
		buttonSearch.setPrefSize(100, 20);
		buttonSearch.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				onSeachButtonClick();
			}
		});
		
		Button buttonCancel = createCancelButton();
		
		bottom.getChildren().addAll(buttonSearch, buttonCancel);
		root.setBottom(bottom);
		
		return new Scene(root);
	}
	
	/** Create a cancel button for closing the stage.
	 * @return created Button. */
	private Button createCancelButton() {
		Button buttonCancel = new Button("Cancel");
		buttonCancel.setCancelButton(true);
		buttonCancel.setPrefSize(100, 20);
		buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				stage.close();
			}
		});
		
		return buttonCancel;
	}
	
	/** Create the scene used for displaying search result.
	 * @return Created Scene. */
	private Scene createResultScene() {
		BorderPane root = new BorderPane();
		
		resultList = FXCollections.observableArrayList();
		
		/* At center, our result grid */
		TableView<Quote> resultTable = new TableView<Quote>();
		resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		resultTable.setPrefWidth(800);
		resultTable.setItems(resultList);
		
		TableColumn<Quote, String> textCol = new TableColumn<Quote, String>("Text");		
		textCol.setCellValueFactory(new PropertyValueFactory<Quote, String>("text"));
		resultTable.getColumns().add(textCol);
		
		TableColumn<Quote, String> whereCol = new TableColumn<Quote, String>("Where");		
		whereCol.setCellValueFactory(new PropertyValueFactory<Quote, String>("whereDescription"));
		whereCol.setMinWidth(300);
		resultTable.getColumns().add(whereCol);
		
		TableColumn<Quote, String> pageCol = new TableColumn<Quote, String>("Page");
		pageCol.setMaxWidth(60);
		pageCol.setMinWidth(40);
		pageCol.setCellValueFactory(new PropertyValueFactory<Quote, String>("page"));
		resultTable.getColumns().add(pageCol);
		
		root.setCenter(resultTable);
		
		/* At bottom, export and cancel buttons */
		HBox bottom = new HBox();
		bottom.setAlignment(Pos.BOTTOM_CENTER);
		bottom.setSpacing(10);
		
		Button buttonExport = new Button("Export");
		buttonExport.setPrefSize(100, 20);
		buttonExport.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				onExportButtonClick();
			}
		});
		
		Button buttonCancel = createCancelButton();
		
		bottom.getChildren().addAll(buttonExport, buttonCancel);
		root.setBottom(bottom);
		
		return new Scene(root);
	}
	
	/** Do the desired search and display its result. */
	private void onSeachButtonClick() {
		
		try(Connection conn = DriverManager.getConnection(database.getURL())) {
			QuoteManager qm = QuoteManager.getSingleton();
			
			resultList.addAll(qm.search(conn, keywords.getSelected(), books.getSelected(), 
					authors.getSelected(), sources.getSelected(), text.getText()));
			QuoteManager.getSingleton().populateRelatedInfo(conn, resultList);
		} catch(SQLException e) {
			showError("Error while searching: '" + e.getMessage() + "'");
		}
		
		stage.setScene(resultScene);
	}
	
	/** Export the search */
	private void onExportButtonClick() {
	}

}
