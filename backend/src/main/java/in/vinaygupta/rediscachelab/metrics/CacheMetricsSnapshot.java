package in.vinaygupta.rediscachelab.metrics;

public record CacheMetricsSnapshot(
        long hits,
        long misses,
        long bypasses,
        long totalRequests,
        double hitRate
) {
}
