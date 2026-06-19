package in.vinaygupta.rediscachelab.product;

import in.vinaygupta.rediscachelab.cache.ProductCache;
import in.vinaygupta.rediscachelab.metrics.CacheMetrics;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
public class ProductService {
    private final ProductCache cache;
    private final ProductRepository repository;
    private final CacheMetrics metrics;

    public ProductService(ProductCache cache, ProductRepository repository, CacheMetrics metrics) {
        this.cache = cache;
        this.repository = repository;
        this.metrics = metrics;
    }

    public ProductLookupResult findProduct(String productId, boolean bypassCache) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ProductSource source = bypassCache ? ProductSource.CACHE_BYPASS : ProductSource.CACHE_HIT;
        Product product = null;

        if (!bypassCache) {
            product = cache.get(productId).orElse(null);
        }

        if (product == null) {
            source = bypassCache ? ProductSource.CACHE_BYPASS : ProductSource.CACHE_MISS;
            product = repository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
            cache.put(product);
        }

        stopWatch.stop();
        long ttlSeconds = cache.ttlSeconds(product.id());
        metrics.record(source);
        return new ProductLookupResult(product, source, stopWatch.getTotalTimeMillis(), ttlSeconds);
    }

    public void evictProduct(String productId) {
        cache.evict(productId);
    }
}
