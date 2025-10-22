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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowBooks {
    private static final int BORROW_LIMIT = 5;
    private static final int BORROW_DAYS = 18;

    private User loggedInUser;
    private ObservableList<Book> availableBooks;
    private ObservableList<BorrowedBookRecord> borrowedBooks;

    private ListView<Book> availableBooksListView;
    private ListView<BorrowedBookRecord> borrowedBooksListView;
    private Label limitLabel;

    public BorrowBooks(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        availableBooks = FXCollections.observableArrayList();
        borrowedBooks = FXCollections.observableArrayList();
        loadBooks();
        availableBooksListView = new ListView<>(availableBooks);
        borrowedBooksListView = new ListView<>(borrowedBooks);
        setupAvailableBooksCellFactory();
        setupBorrowedBooksCellFactory();
    }

    public Node getView() {
        BorderPane mainPane = new BorderPane();
        mainPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f9fbe7, #ffffff);");
        
        // Top: Borrowing limit.
        limitLabel = new Label("Borrowing Limit: Maximum " + BORROW_LIMIT + " books. Currently borrowed: " + borrowedBooks.size());
        limitLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333; -fx-padding: 10px;");
        mainPane.setTop(limitLabel);
        
        // Left: Available Books.
        VBox availableBox = new VBox(10);
        availableBox.setPadding(new Insets(10));
        availableBox.setPrefWidth(500);
        Label availableLabel = new Label("Available Books");
        availableLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #444;");
        availableBox.getChildren().addAll(availableLabel, availableBooksListView);
        mainPane.setLeft(availableBox);
        
        // Right: Borrowed Books.
        VBox borrowedBox = new VBox(10);
        borrowedBox.setPadding(new Insets(10));
        borrowedBox.setPrefWidth(500);
        Label borrowedLabel = new Label("Your Borrowed Books");
        borrowedLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #444;");
        borrowedBox.getChildren().addAll(borrowedLabel, borrowedBooksListView);
        mainPane.setRight(borrowedBox);
        
        // Bottom: Borrow button.
        Button borrowButton = new Button("Borrow Selected Book");
        borrowButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #66bb6a, #43a047);" +
                              " -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10px 20px;");
        borrowButton.setOnAction(e -> borrowSelectedBook());
        HBox bottomBox = new HBox(borrowButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        mainPane.setBottom(bottomBox);
        
        return mainPane;
    }

    @SuppressWarnings("unchecked")
	private void borrowSelectedBook() {
        Book selectedBook = availableBooksListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Please select a book to borrow.");
            return;
        }
        if (selectedBook.getQuantity() <= 0) {
            showAlert("Error", "The selected book is not available.");
            return;
        }
        if (borrowedBooks.size() >= BORROW_LIMIT) {
            showAlert("Error", "You have reached your borrowing limit of " + BORROW_LIMIT + " books.");
            return;
        }

        selectedBook.setQuantity(selectedBook.getQuantity() - 1);
        saveBooks();
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(BORROW_DAYS);
        BorrowedBookRecord record = new BorrowedBookRecord(
                loggedInUser.getUsername(),
                selectedBook,
                borrowDate,
                dueDate,
                false,
                null
        );
        
        List<BorrowedBookRecord> records = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("borrowedBooks.dat"))) {
            records = (List<BorrowedBookRecord>) in.readObject();
        } catch (Exception ex) {
            // If file doesn't exist, use empty list.
        }
        records.add(record);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("borrowedBooks.dat"))) {
            out.writeObject(records);
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to save the borrowing record.");
            return;
        }
        
        borrowedBooks.add(record);
        availableBooksListView.refresh();
        borrowedBooksListView.refresh();
        updateLimitLabel();
        showAlert("Success", "You have borrowed \"" + selectedBook.getTitle() + "\". Please return it by " + dueDate.toString() + ".");
    }

    private void updateLimitLabel() {
        limitLabel.setText("Borrowing Limit: Maximum " + BORROW_LIMIT + " books. Currently borrowed: " + borrowedBooks.size());
    }

    private void saveBooks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("books.dat"))) {
            out.writeObject(new ArrayList<>(availableBooks));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save books data.");
        }
    }

    @SuppressWarnings("unchecked")
	private void loadBooks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("books.dat"))) {
            List<Book> loadedBooks = (List<Book>) in.readObject();
            availableBooks.setAll(loadedBooks);
        } catch (IOException | ClassNotFoundException e) {
            availableBooks.clear();
        }
    }

    private void setupAvailableBooksCellFactory() {
        availableBooksListView.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
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
                            ImageView coverImageView = new ImageView();
                            if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
                                Image coverImage = new Image("file:" + book.getCoverImagePath(), 60, 60, true, true);
                                coverImageView.setImage(coverImage);
                            }
                            coverImageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 4, 0, 2, 2);");

                            Label titleLabel = new Label("Title: " + book.getTitle());
                            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                            Label authorLabel = new Label("Author: " + book.getAuthor());
                            authorLabel.setStyle("-fx-font-size: 14px;");
                            Label genreLabel = new Label("Genre: " + book.getGenre());
                            genreLabel.setStyle("-fx-font-size: 14px;");
                            Label isbnLabel = new Label("ISBN: " + book.getIsbn());
                            isbnLabel.setStyle("-fx-font-size: 14px;");
                            String availabilityStatus = (book.getQuantity() > 0 ? "Available" : "Not Available");
                            Label availabilityLabel = new Label("Availability: " + availabilityStatus);
                            availabilityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (book.getQuantity() > 0 ? "#228B22" : "#B22222") + ";");

                            VBox detailsBox = new VBox(5, titleLabel, authorLabel, genreLabel, isbnLabel, availabilityLabel);
                            detailsBox.setAlignment(Pos.CENTER_LEFT);

                            Button viewReviewsButton = new Button("View Ratings & Reviews");
                            viewReviewsButton.setStyle("-fx-font-size: 14px; -fx-background-color: linear-gradient(to right, #ffa500, #ff4500);" +
                                                        " -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 6px 12px;");
                            viewReviewsButton.setOnAction(e -> {
                                ViewRatingsAndReviews viewRR = new ViewRatingsAndReviews(book);
                                viewRR.show();
                            });

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

    private void setupBorrowedBooksCellFactory() {
        borrowedBooksListView.setCellFactory(new Callback<ListView<BorrowedBookRecord>, ListCell<BorrowedBookRecord>>() {
            @Override
            public ListCell<BorrowedBookRecord> call(ListView<BorrowedBookRecord> listView) {
                return new ListCell<BorrowedBookRecord>() {
                    @Override
                    protected void updateItem(BorrowedBookRecord record, boolean empty) {
                        super.updateItem(record, empty);
                        if (empty || record == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText("Title: " + record.getBookTitle() + " | Due: " + record.getDueDate().toString());
                            setStyle("-fx-font-size: 14px;");
                        }
                    }
                };
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
