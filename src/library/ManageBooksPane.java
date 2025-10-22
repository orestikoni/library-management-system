package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ManageBooksPane {
    // The list of books
    private ObservableList<Book> books = FXCollections.observableArrayList();
    private ListView<Book> bookListView = new ListView<>();

    // Text fields for book details
    private TextField titleField = new TextField();
    private TextField authorField = new TextField();
    private TextField genreField = new TextField();
    private TextField isbnField = new TextField();
    private TextField quantityField = new TextField();

    // Label and variable to show the chosen cover image file
    private Label coverImageLabel = new Label("No image selected");
    private String selectedCoverImagePath = "";

    // Root layout for this pane
    private VBox root;

    public ManageBooksPane() {
        setupUI();
        // Bind the books list to the ListView so that changes are reflected
        bookListView.setItems(books);
        loadBooks();
        setupListViewCellFactory();
    }

    private void setupUI() {
        // Apply a background style to the root pane
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1px;");

        // Create styled labels for each field
        Label header = new Label("Manage Books");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label titleLabel = new Label("Title:");
        titleLabel.setStyle("-fx-font-weight: bold;");
        Label authorLabel = new Label("Author:");
        authorLabel.setStyle("-fx-font-weight: bold;");
        Label genreLabel = new Label("Genre:");
        genreLabel.setStyle("-fx-font-weight: bold;");
        Label isbnLabel = new Label("ISBN:");
        isbnLabel.setStyle("-fx-font-weight: bold;");
        Label quantityLabel = new Label("Quantity:");
        quantityLabel.setStyle("-fx-font-weight: bold;");

        // Style text fields
        for (TextField tf : new TextField[]{titleField, authorField, genreField, isbnField, quantityField}) {
            tf.setPrefWidth(250);
            tf.setStyle("-fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: #bdc3c7;");
        }

        // Button to choose a cover image using a FileChooser
        Button chooseImageButton = new Button("Choose Cover Image");
        chooseImageButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5px;");
        chooseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Book Cover Image");
            // Allow only common image files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    "Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
            fileChooser.getExtensionFilters().add(extFilter);
            Stage stage = new Stage();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                selectedCoverImagePath = file.getAbsolutePath();
                coverImageLabel.setText(file.getName());
            }
        });
        coverImageLabel.setStyle("-fx-text-fill: #7f8c8d;");

        // Buttons for adding, editing, and deleting books
        Button addButton = new Button("Add Book");
        Button editButton = new Button("Edit Book");
        Button deleteButton = new Button("Delete Book");
        for (Button btn : new Button[]{addButton, editButton, deleteButton}) {
            btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5px;");
        }

        addButton.setOnAction(e -> addBook());
        editButton.setOnAction(e -> editBook());
        deleteButton.setOnAction(e -> deleteBook());

        // Assemble the form
        root.getChildren().addAll(
            header,
            titleLabel, titleField,
            authorLabel, authorField,
            genreLabel, genreField,
            isbnLabel, isbnField,
            quantityLabel, quantityField,
            chooseImageButton, coverImageLabel,
            addButton, editButton, deleteButton,
            new Label("Books List:"), bookListView
        );

        // Style the ListView
        bookListView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1px;");
    }

    // Set up the ListView so that each cell shows the cover image and details.
    private void setupListViewCellFactory() {
        bookListView.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
            public ListCell<Book> call(ListView<Book> param) {
                return new ListCell<Book>() {
                    @Override
                    protected void updateItem(Book item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Create an ImageView for the cover image
                            ImageView imageView = new ImageView();
                            if (item.getCoverImagePath() != null && !item.getCoverImagePath().isEmpty()) {
                                Image img = new Image("file:" + item.getCoverImagePath(), 50, 50, true, true);
                                imageView.setImage(img);
                            }
                            // Create a VBox to hold the text details
                            VBox textDetails = new VBox(5);
                            textDetails.getChildren().addAll(
                                new Label("Title: " + item.getTitle()),
                                new Label("Author: " + item.getAuthor()),
                                new Label("Genre: " + item.getGenre()),
                                new Label("ISBN: " + item.getIsbn()),
                                new Label("Quantity: " + item.getQuantity())
                            );
                            // Set a light style for the text details
                            for (Node node : textDetails.getChildren()) {
                                if (node instanceof Label) {
                                    node.setStyle("-fx-text-fill: #2c3e50;");
                                }
                            }
                            // Create an HBox to hold the image and the text details side by side
                            HBox hBox = new HBox(10);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.getChildren().addAll(imageView, textDetails);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });
    }

    // Returns the root node of this pane so it can be embedded elsewhere.
    public Node getView() {
        return root;
    }

    private void addBook() {
        try {
            String title = titleField.getText();
            String author = authorField.getText();
            String genre = genreField.getText();
            String isbn = isbnField.getText();
            int quantity = Integer.parseInt(quantityField.getText());

            // Create a new Book with the provided details and chosen cover image
            Book newBook = new Book(title, author, genre, isbn, quantity, selectedCoverImagePath);
            books.add(newBook);
            saveBooks();
            clearFields();
            showAlert("Success", "Book added successfully.");
        } catch (NumberFormatException ex) {
            showAlert("Error", "Quantity must be a valid number.");
        }
    }

    private void editBook() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Please select a book to edit.");
            return;
        }
        try {
            selectedBook.setTitle(titleField.getText());
            selectedBook.setAuthor(authorField.getText());
            selectedBook.setGenre(genreField.getText());
            selectedBook.setIsbn(isbnField.getText());
            selectedBook.setQuantity(Integer.parseInt(quantityField.getText()));
            selectedBook.setCoverImagePath(selectedCoverImagePath);
            bookListView.refresh();
            saveBooks();
            clearFields();
            showAlert("Success", "Book updated successfully.");
        } catch (NumberFormatException ex) {
            showAlert("Error", "Quantity must be a valid number.");
        }
    }

    private void deleteBook() {
        Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "Please select a book to delete.");
            return;
        }
        books.remove(selectedBook);
        saveBooks();
        showAlert("Success", "Book deleted successfully.");
    }

    // Save the list of books to a file
    private void saveBooks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("books.dat"))) {
            out.writeObject(new ArrayList<>(books));
        } catch (IOException ex) {
            showAlert("Error", "Failed to save books.");
        }
    }

    // Load the list of books from a file
    @SuppressWarnings("unchecked")
	private void loadBooks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("books.dat"))) {
            List<Book> loadedBooks = (List<Book>) in.readObject();
            books.setAll(loadedBooks);
        } catch (IOException | ClassNotFoundException ex) {
            // File may not exist yet; ignore the exception.
        }
    }

    // Clear the input fields after adding or editing a book.
    private void clearFields() {
        titleField.clear();
        authorField.clear();
        genreField.clear();
        isbnField.clear();
        quantityField.clear();
        selectedCoverImagePath = "";
        coverImageLabel.setText("No image selected");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
