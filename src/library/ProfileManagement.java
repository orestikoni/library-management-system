package library;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileManagement {

    private User loggedInUser;
    private static final String USER_FILE = "users.ser";

    public ProfileManagement(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e1f5fe, #ffffff);");
        
        Label header = new Label("Your Personal Information");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0277bd;");
        
        Label nameLabel = new Label("Name: " + loggedInUser.getName());
        Label surnameLabel = new Label("Surname: " + loggedInUser.getSurname());
        Label phoneLabel = new Label("Phone: " + loggedInUser.getPhone());
        Label usernameLabel = new Label("Username: " + loggedInUser.getUsername());
        
        nameLabel.setStyle("-fx-font-size: 16px;");
        surnameLabel.setStyle("-fx-font-size: 16px;");
        phoneLabel.setStyle("-fx-font-size: 16px;");
        usernameLabel.setStyle("-fx-font-size: 16px;");
        
        Button changeButton = new Button("Change Personal Information");
        changeButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #ffb74d, #ff9800); -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        changeButton.setOnAction(e -> showChangeInfoDialog());
        
        root.getChildren().addAll(header, nameLabel, surnameLabel, phoneLabel, usernameLabel, changeButton);
        return root;
    }

    private void showChangeInfoDialog() {
        // Verify current password dialog.
        Stage verifyStage = new Stage();
        VBox verifyRoot = new VBox(15);
        verifyRoot.setPadding(new Insets(20));
        verifyRoot.setAlignment(Pos.CENTER);
        verifyRoot.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10px;");
        
        Label verifyLabel = new Label("Enter your current password:");
        verifyLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
        Button verifyButton = new Button("Verify");
        verifyButton.setStyle("-fx-font-size: 16px; -fx-background-color: #42a5f5; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        
        verifyRoot.getChildren().addAll(verifyLabel, passwordField, verifyButton);
        Scene verifyScene = new Scene(verifyRoot, 350, 200);
        verifyStage.setScene(verifyScene);
        verifyStage.setTitle("Verify Password");
        verifyStage.show();
        
        verifyButton.setOnAction(e -> {
            String inputPass = passwordField.getText();
            if (!inputPass.equals(loggedInUser.getPassword())) {
                showAlert("Error", "Incorrect password.");
            } else {
                verifyStage.close();
                showUpdateForm();
            }
        });
    }

    @SuppressWarnings("unchecked")
	private void showUpdateForm() {
        Stage updateStage = new Stage();
        VBox updateRoot = new VBox(15);
        updateRoot.setPadding(new Insets(20));
        updateRoot.setAlignment(Pos.CENTER_LEFT);
        updateRoot.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10px;");
        
        Label header = new Label("Update Personal Information");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #00838f;");
        
        TextField nameField = new TextField(loggedInUser.getName());
        TextField surnameField = new TextField(loggedInUser.getSurname());
        TextField phoneField = new TextField(loggedInUser.getPhone());
        TextField usernameField = new TextField(loggedInUser.getUsername());
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New password (leave blank to keep current)");
        
        nameField.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
        surnameField.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
        phoneField.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
        usernameField.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
        passwordField.setStyle("-fx-font-size: 16px; -fx-padding: 5px;");
        
        Button submitButton = new Button("Submit Changes");
        submitButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #66bb6a, #43a047); " +
                              "-fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        
        VBox formBox = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Surname:"), surnameField,
                new Label("Phone:"), phoneField,
                new Label("Username:"), usernameField,
                new Label("New Password:"), passwordField,
                submitButton);
        formBox.setPadding(new Insets(10));
        
        updateRoot.getChildren().addAll(header, formBox);
        Scene updateScene = new Scene(updateRoot, 400, 450);
        updateStage.setScene(updateScene);
        updateStage.setTitle("Update Personal Information");
        updateStage.show();
        
        submitButton.setOnAction(e -> {
            String oldUsername = loggedInUser.getUsername();
            loggedInUser.setName(nameField.getText());
            loggedInUser.setSurname(surnameField.getText());
            loggedInUser.setPhone(phoneField.getText());
            loggedInUser.setUsername(usernameField.getText());
            if (!passwordField.getText().isEmpty()) {
                loggedInUser.setPassword(passwordField.getText());
            }
            List<User> allUsers = new ArrayList<>();
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(USER_FILE))) {
                allUsers = (List<User>) in.readObject();
            } catch (Exception ex) {
                // Start with empty list if file not found.
            }
            for (int i = 0; i < allUsers.size(); i++) {
                User user = allUsers.get(i);
                if (user.getUsername().equals(oldUsername)) {
                    allUsers.set(i, loggedInUser);
                    break;
                }
            }
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
                out.writeObject(allUsers);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            showAlert("Success", "Your personal information has been updated.");
            updateStage.close();
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
