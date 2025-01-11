package org.example.cache;

public class CachePreference {
    private EvictionPolicy evictionPolicy;
    private int capacity;

    public CachePreference() {
    }

    public CachePreference(EvictionPolicy evictionPolicy, int capacity) {
        this.evictionPolicy = evictionPolicy;
        this.capacity = capacity;
    }

    public EvictionPolicy getEvictionPolicy() {
        return evictionPolicy;
    }

    public void setEvictionPolicy(EvictionPolicy evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
