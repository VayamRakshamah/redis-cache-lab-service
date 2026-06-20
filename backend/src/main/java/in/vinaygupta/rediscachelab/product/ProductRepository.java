package in.vinaygupta.rediscachelab.product;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import in.vinaygupta.rediscachelab.config.CacheLabProperties;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository {
    private final Duration simulatedLatency;
    private final Map<String, Product> products = Map.of(
            "P1001", new Product("P1001", "Wireless Inventory Scanner", "Operations", new BigDecimal("1299.00"), 42, "BLR-01"),
            "P1002", new Product("P1002", "Cold Chain Sensor", "Pharmacy", new BigDecimal("849.00"), 118, "MUM-02"),
            "P1003", new Product("P1003", "Fulfilment Label Printer", "Logistics", new BigDecimal("2499.00"), 16, "DEL-03"),
            "P1004", new Product("P1004", "Store Pickup Kiosk", "Retail", new BigDecimal("3999.00"), 8, "HYD-04")
    );

    public ProductRepository(CacheLabProperties properties) {
        this.simulatedLatency = properties.simulatedDbLatency();
    }

    public Optional<Product> findById(String productId) {
        sleepForSimulatedDatabaseCost();
        return Optional.ofNullable(products.get(productId.toUpperCase(Locale.ROOT)));
    }

    private void sleepForSimulatedDatabaseCost() {
        try {
            Thread.sleep(simulatedLatency.toMillis());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}
