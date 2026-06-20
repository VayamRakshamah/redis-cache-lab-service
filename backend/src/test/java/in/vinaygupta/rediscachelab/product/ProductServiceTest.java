package in.vinaygupta.rediscachelab.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import in.vinaygupta.rediscachelab.cache.ProductCache;
import in.vinaygupta.rediscachelab.metrics.CacheMetrics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    private static final Product PRODUCT = new Product("P1001", "Wireless Inventory Scanner", "Operations",
            new BigDecimal("1299.00"), 42, "BLR-01");

    @Mock
    private ProductCache cache;

    @Mock
    private ProductRepository repository;

    @Mock
    private CacheMetrics metrics;

    @InjectMocks
    private ProductService productService;

    @Test
    void returnsCacheHitWhenProductExistsInRedis() {
        when(cache.get("P1001")).thenReturn(Optional.of(PRODUCT));
        when(cache.ttlSeconds("P1001")).thenReturn(45L);

        ProductLookupResult result = productService.findProduct("P1001", false);

        assertThat(result.source()).isEqualTo(ProductSource.CACHE_HIT);
        assertThat(result.product()).isEqualTo(PRODUCT);
        assertThat(result.ttlSeconds()).isEqualTo(45L);
        verify(metrics).record(ProductSource.CACHE_HIT);
    }

    @Test
    void loadsFromRepositoryAndWritesRedisOnCacheMiss() {
        when(cache.get("P1001")).thenReturn(Optional.empty());
        when(repository.findById("P1001")).thenReturn(Optional.of(PRODUCT));
        when(cache.ttlSeconds("P1001")).thenReturn(60L);

        ProductLookupResult result = productService.findProduct("P1001", false);

        assertThat(result.source()).isEqualTo(ProductSource.CACHE_MISS);
        verify(cache).put(PRODUCT);
        verify(metrics).record(ProductSource.CACHE_MISS);
    }

    @Test
    void bypassSkipsReadButRefreshesCache() {
        when(repository.findById("P1001")).thenReturn(Optional.of(PRODUCT));

        ProductLookupResult result = productService.findProduct("P1001", true);

        assertThat(result.source()).isEqualTo(ProductSource.CACHE_BYPASS);
        verify(cache).put(PRODUCT);
        verify(metrics).record(ProductSource.CACHE_BYPASS);
    }
}
