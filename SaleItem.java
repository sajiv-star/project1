import java.util.*;

public class SaleItem extends LineItem {
    SaleItem(Product p, int qty) { super(p, qty, p.getPrice()); }
    double discount() { return unitPrice * qty * product.discountRate(qty); }
}
