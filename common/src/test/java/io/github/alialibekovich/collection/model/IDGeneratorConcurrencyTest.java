package io.github.alialibekovich.collection.model;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ids are handed out to concurrent request handlers, so generation must be
 * atomic: with the old non-synchronized HashSet two threads could draw the
 * same id or corrupt the set.
 */
class IDGeneratorConcurrencyTest {

    private static final int THREADS = 8;
    private static final int IDS_PER_THREAD = 5_000;

    @Test
    void concurrentlyGeneratedIdsAreUnique() throws Exception {
        Set<Integer> seen = ConcurrentHashMap.newKeySet();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        try {
            CountDownLatch start = new CountDownLatch(1);
            List<Future<?>> futures = new ArrayList<>();
            for (int t = 0; t < THREADS; t++) {
                futures.add(pool.submit(() -> {
                    start.await();
                    for (int i = 0; i < IDS_PER_THREAD; i++) {
                        seen.add(IDGenerator.generateID());
                    }
                    return null;
                }));
            }
            start.countDown();
            for (Future<?> future : futures) {
                future.get();
            }
            assertEquals(THREADS * IDS_PER_THREAD, seen.size());
        } finally {
            pool.shutdownNow();
        }
    }
}
