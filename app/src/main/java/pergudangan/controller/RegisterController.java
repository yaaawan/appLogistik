package pergudangan.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import pergudangan.model.User;
import pergudangan.model.UserData;
import pergudangan.utils.SceneManager;

public class RegisterController {
    private StackPane root;
    private VBox mainContainer;
    private TextField txtUsername;
    private PasswordField txtPassword;
    private TextField txtPasswordVisible;
    private PasswordField txtConfirm;
    private TextField txtConfirmVisible;
    private Label lblMessage;
    private CheckBox chkShowPassword;
    private Button btnRegister;
    private Button btnBack;

    public RegisterController() {
        createView();
        addAnimations();
    }

    private void createView() {
        root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #2C003E, #3A0058);");

        createBackgroundShapes();
        createOverlayTriangles();

        mainContainer = new VBox(25);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setMaxWidth(450);
        mainContainer.setMaxHeight(700);
        mainContainer.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.12);" +
            "-fx-background-radius: 25;" +
            "-fx-border-color: rgba(255, 255, 255, 0.2);" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 25;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 30, 0, 0, 15);"
        );

        VBox headerSection = createHeaderSection();
        VBox formSection = createFormSection();
        HBox buttonSection = createButtonSection();
        createMessageSection();

        mainContainer.getChildren().addAll(headerSection, formSection, buttonSection, lblMessage);
        root.getChildren().add(mainContainer);
    }

    private void createBackgroundShapes() {
        Color[] softColors = {
            Color.web("#FFFFFF", 0.05),
            Color.web("#FFDEE9", 0.04),
            Color.web("#B5FFFC", 0.04),
            Color.web("#C2FFD8", 0.04)
        };
        for (int i = 0; i < 5; i++) {
            Circle circle = new Circle(60 + Math.random() * 80);
            circle.setFill(softColors[(int)(Math.random() * softColors.length)]);
            circle.setEffect(new GaussianBlur(18));
            circle.setTranslateX((Math.random() - 0.5) * 800);
            circle.setTranslateY((Math.random() - 0.5) * 600);
            root.getChildren().add(circle);

            TranslateTransition move = new TranslateTransition(Duration.seconds(10 + Math.random() * 4), circle);
            move.setByX((Math.random() - 0.5) * 120);
            move.setByY((Math.random() - 0.5) * 120);
            move.setCycleCount(TranslateTransition.INDEFINITE);
            move.setAutoReverse(true);
            move.play();

            FadeTransition fade = new FadeTransition(Duration.seconds(10), circle);
            fade.setFromValue(0.3);
            fade.setToValue(0.6);
            fade.setCycleCount(FadeTransition.INDEFINITE);
            fade.setAutoReverse(true);
            fade.play();
        }
    }

    private void createOverlayTriangles() {
        Polygon triangle1 = new Polygon();
        triangle1.getPoints().addAll(
            0.0, 600.0,
            150.0, 0.0,
            300.0, 600.0
        );
        triangle1.setFill(Color.web("#FFFFFF", 0.03));

        Polygon triangle2 = new Polygon();
        triangle2.getPoints().addAll(
            300.0, 600.0,
            450.0, 0.0,
            600.0, 600.0
        );
        triangle2.setFill(Color.web("#FFFFFF", 0.02));

        root.getChildren().addAll(triangle1, triangle2);
    }

    private VBox createHeaderSection() {
        VBox header = new VBox(12);
        header.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("\uD83D\uDCDD Daftar Akun");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        lblTitle.setStyle("-fx-text-fill: white;");

        Label lblSubtitle = new Label("Isi form untuk membuat akun Anda");
        lblSubtitle.setFont(Font.font("Segoe UI", 15));
        lblSubtitle.setStyle("-fx-text-fill: rgba(255,255,255,0.85);");

        header.getChildren().addAll(lblTitle, lblSubtitle);
        return header;
    }

    private VBox createFormSection() {
        VBox form = new VBox(20);
        form.setAlignment(Pos.CENTER);

        txtUsername = createStyledTextField("\uD83D\uDC64 Username");

        StackPane passwordContainer = new StackPane();
        txtPassword = createStyledPasswordField("\uD83D\uDD12 Password");
        txtPasswordVisible = createStyledTextField("\uD83D\uDD12 Password");
        txtPasswordVisible.setVisible(false);
        txtPasswordVisible.setManaged(false);
        txtPassword.textProperty().bindBidirectional(txtPasswordVisible.textProperty());
        passwordContainer.getChildren().addAll(txtPassword, txtPasswordVisible);

        StackPane confirmContainer = new StackPane();
        txtConfirm = createStyledPasswordField("\uD83D\uDD01 Konfirmasi Password");
        txtConfirmVisible = createStyledTextField("\uD83D\uDD01 Konfirmasi Password");
        txtConfirmVisible.setVisible(false);
        txtConfirmVisible.setManaged(false);
        txtConfirm.textProperty().bindBidirectional(txtConfirmVisible.textProperty());
        confirmContainer.getChildren().addAll(txtConfirm, txtConfirmVisible);

        chkShowPassword = new CheckBox("\uD83D\uDC41\uFE0F Tampilkan Password");
        chkShowPassword.setFont(Font.font("Segoe UI", 12));
        chkShowPassword.setStyle("-fx-text-fill: white;");
        chkShowPassword.setOnAction(e -> togglePasswordVisibility());

        form.getChildren().addAll(txtUsername, passwordContainer, confirmContainer, chkShowPassword);
        return form;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setMaxWidth(320);
        field.setPrefHeight(48);
        field.setFont(Font.font("Segoe UI", 14));
        field.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.12);" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: rgba(255,255,255,0.28);" +
            "-fx-border-radius: 15;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(255,255,255,0.55);"
        );
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setMaxWidth(320);
        field.setPrefHeight(48);
        field.setFont(Font.font("Segoe UI", 14));
        field.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.12);" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: rgba(255,255,255,0.28);" +
            "-fx-border-radius: 15;" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(255,255,255,0.55);"
        );
        return field;
    }

    private HBox createButtonSection() {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);

        btnRegister = createStyledButton("\u2705 Daftar", "#00c6ff", "#0072ff");
        btnRegister.setOnAction(e -> handleRegister());

        btnBack = createStyledButton("\u21A9 Kembali", "#f857a6", "#ff5858");
        btnBack.setOnAction(e -> SceneManager.showLoginScene());

        box.getChildren().addAll(btnRegister, btnBack);
        return box;
    }

    private Button createStyledButton(String text, String color1, String color2) {
        Button btn = new Button(text);
        btn.setPrefSize(150, 45);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btn.setStyle(String.format(
            "-fx-background-color: linear-gradient(to right, %s, %s);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 2);",
            color1, color2));

        btn.setOnMouseEntered(e -> btn.setStyle(String.format(
            "-fx-background-color: linear-gradient(to right, %s, %s);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-scale-x: 1.05; -fx-scale-y: 1.05;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 0, 4);",
            color1, color2)));

        btn.setOnMouseExited(e -> btn.setStyle(String.format(
            "-fx-background-color: linear-gradient(to right, %s, %s);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 2);",
            color1, color2)));

        return btn;
    }

    private void createMessageSection() {
        lblMessage = new Label();
        lblMessage.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblMessage.setAlignment(Pos.CENTER);
        lblMessage.setMaxWidth(350);
        lblMessage.setWrapText(true);
        lblMessage.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        lblMessage.setVisible(false);
    }

    private void addAnimations() {
        mainContainer.setScaleX(0.8);
        mainContainer.setScaleY(0.8);
        mainContainer.setOpacity(0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(600), mainContainer);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), mainContainer);
        fadeIn.setToValue(1.0);

        scaleIn.play();
        fadeIn.play();
    }

    private void togglePasswordVisibility() {
        boolean show = chkShowPassword.isSelected();
        txtPassword.setVisible(!show);
        txtPassword.setManaged(!show);
        txtPasswordVisible.setVisible(show);
        txtPasswordVisible.setManaged(show);
        txtConfirm.setVisible(!show);
        txtConfirm.setManaged(!show);
        txtConfirmVisible.setVisible(show);
        txtConfirmVisible.setManaged(show);
    }

    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = chkShowPassword.isSelected() ? txtPasswordVisible.getText() : txtPassword.getText();
        String confirm = chkShowPassword.isSelected() ? txtConfirmVisible.getText() : txtConfirm.getText();

        if (UserData.getInstance().getUsers().size() >= 3) {
            showMessage("\u26A0\uFE0F Maksimum 3 user terdaftar.", false);
            return;
        }
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showMessage("\u274C Semua field harus diisi.", false);
            return;
        }
        if (username.length() < 3) {
            showMessage("\u26A0\uFE0F Username minimal 3 karakter.", false);
            return;
        }
        if (password.length() < 6) {
            showMessage("\u26A0\uFE0F Password minimal 6 karakter.", false);
            return;
        }
        if (!password.equals(confirm)) {
            showMessage("\u274C Password tidak cocok.", false);
            return;
        }
        if (UserData.getInstance().isUsernameExist(username)) {
            showMessage("\u26A0\uFE0F Username telah digunakan.", false);
            return;
        }

        User newUser = new User(username, password, "user", "active");
        UserData.getInstance().addUser(newUser);
        showMessage("\uD83C\uDF89 Registrasi berhasil!", true);
        clearForm();
    }

    private void clearForm() {
        txtUsername.clear();
        txtPassword.clear();
        txtPasswordVisible.clear();
        txtConfirm.clear();
        txtConfirmVisible.clear();
        chkShowPassword.setSelected(false);
        togglePasswordVisibility();
    }

    private void showMessage(String message, boolean success) {
        lblMessage.setTextFill(success ? Color.LIMEGREEN : Color.SALMON);
        lblMessage.setText(message);
        lblMessage.setVisible(true);

        if (success) {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), lblMessage);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> lblMessage.setVisible(false));
            fadeOut.play();
        }
    }

    public Parent getView() {
        return root;
    }
}
