package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Callback;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchBooksView {
    private ObservableList<Book> searchResults;
    private ListView<Book> searchResultsListView;
    
    private String query;       // The text entered by the user.
    private String filterType;  // "Title", "Author", or "Genre"
    private String genreFilter; // Only used if filterType is "Genre"

    public SearchBooksView(String query, String filterType, String genreFilter) {
        this.query = query;
        this.filterType = filterType;
        this.genreFilter = genreFilter;
        searchResults = FXCollections.observableArrayList();
        searchResultsListView = new ListView<>();
        searchResultsListView.setItems(searchResults);
        setupListViewCellFactory();
        performSearch();
    }

    public Node getView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);
        container.setStyle("-fx-background-color: linear-gradient(to bottom, #fafafa, #ffffff);");

        Label header = new Label("Search Results");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        container.getChildren().addAll(header, searchResultsListView);
        return container;
    }

    @SuppressWarnings("unchecked")
	private void performSearch() {
        List<Book> allBooks = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("books.dat"))) {
            List<Book> loadedBooks = (List<Book>) in.readObject();
            allBooks.addAll(loadedBooks);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<Book> filteredBooks;
        String lowerQuery = query.toLowerCase();
        switch (filterType) {
            case "Title":
                filteredBooks = allBooks.stream()
                        .filter(b -> b.getTitle().toLowerCase().contains(lowerQuery))
                        .collect(Collectors.toList());
                break;
            case "Author":
                filteredBooks = allBooks.stream()
                        .filter(b -> b.getAuthor().toLowerCase().contains(lowerQuery))
                        .collect(Collectors.toList());
                break;
            case "Genre":
                filteredBooks = allBooks.stream()
                        .filter(b -> b.getGenre().equalsIgnoreCase(genreFilter))
                        .collect(Collectors.toList());
                break;
            default:
                filteredBooks = allBooks.stream()
                        .filter(b -> b.getTitle().toLowerCase().contains(lowerQuery))
                        .collect(Collectors.toList());
        }

        searchResults.setAll(filteredBooks);
    }

    private void setupListViewCellFactory() {
        searchResultsListView.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
            @Override
            public ListCell<Book> call(ListView<Book> listView) {
                return new ListCell<Book>() {
                    @Override
                    protected void updateItem(Book book, boolean empty) {
                        super.updateItem(book, empty);
                        if (empty || book == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Use a slightly larger cover image.
                            ImageView coverImageView = new ImageView();
                            if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
                                Image coverImage = new Image("file:" + book.getCoverImagePath(), 60, 60, true, true);
                                coverImageView.setImage(coverImage);
                            }
                            
                            Label titleLabel = new Label("Title: " + book.getTitle());
                            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                            Label authorLabel = new Label("Author: " + book.getAuthor());
                            authorLabel.setStyle("-fx-font-size: 14px;");
                            Label genreLabel = new Label("Genre: " + book.getGenre());
                            genreLabel.setStyle("-fx-font-size: 14px;");
                            Label isbnLabel = new Label("ISBN: " + book.getIsbn());
                            isbnLabel.setStyle("-fx-font-size: 14px;");
                            
                            VBox detailsBox = new VBox(5, titleLabel, authorLabel, genreLabel, isbnLabel);
                            detailsBox.setAlignment(Pos.CENTER_LEFT);
                            
                            // For SearchBooksView, you might add additional styling if needed.
                            HBox cellContent = new HBox(10, coverImageView, detailsBox);
                            cellContent.setAlignment(Pos.CENTER_LEFT);
                            cellContent.setPadding(new Insets(10));
                            cellContent.setStyle("-fx-background-color: white; -fx-background-radius: 8px; " +
                                                 "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);");
                            setGraphic(cellContent);
                        }
                    }
                };
            }
        });
    }
}
