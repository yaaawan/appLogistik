package pergudangan.model;
public class POItem {
    private String id;
    private String nama;
    private int qty;
    private String satuan;
    private double harga;

    public POItem(String id, String nama, int qty, String satuan, double harga) {
        this.id = id;
        this.nama = nama;
        this.qty = qty;
        this.satuan = satuan;
        this.harga = harga;
    }
       public void setNama(String nama) {
        this.nama = nama;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }
    
    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public int getQty() {
        return qty;
    }

    public String getSatuan() {
        return satuan;
    }

    public double getHarga() {
        return harga;
    }

    public double getSubtotal() {
        return qty * harga;
    }

}
