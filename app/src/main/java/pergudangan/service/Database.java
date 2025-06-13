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


public static boolean insertPurchaseOrder(PurchaseOrder po) {
        String sql = "INSERT INTO purchase_order (po_number, tanggal, supplier, items, keterangan, status, total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, po.getPoNumber());
            stmt.setString(2, po.getDate().toString());
            stmt.setString(3, po.getSupplier());
            stmt.setString(4, po.formatItemsText()); // pastikan tersedia di PurchaseOrder
            stmt.setString(5, po.getKeterangan());
            stmt.setString(6, po.getStatus());
            stmt.setDouble(7, po.getTotal());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal insert PO: " + e.getMessage());
            return false;
        }
    }

    public static boolean updatePurchaseOrder(PurchaseOrder po) {
    String sql = "UPDATE purchase_order SET tanggal = ?, supplier = ?, items = ?, keterangan = ?, status = ?, total = ? WHERE po_number = ?";
    try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, po.getDate().toString());
        stmt.setString(2, po.getSupplier());
        stmt.setString(3, po.formatItemsText());
        stmt.setString(4, po.getKeterangan());
        stmt.setString(5, po.getStatus());
        stmt.setDouble(6, po.getTotal());
        stmt.setString(7, po.getPoNumber());

        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        System.err.println("Gagal mengupdate PO: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
     public static List<PurchaseOrder> getAllPurchaseOrders() {
        List<PurchaseOrder> list = new ArrayList<>();
        String sql = "SELECT * FROM purchase_order ORDER BY id";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String poNumber = rs.getString("po_number");
                LocalDate date = LocalDate.parse(rs.getString("tanggal"));
                String supplier = rs.getString("supplier");
                String itemsText = rs.getString("items");
                List<POItem> items = parseItemsText(itemsText);
                String keterangan = rs.getString("keterangan");
                String status = rs.getString("status");
                double total = rs.getDouble("total");

                PurchaseOrder po = new PurchaseOrder(poNumber, supplier, date, status, keterangan, total, items);
                list.add(po);
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil daftar PO: " + e.getMessage());
        }
        return list;
    }

 public static boolean deletePurchaseOrder(String poNumber) {
    String deletePOSql = "DELETE FROM purchase_order WHERE po_number = ?";
    
    try (Connection conn = connect(); 
         PreparedStatement pstmtPO = conn.prepareStatement(deletePOSql)) {
        
        pstmtPO.setString(1, poNumber);
        int affectedRows = pstmtPO.executeUpdate();
        
        if (affectedRows > 0) {
            System.out.println("PO " + poNumber + " berhasil dihapus.");
            return true;
        } else {
            System.err.println("PO " + poNumber + " tidak ditemukan.");
            return false;
        }
        
    } catch (SQLException e) {
        System.err.println("Gagal menghapus PO: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
    


public static boolean savePO(PurchaseOrder po) {
    String sql = "INSERT INTO purchase_order (po_number, tanggal, supplier, items, keterangan, status, total) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, po.getPoNumber());
        stmt.setString(2, po.getDate().toString());
        stmt.setString(3, po.getSupplier());
        stmt.setString(4, po.formatItemsText()); // pastikan method ini ada di PurchaseOrder
        stmt.setString(5, po.getKeterangan());
        stmt.setString(6, po.getStatus());
        stmt.setDouble(7, po.getTotal());
        stmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        System.err.println("Gagal menyimpan PO: " + e.getMessage());
        e.printStackTrace(); // Tambahkan ini untuk debug
        return false;
    }
}

public static List<PurchaseOrder> getAllPO() {
    List<PurchaseOrder> list = new ArrayList<>();
    String sql = "SELECT * FROM purchase_order ORDER BY id";
    
    try (Connection conn = connect(); 
         Statement stmt = conn.createStatement(); 
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            String poNumber = rs.getString("po_number");
            LocalDate date = LocalDate.parse(rs.getString("tanggal"));
            String supplier = rs.getString("supplier");
            String itemsText = rs.getString("items");
            List items = parseItemsText(itemsText); // Tanpa generic untuk sementara
            String keterangan = rs.getString("keterangan");
            String status = rs.getString("status");
            double total = rs.getDouble("total");
            
            PurchaseOrder po = new PurchaseOrder(poNumber, supplier, date, status, keterangan, total, items);
            list.add(po);
        }
    } catch (SQLException e) {
        System.err.println("Gagal mengambil daftar PO: " + e.getMessage());
        e.printStackTrace();
    }
    
    return list;
}


public static List<POItem> parseItemsText(String itemsText) {
    List<POItem> itemList = new ArrayList<>();
    
    if (itemsText == null || itemsText.isBlank()) {
        return itemList;
    }
    
    String[] itemStrings = itemsText.split(";");
    for (String itemStr : itemStrings) {
        itemStr = itemStr.trim();
        if (itemStr.isEmpty()) continue;
        
        String[] parts = itemStr.split(",");
        if (parts.length == 5) {
            try {
                String id = parts[0].trim();
                String nama = parts[1].trim();
                int qty = Integer.parseInt(parts[2].trim());
                String satuan = parts[3].trim();
                double harga = Double.parseDouble(parts[4].trim());
                
                itemList.add(new POItem(id, nama, qty, satuan, harga));
            } catch (NumberFormatException e) {
                System.err.println("Format item tidak valid: " + itemStr + " -> " + e.getMessage());
            }
        }
    }
    return itemList;
}



public static boolean checkIfPONumberExists(String poNumber) {
    String sql = "SELECT COUNT(*) FROM purchase_order WHERE po_number = ?";
    try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, poNumber);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    } catch (SQLException e) {
        System.err.println("Gagal mengecek keberadaan PO: " + e.getMessage());
        return false;
    }
}

     
       
    private static List<POItem> getItemsByPONumber(String poNumber, Connection conn) throws SQLException {
    List<POItem> items = new ArrayList<>();
    String sqlItems = "SELECT id, nama, qty, satuan, harga FROM po_items WHERE po_number = ?";
    try (PreparedStatement pstmt = conn.prepareStatement(sqlItems)) {
        pstmt.setString(1, poNumber);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");  // Ubah jadi String
                String nama = rs.getString("nama");
                int qty = rs.getInt("qty");
                String satuan = rs.getString("satuan");
                double harga = rs.getDouble("harga");

                POItem item = new POItem(id, nama, qty, satuan, harga);
                items.add(item);
            }
        }
    }
    return items;
}



