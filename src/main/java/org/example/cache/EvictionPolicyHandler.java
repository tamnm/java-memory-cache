package org.example.cache;

/**
 * EvictionPolicy interface for managing cache eviction strategies.
 */
interface EvictionPolicyHandler<K> {
    /**
     * Handles the addition of a key to the eviction policy.
     * Manages capacity and evicts if necessary.
     * @param key the key to add
     * @return the evicted key, if any
     */
    K onPut(K key);

    /**
     * Notifies the policy that a key has been accessed.
     * @param key the key accessed
     */
    void onAccess(K key);

    /**
     * Gets the current size of the cache.
     * @return the size of the cache
     */
    int size();

    /**
     * Gets the capacity of the cache.
     * @return the capacity of the cache
     */
    int getCapacity();
}