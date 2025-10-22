package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ReturnBooks {

    private User loggedInUser;
    private ObservableList<BorrowedBookRecord> borrowedRecords;
    private ListView<BorrowedBookRecord> borrowedListView;
    private static final double FEE_PER_DAY = 1.0;
    private static final String BORROWED_FILE = "borrowedBooks.dat";
    
    public ReturnBooks(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        borrowedRecords = FXCollections.observableArrayList();
        borrowedListView = new ListView<>();
        borrowedListView.setItems(borrowedRecords);
        setupListViewCellFactory();
        loadUserBorrowedBooks();
    }
    
    public Node getView() {
        BorderPane mainPane = new BorderPane();
        mainPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f3e5f5, #ffffff);");
        
        Label header = new Label("Return Borrowed Books");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4a148c;");
        mainPane.setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setMargin(header, new Insets(20, 0, 10, 0));
        
        VBox centerBox = new VBox(10, borrowedListView);
        centerBox.setPadding(new Insets(20));
        mainPane.setCenter(centerBox);
        
        Button returnButton = new Button("Return Selected Book");
        returnButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #66bb6a, #43a047);" +
                              " -fx-text-fill: white; -fx-background-radius: 10px; -fx-padding: 10px 20px;");
        returnButton.setOnAction(e -> returnSelectedBook());
        HBox bottomBox = new HBox(returnButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));
        mainPane.setBottom(bottomBox);
        
        return mainPane;
    }
    
    @SuppressWarnings("unchecked")
	private void loadUserBorrowedBooks() {
        List<BorrowedBookRecord> allRecords = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(BORROWED_FILE))) {
            allRecords = (List<BorrowedBookRecord>) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            // If no file, then empty list.
        }
        borrowedRecords.clear();
        for (BorrowedBookRecord record : allRecords) {
            if (record.getUsername().equals(loggedInUser.getUsername()) && !record.isReturned()) {
                borrowedRecords.add(record);
            }
        }
    }
    
    private void returnSelectedBook() {
        BorrowedBookRecord selectedRecord = borrowedListView.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            showAlert("Error", "Please select a book to return.");
            return;
        }
        LocalDate today = LocalDate.now();
        LocalDate dueDate = selectedRecord.getDueDate();
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, today);
        double fee = (daysOverdue > 0) ? daysOverdue * FEE_PER_DAY : 0;
        
        String feeMessage = (fee > 0) ? 
                "This book is overdue by " + daysOverdue + " days.\nYour overdue fee is $" + fee + "." 
                : "No overdue fees!";
        Alert feeAlert = new Alert(Alert.AlertType.INFORMATION);
        feeAlert.setTitle("Overdue Fee");
        feeAlert.setHeaderText(null);
        feeAlert.setContentText(feeMessage);
        feeAlert.showAndWait();
        
        // Mark the record as returned.
        selectedRecord.setReturned(true);
        selectedRecord.setReturnDate(today);
        updateBorrowedBooksFile(selectedRecord);
        borrowedRecords.remove(selectedRecord);
        showAlert("Success", "Book returned successfully.");
    }
    
    @SuppressWarnings("unchecked")
	private void updateBorrowedBooksFile(BorrowedBookRecord updatedRecord) {
        List<BorrowedBookRecord> allRecords = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(BORROWED_FILE))) {
            allRecords = (List<BorrowedBookRecord>) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            // Nothing to update.
        }
        for (BorrowedBookRecord record : allRecords) {
            if (record.getUsername().equals(updatedRecord.getUsername()) &&
                record.getBook().getIsbn().equals(updatedRecord.getBook().getIsbn()) &&
                record.getBorrowDate().equals(updatedRecord.getBorrowDate()) &&
                record.getDueDate().equals(updatedRecord.getDueDate())) {
                record.setReturned(true);
                record.setReturnDate(updatedRecord.getReturnDate());
            }
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(BORROWED_FILE))) {
            out.writeObject(allRecords);
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to update borrowing records.");
        }
    }
    
    private void setupListViewCellFactory() {
        borrowedListView.setCellFactory(param -> new ListCell<BorrowedBookRecord>() {
            @Override
            protected void updateItem(BorrowedBookRecord record, boolean empty) {
                super.updateItem(record, empty);
                if (empty || record == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText("Title: " + record.getBookTitle() + " | Due: " + record.getDueDate().toString());
                    setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
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
