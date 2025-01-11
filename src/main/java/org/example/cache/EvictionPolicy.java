package org.example.cache;

/**
 * Enum representing different types of eviction policies.
 */
public enum EvictionPolicy {
    LRU, // Least Recently Used
    FIFO, // First In First Out
    LFU // Least Frequently Used
}