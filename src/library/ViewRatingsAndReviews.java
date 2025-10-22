package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ViewRatingsAndReviews {
    private Book book;
    private ObservableList<Review> reviewList;
    private ListView<Review> reviewListView;
    private static final String REVIEWS_FILE = "reviews.dat";
    
    public ViewRatingsAndReviews(Book book) {
         this.book = book;
         reviewList = FXCollections.observableArrayList();
         reviewListView = new ListView<>();
         loadReviews();
         reviewListView.setItems(reviewList);
    }
    
    // Opens a new window displaying reviews for this book.
    public void show() {
         Stage stage = new Stage();
         VBox root = new VBox(10);
         root.setPadding(new Insets(10));
         Label header = new Label("Ratings & Reviews for: " + book.getTitle());
         header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
         root.getChildren().addAll(header, reviewListView);
         Scene scene = new Scene(root, 400, 300);
         stage.setScene(scene);
         stage.setTitle("Ratings & Reviews");
         stage.show();
    }
    
    @SuppressWarnings("unchecked")
	private void loadReviews() {
         List<Review> allReviews = new ArrayList<>();
         try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(REVIEWS_FILE))) {
              allReviews = (List<Review>) in.readObject();
         } catch (IOException | ClassNotFoundException e) {
              // If no reviews exist, allReviews remains empty.
         }
         // Debug: print number of reviews loaded from file.
         System.out.println("DEBUG: Loaded " + allReviews.size() + " reviews from " + REVIEWS_FILE);
         for (Review r : allReviews) {
              if (r.getBookTitle().equalsIgnoreCase(book.getTitle())) {
                   reviewList.add(r);
              }
         }
         // Debug: print number of reviews for this book.
         System.out.println("DEBUG: Found " + reviewList.size() + " reviews for book: " + book.getTitle());
    }
}
