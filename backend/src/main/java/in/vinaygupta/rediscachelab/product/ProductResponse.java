package in.vinaygupta.rediscachelab.product;

import java.math.BigDecimal;

public record ProductResponse(
        String productId,
        String name,
        String category,
        BigDecimal price,
        int inventory,
        String storeId,
        ProductSource source,
        long latencyMs,
        long ttlSeconds
) {
    public static ProductResponse from(ProductLookupResult result) {
        Product product = result.product();
        return new ProductResponse(
                product.id(),
                product.name(),
                product.category(),
                product.price(),
                product.inventory(),
                product.storeId(),
                result.source(),
                result.latencyMs(),
                result.ttlSeconds()
        );
    }
}