public static List<PurchaseOrder> getPOByStatus(String status) {
    List<PurchaseOrder> list = new ArrayList<>();
    String sql = "SELECT * FROM purchase_order WHERE status = ? ORDER BY tanggal DESC";
    
    try (Connection conn = connect(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, status);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            String poNumber = rs.getString("po_number");
            LocalDate date = LocalDate.parse(rs.getString("tanggal"));
            String supplier = rs.getString("supplier");
            String itemsText = rs.getString("items");
            List<POItem> items = parseItemsText(itemsText);
            String keterangan = rs.getString("keterangan");
            String poStatus = rs.getString("status");
            double total = rs.getDouble("total");
            
            PurchaseOrder po = new PurchaseOrder(poNumber, supplier, date, poStatus, keterangan, total, items);
            list.add(po);
        }
    } catch (SQLException e) {
        System.err.println("Gagal mengambil PO berdasarkan status: " + e.getMessage());
        e.printStackTrace();
    }
    
    return list;
}


public static String convertPenerimaanItemsToText(List<PenerimaanItem> items) {
    StringBuilder sb = new StringBuilder();
    for (PenerimaanItem item : items) {
        sb.append(item.getNamaBarang()).append(":")
          .append(item.getQtyPO()).append(":")
          .append(item.getQtyDiterima()).append(":")
          .append(item.getSatuan()).append(":")
          .append(item.getKondisi()).append(":")
          .append(item.getKeterangan()).append(";");
    }
    return sb.toString();
}
public static boolean savePenerimaan(Penerimaan penerimaan) {
    
    String sqlPenerimaan = "INSERT INTO penerimaan (no_terima, no_po, supplier, tanggal, catatan) VALUES (?, ?, ?, ?, ?)";
    String sqlItem = "INSERT INTO penerimaan_items (no_terima, nama_barang, qty_po, qty_diterima, satuan, kondisi, keterangan) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = connect()) {
        conn.setAutoCommit(false); // Mulai transaksi

        // Simpan data utama penerimaan
        try (PreparedStatement pstmtPenerimaan = conn.prepareStatement(sqlPenerimaan)) {
            pstmtPenerimaan.setString(1, penerimaan.getNoTerima());
            pstmtPenerimaan.setString(2, penerimaan.getNoPO());
            pstmtPenerimaan.setString(3, penerimaan.getSupplier());
            pstmtPenerimaan.setString(4, penerimaan.getTanggal().toString());
            pstmtPenerimaan.setString(5, penerimaan.getCatatan());
            pstmtPenerimaan.executeUpdate();
        }



                try (PreparedStatement pstmtItem = conn.prepareStatement(sqlItem)) {
            for (PenerimaanItem item : penerimaan.getItems()) {
                pstmtItem.setString(1, penerimaan.getNoTerima());
                pstmtItem.setString(2, item.getNamaBarang());
                pstmtItem.setInt(3, item.getQtyPO());
                pstmtItem.setInt(4, item.getQtyDiterima());
                pstmtItem.setString(5, item.getSatuan());
                pstmtItem.setString(6, item.getKondisi());
                pstmtItem.setString(7, item.getKeterangan());
                pstmtItem.addBatch();
            }
            pstmtItem.executeBatch();
        }

        conn.commit(); 
        return true;

    } catch (SQLException e) {
        System.err.println("Gagal menyimpan data penerimaan: " + e.getMessage());
        e.printStackTrace();
        return false;
    }

}



