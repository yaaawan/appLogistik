package pergudangan.controller;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;    
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import pergudangan.model.User;
import pergudangan.model.UserData;
import pergudangan.utils.SceneManager;

public class LoginController {
    private StackPane root;
    private VBox loginCard;
    private TextField txtUsername;
    private PasswordField txtPassword;
    private TextField txtPasswordVisible;
    private Label lblMessage;
    private CheckBox chkShowPassword;
    private Button btnLogin;
    private ProgressIndicator loadingIndicator;

    public LoginController() {
        createView();
    }

    private void createView() {
        root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #2e004f, #44107a);");
        createBackgroundPattern();

        loginCard = new VBox(25);
        loginCard.setPadding(new Insets(50));
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setMaxWidth(450);
        loginCard.setMaxHeight(650);
        loginCard.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.05);
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-color: rgba(255, 255, 255, 0.15);
            -fx-border-width: 1.5;
            -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 30, 0, 0, 10);
        """);

        Label lblTitle = new Label("Welcome Back");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 40));
        lblTitle.setTextFill(Color.WHITE);

        Label lblSubtitle = new Label("Sign in to access the dashboard");
        lblSubtitle.setFont(Font.font("Segoe UI", 16));
        lblSubtitle.setTextFill(Color.rgb(220, 220, 220, 0.8));

        VBox inputContainer = new VBox(20);
        inputContainer.setAlignment(Pos.CENTER);

        VBox usernameContainer = new VBox();
        txtUsername = new TextField();
        txtUsername.setPromptText("Username");
        styleInputField(txtUsername);
        usernameContainer.setAlignment(Pos.CENTER);
        usernameContainer.getChildren().add(txtUsername);

        VBox passwordContainer = new VBox(5);
        passwordContainer.setAlignment(Pos.CENTER);

        txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");
        styleInputField(txtPassword);

        txtPasswordVisible = new TextField();
        txtPasswordVisible.setPromptText("Password");
        txtPasswordVisible.setVisible(false);
        txtPasswordVisible.managedProperty().bind(txtPasswordVisible.visibleProperty());
        txtPassword.textProperty().bindBidirectional(txtPasswordVisible.textProperty());
        styleInputField(txtPasswordVisible);

        chkShowPassword = new CheckBox("Show Password");
        chkShowPassword.setTextFill(Color.rgb(255, 255, 255, 0.9));
        chkShowPassword.setFont(Font.font("Segoe UI", 14));
        chkShowPassword.setOnAction(e -> togglePasswordVisibility());

        passwordContainer.getChildren().addAll(txtPassword, txtPasswordVisible, chkShowPassword);

        btnLogin = new Button("Sign In");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setPrefHeight(50);
        styleLoginButton(btnLogin);
        btnLogin.setOnAction(e -> handleLoginWithAnimation());

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(24, 24);
        loadingIndicator.setVisible(false);
        loadingIndicator.setStyle("-fx-accent: white;");
        loadingIndicator.setTranslateX(-10);

        StackPane loginButtonWrapper = new StackPane(btnLogin, loadingIndicator);
        loginButtonWrapper.setAlignment(Pos.CENTER_RIGHT);
        loginButtonWrapper.setMaxWidth(300);
        loginButtonWrapper.setPrefHeight(50);

        inputContainer.getChildren().addAll(usernameContainer, passwordContainer, loginButtonWrapper);

        lblMessage = new Label();
        lblMessage.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        lblMessage.setTextFill(Color.WHITE);
        lblMessage.setWrapText(true);
        lblMessage.setAlignment(Pos.CENTER);
        lblMessage.setMaxWidth(350);

        VBox registerSection = new VBox(10);
        registerSection.setAlignment(Pos.CENTER);

        Separator separator = new Separator();
        separator.setMaxWidth(300);

        Label lblNoAccount = new Label("Don't have an account?");
        lblNoAccount.setTextFill(Color.rgb(255, 255, 255, 0.8));
        lblNoAccount.setFont(Font.font("Segoe UI", 14));

        Button btnRegister = new Button("Create Account");
        btnRegister.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnRegister.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-border-color: rgba(255, 255, 255, 0.4);
            -fx-border-width: 2;
            -fx-border-radius: 20;
            -fx-padding: 10 30;
            -fx-cursor: hand;
        """);

        btnRegister.setOnMouseEntered(e -> btnRegister.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.1);
            -fx-text-fill: white;
            -fx-border-color: white;
            -fx-border-width: 2;
            -fx-border-radius: 20;
            -fx-padding: 10 30;
            -fx-cursor: hand;
        """));

        btnRegister.setOnMouseExited(e -> btnRegister.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-border-color: rgba(255, 255, 255, 0.4);
            -fx-border-width: 2;
            -fx-border-radius: 20;
            -fx-padding: 10 30;
            -fx-cursor: hand;
        """));

        btnRegister.setOnAction(e -> {
            playButtonAnimation(btnRegister);
            SceneManager.showRegisterScene();
        });

        registerSection.getChildren().addAll(separator, lblNoAccount, btnRegister);

        loginCard.getChildren().addAll(
            lblTitle, lblSubtitle,
            inputContainer,
            lblMessage, registerSection
        );

        root.getChildren().add(loginCard);

        addKeyListeners();
        playEntranceAnimation();
    }

    private void createBackgroundPattern() {
        for (int i = 0; i < 12; i++) {
            Rectangle rect = new Rectangle(120, 120);
            rect.setFill(Color.rgb(255, 255, 255, 0.03));
            rect.setRotate(45);
            rect.setTranslateX((i % 4) * 200 - 300);
            rect.setTranslateY((i / 4) * 150 - 200);
            root.getChildren().add(rect);

            TranslateTransition tt = new TranslateTransition(Duration.seconds(10 + i), rect);
            tt.setByY(15);
            tt.setCycleCount(Timeline.INDEFINITE);
            tt.setAutoReverse(true);
            tt.play();
        }
    }

    private void styleInputField(TextField field) {
        field.setMaxWidth(300);
        field.setPrefHeight(45);
        field.setFont(Font.font("Segoe UI", 15));
        field.setStyle("""
            -fx-background-color: rgba(255,255,255,0.9);
            -fx-background-radius: 10;
            -fx-border-color: transparent;
            -fx-padding: 10 15;
        """);

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("""
                    -fx-background-color: white;
                    -fx-border-color: #5A8DEE;
                    -fx-border-width: 2;
                    -fx-background-radius: 10;
                    -fx-padding: 10 15;
                """);
            } else {
                field.setStyle("""
                    -fx-background-color: rgba(255,255,255,0.9);
                    -fx-border-color: transparent;
                    -fx-background-radius: 10;
                    -fx-padding: 10 15;
                """);
            }
        });
    }

    private void styleLoginButton(Button button) {
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
        button.setStyle("""
            -fx-background-color: linear-gradient(to right, #5A8DEE, #8854d0);
            -fx-text-fill: white;
            -fx-background-radius: 25;
            -fx-cursor: hand;
            -fx-padding: 0 40 0 40;
        """);

        button.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(120), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(120), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    private void playEntranceAnimation() {
        loginCard.setTranslateY(60);
        loginCard.setOpacity(0);
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(loginCard.translateYProperty(), 60),
                new KeyValue(loginCard.opacityProperty(), 0)
            ),
            new KeyFrame(Duration.millis(700),
                new KeyValue(loginCard.translateYProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(loginCard.opacityProperty(), 1, Interpolator.EASE_OUT)
            )
        );
        timeline.play();
    }

    private void togglePasswordVisibility() {
        boolean show = chkShowPassword.isSelected();
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150));
        FadeTransition fadeIn = new FadeTransition(Duration.millis(150));

        if (show) {
            fadeOut.setNode(txtPassword);
            fadeIn.setNode(txtPasswordVisible);
        } else {
            fadeOut.setNode(txtPasswordVisible);
            fadeIn.setNode(txtPassword);
        }

        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            txtPasswordVisible.setVisible(show);
            txtPassword.setVisible(!show);
            fadeIn.play();
        });

        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeOut.play();
    }

    private void handleLoginWithAnimation() {
        btnLogin.setText("");
        loadingIndicator.setVisible(true);
        btnLogin.setDisable(true);

        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(e -> {
            handleLogin();
            btnLogin.setText("Sign In");
            loadingIndicator.setVisible(false);
            btnLogin.setDisable(false);
        });
        pause.play();
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = chkShowPassword.isSelected() ? txtPasswordVisible.getText() : txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please fill in all fields", false);
            return;
        }

        if (validateCredentials(username, password)) {
            showMessage("Welcome back! Redirecting...", true);
            PauseTransition pause = new PauseTransition(Duration.millis(1000));
            pause.setOnFinished(e -> SceneManager.showDashboardScene());
            pause.play();
        } else {
            showMessage("Invalid credentials. Please try again.", false);
            shakeAnimation();
        }
    }

    private void shakeAnimation() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(100), loginCard);
        tt.setFromX(0);
        tt.setToX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }

    private boolean validateCredentials(String username, String password) {
        for (User user : UserData.getInstance().getUsers()) {
            if (user.getName().equalsIgnoreCase(username)
                    && user.getPassword().equals(UserData.hashPassword(password))
                    && user.getStatus().equalsIgnoreCase("active")) {
                return true;
            }
        }
        return false;
    }

    private void showMessage(String message, boolean success) {
        lblMessage.setStyle(success ?
            "-fx-text-fill: #4CAF50;" :
            "-fx-text-fill: #f44336;");
        lblMessage.setText(message);

        FadeTransition ft = new FadeTransition(Duration.millis(300), lblMessage);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void playButtonAnimation(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setToX(0.95);
        st.setToY(0.95);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void addKeyListeners() {
        txtUsername.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLoginWithAnimation();
        });
        txtPassword.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLoginWithAnimation();
        });
        txtPasswordVisible.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLoginWithAnimation();
        });
    }

    public Parent getView() {
        return root;
    }
}