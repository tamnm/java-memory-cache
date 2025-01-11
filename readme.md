# Eviction Policy-Based Memory Cache

## Overview
This project implements a flexible memory caching system with support for multiple eviction policies. The caching system is highly extensible, thread-safe, and provides both synchronous and asynchronous operations. It is designed to handle various caching strategies such as:

1. **Least Recently Used (LRU)**: Evicts the least recently accessed item.
2. **First In First Out (FIFO)**: Evicts the oldest item in the cache.
3. **Least Frequently Used (LFU)**: Evicts the least frequently accessed item.

---

## Features
- Supports three eviction policies: **LRU**, **FIFO**, and **LFU**.
- Extensible design for adding new eviction policies.
- Thread-safe operations using `ConcurrentHashMap` and synchronized blocks.
- Provides synchronous and asynchronous APIs for cache access.

---

## Package Structure

```
org.example.cache
├── CachePreference.java        // Configuration class for cache preferences
├── CacheProvider.java          // Interface for creating caches with different policies
├── CacheProviderImpl.java      // Implementation of CacheProvider
├── EvictionPolicy.java         // Enum for supported eviction policies
├── EvictionPolicyHandler.java  // Interface for eviction policy implementations
├── FIFOEvictionPolicy.java     // Implementation of FIFO eviction policy
├── LFUEvictionPolicy.java      // Implementation of LFU eviction policy
├── LRUEvictionPolicy.java      // Implementation of LRU eviction policy
├── MemoryCache.java            // Interface for the memory cache
└── MemoryCacheImpl.java        // Implementation of the memory cache
```

---

## How to Use

### 1. Setting Up a Cache
You can configure the cache by specifying the eviction policy and capacity using `CachePreference`. Then, use the `CacheProvider` to create a cache instance.

```java
import org.example.cache.*;

public class CacheExample {
    public static void main(String[] args) {
        CacheProvider cacheProvider = new CacheProviderImpl();
        CachePreference preference = new CachePreference(EvictionPolicy.LRU, 5);

        MemoryCache<String, String> cache = cacheProvider.getCache(preference);

        // Adding items to the cache
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        // Retrieving items
        System.out.println(cache.get("key1").orElse("Not Found"));

        // Asynchronous retrieval
        cache.getAsync("key3", key -> CompletableFuture.completedFuture(Optional.of("value3")))
             .thenAccept(value -> System.out.println("Async Value: " + value.orElse("Not Found")));

        // Removing items
        cache.remove("key1");

        // Clearing the cache
        cache.clear();
    }
}
```

### 2. Switching Eviction Policies
To change the eviction policy, update the `CachePreference` with the desired policy:
```java
CachePreference preference = new CachePreference(EvictionPolicy.FIFO, 10);
MemoryCache<String, String> cache = cacheProvider.getCache(preference);
```

---

## Classes

### 1. `CachePreference`
- Stores configuration for eviction policy and cache capacity.

### 2. `CacheProvider` and `CacheProviderImpl`
- Factory for creating `MemoryCache` instances based on the provided `CachePreference`.

### 3. `MemoryCache` and `MemoryCacheImpl`
- Interface and implementation of the memory cache supporting read-through and asynchronous operations.

### 4. Eviction Policies
#### a. `LRUEvictionPolicy`
- Tracks the least recently used items using a `Deque`.

#### b. `FIFOEvictionPolicy`
- Tracks items in a first-in-first-out order using a `Queue`.

#### c. `LFUEvictionPolicy`
- Tracks access frequency using a `HashMap`.

---

## Extending the System
To add a new eviction policy:
1. Implement the `EvictionPolicyHandler` interface.
2. Define the eviction logic in `onPut` and `onAccess` methods.
3. Add the new policy to the `EvictionPolicy` enum.
4. Update `CacheProviderImpl` to support the new policy.

---

## Testing
Unit tests are provided for all eviction policies to ensure correctness and thread safety. Each eviction policy has dedicated test classes:
- `LRUEvictionPolicyTest`
- `FIFOEvictionPolicyTest`
- `LFUEvictionPolicyTest`

---

## License
This project is licensed under the MIT License.

