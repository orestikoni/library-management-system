package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BooksReservations {

    private ObservableList<Reservation> reservationsList;
    private TableView<Reservation> tableView;
    private static final String RESERVATION_FILE = "reservations.ser";
    private User currentUser;
    
    public BooksReservations(User currentUser) {
        this.currentUser = currentUser;
        reservationsList = FXCollections.observableArrayList();
        tableView = new TableView<>();
        loadReservations();
        tableView.setItems(reservationsList);
        setupTableColumns();
    }
    
    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #fff8e1, #ffffff);");
        
        Label header = new Label("Book Reservations");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ff8f00;");
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button cancelButton = new Button("Cancel Reservation");
        cancelButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #ef5350, #e53935); " +
                               "-fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        cancelButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        cancelButton.setOnAction(e -> cancelReservation());
        
        Button addButton = new Button("Reserve a Book");
        addButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #42a5f5, #1e88e5); " +
                            "-fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        addButton.setOnAction(e -> addReservation());
        
        buttonBox.getChildren().addAll(cancelButton, addButton);
        root.getChildren().addAll(header, tableView, buttonBox);
        return root;
    }
    
    @SuppressWarnings("unchecked")
	private void setupTableColumns() {
        TableColumn<Reservation, String> bookTitleCol = new TableColumn<>("Book Title");
        bookTitleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBookTitle()));
        bookTitleCol.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        TableColumn<Reservation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        statusCol.setStyle("-fx-font-size: 14px;");
        tableView.getColumns().addAll(bookTitleCol, statusCol);
    }
    
    private void cancelReservation() {
        Reservation selectedReservation = tableView.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert("Error", "Please select a reservation to cancel.");
            return;
        }
        reservationsList.remove(selectedReservation);
        saveReservations();
        showAlert("Success", "Reservation for \"" + selectedReservation.getBookTitle() + "\" canceled.");
    }
    
    private void addReservation() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reserve a Book");
        dialog.setHeaderText("Enter the title of the book you wish to reserve:");
        dialog.setContentText("Book Title:");
        dialog.showAndWait().ifPresent(title -> {
            if (title.trim().isEmpty()) {
                showAlert("Error", "Book title cannot be empty.");
            } else {
                Reservation newRes = new Reservation(title.trim(), currentUser.getUsername());
                reservationsList.add(newRes);
                saveReservations();
                showAlert("Success", "Reservation for \"" + title + "\" added.");
            }
        });
    }
    
    @SuppressWarnings("unchecked")
	private void loadReservations() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(RESERVATION_FILE))) {
            List<Reservation> loadedReservations = (List<Reservation>) in.readObject();
            for (Reservation res : loadedReservations) {
                if (res.getUsername().equals(currentUser.getUsername())) {
                    reservationsList.add(res);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            reservationsList.clear();
        }
    }
    
    private void saveReservations() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(RESERVATION_FILE))) {
            out.writeObject(new ArrayList<>(reservationsList));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save reservations.");
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

