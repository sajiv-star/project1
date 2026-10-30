import java.util.*;

public class PurchaseOrderItem extends LineItem {
    PurchaseOrderItem(Product p, int qty, double cost) { super(p, qty, cost); }
    double discount() { return 0; }
}
