package org.example.cache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LRUEvictionPolicyTest {

    @Test
    void testOnPut() {
        LRUEvictionPolicy<String> policy = new LRUEvictionPolicy<>(3);
        assertNull(policy.onPut("key1"));
        assertNull(policy.onPut("key2"));
        assertNull(policy.onPut("key3"));
        assertEquals("key1", policy.onPut("key4"));
    }

    @Test
    void testOnAccess() {
        LRUEvictionPolicy<String> policy = new LRUEvictionPolicy<>(2);
        policy.onPut("key1");
        policy.onPut("key2");
        policy.onAccess("key1");
        assertEquals("key2", policy.onPut("key3"));
    }

    @Test
    void testOnPutAndOnAccess() {
        LRUEvictionPolicy<String> policy = new LRUEvictionPolicy<>(3);
        policy.onPut("key1");
        policy.onPut("key2");
        policy.onAccess("key1");
        policy.onPut("key3");
        policy.onAccess("key2");
        assertEquals("key1", policy.onPut("key4"));
    }

    @Test
    void testCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new LRUEvictionPolicy<>(0));
        assertThrows(IllegalArgumentException.class, () -> new LRUEvictionPolicy<>(-1));
    }
}