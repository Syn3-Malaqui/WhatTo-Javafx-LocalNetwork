import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {

    private int badgeCount = 0;

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(400, 450);

        VBox badgeList = new VBox(10);
        AnchorPane.setTopAnchor(badgeList, 20.0);
        AnchorPane.setLeftAnchor(badgeList, 20.0);
        AnchorPane.setRightAnchor(badgeList, 20.0);

        Button addButton = new Button("Add Badge");
        addButton.getStyleClass().add("button");

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

        scene.getStylesheets().add(getClass().getResource("Styling.css").toExternalForm());

        primaryStage.setTitle("WhatTo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createBadge(String text) {
        Label badge = new Label(text);
        badge.setFont(Font.font("Arial", 14));
        badge.getStyleClass().add("badge");
        badge.setMaxWidth(Double.MAX_VALUE);
        return badge;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
