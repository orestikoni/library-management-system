package library;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CheckBookAvailability {

    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e0f7fa, #ffffff);");
        
        Label header = new Label("Check Book Availability");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter book title...");
        searchField.setMaxWidth(300);
        searchField.setStyle("-fx-font-size: 16px; -fx-padding: 8px;");
        
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #42a5f5, #1e88e5);" +
                              " -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        
        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        
        searchButton.setOnAction(e -> {
            String title = searchField.getText().trim();
            if (title.isEmpty()) {
                resultLabel.setText("Please enter a book title.");
                return;
            }
            Book foundBook = findBookByTitle(title);
            if (foundBook != null && foundBook.getQuantity() > 0) {
                resultLabel.setText("\"" + foundBook.getTitle() + "\" is available in the library!");
            } else {
                resultLabel.setText("\"" + title + "\" is not available in the library!");
            }
        });
        
        root.getChildren().addAll(header, searchField, searchButton, resultLabel);
        return root;
    }
    
    @SuppressWarnings("unchecked")
	private Book findBookByTitle(String title) {
        List<Book> books = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("books.dat"))) {
            books = (List<Book>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }
}

