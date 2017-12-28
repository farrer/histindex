package org.dnteam.histindex.frames;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.Quote;
import org.dnteam.histindex.database.QuoteManager;
import org.dnteam.histindex.exporters.CSVExporter;
import org.dnteam.histindex.exporters.Exporter;
import org.dnteam.histindex.exporters.PdfExporter;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import net.sf.nervalreports.core.ReportGenerationException;

/** Frame used to create and display {@link Quote} searches.
 * @author farrer */
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
	private TextArea quoteText;
	private TextArea comment;
	private TextArea keys;
	private Scene resultScene;
	
	/** Constructor.
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
		
		/* Right area with current selected text and comment. */
		VBox right = new VBox();
		
		quoteText = new TextArea();
		quoteText.setWrapText(true);
		quoteText.setPrefWidth(200);
		quoteText.setPrefHeight(200);
		quoteText.setEditable(false);
		
		comment = new TextArea();
		comment.setWrapText(true);
		comment.setPrefWidth(200);
		comment.setPrefHeight(100);
		comment.setEditable(false);
		
		keys = new TextArea();
		keys.setWrapText(true);
		keys.setPrefWidth(200);
		keys.setPrefHeight(100);
		keys.setEditable(false);
		
		right.getChildren().addAll(quoteText, comment, keys);
		root.setRight(right);
		
		/* At center, our result grid */
		TableView<Quote> resultTable = new TableView<Quote>();
		resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		resultTable.setTooltip(new Tooltip("Double-click on selection to remove from result."));
		resultTable.setPrefWidth(800);
		resultTable.setItems(resultList);
		
//		TableColumn<Quote, Long> idCol = new TableColumn<Quote, Long>("Id");
//		idCol.setMaxWidth(60);
//		idCol.setMinWidth(40);
//		idCol.setCellValueFactory(new PropertyValueFactory<Quote, Long>("id"));
//		resultTable.getColumns().add(idCol);
		
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
		resultTable.setOnMousePressed(new EventHandler<MouseEvent>() {
		    public void handle(MouseEvent event) {
		    	if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
		    		removeSelected(resultTable.getSelectionModel().getSelectedItem());
		    	} else {
		    		setSelected(resultTable.getSelectionModel().getSelectedItem());
		    	}
		    }
		});
		resultTable.setOnKeyReleased(new EventHandler<KeyEvent>() {
		    public void handle(KeyEvent event) {
		    	if (event.getCode() == KeyCode.DELETE) {
		    		removeSelected(resultTable.getSelectionModel().getSelectedItem());
		        } else {
		        	/* Show the current selection full text */
		        	setSelected(resultTable.getSelectionModel().getSelectedItem());
		        }
		    }
		});
		
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
	
	/** Remove current selected {@link Quote} from the result list.
	 * @param selected current selected {@link Quote}. */
	private void removeSelected(Quote selected) {
    	if(selected != null) {
    		resultList.remove(selected);
    		setSelected(null);
    	}
	}
	
	/** Change current selected {@link Quote} at result list.
	 * @param selected new selected {@link Quote}. */
	private void setSelected(Quote selected) {
		if(selected != null) {
    		quoteText.setText(selected.getText());
    		comment.setText(selected.getComment());
    		keys.setText(selected.getKeywords());
        } else {
        	quoteText.clear();
        	comment.clear();
        	keys.clear();
        }
	}
	
	/** Do the desired search and display its result. */
	private void onSeachButtonClick() {
		
		searchScene.getRoot().setDisable(true);
		
		try(Connection conn = DriverManager.getConnection(database.getURL())) {
			QuoteManager qm = QuoteManager.getSingleton();
			
			resultList.addAll(qm.search(conn, keywords.getSelected(), books.getSelected(), 
					authors.getSelected(), sources.getSelected(), text.getText()));
			QuoteManager.getSingleton().populateRelatedInfo(conn, resultList);
		} catch(SQLException e) {
			showError("Error while searching: '" + e.getMessage() + "'");
		}
		
		searchScene.getRoot().setDisable(false);
		
		stage.setScene(resultScene);
	}
	
	/** Export the search */
	private void onExportButtonClick() {
		
		resultScene.getRoot().setDisable(true);
		
		final FileChooser fileChooser = new FileChooser();
		final ExtensionFilter pdfExtension = new ExtensionFilter("PDF", "*.pdf");
		final ExtensionFilter csvExtension = new ExtensionFilter("CSV", "*.csv");
		fileChooser.getExtensionFilters().addAll(pdfExtension, csvExtension);
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			
			/* Create the desired exporter by the defined extension. */
			Exporter exporter = null;
			String extension = "";
			if(fileChooser.getSelectedExtensionFilter().equals(csvExtension)) {
				exporter = new CSVExporter(database, resultList);
				extension = ".csv";
			}
			else /*if(fileChooser.getSelectedExtensionFilter().equals(pdfExtension))*/ {			
				exporter = new PdfExporter(database, resultList);
				extension = ".pdf";
			} 
			
			/* Do the export */
			try {
				String fileName = file.getAbsolutePath();
				if(!fileName.endsWith(extension)) {
					fileName += extension;
				}
				exporter.generate(keywords.getSelected(), books.getSelected(), 
						authors.getSelected(), sources.getSelected(), text.getText());
				exporter.export(fileName);
				Alert alert = new Alert(AlertType.INFORMATION, "The search was exported");
				alert.showAndWait();
			} catch (ReportGenerationException e) {
				exporter.release();
				showError("Error while exporting results: '" + e.getMessage() + "'");
			}
			
		}
		
		resultScene.getRoot().setDisable(false);
		
	}

}
