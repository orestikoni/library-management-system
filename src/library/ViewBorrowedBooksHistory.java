package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ViewBorrowedBooksHistory {

    private TableView<BorrowedBookRecord> tableView;
    private ObservableList<BorrowedBookRecord> borrowedBooks;
    private String username; // The logged-in user's username

    public ViewBorrowedBooksHistory(String username) {
        this.username = username;
        tableView = new TableView<>();
        borrowedBooks = FXCollections.observableArrayList();
        loadBorrowedBooks();
        setupTableColumns();
        tableView.setItems(borrowedBooks);
    }

    public Node getView() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: linear-gradient(to bottom, #e8f5e9, #ffffff);");
        
        Label header = new Label("Your Borrowed Books History");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        
        container.getChildren().addAll(header, tableView);
        return container;
    }

    @SuppressWarnings("unchecked")
	private void setupTableColumns() {
        TableColumn<BorrowedBookRecord, String> bookTitleCol = new TableColumn<>("Book Title");
        bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        bookTitleCol.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TableColumn<BorrowedBookRecord, LocalDate> borrowDateCol = new TableColumn<>("Borrow Date");
        borrowDateCol.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        borrowDateCol.setCellFactory(column -> new TableCell<BorrowedBookRecord, LocalDate>() {
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.format(formatter));
            }
        });
        borrowDateCol.setStyle("-fx-font-size: 14px;");

        TableColumn<BorrowedBookRecord, LocalDate> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        dueDateCol.setCellFactory(column -> new TableCell<BorrowedBookRecord, LocalDate>() {
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.format(formatter));
            }
        });
        dueDateCol.setStyle("-fx-font-size: 14px;");

        TableColumn<BorrowedBookRecord, String> overdueCol = new TableColumn<>("Overdue Status");
        overdueCol.setCellValueFactory(new PropertyValueFactory<>("overdueStatus"));
        overdueCol.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        tableView.getColumns().addAll(bookTitleCol, borrowDateCol, dueDateCol, overdueCol);
    }

    @SuppressWarnings("unchecked")
	private void loadBorrowedBooks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("borrowedBooks.dat"))) {
            List<BorrowedBookRecord> list = (List<BorrowedBookRecord>) in.readObject();
            List<BorrowedBookRecord> userRecords = list.stream()
                    .filter(record -> record.getUsername().equals(username))
                    .collect(Collectors.toList());
            borrowedBooks.setAll(userRecords);
        } catch (IOException | ClassNotFoundException e) {
            borrowedBooks.clear();
        }
    }
}

