package in.vinaygupta.rediscachelab.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache-lab")
public record CacheLabProperties(
        ProductCache productCache,
        Duration simulatedDbLatency
) {
    public record ProductCache(Duration ttl, String keyPrefix) {
    }
}
