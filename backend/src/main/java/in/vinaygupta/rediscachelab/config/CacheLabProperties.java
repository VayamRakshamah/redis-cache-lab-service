package in.vinaygupta.rediscachelab.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache-lab")
public record CacheLabProperties(
        Cors cors,
        ProductCache productCache,
        Duration simulatedDbLatency
) {
    public record Cors(List<String> allowedOrigins) {
    }

    public record ProductCache(Duration ttl, String keyPrefix) {
    }
}
