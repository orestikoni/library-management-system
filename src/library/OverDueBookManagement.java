package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class OverDueBookManagement {

    private ObservableList<BorrowedBookRecord> overdueRecords;
    private ListView<BorrowedBookRecord> listView;
    private static final double FINE_PER_DAY = 2.0; // Example fine per day

    public OverDueBookManagement() {
        overdueRecords = FXCollections.observableArrayList();
        listView = new ListView<>();
        loadOverdueRecords();
        listView.setItems(overdueRecords);
        setupListViewCellFactory();
    }

    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1px;");

        Label header = new Label("Overdue Book Management");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button chargeFineButton = new Button("Charge Fine & Send Reminder");
        chargeFineButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 5px;");
        chargeFineButton.setOnAction(e -> chargeFineAndSendReminder());

        root.getChildren().addAll(header, listView, chargeFineButton);
        return root;
    }

    @SuppressWarnings("unchecked")
	private void loadOverdueRecords() {
        List<BorrowedBookRecord> allRecords = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("borrowedBooks.dat"))) {
            allRecords = (List<BorrowedBookRecord>) in.readObject();
        } catch (Exception e) { }
        LocalDate today = LocalDate.now();
        for (BorrowedBookRecord rec : allRecords) {
            if ((!rec.isReturned() && today.isAfter(rec.getDueDate())) ||
                (rec.isReturned() && rec.getReturnDate() != null && rec.getReturnDate().isAfter(rec.getDueDate()))) {
                overdueRecords.add(rec);
            }
        }
    }

    private void setupListViewCellFactory() {
        listView.setCellFactory(param -> new ListCell<BorrowedBookRecord>() {
            @Override
            protected void updateItem(BorrowedBookRecord rec, boolean empty) {
                super.updateItem(rec, empty);
                if (empty || rec == null) {
                    setText(null);
                } else {
                    long daysOverdue = ChronoUnit.DAYS.between(rec.getDueDate(), LocalDate.now());
                    double fine = daysOverdue * FINE_PER_DAY;
                    setText("Book: " + rec.getBookTitle() + " | Overdue: " + daysOverdue + " days | Fine: $" + fine);
                    setStyle("-fx-padding: 5px; -fx-font-size: 14px;");
                }
            }
        });
    }

    private void chargeFineAndSendReminder() {
        BorrowedBookRecord selectedRecord = listView.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            showAlert("Error", "Please select an overdue record.");
            return;
        }
        long daysOverdue = ChronoUnit.DAYS.between(selectedRecord.getDueDate(), LocalDate.now());
        double fine = daysOverdue * FINE_PER_DAY;
        showAlert("Reminder Sent", "Reminder sent to user " + selectedRecord.getUsername() +
                  ". Fine: $" + fine);
    }

    private void showAlert(String title, String message) {
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle(title);
         alert.setHeaderText(null);
         alert.setContentText(message);
         alert.showAndWait();
    }
}

