package org.example.cache;

import java.util.Deque;
import java.util.LinkedList;

/**
 * LRU EvictionPolicy implementation using a ConcurrentLinkedDeque.
 */
class LRUEvictionPolicy<K> implements EvictionPolicyHandler<K> {
    private final Deque<K> accessOrder = new LinkedList<>();
    private final int capacity;

    public LRUEvictionPolicy(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
    }

    @Override
    public synchronized K onPut(K key) {
        accessOrder.remove(key);
        accessOrder.addFirst(key);
        if (accessOrder.size() > capacity) {
            return accessOrder.pollLast(); // Evict the least recently used item
        }
        return null;
    }

    @Override
    public synchronized void onAccess(K key) {
        accessOrder.remove(key);
        accessOrder.addFirst(key);
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