package library;

import javafx.beans.property.SimpleStringProperty;
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

public class UserManagement {

    private ObservableList<User> usersList;
    private TableView<User> tableView;
    private static final String USER_FILE = "users.ser";

    public UserManagement() {
        usersList = FXCollections.observableArrayList();
        tableView = new TableView<>();
        loadUsers();
        tableView.setItems(usersList);
        setupTableColumns();
    }

    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e8f5e9, #ffffff);");
        
        Label header = new Label("User Management");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button suspendButton = new Button("Suspend/Activate User");
        suspendButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #ff8a65, #ff7043);" +
                               " -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        Button removeButton = new Button("Remove User");
        removeButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #e57373, #ef5350);" +
                              " -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        
        suspendButton.setOnAction(e -> toggleSuspendUser());
        removeButton.setOnAction(e -> removeSelectedUser());
        buttonBox.getChildren().addAll(suspendButton, removeButton);
        
        root.getChildren().addAll(header, tableView, buttonBox);
        return root;
    }

    @SuppressWarnings("unchecked")
	private void setupTableColumns() {
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TableColumn<User, String> surnameCol = new TableColumn<>("Surname");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        surnameCol.setStyle("-fx-font-size: 16px;");
        
        TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setStyle("-fx-font-size: 16px;");
        
        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String status = user.isSuspended() ? "Suspended" : "Active";
            return new SimpleStringProperty(status);
        });
        statusCol.setStyle("-fx-font-size: 16px; -fx-font-style: italic;");
        
        tableView.getColumns().addAll(nameCol, surnameCol, phoneCol, usernameCol, statusCol);
    }

    private void toggleSuspendUser() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to suspend/activate.");
            return;
        }
        selectedUser.setSuspended(!selectedUser.isSuspended());
        tableView.refresh();
        saveUsers();
        showAlert("Success", "User " + selectedUser.getUsername() + " is now " + (selectedUser.isSuspended() ? "Suspended" : "Active") + ".");
    }

    private void removeSelectedUser() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to remove.");
            return;
        }
        usersList.remove(selectedUser);
        tableView.refresh();
        saveUsers();
        showAlert("Success", "User " + selectedUser.getUsername() + " has been removed.");
    }

    @SuppressWarnings("unchecked")
	private void loadUsers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            List<User> loadedUsers = (List<User>) in.readObject();
            usersList.setAll(loadedUsers);
        } catch (IOException | ClassNotFoundException e) {
            usersList.clear();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            out.writeObject(new ArrayList<>(usersList));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save users.");
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
