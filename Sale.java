import java.util.*;

public class Sale {
    int id;
    List<SaleItem> items = new ArrayList<>();
    Sale(int id) { this.id = id; }
    int qtyOf(Product p) {
        int n = 0;
        for (SaleItem i : items) if (i.product == p) n += i.qty;
        return n;
    }
}
