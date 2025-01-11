package org.example.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CacheProviderImplTest {

    private CacheProviderImpl provider;
    private CachePreference preference;

    @BeforeEach
    void setup(){
        provider = new CacheProviderImpl();
        preference = new CachePreference();
        preference.setCapacity(100);
    }

    @Test
    public void testGetCache_LRU() {
        // Arrange
        preference.setEvictionPolicy(EvictionPolicy.LRU);

        // Act
        MemoryCache<String, String> cache = provider.getCache(preference);

        // Assert
        assertNotNull(cache);
        assertInstanceOf(MemoryCacheImpl.class, cache);
        assertEquals(EvictionPolicy.LRU, preference.getEvictionPolicy());
    }

    @Test
    public void testGetCache_FIFO() {
        // Arrange
        preference.setEvictionPolicy(EvictionPolicy.FIFO);

        // Act
        MemoryCache<String, String> cache = provider.getCache(preference);

        // Assert
        assertNotNull(cache);
        assertInstanceOf(MemoryCacheImpl.class, cache);
        assertEquals(EvictionPolicy.FIFO, preference.getEvictionPolicy());
    }

    @Test
    public void testGetCache_LFU() {
        // Arrange
        preference.setEvictionPolicy(EvictionPolicy.LFU);

        // Act
        MemoryCache<String, String> cache = provider.getCache(preference);

        // Assert
        assertNotNull(cache);
        assertInstanceOf(MemoryCacheImpl.class, cache);
        assertEquals(EvictionPolicy.LFU, preference.getEvictionPolicy());
    }

    @Test
    public void testGetCache_InvalidEvictionPolicy() {
        // Arrange
        preference.setEvictionPolicy(null);

        // Act and Assert
        assertThrows(NullPointerException.class, () -> provider.getCache(preference));
    }

    @Test
    public void testGetCache_Capacity() {
        // Arrange
        preference.setEvictionPolicy(EvictionPolicy.LRU);
        preference.setCapacity(100);

        // Act
        MemoryCache<String, String> cache = provider.getCache(preference);

        // Assert
        assertNotNull(cache);
        assertInstanceOf(MemoryCacheImpl.class, cache);
    }

    @Test
    public void testGetCache_MultipleCaches() {
        // Arrange
        CachePreference preference1 = new CachePreference();
        preference1.setEvictionPolicy(EvictionPolicy.LRU);
        preference1.setCapacity(100);

        CachePreference preference2 = new CachePreference();
        preference2.setEvictionPolicy(EvictionPolicy.FIFO);
        preference2.setCapacity(200);

        // Act
        MemoryCache<String, String> cache1 = provider.getCache(preference1);
        MemoryCache<String, String> cache2 = provider.getCache(preference2);

        // Assert
        assertNotNull(cache1);
        assertNotNull(cache2);
        assertNotSame(cache1, cache2);
        assertInstanceOf(MemoryCacheImpl.class, cache1);
        assertInstanceOf(MemoryCacheImpl.class, cache2);
    }
}






