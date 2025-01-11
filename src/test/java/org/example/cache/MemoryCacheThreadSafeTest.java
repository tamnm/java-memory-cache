package org.example.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MemoryCacheThreadSafeTest {

    private MemoryCacheImpl<String, String> cache;
    private LRUEvictionPolicy<String> evictionPolicy;

    @BeforeEach
    void setUp() {
        // Use LRU Policy for thread safety tests
        evictionPolicy = new LRUEvictionPolicy<>(5);
        cache = new MemoryCacheImpl<>(evictionPolicy); // Small capacity to force evictions
    }

    @Test
    void testThreadSafetyWithEvictions() throws InterruptedException {
        // Arrange
        int threadCount = 10;
        AtomicInteger evictedCounter = new AtomicInteger(0);
        AtomicInteger putCounter     = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // Act
        IntStream.range(0, threadCount).forEach(i -> executorService.execute(() -> {
            cache.put("key" + i, "value" + i);
            System.out.println("Put key " + i);
            putCounter.incrementAndGet();
        }));
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));

        // Assert

        // Ensure all keys were put into the cache
        assertEquals(threadCount, putCounter.get());

        // Try to get the values
       IntStream.range(0, threadCount).forEach(i -> {
            Optional<String> value = cache.get("key" + i);
            System.out.println("Get key " + i+" value " + value);
            if(value.isEmpty()) {
                evictedCounter.incrementAndGet();
            }
        });
        // Only the last 5 keys should remain due to LRU eviction policy
        assertEquals(5, evictedCounter.get());
        assertEquals(5, evictionPolicy.size());

    }

    @Test
    void testThreadSafetyWithRemove() throws InterruptedException {
        // Arrange
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        IntStream.range(0, threadCount).forEach(i -> cache.put("key" + i, "value" + i));

        // Act
        IntStream.range(0, threadCount).forEach(i -> executorService.execute(() -> cache.remove("key" + i)));
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));

        // Assert
        IntStream.range(0, threadCount).forEach(i -> {
            Optional<String> value = cache.get("key" + i);
            assertFalse(value.isPresent());
        });
    }

    @Test
    void testThreadSafetyWithClear() throws InterruptedException {
        // Arrange
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        IntStream.range(0, threadCount).forEach(i -> cache.put("key" + i, "value" + i));

        // Act
        executorService.execute(cache::clear);
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));

        // Assert
        IntStream.range(0, threadCount).forEach(i -> {
            Optional<String> value = cache.get("key" + i);
            assertFalse(value.isPresent());
        });
    }
}
