package pergudangan.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pergudangan.service.Database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserData {
    private static final UserData instance = new UserData();
    private final ObservableList<User> users = FXCollections.observableArrayList();

    private UserData() {
        loadUsersFromDatabase();
    }

    public static UserData getInstance() {
        return instance;
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        if (user == null) {
            System.err.println("User tidak boleh null.");
            return;
        }

        String username = user.getName();
        String password = user.getPassword();

        if (username == null || username.trim().isEmpty()) {
            System.err.println("Nama pengguna tidak boleh kosong.");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            System.err.println("Password tidak boleh kosong.");
            return;
        }

        if (isUsernameExist(username)) {
            System.out.println("Username sudah terdaftar: " + username);
            return;
        }

        String hashedPassword = hashPassword(password);
        user.setPassword(hashedPassword);

        if (Database.insertUser(user)) {
            users.add(user);
            System.out.println("User berhasil ditambahkan: " + username);
        } else {
            System.err.println("Gagal menambahkan user ke database: " + username);
        }
    }


    public boolean isUsernameExist(String username) {
        return users.stream()
                .anyMatch(u -> u.getName().equalsIgnoreCase(username));
    }


    private void loadUsersFromDatabase() {
        users.clear();
        String sql = "SELECT * FROM users";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("status")
                );
                users.add(user);
            }
            System.out.println("User berhasil dimuat dari database.");

        } catch (SQLException e) {
            System.err.println("Gagal memuat data user dari database: " + e.getMessage());
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm tidak tersedia", e);
        }
    }
    public boolean removeUser(User user) {
    if (user == null) return false;

    boolean deletedFromDB = Database.deleteUser(user.getName());

    if (deletedFromDB) {
        users.remove(user);
        System.out.println("User berhasil dihapus: " + user.getName());
        return true;
    } else {
        System.err.println("Gagal menghapus user dari database: " + user.getName());
        return false;
    }
}

}




