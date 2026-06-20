const API_BASE = "http://localhost:8080/api";

const els = {
  productId: document.querySelector("#productId"),
  fetchButton: document.querySelector("#fetchButton"),
  fetchAgainButton: document.querySelector("#fetchAgainButton"),
  bypassButton: document.querySelector("#bypassButton"),
  clearButton: document.querySelector("#clearButton"),
  sourceBadge: document.querySelector("#sourceBadge"),
  productName: document.querySelector("#productName"),
  latencyMs: document.querySelector("#latencyMs"),
  productCode: document.querySelector("#productCode"),
  category: document.querySelector("#category"),
  price: document.querySelector("#price"),
  inventory: document.querySelector("#inventory"),
  storeId: document.querySelector("#storeId"),
  ttlSeconds: document.querySelector("#ttlSeconds"),
  ttlBadge: document.querySelector("#ttlBadge"),
  hitRate: document.querySelector("#hitRate"),
  totalRequests: document.querySelector("#totalRequests"),
  timeline: document.querySelector("#timeline")
};

async function fetchProduct({ bypassCache = false } = {}) {
  setBusy(true);
  const productId = els.productId.value;
  const url = new URL(`${API_BASE}/products/${encodeURIComponent(productId)}`);
  if (bypassCache) {
    url.searchParams.set("bypassCache", "true");
  }

  try {
    const response = await fetch(url);
    const payload = await response.json();
    if (!response.ok) {
      throw new Error(payload.message || "Request failed");
    }
    renderProduct(payload);
    await refreshMetrics();
  } catch (error) {
    renderError(error);
  } finally {
    setBusy(false);
  }
}

async function clearCache() {
  setBusy(true);
  try {
    const response = await fetch(`${API_BASE}/cache/products/${encodeURIComponent(els.productId.value)}`, {
      method: "DELETE"
    });
    if (!response.ok) {
      let message = "Unable to clear cache";
      try {
        const payload = await response.json();
        message = payload.message || message;
      } catch {
      }
      throw new Error(message);
    }
    els.sourceBadge.textContent = "Cache cleared";
    els.sourceBadge.className = "badge neutral";
    els.latencyMs.textContent = "--";
    els.ttlSeconds.textContent = "0 sec";
    els.ttlBadge.textContent = "0s";
    els.timeline.className = "timeline";
  } catch (error) {
    renderError(error);
  } finally {
    setBusy(false);
  }
}

async function refreshMetrics() {
  const response = await fetch(`${API_BASE}/metrics/cache`);
  if (!response.ok) {
    return;
  }
  const metrics = await response.json();
  els.hitRate.textContent = `${Math.round(metrics.hitRate * 100)}%`;
  els.totalRequests.textContent = metrics.totalRequests;
}

function renderProduct(product) {
  const source = product.source;
  els.sourceBadge.textContent = source.replace("CACHE_", "").replace("_", " ");
  els.sourceBadge.className = `badge ${badgeClass(source)}`;
  els.productName.textContent = product.name;
  els.latencyMs.textContent = product.latencyMs;
  els.productCode.textContent = product.productId;
  els.category.textContent = product.category;
  els.price.textContent = formatPrice(product.price);
  els.inventory.textContent = product.inventory;
  els.storeId.textContent = product.storeId;
  els.ttlSeconds.textContent = `${product.ttlSeconds} sec`;
  els.ttlBadge.textContent = `${product.ttlSeconds}s`;
  els.timeline.className = `timeline ${badgeClass(source)}`;
}

function renderError(error) {
  els.sourceBadge.textContent = "Error";
  els.sourceBadge.className = "badge neutral";
  els.productName.textContent = error.message;
  els.latencyMs.textContent = "--";
  els.productCode.textContent = "--";
  els.category.textContent = "--";
  els.price.textContent = "--";
  els.inventory.textContent = "--";
  els.storeId.textContent = "--";
  els.ttlSeconds.textContent = "--";
  els.ttlBadge.textContent = "--";
  els.timeline.className = "timeline";
}

function setBusy(isBusy) {
  [els.fetchButton, els.fetchAgainButton, els.bypassButton, els.clearButton].forEach((button) => {
    button.disabled = isBusy;
  });
}

function badgeClass(source) {
  if (source === "CACHE_HIT") return "hit";
  if (source === "CACHE_MISS") return "miss";
  if (source === "CACHE_BYPASS") return "bypass";
  return "neutral";
}

function formatPrice(value) {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0
  }).format(value);
}

els.fetchButton.addEventListener("click", () => fetchProduct());
els.fetchAgainButton.addEventListener("click", () => fetchProduct());
els.bypassButton.addEventListener("click", () => fetchProduct({ bypassCache: true }));
els.clearButton.addEventListener("click", clearCache);

refreshMetrics().catch(() => {});
