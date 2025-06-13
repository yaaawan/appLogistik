package pergudangan.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import pergudangan.utils.SceneManager;
import javafx.application.Platform;

public class DashboardController {
    
    private StackPane view;
    private VBox mainContent;
    
    public DashboardController() {
        createView();
        addAnimations();
    }
    
    private void createView() {
        view = new StackPane();
        view.setPrefSize(900, 650);
        view.setMinSize(900, 650);
        
        Rectangle background = new Rectangle(900, 650);
        LinearGradient bgGradient = new LinearGradient(0, 0, 1, 1, true, null,
            new Stop(0, Color.web("#667eea")),
            new Stop(0.5, Color.web("#764ba2")),
            new Stop(1, Color.web("#f093fb"))
        );
        background.setFill(bgGradient);

        Rectangle overlay = new Rectangle(900, 650);
        overlay.setFill(Color.web("#ffffff", 0.1));
        overlay.setEffect(new GaussianBlur(1));
 
        mainContent = new VBox(15);
        mainContent.setPadding(new Insets(25, 40, 25, 40));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setMaxWidth(850);

        VBox header = createHeader();
        GridPane navigationGrid = createNavigationGrid();    
        HBox footer = createFooter();
        mainContent.getChildren().addAll(header, navigationGrid, footer);
        view.getChildren().addAll(background, overlay, mainContent);
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(5));
        
