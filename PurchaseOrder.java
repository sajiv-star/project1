import java.util.*;

public class PurchaseOrder {
    int id;
    Supplier supplier;                                // aggregation: required
    List<PurchaseOrderItem> items = new ArrayList<>();
    boolean received = false;
    PurchaseOrder(int id, Supplier s) { this.id = id; this.supplier = s; }
}

// ---------- Main program ----------
