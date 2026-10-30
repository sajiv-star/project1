import java.util.*;

public class Supplier {
    private int id;
    private String name, phone;

    Supplier(int id, String name, String phone) { this.id = id; this.name = name; this.phone = phone; }
    int getId() { return id; }
    String getName() { return name; }
    String getPhone() { return phone; }
}

// Base for SaleItem and PurchaseOrderItem (inheritance); each item links to a Product
