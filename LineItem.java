import java.util.*;

public abstract class LineItem {
    protected Product product;
    protected int qty;
    protected double unitPrice;

    LineItem(Product product, int qty, double unitPrice) {
        this.product = product; this.qty = qty; this.unitPrice = unitPrice;
    }
    abstract double discount();                       // polymorphic
    double taxable() { return unitPrice * qty - discount(); }
    double tax() { return taxable() * product.getTax().getRate() / 100; }
    double total() { return taxable() + tax(); }
}
