package pergudangan.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pergudangan.model.*;
import pergudangan.service.Database;
import pergudangan.utils.SceneManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import java.util.Optional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PenerimaanController {
    // UI Components
    private VBox root;
    private TextField txtNoTerima, txtPONumber, txtSupplier, txtKeterangan;
    private DatePicker datePicker;
    private Label lblPOInfo, lblMessage;
    private TableView<PenerimaanItem> tablePenerimaan;
    private ObservableList<PenerimaanItem> penerimaanData;
    
    // Business Logic
    private Stage stage;
    private PurchaseOrder selectedPO;
    private Penerimaan editingPenerimaan;
    private TableView<Penerimaan> penerimaanTable;
    
    // Styling Constants
    private static final String PRIMARY_COLOR = "#2E7D32";
    private static final String SECONDARY_COLOR = "#1976D2";
    private static final String SUCCESS_COLOR = "#388E3C";
    private static final String WARNING_COLOR = "#F57C00";
    private static final String ERROR_COLOR = "#D32F2F";
    private static final String NEUTRAL_COLOR = "#616161";
    
    public PenerimaanController(Stage stage) {
        this.stage = stage;
        createView();
        generateNoTerima();
    }

    private void createView() {
        root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #FAFAFA;");

        VBox mainContent = createMainContent();
        root.getChildren().add(mainContent);
    }

    private VBox createMainContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10;");
        content.setPadding(new Insets(20));
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        shadow.setOffsetX(0);
        shadow.setOffsetY(2);
        shadow.setRadius(6);
        content.setEffect(shadow);

        VBox header = createHeader();
        VBox formSection = createFormSection();
        VBox infoSection = createInfoSection();
        VBox tableSection = createTableSection();
        HBox buttonSection = createButtonSection();
        VBox messageSection = createMessageSection();

        content.getChildren().addAll(header, formSection, infoSection, tableSection, buttonSection, messageSection);
        return content;
    }

    private VBox createHeader() {
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        
        Label title = new Label("Form Penerimaan Barang");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        
        Label subtitle = new Label("Kelola penerimaan barang dari supplier");
        subtitle.setFont(Font.font("System", 12));
        subtitle.setStyle("-fx-text-fill: " + NEUTRAL_COLOR + ";");
        
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #E0E0E0;");
        separator.setPrefWidth(180);
        
        header.getChildren().addAll(title, subtitle, separator);
        return header;
    }

    private VBox createFormSection() {
        VBox formSection = new VBox(10);
        formSection.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 8; -fx-border-color: #E0E0E0; -fx-border-radius: 8;");
        formSection.setPadding(new Insets(15));

        Label formTitle = new Label("Informasi Penerimaan");
        formTitle.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        formTitle.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        initializeFormFields();

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.TOP_LEFT);

        addFormFieldsToGrid(formGrid);

        formSection.getChildren().addAll(formTitle, formGrid);
        return formSection;
    }

    private void initializeFormFields() {
        txtNoTerima = createStyledTextField("Nomor Penerimaan", false);
        txtPONumber = createStyledTextField("Nomor PO", false);
        txtSupplier = createStyledTextField("Supplier", false);
        txtKeterangan = createStyledTextField("Catatan Penerimaan", true);
        
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle(getTextFieldStyle());
        datePicker.setPrefWidth(180);
    }

    private TextField createStyledTextField(String promptText, boolean editable) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setEditable(editable);
        textField.setStyle(getTextFieldStyle());
        textField.setPrefWidth(180);
        return textField;
    }

    private String getTextFieldStyle() {
        return "-fx-background-color: white; " +
               "-fx-border-color: #BDBDBD; " +
               "-fx-border-radius: 4; " +
               "-fx-background-radius: 4; " +
               "-fx-padding: 6; " +
               "-fx-font-size: 12;";
    }

    private void addFormFieldsToGrid(GridPane grid) {
        grid.add(createFormLabel("No Penerimaan:"), 0, 0);
        grid.add(txtNoTerima, 1, 0);
        
        grid.add(createFormLabel("Tanggal:"), 2, 0);
        grid.add(datePicker, 3, 0);
        
        grid.add(createFormLabel("No PO:"), 0, 1);
        grid.add(txtPONumber, 1, 1);
        
        grid.add(createFormLabel("Supplier:"), 2, 1);
        grid.add(txtSupplier, 3, 1);
        
        grid.add(createFormLabel("Catatan:"), 0, 2);
        grid.add(txtKeterangan, 1, 2, 3, 1);
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.NORMAL, 12));
        label.setStyle("-fx-text-fill: #424242;");
        label.setPrefWidth(90);
        return label;
    }

    private VBox createInfoSection() {
        VBox infoSection = new VBox(5);
        
        lblPOInfo = new Label("Pilih PO untuk memulai penerimaan");
        lblPOInfo.setFont(Font.font("System", FontWeight.NORMAL, 12));
        lblPOInfo.setStyle("-fx-text-fill: " + NEUTRAL_COLOR + "; -fx-background-color: #F5F5F5; -fx-padding: 10; -fx-background-radius: 6;");
        
        infoSection.getChildren().add(lblPOInfo);
        return infoSection;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox(10);
        
        Label tableTitle = new Label("Detail Item Penerimaan");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        tableTitle.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        
        createTablePenerimaan();
        
        tableSection.getChildren().addAll(tableTitle, tablePenerimaan);
        return tableSection;
    }

    private void createTablePenerimaan() {
        tablePenerimaan = new TableView<>();
        penerimaanData = FXCollections.observableArrayList();
        tablePenerimaan.setItems(penerimaanData);
        tablePenerimaan.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 6;");
        tablePenerimaan.setPrefHeight(250);

        createTableColumns();
        
        tablePenerimaan.setEditable(true);
        tablePenerimaan.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void createTableColumns() {
        TableColumn<PenerimaanItem, String> colNama = new TableColumn<>("Nama Barang");
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        colNama.setPrefWidth(180);

        TableColumn<PenerimaanItem, Integer> colQtyPO = new TableColumn<>("Qty PO");
        colQtyPO.setCellValueFactory(new PropertyValueFactory<>("qtyPO"));
        colQtyPO.setPrefWidth(70);

        TableColumn<PenerimaanItem, Integer> colQtyTerima = new TableColumn<>("Qty Diterima");
        colQtyTerima.setCellValueFactory(new PropertyValueFactory<>("qtyDiterima"));
        colQtyTerima.setPrefWidth(80);
        colQtyTerima.setCellFactory(col -> createEditableQuantityCell());

        TableColumn<PenerimaanItem, String> colSatuan = new TableColumn<>("Satuan");
        colSatuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
        colSatuan.setPrefWidth(70);

        TableColumn<PenerimaanItem, BigDecimal> colHarga = new TableColumn<>("Harga");
        colHarga.setCellValueFactory(new PropertyValueFactory<>("hargaSatuan"));
        colHarga.setPrefWidth(90);
        colHarga.setCellFactory(col -> createCurrencyCell());

        TableColumn<PenerimaanItem, BigDecimal> colTotalHarga = new TableColumn<>("Total Harga");
        colTotalHarga.setCellValueFactory(new PropertyValueFactory<>("totalHarga"));
        colTotalHarga.setPrefWidth(100);
        colTotalHarga.setCellFactory(col -> createCurrencyCell());

        tablePenerimaan.getColumns().addAll(colNama, colQtyPO, colQtyTerima, colSatuan, colHarga, colTotalHarga);
    }

    private TableCell<PenerimaanItem, Integer> createEditableQuantityCell() {
        return new TableCell<PenerimaanItem, Integer>() {
            private TextField textField;

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    if (textField == null) {
                        textField = new TextField();
                        textField.setStyle("-fx-background-radius: 4; -fx-border-radius: 4;");
                        textField.setOnAction(e -> commitQuantityEdit());
                        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                            if (!isNowFocused) {
                                commitQuantityEdit();
                            }
                        });
                    }
                    textField.setText(item != null ? item.toString() : "0");
                    setGraphic(textField);
                }
            }

            private void commitQuantityEdit() {
                try {
                    int newValue = Integer.parseInt(textField.getText());
                    commitEdit(newValue);
                    updateItemCondition(newValue);
                } catch (NumberFormatException ex) {
                    cancelEdit();
                }
            }

            private void updateItemCondition(int newValue) {
                PenerimaanItem item = getTableRow().getItem();
                if (item != null && newValue >= 0 && newValue <= item.getQtyPO()) {
                    item.setQtyDiterima(newValue);
                    item.setKondisi(determineCondition(newValue, item.getQtyPO()));
                    tablePenerimaan.refresh();
                }
            }

            private String determineCondition(int received, int ordered) {
                if (received == 0) return "Tidak Diterima";
                if (received < ordered) return "Sebagian";
                return "Lengkap";
            }
        };
    }

    private TableCell<PenerimaanItem, BigDecimal> createCurrencyCell() {
        return new TableCell<PenerimaanItem, BigDecimal>() {
            private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(item));
                }
            }
        };
    }

    private HBox createButtonSection() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 10, 0));

        Button btnPilihPO = createStyledButton("Pilih PO", SECONDARY_COLOR, e -> showPOList());
        Button btnSimpan = createStyledButton("Simpan", SUCCESS_COLOR, e -> simpanPenerimaan());
        Button btnLihatPenerimaan = createStyledButton("Lihat", WARNING_COLOR, e -> showPenerimaanList());
        Button btnBatal = createStyledButton("Batal", NEUTRAL_COLOR, e -> batalkanPenerimaan());
        Button btnKembali = createStyledButton("Kembali", ERROR_COLOR, e -> SceneManager.showDashboardScene());

        buttonBox.getChildren().addAll(btnPilihPO, btnSimpan, btnLihatPenerimaan, btnBatal, btnKembali);
        return buttonBox;
    }

    private Button createStyledButton(String text, String color, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 8 15; " +
            "-fx-font-size: 12; " +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-opacity: 0.9;"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle().replace("-fx-opacity: 0.9;", "")));
        
        button.setOnAction(action);
        return button;
    }

    private VBox createMessageSection() {
        VBox messageSection = new VBox();
        messageSection.setAlignment(Pos.CENTER);
        
        lblMessage = new Label();
        lblMessage.setFont(Font.font("System", FontWeight.NORMAL, 12));
        lblMessage.setStyle("-fx-padding: 8; -fx-background-radius: 4;");
        
        messageSection.getChildren().add(lblMessage);
        return messageSection;
    }

    private void generateNoTerima() {
        txtNoTerima.setText("TR-" + System.currentTimeMillis());
    }


    private void showPOList() {
        List<PurchaseOrder> poList = Database.getPOByStatus("Dikirim");

        if (poList == null || poList.isEmpty()) {
            showAlert("Info", "Tidak ada PO dengan status 'Dikirim' yang dapat diterima.", Alert.AlertType.INFORMATION);
            return;
        }

        Stage dialog = createPOSelectionDialog(poList);
        dialog.show();
    }

    private Stage createPOSelectionDialog(List<PurchaseOrder> poList) {
        TableView<PurchaseOrder> table = createPOTable(poList);
        
        Stage dialog = new Stage();
        dialog.setTitle("Pilih Purchase Order");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(stage);

        Button btnPilih = createStyledButton("Pilih PO", SUCCESS_COLOR, e -> {
            PurchaseOrder selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadPOForReceiving(selected);
                dialog.close();
            } else {
                showAlert("Peringatan", "Pilih PO terlebih dahulu.", Alert.AlertType.WARNING);
            }
        });

        Button btnBatal = createStyledButton("Batal", NEUTRAL_COLOR, e -> dialog.close());

        HBox buttonBox = new HBox(10, btnPilih, btnBatal);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        VBox vbox = new VBox(15, table, buttonBox);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(vbox, 700, 450);
        dialog.setScene(scene);
        return dialog;
    }

    private TableView<PurchaseOrder> createPOTable(List<PurchaseOrder> poList) {
        TableView<PurchaseOrder> table = new TableView<>();
        ObservableList<PurchaseOrder> data = FXCollections.observableArrayList(poList);
        table.setItems(data);
        table.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0;");

        TableColumn<PurchaseOrder, String> colPoNumber = new TableColumn<>("No PO");
        colPoNumber.setCellValueFactory(new PropertyValueFactory<>("poNumber"));
        colPoNumber.setPrefWidth(150);

        TableColumn<PurchaseOrder, String> colSupplier = new TableColumn<>("Supplier");
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colSupplier.setPrefWidth(150);

        TableColumn<PurchaseOrder, LocalDate> colDate = new TableColumn<>("Tanggal");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setPrefWidth(100);

        TableColumn<PurchaseOrder, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setPrefWidth(120);
        colTotal.setCellFactory(tc -> new TableCell<PurchaseOrder, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                setText(empty || total == null ? null : String.format("Rp %,.2f", total));
            }
        });

        table.getColumns().addAll(colPoNumber, colSupplier, colDate, colTotal);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private void loadPOForReceiving(PurchaseOrder po) {
        selectedPO = po;
        editingPenerimaan = null;
        
        txtPONumber.setText(po.getPoNumber());
        txtSupplier.setText(po.getSupplier());
        
        updatePOInfoLabel(po);
        loadPOItems(po);
        
        txtKeterangan.setText("");
        datePicker.setValue(LocalDate.now());
        showMessage("PO berhasil dimuat. Silakan periksa dan sesuaikan qty diterima.", SUCCESS_COLOR);
    }

    private void updatePOInfoLabel(PurchaseOrder po) {
        lblPOInfo.setText("PO dipilih: " + po.getPoNumber() + " | Total: Rp " + String.format("%,.2f", po.getTotal()));
        lblPOInfo.setStyle("-fx-text-fill: " + SUCCESS_COLOR + "; -fx-font-weight: bold; -fx-background-color: #E8F5E8; -fx-padding: 12; -fx-background-radius: 6;");
    }

    private void loadPOItems(PurchaseOrder po) {
        penerimaanData.clear();
        for (POItem item : po.getItems()) {
            PenerimaanItem penerimaanItem = new PenerimaanItem(
                item.getNama(),
                item.getQty(),
                0,
                item.getSatuan(),
                "",
                ""
            );

            penerimaanItem.setHargaSatuan(BigDecimal.valueOf(item.getHarga()));
            penerimaanItem.setTotalHarga(BigDecimal.valueOf(item.getSubtotal()));
            penerimaanData.add(penerimaanItem);
        }
    }

    private void simpanPenerimaan() {
        if (!validatePenerimaan()) return;

        Penerimaan penerimaan = createPenerimaanFromForm();
        
        if (Database.savePenerimaan(penerimaan)) {
            updatePOStatus();
            updateStockFromPenerimaan(penerimaan);
            showMessage("Penerimaan berhasil disimpan dan stok diperbarui.", SUCCESS_COLOR);
            resetForm();
        } else {
            showMessage("Gagal menyimpan penerimaan. Coba lagi.", ERROR_COLOR);
        }
    }

    private boolean validatePenerimaan() {
        if (selectedPO == null || penerimaanData.isEmpty()) {
            showMessage("Pastikan PO dipilih dan ada item yang diterima.", ERROR_COLOR);
            return false;
        }
        return true;
    }

    private Penerimaan createPenerimaanFromForm() {
        Penerimaan penerimaan = new Penerimaan();
        penerimaan.setNoTerima(txtNoTerima.getText());
        penerimaan.setNoPO(selectedPO.getPoNumber());
        penerimaan.setSupplier(txtSupplier.getText());
        penerimaan.setTanggal(datePicker.getValue());
        penerimaan.setCatatan(txtKeterangan.getText());
        penerimaan.setItems(new ArrayList<>(penerimaanData));
        return penerimaan;
    }

    private void updatePOStatus() {
        selectedPO.setStatus("Diterima");
        Database.updatePurchaseOrder(selectedPO);
    }

    private void updateStockFromPenerimaan(Penerimaan penerimaan) {
        for (PenerimaanItem item : penerimaan.getItems()) {
            if (item.getQtyDiterima() > 0) {
                updateItemStock(item);
            }
        }
    }

    private void updateItemStock(PenerimaanItem item) {
        BigDecimal hargaSatuan = item.getHargaSatuan();
        if (hargaSatuan == null) {
            showMessage("Harga satuan belum diisi untuk item " + item.getNamaBarang(), ERROR_COLOR);
            return;
        }

        double sellingPrice = hargaSatuan.doubleValue() + 1000;
        
        boolean stockUpdated = Database.incrementStock(
            item.getNamaBarang(),
            item.getQtyDiterima(),
            item.getSatuan(),
            hargaSatuan.doubleValue(),
            sellingPrice,
            null
        );

        if (!stockUpdated) {
            showMessage("Gagal memperbarui stok untuk " + item.getNamaBarang(), ERROR_COLOR);
        }
    }

    private void resetForm() {
        generateNoTerima();
        datePicker.setValue(LocalDate.now());
        txtPONumber.clear();
        txtSupplier.clear();
        txtKeterangan.clear();
        penerimaanData.clear();
        selectedPO = null;
        editingPenerimaan = null;
        
        lblPOInfo.setText("Pilih PO untuk memulai penerimaan");
        lblPOInfo.setStyle("-fx-text-fill: " + NEUTRAL_COLOR + "; -fx-background-color: #F5F5F5; -fx-padding: 12; -fx-background-radius: 6;");
        lblMessage.setText("");
    }

    private void showPenerimaanList() {
        List<Penerimaan> penerimaanList = Database.getAllPenerimaan();

        if (penerimaanList == null || penerimaanList.isEmpty()) {
            showAlert("Info", "Belum ada data penerimaan.", Alert.AlertType.INFORMATION);
            return;
        }

        Stage dialog = createPenerimaanListDialog(penerimaanList);
        dialog.show();
    }

    private Stage createPenerimaanListDialog(List<Penerimaan> penerimaanList) {
        TableView<Penerimaan> table = createPenerimaanTable(penerimaanList);
        
        Stage dialog = new Stage();
        dialog.setTitle("Daftar Penerimaan");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(stage);

        Button btnEdit = createStyledButton("Edit Penerimaan", SECONDARY_COLOR, e -> {
            Penerimaan selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                editPenerimaan(selected);
                dialog.close();
            } else {
                showAlert("Peringatan", "Pilih penerimaan untuk diedit.", Alert.AlertType.WARNING);
            }
        });

        Button btnTutup = createStyledButton("Tutup", NEUTRAL_COLOR, e -> dialog.close());

        HBox buttonBox = new HBox(10, btnEdit, btnTutup);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        VBox vbox = new VBox(15, table, buttonBox);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(vbox, 750, 550);
        dialog.setScene(scene);
        return dialog;
    }

    private TableView<Penerimaan> createPenerimaanTable(List<Penerimaan> penerimaanList) {
        TableView<Penerimaan> table = new TableView<>();
        ObservableList<Penerimaan> data = FXCollections.observableArrayList(penerimaanList);
        table.setItems(data);
        table.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0;");

        TableColumn<Penerimaan, String> colNoTerima = new TableColumn<>("No Penerimaan");
        colNoTerima.setCellValueFactory(new PropertyValueFactory<>("noTerima"));
        colNoTerima.setPrefWidth(150);

        TableColumn<Penerimaan, String> colNoPO = new TableColumn<>("No PO");
        colNoPO.setCellValueFactory(new PropertyValueFactory<>("noPO"));
        colNoPO.setPrefWidth(150);

        TableColumn<Penerimaan, String> colSupplier = new TableColumn<>("Supplier");
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colSupplier.setPrefWidth(150);

        TableColumn<Penerimaan, LocalDate> colTanggal = new TableColumn<>("Tanggal");
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colTanggal.setPrefWidth(100);

        table.getColumns().addAll(colNoTerima, colNoPO, colSupplier, colTanggal);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

   private void editPenerimaan(Penerimaan penerimaan) {
        editingPenerimaan = penerimaan;
        selectedPO = null;

        txtNoTerima.setText(penerimaan.getNoTerima());
        txtPONumber.setText(penerimaan.getNoPO());
        txtSupplier.setText(penerimaan.getSupplier());
        txtKeterangan.setText(penerimaan.getCatatan());
        datePicker.setValue(penerimaan.getTanggal());

        loadPenerimaanItems(penerimaan);

        lblPOInfo.setText("Edit Penerimaan No: " + penerimaan.getNoTerima());
        lblPOInfo.setStyle("-fx-text-fill: " + SECONDARY_COLOR + "; -fx-font-weight: bold; -fx-background-color: #E3F2FD; -fx-padding: 12; -fx-background-radius: 6;");

        showMessage("Edit penerimaan dimuat. Anda dapat mengubah qty diterima.", WARNING_COLOR);
    }

    private void loadPenerimaanItems(Penerimaan penerimaan) {
        penerimaanData.clear();
        penerimaanData.addAll(penerimaan.getItems());
    }

    private void batalkanPenerimaan() {
        if (selectedPO != null || editingPenerimaan != null) {
            Optional<ButtonType> result = showConfirmationDialog(
                "Konfirmasi Pembatalan",
                "Apakah Anda yakin ingin membatalkan penerimaan ini? Semua data yang belum disimpan akan hilang."
            );

            if (result.isPresent() && result.get() == ButtonType.OK) {
                resetForm();
                showMessage("Penerimaan dibatalkan.", WARNING_COLOR);
            }
        } else {
            showMessage("Tidak ada penerimaan yang sedang diproses.", NEUTRAL_COLOR);
        }
    }

    private Optional<ButtonType> showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        return alert.showAndWait();
    }

    private void showMessage(String message, String color) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: white; -fx-background-color: " + color + "; -fx-padding: 10; -fx-background-radius: 6;");
        
        // Auto-hide message after 5 seconds
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(5), e -> {
                lblMessage.setText("");
                lblMessage.setStyle("");
            })
        );
        timeline.play();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(stage);
        alert.showAndWait();
    }

    public Parent getView() {
        return root;
    }

    public void setPenerimaanTable(TableView<Penerimaan> penerimaanTable) {
        this.penerimaanTable = penerimaanTable;
    }

    private void refreshMainTable() {
        if (penerimaanTable != null) {
            List<Penerimaan> updatedList = Database.getAllPenerimaan();
            if (updatedList != null) {
                penerimaanTable.getItems().clear();
                penerimaanTable.getItems().addAll(updatedList);
            }
        }
    }

    // Additional utility methods for better user experience
    private void setupKeyboardShortcuts() {
        root.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case F1:
                    showPOList();
                    break;
                case F2:
                    if (validatePenerimaan()) {
                        simpanPenerimaan();
                    }
                    break;
                case F3:
                    showPenerimaanList();
                    break;
                case ESCAPE:
                    batalkanPenerimaan();
                    break;
                default:
                    break;
            }
        });
    }

    private void recalculateItemTotals() {
        for (PenerimaanItem item : penerimaanData) {
            if (item.getHargaSatuan() != null) {
                BigDecimal newTotal = item.getHargaSatuan().multiply(BigDecimal.valueOf(item.getQtyDiterima()));
                item.setTotalHarga(newTotal);
            }
        }
        tablePenerimaan.refresh();
    }

    private boolean validateItemQuantities() {
        for (PenerimaanItem item : penerimaanData) {
            if (item.getQtyDiterima() > item.getQtyPO()) {
                showAlert("Validasi Error", 
                    "Qty diterima untuk item '" + item.getNamaBarang() + 
                    "' melebihi qty PO (" + item.getQtyPO() + ")", 
                    Alert.AlertType.ERROR);
                return false;
            }
        }
        return true;
    }

    private String getReceivingSummary() {
        int totalItems = penerimaanData.size();
        int receivedItems = (int) penerimaanData.stream()
            .filter(item -> item.getQtyDiterima() > 0)
            .count();
        
        return String.format("Total: %d item | Diterima: %d item", totalItems, receivedItems);
    }

    private void exportPenerimaanData(Penerimaan penerimaan) {
        showMessage("Fitur export akan segera tersedia.", NEUTRAL_COLOR);
    }

    public void cleanup() {
        if (stage != null) {
            stage.close();
        }
        penerimaanData.clear();
        selectedPO = null;
        editingPenerimaan = null;
    }
    
}





