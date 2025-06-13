package pergudangan.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.stage.Stage;
import pergudangan.model.POItem;
import pergudangan.model.PurchaseOrder;
import pergudangan.utils.SceneManager;
import pergudangan.service.Database;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

public class POController {
    private ScrollPane scrollPane;
    private VBox root;
    private TextField txtPONumber, txtSupplier, txtKeterangan;
    private DatePicker datePicker;
    private ComboBox<String> comboStatus;
    private Label lblTotal, lblMessage;
    private TableView<POItem> tableItems;
    private ObservableList<POItem> itemsData;
    private Stage stage;
    private int itemCounter = 1;
    
    private Runnable backToDashboardCallback;
    
    private boolean isEditingExistingPO = false;
    private PurchaseOrder currentEditingPO = null;

    public POController(Stage stage) {
        this.stage = stage;
        createView();
        generatePONumber();
    }
    
    public void setBackToDashboardCallback(Runnable callback) {
        this.backToDashboardCallback = callback;
    }

    private void createView() {
        root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #f8f9fa, #e9ecef);");

        VBox headerSection = createHeaderSection();
        
        VBox formSection = createFormSection();
        
        VBox itemsSection = createItemsSection();
        
        HBox buttonsSection = createButtonsSection();
        
        lblMessage = new Label();
        lblMessage.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        lblMessage.setWrapText(true);

        root.getChildren().addAll(headerSection, formSection, itemsSection, buttonsSection, lblMessage);
        
        scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
    }

    private VBox createHeaderSection() {
        VBox headerBox = new VBox(10);
        headerBox.setPadding(new Insets(20));
        headerBox.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #667eea, #764ba2);" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );

        Label title = new Label("📋 Form Purchase Order");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 2, 0, 1, 1);");

