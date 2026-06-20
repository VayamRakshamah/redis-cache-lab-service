package in.vinaygupta.rediscachelab.metrics;

import java.util.concurrent.atomic.AtomicLong;

import in.vinaygupta.rediscachelab.product.ProductSource;
import org.springframework.stereotype.Component;

@Component
public class CacheMetrics {
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final AtomicLong bypasses = new AtomicLong();

    public void record(ProductSource source) {
        switch (source) {
            case CACHE_HIT -> hits.incrementAndGet();
            case CACHE_MISS -> misses.incrementAndGet();
            case CACHE_BYPASS -> bypasses.incrementAndGet();
        }
    }

    public CacheMetricsSnapshot snapshot() {
        long hitCount = hits.get();
        long missCount = misses.get();
        long bypassCount = bypasses.get();
        long total = hitCount + missCount + bypassCount;
        double hitRate = total == 0 ? 0 : (double) hitCount / total;
        return new CacheMetricsSnapshot(hitCount, missCount, bypassCount, total, hitRate);
    }
}
