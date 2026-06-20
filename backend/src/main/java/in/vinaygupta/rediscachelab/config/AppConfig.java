package in.vinaygupta.rediscachelab.config;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(CacheLabProperties.class)
public class AppConfig implements WebMvcConfigurer {
    private final CacheLabProperties properties;

    public AppConfig(CacheLabProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> allowedOrigins = properties.cors().allowedOrigins().stream()
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();

        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods("GET", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-Cache-Status", "X-Product-TTL-Seconds", "X-Origin-Latency-Ms");
    }
}
