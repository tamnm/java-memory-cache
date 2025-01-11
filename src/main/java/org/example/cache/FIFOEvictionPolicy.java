package org.example.cache;

import java.util.LinkedList;
import java.util.Queue;

/**
 * FIFO EvictionPolicy implementation.
 *
 * @param <K> the type of keys in the eviction policy
 */
class FIFOEvictionPolicy<K> implements EvictionPolicyHandler<K> {
    private final Queue<K> accessOrder;
    private final int capacity;

    public FIFOEvictionPolicy(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        this.accessOrder = new LinkedList<>();
    }

    @Override
    public synchronized K onPut(K key) {
        K beEvicted = null;

        if (accessOrder.size() >= capacity) {
             beEvicted = accessOrder.poll(); // Evict the oldest item

        }
        accessOrder.add(key);

        return beEvicted;
    }

    @Override
    public synchronized void onAccess(K key) {
        // No reordering for FIFO
    }

    @Override
    public synchronized int size() {
        return accessOrder.size();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
