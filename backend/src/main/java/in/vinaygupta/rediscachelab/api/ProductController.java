package in.vinaygupta.rediscachelab.api;

import in.vinaygupta.rediscachelab.metrics.CacheMetrics;
import in.vinaygupta.rediscachelab.metrics.CacheMetricsSnapshot;
import in.vinaygupta.rediscachelab.product.ProductLookupResult;
import in.vinaygupta.rediscachelab.product.ProductResponse;
import in.vinaygupta.rediscachelab.product.ProductService;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api")
public class ProductController {
    private static final String PRODUCT_ID_PATTERN = "^[A-Za-z][A-Za-z0-9_-]{2,24}$";

    private final ProductService productService;
    private final CacheMetrics cacheMetrics;

    public ProductController(ProductService productService, CacheMetrics cacheMetrics) {
        this.productService = productService;
        this.cacheMetrics = cacheMetrics;
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable @Pattern(regexp = PRODUCT_ID_PATTERN) String productId,
            @RequestParam(defaultValue = "false") boolean bypassCache
    ) {
        ProductLookupResult result = productService.findProduct(productId, bypassCache);
        return ResponseEntity.ok()
                .header("X-Cache-Status", result.source().name())
                .header("X-Product-TTL-Seconds", Long.toString(result.ttlSeconds()))
                .header("X-Origin-Latency-Ms", Long.toString(result.latencyMs()))
                .body(ProductResponse.from(result));
    }

    @DeleteMapping("/cache/products/{productId}")
    public ResponseEntity<Void> evictProduct(@PathVariable @Pattern(regexp = PRODUCT_ID_PATTERN) String productId) {
        productService.evictProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/metrics/cache")
    public CacheMetricsSnapshot cacheMetrics() {
        return cacheMetrics.snapshot();
    }
}
