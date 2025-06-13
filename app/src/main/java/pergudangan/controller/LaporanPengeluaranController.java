package pergudangan.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import pergudangan.model.Pengeluaran;
import pergudangan.utils.SceneManager;
import pergudangan.service.Database;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LaporanPengeluaranController {
    private StackPane mainView;
    private VBox contentContainer;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Button btnGenerateReport;
    private TableView<Pengeluaran> reportTable;
    private ObservableList<Pengeluaran> reportData;
    private Label lblMessage;
    private Button btnBackToDashboard;
    private Label totalLabel;
    
    public LaporanPengeluaranController(Stage stage) {
        createView();
        setupAnimations();
    }

    private void createView() {
        mainView = new StackPane();
        mainView.setPrefSize(800, 600); 
        
        Rectangle background = new Rectangle(800, 600);
        LinearGradient bgGradient = new LinearGradient(0, 0, 1, 1, true, null,
            new Stop(0, Color.web("#667eea")),
            new Stop(0.5, Color.web("#764ba2")),
            new Stop(1, Color.web("#f093fb"))
        );
        background.setFill(bgGradient);
        
        Rectangle overlay = new Rectangle(800, 600);
        overlay.setFill(Color.web("#ffffff", 0.05));
        overlay.setEffect(new GaussianBlur(1));
        
        contentContainer = new VBox(15);
        contentContainer.setPadding(new Insets(15));
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.setMaxWidth(750);
        
        VBox header = createHeader();
        
        VBox dateCard = createDateSelectionCard();
        
        HBox buttonBox = createActionButtons();
        
        lblMessage = new Label();
        lblMessage.setFont(Font.font("Inter", FontWeight.MEDIUM, 12)); 
        lblMessage.setWrapText(true);
        lblMessage.setAlignment(Pos.CENTER);
        
        VBox tableCard = createTableCard();
        
        VBox summaryCard = createSummaryCard();
        
        contentContainer.getChildren().addAll(header, dateCard, buttonBox, lblMessage, tableCard, summaryCard);
        
        mainView.getChildren().addAll(background, overlay, contentContainer);
    }
    
    private VBox createHeader() {
        VBox header = new VBox(8); 
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(10));
        
        // Main title
        Label titleLabel = new Label("📊 LAPORAN PENGELUARAN");
        titleLabel.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 24)); 
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.4))); 
        
        // Subtitle
        Label subtitleLabel = new Label("Analisis dan laporan pengeluaran gudang");
        subtitleLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 13)); 
        subtitleLabel.setTextFill(Color.web("#ffffff", 0.9));
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }
    
    private VBox createDateSelectionCard() {
        VBox dateCard = new VBox(12);
        dateCard.setPadding(new Insets(15)); 
        dateCard.setAlignment(Pos.CENTER);
        dateCard.setMaxWidth(400); 

        String cardStyle = """
            -fx-background-color: rgba(255, 255, 255, 0.95);
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);
        """;
        dateCard.setStyle(cardStyle);
        
        Label cardTitle = new Label("📅 Pilih Rentang Tanggal");
        cardTitle.setFont(Font.font("Inter", FontWeight.BOLD, 14)); 
        cardTitle.setTextFill(Color.web("#2c3e50"));
        
        HBox datePickersBox = new HBox(15); 
        datePickersBox.setAlignment(Pos.CENTER);
        
        VBox startDateBox = new VBox(5); 
        startDateBox.setAlignment(Pos.CENTER);
        Label startLabel = new Label("Tanggal Mulai");
        startLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12)); 
        startLabel.setTextFill(Color.web("#34495e"));
        
        startDatePicker = new DatePicker();
        startDatePicker.setPrefWidth(150); 
        startDatePicker.setStyle(getDatePickerStyle());
        
        startDateBox.getChildren().addAll(startLabel, startDatePicker);
   
        VBox endDateBox = new VBox(5); 
        endDateBox.setAlignment(Pos.CENTER);
        Label endLabel = new Label("Tanggal Selesai");
        endLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        endLabel.setTextFill(Color.web("#34495e"));
        
        endDatePicker = new DatePicker();
        endDatePicker.setPrefWidth(150); 
        endDatePicker.setStyle(getDatePickerStyle());
        
        endDateBox.getChildren().addAll(endLabel, endDatePicker);
        
        datePickersBox.getChildren().addAll(startDateBox, endDateBox);
        dateCard.getChildren().addAll(cardTitle, datePickersBox);
        
        return dateCard;
    }
    
    private String getDatePickerStyle() {
        return """
            -fx-background-color: #ffffff;
            -fx-border-color: #e0e0e0;
            -fx-border-width: 1;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-font-family: 'Inter';
            -fx-font-size: 12px;
            -fx-padding: 6;
        """;
    }
    
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(10); 
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5)); 
        
        btnGenerateReport = new Button("📊 Generate Laporan");
        btnGenerateReport.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        btnGenerateReport.setPrefSize(160, 40); 
        
        String generateBtnStyle = """
            -fx-background-color: linear-gradient(to bottom, #3498db, #2980b9);
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.4), 6, 0, 0, 1);
        """;
        
        String generateBtnHoverStyle = """
            -fx-background-color: linear-gradient(to bottom, #2980b9, #1f618d);
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.6), 8, 0, 0, 2);
        """;
        
        btnGenerateReport.setStyle(generateBtnStyle);
        btnGenerateReport.setOnMouseEntered(e -> btnGenerateReport.setStyle(generateBtnHoverStyle));
        btnGenerateReport.setOnMouseExited(e -> btnGenerateReport.setStyle(generateBtnStyle));
        btnGenerateReport.setOnAction(e -> {
            addClickAnimation(btnGenerateReport);
            generateReport();
        });
        
        btnBackToDashboard = new Button("🏠 Kembali ke Dashboard");
        btnBackToDashboard.setFont(Font.font("Inter", FontWeight.BOLD, 13)); 
        btnBackToDashboard.setPrefSize(180, 40); 
        
        String backBtnStyle = """
            -fx-background-color: linear-gradient(to bottom, #27ae60, #229954);
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.4), 6, 0, 0, 1);
        """;
        
        String backBtnHoverStyle = """
            -fx-background-color: linear-gradient(to bottom, #229954, #1e8449);
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.6), 8, 0, 0, 2);
        """;
        
        btnBackToDashboard.setStyle(backBtnStyle);
        btnBackToDashboard.setOnMouseEntered(e -> btnBackToDashboard.setStyle(backBtnHoverStyle));
        btnBackToDashboard.setOnMouseExited(e -> btnBackToDashboard.setStyle(backBtnStyle));
        btnBackToDashboard.setOnAction(e -> {
            addClickAnimation(btnBackToDashboard);
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(200), event -> SceneManager.showDashboardScene())
            );
            timeline.play();
        });
        
        buttonBox.getChildren().addAll(btnGenerateReport, btnBackToDashboard);
        return buttonBox;
    }
    
    private VBox createTableCard() {
        VBox tableCard = new VBox(10); 
        tableCard.setPadding(new Insets(15)); 
        tableCard.setMaxWidth(700); 
        
        String tableCardStyle = """
            -fx-background-color: rgba(255, 255, 255, 0.95);
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);
        """;
        tableCard.setStyle(tableCardStyle);
        
        Label tableTitle = new Label("📋 Data Pengeluaran");
        tableTitle.setFont(Font.font("Inter", FontWeight.BOLD, 14)); 
        tableTitle.setTextFill(Color.web("#2c3e50"));

        reportTable = new TableView<>();
        reportData = FXCollections.observableArrayList();
        reportTable.setItems(reportData);
        reportTable.setPrefHeight(200); 
        reportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
   
        String tableStyle = """
            -fx-background-color: #ffffff;
            -fx-border-color: #e0e0e0;
            -fx-border-width: 1;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-font-size: 11px;
        """;
        reportTable.setStyle(tableStyle);
        
        // Table columns
        TableColumn<Pengeluaran, String> colItemName = new TableColumn<>("Nama Barang");
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colItemName.setMinWidth(150); 
        
        TableColumn<Pengeluaran, Integer> colQuantity = new TableColumn<>("Jumlah");
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQuantity.setMinWidth(80);
        
        TableColumn<Pengeluaran, Double> colTotalPrice = new TableColumn<>("Total Harga");
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colTotalPrice.setMinWidth(120);
        
        TableColumn<Pengeluaran, LocalDate> colDate = new TableColumn<>("Tanggal");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setMinWidth(120); 
        
        // Format date column
        colDate.setCellFactory(column -> new javafx.scene.control.TableCell<Pengeluaran, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });
        

        colTotalPrice.setCellFactory(column -> new javafx.scene.control.TableCell<Pengeluaran, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.2f", item));
                }
            }
        });
        
        reportTable.getColumns().addAll(colItemName, colQuantity, colTotalPrice, colDate);
        
        tableCard.getChildren().addAll(tableTitle, reportTable);
        return tableCard;
    }
    
    private VBox createSummaryCard() {
        VBox summaryCard = new VBox(8); 
        summaryCard.setPadding(new Insets(15)); 
        summaryCard.setAlignment(Pos.CENTER);
        summaryCard.setMaxWidth(300); 
        
        String summaryCardStyle = """
            -fx-background-color: rgba(255, 255, 255, 0.95);
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);
        """;
        summaryCard.setStyle(summaryCardStyle);
        
        Label summaryTitle = new Label("💰 Ringkasan Total");
        summaryTitle.setFont(Font.font("Inter", FontWeight.BOLD, 14)); 
        summaryTitle.setTextFill(Color.web("#2c3e50"));
        
        totalLabel = new Label("Rp 0,00");
        totalLabel.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 18)); 
        totalLabel.setTextFill(Color.web("#e74c3c"));
        
        summaryCard.getChildren().addAll(summaryTitle, totalLabel);
        return summaryCard;
    }
    
    private void setupAnimations() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), contentContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    private void addClickAnimation(Button button) {
        ScaleTransition clickScale = new ScaleTransition(Duration.millis(100), button);
        clickScale.setToX(0.95);
        clickScale.setToY(0.95);
        clickScale.setAutoReverse(true);
        clickScale.setCycleCount(2);
        clickScale.play();
    }

    private void generateReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showMessage("⚠️ Silakan pilih rentang tanggal terlebih dahulu", "#e74c3c");
            return;
        }
        
        if (startDate.isAfter(endDate)) {
            showMessage("⚠️ Tanggal mulai tidak boleh lebih besar dari tanggal selesai", "#e74c3c");
            return;
        }

        List<Pengeluaran> pengeluaranList = Database.getPengeluaranByDateRange(startDate, endDate);
        reportData.clear();
        reportData.addAll(pengeluaranList);

        if (reportData.isEmpty()) {
            showMessage("📋 Tidak ada data pengeluaran untuk rentang tanggal ini", "#f39c12");
            totalLabel.setText("Rp 0,00");
        } else {
            double totalAmount = reportData.stream()
                .mapToDouble(Pengeluaran::getTotalPrice)
                .sum();
            
            showMessage(String.format("✅ Laporan berhasil dihasilkan (%d data ditemukan)", reportData.size()), "#27ae60");
            totalLabel.setText(String.format("Rp %,.2f", totalAmount));
            
         
            ScaleTransition scaleTotal = new ScaleTransition(Duration.millis(300), totalLabel);
            scaleTotal.setFromX(0.8);
            scaleTotal.setFromY(0.8);
            scaleTotal.setToX(1.0);
            scaleTotal.setToY(1.0);
            scaleTotal.play();
        }
    }
    
    private void showMessage(String message, String color) {
        lblMessage.setText(message);
        lblMessage.setStyle(String.format("-fx-text-fill: %s;", color));
        
   
        FadeTransition messageAnimation = new FadeTransition(Duration.millis(300), lblMessage);
        messageAnimation.setFromValue(0);
        messageAnimation.setToValue(1);
        messageAnimation.play();
    }

    public Parent getView() {
        return mainView;
    }
}