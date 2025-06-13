package pergudangan.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Modality;
import pergudangan.model.*;
import pergudangan.utils.SceneManager;
import pergudangan.service.Database;
import javafx.beans.property.SimpleDoubleProperty;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.Parent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.StringConverter;
import java.time.format.DateTimeFormatter;

public class PengeluaranController {
    private BorderPane root;
    private TextField txtItemName, txtQuantity;
    private Label lblMessage;
    private TableView<StockItem> tableStock;
    private ObservableList<StockItem> stockData;
    private Stage stage;
    private ComboBox<String> comboKategori;

    public PengeluaranController(Stage stage) {
        this.stage = stage;
        createView();
        loadStockData("Semua"); 
    }

    private void createView() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20;");
        VBox headerSection = createHeaderSection();
        VBox mainContent = createMainContent();
        HBox footerSection = createFooterSection();

        root.setTop(headerSection);
        root.setCenter(mainContent);
        root.setBottom(footerSection);
    }

    private VBox createHeaderSection() {
        VBox headerBox = new VBox(15);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 20, 0));
        
        Label title = new Label("📦 Form Pengeluaran Stok Barang");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");
        
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #3498db;");
        
        headerBox.getChildren().addAll(title, separator);
        return headerBox;
    }

    private VBox createMainContent() {
        VBox mainBox = new VBox(20);
        VBox formSection = createFormSection();
        VBox tableSection = createTableSection();
        
        mainBox.getChildren().addAll(formSection, tableSection);
        return mainBox;
    }

    private VBox createFormSection() {
        VBox formBox = new VBox(15);
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        formBox.setPadding(new Insets(20));
        
        Label formTitle = new Label("📝 Data Pengeluaran");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        formTitle.setStyle("-fx-text-fill: #34495e;");
        
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(15);
        inputGrid.setVgap(15);
        inputGrid.setAlignment(Pos.CENTER_LEFT);
        
        Label lblItemName = new Label("Nama Barang:");
        lblItemName.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        lblItemName.setStyle("-fx-text-fill: #2c3e50;");
        
        txtItemName = new TextField();
        txtItemName.setPromptText("Masukkan nama barang...");
        txtItemName.setPrefWidth(200);
        txtItemName.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #bdc3c7; -fx-padding: 8;");
        
        Label lblQuantity = new Label("Jumlah:");
        lblQuantity.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        lblQuantity.setStyle("-fx-text-fill: #2c3e50;");
        
        txtQuantity = new TextField();
        txtQuantity.setPromptText("Masukkan jumlah...");
        txtQuantity.setPrefWidth(200);
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #bdc3c7; -fx-padding: 8;");
        
        Label lblCategory = new Label("Kategori:");
        lblCategory.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        lblCategory.setStyle("-fx-text-fill: #2c3e50;");
        
        comboKategori = new ComboBox<>();
        comboKategori.getItems().addAll("Semua", "Makanan", "Minuman", "Elektronik", "Peralatan", "Lainnya");
        comboKategori.setValue("Semua");
        comboKategori.setPrefWidth(200);
        comboKategori.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #bdc3c7;");
        comboKategori.setOnAction(e -> loadStockData(comboKategori.getValue()));
        
        inputGrid.add(lblItemName, 0, 0);
        inputGrid.add(txtItemName, 1, 0);
        inputGrid.add(lblQuantity, 0, 1);
        inputGrid.add(txtQuantity, 1, 1);
        inputGrid.add(lblCategory, 0, 2);
        inputGrid.add(comboKategori, 1, 2);
        
        Button btnProcess = new Button("✅ Proses Pengeluaran");
        btnProcess.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; " +
                          "-fx-background-radius: 5; -fx-padding: 10 20; -fx-font-size: 12px;");
        btnProcess.setOnMouseEntered(e -> btnProcess.setStyle("-fx-background-color: #229954; -fx-text-fill: white; -fx-font-weight: bold; " +
                                                            "-fx-background-radius: 5; -fx-padding: 10 20; -fx-font-size: 12px;"));
        btnProcess.setOnMouseExited(e -> btnProcess.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; " +
                                                           "-fx-background-radius: 5; -fx-padding: 10 20; -fx-font-size: 12px;"));
        btnProcess.setOnAction(e -> processPengeluaran());
        
        lblMessage = new Label();
        lblMessage.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        
        HBox processBox = new HBox(15);
        processBox.setAlignment(Pos.CENTER_LEFT);
        processBox.getChildren().addAll(btnProcess, lblMessage);
        
        formBox.getChildren().addAll(formTitle, inputGrid, processBox);
        return formBox;
    }

    private VBox createTableSection() {
        VBox tableBox = new VBox(15);
        tableBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        tableBox.setPadding(new Insets(20));
        
        Label tableTitle = new Label("📊 Data Stok Barang");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        tableTitle.setStyle("-fx-text-fill: #34495e;");
        
        tableStock = new TableView<>();
        stockData = FXCollections.observableArrayList();
        tableStock.setItems(stockData);
        tableStock.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ecf0f1; -fx-border-radius: 5;");
        
        TableColumn<StockItem, String> colItemName = new TableColumn<>("Nama Barang");
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colItemName.setPrefWidth(180);
        colItemName.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-weight: bold;");

        TableColumn<StockItem, Integer> colQuantity = new TableColumn<>("Jumlah");
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQuantity.setPrefWidth(80);
        colQuantity.setStyle("-fx-alignment: CENTER;");

        TableColumn<StockItem, Double> colSellingPrice = new TableColumn<>("Harga Jual");
        colSellingPrice.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        colSellingPrice.setPrefWidth(120);
        colSellingPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
        colSellingPrice.setCellFactory(column -> new TableCell<StockItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.0f", item));
                }
            }
        });

        TableColumn<StockItem, String> colCategory = new TableColumn<>("Kategori");
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCategory.setPrefWidth(120);
        colCategory.setStyle("-fx-alignment: CENTER;");

        TableColumn<StockItem, Double> colTotalPrice = new TableColumn<>("Total Harga");
        colTotalPrice.setCellValueFactory(cellData -> {
            StockItem item = cellData.getValue();
            return new SimpleDoubleProperty(item.getSellingPrice() * item.getQuantity()).asObject();
        });
        colTotalPrice.setPrefWidth(140);
        colTotalPrice.setStyle("-fx-alignment: CENTER-RIGHT;");
        colTotalPrice.setCellFactory(column -> new TableCell<StockItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.0f", item));
                }
            }
        });

        tableStock.getColumns().addAll(colItemName, colQuantity, colSellingPrice, colCategory, colTotalPrice);
        
        tableStock.setRowFactory(tv -> {
            TableRow<StockItem> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    if (newItem.getQuantity() <= 5) {
                        row.setStyle("-fx-background-color: #fadbd8;"); // Light red for low stock
                    } else {
                        row.setStyle("");
                    }
                }
            });
            return row;
        });
        
        tableBox.getChildren().addAll(tableTitle, tableStock);
        return tableBox;
    }

    private HBox createFooterSection() {
        HBox footerBox = new HBox(15);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button btnViewPengeluaran = new Button("📋 Lihat Pengeluaran");
        btnViewPengeluaran.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                                   "-fx-background-radius: 5; -fx-padding: 10 20; -fx-font-size: 12px;");
        btnViewPengeluaran.setOnMouseEntered(e -> btnViewPengeluaran.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; " +
                                                                             "-fx-background-radius: 5; -fx-padding: 10 20; -fx-font-size: 12px;"));
        btnViewPengeluaran.setOnMouseExited(e -> btnViewPengeluaran.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                                                                            "-fx-background-radius: 5; -fx-padding: 10 20; -fx-font-size: 12px;"));
        btnViewPengeluaran.setOnAction(e -> showPengeluaranList());

        Button btnBackToDashboard = new Button("🏠 Kembali ke Dashboard");
        btnBackToDashboard.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; " +
                                   "-fx-background-radius: 5; -fx-padding: 10 20; -fx-font-size: 12px;");
        btnBackToDashboard.setOnMouseEntered(e -> btnBackToDashboard.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; " +
                                                                             "-fx-background-radius: 5; -fx-padding: 10 20; -fx-font-size: 12px;"));
        btnBackToDashboard.setOnMouseExited(e -> btnBackToDashboard.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; " +
                                                                            "-fx-background-radius: 5; -fx-padding: 10 20; -fx-font-size: 12px;"));
        btnBackToDashboard.setOnAction(e -> SceneManager.showDashboardScene());
        
        footerBox.getChildren().addAll(btnViewPengeluaran, btnBackToDashboard);
        return footerBox;
    }

    private void loadStockData(String kategori) {
        List<StockItem> items = Database.getAllStockItems(null);
        stockData.clear();

        if (kategori.equalsIgnoreCase("Semua")) {
            stockData.addAll(items);
        } else {
            List<StockItem> filtered = items.stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(kategori))
                .collect(Collectors.toList());
            stockData.addAll(filtered);
        }
    }

    private void processPengeluaran() {
        String itemName = txtItemName.getText().trim();
        String quantityStr = txtQuantity.getText().trim();
        String category = comboKategori.getValue(); 

        if (itemName.isEmpty() || quantityStr.isEmpty() || category == null || category.isEmpty()) {
            showMessage("⚠️ Semua field termasuk kategori harus diisi.", "#e74c3c");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                showMessage("⚠️ Jumlah harus lebih dari 0.", "#e74c3c");
                return;
            }
        } catch (NumberFormatException e) {
            showMessage("⚠️ Jumlah harus berupa angka yang valid.", "#e74c3c");
            return;
        }

        StockItem stockItem = stockData.stream()
            .filter(item -> item.getItemName().equalsIgnoreCase(itemName))
            .findFirst()
            .orElse(null);

        if (stockItem == null) {
            showMessage("❌ Barang tidak ditemukan di stok.", "#e74c3c");
            return;
        }

        if (quantity > stockItem.getQuantity()) {
            showMessage("❌ Jumlah pengeluaran melebihi stok yang tersedia.", "#e74c3c");
            return;
        }

        double totalPrice = quantity * stockItem.getSellingPrice();

        Pengeluaran pengeluaran = new Pengeluaran(0, itemName, quantity, totalPrice, LocalDate.now(), category);
        Database.insertPengeluaran(pengeluaran);

        stockItem.setQuantity(stockItem.getQuantity() - quantity);
        boolean success = Database.updateStockItem(stockItem);

        if (success) {
            showMessage("✅ Pengeluaran berhasil diproses.", "#27ae60");
            loadStockData(comboKategori.getValue());
            clearForm();
        } else {
            showMessage("❌ Gagal memproses pengeluaran.", "#e74c3c");
        }
    }

    private void clearForm() {
        txtItemName.clear();
        txtQuantity.clear();
        comboKategori.setValue("Semua");
    }

    private void showPengeluaranList() {
        Stage dialog = new Stage();
        dialog.setTitle("📋 Daftar Pengeluaran");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(stage);

        BorderPane dialogRoot = new BorderPane();
        dialogRoot.setStyle("-fx-background-color: #f8f9fa;");
        dialogRoot.setPadding(new Insets(20));

        // Header
        Label headerLabel = new Label("📋 Daftar Pengeluaran Barang");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        headerLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getChildren().addAll(headerLabel, new Separator());

        TableView<Pengeluaran> tablePengeluaran = new TableView<>();
        tablePengeluaran.setStyle("-fx-background-color: white; -fx-border-color: #ecf0f1; -fx-border-radius: 5;");

        ObservableList<Pengeluaran> pengeluaranData = FXCollections.observableArrayList(Database.getAllPengeluaran());
        tablePengeluaran.setItems(pengeluaranData);

        TableColumn<Pengeluaran, String> colItemName = new TableColumn<>("Nama Barang");
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colItemName.setPrefWidth(150);

        TableColumn<Pengeluaran, Integer> colQuantity = new TableColumn<>("Jumlah");
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQuantity.setPrefWidth(80);
        colQuantity.setStyle("-fx-alignment: CENTER;");

        TableColumn<Pengeluaran, Double> colTotalPrice = new TableColumn<>("Total Harga");
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colTotalPrice.setPrefWidth(120);
        colTotalPrice.setCellFactory(column -> new TableCell<Pengeluaran, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.0f", item));
                }
            }
        });

        TableColumn<Pengeluaran, LocalDate> colDate = new TableColumn<>("Tanggal");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setPrefWidth(100);
        colDate.setCellFactory(column -> new TableCell<Pengeluaran, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        });

        ObservableList<String> kategoriList = FXCollections.observableArrayList(
                "Makanan", "Minuman", "ATK", "Elektronik", "Lainnya"
        );
        TableColumn<Pengeluaran, String> colCategory = new TableColumn<>("Kategori");
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCategory.setCellFactory(ComboBoxTableCell.forTableColumn(kategoriList));
        colCategory.setPrefWidth(100);
        tablePengeluaran.setEditable(true);
        colCategory.setEditable(true);
        colCategory.setOnEditCommit(event -> {
            Pengeluaran p = event.getRowValue();
            p.setCategory(event.getNewValue());

            boolean updated = Database.updatePengeluaranCategory(p.getId(), event.getNewValue());
            if (!updated) {
                showAlert("❌ Gagal mengupdate kategori.");
            }
            tablePengeluaran.refresh();
        });

        TableColumn<Pengeluaran, Void> colActions = new TableColumn<>("Aksi");
        colActions.setPrefWidth(120);
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✏️");
            private final Button btnDelete = new Button("🗑️");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 3; -fx-padding: 5;");
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 3; -fx-padding: 5;");
                pane.setAlignment(Pos.CENTER);

                btnEdit.setOnAction(e -> {
                    Pengeluaran p = getTableView().getItems().get(getIndex());
                    showEditDialog(p);
                });

                btnDelete.setOnAction(e -> {
                    Pengeluaran p = getTableView().getItems().get(getIndex());
                    boolean confirmed = confirmDelete();
                    if (confirmed) {
                        boolean success = Database.deletePengeluaran(p);
                        if (success) {
                            getTableView().getItems().remove(p);
                            showAlert("✅ Data pengeluaran berhasil dihapus.");
                        } else {
                            showAlert("❌ Gagal menghapus data pengeluaran.");
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        tablePengeluaran.getColumns().addAll(colItemName, colQuantity, colCategory, colTotalPrice, colDate, colActions);

        dialogRoot.setTop(headerBox);
        dialogRoot.setCenter(tablePengeluaran);

        Scene scene = new Scene(dialogRoot, 900, 500);
        dialog.setScene(scene);
        dialog.show();
    }

    private boolean confirmDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("🗑️ Konfirmasi Hapus");
        alert.setHeaderText("Hapus Data Pengeluaran");
        alert.setContentText("Apakah Anda yakin ingin menghapus data ini?");
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #ffffff;");
        
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ℹ️ Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #ffffff;");
        
        alert.showAndWait();
    }

    private void showEditDialog(Pengeluaran pengeluaran) {
        Stage editDialog = new Stage();
        editDialog.setTitle("✏️ Edit Pengeluaran");
        editDialog.initModality(Modality.WINDOW_MODAL);
        editDialog.initOwner(stage);

        VBox editBox = new VBox(15);
        editBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-spacing: 15;");

        Label title = new Label("✏️ Edit Data Pengeluaran");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #2c3e50;");

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(15);

        Label lblItemName = new Label("Nama Barang:");
        lblItemName.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        TextField txtItemName = new TextField(pengeluaran.getItemName());
        txtItemName.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #bdc3c7; -fx-padding: 8;");

        Label lblQuantity = new Label("Jumlah:");
        lblQuantity.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        TextField txtQuantity = new TextField(String.valueOf(pengeluaran.getQuantity()));
        txtQuantity.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #bdc3c7; -fx-padding: 8;");

        inputGrid.add(lblItemName, 0, 0);
        inputGrid.add(txtItemName, 1, 0);
        inputGrid.add(lblQuantity, 0, 1);
        inputGrid.add(txtQuantity, 1, 1);

        Label lblMessage = new Label();
        lblMessage.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));

        Button btnSave = new Button("💾 Simpan");
        btnSave.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 5; -fx-padding: 10 20;");
        btnSave.setOnAction(e -> {
            String itemName = txtItemName.getText().trim();
            String quantityStr = txtQuantity.getText().trim();

            if (itemName.isEmpty() || quantityStr.isEmpty()) {
                lblMessage.setText("⚠️ Semua field harus diisi.");
                lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            int newQuantity;
            try {
                newQuantity = Integer.parseInt(quantityStr);
                if (newQuantity <= 0) {
                    lblMessage.setText("⚠️ Jumlah harus lebih dari 0.");
                    lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                    return;
                }
            } catch (NumberFormatException ex) {
                lblMessage.setText("⚠️ Jumlah harus berupa angka yang valid.");
                lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            int oldQuantity = pengeluaran.getQuantity();
            int diff = newQuantity - oldQuantity;

            StockItem stockItem = Database.getStockItemByName(itemName);
            if (stockItem == null) {
                lblMessage.setText("❌ Barang tidak ditemukan di stok.");
                lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            int updatedStock = stockItem.getQuantity() - diff;
            if (updatedStock < 0) {
                lblMessage.setText("❌ Jumlah pengeluaran melebihi stok yang tersedia.");
                lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            stockItem.setQuantity(updatedStock);
            Database.updateStockItem(stockItem);

            pengeluaran.setItemName(itemName);
            pengeluaran.setQuantity(newQuantity);
            pengeluaran.setTotalPrice(newQuantity * stockItem.getSellingPrice());

            boolean success = Database.updatePengeluaran(pengeluaran);
            if (success) {
                lblMessage.setText("✅ Pengeluaran berhasil diperbarui.");
                lblMessage.setStyle("-fx-text-fill: #27ae60;");
                
                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(1000);
                        editDialog.close();
                        loadStockData(comboKategori.getValue());
                    } catch (InterruptedException ex) {
                        editDialog.close();
                        loadStockData(comboKategori.getValue());
                    }
                });
            } else {
                lblMessage.setText("❌ Gagal memperbarui pengeluaran.");
                lblMessage.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        Button btnCancel = new Button("❌ Batal");
        btnCancel.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; " +
                          "-fx-background-radius: 5; -fx-padding: 10 20;");
        btnCancel.setOnAction(e -> editDialog.close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(btnSave, btnCancel);

        editBox.getChildren().addAll(title, inputGrid, buttonBox, lblMessage);

        Scene scene = new Scene(editBox, 400, 300);
        editDialog.setScene(scene);
        editDialog.show();
    }

    private void showMessage(String msg, String color) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(5),
                e -> lblMessage.setText("")
            )
        );
        timeline.play();
    }

    public Parent getView() {
        return root;
    }
}