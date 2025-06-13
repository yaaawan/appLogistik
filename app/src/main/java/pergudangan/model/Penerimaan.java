package pergudangan.model;

import java.time.LocalDate;
import java.util.List;
import pergudangan.service.Database;

public class Penerimaan {
    private String noTerima;
    private String noPO;
    private String supplier;
    private LocalDate tanggal;
    private String catatan;

    private List<PenerimaanItem> items;

    public Penerimaan() {}

    public Penerimaan(String noTerima, String noPO, String supplier, LocalDate tanggal, String catatan, List<PenerimaanItem> items) {
        this.noTerima = noTerima;
        this.noPO = noPO;
        this.supplier = supplier;
        this.tanggal = tanggal;
        this.catatan = catatan;
        this.items = items;
    }

    public String getNoTerima() { return noTerima; }
    public String getNoPO() { return noPO; }
    public String getSupplier() { return supplier; }
    public LocalDate getTanggal() { return tanggal; }
    public String getCatatan() { return catatan; }
    public List<PenerimaanItem> getItems() { return items; }

    public void setNoTerima(String noTerima) { this.noTerima = noTerima; }
    public void setNoPO(String noPO) { this.noPO = noPO; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
    public void setItems(List<PenerimaanItem> items) { this.items = items; }
    
    

    public void periksaPenerimaan() {
        System.out.println("Pemeriksaan PO: " + noPO);

        int totalQtyPO = 0;
        int totalQtyDiterima = 0;

        for (PenerimaanItem item : items) {
            totalQtyPO += item.getQtyPO();
            totalQtyDiterima += item.getQtyDiterima();

            if (item.getQtyDiterima() == item.getQtyPO()) {
                System.out.println("Barang " + item.getNamaBarang() + " sudah lengkap diterima.");
            } else if (item.getQtyDiterima() == 0) {
                System.out.println("Barang " + item.getNamaBarang() + " dikembalikan.");
            } else {
                System.out.println("Barang " + item.getNamaBarang() + " sebagian diterima.");
            }
        }

        String statusFinal = (totalQtyDiterima == totalQtyPO) ? "Selesai" : "Dikembalikan";
        boolean updated = Database.updatePOStatus(noPO, statusFinal);

        if (updated) {
            System.out.println("Status PO diperbarui menjadi: " + statusFinal);
        } else {
            System.err.println("Gagal memperbarui status PO.");
        }
    }
    


    @Override
    public String toString() {
        return "Penerimaan{" +
                "noTerima='" + noTerima + '\'' +
                ", noPO='" + noPO + '\'' +
                ", supplier='" + supplier + '\'' +
                ", tanggal=" + tanggal +
                ", catatan='" + catatan + '\'' +
                ", items=" + items +
                '}';
    }
}
