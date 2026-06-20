# Deploy Backend On Render

GitHub Pages can host the static dashboard, but the Spring Boot API and Redis need a backend host. This project is ready for a Render Web Service plus a Redis-compatible Render Key Value instance.

## Render Services

Create two Render resources:

1. Key Value instance for Redis-compatible storage.
2. Web Service for the Spring Boot backend.

For the Web Service:

```text
Runtime: Docker
Root directory: backend
Port: 8080
```

## Environment Variables

Set these on the Render Web Service:

```text
SERVER_PORT=8080
SPRING_DATA_REDIS_URL=<internal Redis URL from Render Key Value>
PRODUCT_CACHE_TTL=60s
SIMULATED_DB_LATENCY=550ms
CORS_ALLOWED_ORIGINS=https://vayamrakshamah.github.io,https://vinaygupta.in,http://vinaygupta.in
```

Use `SPRING_DATA_REDIS_URL` for hosted Redis because Spring Boot maps it to `spring.data.redis.url`. Local Docker Compose can continue using `REDIS_HOST` and `REDIS_PORT`.

## Smoke Test

After Render deploys the backend, run:

```bash
curl https://your-render-service.onrender.com/actuator/health
curl -i https://your-render-service.onrender.com/api/products/P1001
curl -i https://your-render-service.onrender.com/api/products/P1001
curl https://your-render-service.onrender.com/api/metrics/cache
```

The first product call should return `CACHE_MISS`; the second should return `CACHE_HIT`.

## Connect The GitHub Pages Dashboard

Open the dashboard with the hosted API URL:

```text
https://vayamrakshamah.github.io/redis-cache-lab-service/?api=https://your-render-service.onrender.com/api
```

If GitHub Pages redirects to the custom domain, use:

```text
https://vinaygupta.in/redis-cache-lab-service/?api=https://your-render-service.onrender.com/api
```
