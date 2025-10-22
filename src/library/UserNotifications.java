package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserNotifications {

    private ObservableList<Notification> notificationsList;
    private TableView<Notification> tableView;
    private static final String USER_NOTIF_FILE = "userNotifications.dat";
    @SuppressWarnings("unused")
	private String username;

    public UserNotifications(String username) {
        this.username = username;
        notificationsList = FXCollections.observableArrayList();
        tableView = new TableView<>();
        loadNotifications();
        tableView.setItems(notificationsList);
        setupTableColumns();
    }

    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e8f5e9, #ffffff);");
        
        Label header = new Label("Your Notifications");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        
        root.getChildren().addAll(header, tableView);
        return root;
    }

    @SuppressWarnings("unchecked")
	private void setupTableColumns() {
        TableColumn<Notification, String> messageCol = new TableColumn<>("Message");
        messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageCol.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        TableColumn<Notification, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setStyle("-fx-font-size: 14px;");
        tableView.getColumns().addAll(messageCol, dateCol);
    }

    @SuppressWarnings("unchecked")
	private void loadNotifications() {
        List<Notification> allNotifs = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(USER_NOTIF_FILE))) {
            allNotifs = (List<Notification>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            allNotifs.clear();
        }
        // Let's assume all notifications belong to the user.
        notificationsList.setAll(allNotifs);
    }

    // Inner helper class for user notifications.
    public static class Notification implements Serializable {
        private static final long serialVersionUID = 1L;
        private String message;
        private String date;
        public Notification(String message) {
            this.message = message;
            this.date = java.time.LocalDate.now().toString();
        }
        public String getMessage() { return message; }
        public String getDate() { return date; }
    }
}