public static List<PenerimaanItem> getItemsByNoTerima(String noTerima) {
    List<PenerimaanItem> items = new ArrayList<>();
    String sql = "SELECT * FROM penerimaan_items WHERE no_terima = ?";

    try (Connection conn = connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, noTerima);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            PenerimaanItem item = new PenerimaanItem(
                rs.getString("nama_barang"),
                rs.getInt("qty_po"),
                rs.getInt("qty_diterima"),
                rs.getString("satuan"),
                rs.getString("kondisi"),
                rs.getString("keterangan")
            );
            items.add(item);
        }
    } catch (SQLException e) {
        System.err.println("Error fetching items: " + e.getMessage());
    }

    return items;
}
public static List<Penerimaan> getAllPenerimaan() {
    List<Penerimaan> list = new ArrayList<>();

    String sqlPenerimaan = "SELECT * FROM penerimaan ORDER BY tanggal DESC";
    String sqlItems = "SELECT * FROM penerimaan_items WHERE no_terima = ?";

    try (Connection conn = connect();
         PreparedStatement pstmtPenerimaan = conn.prepareStatement(sqlPenerimaan)) {

        ResultSet rsPenerimaan = pstmtPenerimaan.executeQuery();

        while (rsPenerimaan.next()) {
            String noTerima = rsPenerimaan.getString("no_terima");
            String noPO = rsPenerimaan.getString("no_po");
            String supplier = rsPenerimaan.getString("supplier");
            LocalDate tanggal = LocalDate.parse(rsPenerimaan.getString("tanggal"));
            String catatan = rsPenerimaan.getString("catatan");

            List<PenerimaanItem> items = new ArrayList<>();

            try (PreparedStatement pstmtItems = conn.prepareStatement(sqlItems)) {
                pstmtItems.setString(1, noTerima);
                ResultSet rsItems = pstmtItems.executeQuery();

                while (rsItems.next()) {
                    String namaBarang = rsItems.getString("nama_barang");
                    int qtyPO = rsItems.getInt("qty_po");
                    int qtyDiterima = rsItems.getInt("qty_diterima");
                    String satuan = rsItems.getString("satuan");
                    String kondisi = rsItems.getString("kondisi");
                    String keterangan = rsItems.getString("keterangan");

                    PenerimaanItem item = new PenerimaanItem(namaBarang, qtyPO, qtyDiterima, satuan, kondisi, keterangan);
                    items.add(item);
                }
            }

            Penerimaan penerimaan = new Penerimaan(noTerima, noPO, supplier, tanggal, catatan, items);
            list.add(penerimaan);
        }

    } catch (SQLException e) {
        System.err.println("Gagal mengambil data penerimaan: " + e.getMessage());
        e.printStackTrace();
    }

    return list;
}




public static PurchaseOrder getPOByNumber(String poNumber) {
    if (poNumber == null || poNumber.trim().isEmpty()) {
        System.err.println("PO Number tidak boleh kosong");
        return null;
    }
    
    PurchaseOrder po = null;
    String query = "SELECT po_number, supplier, date, total, status FROM purchase_orders WHERE po_number = ?";
    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
        conn = Database.connect();
        if (conn == null) {
            System.err.println("Koneksi database gagal");
            return null;
        }
        
        stmt = conn.prepareStatement(query);
        stmt.setString(1, poNumber.trim());
        rs = stmt.executeQuery();
        
        if (rs.next()) {
            po = new PurchaseOrder();
            po.setPoNumber(rs.getString("po_number"));
            po.setSupplier(rs.getString("supplier"));
            
            // Handle date dengan null check
            java.sql.Date sqlDate = rs.getDate("date");
            if (sqlDate != null) {
                po.setDate(sqlDate.toLocalDate());
            }
            
            po.setTotal(rs.getDouble("total"));
            po.setStatus(rs.getString("status"));
            
            // Ambil items secara terpisah dengan koneksi yang sama
            List<POItem> items = getItemsByPONumber(poNumber.trim(), conn);
            po.setItems(items != null ? items : new ArrayList<>());
            
            System.out.println("PO berhasil ditemukan: " + poNumber + " dengan status: " + po.getStatus());
        } else {
            System.out.println("PO tidak ditemukan: " + poNumber);
        }
        
    } catch (SQLException e) {
        System.err.println("Error saat mengambil PO " + poNumber + ": " + e.getMessage());
        e.printStackTrace();
        po = null;
    } finally {
        // Tutup resources secara manual
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error saat menutup koneksi: " + e.getMessage());
        }
    }
    
    return po;
}


