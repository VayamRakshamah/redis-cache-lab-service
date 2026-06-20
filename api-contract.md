# API Contract

Base URL: `http://localhost:8080/api`

## Get Product

```http
GET /products/{productId}
```

Optional query params:

| Name | Type | Default | Purpose |
|---|---:|---:|---|
| `bypassCache` | boolean | `false` | Skip Redis read, load from repository, and refresh cache. |

Product IDs seeded in the lab:

```text
P1001
P1002
P1003
P1004
```

Success response:

```json
{
  "productId": "P1001",
  "name": "Wireless Inventory Scanner",
  "category": "Operations",
  "price": 1299.00,
  "inventory": 42,
  "storeId": "BLR-01",
  "source": "CACHE_MISS",
  "latencyMs": 557,
  "ttlSeconds": 60
}
```

Headers:

```http
X-Cache-Status: CACHE_MISS
X-Product-TTL-Seconds: 60
X-Origin-Latency-Ms: 557
```

## Clear Product Cache

```http
DELETE /cache/products/{productId}
```

Returns `204 No Content`.

## Cache Metrics

```http
GET /metrics/cache
```

Success response:

```json
{
  "hits": 1,
  "misses": 2,
  "bypasses": 1,
  "totalRequests": 4,
  "hitRate": 0.25
}
```
