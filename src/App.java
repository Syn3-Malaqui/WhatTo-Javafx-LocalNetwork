import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.animation.FillTransition;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;
import javafx.scene.control.ScrollPane;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.animation.TranslateTransition;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class App extends Application {

    private int badgeCount = 0;
    private ScrollPane scrollPane;

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(400, 650);

        VBox badgeList = new VBox(10);
        scrollPane = new ScrollPane(badgeList);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        AnchorPane.setTopAnchor(scrollPane, 10.0);
        AnchorPane.setLeftAnchor(scrollPane, 10.0);
        AnchorPane.setRightAnchor(scrollPane, 10.0);
        AnchorPane.setBottomAnchor(scrollPane, 100.0);

        Button addButton = new Button("Add Badge");
        addButton.getStyleClass().add("button");

        // --- Smooth hover animation for button ---
        DropShadow buttonShadow = new DropShadow(15, Color.web("#34D399", 0.3));
        addButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            addButton.setScaleX(1.0);
            addButton.setScaleY(1.0);
            ScaleTransition st = new ScaleTransition(Duration.millis(250), addButton);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
            addButton.setEffect(buttonShadow);
            addButton.setStyle("-fx-background-color: #34D399; -fx-text-fill: #111827;");
        });
        addButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            addButton.setScaleX(1.1);
            addButton.setScaleY(1.1);
            ScaleTransition st = new ScaleTransition(Duration.millis(250), addButton);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
            addButton.setEffect(null);
            addButton.setStyle("");
        });

        addButton.setOnAction(e -> {
            badgeCount++;
            Node badge = createBadge("Task " + badgeCount, scrollPane);
            badgeList.getChildren().add(0, badge);
            HBox topRow = (HBox) ((VBox) badge).getUserData();
            topRow.setScaleX(1.2);
            topRow.setScaleY(1.2);
            ScaleTransition popIn = new ScaleTransition(Duration.millis(200), topRow);
            popIn.setToX(1.0);
            popIn.setToY(1.0);
            popIn.play();
            Platform.runLater(() -> {
                double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
                double viewportHeight = scrollPane.getViewportBounds().getHeight();
                double pixelScroll = scrollPane.getVvalue() * (contentHeight - viewportHeight);
                double newVvalue = (contentHeight - viewportHeight == 0) ? 0 : pixelScroll / (contentHeight - viewportHeight);
                scrollPane.setVvalue(newVvalue);
            });
        });

        // Anchor the button to bottom right
        AnchorPane.setBottomAnchor(addButton, 20.0);
        AnchorPane.setRightAnchor(addButton, 20.0);

        // Gradient overlays for blur/fade at top and bottom
        Pane topFade = new Pane();
        topFade.setMouseTransparent(true);
        topFade.setPrefHeight(30);
        topFade.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 0% 100%, rgba(255,255,255,0.9), transparent);");
        AnchorPane.setTopAnchor(topFade, 10.0);
        AnchorPane.setLeftAnchor(topFade, 10.0);
        AnchorPane.setRightAnchor(topFade, 10.0);

        Pane bottomFade = new Pane();
        bottomFade.setMouseTransparent(true);
        bottomFade.setPrefHeight(30);
        bottomFade.setStyle("-fx-background-color: linear-gradient(from 0% 100% to 0% 0%, rgba(255,255,255,0.9), transparent);");
        AnchorPane.setBottomAnchor(bottomFade, 100.0);
        AnchorPane.setLeftAnchor(bottomFade, 10.0);
        AnchorPane.setRightAnchor(bottomFade, 10.0);

        root.getChildren().addAll(scrollPane, topFade, bottomFade, addButton);

        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("Styling.css").toExternalForm());

        primaryStage.setTitle("WhatTo");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Node createBadge(String text, ScrollPane scrollPane) {
        // Parse title/body if present (split by first newline)
        final String title;
        final String body;
        int nl = text.indexOf("\n");
        if (nl != -1) {
            title = text.substring(0, nl);
            body = text.substring(nl + 1);
        } else {
            title = text;
            body = "";
        }

        // --- Badge UI with toggle button ---
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 14));
        titleLabel.getStyleClass().add("badge-title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLabel, javafx.scene.layout.Priority.ALWAYS);

        Button toggleBtn = new Button(body.isEmpty() ? "" : "▼");
        toggleBtn.setFocusTraversable(false);
        toggleBtn.getStyleClass().add("badge-toggle-btn");
        toggleBtn.setMinWidth(28);
        toggleBtn.setPrefWidth(28);
        toggleBtn.setMaxWidth(28);
        toggleBtn.setFont(Font.font("Arial", 14));

        // Render body: checklist if lines start with '- [ ]' or '- [x]', else normal text
        VBox bodyBox = new VBox();
        bodyBox.setSpacing(4);
        boolean hasChecklist = false;
        
        // Create badgeBox before the checkbox listeners need to reference it
        VBox badgeBox = new VBox();
        badgeBox.setAlignment(Pos.CENTER_LEFT);
        badgeBox.getStyleClass().add("badge");
        badgeBox.setMaxWidth(Double.MAX_VALUE);
        
        if (!body.isEmpty()) {
            String[] lines = body.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (line.trim().matches("- \\[([ xX])\\] .*")) {  // Proper regex for "- [ ] " or "- [x] "
                    hasChecklist = true;
                    boolean checked = line.trim().startsWith("- [x]") || line.trim().startsWith("- [X]");
                    
                    // Extract all text after the checkbox
                    String itemText = line.trim().substring(6); // Skip "- [x] " or "- [ ] "
                    
                    CheckBox cb = new CheckBox(itemText);
                    cb.setSelected(checked);
                    cb.getStyleClass().add("badge-checklist");
                    final int idx = i;
                    final VBox finalBadgeBox = badgeBox;
                    
                    cb.selectedProperty().addListener((obs, was, isNow) -> {
                        try {
                            double scrollValue = scrollPane.getVvalue();
                            VBox parent = (VBox) finalBadgeBox.getParent();
                            int badgeIdx = parent.getChildren().indexOf(finalBadgeBox);
                            String[] editLines = body.split("\n");
                            
                            // Update only the checkbox state, preserve the item text
                            String prefix = isNow ? "- [x] " : "- [ ] ";
                            editLines[idx] = prefix + itemText;
                            
                            String newBody = String.join("\n", editLines);
                            String newText = title + (newBody.isEmpty() ? "" : "\n" + newBody);
                            Node newBadge = createBadge(newText, scrollPane);
                            parent.getChildren().set(badgeIdx, newBadge);
                            Platform.runLater(() -> {
                                double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
                                double viewportHeight = scrollPane.getViewportBounds().getHeight();
                                double pixelScroll = scrollValue * (contentHeight - viewportHeight);
                                double newVvalue = (contentHeight - viewportHeight == 0) ? 0 : pixelScroll / (contentHeight - viewportHeight);
                                scrollPane.setVvalue(newVvalue);
                            });
                        } catch (Exception e) {
                            System.err.println("Error updating checkbox: " + e.getMessage());
                        }
                    });
                    
                    bodyBox.getChildren().add(cb);
                } else if (!line.trim().isEmpty()) {
                    Label l = new Label(line);
                    l.setFont(Font.font("Arial", 13));
                    l.getStyleClass().add("badge-body");
                    l.setWrapText(true);
                    bodyBox.getChildren().add(l);
                }
            }
        }
        bodyBox.setVisible(!body.isEmpty());
        bodyBox.setManaged(!body.isEmpty());

        // Create the topRow (title and toggle button)
        HBox topRow;
        if (!body.isEmpty()) {
            topRow = new HBox(titleLabel, toggleBtn);
        } else {
            topRow = new HBox(titleLabel);
        }
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setSpacing(8);
        topRow.setFillHeight(true);
        
        // Add components to badgeBox
        badgeBox.getChildren().add(topRow);
        if (!body.isEmpty()) badgeBox.getChildren().add(bodyBox);
        
        // --- Pop-in animation only on topRow ---
        topRow.setScaleX(1.0);
        topRow.setScaleY(1.0);
        badgeBox.setUserData(topRow); // Store reference for pop-in

        // Toggle show/hide body
        toggleBtn.setOnAction(ev -> {
            boolean showing = bodyBox.isVisible();
            bodyBox.setVisible(!showing);
            bodyBox.setManaged(!showing);
            toggleBtn.setText(showing ? "▲" : "▼");
        });

        // --- Smooth hover animation for badge ---
        DropShadow badgeShadow = new DropShadow(10, Color.web("#10B981", 0.18));
        badgeBox.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            badgeBox.setScaleX(1.0);
            badgeBox.setScaleY(1.0);
            ScaleTransition st = new ScaleTransition(Duration.millis(250), badgeBox);
            st.setToX(1.08);
            st.setToY(1.08);
            st.play();
            badgeBox.setEffect(badgeShadow);
            badgeBox.setStyle("-fx-background-color: #A7F3D0; -fx-text-fill: #047857;");
        });
        badgeBox.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            badgeBox.setScaleX(1.08);
            badgeBox.setScaleY(1.08);
            ScaleTransition st = new ScaleTransition(Duration.millis(250), badgeBox);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
            badgeBox.setEffect(null);
            badgeBox.setStyle("");
        });

        // --- Swipe to delete ---
        final double[] dragStartX = new double[1];
        badgeBox.setOnMousePressed(e -> {
            dragStartX[0] = e.getSceneX();
        });
        badgeBox.setOnMouseDragged(e -> {
            double offsetX = e.getSceneX() - dragStartX[0];
            if (offsetX < 0) {
                badgeBox.setTranslateX(offsetX);
            }
        });
        badgeBox.setOnMouseReleased(e -> {
            if (badgeBox.getTranslateX() < -80) {
                // Animate out and remove
                TranslateTransition tt = new TranslateTransition(Duration.millis(200), badgeBox);
                tt.setToX(-400);
                tt.setOnFinished(ev -> ((VBox)badgeBox.getParent()).getChildren().remove(badgeBox));
                tt.play();
            } else {
                // Animate back to position
                TranslateTransition tt = new TranslateTransition(Duration.millis(200), badgeBox);
                tt.setToX(0);
                tt.play();
            }
        });

        // --- Click to edit badge (title + body) ---
        badgeBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY && !badgeBox.isDisabled()) {
                VBox parent = (VBox) badgeBox.getParent();
                int idx = parent.getChildren().indexOf(badgeBox);
                VBox editorBox = new VBox(6);
                editorBox.setFillWidth(true);
                editorBox.getStyleClass().add("badge-editor-box");

                TextField titleField = new TextField(title);
                titleField.setFont(Font.font("Arial", 16));
                titleField.setPromptText("Title");
                titleField.getStyleClass().add("badge-title-field");

                // For the body, strip only the checklist brackets for editing
                StringBuilder plainBody = new StringBuilder();
                String[] bodyLines = body.split("\n", -1);
                for (int i = 0; i < bodyLines.length; i++) {
                    String line = bodyLines[i];
                    if (line.trim().matches("- \\[([ xX])\\] .+")) {
                        plainBody.append("- ").append(line.trim().substring(6)); // Show dash and item text
                    } else {
                        plainBody.append(line);
                    }
                    if (i < bodyLines.length - 1) plainBody.append("\n");
                }

                TextArea bodyArea = new TextArea(plainBody.toString());
                bodyArea.setFont(Font.font("Arial", 14));
                bodyArea.setPromptText("Body");
                bodyArea.setWrapText(true);
                bodyArea.setPrefRowCount(Math.max(2, plainBody.toString().split("\n").length));
                bodyArea.setMaxHeight(Double.MAX_VALUE);
                bodyArea.setStyle("-fx-vbar-policy: never;");
                bodyArea.getStyleClass().add("badge-body-area");

                // Auto-correct lines starting with '-' to '- ' as the user types
                bodyArea.textProperty().addListener((obs, oldVal, newVal) -> {
                    String[] lines = newVal.split("\n", -1);
                    StringBuilder corrected = new StringBuilder();
                    for (int i = 0; i < lines.length; i++) {
                        String line = lines[i];
                        if (line.startsWith("-") && !line.startsWith("- ")) {
                            line = "- " + line.substring(1).replaceFirst("^ +", "");
                        }
                        corrected.append(line);
                        if (i < lines.length - 1) corrected.append("\n");
                    }
                    if (!newVal.equals(corrected.toString())) {
                        int caret = bodyArea.getCaretPosition();
                        bodyArea.setText(corrected.toString());
                        bodyArea.positionCaret(Math.min(caret + 1, corrected.length()));
                    }

                    // Auto-grow as you type (scale to text only)
                    int linesCount = Math.max(1, newVal.split("\n", -1).length);
                    double fontHeight = bodyArea.getFont().getSize() + 6; // 6 for padding
                    bodyArea.setPrefHeight(fontHeight * linesCount);
                    bodyArea.setScrollTop(Double.MIN_VALUE);
                });

                // Suggest checklist continuation on Enter
                bodyArea.setOnKeyPressed(ev -> {
                    if (ev.getCode() == javafx.scene.input.KeyCode.ENTER) {
                        int caretPos = bodyArea.getCaretPosition();
                        String bodyText = bodyArea.getText();
                        int lineStart = bodyText.lastIndexOf('\n', caretPos - 2) + 1;
                        int lineEnd = bodyText.indexOf('\n', caretPos);
                        if (lineEnd == -1) lineEnd = bodyText.length();
                        String currentLine = bodyText.substring(lineStart, caretPos);
                        if (currentLine.trim().startsWith("- ")) {
                            Platform.runLater(() -> {
                                bodyArea.insertText(bodyArea.getCaretPosition(), "- ");
                            });
                        }
                    }
                });

                editorBox.getChildren().addAll(titleField, bodyArea);
                parent.getChildren().set(idx, editorBox);
                titleField.requestFocus();
                titleField.selectAll();

                // Save on focus lost from both fields
                ChangeListener<Boolean> focusListener = (obs, was, isNow) -> {
                    if (!titleField.isFocused() && !bodyArea.isFocused()) {
                        // Restore checklist markdown on save
                        String[] originalLines = body.split("\n", -1);
                        String[] editedLines = bodyArea.getText().split("\n", -1);
                        StringBuilder restoredBody = new StringBuilder();
                        for (int i = 0; i < editedLines.length; i++) {
                            String edited = editedLines[i];
                            // Ensure dash is followed by a space
                            if (edited.startsWith("-") && !edited.startsWith("- ")) {
                                edited = "- " + edited.substring(1).replaceFirst("^ +", "");
                            }
                            if (i < originalLines.length && originalLines[i].trim().matches("- \\[([ xX])\\] .+")) {
                                // Restore the original checklist prefix
                                String prefix = originalLines[i].trim().substring(0, 6); // - [ ]  or - [x] 
                                restoredBody.append(prefix).append(edited.trim().substring(2)); // Remove the dash and space from edited
                            } else if (edited.trim().startsWith("- ")) {
                                // New checklist line: add unchecked prefix
                                restoredBody.append("- [ ] ").append(edited.trim().substring(2));
                            } else {
                                restoredBody.append(edited);
                            }
                            if (i < editedLines.length - 1) restoredBody.append("\n");
                        }
                        String newText = titleField.getText();
                        if (!restoredBody.toString().isEmpty()) {
                            newText += "\n" + restoredBody.toString();
                        }
                        Node newBadge = createBadge(newText, scrollPane);
                        parent.getChildren().set(idx, newBadge);
                    }
                };
                titleField.focusedProperty().addListener(focusListener);
                bodyArea.focusedProperty().addListener(focusListener);
            }
        });

        return badgeBox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
