package library;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;

public class ChangeAdminCredentials {

    private static final String ADMIN_FILE = "admin.dat";
    private String adminUsername;
    private String adminPassword;
    
    public ChangeAdminCredentials() {
        loadAdminCredentials();
    }
    
    public Node getView() {
        // Root container with padding, spacing, and a subtle background color
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f6f7; -fx-border-color: #bdc3c7; -fx-border-width: 1px;");

        // Header label with custom font size, weight, and color
        Label header = new Label("Change Admin Credentials");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        // Username TextField with preset width and rounded corners
        TextField usernameField = new TextField(adminUsername);
        usernameField.setPrefWidth(300);
        usernameField.setStyle("-fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: #95a5a6;");

        // PasswordField with prompt text and similar styling as usernameField
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter new password");
        passwordField.setPrefWidth(300);
        passwordField.setStyle("-fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: #95a5a6;");

        // Submit button styled with a bold font and rounded corners
        Button submitButton = new Button("Change Credentials");
        submitButton.setPrefWidth(300);
        submitButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px;");
        submitButton.setOnAction(e -> {
            String newUsername = usernameField.getText().trim();
            String newPassword = passwordField.getText().trim();
            if (newUsername.isEmpty() || newPassword.isEmpty()) {
                showAlert("Error", "Username and password cannot be empty.");
                return;
            }
            adminUsername = newUsername;
            adminPassword = newPassword;
            saveAdminCredentials();
            showAlert("Success", "Admin credentials updated.");
        });
        
        // VBox to group form labels and fields together with spacing
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.getChildren().addAll(
            new Label("New Username:"), usernameField,
            new Label("New Password:"), passwordField,
            submitButton
        );
        
        // Optionally style the labels inside formBox
        for (Node node : formBox.getChildren()) {
            if (node instanceof Label) {
                node.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50;");
            }
        }
        
        root.getChildren().addAll(header, formBox);
        return root;
    }
    
    // Loads the admin credentials from a text file.
    // The first line is the username and the second line is the password.
    private void loadAdminCredentials() {
        File file = new File(ADMIN_FILE);
        if (!file.exists()) {
            // Set default credentials if the file does not exist.
            adminUsername = "admin";
            adminPassword = "admin123";
            saveAdminCredentials();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String username = reader.readLine();
            String password = reader.readLine();
            if (username == null || password == null) {
                // If file is malformed, reset to defaults.
                adminUsername = "admin";
                adminPassword = "admin123";
            } else {
                adminUsername = username;
                adminPassword = password;
            }
        } catch (IOException e) {
            // If there is an error, fall back to default credentials.
            adminUsername = "admin";
            adminPassword = "admin123";
        }
    }
    
    // Saves the admin credentials to a text file.
    // The username is written on the first line and the password on the second.
    private void saveAdminCredentials() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ADMIN_FILE))) {
            writer.println(adminUsername);
            writer.println(adminPassword);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save admin credentials.");
        }
    }
    
    // Displays an alert dialog with a given title and message.
    private void showAlert(String title, String message) {
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle(title);
         alert.setHeaderText(null);
         alert.setContentText(message);
         alert.showAndWait();
    }
}