        Label titleLabel = new Label("🏭 WAREHOUSE MANAGEMENT");
        titleLabel.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.4)));
        
        Label subtitleLabel = new Label("Kelola gudang dengan efisien");
        subtitleLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        subtitleLabel.setTextFill(Color.web("#ffffff", 0.9));
        
        Separator separator = new Separator();
        separator.setMaxWidth(250);
        separator.setPrefHeight(2);
        separator.setStyle("-fx-background-color: rgba(255,255,255,0.4);");
        
        header.getChildren().addAll(titleLabel, subtitleLabel, separator);
        return header;
    }
    
    private GridPane createNavigationGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(15, 0, 15, 0));
        
        Button receiveBtn = createModernCard("📥", "Penerimaan", "Barang Masuk", 
            "Kelola dan catat semua barang yang masuk ke gudang", 
            "#4CAF50", () -> SceneManager.showPenerimaanScene());
            
        Button userBtn = createModernCard("👥", "Manajemen", "Pengguna", 
            "Atur akses pengguna dan hak akses sistem", 
            "#2196F3", () -> SceneManager.showUserManagementScene());
            
        Button poBtn = createModernCard("📋", "Purchase", "Order", 
            "Buat dan kelola pesanan pembelian barang", 
            "#FF9800", () -> SceneManager.showPOScene());
            
        Button outBtn = createModernCard("📤", "Pengeluaran", "Barang", 
            "Catat dan monitor barang yang keluar dari gudang", 
            "#F44336", () -> SceneManager.showPengeluaranScene());
            
        Button stockBtn = createModernCard("📦", "Manajemen", "Stok", 
            "Pantau dan kelola inventori barang secara real-time", 
            "#9C27B0", () -> SceneManager.showStockScene());
            
        Button reportBtn = createModernCard("📊", "Laporan", "Pengeluaran", 
            "Analisis dan laporan komprehensif operasional", 
            "#00BCD4", () -> SceneManager.showLaporanScene());
        
        // Arrange in 3x2 grid
        grid.add(receiveBtn, 0, 0);
        grid.add(userBtn, 1, 0);
        grid.add(poBtn, 2, 0);
        grid.add(outBtn, 0, 1);
        grid.add(stockBtn, 1, 1);
        grid.add(reportBtn, 2, 1);
        
        return grid;
    }
    
    private Button createModernCard(String icon, String title, String subtitle, 
                                   String description, String accentColor, Runnable action) {
        Button card = new Button();
        card.setPrefSize(250, 140);
        card.setMinSize(250, 140);
        card.setMaxSize(250, 140);
        card.setGraphicTextGap(0);
        
        // Card content
        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(12));
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI Emoji", 24));
        
        // Title and subtitle
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Inter", FontWeight.BOLD, 13));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 12));
        subtitleLabel.setTextFill(Color.web("#34495e"));
        
        // Description
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Inter", FontWeight.NORMAL, 10));
        descLabel.setTextFill(Color.web("#7f8c8d"));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(220);
        descLabel.setStyle("-fx-text-alignment: center;");
        
        // Accent line
        Rectangle accentLine = new Rectangle(40, 2);
        accentLine.setFill(Color.web(accentColor));
        accentLine.setArcWidth(1);
        accentLine.setArcHeight(1);
        
        content.getChildren().addAll(iconLabel, titleLabel, subtitleLabel, 
                                   descLabel, accentLine);
        card.setGraphic(content);
        
        // Base styling
        String baseStyle = String.format("""
            -fx-background-color: rgba(255, 255, 255, 0.95);
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-color: rgba(255, 255, 255, 0.8);
            -fx-border-width: 1;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 1);
        """);
        
        String hoverStyle = String.format("""
            -fx-background-color: rgba(255, 255, 255, 1.0);
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-border-color: %s;
            -fx-border-width: 2;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);
        """, accentColor);
        
        card.setStyle(baseStyle);
        
        // Hover animations
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), card);
        scaleIn.setToX(1.03);
        scaleIn.setToY(1.03);
        
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), card);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        
        card.setOnMouseEntered(e -> {
            card.setStyle(hoverStyle);
            scaleIn.play();
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(baseStyle);
            scaleOut.play();
        });
        
        // Action
        card.setOnAction(e -> {
            addClickAnimation(card);
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(200), event -> action.run())
            );
            timeline.play();
        });
        
        // Tooltip
        Tooltip tooltip = new Tooltip(description);
        tooltip.setFont(Font.font("Inter", 12));
        tooltip.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6;");
        Tooltip.install(card, tooltip);
        
        return card;
    }
    
    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15, 0, 0, 0));
        
        // Exit button
        Button exitBtn = new Button("🚪 Keluar");
        exitBtn.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        exitBtn.setPrefWidth(150);
        exitBtn.setPrefHeight(40);
        exitBtn.setMinSize(150, 40);
        exitBtn.setMaxSize(150, 40);
        
        String exitBaseStyle = """
            -fx-background-color: rgba(244, 67, 54, 0.9);
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(244,67,54,0.4), 6, 0, 0, 1);
        """;
        
        String exitHoverStyle = """
            -fx-background-color: rgba(244, 67, 54, 1.0);
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(244,67,54,0.6), 8, 0, 0, 2);
        """;
        
        exitBtn.setStyle(exitBaseStyle);
        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle(exitHoverStyle));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle(exitBaseStyle));
        exitBtn.setOnAction(e -> {
            addClickAnimation(exitBtn);
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(200), event -> Platform.exit())
            );
            timeline.play();
        });
        
        footer.getChildren().add(exitBtn);
        return footer;
    }
    
    private void addAnimations() {
        // Fade in animation for main content
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainContent);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        // Slide in animation for cards
        Timeline slideIn = new Timeline();
        slideIn.getKeyFrames().add(
            new KeyFrame(Duration.millis(300), e -> {
                mainContent.getChildren().forEach(node -> {
                    if (node instanceof GridPane) {
                        ((GridPane) node).getChildren().forEach(card -> {
                            card.setOpacity(0);
                            card.setTranslateY(20);
                            
                            FadeTransition cardFade = new FadeTransition(Duration.millis(500), card);
                            cardFade.setFromValue(0);
                            cardFade.setToValue(1);
                            
                            Timeline cardSlide = new Timeline(
                                new KeyFrame(Duration.millis(500), 
                                    new javafx.animation.KeyValue(card.translateYProperty(), 0))
                            );
                            
                            cardFade.play();
                            cardSlide.play();
                        });
                    }
                });
            })
        );
        slideIn.play();
    }
    
    private void addClickAnimation(Button button) {
        ScaleTransition clickScale = new ScaleTransition(Duration.millis(100), button);
        clickScale.setToX(0.95);
        clickScale.setToY(0.95);
        clickScale.setAutoReverse(true);
        clickScale.setCycleCount(2);
        clickScale.play();
    }
    
    public StackPane getView() {
        return view;
    }
}