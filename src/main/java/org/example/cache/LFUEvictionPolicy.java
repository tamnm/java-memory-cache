package org.example.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * LFU EvictionPolicy implementation.
 *
 * @param <K> the type of keys in the eviction policy
 */
class LFUEvictionPolicy<K> implements EvictionPolicyHandler<K> {
    private final Map<K, Integer> frequencyMap;
    private final int capacity;

    public LFUEvictionPolicy(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        this.frequencyMap = new HashMap<>();
    }

    @Override
    public synchronized K onPut(K key) {
        K beEvicted = null;

        if (frequencyMap.size() >= capacity) {
            // Find the least frequently used key
            K leastFrequentKey = frequencyMap.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
            if (leastFrequentKey != null) {
                frequencyMap.remove(leastFrequentKey);
            }
            beEvicted = leastFrequentKey;
        }
        frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
        return beEvicted;
    }

    @Override
    public synchronized void onAccess(K key) {

        if (frequencyMap.containsKey(key)) {
            frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
        }
    }

    @Override
    public synchronized int size() {
        return frequencyMap.size();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
