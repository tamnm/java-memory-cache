package org.example.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LFUEvictionPolicyTest {

    @Test
    void testLFUEvictionPolicy() {
        // Arrange
        LFUEvictionPolicy<String> lfu = new LFUEvictionPolicy<>(3);

        // Act & Assert
        assertNull(lfu.onPut("A")); // No eviction yet
        assertNull(lfu.onPut("B"));
        assertNull(lfu.onPut("C"));

        lfu.onAccess("A"); // Increment frequency of "A"
        lfu.onAccess("A");
        lfu.onAccess("B"); // Increment frequency of "B"

        assertEquals("C", lfu.onPut("D")); // Evict "C" (least frequently used)

        lfu.onAccess("D"); // Increment frequency of "D"
        assertEquals("B", lfu.onPut("E")); // Evict "B" (least frequently used)

        assertEquals(3, lfu.size());
        assertEquals(3, lfu.getCapacity());

        // Test edge cases
        lfu.onAccess("E");
        assertEquals("D", lfu.onPut("F")); // Evict "D"
        lfu.onAccess("F");
        lfu.onAccess("A");
        assertEquals("E", lfu.onPut("G")); // Evict "E"
    }

    @Test
    void testLFUConcurrentAccess() throws InterruptedException {
        // Arrange
        LFUEvictionPolicy<String> lfu = new LFUEvictionPolicy<>(3);
        Thread[] threads = new Thread[10];

        // Act
        for (int i = 0; i < threads.length; i++) {
            int index = i;
            threads[i] = new Thread(() -> {
                lfu.onPut("Key" + index);
                lfu.onAccess("Key" + index);
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        assertEquals(3, lfu.size());
        assertEquals(3, lfu.getCapacity());
    }
}

