package org.example.cache;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FIFOEvictionPolicyTest {

    @Test
    void testFIFOEvictionPolicy() {
        // Arrange
        FIFOEvictionPolicy<String> fifo = new FIFOEvictionPolicy<>(3);

        // Act & Assert
        assertNull(fifo.onPut("A")); // No eviction yet
        assertNull(fifo.onPut("B"));
        assertNull(fifo.onPut("C"));
        assertEquals("A", fifo.onPut("D")); // Evict "A" (first in)

        fifo.onAccess("B"); // Access should not affect order
        assertEquals("B", fifo.onPut("E")); // Evict "B" (first in)

        assertEquals(3, fifo.size());
        assertEquals(3, fifo.getCapacity());

        // Test edge cases
        fifo.onPut("F");
        fifo.onPut("G");
        assertEquals("E", fifo.onPut("H")); // Evict oldest "C"
    }

    @Test
    void testFIFOConcurrentAccess() throws InterruptedException {
        // Arrange
        FIFOEvictionPolicy<String> fifo = new FIFOEvictionPolicy<>(3);
        Thread[] threads = new Thread[10];
        List<String> putOrder = new ArrayList<>();
        // Act
        for (int i = 0; i < threads.length; i++) {
            int index = i;
            threads[i] = new Thread(() -> {
                fifo.onPut("Key" + index);
                putOrder.add("Key" + index);
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        assertEquals(3, fifo.size());
        assertEquals(3, fifo.getCapacity());

        // Validate eviction order
        assertEquals(putOrder.get(putOrder.size()-fifo.size()), fifo.onPut("Key10"));
    }
}
