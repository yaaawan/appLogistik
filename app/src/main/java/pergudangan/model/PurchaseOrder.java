package pergudangan.model;

import java.time.LocalDate;
import java.util.List;

public class PurchaseOrder {
    private String poNumber;
    private String supplier;
    private LocalDate date;
    private String status;
    private String keterangan;
    private double total;
    private List<POItem> items;
    public PurchaseOrder() {
    }

    public PurchaseOrder(String poNumber, String supplier, LocalDate date, String status, String keterangan, double total, List<POItem> items) {
        this.poNumber = poNumber;
        this.supplier = supplier;
        this.date = date;
        this.status = status;
        this.keterangan = keterangan;
        this.total = total;
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPoNumber() {
        return poNumber;
    }
    
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getSupplier() {
        return supplier;
    }
    
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
     public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getKeterangan() {
        return keterangan;
    }
    
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }


    public double getTotal() {
        return total;
    }

     public void setTotal(double total) {
        this.total = total;
    }


    public List<POItem> getItems() {
        return items;
    }
    
    public void setItems(List<POItem> items) {
        this.items = items; // Menambahkan setter untuk items
    }
    
    
 public String formatItemsText() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < items.size(); i++) {
        POItem item = items.get(i);
        sb.append(item.getId()).append(",")
          .append(item.getNama()).append(",")
          .append(item.getQty()).append(",")
          .append(item.getSatuan()).append(",")
          .append(item.getHarga());
        
        if (i < items.size() - 1) {
            sb.append(";");
        }
    }
    return sb.toString();
}

}
