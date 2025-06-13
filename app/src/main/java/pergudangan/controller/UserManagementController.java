package pergudangan.controller;

import javafx.animation.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.Duration;
import pergudangan.model.User;
import pergudangan.model.UserData;
import pergudangan.utils.SceneManager;
import javafx.scene.Parent;

public class UserManagementController {

    private BorderPane root;
    private VBox contentArea;
    private TableView<User> userTable;
    private FilteredList<User> filteredUsers;
    private TextField searchField;
    private Label statsLabel;

    public UserManagementController() {
        createView();
    }

    private void createView() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);");
        
        createHeader();
        createMainContent();
        createFooter();
        
        playEntranceAnimation();
    }

    private void createHeader() {
        // Header container with glass effect
        VBox header = new VBox(20);
        header.setPadding(new Insets(30, 40, 30, 40));
        header.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9);" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 20, 0, 0, 5);"
        );

        // Title section
        HBox titleSection = new HBox(15);
        titleSection.setAlignment(Pos.CENTER_LEFT);
        
        // Icon placeholder (you can replace with actual icon)
        Rectangle iconPlaceholder = new Rectangle(40, 40);
        iconPlaceholder.setFill(Color.web("#667eea"));
        iconPlaceholder.setArcWidth(10);
        iconPlaceholder.setArcHeight(10);
        
        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("User Management");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#2d3748"));
        
        Label subtitleLabel = new Label("Manage system users and their permissions");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.web("#718096"));
        
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        titleSection.getChildren().addAll(iconPlaceholder, titleBox);

        // Search and stats section
        HBox searchSection = new HBox(20);
        searchSection.setAlignment(Pos.CENTER_LEFT);
        
        // Enhanced search field
        VBox searchContainer = new VBox(5);
        Label searchLabel = new Label("Search Users");
        searchLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        searchLabel.setTextFill(Color.web("#4a5568"));
        
        searchField = new TextField();
        searchField.setPromptText("Search by name, role, or status...");
        searchField.setPrefWidth(350);
        searchField.setPrefHeight(45);
        searchField.setFont(Font.font("Segoe UI", 15));
        styleSearchField(searchField);
        
        searchContainer.getChildren().addAll(searchLabel, searchField);
        
        // Stats display
        statsLabel = new Label();
        statsLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        statsLabel.setTextFill(Color.web("#667eea"));
        statsLabel.setStyle(
            "-fx-background-color: rgba(102, 126, 234, 0.1);" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 15;"
        );
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        searchSection.getChildren().addAll(searchContainer, spacer, statsLabel);
        
        header.getChildren().addAll(titleSection, searchSection);
        root.setTop(header);
    }

    private void createMainContent() {
        contentArea = new VBox(25);
        contentArea.setPadding(new Insets(30, 40, 30, 40));
        
        // Setup filtered list
        filteredUsers = new FilteredList<>(UserData.getInstance().getUsers(), p -> true);
        updateStats();
        
        // Search functionality with animation
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            filteredUsers.setPredicate(user -> {
                if (filter.isEmpty()) return true;
                return user.getName().toLowerCase().contains(filter) ||
                       user.getRole().toLowerCase().contains(filter) ||
                       user.getStatus().toLowerCase().contains(filter);
            });
            updateStats();
            playTableRefreshAnimation();
        });

        // Create enhanced table
        createUserTable();
        
        contentArea.getChildren().add(userTable);
        
        // Wrap content in scroll pane
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        root.setCenter(scrollPane);
    }

    private void createUserTable() {
        userTable = new TableView<>();
        userTable.setItems(filteredUsers);
        userTable.setPrefHeight(500);
        
        // Modern table styling
        userTable.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 15, 0, 0, 5);" +
            "-fx-padding: 0;"
        );
        
        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setStyle("-fx-background-color: transparent;");
            
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    row.setStyle("-fx-background-color: transparent;");
                } else {
                    // Alternate row colors
                    if (row.getIndex() % 2 == 0) {
                        row.setStyle("-fx-background-color: rgba(102, 126, 234, 0.05);");
                    } else {
                        row.setStyle("-fx-background-color: white;");
                    }
                }
            });
            
            // Hover effect
            row.setOnMouseEntered(e -> {
                if (row.getItem() != null) {
                    row.setStyle("-fx-background-color: rgba(102, 126, 234, 0.1); -fx-cursor: hand;");
                }
            });
            
            row.setOnMouseExited(e -> {
                if (row.getItem() != null) {
                    if (row.getIndex() % 2 == 0) {
                        row.setStyle("-fx-background-color: rgba(102, 126, 234, 0.05);");
                    } else {
                        row.setStyle("-fx-background-color: white;");
                    }
                }
            });
            
            return row;
        });

        // Enhanced columns
        TableColumn<User, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setPrefWidth(200);
        styleTableColumn(nameCol);
        
        TableColumn<User, String> passwordCol = new TableColumn<>("Password");
        passwordCol.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        passwordCol.setPrefWidth(150);
        passwordCol.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("••••••••"); // Hide password with dots
                }
                setStyle("-fx-alignment: center;");
            }
        });
        styleTableColumn(passwordCol);

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        roleCol.setPrefWidth(120);
        roleCol.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    String roleStyle = getRoleStyle(item);
                    setStyle("-fx-alignment: center; " + roleStyle);
                }
            }
        });
        styleTableColumn(roleCol);

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toUpperCase());
                    String statusStyle = getStatusStyle(item);
                    setStyle("-fx-alignment: center; " + statusStyle);
                }
            }
        });
        styleTableColumn(statusCol);

        // Enhanced action column
        TableColumn<User, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(150);
        styleTableColumn(actionCol);

        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = param -> {
            return new TableCell<User, Void>() {
                private final HBox actionBox = new HBox(10);
                private final Button editBtn = new Button("Edit");
                private final Button deleteBtn = new Button("Delete");

                {
                    // Style edit button
                    editBtn.setStyle(
                        "-fx-background-color: #667eea;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 6 12;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                    );
                    
                    // Style delete button
                    deleteBtn.setStyle(
                        "-fx-background-color: #e53e3e;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 6 12;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                    );
                    
                    // Button hover effects
                    addButtonHoverEffect(editBtn, "#5a67d8", "#667eea");
                    addButtonHoverEffect(deleteBtn, "#c53030", "#e53e3e");
                    
                    // Button actions
                    editBtn.setOnAction(e -> {
                        User user = getTableView().getItems().get(getIndex());
                        showEditDialog(user);
                    });
                    
                    deleteBtn.setOnAction(e -> {
                        User user = getTableView().getItems().get(getIndex());
                        showDeleteConfirmation(user);
                    });
                    
                    actionBox.setAlignment(Pos.CENTER);
                    actionBox.getChildren().addAll(editBtn, deleteBtn);
                }

                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(actionBox);
                    }
                }
            };
        };

        actionCol.setCellFactory(cellFactory);
        userTable.getColumns().addAll(nameCol, passwordCol, roleCol, statusCol, actionCol);
    }

    private void createFooter() {
        HBox footer = new HBox(20);
        footer.setPadding(new Insets(20, 40, 30, 40));
        footer.setAlignment(Pos.CENTER_LEFT);
        
        Button backBtn = new Button("← Back to Dashboard");
        styleActionButton(backBtn, "#667eea", "#5a67d8");
        backBtn.setOnAction(e -> {
            playButtonAnimation(backBtn);
            SceneManager.showDashboardScene();
        });

        Button refreshBtn = new Button("🔄 Refresh Data");
        styleActionButton(refreshBtn, "#38a169", "#2f855a");
        refreshBtn.setOnAction(e -> {
            playButtonAnimation(refreshBtn);
            refreshData();
        });
        
        Button clearSearchBtn = new Button("✖ Clear Search");
        styleActionButton(clearSearchBtn, "#718096", "#4a5568");
        clearSearchBtn.setOnAction(e -> {
            playButtonAnimation(clearSearchBtn);
            searchField.clear();
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label footerInfo = new Label("User Management System v2.0");
        footerInfo.setFont(Font.font("Segoe UI", 12));
        footerInfo.setTextFill(Color.web("#718096"));
        
        footer.getChildren().addAll(backBtn, refreshBtn, clearSearchBtn, spacer, footerInfo);
        root.setBottom(footer);
    }

    private void styleSearchField(TextField field) {
        field.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 2;" +
            "-fx-padding: 12 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.05), 10, 0, 0, 2);"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-radius: 12;" +
                    "-fx-border-color: #667eea;" +
                    "-fx-border-width: 2;" +
                    "-fx-padding: 12 16;" +
                    "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.2), 15, 0, 0, 5);"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-radius: 12;" +
                    "-fx-border-color: #e2e8f0;" +
                    "-fx-border-width: 2;" +
                    "-fx-padding: 12 16;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.05), 10, 0, 0, 2);"
                );
            }
        });
    }

    private void styleActionButton(Button button, String bgColor, String hoverColor) {
        button.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12 20;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 8, 0, 0, 3);"
        );
        
        addButtonHoverEffect(button, hoverColor, bgColor);
    }

    private void addButtonHoverEffect(Button button, String hoverColor, String normalColor) {
        button.setOnMouseEntered(e -> {
            button.setStyle(
                button.getStyle().replace(normalColor, hoverColor)
            );
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                button.getStyle().replace(hoverColor, normalColor)
            );
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    private void styleTableColumn(TableColumn<?, ?> column) {
        column.setStyle(
            "-fx-alignment: CENTER;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #2d3748;"
        );
    }

    private String getRoleStyle(String role) {
        return switch (role.toLowerCase()) {
            case "admin" -> 
                "-fx-background-color: #fed7d7; -fx-text-fill: #c53030; " +
                "-fx-background-radius: 12; -fx-padding: 4 8; -fx-font-weight: bold;";
            case "user" -> 
                "-fx-background-color: #c6f6d5; -fx-text-fill: #2f855a; " +
                "-fx-background-radius: 12; -fx-padding: 4 8; -fx-font-weight: bold;";
            case "manager" -> 
                "-fx-background-color: #feebc8; -fx-text-fill: #c05621; " +
                "-fx-background-radius: 12; -fx-padding: 4 8; -fx-font-weight: bold;";
            default -> 
                "-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; " +
                "-fx-background-radius: 12; -fx-padding: 4 8; -fx-font-weight: bold;";
        };
    }

    private String getStatusStyle(String status) {
        return switch (status.toLowerCase()) {
            case "active" -> 
                "-fx-background-color: #c6f6d5; -fx-text-fill: #2f855a; " +
                "-fx-background-radius: 12; -fx-padding: 4 8; -fx-font-weight: bold;";
            case "inactive" -> 
                "-fx-background-color: #fed7d7; -fx-text-fill: #c53030; " +
                "-fx-background-radius: 12; -fx-padding: 4 8; -fx-font-weight: bold;";
            case "pending" -> 
                "-fx-background-color: #feebc8; -fx-text-fill: #c05621; " +
                "-fx-background-radius: 12; -fx-padding: 4 8; -fx-font-weight: bold;";
            default -> 
                "-fx-background-color: #e2e8f0; -fx-text-fill: #4a5568; " +
                "-fx-background-radius: 12; -fx-padding: 4 8; -fx-font-weight: bold;";
        };
    }

    private void updateStats() {
        int total = UserData.getInstance().getUsers().size();
        int filtered = filteredUsers.size();
        long activeUsers = filteredUsers.stream()
            .filter(user -> "active".equalsIgnoreCase(user.getStatus()))
            .count();
        
        statsLabel.setText(String.format("Showing %d of %d users | %d active", 
            filtered, total, activeUsers));
    }

    private void showEditDialog(User user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edit User");
        alert.setHeaderText("Edit User: " + user.getName());
        alert.setContentText("Edit functionality would be implemented here.");
        alert.showAndWait();
    }

    private void showDeleteConfirmation(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete User Confirmation");
        alert.setContentText("Are you sure you want to delete user: " + user.getName() + "?\n\nThis action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                UserData.getInstance().removeUser(user);
                updateStats();
                playTableRefreshAnimation();
                
                // Show success message
                showSuccessMessage("User '" + user.getName() + "' has been deleted successfully.");
            }
        });
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshData() {
        updateStats();
        userTable.refresh();
        playTableRefreshAnimation();
    }

    private void playEntranceAnimation() {
        // Initial state
        root.setOpacity(0);
        root.setTranslateY(30);
        
        // Fade in and slide up
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO,
                new KeyValue(root.opacityProperty(), 0),
                new KeyValue(root.translateYProperty(), 30)
            ),
            new KeyFrame(Duration.millis(600),
                new KeyValue(root.opacityProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_OUT)
            )
        );
        timeline.play();
    }

    private void playTableRefreshAnimation() {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), userTable);
        st.setToX(0.98);
        st.setToY(0.98);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void playButtonAnimation(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setToX(0.95);
        st.setToY(0.95);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    public Parent getView() {
        return root;
    }
}