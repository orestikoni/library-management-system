package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookReviewsAndRatings {

    private static final String REVIEWS_FILE = "reviews.dat";
    private ObservableList<Review> reviewsList;
    private ListView<Review> reviewsListView;
    private ComboBox<Book> bookComboBox;
    private User currentUser;

    public BookReviewsAndRatings(User currentUser) {
        this.currentUser = currentUser;
        reviewsList = FXCollections.observableArrayList();
        reviewsListView = new ListView<>();
        loadReviews();
        reviewsListView.setItems(reviewsList);
        bookComboBox = new ComboBox<>();
        loadBooksIntoComboBox();
    }

    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e3f2fd, #ffffff);");
        Label header = new Label("Book Reviews & Ratings");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1565c0;");
        
        Label selectBookLabel = new Label("Select Book:");
        selectBookLabel.setStyle("-fx-font-size: 16px;");
        Label ratingLabel = new Label("Rating (0-5):");
        ratingLabel.setStyle("-fx-font-size: 16px;");
        ComboBox<Integer> ratingComboBox = new ComboBox<>();
        ratingComboBox.getItems().addAll(0, 1, 2, 3, 4, 5);
        ratingComboBox.setValue(5);
        ratingComboBox.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
        
        Label reviewLabel = new Label("Review (up to 50 words):");
        reviewLabel.setStyle("-fx-font-size: 16px;");
        TextArea reviewArea = new TextArea();
        reviewArea.setWrapText(true);
        reviewArea.setPromptText("Enter your review here (max 50 words)");
        reviewArea.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
        
        Button submitButton = new Button("Submit Review");
        submitButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #ff9800, #f57c00);" +
                              " -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        submitButton.setOnAction(e -> {
            String reviewText = reviewArea.getText().trim();
            int wordCount = reviewText.isEmpty() ? 0 : reviewText.split("\\s+").length;
            if (wordCount > 50) {
                showAlert("Error", "Review must be 50 words or less.");
                return;
            }
            if (bookComboBox.getValue() == null) {
                showAlert("Error", "Please select a book.");
                return;
            }
            int rating = ratingComboBox.getValue();
            Book selectedBook = bookComboBox.getValue();
            Review newReview = new Review(selectedBook.getTitle(), currentUser.getUsername(), rating, reviewText);
            reviewsList.add(newReview);
            saveReviews();
            showAlert("Success", "Your review has been submitted.");
            reviewArea.clear();
            ratingComboBox.setValue(5);
        });
        
        VBox formBox = new VBox(10, selectBookLabel, bookComboBox, ratingLabel, ratingComboBox, reviewLabel, reviewArea, submitButton);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);");
        
        Label reviewsHeader = new Label("Existing Reviews:");
        reviewsHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #424242;");
        
        root.getChildren().addAll(header, formBox, reviewsHeader, reviewsListView);
        return root;
    }

    @SuppressWarnings("unchecked")
	private void loadBooksIntoComboBox() {
        List<Book> books = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("books.dat"))) {
            books = (List<Book>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Leave combo box empty if error.
        }
        bookComboBox.getItems().addAll(books);
        bookComboBox.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
    }

    @SuppressWarnings("unchecked")
	private void loadReviews() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(REVIEWS_FILE))) {
            List<Review> loadedReviews = (List<Review>) in.readObject();
            reviewsList.setAll(loadedReviews);
        } catch (IOException | ClassNotFoundException e) {
            reviewsList.clear();
        }
    }

    private void saveReviews() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(REVIEWS_FILE))) {
            out.writeObject(new ArrayList<>(reviewsList));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save reviews.");
        }
    }

    private void showAlert(String title, String message) {
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle(title);
         alert.setHeaderText(null);
         alert.setContentText(message);
         alert.showAndWait();
    }
}
