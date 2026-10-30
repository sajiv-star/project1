import java.util.*;

public class RetailPOS {
    static Scanner sc = new Scanner(System.in);
    static List<Product> products = new ArrayList<>();
    static List<Supplier> suppliers = new ArrayList<>();
    static List<PurchaseOrder> orders = new ArrayList<>();
    static TaxCategory[] taxes = {
        new TaxCategory("Exempt", 0), new TaxCategory("Essential", 5),
        new TaxCategory("Standard", 18), new TaxCategory("Luxury", 28)
    };
    static int saleNo = 5001, poNo = 1001;

    // ----- validated input helpers -----
    static int readInt(String msg, int min, int max) {
        while (true) {
            System.out.print(msg);
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                if (v < min || v > max) throw new InvalidInputException("Enter " + min + " to " + max);
                return v;
            } catch (NumberFormatException e) {
                System.out.println("  ! Enter a whole number.");
            } catch (InvalidInputException e) {
                System.out.println("  ! " + e.getMessage());
            }
        }
    }

    static double readPositive(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                double v = Double.parseDouble(sc.nextLine().trim());
                if (v <= 0) throw new InvalidInputException("Value must be greater than 0");
                return v;
            } catch (NumberFormatException e) {
                System.out.println("  ! Enter a valid number.");
            } catch (InvalidInputException e) {
                System.out.println("  ! " + e.getMessage());
            }
        }
    }

    static String readText(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("  ! Cannot be empty.");
        }
    }

    static String readPhone(String msg) {
        while (true) {
            String s = readText(msg);
            if (s.matches("\\d{10}")) return s;
            System.out.println("  ! Phone must be 10 digits.");
        }
    }

    static boolean readYes(String msg) {
        while (true) {
            String s = readText(msg + " (y/n): ").toLowerCase();
            if (s.equals("y")) return true;
            if (s.equals("n")) return false;
            System.out.println("  ! Enter y or n.");
        }
    }

    static Product findProduct(int id) throws InvalidInputException {
        for (Product p : products) if (p.getId() == id) return p;
        throw new InvalidInputException("Product " + id + " not found.");
    }

    static Supplier findSupplier(int id) throws InvalidInputException {
        for (Supplier s : suppliers) if (s.getId() == id) return s;
        throw new InvalidInputException("Supplier " + id + " not found.");
    }

    // ----- menu actions -----
    static void addProduct() {
        String name = readText("Product name: ");
        double price = readPositive("Selling price: ");
        double cost = readPositive("Cost price: ");
        int stock = readInt("Opening stock: ", 0, 100000);
        for (int i = 0; i < taxes.length; i++)
            System.out.println("  " + (i + 1) + ". " + taxes[i].getName() + " " + taxes[i].getRate() + "%");
        TaxCategory t = taxes[readInt("Tax category: ", 1, taxes.length) - 1];
        Product p = new Product(products.size() + 1, name, price, cost, stock, t);
        products.add(p);
        System.out.println("Product added with ID " + p.getId());
    }

    static void addSupplier() {
        String name = readText("Supplier name: ");
        String phone = readPhone("Phone (10 digits): ");
        Supplier s = new Supplier(suppliers.size() + 1, name, phone);
        suppliers.add(s);
        System.out.println("Supplier added with ID " + s.getId());
    }

    static void createPurchaseOrder() throws InvalidInputException {
        if (suppliers.isEmpty() || products.isEmpty())
            throw new InvalidInputException("Add at least one supplier and one product first.");
        for (Supplier s : suppliers) System.out.println("  " + s.getId() + ". " + s.getName());
        Supplier sup = findSupplier(readInt("Supplier ID: ", 1, 100000));
        PurchaseOrder po = new PurchaseOrder(poNo, sup);
        do {
            try {
                Product p = findProduct(readInt("Product ID: ", 1, 100000));
                int q = readInt("Quantity: ", 1, 100000);
                double c = readPositive("Unit cost: ");
                po.items.add(new PurchaseOrderItem(p, q, c));
            } catch (InvalidInputException e) {
                System.out.println("  ! " + e.getMessage());
            }
        } while (readYes("Add another item?"));
        if (po.items.isEmpty()) throw new InvalidInputException("Order has no items; cancelled.");
        orders.add(po);
        poNo++;
        System.out.println("Purchase order " + po.id + " created (stock updates on receipt).");
    }

    static void receivePurchase() throws InvalidInputException {
        boolean any = false;
        for (PurchaseOrder o : orders)
            if (!o.received) { any = true; System.out.println("  PO " + o.id + " - " + o.supplier.getName()); }
        if (!any) throw new InvalidInputException("No pending purchase orders.");
        int id = readInt("PO number: ", 1, 1000000);
        PurchaseOrder po = null;
        for (PurchaseOrder o : orders) if (o.id == id && !o.received) po = o;
        if (po == null) throw new InvalidInputException("No pending order with that number.");

        double sub = 0, tax = 0;
        System.out.println("\n===== PURCHASE RECEIPT  PO " + po.id + " | Supplier: " + po.supplier.getName() + " =====");
        System.out.printf("%-4s %-16s %4s %9s %8s %10s%n", "ID", "Item", "Qty", "Cost", "Tax", "Total");
        for (PurchaseOrderItem i : po.items) {
            i.product.addStock(i.qty);                // purchase increases stock on receipt
            System.out.printf("%-4d %-16s %4d %9.2f %8.2f %10.2f%n",
                    i.product.getId(), i.product.getName(), i.qty, i.unitPrice, i.tax(), i.total());
            sub += i.taxable(); tax += i.tax();
        }
        po.received = true;
        System.out.printf("Subtotal: %.2f   Tax: %.2f   TOTAL: %.2f%n", sub, tax, sub + tax);
        for (PurchaseOrderItem i : po.items)
            System.out.println("Updated stock: " + i.product.getName() + " = " + i.product.getStock());
    }

    static void makeSale() throws InvalidInputException {
        if (products.isEmpty()) throw new InvalidInputException("No products available.");
        Sale sale = new Sale(saleNo);
        do {
            try {
                Product p = findProduct(readInt("Product ID: ", 1, 100000));
                int q = readInt("Quantity: ", 1, 100000);
                if (sale.qtyOf(p) + q > p.getStock())    // stock check before confirmation
                    throw new InsufficientStockException("Only " + p.getStock() + " of " + p.getName() + " in stock.");
                sale.items.add(new SaleItem(p, q));
            } catch (InvalidInputException | InsufficientStockException e) {
                System.out.println("  ! " + e.getMessage());
            }
        } while (readYes("Add another item?"));
        if (sale.items.isEmpty()) throw new InvalidInputException("No items; sale cancelled.");
        if (!readYes("Confirm sale")) { System.out.println("Sale cancelled."); return; }

        double sub = 0, disc = 0, tax = 0;
        System.out.println("\n===== SALE BILL  No " + sale.id + " =====");
        System.out.printf("%-4s %-14s %4s %9s %8s %8s %10s%n", "ID", "Item", "Qty", "Price", "Disc", "Tax", "Total");
        for (SaleItem i : sale.items) {
            i.product.removeStock(i.qty);             // sale decreases stock immediately
            System.out.printf("%-4d %-14s %4d %9.2f %8.2f %8.2f %10.2f%n", i.product.getId(), i.product.getName(),
                    i.qty, i.unitPrice, i.discount(), i.tax(), i.total());
            sub += i.unitPrice * i.qty; disc += i.discount(); tax += i.tax();
        }
        System.out.printf("Subtotal: %.2f   Discount: %.2f   Tax: %.2f%nTOTAL: %.2f%n", sub, disc, tax, sub - disc + tax);
        for (SaleItem i : sale.items)
            System.out.println("Updated stock: " + i.product.getName() + " = " + i.product.getStock());
        saleNo++;
    }

    static void displayInventory() {
        System.out.println("\n===== INVENTORY =====");
        System.out.printf("%-4s %-16s %9s %9s %-14s %6s%n", "ID", "Product", "Price", "Cost", "Tax", "Stock");
        for (Product p : products)
            System.out.printf("%-4d %-16s %9.2f %9.2f %-14s %6d%n", p.getId(), p.getName(), p.getPrice(),
                    p.getCost(), p.getTax().getName() + " " + p.getTax().getRate() + "%", p.getStock());
        if (products.isEmpty()) System.out.println("(no products)");
    }

    public static void main(String[] args) {
        int choice = 0;
        try {
            while (choice != 7) {
                System.out.println("\n1. Add Product\n2. Add Supplier\n3. Create Purchase Order"
                        + "\n4. Receive Purchase\n5. Make Sale\n6. Display Inventory\n7. Exit");
                choice = readInt("Enter choice: ", 1, 7);
                try {
                    switch (choice) {
                        case 1: addProduct(); break;
                        case 2: addSupplier(); break;
                        case 3: createPurchaseOrder(); break;
                        case 4: receivePurchase(); break;
                        case 5: makeSale(); break;
                        case 6: displayInventory(); break;
                        default: System.out.println("Goodbye!");
                    }
                } catch (InvalidInputException e) {
                    System.out.println("  ! " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nInput ended. Exiting.");
        }
    }
}
