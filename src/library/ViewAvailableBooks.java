package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ViewAvailableBooks {
    private ObservableList<Book> books;
    private ListView<Book> bookListView;
    
    public ViewAvailableBooks() {
         books = FXCollections.observableArrayList();
         bookListView = new ListView<>();
         loadBooks();
         bookListView.setItems(books);
         setupListViewCellFactory();
    }
    
    public Node getView() {
         VBox container = new VBox(15);
         container.setPadding(new Insets(20));
         container.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f0f0, #ffffff);");
         Label header = new Label("Available Books");
         header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");
         container.getChildren().addAll(header, bookListView);
         return container;
    }
    
    @SuppressWarnings("unchecked")
	private void loadBooks() {
         List<Book> loadedBooks = new ArrayList<>();
         try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("books.dat"))) {
              loadedBooks = (List<Book>) in.readObject();
         } catch (IOException | ClassNotFoundException e) {
              loadedBooks = new ArrayList<>();
         }
         books.setAll(loadedBooks);
    }
    
    private void setupListViewCellFactory() {
         bookListView.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
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
                                  // Cover image with drop shadow.
                                  ImageView coverImageView = new ImageView();
                                  if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
                                       Image img = new Image("file:" + book.getCoverImagePath(), 60, 60, true, true);
                                       coverImageView.setImage(img);
                                  }
                                  coverImageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 4, 0, 2, 2);");

                                  // Book details.
                                  Label titleLabel = new Label("Title: " + book.getTitle());
                                  titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                                  Label authorLabel = new Label("Author: " + book.getAuthor());
                                  authorLabel.setStyle("-fx-font-size: 14px;");
                                  Label genreLabel = new Label("Genre: " + book.getGenre());
                                  genreLabel.setStyle("-fx-font-size: 14px;");
                                  Label isbnLabel = new Label("ISBN: " + book.getIsbn());
                                  isbnLabel.setStyle("-fx-font-size: 14px;");
                                  String availability = (book.getQuantity() > 0 ? "Available" : "Not Available");
                                  Label availLabel = new Label("Availability: " + availability);
                                  availLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " 
                                          + (book.getQuantity() > 0 ? "#228B22" : "#B22222") + ";");

                                  VBox detailsBox = new VBox(5, titleLabel, authorLabel, genreLabel, isbnLabel, availLabel);
                                  detailsBox.setAlignment(Pos.CENTER_LEFT);

                                  // "View Ratings & Reviews" button with gradient and rounded corners.
                                  Button viewReviewsButton = new Button("View Ratings & Reviews");
                                  viewReviewsButton.setStyle("-fx-font-size: 14px; -fx-background-color: linear-gradient(to right, #ffa500, #ff4500);" +
                                                              " -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 6px 12px;");
                                  viewReviewsButton.setOnAction(e -> {
                                        ViewRatingsAndReviews viewRR = new ViewRatingsAndReviews(book);
                                        viewRR.show();
                                  });

                                  // Assemble cell.
                                  HBox cellContent = new HBox(15, coverImageView, detailsBox, viewReviewsButton);
                                  cellContent.setAlignment(Pos.CENTER_LEFT);
                                  cellContent.setPadding(new Insets(10));
                                  cellContent.setStyle("-fx-background-color: white; -fx-background-radius: 8px; " +
                                                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 2);");
                                  setGraphic(cellContent);
                             }
                        }
                   };
              }
         });
    }
}
