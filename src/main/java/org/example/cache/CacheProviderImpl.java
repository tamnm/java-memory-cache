package org.example.cache;

import java.util.Objects;

public class CacheProviderImpl implements CacheProvider {
    @Override
    public <K, V> MemoryCache<K, V> getCache(CachePreference cachePreference) {
        Objects.requireNonNull(cachePreference, "Cache preference cannot be null");
        return switch (cachePreference.getEvictionPolicy()) {
            case LRU -> new MemoryCacheImpl<>(new LRUEvictionPolicy<>(cachePreference.getCapacity()));
            case LFU -> new MemoryCacheImpl<>(new LFUEvictionPolicy<>(cachePreference.getCapacity()));
            case FIFO -> new MemoryCacheImpl<>(new FIFOEvictionPolicy<>(cachePreference.getCapacity()));
            default ->
                    throw new IllegalArgumentException("Unsupported eviction policy: " + cachePreference.getEvictionPolicy());
        };
    }
}
