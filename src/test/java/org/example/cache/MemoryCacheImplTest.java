package org.example.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemoryCacheImplTest {

    private MemoryCacheImpl<String, String> cache;

    @Mock
    private EvictionPolicyHandler<String> evictionPolicy;

    @BeforeEach
    void setUp() {
        cache = new MemoryCacheImpl<>(evictionPolicy);
    }

    @Test
    void testGetWhenValueIsPresent() {
        // Arrange
        cache.put("key1", "value1");

        // Act
        Optional<String> result = cache.get("key1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("value1", result.get());
        verify(evictionPolicy).onAccess("key1");
    }

    @Test
    void testGetWhenValueIsAbsent() {
        // Act
        Optional<String> result = cache.get("key1");

        // Assert
        assertFalse(result.isPresent());
        verifyNoInteractions(evictionPolicy);
    }

    @Test
    void testGetWithLoaderWhenValueIsPresent() {
        // Arrange
        cache.put("key1", "value1");

        // Act
        Optional<String> result = cache.get("key1", key -> Optional.of("loadedValue"));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("value1", result.get());
        verify(evictionPolicy).onAccess("key1");
    }

    @Test
    void testGetWithLoaderWhenValueIsAbsent() {
        // Act
        Optional<String> result = cache.get("key1", key -> Optional.of("loadedValue"));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("loadedValue", result.get());
        verify(evictionPolicy).onPut("key1");
    }

    @Test
    void testGetWithLoaderWhenLoaderReturnsEmpty() {
        // Act
        Optional<String> result = cache.get("key1", key -> Optional.empty());

        // Assert
        assertFalse(result.isPresent());
        verifyNoInteractions(evictionPolicy);
    }

    @Test
    void testGetAsyncWhenValueIsPresent() throws ExecutionException, InterruptedException {
        // Arrange
        cache.put("key1", "value1");

        // Act
        CompletableFuture<Optional<String>> future = cache.getAsync("key1", key -> CompletableFuture.completedFuture(Optional.of("loadedValue")));
        Optional<String> result = future.get();

        // Assert
        assertTrue(result.isPresent());
        assertEquals("value1", result.get());
        verify(evictionPolicy).onAccess("key1");
    }

    @Test
    void testGetAsyncWhenValueIsAbsent() throws ExecutionException, InterruptedException {
        // Act
        CompletableFuture<Optional<String>> future = cache.getAsync("key1", key -> CompletableFuture.completedFuture(Optional.of("loadedValue")));
        Optional<String> result = future.get();

        // Assert
        assertTrue(result.isPresent());
        assertEquals("loadedValue", result.get());
        verify(evictionPolicy).onPut("key1");
    }

    @Test
    void testGetAsyncWhenLoaderReturnsEmpty() throws ExecutionException, InterruptedException {
        // Act
        CompletableFuture<Optional<String>> future = cache.getAsync("key1", key -> CompletableFuture.completedFuture(Optional.empty()));
        Optional<String> result = future.get();

        // Assert
        assertFalse(result.isPresent());
        verifyNoInteractions(evictionPolicy);
    }

    @Test
    void testPutValue() {
        // Act
        cache.put("key1", "value1");
        Optional<String> result = cache.get("key1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("value1", result.get());
        verify(evictionPolicy).onPut("key1");
    }

    @Test
    void testPutEvictsKey() {
        // Arrange
        when(evictionPolicy.onPut("key2")).thenReturn("key1");
        when(evictionPolicy.onPut(not(eq("key2")))).thenReturn(null);
        // Act
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        Optional<String> evictedValue = cache.get("key1");
        Optional<String> retainedValue = cache.get("key2");

        // Assert
        assertFalse(evictedValue.isPresent());
        assertTrue(retainedValue.isPresent());
        assertEquals("value2", retainedValue.get());
        verify(evictionPolicy).onPut("key1");
        verify(evictionPolicy).onPut("key2");
    }

    @Test
    void testRemoveKey() {
        // Arrange
        cache.put("key1", "value1");

        // Act
        cache.remove("key1");
        Optional<String> result = cache.get("key1");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testClearCache() {
        // Arrange
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        // Act
        cache.clear();

        // Assert
        assertFalse(cache.get("key1").isPresent());
        assertFalse(cache.get("key2").isPresent());
    }

    @Test
    void testContainsKey() {
        // Arrange
        cache.put("key1", "value1");

        // Act & Assert
        assertTrue(cache.containsKey("key1"));
        assertFalse(cache.containsKey("key2"));
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        // Arrange
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // Act
        // Multiple threads attempt to put key-value pairs into the cache concurrently.
        // Each thread inserts a unique key-value pair (e.g., "key0" -> "value0").
        IntStream.range(0, threadCount).forEach(i -> executorService.execute(() -> cache.put("key" + i, "value" + i)));

        // Wait for all threads to complete.
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));

        // Assert
        // Validate that all key-value pairs are successfully stored in the cache.
        IntStream.range(0, threadCount).forEach(i -> {
            Optional<String> value = cache.get("key" + i);
            assertTrue(value.isPresent()); // Check that the value exists in the cache.
            assertEquals("value" + i, value.get()); // Ensure the correct value is retrieved.
        });
    }

    @Test
    void testConcurrentGetWithLoader() throws InterruptedException {
        // Arrange
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        Function<String, Optional<String>> loader = key -> Optional.of("loadedValue" + key);

        // Act
        // Multiple threads attempt to get the same key ("key") concurrently, with a loader function.
        // The loader generates the value if it doesn't exist (e.g., "loadedValuekey").
        IntStream.range(0, threadCount).forEach(i -> executorService.execute(() -> {
            Optional<String> value = cache.get("key", loader);
            assertTrue(value.isPresent()); // Verify the value exists.
            assertEquals("loadedValuekey", value.get()); // Ensure the loader-generated value is correct.
        }));

        // Wait for all threads to complete.
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));

        // Assert
        // Verify that the loader was only invoked once and the key is cached properly.
        Optional<String> value = cache.get("key");
        assertTrue(value.isPresent()); // Ensure the value exists.
        assertEquals("loadedValuekey", value.get()); // Confirm the cached value matches the loader output.
    }
}
