package pergudangan.model;

import java.math.BigDecimal;

public class PenerimaanItem {
    private int id;
    private String kodeBarang;
    private String noTerima;
    private String status;
    private String namaBarang;
    private int qtyPO;
    private int qtyDiterima;
    private String satuan;
    private String kondisi;
    private String keterangan;
    private BigDecimal hargaSatuan;
    private BigDecimal totalHarga;

    private double purchasePrice;
    private double sellingPrice;
    private String category;

    public PenerimaanItem() {}

    public PenerimaanItem(String namaBarang, int qtyPO, int qtyDiterima, String satuan, String kondisi, String keterangan) {
        this.namaBarang = namaBarang;
        this.qtyPO = qtyPO;
        this.qtyDiterima = qtyDiterima;
        this.satuan = satuan;
        this.kondisi = kondisi;
        this.keterangan = keterangan;
    }
    
    public void hitungHarga(BigDecimal hargaSatuan, double purchasePrice, double sellingPrice, String category) {
    this.hargaSatuan = hargaSatuan;
    this.totalHarga = hargaSatuan.multiply(BigDecimal.valueOf(qtyDiterima));
    this.purchasePrice = purchasePrice;
    this.sellingPrice = sellingPrice;
    this.category = category;
}


    public int getId() {
        return id;
    }

    public String getKodeBarang() {
        return kodeBarang;
    }

    public String getNoTerima() {
        return noTerima;
    }

    public String getStatus() {
        return status;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public int getQtyPO() {
        return qtyPO;
    }

    public int getQtyDiterima() {
        return qtyDiterima;
    }

    public String getSatuan() {
        return satuan;
    }

    public String getKondisi() {
        return kondisi;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public BigDecimal getHargaSatuan() {
        return hargaSatuan;
    }

    public BigDecimal getTotalHarga() {
        return totalHarga;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public String getCategory() {
        return category;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public void setNoTerima(String noTerima) {
        this.noTerima = noTerima;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public void setQtyPO(int qtyPO) {
        this.qtyPO = qtyPO;
    }

    public void setQtyDiterima(int qtyDiterima) {
        this.qtyDiterima = qtyDiterima;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public void setKondisi(String kondisi) {
        this.kondisi = kondisi;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setHargaSatuan(BigDecimal hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
    }

    public void setTotalHarga(BigDecimal totalHarga) {
        this.totalHarga = totalHarga;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    

    @Override
    public String toString() {
        return "PenerimaanItem{" +
                "id=" + id +
                ", kodeBarang='" + kodeBarang + '\'' +
                ", noTerima='" + noTerima + '\'' +
                ", status='" + status + '\'' +
                ", namaBarang='" + namaBarang + '\'' +
                ", qtyPO=" + qtyPO +
                ", qtyDiterima=" + qtyDiterima +
                ", satuan='" + satuan + '\'' +
                ", kondisi='" + kondisi + '\'' +
                ", keterangan='" + keterangan + '\'' +
                ", hargaSatuan=" + hargaSatuan +
                ", totalHarga=" + totalHarga +
                ", purchasePrice=" + purchasePrice +
                ", sellingPrice=" + sellingPrice +
                ", category='" + category + '\'' +
                '}';
    }
}
