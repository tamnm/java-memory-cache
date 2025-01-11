package org.example.cache;

/**
 * Interface for providing cache instances with different eviction policies.
 */
public interface CacheProvider {

    /**
     * Retrieves a memory cache instance configured with the specified eviction policy.
     *
     * @param cachePreference the preference of the Cache being created
     * @param <K> the type of keys maintained by the cache
     * @param <V> the type of mapped values
     * @return a memory cache instance with the specified eviction policy
     */
    <K, V> MemoryCache<K, V> getCache(CachePreference cachePreference);
}