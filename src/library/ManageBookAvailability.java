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

public class ManageBookAvailability {

    private ObservableList<Book> books;
    private ListView<Book> bookListView;
    private static final String BOOK_FILE = "books.dat";

    public ManageBookAvailability() {
        books = FXCollections.observableArrayList();
        bookListView = new ListView<>();
        loadBooks();
        bookListView.setItems(books);
        setupListViewCellFactory();
    }

    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1px;");

        Label header = new Label("Manage Book Availability");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Button toggleAvailabilityButton = new Button("Toggle Availability");
        toggleAvailabilityButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 5px;");
        toggleAvailabilityButton.setOnAction(e -> toggleAvailability());
        root.getChildren().addAll(header, bookListView, toggleAvailabilityButton);
        return root;
    }

    private void toggleAvailability() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Please select a book.");
            return;
        }
        if (selectedBook.getQuantity() > 0) {
            // Mark as unavailable by setting quantity to 0.
            selectedBook.setQuantity(0);
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Set Book Quantity");
            dialog.setHeaderText("Book \"" + selectedBook.getTitle() + "\" is currently unavailable.");
            dialog.setContentText("Enter new quantity to mark as available:");
            dialog.showAndWait().ifPresent(input -> {
                try {
                    int newQuantity = Integer.parseInt(input);
                    if (newQuantity < 0)
                        throw new NumberFormatException();
                    selectedBook.setQuantity(newQuantity);
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Invalid quantity. Please enter a non-negative integer.");
                }
            });
        }
        bookListView.refresh();
        saveBooks();
        showAlert("Success", "Availability updated for \"" + selectedBook.getTitle() + "\".");
    }

    @SuppressWarnings("unchecked")
	private void loadBooks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(BOOK_FILE))) {
            List<Book> loadedBooks = (List<Book>) in.readObject();
            books.setAll(loadedBooks);
        } catch (IOException | ClassNotFoundException e) {
            books.clear();
        }
    }

    private void saveBooks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(BOOK_FILE))) {
            out.writeObject(new ArrayList<>(books));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save books.");
        }
    }

    private void setupListViewCellFactory() {
        bookListView.setCellFactory(param -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setText(null);
                } else {
                    String avail = (book.getQuantity() > 0) ? "Available (" + book.getQuantity() + ")" : "Unavailable";
                    setText("Title: " + book.getTitle() + " | Availability: " + avail);
                    setStyle("-fx-padding: 5px; -fx-font-size: 14px;");
                }
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

