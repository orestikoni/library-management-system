package library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ManageCategoriesAndGenres {

    private ObservableList<String> categories;
    private ListView<String> listView;
    private static final String CATEGORIES_FILE = "categories.dat";
    
    public ManageCategoriesAndGenres() {
        categories = FXCollections.observableArrayList();
        listView = new ListView<>();
        loadCategories();
        listView.setItems(categories);
    }
    
    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1px;");
        
        Label header = new Label("Manage Categories & Genres");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("Add Category");
        Button editButton = new Button("Edit Category");
        Button removeButton = new Button("Remove Category");
        for (Button btn : new Button[]{addButton, editButton, removeButton}) {
            btn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-background-radius: 5px;");
        }
        buttonBox.getChildren().addAll(addButton, editButton, removeButton);
        
        addButton.setOnAction(e -> addCategory());
        editButton.setOnAction(e -> editCategory());
        removeButton.setOnAction(e -> removeCategory());
        
        root.getChildren().addAll(header, listView, buttonBox);
        return root;
    }
    
    private void addCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Enter new category/genre:");
        dialog.setContentText("Category:");
        dialog.showAndWait().ifPresent(cat -> {
            String newCat = cat.trim();
            if (!newCat.isEmpty() && !categories.contains(newCat)) {
                categories.add(newCat);
                saveCategories();
            }
        });
    }
    
    private void editCategory() {
        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a category to edit.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selected);
        dialog.setTitle("Edit Category");
        dialog.setHeaderText("Edit selected category:");
        dialog.setContentText("Category:");
        dialog.showAndWait().ifPresent(newCat -> {
            String newCategory = newCat.trim();
            if (!newCategory.isEmpty()) {
                int index = categories.indexOf(selected);
                categories.set(index, newCategory);
                saveCategories();
            }
        });
    }
    
    private void removeCategory() {
        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a category to remove.");
            return;
        }
        categories.remove(selected);
        saveCategories();
    }
    
    private void loadCategories() {
        File file = new File(CATEGORIES_FILE);
        if (!file.exists()) {
            // If no file, set some defaults.
            categories.setAll("Fiction", "Non-Fiction", "Science", "History", "Biography");
            saveCategories();
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(CATEGORIES_FILE))) {
            @SuppressWarnings("unchecked")
			List<String> list = (List<String>) in.readObject();
            if (list == null || list.isEmpty()) {
                categories.setAll("Fiction", "Non-Fiction", "Science", "History", "Biography");
            } else {
                categories.setAll(list);
            }
        } catch (IOException | ClassNotFoundException e) {
            categories.clear();
        }
    }
    
    private void saveCategories() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(CATEGORIES_FILE))) {
            out.writeObject(new ArrayList<>(categories));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save categories.");
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
