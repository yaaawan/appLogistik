package pergudangan.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import pergudangan.model.StockItem;
import pergudangan.utils.SceneManager;
import pergudangan.service.Database;
import java.util.List;
import java.util.Optional;

public class StokController {
    private VBox root;
    private TextField txtItemName, txtQuantity, txtUnit, txtPurchasePrice, txtSellingPrice;
    private ComboBox<String> cmbCategory;
    private Label lblMessage;
    private TableView<StockItem> tableStock;
    private ObservableList<StockItem> stockData;
    private ComboBox<String> cmbFilterCategory;
    private Stage stage;

    public StokController(Stage stage) {
        this.stage = stage;
        createView();
        loadStockData(null);
    }

    private void createView() {
        root = new VBox(12); 
        root.setPadding(new Insets(16)); 
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");

        createHeader();
        createInputForm();
        createActionButtons();
        createMessageLabel();
        createFilterSection();
        createTable();
    }

    private void createHeader() {
        VBox headerBox = new VBox(6);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 12, 0));

        Label title = new Label("📦 Manajemen Stok Barang");
        title.setFont(Font.font("System", FontWeight.BOLD, 22)); 
        title.setStyle("-fx-text-fill: #2c3e50;");
        
        Label subtitle = new Label("Kelola inventori dengan mudah dan efisien");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 11));
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        headerBox.getChildren().addAll(title, subtitle);
        root.getChildren().add(headerBox);
    }

    private void createInputForm() {
        VBox formContainer = new VBox(10); 
        formContainer.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-border-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        formContainer.setPadding(new Insets(16));
        Label formTitle = new Label("📝 Informasi Barang");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        formTitle.setStyle("-fx-text-fill: #2c3e50;");

        txtItemName = createStyledTextField("Nama Barang", "📦");
        txtQuantity = createStyledTextField("Jumlah", "🔢");
        txtUnit = createStyledTextField("Satuan (kg, pcs, ltr)", "📏");
        txtPurchasePrice = createStyledTextField("Harga Beli", "💰");
        txtSellingPrice = createStyledTextField("Harga Jual", "💵");

        cmbCategory = new ComboBox<>();
        cmbCategory.getItems().addAll("Makanan", "Minuman", "Elektronik", "Peralatan", "Lainnya");
        cmbCategory.setPromptText("🏷️ Pilih Kategori");
        cmbCategory.setPrefWidth(180);
        cmbCategory.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-border-color: #dee2e6; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 8; " +
            "-fx-font-size: 12;"
        );

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(8);
        formGrid.setAlignment(Pos.CENTER);

        formGrid.add(new Label("Nama Barang:"), 0, 0);
        formGrid.add(txtItemName, 1, 0);
        formGrid.add(new Label("Kategori:"), 2, 0);
        formGrid.add(cmbCategory, 3, 0);

        formGrid.add(new Label("Jumlah:"), 0, 1);
        formGrid.add(txtQuantity, 1, 1);
        formGrid.add(new Label("Satuan:"), 2, 1);
        formGrid.add(txtUnit, 3, 1);

        formGrid.add(new Label("Harga Beli:"), 0, 2);
        formGrid.add(txtPurchasePrice, 1, 2);
        formGrid.add(new Label("Harga Jual:"), 2, 2);
        formGrid.add(txtSellingPrice, 3, 2);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j += 2) {
                if (formGrid.getChildren().size() > i * 4 + j) {
                    ((Label) formGrid.getChildren().get(i * 4 + j)).setStyle(
                        "-fx-font-weight: bold; -fx-text-fill: #495057; -fx-font-size: 12;"
                    );
                }
            }
        }

        formContainer.getChildren().addAll(formTitle, formGrid);
        root.getChildren().add(formContainer);
    }

    private TextField createStyledTextField(String promptText, String icon) {
        TextField textField = new TextField();
        textField.setPromptText(icon + " " + promptText);
        textField.setPrefWidth(160);
        textField.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-border-color: #dee2e6; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 8; " +
            "-fx-font-size: 12;"
        );
        
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textField.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-border-color: #007bff; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-radius: 8; " +
                    "-fx-padding: 8; " +
                    "-fx-font-size: 12;"
                );
            } else {
                textField.setStyle(
                    "-fx-background-color: #f8f9fa; " +
                    "-fx-border-color: #dee2e6; " +
                    "-fx-border-radius: 8; " +
                    "-fx-background-radius: 8; " +
                    "-fx-padding: 8; " +
                    "-fx-font-size: 12;"
                );
            }
        });
        
        return textField;
    }

    private void createActionButtons() {
        HBox buttonContainer = new HBox(8);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(12, 0, 0, 0));

        Button btnAdd = createStyledButton("➕ Tambah", "#28a745", "#ffffff");
        btnAdd.setOnAction(e -> addStockItem());

        Button btnEdit = createStyledButton("✏️ Edit", "#ffc107", "#212529");
        btnEdit.setOnAction(e -> editStockItem());

        Button btnDelete = createStyledButton("🗑️ Hapus", "#dc3545", "#ffffff");
        btnDelete.setOnAction(e -> deleteStockItem());

        Button btnBackToDashboard = createStyledButton("🏠 Dashboard", "#6c757d", "#ffffff");
        btnBackToDashboard.setOnAction(e -> SceneManager.showDashboardScene());

        buttonContainer.getChildren().addAll(btnAdd, btnEdit, btnDelete, btnBackToDashboard);
        root.getChildren().add(buttonContainer);
    }

    private Button createStyledButton(String text, String bgColor, String textColor) {
        Button button = new Button(text);
        button.setPrefWidth(115);
        button.setPrefHeight(34);
        button.setStyle(
            "-fx-background-color: " + bgColor + "; " +
            "-fx-text-fill: " + textColor + "; " +
            "-fx-font-size: 12; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: derive(" + bgColor + ", -10%); " +
                "-fx-text-fill: " + textColor + "; " +
                "-fx-font-size: 12; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + bgColor + "; " +
                "-fx-text-fill: " + textColor + "; " +
                "-fx-font-size: 12; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8; " +
                "-fx-cursor: hand;"
            );
        });

        return button;
    }

    private void createMessageLabel() {
        lblMessage = new Label();
        lblMessage.setFont(Font.font("System", FontWeight.BOLD, 12));
        lblMessage.setPadding(new Insets(6));
        lblMessage.setStyle(
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-padding: 6;"
        );
        root.getChildren().add(lblMessage);
    }

    private void createFilterSection() {
        HBox filterContainer = new HBox(10);
        filterContainer.setAlignment(Pos.CENTER_LEFT);
        filterContainer.setPadding(new Insets(8, 0, 6, 0)); 
        filterContainer.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);"
        );

        Label filterLabel = new Label("🔍 Filter Kategori:");
        filterLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        filterLabel.setStyle("-fx-text-fill: #495057;");

        cmbFilterCategory = new ComboBox<>();
        cmbFilterCategory.getItems().addAll("Semua", "Makanan", "Minuman", "Elektronik", "Peralatan", "Lainnya");
        cmbFilterCategory.setValue("Semua");
        cmbFilterCategory.setPrefWidth(140);
        cmbFilterCategory.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-border-color: #007bff; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 6; " +
            "-fx-font-size: 12;"
        );
        
        cmbFilterCategory.setOnAction(e -> {
            String selectedCategory = cmbFilterCategory.getValue();
            if ("Semua".equals(selectedCategory)) selectedCategory = null;
            loadStockData(selectedCategory);
        });

        filterContainer.getChildren().addAll(filterLabel, cmbFilterCategory);
        root.getChildren().add(filterContainer);
    }

    private void createTable() {
        tableStock = new TableView<>();
        stockData = FXCollections.observableArrayList();
        tableStock.setItems(stockData);

        // Style the table
        tableStock.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #dee2e6; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );

        tableStock.setPrefHeight(400);  

        TableColumn<StockItem, String> colItemName = createStyledColumn("📦 Nama Barang", "itemName", 140);
        TableColumn<StockItem, Integer> colQuantity = createStyledColumn("🔢 Jumlah", "quantity", 80);
        TableColumn<StockItem, String> colUnit = createStyledColumn("📏 Satuan", "unit", 80);
        TableColumn<StockItem, Double> colPurchasePrice = createStyledColumn("💰 Harga Beli", "purchasePrice", 100);
        TableColumn<StockItem, Double> colSellingPrice = createStyledColumn("💵 Harga Jual", "sellingPrice", 100);
        TableColumn<StockItem, String> colCategory = createStyledColumn("🏷️ Kategori", "category", 110);

        colPurchasePrice.setCellFactory(column -> new TableCell<StockItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %.2f", item));
                }
            }
        });

        colSellingPrice.setCellFactory(column -> new TableCell<StockItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %.2f", item));
                }
            }
        });

        tableStock.getColumns().addAll(colItemName, colQuantity, colUnit, colPurchasePrice, colSellingPrice, colCategory);
        
        tableStock.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtItemName.setText(newSelection.getItemName());
                txtQuantity.setText(String.valueOf(newSelection.getQuantity()));
                txtUnit.setText(newSelection.getUnit());
                txtPurchasePrice.setText(String.valueOf(newSelection.getPurchasePrice()));
                txtSellingPrice.setText(String.valueOf(newSelection.getSellingPrice()));
                cmbCategory.setValue(newSelection.getCategory());
            }
        });

        ScrollPane scrollPane = new ScrollPane(tableStock);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setPrefHeight(400); 

        root.getChildren().add(scrollPane);
    }

    private <T> TableColumn<StockItem, T> createStyledColumn(String title, String property, int width) {
        TableColumn<StockItem, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        column.setStyle("-fx-font-size: 11; -fx-alignment: CENTER;");
        return column;
    }

    private void loadStockData(String filterCategory) {
        List<StockItem> items = Database.getAllStockItems(filterCategory);
        stockData.clear();
        stockData.addAll(items);
    }

    private void resetForm() {
        txtItemName.clear();
        txtQuantity.clear();
        txtUnit.clear();
        txtPurchasePrice.clear();
        txtSellingPrice.clear();
        cmbCategory.setValue(null);
        tableStock.getSelectionModel().clearSelection();
    }

    private void showMessage(String message, String type) {
        lblMessage.setText(message);
        switch (type) {
            case "success":
                lblMessage.setStyle(
                    "-fx-text-fill: #155724; " +
                    "-fx-background-color: #d4edda; " +
                    "-fx-border-color: #c3e6cb; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-radius: 8; " +
                    "-fx-padding: 6;"
                );
                break;
            case "error":
                lblMessage.setStyle(
                    "-fx-text-fill: #721c24; " +
                    "-fx-background-color: #f8d7da; " +
                    "-fx-border-color: #f5c6cb; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-radius: 8; " +
                    "-fx-padding: 6;"
                );
                break;
            case "warning":
                lblMessage.setStyle(
                    "-fx-text-fill: #856404; " +
                    "-fx-background-color: #fff3cd; " +
                    "-fx-border-color: #ffeaa7; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-radius: 8; " +
                    "-fx-padding: 6;"
                );
                break;
        }
    }

    private void addStockItem() {
        String itemName = txtItemName.getText().trim();
        String quantityStr = txtQuantity.getText().trim();
        String unit = txtUnit.getText().trim();
        String purchasePriceStr = txtPurchasePrice.getText().trim();
        String sellingPriceStr = txtSellingPrice.getText().trim();
        String category = cmbCategory.getValue();

        if (itemName.isEmpty() || quantityStr.isEmpty() || unit.isEmpty() ||
            purchasePriceStr.isEmpty() || sellingPriceStr.isEmpty() || category == null) {
            showMessage("❌ Semua field harus diisi.", "error");
            return;
        }

        int quantity;
        double purchasePrice;
        double sellingPrice;
        try {
            quantity = Integer.parseInt(quantityStr);
            purchasePrice = Double.parseDouble(purchasePriceStr);
            sellingPrice = Double.parseDouble(sellingPriceStr);
            
            if (quantity <= 0 || purchasePrice <= 0 || sellingPrice <= 0) {
                showMessage("❌ Jumlah dan harga harus berupa angka positif.", "error");
                return;
            }
        } catch (NumberFormatException e) {
            showMessage("❌ Jumlah dan harga harus berupa angka yang valid.", "error");
            return;
        }

        boolean success = Database.insertOrUpdateStockItem(
            new StockItem(itemName, quantity, unit, purchasePrice, sellingPrice, category)
        );

        if (success) {
            showMessage("✅ Stok berhasil ditambahkan.", "success");
            loadStockData(cmbFilterCategory.getValue().equals("Semua") ? null : cmbFilterCategory.getValue());
            resetForm();
        } else {
            showMessage("❌ Gagal menambahkan stok.", "error");
        }
    }

    private void editStockItem() {
        StockItem selectedItem = tableStock.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showMessage("⚠️ Pilih item dari tabel untuk diedit.", "warning");
            return;
        }

        String itemName = txtItemName.getText().trim();
        String quantityStr = txtQuantity.getText().trim();
        String unit = txtUnit.getText().trim();
        String purchasePriceStr = txtPurchasePrice.getText().trim();
        String sellingPriceStr = txtSellingPrice.getText().trim();
        String category = cmbCategory.getValue();

        if (itemName.isEmpty() || quantityStr.isEmpty() || unit.isEmpty() ||
            purchasePriceStr.isEmpty() || sellingPriceStr.isEmpty() || category == null) {
            showMessage("❌ Semua field harus diisi.", "error");
            return;
        }

        int quantity;
        double purchasePrice;
        double sellingPrice;
        try {
            quantity = Integer.parseInt(quantityStr);
            purchasePrice = Double.parseDouble(purchasePriceStr);
            sellingPrice = Double.parseDouble(sellingPriceStr);
            
            if (quantity <= 0 || purchasePrice <= 0 || sellingPrice <= 0) {
                showMessage("❌ Jumlah dan harga harus berupa angka positif.", "error");
                return;
            }
        } catch (NumberFormatException e) {
            showMessage("❌ Jumlah dan harga harus berupa angka yang valid.", "error");
            return;
        }

        selectedItem.setItemName(itemName);
        selectedItem.setQuantity(quantity);
        selectedItem.setUnit(unit);
        selectedItem.setPurchasePrice(purchasePrice);
        selectedItem.setSellingPrice(sellingPrice);
        selectedItem.setCategory(category);

        boolean success = Database.updateStockItem(selectedItem);
        if (success) {
            showMessage("✅ Stok berhasil diperbarui.", "success");
            loadStockData(cmbFilterCategory.getValue().equals("Semua") ? null : cmbFilterCategory.getValue());
            resetForm();
        } else {
            showMessage("❌ Gagal memperbarui stok.", "error");
        }
    }

    private void deleteStockItem() {
        StockItem selectedItem = tableStock.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showMessage("⚠️ Pilih item dari tabel untuk dihapus.", "warning");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("🗑️ Konfirmasi Hapus");
        alert.setHeaderText("Hapus Item Stok");
        alert.setContentText("Apakah Anda yakin ingin menghapus item '" + selectedItem.getItemName() + "'?\n\nTindakan ini tidak dapat dibatalkan.");
        
        alert.getDialogPane().setStyle(
            "-fx-background-color: white; " +
            "-fx-font-size: 14;"
        );

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = Database.deleteStockItem(selectedItem.getItemName());
            if (success) {
                showMessage("✅ Item berhasil dihapus.", "success");
                loadStockData(cmbFilterCategory.getValue().equals("Semua") ? null : cmbFilterCategory.getValue());
                resetForm();
            } else {
                showMessage("❌ Gagal menghapus item.", "error");
            }
        }
    }

    public Parent getView() {
        return root;
    }
}

