package library;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SuggestBooks {

    private static final String SUGGESTIONS_FILE = "suggestions.dat";
    private User currentUser;

    public SuggestBooks(User currentUser) {
        this.currentUser = currentUser;
    }

    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #fce4ec, #ffffff);");
        
        Label header = new Label("Suggest Books");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #c2185b;");
        
        TextField suggestionField = new TextField();
        suggestionField.setPromptText("Enter book title or author name...");
        suggestionField.setStyle("-fx-font-size: 16px; -fx-padding: 8px; -fx-max-width: 300px;");
        
        Button submitButton = new Button("Submit Suggestion");
        submitButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #ff4081, #f50057);" +
                              " -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        submitButton.setOnAction(e -> {
            String suggestionText = suggestionField.getText().trim();
            if (suggestionText.isEmpty()) {
                showAlert("Error", "Please enter a suggestion.");
                return;
            }
            Suggestion newSuggestion = new Suggestion(currentUser.getUsername(), suggestionText);
            saveSuggestion(newSuggestion);
            // Also add a notification for admin.
            AdminNotifications.addNotification("New suggestion from " + currentUser.getUsername() + ": " + suggestionText);
            showAlert("Success", "Your suggestion has been submitted.");
            suggestionField.clear();
        });
        
        root.getChildren().addAll(header, suggestionField, submitButton);
        return root;
    }
    
    @SuppressWarnings("unchecked")
	private void saveSuggestion(Suggestion suggestion) {
        List<Suggestion> suggestions = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SUGGESTIONS_FILE))) {
            suggestions = (List<Suggestion>) in.readObject();
        } catch (Exception e) {
            // File may not exist; start with empty list.
        }
        suggestions.add(suggestion);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SUGGESTIONS_FILE))) {
            out.writeObject(suggestions);
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
    
    public static class Suggestion implements Serializable {
        private static final long serialVersionUID = 1L;
        private String username;
        private String suggestionText;
        
        public Suggestion(String username, String suggestionText) {
            this.username = username;
            this.suggestionText = suggestionText;
        }
        
        public String getUsername() { return username; }
        public String getSuggestionText() { return suggestionText; }
    }
}
