import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;
import javafx.scene.control.ScrollPane;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

public class App extends Application {

    private ScrollPane scrollPane;
    private VBox contentArea;
    private static final double BLOCK_SPACING = 20.0;

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(400, 550);

        contentArea = new VBox(BLOCK_SPACING);
        contentArea.setPadding(new javafx.geometry.Insets(20));
        
        scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);

        // Add initial "What To..." block
        addInitialBlock();

        root.getChildren().add(scrollPane);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Styling.css").toExternalForm());

        primaryStage.setTitle("WhatTo");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void addInitialBlock() {
        VBox block = createInitialBlock();
        contentArea.getChildren().add(block);
    }

    private VBox createInitialBlock() {
        VBox block = new VBox(5);
        block.setMaxWidth(Double.MAX_VALUE);
        block.getStyleClass().add("writing-block");

        TextField titleField = new TextField("What To...");
        titleField.setFont(Font.font("Arial", 16));
        titleField.getStyleClass().add("title-field");
        titleField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        // Create a hidden TextArea for input
        TextArea hiddenEditor = new TextArea();
        hiddenEditor.setWrapText(true);
        hiddenEditor.setPrefRowCount(1);
        hiddenEditor.setMaxHeight(Double.MAX_VALUE);
        hiddenEditor.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-opacity: 0; -fx-pref-height: 0; -fx-max-height: 0; -fx-min-height: 0;");
        hiddenEditor.getStyleClass().add("writing-area");
        hiddenEditor.setFont(Font.font("Arial", 14));
        hiddenEditor.setVisible(false);
        hiddenEditor.setManaged(false);

        VBox renderedBody = new VBox(0);
        renderedBody.setFillWidth(true);
        renderedBody.setAlignment(Pos.TOP_LEFT);
        renderedBody.setStyle("-fx-background-color: transparent;");
        renderedBody.setMinHeight(24);
        renderedBody.setMaxWidth(Double.MAX_VALUE);
        renderedBody.setPrefWidth(Region.USE_COMPUTED_SIZE);
        renderedBody.setOnMouseClicked(e -> hiddenEditor.requestFocus());

        // Show renderedBody when body is active
        renderedBody.setVisible(false);
        renderedBody.setManaged(false);

        // Show/hide renderedBody and hiddenEditor on Enter in title
        titleField.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER && !titleField.getText().equals("What To...")) {
                e.consume();
                renderedBody.setVisible(true);
                renderedBody.setManaged(true);
                hiddenEditor.setVisible(true);
                hiddenEditor.setManaged(true);
                hiddenEditor.requestFocus();
            }
        });

        // Mirror hiddenEditor text and caret to renderedBody
        ChangeListener<Object> updateRendered = (obs, oldVal, newVal) -> {
            updateRenderedBodyWithSelection(hiddenEditor, renderedBody, hiddenEditor.getSelection().getStart(), hiddenEditor.getSelection().getEnd());
        };
        hiddenEditor.textProperty().addListener(updateRendered);
        hiddenEditor.caretPositionProperty().addListener(updateRendered);
        hiddenEditor.selectionProperty().addListener(updateRendered);

        // Initial update
        updateRenderedBodyWithSelection(hiddenEditor, renderedBody, hiddenEditor.getSelection().getStart(), hiddenEditor.getSelection().getEnd());

        // Selection state for rendered view
        final int[] dragStartChar = { -1 };
        final int[] dragEndChar = { -1 };

        renderedBody.setOnMousePressed(e -> {
            int charPos = getCharPosFromMouse(renderedBody, e.getX(), e.getY());
            dragStartChar[0] = charPos;
            dragEndChar[0] = charPos;
            hiddenEditor.positionCaret(charPos);
            hiddenEditor.deselect();
            updateRenderedBodyWithSelection(hiddenEditor, renderedBody, dragStartChar[0], dragEndChar[0]);
            hiddenEditor.requestFocus();
        });
        renderedBody.setOnMouseDragged(e -> {
            int charPos = getCharPosFromMouse(renderedBody, e.getX(), e.getY());
            dragEndChar[0] = charPos;
            int selStart = Math.min(dragStartChar[0], dragEndChar[0]);
            int selEnd = Math.max(dragStartChar[0], dragEndChar[0]);
            hiddenEditor.selectRange(selStart, selEnd);
            updateRenderedBodyWithSelection(hiddenEditor, renderedBody, selStart, selEnd);
        });
        renderedBody.setOnMouseReleased(e -> {
            int charPos = getCharPosFromMouse(renderedBody, e.getX(), e.getY());
            dragEndChar[0] = charPos;
            int selStart = Math.min(dragStartChar[0], dragEndChar[0]);
            int selEnd = Math.max(dragStartChar[0], dragEndChar[0]);
            hiddenEditor.selectRange(selStart, selEnd);
            updateRenderedBodyWithSelection(hiddenEditor, renderedBody, selStart, selEnd);
        });

        // Update rendered view on selection change
        hiddenEditor.selectionProperty().addListener((obs, oldVal, newVal) -> {
            updateRenderedBodyWithSelection(hiddenEditor, renderedBody, newVal.getStart(), newVal.getEnd());
        });

        // Keyboard focus: always keep hiddenEditor focused when renderedBody is visible
        renderedBody.setOnMousePressed(e -> hiddenEditor.requestFocus());

        block.getChildren().addAll(titleField, hiddenEditor, renderedBody);

        // Hover effect
        DropShadow blockShadow = new DropShadow(10, Color.web("#10B981", 0.18));
        block.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            block.setEffect(blockShadow);
            block.setStyle("-fx-background-color: #A7F3D0; -fx-background-radius: 5;");
        });
        block.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            block.setEffect(null);
            block.setStyle("");
        });

        return block;
    }

    // Helper: get character position from mouse coordinates
    private int getCharPosFromMouse(VBox renderedBody, double mouseX, double mouseY) {
        int charCount = 0;
        double y = 0;
        for (Node row : renderedBody.getChildren()) {
            if (!(row instanceof HBox)) continue;
            HBox hbox = (HBox) row;
            double rowHeight = hbox.getHeight();
            if (mouseY >= y && mouseY < y + rowHeight) {
                double x = 0;
                for (Node n : hbox.getChildren()) {
                    if (n instanceof Label) {
                        Label l = (Label) n;
                        double w = l.getWidth();
                        if (mouseX >= x && mouseX < x + w) {
                            return charCount;
                        }
                        charCount++;
                        x += w;
                    } else if (n instanceof Circle) {
                        x += ((Circle) n).getRadius() * 2 + 6;
                    }
                }
                return charCount;
            }
            y += rowHeight;
            // Count chars in this row
            for (Node n : hbox.getChildren()) {
                if (n instanceof Label) charCount++;
                else if (n instanceof Circle) ;
            }
            charCount++; // for newline
        }
        return charCount;
    }

    // Render with selection highlighting
    private void updateRenderedBodyWithSelection(TextArea hiddenEditor, VBox renderedBody, int selStart, int selEnd) {
        renderedBody.getChildren().clear();
        String text = hiddenEditor.getText();
        int caret = hiddenEditor.getCaretPosition();
        String[] lines = text.split("\n", -1);
        int charCount = 0;
        for (int i = 0; i < lines.length; i++) {
            final int lineIndex = i;
            String line = lines[i];
            boolean isChecklist = line.trim().matches("- \\[([ xX])\\] .*");
            if (isChecklist) {
                boolean checked = line.trim().startsWith("- [x]") || line.trim().startsWith("- [X]");
                String itemText = line.trim().substring(6);
                HBox row = new HBox(6);
                row.setAlignment(Pos.CENTER_LEFT);
                Circle circle = new Circle(8);
                circle.setFill(checked ? Color.web("#3B82F6") : Color.web("#9CA3AF"));
                circle.setStroke(Color.web("#9CA3AF"));
                circle.setStrokeWidth(1);
                circle.setOnMouseClicked(e -> {
                    String[] allLines = hiddenEditor.getText().split("\n", -1);
                    String currentLine = allLines[lineIndex];
                    boolean currentlyChecked = currentLine.trim().startsWith("- [x]") || currentLine.trim().startsWith("- [X]");
                    String currentItemText = currentLine.trim().substring(6);
                    allLines[lineIndex] = "- [" + (currentlyChecked ? " " : "x") + "] " + currentItemText;
                    hiddenEditor.setText(String.join("\n", allLines));
                    hiddenEditor.positionCaret(caret);
                });
                circle.getStyleClass().add("checklist-circle");
                row.getChildren().add(circle);
                // Use TextFlow for the text part
                TextFlow textFlow = new TextFlow();
                for (int j = 0; j <= itemText.length(); j++) {
                    boolean inSel = (charCount + 6 + j >= selStart && charCount + 6 + j < selEnd);
                    if (charCount + 6 + j == caret) {
                        Text caretText = new Text("|");
                        caretText.setFill(Color.web("#3B82F6"));
                        caretText.setFont(Font.font("Arial", 14));
                        textFlow.getChildren().add(caretText);
                    }
                    if (j < itemText.length()) {
                        Text charText = new Text(String.valueOf(itemText.charAt(j)));
                        charText.setFont(Font.font("Arial", 14));
                        if (inSel) charText.setStyle("-fx-background-color: #BEE3F8;");
                        textFlow.getChildren().add(charText);
                    }
                }
                row.getChildren().add(textFlow);
                renderedBody.getChildren().add(row);
            } else {
                // Use TextFlow for normal lines
                TextFlow textFlow = new TextFlow();
                for (int j = 0; j <= line.length(); j++) {
                    boolean inSel = (charCount + j >= selStart && charCount + j < selEnd);
                    if (charCount + j == caret) {
                        Text caretText = new Text("|");
                        caretText.setFill(Color.web("#3B82F6"));
                        caretText.setFont(Font.font("Arial", 14));
                        textFlow.getChildren().add(caretText);
                    }
                    if (j < line.length()) {
                        Text charText = new Text(String.valueOf(line.charAt(j)));
                        charText.setFont(Font.font("Arial", 14));
                        if (inSel) charText.setStyle("-fx-background-color: #BEE3F8;");
                        textFlow.getChildren().add(charText);
                    }
                }
                renderedBody.getChildren().add(textFlow);
            }
            charCount += line.length() + 1;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}