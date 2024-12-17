public class Product {
    private final String name;
    private final String category;
    private final double price;
    private int quantity;


    public Product(String name, String category, double price, int quantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }


    public String getName() {
        return name;
    }


    public String getCategory() {
        return category;
    }


    public double getPrice() {
        return price;
    }


    public int getQuantity() {
        return quantity;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}