        Label subtitle = new Label("Kelola Purchase Order dengan mudah dan efisien");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web("#e8eaf6"));

        headerBox.getChildren().addAll(title, subtitle);
        return headerBox;
    }

    private VBox createFormSection() {
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(25));
        formBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        Label formTitle = new Label("📝 Informasi Purchase Order");
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        formTitle.setTextFill(Color.web("#2c3e50"));

        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(15, 0, 0, 0));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(120);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        formGrid.getColumnConstraints().addAll(col1, col2);

        txtPONumber = createStyledTextField("Nomor PO akan di-generate otomatis", false);
        txtSupplier = createStyledTextField("Masukkan nama supplier", true);
        txtKeterangan = createStyledTextField("Keterangan tambahan (opsional)", true);
        
        datePicker = new DatePicker(LocalDate.now());
        styleComponent(datePicker);
        
        comboStatus = new ComboBox<>();
        comboStatus.getItems().addAll("📝 Draft", "📤 Dikirim", "📦 Diterima", "❌ Dibatalkan");
        comboStatus.setValue("📝 Draft");
        styleComponent(comboStatus);

        lblTotal = new Label("Total: Rp 0");
        lblTotal.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTotal.setTextFill(Color.web("#27ae60"));
        lblTotal.setStyle(
            "-fx-background-color: #e8f5e8;" +
            "-fx-padding: 10 15;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #27ae60;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;"
        );

        formGrid.add(createLabel("No PO:"), 0, 0);
        formGrid.add(txtPONumber, 1, 0);
        formGrid.add(createLabel("Tanggal:"), 0, 1);
        formGrid.add(datePicker, 1, 1);
        formGrid.add(createLabel("Supplier:"), 0, 2);
        formGrid.add(txtSupplier, 1, 2);
        formGrid.add(createLabel("Status:"), 0, 3);
        formGrid.add(comboStatus, 1, 3);
        formGrid.add(createLabel("Keterangan:"), 0, 4);
        formGrid.add(txtKeterangan, 1, 4);
        formGrid.add(new Label(), 0, 5); 
        formGrid.add(lblTotal, 1, 5);

        formBox.getChildren().addAll(formTitle, formGrid);
        return formBox;
    }

    private VBox createItemsSection() {
        VBox itemsBox = new VBox(15);
        itemsBox.setPadding(new Insets(25));
        itemsBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        Label itemsTitle = new Label("🛒 Daftar Item");
        itemsTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        itemsTitle.setTextFill(Color.web("#2c3e50"));

        tableItems = createStyledTable();
        itemsData = FXCollections.observableArrayList();
        tableItems.setItems(itemsData);

        VBox itemInputSection = createItemInputSection();

        itemsBox.getChildren().addAll(itemsTitle, tableItems, itemInputSection);
        return itemsBox;
    }

    private TableView<POItem> createStyledTable() {
        TableView<POItem> table = new TableView<>();
        table.setPrefHeight(300);
        table.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );

        // Create columns with enhanced styling
        TableColumn<POItem, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(80);
        colId.setStyle("-fx-alignment: CENTER;");

        TableColumn<POItem, String> colNama = new TableColumn<>("Nama Barang");
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNama.setPrefWidth(200);

        TableColumn<POItem, Integer> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colQty.setPrefWidth(80);
        colQty.setStyle("-fx-alignment: CENTER;");

        TableColumn<POItem, String> colSatuan = new TableColumn<>("Satuan");
        colSatuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
        colSatuan.setPrefWidth(100);
        colSatuan.setStyle("-fx-alignment: CENTER;");

        TableColumn<POItem, Double> colHarga = new TableColumn<>("Harga");
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colHarga.setPrefWidth(120);
        colHarga.setCellFactory(tc -> new TableCell<POItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.0f", price));
                }
                setStyle("-fx-alignment: CENTER-RIGHT;");
            }
        });

        TableColumn<POItem, Double> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colSubtotal.setPrefWidth(150);
        colSubtotal.setCellFactory(tc -> new TableCell<POItem, Double>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.0f", subtotal));
                    setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
                }
            }
        });

        table.getColumns().addAll(colId, colNama, colQty, colSatuan, colHarga, colSubtotal);
        return table;
    }

    private VBox createItemInputSection() {
        VBox inputSection = new VBox(15);
        inputSection.setPadding(new Insets(20, 0, 0, 0));

        Label inputTitle = new Label("➕ Tambah Item Baru");
        inputTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        inputTitle.setTextFill(Color.web("#2c3e50"));

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(15);
        inputGrid.setVgap(10);

        TextField txtId = createStyledTextField("ID otomatis", false);
        txtId.setPrefWidth(80);

        TextField txtNama = createStyledTextField("Nama barang", true);
        txtNama.setPrefWidth(200);

        TextField txtQty = createStyledTextField("Jumlah", true);
        txtQty.setPrefWidth(80);

        TextField txtSatuan = createStyledTextField("Satuan", true);
        txtSatuan.setPrefWidth(100);

        TextField txtHarga = createStyledTextField("Harga", true);
        txtHarga.setPrefWidth(120);

        // Add to grid
        inputGrid.add(txtId, 0, 0);
        inputGrid.add(txtNama, 1, 0);
        inputGrid.add(txtQty, 2, 0);
        inputGrid.add(txtSatuan, 3, 0);
        inputGrid.add(txtHarga, 4, 0);

        HBox actionButtons = new HBox(10);
        
        Button btnAddItem = createStyledButton("➕ Tambah", "#27ae60", "#ffffff");
        btnAddItem.setOnAction(e -> addItem(txtId, txtNama, txtQty, txtSatuan, txtHarga));

        Button btnEditItem = createStyledButton("✏️ Edit", "#f39c12", "#ffffff");
        btnEditItem.setOnAction(e -> editItem(txtId, txtNama, txtQty, txtSatuan, txtHarga));

        Button btnDeleteItem = createStyledButton("🗑️ Hapus", "#e74c3c", "#ffffff");
        btnDeleteItem.setOnAction(e -> deleteItem());

        actionButtons.getChildren().addAll(btnAddItem, btnEditItem, btnDeleteItem);

        inputSection.getChildren().addAll(inputTitle, inputGrid, actionButtons);
        return inputSection;
    }

    private HBox createButtonsSection() {
        HBox buttonsBox = new HBox(15);
        buttonsBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnSave = createStyledButton("💾 Simpan PO", "#3498db", "#ffffff");
        btnSave.setPrefWidth(150);
        btnSave.setOnAction(e -> savePO());

        Button btnViewPO = createStyledButton("👁️ Lihat PO", "#9b59b6", "#ffffff");
        btnViewPO.setPrefWidth(150);
        btnViewPO.setOnAction(e -> showSavedPOs());

        Button btnBack = createStyledButton("🏠 Dashboard", "#95a5a6", "#ffffff");
        btnBack.setPrefWidth(150);
        btnBack.setOnAction(e -> SceneManager.showDashboardScene());

        buttonsBox.getChildren().addAll(btnSave, btnViewPO, btnBack);
        return buttonsBox;
    }

    private TextField createStyledTextField(String promptText, boolean editable) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setEditable(editable);
        styleComponent(textField);
        return textField;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.NORMAL, 14));
        label.setTextFill(Color.web("#2c3e50"));
        return label;
    }

    private Button createStyledButton(String text, String backgroundColor, String textColor) {
        Button button = new Button(text);
        button.setStyle(String.format(
            "-fx-background-color: %s;" +
            "-fx-text-fill: %s;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);",
            backgroundColor, textColor
        ));

        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + 
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 3);" +
            "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        
        button.setOnMouseExited(e -> button.setStyle(String.format(
            "-fx-background-color: %s;" +
            "-fx-text-fill: %s;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);" +
            "-fx-scale-x: 1.0; -fx-scale-y: 1.0;",
            backgroundColor, textColor
        )));

        return button;
    }

    private void styleComponent(Control component) {
        component.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #ced4da;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 8 12;" +
            "-fx-font-size: 14px;"
        );

        component.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                component.setStyle(component.getStyle() + 
                    "-fx-border-color: #007bff; -fx-border-width: 2;");
            } else {
                component.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #ced4da;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;" +
                    "-fx-padding: 8 12;" +
                    "-fx-font-size: 14px;"
                );
            }
        });
    }

    private void generatePONumber() {
        if (!isEditingExistingPO) {
            txtPONumber.setText("PO-" + System.currentTimeMillis());
        }
    }

    private void addItem(TextField txtId, TextField txtNama, TextField txtQty, TextField txtSatuan, TextField txtHarga) {
        try {
            if (txtNama.getText().isEmpty() || txtQty.getText().isEmpty() || txtSatuan.getText().isEmpty() || txtHarga.getText().isEmpty()) {
                showMessage("⚠️ Semua data item harus diisi.", "#e74c3c");
                return;
            }

            int qty = Integer.parseInt(txtQty.getText());
            double harga = Double.parseDouble(txtHarga.getText());

            POItem item = new POItem(String.valueOf(itemCounter++), txtNama.getText(), qty, txtSatuan.getText(), harga);
            itemsData.add(item);
            updateTotal();
            clearItemFields(txtId, txtNama, txtQty, txtSatuan, txtHarga);
            showMessage("✅ Item berhasil ditambahkan.", "#27ae60");
        } catch (NumberFormatException ex) {
            showMessage("⚠️ Qty dan Harga harus berupa angka.", "#e74c3c");
        }
    }

    private void editItem(TextField txtId, TextField txtNama, TextField txtQty, TextField txtSatuan, TextField txtHarga) {
        POItem selectedItem = tableItems.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                txtId.setText(selectedItem.getId());
                txtNama.setText(selectedItem.getNama());
                txtQty.setText(String.valueOf(selectedItem.getQty()));
                txtSatuan.setText(selectedItem.getSatuan());
                txtHarga.setText(String.valueOf(selectedItem.getHarga()));

                Button btnConfirmEdit = createStyledButton("✓ Konfirmasi Edit", "#f39c12", "#ffffff");
                btnConfirmEdit.setOnAction(e -> {
                    try {
                        selectedItem.setNama(txtNama.getText());
                        selectedItem.setQty(Integer.parseInt(txtQty.getText()));
                        selectedItem.setSatuan(txtSatuan.getText());
                        selectedItem.setHarga(Double.parseDouble(txtHarga.getText()));
                        tableItems.refresh();
                        updateTotal();
                        clearItemFields(txtId, txtNama, txtQty, txtSatuan, txtHarga);
                        showMessage("✅ Item berhasil diedit.", "#27ae60");
                        ((HBox)btnConfirmEdit.getParent()).getChildren().remove(btnConfirmEdit);
                    } catch (NumberFormatException ex) {
                        showMessage("⚠️ Qty dan Harga harus berupa angka.", "#e74c3c");
                    }
                });

                HBox itemInputBox = (HBox) txtId.getParent().getParent().getChildrenUnmodifiable().get(2);
                if (!itemInputBox.getChildren().contains(btnConfirmEdit)) {
                    itemInputBox.getChildren().add(btnConfirmEdit);
                }
            } catch (Exception ex) {
                showMessage("❌ Gagal mengedit item: " + ex.getMessage(), "#e74c3c");
            }
        } else {
            showMessage("⚠️ Pilih item yang akan diedit.", "#f39c12");
        }
    }

    private void deleteItem() {
        POItem selectedItem = tableItems.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            itemsData.remove(selectedItem);
            updateTotal();
            showMessage("✅ Item berhasil dihapus.", "#27ae60");
        } else {
            showMessage("⚠️ Pilih item yang akan dihapus.", "#f39c12");
        }
    }

    private void clearItemFields(TextField txtId, TextField txtNama, TextField txtQty, TextField txtSatuan, TextField txtHarga) {
        txtId.clear();
        txtNama.clear();
        txtQty.clear();
        txtSatuan.clear();
        txtHarga.clear();
    }

    private void updateTotal() {
        double total = 0;
        for (POItem item : itemsData) {
            total += item.getSubtotal();
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        lblTotal.setText("💰 Total: " + nf.format(total));
    }

    private void showMessage(String message, String color) {
        lblMessage.setText(message);
        lblMessage.setStyle(String.format(
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: %s;" +
            "-fx-background-color: %s20; -fx-padding: 10 15; -fx-background-radius: 6;" +
            "-fx-border-color: %s; -fx-border-width: 1; -fx-border-radius: 6;",
            color, color, color
        ));
    }

    private void savePO() {
        String poNumber = txtPONumber.getText();
        String supplier = txtSupplier.getText();
        LocalDate date = datePicker.getValue();
        String status = comboStatus.getValue();
        String keterangan = txtKeterangan.getText();

        if (supplier == null || supplier.isEmpty()) {
            showMessage("⚠️ Supplier harus diisi.", "#e74c3c");
            return;
        }

        if (date == null) {
            showMessage("⚠️ Tanggal harus dipilih.", "#e74c3c");
            return;
        }

        if (status == null || status.isEmpty()) {
            showMessage("⚠️ Status harus dipilih.", "#e74c3c");
            return;
        }

        if (itemsData == null || itemsData.isEmpty()) {
            showMessage("⚠️ Item PO tidak boleh kosong.", "#e74c3c");
            return;
        }

        double total = 0;
        for (POItem item : itemsData) {
            total += item.getSubtotal();
        }

        String cleanStatus = status.replaceAll("📝 |📤 |📦 |❌ ", "");
        PurchaseOrder po = new PurchaseOrder(poNumber, supplier, date, cleanStatus, keterangan, total, new ArrayList<>(itemsData));

        boolean success;
        if (isEditingExistingPO && currentEditingPO != null) {
            success = Database.updatePurchaseOrder(po);
            if (success) {
                showMessage("✅ PO berhasil diupdate.", "#27ae60");
                showPODetails(po);
                clearForm();
            } else {
                showMessage("❌ Gagal mengupdate PO.", "#e74c3c");
            }
        } else {
            success = Database.savePO(po);
            if (success) {
                showMessage("✅ PO berhasil disimpan.", "#27ae60");
                showPODetails(po);
                clearForm();
            } else {
                showMessage("❌ Gagal menyimpan PO.", "#e74c3c");
            }
        }
    }

    private void showPODetails(PurchaseOrder po) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 Nomor PO: ").append(po.getPoNumber()).append("\n");
        sb.append("🏢 Supplier: ").append(po.getSupplier()).append("\n");
        sb.append("📅 Tanggal: ").append(po.getDate()).append("\n");
        sb.append("📊 Status: ").append(po.getStatus()).append("\n");
        sb.append("📝 Keterangan: ").append(po.getKeterangan()).append("\n");
        sb.append("💰 Total: Rp ").append(String.format("%,.2f", po.getTotal())).append("\n\n");
        sb.append("🛒 Daftar Item:\n");
        for (POItem item : po.getItems()) {
            sb.append("• ").append(item.getNama())
              .append(" | Qty: ").append(item.getQty())
              .append(" ").append(item.getSatuan())
              .append(" | Harga: Rp ").append(String.format("%,.2f", item.getHarga()))
              .append(" | Subtotal: Rp ").append(String.format("%,.2f", item.getSubtotal()))
              .append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("✅ Detail Purchase Order");
        alert.setHeaderText("PO berhasil " + (isEditingExistingPO ? "diupdate" : "disimpan"));
        alert.setContentText(sb.toString());
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-font-family: 'System';");
        
        alert.showAndWait();
    }

    private void showSavedPOs() {
        List<PurchaseOrder> poList = Database.getAllPO();

        if (poList == null || poList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("📊 Data PO");
            alert.setHeaderText(null);
            alert.setContentText("Belum ada data Purchase Order tersimpan.");
            alert.showAndWait();
            return;
        }

        TableView<PurchaseOrder> table = new TableView<>();
        ObservableList<PurchaseOrder> data = FXCollections.observableArrayList(poList);
        table.setItems(data);
        
        table.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #dee2e6;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );

        // Setup columns with enhanced styling
        TableColumn<PurchaseOrder, String> colPoNumber = new TableColumn<>("📋 No PO");
        colPoNumber.setCellValueFactory(new PropertyValueFactory<>("poNumber"));
        colPoNumber.setPrefWidth(150);

        TableColumn<PurchaseOrder, String> colSupplier = new TableColumn<>("🏢 Supplier");
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colSupplier.setPrefWidth(150);

        TableColumn<PurchaseOrder, LocalDate> colDate = new TableColumn<>("📅 Tanggal");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setPrefWidth(120);

        TableColumn<PurchaseOrder, String> colStatus = new TableColumn<>("📊 Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(100);

        TableColumn<PurchaseOrder, Double> colTotal = new TableColumn<>("💰 Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setPrefWidth(150);
        colTotal.setCellFactory(tc -> new TableCell<PurchaseOrder, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.2f", total));
                    setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
                }
            }
        });

        table.getColumns().addAll(colPoNumber, colSupplier, colDate, colStatus, colTotal);

        Stage dialog = new Stage();
        dialog.setTitle("📊 Daftar Purchase Order");

        Button btnEditPO = createStyledButton("✏️ Edit PO", "#f39c12", "#ffffff");
        btnEditPO.setOnAction(e -> {
            PurchaseOrder selectedPO = table.getSelectionModel().getSelectedItem();
            if (selectedPO != null) {
                try {
                    System.out.println("Editing PO: " + selectedPO.getPoNumber());
                    editPO(selectedPO);
                    dialog.close();
                    showMessage("🔄 Mode Edit: " + selectedPO.getPoNumber(), "#f39c12");
                } catch (Exception ex) {
                    System.err.println("Error editing PO: " + ex.getMessage());
                    ex.printStackTrace();
                    
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("❌ Error");
                    errorAlert.setHeaderText("Gagal Edit PO");
                    errorAlert.setContentText("Error: " + ex.getMessage());
                    errorAlert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("⚠️ Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Pilih PO yang akan diedit.");
                alert.showAndWait();
            }
        });

        Button btnDeletePO = createStyledButton("🗑️ Hapus PO", "#e74c3c", "#ffffff");
        btnDeletePO.setOnAction(e -> {
            PurchaseOrder selectedPO = table.getSelectionModel().getSelectedItem();
            if (selectedPO != null) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("⚠️ Konfirmasi Hapus");
                confirmAlert.setHeaderText("Hapus Purchase Order");
                confirmAlert.setContentText("Apakah Anda yakin ingin menghapus PO: " + selectedPO.getPoNumber() + "?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        boolean success = Database.deletePurchaseOrder(selectedPO.getPoNumber());
                        
                        if (success) {
                            data.remove(selectedPO);
                            table.refresh();
                            
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("✅ Berhasil");
                            successAlert.setHeaderText(null);
                            successAlert.setContentText("PO " + selectedPO.getPoNumber() + " berhasil dihapus.");
                            successAlert.showAndWait();
                        } else {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("❌ Error");
                            errorAlert.setHeaderText("Gagal Menghapus PO");
                            errorAlert.setContentText("PO tidak ditemukan atau terjadi kesalahan database.");
                            errorAlert.showAndWait();
                        }
                    } catch (Exception ex) {
                        System.err.println("Error deleting PO: " + ex.getMessage());
                        ex.printStackTrace();
                        
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("❌ Error");
                        errorAlert.setHeaderText("Exception saat menghapus");
                        errorAlert.setContentText("Error: " + ex.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("⚠️ Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Pilih PO yang akan dihapus.");
                alert.showAndWait();
            }
        });

        Button btnEditStatus = createStyledButton("📊 Edit Status", "#3498db", "#ffffff");
        btnEditStatus.setOnAction(e -> {
            PurchaseOrder selectedPO = table.getSelectionModel().getSelectedItem();
            if (selectedPO != null) {
                ComboBox<String> statusComboBox = new ComboBox<>();
                statusComboBox.getItems().addAll("📝 Draft", "📤 Dikirim", "📦 Diterima", "✅ Selesai");
                
                // Set current status with emoji
                String currentStatus = selectedPO.getStatus();
                switch (currentStatus) {
                    case "Draft": statusComboBox.setValue("📝 Draft"); break;
                    case "Dikirim": statusComboBox.setValue("📤 Dikirim"); break;
                    case "Diterima": statusComboBox.setValue("📦 Diterima"); break;
                    case "Selesai": statusComboBox.setValue("✅ Selesai"); break;
                    default: statusComboBox.setValue("📝 Draft");
                }
                
                styleComponent(statusComboBox);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("📊 Edit Status PO");
                alert.setHeaderText("Pilih status baru untuk PO: " + selectedPO.getPoNumber());
                alert.getDialogPane().setContent(statusComboBox);
                alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    String newStatusWithEmoji = statusComboBox.getValue();
                    String newStatus = newStatusWithEmoji.replaceAll("📝 |📤 |📦 |✅ ", "");
                    
                    if (newStatus != null && !newStatus.equals(selectedPO.getStatus())) {
                        String oldStatus = selectedPO.getStatus();
                        selectedPO.setStatus(newStatus);

                        boolean success = Database.updatePurchaseOrder(selectedPO);
                        if (success) {
                            table.refresh();
                            
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("✅ Berhasil");
                            successAlert.setHeaderText(null);
                            successAlert.setContentText("Status PO berhasil diubah menjadi: " + newStatus);
                            successAlert.showAndWait();
                        } else {
                            selectedPO.setStatus(oldStatus);
                            table.refresh();
                            
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("❌ Error");
                            errorAlert.setHeaderText(null);
                            errorAlert.setContentText("Gagal mengubah status PO.");
                            errorAlert.showAndWait();
                        }
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("⚠️ Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Pilih PO yang akan diedit statusnya.");
                alert.showAndWait();
            }
        });

        Button btnClose = createStyledButton("❌ Tutup", "#95a5a6", "#ffffff");
        btnClose.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(15, btnEditPO, btnDeletePO, btnEditStatus, btnClose);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        VBox dialogContent = new VBox(15);
        dialogContent.setPadding(new Insets(20));
        dialogContent.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #f8f9fa, #e9ecef);");
        
        Label dialogTitle = new Label("📊 Daftar Purchase Order");
        dialogTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        dialogTitle.setTextFill(Color.web("#2c3e50"));
        
        dialogContent.getChildren().addAll(dialogTitle, table, buttonBox);

        Scene scene = new Scene(dialogContent, 800, 600);
        dialog.setScene(scene);
        dialog.initOwner(stage);
        dialog.show();
    }

    private void editPO(PurchaseOrder po) {
        System.out.println("Starting edit for PO: " + po.getPoNumber());
        
        isEditingExistingPO = true;
        currentEditingPO = po;
        
        txtPONumber.setText(po.getPoNumber());
        txtSupplier.setText(po.getSupplier());
        datePicker.setValue(po.getDate());

        String status = po.getStatus();
        switch (status) {
            case "Draft": comboStatus.setValue("📝 Draft"); break;
            case "Dikirim": comboStatus.setValue("📤 Dikirim"); break;
            case "Diterima": comboStatus.setValue("📦 Diterima"); break;
            case "Dibatalkan": comboStatus.setValue("❌ Dibatalkan"); break;
            default: comboStatus.setValue("📝 Draft");
        }
        
        txtKeterangan.setText(po.getKeterangan());
        
        itemsData.clear();
        if (po.getItems() != null) {
            itemsData.addAll(po.getItems());
        }
        
        itemCounter = 1;
        for (POItem item : po.getItems()) {
            try {
                int id = Integer.parseInt(item.getId());
                if (id >= itemCounter) {
                    itemCounter = id + 1;
                }
            } catch (NumberFormatException e) {
            }
        }
        
        updateTotal();
        showMessage("🔄 Mode Edit PO: " + po.getPoNumber(), "#f39c12");
        
        System.out.println("Edit setup completed for PO: " + po.getPoNumber());
    }

    private void clearForm() {
        txtPONumber.clear();
        txtSupplier.clear();
        txtKeterangan.clear();
        datePicker.setValue(LocalDate.now());
        comboStatus.setValue("📝 Draft");
        itemsData.clear();
        updateTotal();
        lblMessage.setText("");
        isEditingExistingPO = false;
        currentEditingPO = null;
        itemCounter = 1;
        generatePONumber();
    }

    public Parent getView() {
        return scrollPane;
    }
}