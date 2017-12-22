package org.dnteam.histindex;

import java.io.File;
import java.sql.SQLException;

import org.dnteam.histindex.database.Database;
import org.dnteam.histindex.database.Entity;
import org.dnteam.histindex.exporters.ExporterFonts;
import org.dnteam.histindex.frames.AuthorEditFrame;
import org.dnteam.histindex.frames.AuthorListFrame;
import org.dnteam.histindex.frames.BookEditFrame;
import org.dnteam.histindex.frames.BookListFrame;
import org.dnteam.histindex.frames.KeywordEditFrame;
import org.dnteam.histindex.frames.KeywordListFrame;
import org.dnteam.histindex.frames.QuoteEditFrame;
import org.dnteam.histindex.frames.QuoteListFrame;
import org.dnteam.histindex.frames.SearchFrame;
import org.dnteam.histindex.frames.SourceEditFrame;
import org.dnteam.histindex.frames.SourceListFrame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The main class for HistIndex application.
 */
public class App extends Application {

	private Database database = null;
	private Menu menuFile;
	private Menu menuQuote;
	private Menu menuKeyword;
	private Menu menuAuthor;
	private Menu menuSource;
	private Menu menuBook;
	private Menu menuSearch;
	private Label databaseLabel;
	private HBox hbox;

	public static void main(String[] args) {
		launch(args);
	}

