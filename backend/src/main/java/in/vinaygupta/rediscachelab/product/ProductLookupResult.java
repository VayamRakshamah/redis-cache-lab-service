package in.vinaygupta.rediscachelab.product;

public record ProductLookupResult(
        Product product,
        ProductSource source,
        long latencyMs,
        long ttlSeconds
) {
}
