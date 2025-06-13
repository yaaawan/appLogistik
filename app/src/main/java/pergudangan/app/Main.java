package pergudangan.app;
import javafx.application.Application;
import javafx.stage.Stage;
import pergudangan.utils.SceneManager;
import pergudangan.service.Database;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Database.initDatabase();

            SceneManager.setStage(primaryStage);
            SceneManager.showLoginScene();  
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal memulai aplikasi: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
