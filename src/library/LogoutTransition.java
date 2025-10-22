package library;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LogoutTransition {

    public static void performLogout(Stage stage) {
        // Creating a simple loading layout with a progress indicator and "Logging out..." label.
        VBox loadingPane = new VBox(20);
        loadingPane.setAlignment(Pos.CENTER);
        ProgressIndicator indicator = new ProgressIndicator();
        Label label = new Label("Logging out...");
        loadingPane.getChildren().addAll(indicator, label);

        // Set and show the scene.
        Scene loadingScene = new Scene(loadingPane, 600, 400);
        stage.setScene(loadingScene);
        stage.show();

        // After 2 seconds, redirect to the LoginMenu.
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            try {
                new LoginMenu().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }
}
