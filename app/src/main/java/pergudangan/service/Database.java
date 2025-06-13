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