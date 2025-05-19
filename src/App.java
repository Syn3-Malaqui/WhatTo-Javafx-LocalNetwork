import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {

    private int badgeCount = 0;

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(600, 300);

        VBox badgeList = new VBox(10);
        badgeList.setLayoutX(20);
        badgeList.setLayoutY(20);

        Button addButton = new Button("Add Badge");
        addButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> {
            badgeCount++;
            Label badge = createBadge("Task " + badgeCount);
            badgeList.getChildren().add(badge);
        });

        // Anchor the button to bottom right
        AnchorPane.setBottomAnchor(addButton, 20.0);
        AnchorPane.setRightAnchor(addButton, 20.0);

        root.getChildren().addAll(badgeList, addButton);

        Scene scene = new Scene(root);
        primaryStage.setTitle("To-Do Badge List");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createBadge(String text) {
        Label badge = new Label(text);
        badge.setFont(Font.font("Arial", 14));
        badge.setStyle("-fx-background-color: #FFEB3B; -fx-padding: 8 12 8 12; -fx-background-radius: 8;");
        return badge;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
