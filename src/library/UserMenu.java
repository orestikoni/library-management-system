package library;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UserMenu extends Application {
    private User loggedInUser;
    // These variables hold the current search filter settings.
    private String currentFilterType = "Title"; // default filter type
    private String currentGenre = "Novel";        // default genre (if Genre filter is used)

    public User getLoggedInUser() {
        return loggedInUser;
    }

    // Constructor accepting a logged-in User
    public UserMenu(User user) {
        this.loggedInUser = user;
    }

    @Override
    public void start(Stage primaryStage) {
        // --- Create the top search bar ---
        TextField searchField = new TextField();
        searchField.setPromptText("Search for books...");
        searchField.setPrefWidth(400);

        Button searchButton = new Button("🔍 Search");
        searchButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");

        Button filterButton = new Button("⚙️ Filter");
        filterButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");

        HBox searchBar = new HBox(10, searchField, searchButton, filterButton);
        searchBar.setPadding(new Insets(10));
        searchBar.setAlignment(Pos.CENTER);
        searchBar.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10px;");

        // --- Side Menu Buttons ---
        VBox menuButtons = new VBox(15);
        menuButtons.setPadding(new Insets(20));
        menuButtons.setAlignment(Pos.TOP_CENTER);

        String buttonStyle = "-fx-background-color: deepskyblue; -fx-text-fill: white; "
                + "-fx-font-size: 14px; -fx-pref-width: 200px; -fx-pref-height: 40px; -fx-background-radius: 8px;";

        Button viewAvailableBooksButton = new Button("📚 View Available Books");
        Button checkBookAvailabilityButton = new Button("🔎 Check Book Availability");
        Button borrowBooksButton = new Button("📥 Borrow Books");
        Button returnBooksButton = new Button("🔄 Return Books");
        Button viewBorrowedHistoryButton = new Button("📖 View Borrowed History");
        Button bookReservationsButton = new Button("📅 Book Reservations");
        Button profileManagementButton = new Button("👤 Profile Management");
        Button bookReviewsButton = new Button("⭐ Book Reviews & Ratings");
        Button suggestBooksButton = new Button("💡 Suggest Books");
        Button notificationsButton = new Button("🔔 Notifications");
        Button logoutButton = new Button("🚪 Logout");

        for (Button btn : new Button[]{viewAvailableBooksButton, checkBookAvailabilityButton, borrowBooksButton,
                returnBooksButton, viewBorrowedHistoryButton, bookReservationsButton, profileManagementButton,
                bookReviewsButton, notificationsButton, suggestBooksButton, logoutButton}) {
            btn.setStyle(buttonStyle);
        }
        logoutButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

        menuButtons.getChildren().addAll(viewAvailableBooksButton, checkBookAvailabilityButton, borrowBooksButton,
                returnBooksButton, viewBorrowedHistoryButton, bookReservationsButton, profileManagementButton,
                bookReviewsButton, notificationsButton, suggestBooksButton, logoutButton);

        // --- Main layout ---
        BorderPane root = new BorderPane();
        root.setTop(searchBar);
        root.setLeft(menuButtons);
        root.setStyle("-fx-background-color: #f0f0f0;");

        // --- Button Actions ---

        // Show available books
        viewAvailableBooksButton.setOnAction(e -> {
            ViewAvailableBooks viewBooks = new ViewAvailableBooks();
            root.setCenter(viewBooks.getView());
        });

        // Borrow Books view
        borrowBooksButton.setOnAction(e -> {
            BorrowBooks borrowBooksPane = new BorrowBooks(loggedInUser);
            root.setCenter(borrowBooksPane.getView());
        });

        // Borrowed Books History view
        viewBorrowedHistoryButton.setOnAction(e -> {
            ViewBorrowedBooksHistory historyView = new ViewBorrowedBooksHistory(loggedInUser.getUsername());
            root.setCenter(historyView.getView());
        });

        // --- Filter Button: Open a small modal to choose filter criteria ---
        filterButton.setOnAction(e -> {
            Stage filterStage = new Stage();
            filterStage.initModality(Modality.APPLICATION_MODAL);
            filterStage.setTitle("Select Filter Criteria");

            ToggleGroup filterGroup = new ToggleGroup();
            RadioButton titleButton = new RadioButton("Title");
            titleButton.setToggleGroup(filterGroup);
            RadioButton authorButton = new RadioButton("Author");
            authorButton.setToggleGroup(filterGroup);
            RadioButton genreButton = new RadioButton("Genre");
            genreButton.setToggleGroup(filterGroup);

            // Set the radio selection based on the current filter.
            if (currentFilterType.equals("Title")) {
                titleButton.setSelected(true);
            } else if (currentFilterType.equals("Author")) {
                authorButton.setSelected(true);
            } else if (currentFilterType.equals("Genre")) {
                genreButton.setSelected(true);
            }

            // When Genre is chosen, allow picking one from a preset list.
            ComboBox<String> genreComboBox = new ComboBox<>();
            genreComboBox.getItems().addAll("Novel", "Science-fiction", "Thriller", "Mystery", "Romance", "Non-Fiction", "Fantasy");
            genreComboBox.setValue(currentGenre);
            genreComboBox.setDisable(!currentFilterType.equals("Genre"));

            // Listeners to enable/disable the genre selection as needed.
            titleButton.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    genreComboBox.setDisable(true);
                }
            });
            authorButton.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    genreComboBox.setDisable(true);
                }
            });
            genreButton.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    genreComboBox.setDisable(false);
                }
            });

            Button applyButton = new Button("Apply Filter");
            applyButton.setOnAction(ev -> {
                if (titleButton.isSelected()) {
                    currentFilterType = "Title";
                } else if (authorButton.isSelected()) {
                    currentFilterType = "Author";
                } else if (genreButton.isSelected()) {
                    currentFilterType = "Genre";
                    currentGenre = genreComboBox.getValue();
                }
                filterStage.close();
            });

            VBox filterLayout = new VBox(10, titleButton, authorButton, genreButton, genreComboBox, applyButton);
            filterLayout.setAlignment(Pos.CENTER);
            filterLayout.setPadding(new Insets(10));

            Scene filterScene = new Scene(filterLayout, 300, 250);
            filterStage.setScene(filterScene);
            filterStage.showAndWait();
        });

        // --- Search Button: When clicked, create and show a SearchBooksView ---
        searchButton.setOnAction(e -> {
            String query = searchField.getText().trim();
            // Create a search view using the current filter settings.
            SearchBooksView searchBooksView = new SearchBooksView(query, currentFilterType, currentGenre);
            root.setCenter(searchBooksView.getView());
        });
        
        bookReservationsButton.setOnAction(e -> {
            // Create a new BooksReservations pane and set it into the center of the root layout.
            BooksReservations reservationsPane = new BooksReservations(loggedInUser);
            root.setCenter(reservationsPane.getView());
        });

        // Set the action for the Return Books button
        returnBooksButton.setOnAction(e -> {
            // Create a new ReturnBooks pane and set it into the center of the root layout.
            ReturnBooks returnBooksPane = new ReturnBooks(loggedInUser);
            root.setCenter(returnBooksPane.getView());
        });
        
        profileManagementButton.setOnAction(e -> {
            ProfileManagement profilePane = new ProfileManagement(loggedInUser);
            root.setCenter(profilePane.getView());
        });

        bookReviewsButton.setOnAction(e -> {
            BookReviewsAndRatings reviewsPane = new BookReviewsAndRatings(loggedInUser);
            root.setCenter(reviewsPane.getView());
        });

        checkBookAvailabilityButton.setOnAction(e -> {
            CheckBookAvailability availabilityPane = new CheckBookAvailability();
            root.setCenter(availabilityPane.getView());
        });
        
        logoutButton.setOnAction(e -> {
            // Pass the primary stage to the logout transition.
            LogoutTransition.performLogout(primaryStage);
        });
        
        suggestBooksButton.setOnAction(e -> {
            SuggestBooks suggestPane = new SuggestBooks(loggedInUser);
            root.setCenter(suggestPane.getView());
        });
        logoutButton.setOnAction(e -> LogoutTransition.performLogout(primaryStage));
        
        notificationsButton.setOnAction(e -> {
            UserNotifications notifPane = new UserNotifications(loggedInUser.getUsername());
            root.setCenter(notifPane.getView());
        });
        
        viewAvailableBooksButton.setOnAction(e -> {
            ViewAvailableBooks vab = new ViewAvailableBooks();
            root.setCenter(vab.getView());
        });
        

        // --- Final Stage Setup ---
        primaryStage.setTitle("User Menu");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}

