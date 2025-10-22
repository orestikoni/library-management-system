package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ViewReservedBooks {
    private ObservableList<Reservation> reservationsList;
    private TableView<Reservation> tableView;
    private static final String RESERVATION_FILE = "reservations.ser";

    public ViewReservedBooks() {
        reservationsList = FXCollections.observableArrayList();
        tableView = new TableView<>();
        loadReservations();
        tableView.setItems(reservationsList);
        setupTableColumns();
    }

    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #fff3e0, #ffffff);");
        
        Label header = new Label("Reserved Books Management");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ef6c00;");
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button notifyButton = new Button("Notify User");
        notifyButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #42a5f5, #1e88e5);" +
                               " -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        Button removeButton = new Button("Remove Reservation");
        removeButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #ef5350, #e53935);" +
                               " -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        notifyButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        removeButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        
        notifyButton.setOnAction(e -> notifyUser());
        removeButton.setOnAction(e -> removeReservation());
        
        buttonBox.getChildren().addAll(notifyButton, removeButton);
        
        root.getChildren().addAll(header, tableView, buttonBox);
        return root;
    }

    @SuppressWarnings("unchecked")
	private void setupTableColumns() {
        TableColumn<Reservation, String> bookTitleCol = new TableColumn<>("Book Title");
        bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        bookTitleCol.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TableColumn<Reservation, String> usernameCol = new TableColumn<>("Reserved By");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setStyle("-fx-font-size: 16px;");
        
        TableColumn<Reservation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setStyle("-fx-font-size: 16px;");
        
        tableView.getColumns().addAll(bookTitleCol, usernameCol, statusCol);
    }

    private void notifyUser() {
        Reservation selectedReservation = tableView.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert("Error", "Please select a reservation to notify.");
            return;
        }
        selectedReservation.setStatus("Available - Notified");
        tableView.refresh();
        saveReservations();
        showAlert("Success", "User " + selectedReservation.getUsername() + " has been notified.");
    }

    private void removeReservation() {
        Reservation selectedReservation = tableView.getSelectionModel().getSelectedItem();
        if (selectedReservation == null) {
            showAlert("Error", "Please select a reservation to remove.");
            return;
        }
        reservationsList.remove(selectedReservation);
        saveReservations();
        showAlert("Success", "The reservation for \"" + selectedReservation.getBookTitle() + "\" has been removed.");
    }

    @SuppressWarnings("unchecked")
	private void loadReservations() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(RESERVATION_FILE))) {
            List<Reservation> loadedReservations = (List<Reservation>) in.readObject();
            reservationsList.setAll(loadedReservations);
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

