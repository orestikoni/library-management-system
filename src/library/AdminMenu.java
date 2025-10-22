package library;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminMenu extends Application {
    private User loggedInAdmin;
    public User getLoggedInAdmin() { return loggedInAdmin; }
    
    public AdminMenu(User admin) {
        this.loggedInAdmin = admin;
    }

    @Override
    public void start(Stage primaryStage) {
        // Top search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setPrefWidth(400);
        Button searchButton = new Button("🔍 Search");
        searchButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");

        HBox searchBar = new HBox(10, searchField, searchButton);
        searchBar.setPadding(new Insets(10));
        searchBar.setAlignment(Pos.CENTER);
        searchBar.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10px;");

        // Side menu buttons
        VBox menuButtons = new VBox(15);
        menuButtons.setPadding(new Insets(20));
        menuButtons.setAlignment(Pos.TOP_CENTER);
        
        String buttonStyle = "-fx-background-color: deepskyblue; -fx-text-fill: white; -fx-font-size: 14px; " +
                             "-fx-pref-width: 250px; -fx-pref-height: 40px; -fx-background-radius: 8px;";

        Button manageBooksButton = new Button("📚 Manage Books");
        Button viewBorrowedBooksButton = new Button("📖 View All Borrowed Books");
        Button viewReservedBooksButton = new Button("📅 View Reserved Books");
        Button userManagementButton = new Button("👥 User Management");
        Button generateReportsButton = new Button("📊 Generate Reports");
        Button overdueBooksButton = new Button("⏳ Overdue Book Management");
        Button manageCategoriesButton = new Button("📂 Manage Categories & Genres");
        Button manageAvailabilityButton = new Button("📖 Manage Book Availability");
        Button notificationsButton = new Button("🔔 Notifications");
        Button changeAdminCredentialsButton = new Button("🔐 Change Admin Credentials");
        Button logoutButton = new Button("🚪 Logout");

        for (Button btn : new Button[]{manageBooksButton, viewBorrowedBooksButton, viewReservedBooksButton,
                userManagementButton, generateReportsButton, overdueBooksButton,
                manageCategoriesButton, manageAvailabilityButton, notificationsButton,
                changeAdminCredentialsButton, logoutButton}) {
            btn.setStyle(buttonStyle);
        }

        logoutButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

        menuButtons.getChildren().addAll(manageBooksButton, viewBorrowedBooksButton, viewReservedBooksButton,
                userManagementButton, generateReportsButton, overdueBooksButton,
                manageCategoriesButton, manageAvailabilityButton, notificationsButton,
                changeAdminCredentialsButton, logoutButton);
        
        

        // Layout setup
        BorderPane root = new BorderPane();
        root.setTop(searchBar);
        root.setLeft(menuButtons);
        root.setStyle("-fx-background-color: #f0f0f0;");
        
        manageBooksButton.setOnAction(e -> {
            ManageBooksPane manageBooksPane = new ManageBooksPane();
            root.setCenter(manageBooksPane.getView());  // Correct method is setCenter()
        });
        
        viewBorrowedBooksButton.setOnAction(e -> {
            ViewAllBorrowedBooks viewAll = new ViewAllBorrowedBooks();
            root.setCenter(viewAll.getView());
        });
        
        userManagementButton.setOnAction(e -> {
            UserManagement userManagementPane = new UserManagement();
            root.setCenter(userManagementPane.getView());
        });
        
        viewReservedBooksButton.setOnAction(e -> {
            ViewReservedBooks userManagementPane = new ViewReservedBooks();
            root.setCenter(userManagementPane.getView());
        });
        
        logoutButton.setOnAction(e -> {
            LogoutTransition.performLogout(primaryStage);
        });
        
        generateReportsButton.setOnAction(e -> {
            GenerateReports reportsPane = new GenerateReports();
            root.setCenter(reportsPane.getView());
        });
        
        notificationsButton.setOnAction(e -> {
            AdminNotifications notifPane = new AdminNotifications();
            root.setCenter(notifPane.getView());
        });
        
        overdueBooksButton.setOnAction(e -> {
            OverDueBookManagement overduePane = new OverDueBookManagement();
            root.setCenter(overduePane.getView());
        });
        
        changeAdminCredentialsButton.setOnAction(e -> {
            ChangeAdminCredentials credPane = new ChangeAdminCredentials();
            root.setCenter(credPane.getView());
        });
        
        manageAvailabilityButton.setOnAction(e -> {
            ManageBookAvailability availPane = new ManageBookAvailability();
            root.setCenter(availPane.getView());
        });
        
        manageCategoriesButton.setOnAction(e -> {
            ManageCategoriesAndGenres catPane = new ManageCategoriesAndGenres();
            root.setCenter(catPane.getView());
        });
        

        // Full-screen setup
        primaryStage.setTitle("Admin Menu");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}