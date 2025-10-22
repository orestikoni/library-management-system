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

public class AdminNotifications {

    private ObservableList<Notification> notificationsList;
    private TableView<Notification> tableView;
    public static final String NOTIFICATIONS_FILE = "adminNotifications.dat";

    public AdminNotifications() {
        notificationsList = FXCollections.observableArrayList();
        tableView = new TableView<>();
        loadNotifications();
        tableView.setItems(notificationsList);
        setupTableColumns();
    }

    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1px;");
        
        Label header = new Label("Admin Notifications");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextArea notifArea = new TextArea();
        notifArea.setPromptText("Enter notification message here...");
        notifArea.setWrapText(true);
        notifArea.setStyle("-fx-background-radius: 5px; -fx-border-radius: 5px;");
        
        Button sendButton = new Button("Send Notification");
        sendButton.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-background-radius: 5px;");
        sendButton.setOnAction(e -> {
            String message = notifArea.getText().trim();
            if (message.isEmpty()) {
                showAlert("Error", "Please enter a notification message.");
                return;
            }
            Notification newNotif = new Notification(message);
            notificationsList.add(newNotif);
            saveNotifications();
            showAlert("Success", "Notification sent.");
            notifArea.clear();
        });

        root.getChildren().addAll(header,
            new Label("Send New Notification:"), notifArea,
            sendButton,
            new Label("Sent Notifications:"), tableView);
        return root;
    }

    @SuppressWarnings("unchecked")
	private void setupTableColumns() {
        TableColumn<Notification, String> messageCol = new TableColumn<>("Message");
        messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageCol.setPrefWidth(300);
        TableColumn<Notification, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);
        tableView.getColumns().addAll(messageCol, dateCol);
    }

    @SuppressWarnings("unchecked")
	private void loadNotifications() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(NOTIFICATIONS_FILE))) {
            List<Notification> loadedNotifs = (List<Notification>) in.readObject();
            notificationsList.setAll(loadedNotifs);
        } catch (IOException | ClassNotFoundException e) {
            notificationsList.clear();
        }
    }

    private void saveNotifications() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(NOTIFICATIONS_FILE))) {
            out.writeObject(new ArrayList<>(notificationsList));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save notifications.");
        }
    }

    @SuppressWarnings("unchecked")
	public static void addNotification(String message) {
        List<Notification> notifs = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(NOTIFICATIONS_FILE))) {
            notifs = (List<Notification>) in.readObject();
        } catch (Exception e) {
            notifs = new ArrayList<>();
        }
        notifs.add(new Notification(message));
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(NOTIFICATIONS_FILE))) {
            out.writeObject(notifs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle(title);
         alert.setHeaderText(null);
         alert.setContentText(message);
         alert.showAndWait();
    }

    // Inner helper class for notifications.
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