	/** Create and define our File menu.
	 * @param primaryStage stage where the menu is.
	 * @param fileChooser a FileChooser used for select our database.
	 * @return the Menu created. */
	private Menu createMenuFile(Stage primaryStage, FileChooser fileChooser) {
		Menu menuFile = new Menu("File");

		/* New */
		MenuItem newItem = new MenuItem("New", new ImageView(new Image("icons/database_add.png")));
		newItem.setAccelerator(
		      new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN));
		newItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				File file = fileChooser.showSaveDialog(primaryStage);
				if (file != null) {
					database = new Database(file.getAbsolutePath());
					try {
						database.createSchema();
					} catch (SQLException e) {
						Alert alert = new Alert(AlertType.WARNING, "Couldn't create database: " + e.getMessage());
						alert.showAndWait();
					}
				}
				setMenuAvailability();
			}
		});

		/* Load */
		MenuItem loadItem = new MenuItem("Load", new ImageView(new Image("icons/database_connect.png")));
		loadItem.setAccelerator(
		      new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN));
		loadItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				File file = fileChooser.showOpenDialog(primaryStage);
				if (file != null) {
					String fileName = file.getAbsolutePath();
					if(fileName.endsWith(".db")) {
						database = new Database(fileName);
					} else {
						database = new Database(fileName);
					}
				}
				setMenuAvailability();
			}
		});

		/* Exit */
		MenuItem exitItem = new MenuItem("Exit", new ImageView(new Image("icons/door_out.png")));
		exitItem.setAccelerator(
		      new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN));
		exitItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				Platform.exit();
			}
		});

		/* Add our items and done */
		menuFile.getItems().addAll(newItem, loadItem, exitItem);
		return menuFile;
	}
	
	/** Create and define our search menu.
	 * @param primaryStage Stage where the menu will be.
	 * @return created search Menu. */
	private Menu createMenuSearch(Stage primaryStage) {
		Menu menu = new Menu("Search");
		Image findImage = new Image("icons/magnifier.png");
		EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				new SearchFrame(database);
			}
		};

		MenuItem newItem = new MenuItem("New", new ImageView(findImage));
		newItem.setAccelerator(
		      new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN));
		newItem.setOnAction(eventHandler);
		
		menu.getItems().addAll(newItem);
		
		Button searchButton = new Button();
		searchButton.setTooltip(new Tooltip("Create a new search"));
		searchButton.setGraphic(new ImageView(findImage));
		searchButton.setOnAction(eventHandler);
		
		Separator sep = new Separator();
		sep.setOrientation(Orientation.VERTICAL);
		sep.setMinWidth(1);
		sep.setMaxWidth(1);
		sep.setBorder(null);
		sep.setHalignment(HPos.LEFT);
		hbox.getChildren().addAll(sep, searchButton);
		
		return menu;
	}
	
	/** Create a menu and toolbar elements for an {@link Entity}.
	 * @param primaryStage stage to insert the menu.
	 * @param title title of the {@link Entity}.
	 * @param pluralTitle title of the {@link Entity} at the plural form.
	 * @param imageAdd icon of the {@link Entity} insertion.
	 * @param imageList icon of the {@link Entity} list.
	 * @param addHandler action handler of insertion.
	 * @param listHandler action handler of list.
	 * @return created Menu. */
	private Menu createMenu(Stage primaryStage, String title, String pluralTitle, Image imageAdd, Image imageList,
			EventHandler<ActionEvent> addHandler, EventHandler<ActionEvent> listHandler) {
		
		Menu menu = new Menu(title);
		
		/* Insert */
		MenuItem insertItem = new MenuItem("Insert", new ImageView(imageAdd));
		insertItem.setOnAction(addHandler);
		Button insertButton = new Button();
		insertButton.setTooltip(new Tooltip("Insert " + title));
		insertButton.setGraphic(new ImageView(imageAdd));
		insertButton.setOnAction(addHandler);
		
		/* List */
		MenuItem listItem = new MenuItem("List", new ImageView(imageList));
		listItem.setOnAction(listHandler);
		Button listButton = new Button();
		listButton.setTooltip(new Tooltip("List " + pluralTitle));
		listButton.setGraphic(new ImageView(imageList));
		listButton.setOnAction(listHandler);
		
		menu.getItems().addAll(insertItem, listItem);
		Separator sep = new Separator();
		sep.setOrientation(Orientation.VERTICAL);
		sep.setMinWidth(1);
		sep.setMaxWidth(1);
		sep.setBorder(null);
		sep.setHalignment(HPos.LEFT);
		hbox.getChildren().addAll(sep, insertButton, listButton);
		
		return menu;
	}
	
	private void setMenuAvailability() {
		if(this.database != null) {
			menuQuote.setDisable(false);
			menuKeyword.setDisable(false);
			menuAuthor.setDisable(false);
			menuSource.setDisable(false);
			menuBook.setDisable(false);
			menuSearch.setDisable(false);
			hbox.setDisable(false);
			databaseLabel.setText("Database: " + database.getFilename());
		} else {
			menuQuote.setDisable(true);
			menuKeyword.setDisable(true);
			menuAuthor.setDisable(true);
			menuSource.setDisable(true);
			menuBook.setDisable(true);
			menuSearch.setDisable(true);
			hbox.setDisable(true);
			databaseLabel.setText("Database: None");
		}
		
	}
	
	/** {@inheritDoc} */
	@Override
	public void stop() {
		ExporterFonts.finish();
	}

	/** {@inheritDoc} */
	@Override
	public void start(Stage primaryStage) {
		
		HistIndexProperties.init();
		ExporterFonts.init("fonts/FreeSerif.ttf", "fonts/FreeSerifItalic.ttf", 
				"fonts/FreeSerifBold.ttf", "fonts/FreeSerifBoldItalic.ttf");
		
		primaryStage.setTitle("HistIndex " + HistIndexProperties.getVersion());
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent t) {
				Platform.exit();
			}
		});

		/* The file chooser we'll use for create a new or load databases */
		final FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("SQLite database", "*.db"));
		
		hbox = new HBox();

		/* Our main menu */
		final MenuBar menuBar = new MenuBar();
		menuFile = createMenuFile(primaryStage, fileChooser);
		menuBar.getMenus().add(menuFile);
		
		menuQuote = createMenu(primaryStage, "Quote", "Quotes", new Image("icons/comment_add.png"),
				new Image("icons/comment.png"), 
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent t) {
						new QuoteEditFrame(database, null, null);
					}
				}, 
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent t) {
						new QuoteListFrame(database);
					}
			   });
		menuBar.getMenus().add(menuQuote);
		
		menuKeyword = createMenu(primaryStage, "Keyword", "Keywords", new Image("icons/font_add.png"),
				new Image("icons/font.png"), 
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent t) {
						new KeywordEditFrame(database, null, null);
					}
				}, 
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent t) {
						new KeywordListFrame(database);
					}
			   });
		menuBar.getMenus().add(menuKeyword);
		
		menuAuthor = createMenu(primaryStage, "Author", "Authors", new Image("icons/user_add.png"),
				new Image("icons/group.png"), 
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent t) {
						new AuthorEditFrame(database, null, null);
					}
				}, 
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent t) {
						new AuthorListFrame(database);
					}
			   });
		menuBar.getMenus().add(menuAuthor);
		
		menuSource = createMenu(primaryStage, "Source", "Sources", new Image("icons/vcard_add.png"),
				new Image("icons/vcard.png"), 
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent t) {
						new SourceEditFrame(database, null, null);
					}
				}, 
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent t) {
						new SourceListFrame(database);
					}
			   });
		menuBar.getMenus().add(menuSource);
		
		menuBook = createMenu(primaryStage, "Book/Article", "Books/Articles", 
				new Image("icons/book_add.png"), new Image("icons/book.png"), 
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent t) {
						new BookEditFrame(database, null, null);
					}
				}, 
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent t) {
						new BookListFrame(database);
					}
			   });
		menuBar.getMenus().add(menuBook);
		
		menuSearch = createMenuSearch(primaryStage);
		menuBar.getMenus().add(menuSearch);
		
		VBox root = new VBox();
		databaseLabel = new Label("Database: None");
		databaseLabel.setPrefWidth(600);
		root.getChildren().addAll(menuBar, hbox, databaseLabel);
		Scene scene = new Scene(root);
		
		setMenuAvailability();

		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setX(0);
		primaryStage.setY(0);
	}
}
