# Run Locally

## Preferred Path

```bash
docker compose up --build
```

Open the dashboard at `http://localhost:3000`.

## Expected Demo

1. Select `P1001`.
2. Click `Fetch`.
3. First response should show `MISS` and a higher latency, roughly the configured simulated database latency.
4. Click `Fetch again`.
5. Second response should show `HIT` and much lower latency.
6. Click `Clear cache`.
7. Fetch again and confirm the response returns to `MISS`.

## Useful Commands

```bash
curl -i http://localhost:8080/api/products/P1001
curl -i http://localhost:8080/api/products/P1001
curl -i "http://localhost:8080/api/products/P1001?bypassCache=true"
curl -i -X DELETE http://localhost:8080/api/cache/products/P1001
curl http://localhost:8080/api/metrics/cache
```

## Tuning

The main demo knobs are environment variables:

```text
PRODUCT_CACHE_TTL=60s
SIMULATED_DB_LATENCY=550ms
REDIS_HOST=redis
REDIS_PORT=6379
```

Use a higher simulated database latency when recording the portfolio video so the cache hit improvement is immediately visible.
