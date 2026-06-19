package in.vinaygupta.rediscachelab.cache;

import java.time.Duration;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.vinaygupta.rediscachelab.config.CacheLabProperties;
import in.vinaygupta.rediscachelab.product.Product;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductCache {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration ttl;
    private final String keyPrefix;

    public ProductCache(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, CacheLabProperties properties) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.ttl = properties.productCache().ttl();
        this.keyPrefix = properties.productCache().keyPrefix();
    }

    public Optional<Product> get(String productId) {
        String json = redisTemplate.opsForValue().get(key(productId));
        if (json == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(json, Product.class));
        } catch (JsonProcessingException exception) {
            evict(productId);
            return Optional.empty();
        }
    }

    public void put(Product product) {
        try {
            redisTemplate.opsForValue().set(key(product.id()), objectMapper.writeValueAsString(product), ttl);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize product " + product.id(), exception);
        }
    }

    public void evict(String productId) {
        redisTemplate.delete(key(productId));
    }

    public long ttlSeconds(String productId) {
        Long seconds = redisTemplate.getExpire(key(productId));
        return seconds == null || seconds < 0 ? 0 : seconds;
    }

    private String key(String productId) {
        return keyPrefix + productId.toUpperCase();
    }
}
