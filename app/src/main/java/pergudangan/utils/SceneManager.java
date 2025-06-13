package pergudangan.utils;

import pergudangan.controller.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        if (primaryStage != null) {
            throw new IllegalStateException("Primary stage sudah pernah di-set!");
        }
        if (stage == null) {
            throw new IllegalArgumentException("Stage tidak boleh null.");
        }
        primaryStage = stage;
        showLoginScene();
    }

    private static void setSceneFullScreen(Parent view, String title) {
        if (primaryStage == null) {
            throw new IllegalStateException("PrimaryStage belum di-set. Panggil setStage(Stage) terlebih dahulu.");
        }
        if (view == null) {
            System.err.println("View untuk scene '" + title + "' adalah null. Tidak dapat menampilkan scene.");
            return;
        }
        Scene scene = new Scene(view);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    // Versi fixed size (optional)
    private static void setSceneSafely(Parent view, String title, double width, double height) {
        if (primaryStage == null) {
            throw new IllegalStateException("PrimaryStage belum di-set. Panggil setStage(Stage) terlebih dahulu.");
        }
        if (view == null) {
            System.err.println("View untuk scene '" + title + "' adalah null. Tidak dapat menampilkan scene.");
            return;
        }
        Scene scene = new Scene(view, width, height);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void showLoginScene() {
        LoginController controller = new LoginController();
        setSceneFullScreen(controller.getView(), "Login");
    }

   public static void showRegisterScene() {
    RegisterController controller = new RegisterController();
    setSceneSafely(controller.getView(), "Registrasi Pengguna", 500, 600);
}


    public static void showDashboardScene() {
        DashboardController controller = new DashboardController();
        setSceneSafely(controller.getView(), "Dashboard Warehouse", 900, 600);
    }

    
    public static void showPenerimaanScene() {
        PenerimaanController controller = new PenerimaanController(primaryStage);
        setSceneSafely(controller.getView(), "Penerimaan Barang", 800, 600);
    }

    public static void showPengeluaranScene() {
        PengeluaranController controller = new PengeluaranController(primaryStage);
        setSceneSafely(controller.getView(), "Pengeluaran Barang", 800, 600);
    }

    public static void showLaporanScene() {
        LaporanPengeluaranController controller = new LaporanPengeluaranController(primaryStage);
        setSceneSafely(controller.getView(), "Laporan Pengeluaran", 800, 600);
    }

    public static void showUserManagementScene() {
        UserManagementController controller = new UserManagementController();
        setSceneSafely(controller.getView(), "Manajemen Pengguna", 900, 600);
    }
    
    
    public static void showStockScene() {
        StokController controller = new StokController(primaryStage);
        setSceneSafely(controller.getView(), "Manajemen Stok", 800, 600);
    }

    public static void showPOScene() {
        POController controller = new POController(primaryStage);
        setSceneSafely(controller.getView(), "Purchase Order", 850, 650);
    }

    public static Stage getMainStage() {
        return primaryStage;
    }
}




