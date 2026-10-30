import java.util.*;

public class TaxCategory {
    private String name;
    private double rate; // percent

    TaxCategory(String name, double rate) { this.name = name; this.rate = rate; }
    String getName() { return name; }
    double getRate() { return rate; }
}
