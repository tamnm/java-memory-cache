package org.example.cache;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Generic interface for a memory cache that supports read-through and asynchronous operations.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public interface MemoryCache<K, V> {

    /**
     * Retrieves a value from the cache.
     * If the value is not present, it uses the loader to fetch and store the value in the cache.
     *
     * @param key   the key to retrieve the value
     * @param loader a function to load the value if it is not present in the cache
     * @return the cached or loaded value wrapped in an Optional
     */
    Optional<V> get(K key, Function<K, Optional<V>> loader);

    /**
     * Retrieves a value from the cache without a loader.
     *
     * @param key the key to retrieve the value
     * @return an Optional containing the value if present, otherwise an empty Optional
     */
    Optional<V> get(K key);

    /**
     * Retrieves a value from the cache asynchronously.
     * If the value is not present, it uses the loader to fetch and store the value in the cache asynchronously.
     *
     * @param key    the key to retrieve the value
     * @param loader a function to load the value asynchronously if it is not present in the cache
     * @return a CompletableFuture containing the cached or loaded value wrapped in an Optional
     */
    CompletableFuture<Optional<V>> getAsync(K key, Function<K, CompletableFuture<Optional<V>>> loader);

    /**
     * Puts a value into the cache.
     *
     * @param key   the key associated with the value
     * @param value the value to store in the cache
     */
    void put(K key, V value);

    /**
     * Removes a value from the cache.
     *
     * @param key the key whose associated value is to be removed
     */
    void remove(K key);

    /**
     * Clears the entire cache.
     */
    void clear();

    /**
     * Checks if the cache contains a value for the given key.
     *
     * @param key the key to check
     * @return true if the cache contains a value for the key, false otherwise
     */
    boolean containsKey(K key);
}