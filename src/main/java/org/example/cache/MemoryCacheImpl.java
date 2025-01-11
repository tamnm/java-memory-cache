package org.example.cache;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

class MemoryCacheImpl<K, V> implements MemoryCache<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final EvictionPolicyHandler<K> evictionPolicy;

    public MemoryCacheImpl(EvictionPolicyHandler<K> evictionPolicy) {
        this.evictionPolicy = Objects.requireNonNull(evictionPolicy, "Eviction policy cannot be null");
    }

    @Override
    public Optional<V> get(K key, Function<K, Optional<V>> loader) {
        Objects.requireNonNull(key, "Key cannot be null");
        V value = cache.get(key);
        if (value != null) {
            evictionPolicy.onAccess(key);
            return Optional.of(value);
        }

        try {
            Optional<V> loadedValue = loader.apply(key);
            loadedValue.ifPresent(val -> put(key, val));
            return loadedValue;
        } catch (Exception e) {
            // Log the exception and rethrow as needed
            throw new RuntimeException("Error during loading", e);
        }
    }

    @Override
    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "Key cannot be null");
        V value = cache.get(key);
        if (value != null) {
            evictionPolicy.onAccess(key);
        }
        return Optional.ofNullable(value);
    }

    @Override
    public CompletableFuture<Optional<V>> getAsync(K key, Function<K, CompletableFuture<Optional<V>>> loader) {
        Objects.requireNonNull(key, "Key cannot be null");

        V value = cache.get(key);
        if (value != null) {
            evictionPolicy.onAccess(key);
            return CompletableFuture.completedFuture(Optional.of(value));
        }

        return loader.apply(key).thenApply(loadedValue -> {
            loadedValue.ifPresent(val -> put(key, val));
            return loadedValue;
        });
    }

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");

        K evictedKey = evictionPolicy.onPut(key);
        if (evictedKey != null) {
            cache.remove(evictedKey);
        }
        cache.put(key, value);
    }

    @Override
    public void remove(K key) {
        Objects.requireNonNull(key, "Key cannot be null");
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public boolean containsKey(K key) {
        Objects.requireNonNull(key, "Key cannot be null");
        return cache.containsKey(key);
    }
}