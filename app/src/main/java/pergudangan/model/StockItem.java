package pergudangan.model;

public class StockItem {
    private String itemName;
    private int quantity;
    private String unit;
    private double purchasePrice;
    private double sellingPrice;
    private String category;

    public StockItem(String itemName, int quantity, String unit, double purchasePrice, double sellingPrice, String category) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.unit = unit;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.category = category;
    }

    // Getter
    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
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

    // Setter
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
}
