import java.util.*;

public class Product {
    private int id;
    private String name;
    private double price, cost;
    private int stock;
    private TaxCategory tax;

    Product(int id, String name, double price, double cost, int stock, TaxCategory tax) {
        this.id = id; this.name = name; this.price = price;
        this.cost = cost; this.stock = stock; this.tax = tax;
    }
    int getId() { return id; }
    String getName() { return name; }
    double getPrice() { return price; }
    double getCost() { return cost; }
    int getStock() { return stock; }
    TaxCategory getTax() { return tax; }
    void addStock(int q) { stock += q; }
    void removeStock(int q) { stock -= q; }
    // Bulk discount: 10% when 10 or more units are bought
    double discountRate(int qty) { return qty >= 10 ? 0.10 : 0; }
}
