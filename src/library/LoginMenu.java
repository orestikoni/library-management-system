package library;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LoginMenu extends Application {

    private static final String USER_FILE = "users.ser";
    private List<User> users;

    
    private final String userButtonStyle = "-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 10px; " +
            "-fx-background-color: linear-gradient(to bottom, #5a9bd5, #4682B4); " +
            "-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 4, 0, 0, 1);";
    private final String adminButtonStyle = "-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 10px; " +
            "-fx-background-color: linear-gradient(to bottom, #d55a5a, #B22222); " +
            "-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 4, 0, 0, 1);";

    @Override
    public void start(Stage primaryStage) {
        users = loadUsers();

        // --- Loading Screen Setup ---
        StackPane loadingPane = new StackPane();
        // Use a gradient background.
        loadingPane.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEFA, #4682B4);");

        // Load the book image (simulate page flipping).
        Image logoImage = null;
        try {
            logoImage = new Image(getClass().getResource("/library/loading_book.jpg.png").toExternalForm());
        } catch (NullPointerException npe) {
            System.err.println("Error: Resource '/library/loading_book.jpg.png' not found.");
        }
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(100);
        logoImageView.setPreserveRatio(true);

        // Create a progress bar that fills up over 3 seconds.
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);

        Label loadingLabel = new Label("Loading...");
        loadingLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        VBox loadingBox = new VBox(20, logoImageView, progressBar, loadingLabel);
        loadingBox.setAlignment(Pos.CENTER);
        loadingPane.getChildren().add(loadingBox);

        Scene loadingScene = new Scene(loadingPane, 600, 400);
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(loadingScene);
        primaryStage.setResizable(true); // Allow resizing.
        primaryStage.show();

        // Bind the loadingPane's size to the scene so it adjusts when resized.
        loadingPane.prefWidthProperty().bind(loadingScene.widthProperty());
        loadingPane.prefHeightProperty().bind(loadingScene.heightProperty());

        // Animate the progress bar over 3 seconds then switch to login menu.
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(3), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.setOnFinished(e -> showLoginMenu(primaryStage));
        timeline.play();

        // Rotate the logo continuously (simulate page-flipping).
        RotateTransition rotate = new RotateTransition(Duration.seconds(1), logoImageView);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.play();
    }

    private void showLoginMenu(Stage primaryStage) {
        // --- Login Menu Background ---
        Image bgImage = null;
        try {
            bgImage = new Image(getClass().getResource("/library/book_background.jpg").toExternalForm());
        } catch (NullPointerException npe) {
            System.err.println("Error: Resource '/library/book_background.jpg' not found.");
        }
        ImageView background = new ImageView(bgImage);
        background.setPreserveRatio(false);

        StackPane root = new StackPane();
        root.getChildren().add(background);

        // Bind background image dimensions to the root (for resizability).
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
        background.setOpacity(0.8);

        // --- Buttons ---
        Button signUpButton = new Button("Sign Up");
        Button loginButton = new Button("Log In");
        Button adminLoginButton = new Button("Admin Login");

        signUpButton.setStyle(userButtonStyle);
        loginButton.setStyle(userButtonStyle);
        adminLoginButton.setStyle(adminButtonStyle);

        signUpButton.setOnAction(e -> showSignUpForm(primaryStage));
        loginButton.setOnAction(e -> showLoginForm(primaryStage, false));  // Regular user login.
        adminLoginButton.setOnAction(e -> showLoginForm(primaryStage, true)); // Admin login.

        VBox buttonBox = new VBox(20, signUpButton, loginButton, adminLoginButton);
        buttonBox.setAlignment(Pos.CENTER);
        // Move the buttons upward slightly.
        buttonBox.setTranslateY(-30);
        root.getChildren().add(buttonBox);

        Scene menuScene = new Scene(root, 600, 400);
        primaryStage.setScene(menuScene);
    }

    private void showSignUpForm(Stage primaryStage) {
        // --- Sign-Up Background ---
        String imagePath = "/library/signup_background.jpg";
        Image backgroundImage = null;
        try {
            backgroundImage = new Image(getClass().getResource(imagePath).toExternalForm());
        } catch (NullPointerException npe) {
            System.err.println("Error: Resource '" + imagePath + "' not found.");
        }
        ImageView background = new ImageView(backgroundImage);
        background.setPreserveRatio(false);

        StackPane root = new StackPane();
        root.getChildren().add(background);
        // Bind the background image to the container.
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        // --- Form Labels and Fields (labels not bold) ---
        Label nameLabel = new Label("Name");
        nameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        TextField nameField = new TextField();
        nameField.setPromptText("First Name (Capitalized)");

        Label surnameLabel = new Label("Surname");
        surnameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        TextField surnameField = new TextField();
        surnameField.setPromptText("Last Name (Capitalized)");

        Label phoneLabel = new Label("Phone");
        phoneLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone (10 digits)");

        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button backButton = new Button("Back");
        backButton.setStyle(userButtonStyle);
        backButton.setOnAction(e -> showLoginMenu(primaryStage));

        Button submitButton = new Button("Sign Up");
        submitButton.setStyle(userButtonStyle);
        submitButton.setOnAction(e -> {
            String name = nameField.getText();
            String surname = surnameField.getText();
            String phone = phoneField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (!name.matches("[A-Z][a-z]*") || !surname.matches("[A-Z][a-z]*")) {
                showAlert("Error", "Name and Surname must start with a capital letter.");
                return;
            }
            if (!phone.matches("\\d{10}")) {
                showAlert("Error", "Phone number must be 10 digits.");
                return;
            }

            User newUser = new User(name, surname, phone, username, password);
            users.add(newUser);
            saveUsers();

            // Show a logging animation instead of a popup alert.
            showLoggingAnimation(primaryStage, () -> {
                UserMenu userMenu = new UserMenu(newUser);
                try {
                    userMenu.start(primaryStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });

        // Set up the grid for the form. A negative translateX shifts the fields to the left.
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(20));
        grid.setTranslateX(-50);
        grid.setAlignment(Pos.CENTER);
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(surnameLabel, 0, 1);
        grid.add(surnameField, 1, 1);
        grid.add(phoneLabel, 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(usernameLabel, 0, 3);
        grid.add(usernameField, 1, 3);
        grid.add(passwordLabel, 0, 4);
        grid.add(passwordField, 1, 4);
        grid.add(submitButton, 1, 5);
        grid.add(backButton, 1, 6);

        root.getChildren().add(grid);
        Scene signUpScene = new Scene(root, 600, 400);
        primaryStage.setScene(signUpScene);
    }

    private void showLoginForm(Stage primaryStage, boolean isAdmin) {
        // --- Login Form Background ---
        ImageView background = new ImageView(new Image(getClass().getResource("/library/signup_background.jpg").toExternalForm()));
        background.setPreserveRatio(false);

        StackPane root = new StackPane();
        root.getChildren().add(background);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());

        // --- Form Labels and Fields (labels not bold) ---
        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Button backButton = new Button("Back");
        backButton.setStyle(userButtonStyle);
        backButton.setOnAction(e -> showLoginMenu(primaryStage));

        Button loginButton = new Button("Log In");
        loginButton.setStyle(userButtonStyle);
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (isAdmin) {
                if (username.equals("admin") && password.equals("admin123")) {
                    showLoggingAnimation(primaryStage, () -> {
                        User dummyAdmin = new User("Admin", "Admin", "", username, password);
                        AdminMenu adminMenu = new AdminMenu(dummyAdmin);
                        try {
                            adminMenu.start(primaryStage);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    errorLabel.setText("Invalid Admin Credentials!");
                }
            } else {
                boolean valid = false;
                for (User user : users) {
                    if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                        valid = true;
                        showLoggingAnimation(primaryStage, () -> {
                            UserMenu userMenu = new UserMenu(user);
                            try {
                                userMenu.start(primaryStage);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                        break;
                    }
                }
                if (!valid) {
                    errorLabel.setText("Invalid Username or Password!");
                }
            }
        });

        // Grid layout with a slight left-shift.
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(20));
        grid.setTranslateX(-50);
        grid.setAlignment(Pos.CENTER);
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(errorLabel, 1, 3);
        grid.add(backButton, 1, 4);

        root.getChildren().add(grid);
        Scene loginScene = new Scene(root, 600, 400);
        primaryStage.setScene(loginScene);
    }

    /**
     * Displays a "logging in" overlay animation before running the onFinish action.
     */
    private void showLoggingAnimation(Stage primaryStage, Runnable onFinish) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        ProgressIndicator pi = new ProgressIndicator();
        Label loggingLabel = new Label("Logging in...");
        loggingLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        box.getChildren().addAll(pi, loggingLabel);
        overlay.getChildren().add(box);

        // Overlay the current scene.
        Scene currentScene = primaryStage.getScene();
        StackPane newRoot = new StackPane(currentScene.getRoot(), overlay);
        Scene newScene = new Scene(newRoot, currentScene.getWidth(), currentScene.getHeight());
        primaryStage.setScene(newScene);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> onFinish.run());
        pause.play();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @SuppressWarnings("unchecked")
	private List<User> loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


