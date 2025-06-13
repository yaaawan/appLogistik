package pergudangan.model;

import java.time.LocalDate;

public class Pengeluaran {
    private int id;              
    private String itemName;
    private int quantity;
    private double totalPrice;
    private LocalDate date;
    private String category;     

    public Pengeluaran() {}


    public Pengeluaran(int id, String itemName, int quantity, double totalPrice, LocalDate date, String category) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.date = date;
        this.category = category;
    }

    // Getter dan setter id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
