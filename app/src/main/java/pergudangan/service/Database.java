package pergudangan.service;

import pergudangan.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Database {
    private static final String URL = "jdbc:sqlite:warehouse.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite tidak ditemukan: " + e.getMessage());
        }
    }

    public static Connection connect() throws SQLException {
        System.out.println("Menggunakan database di: " + new java.io.File("warehouse.db").getAbsolutePath());
        return DriverManager.getConnection(URL);
    }

    public static void initDatabase() {
        createUsersTable();
        createPengeluaranTable();
        createPenerimaanTables();
        createPengeluaranTables();
        createTables();
        createStokItemTables();
    }

    private static void createUsersTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                status TEXT NOT NULL
            );
        """;
        executeCreateTable(sql, "users");
    }

private static void createPenerimaanTables() {
    String createPenerimaanTable = """
        CREATE TABLE IF NOT EXISTS penerimaan (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            no_terima TEXT UNIQUE NOT NULL,
            no_po TEXT NOT NULL,
            supplier TEXT NOT NULL,
            tanggal TEXT NOT NULL,
            catatan TEXT
        )
    """;

    String createPenerimaanItemsTable = """
        CREATE TABLE IF NOT EXISTS penerimaan_items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            no_terima TEXT NOT NULL,
            nama_barang TEXT NOT NULL,
            qty_po INTEGER NOT NULL,
            qty_diterima INTEGER NOT NULL,
            satuan TEXT NOT NULL,
            kondisi TEXT NOT NULL,
            keterangan TEXT,
            FOREIGN KEY (no_terima) REFERENCES penerimaan(no_terima) ON DELETE CASCADE
        )
    """;

    try (Connection connection = connect();
         Statement stmt = connection.createStatement()) {
        // Buat tabel penerimaan
        stmt.execute(createPenerimaanTable);
        System.out.println("Tabel 'penerimaan' berhasil dibuat atau sudah ada.");

        // Buat tabel penerimaan_items
        stmt.execute(createPenerimaanItemsTable);
        System.out.println("Tabel 'penerimaan_items' berhasil dibuat atau sudah ada.");
    } catch (SQLException e) {
        System.err.println("Error creating penerimaan tables: " + e.getMessage());
    }
}



  public static void createTables() {
    String sql = """
        CREATE TABLE IF NOT EXISTS purchase_order (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            po_number TEXT NOT NULL UNIQUE,
            tanggal TEXT NOT NULL,
            supplier TEXT NOT NULL,
            items TEXT NOT NULL,
            keterangan TEXT,
            status TEXT,
            total REAL
        );
    """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
        stmt.execute(sql);
        System.out.println("Tabel purchase_order siap.");
    } catch (SQLException e) {
        System.err.println("Gagal membuat tabel: " + e.getMessage());
    }
}




  public static void createPengeluaranTables() {
    String createInventoryTable = """
        CREATE TABLE IF NOT EXISTS inventory (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            kode_barang VARCHAR(50) UNIQUE NOT NULL,
            nama_barang VARCHAR(255) NOT NULL,
            jumlah INT NOT NULL CHECK (jumlah >= 0),
            satuan VARCHAR(20),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    """;

    String createPengeluaranTable = """
        CREATE TABLE IF NOT EXISTS pengeluaran (
            no_keluar VARCHAR(50) PRIMARY KEY,
            tanggal DATE NOT NULL,
            tujuan VARCHAR(255) NOT NULL,
            pic VARCHAR(100) NOT NULL,
            catatan TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    """;

    String createPengeluaranItemsTable = """
        CREATE TABLE IF NOT EXISTS pengeluaran_items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            no_keluar VARCHAR(50) NOT NULL,
            kode_barang VARCHAR(50) NOT NULL,
            nama_barang VARCHAR(255) NOT NULL,
            jumlah INT NOT NULL CHECK (jumlah >= 0),
            qty_keluar INT NOT NULL CHECK (qty_keluar >= 0),
            stok_tersedia INT DEFAULT 0 CHECK (stok_tersedia >= 0),
            harga_satuan DECIMAL(15,2) NOT NULL,
            total_harga DECIMAL(15,2) NOT NULL,
            satuan VARCHAR(20) NOT NULL,
            keterangan TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (no_keluar) REFERENCES pengeluaran(no_keluar) ON DELETE CASCADE,
            FOREIGN KEY (kode_barang) REFERENCES inventory(kode_barang) ON UPDATE CASCADE ON DELETE RESTRICT
        );
    """;

    String createTriggerUpdateTimestamp = """
        CREATE TRIGGER trg_pengeluaran_items_updated
        AFTER UPDATE ON pengeluaran_items
        FOR EACH ROW
        BEGIN
            UPDATE pengeluaran_items
            SET updated_at = CURRENT_TIMESTAMP
            WHERE id = NEW.id;
        END
    """;

    try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
        stmt.execute(createInventoryTable);
        System.out.println("Tabel 'inventory' berhasil dibuat atau sudah ada.");

        stmt.execute(createPengeluaranTable);
        System.out.println("Tabel 'pengeluaran' berhasil dibuat atau sudah ada.");

        stmt.execute(createPengeluaranItemsTable);
        System.out.println("Tabel 'pengeluaran_items' berhasil dibuat atau sudah ada.");

        // Buat trigger, tapi cek dulu apakah sudah ada
        try {
            stmt.execute(createTriggerUpdateTimestamp);
            System.out.println("Trigger 'trg_pengeluaran_items_updated' berhasil dibuat.");
        } catch (SQLException e) {
            // Biasanya error karena trigger sudah ada, jadi bisa diabaikan
            System.out.println("Trigger 'trg_pengeluaran_items_updated' sudah ada, dilewati.");
        }

    } catch (SQLException e) {
        System.err.println("Error creating tables atau trigger: " + e.getMessage());
    }
}



  public static void createStokItemTables() {
    String sql = """
       CREATE TABLE IF NOT EXISTS stock (
           item_name TEXT PRIMARY KEY,
           quantity INTEGER,
           unit TEXT,
           purchase_price REAL,
           selling_price REAL,
           category TEXT
       );
    """;

     try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
        stmt.execute(sql);
        System.out.println("Tabel 'stock' berhasil dibuat atau sudah ada.");
    } catch (SQLException e) {
        System.err.println("Gagal membuat tabel 'stock': " + e.getMessage());
    }
}

  
private static void createPengeluaranTable() {
        String sql = """
            CREATE TABLE pengeluaran (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                item_name TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                total_price REAL NOT NULL,
                date TIMESTAMP NOT NULL,
                category TEXT NOT NULL
            );
        """;
        executeCreateTable(sql, "pengeluaran");
    }


    private static void executeCreateTable(String sql, String tableName) {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabel '" + tableName + "' berhasil dibuat atau sudah ada.");
        } catch (SQLException e) {
            System.err.println("Gagal membuat tabel '" + tableName + "': " + e.getMessage());
        }
    }



    public static boolean insertUser(User user) {
        String sql = "INSERT INTO users(name, password, role, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getStatus());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("User '" + user.getName() + "' sudah ada di database.");
            } else {
                System.err.println("Gagal menambahkan user: " + e.getMessage());
            }
            return false;
        }
    }

    public static User validateLogin(String name, String password) {
        String sql = "SELECT * FROM users WHERE name = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("name"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.err.println("Gagal melakukan login: " + e.getMessage());
        }
        return null;
    }


   

    private static boolean executeInsert(String sql, String tanggal, double jumlah, String keterangan, String user) {
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tanggal);
            stmt.setDouble(2, jumlah);
            stmt.setString(3, keterangan);
            stmt.setString(4, user);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal menyisipkan data: " + e.getMessage());
            return false;
        }
    }