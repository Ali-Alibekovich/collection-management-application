package io.github.alialibekovich.collection.server.util;

import io.github.alialibekovich.collection.model.Address;
import io.github.alialibekovich.collection.model.Coordinates;
import io.github.alialibekovich.collection.model.Location;
import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.model.OrganizationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression guard for the shared collection: {@code initializeCollection}
 * must hand out a thread-safe list, because request handlers mutate it from
 * a worker pool. With a plain {@code ArrayList} this test loses elements or
 * throws.
 */
class CollectionManagerConcurrencyTest {

    private static final int THREADS = 8;
    private static final int ADDS_PER_THREAD = 1000;

    @Test
    void concurrentAddsAndRemovesAreNotLost() throws Exception {
        CollectionManager.initializeCollection();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        try {
            CountDownLatch start = new CountDownLatch(1);
            List<Future<?>> futures = new ArrayList<>();
            for (int t = 0; t < THREADS; t++) {
                int thread = t;
                futures.add(pool.submit(() -> {
                    start.await();
                    for (int i = 0; i < ADDS_PER_THREAD; i++) {
                        CollectionManager.getCollection()
                                .add(sampleOrganization(thread * ADDS_PER_THREAD + i));
                    }
                    return null;
                }));
            }
            start.countDown();
            for (Future<?> future : futures) {
                future.get();
            }

            assertEquals(THREADS * ADDS_PER_THREAD, CollectionManager.getCollection().size());

            // concurrent targeted removals must each take out exactly their element
            List<Future<?>> removals = new ArrayList<>();
            for (int t = 0; t < THREADS; t++) {
                int thread = t;
                removals.add(pool.submit(() -> {
                    start.await();
                    for (int i = 0; i < ADDS_PER_THREAD; i++) {
                        CollectionManager.removeElement(thread * ADDS_PER_THREAD + i);
                    }
                    return null;
                }));
            }
            for (Future<?> future : removals) {
                future.get();
            }

            assertEquals(0, CollectionManager.getCollection().size());
        } finally {
            pool.shutdownNow();
        }
    }

    private static Organization sampleOrganization(int id) {
        return new Organization(
                id,
                "org-" + id,
                new Coordinates(1.0, 2.0),
                LocalDateTime.of(2020, 7, 1, 20, 29),
                100.0,
                OrganizationType.COMMERCIAL,
                new Address("street", "zip", new Location(1f, 2f, "town")),
                "owner",
                "0x990000ff");
    }
}
