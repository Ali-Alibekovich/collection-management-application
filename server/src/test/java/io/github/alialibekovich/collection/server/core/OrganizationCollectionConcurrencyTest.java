package io.github.alialibekovich.collection.server.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.alialibekovich.collection.server.core.OrganizationFixtures.organization;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The collection is mutated by a pool of request handlers; compound scans and
 * targeted removals must not lose elements or throw
 * ConcurrentModificationException.
 */
class OrganizationCollectionConcurrencyTest {

    private static final int THREADS = 8;
    private static final int OPS_PER_THREAD = 1000;

    @Test
    void concurrentAddsAndRemovesAreNotLost() throws Exception {
        OrganizationCollection collection = new OrganizationCollection(new InMemoryOrganizationStore());
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        try {
            CountDownLatch start = new CountDownLatch(1);
            List<Future<?>> adds = new ArrayList<>();
            for (int t = 0; t < THREADS; t++) {
                int thread = t;
                adds.add(pool.submit(() -> {
                    start.await();
                    for (int i = 0; i < OPS_PER_THREAD; i++) {
                        collection.snapshot(); // concurrent read pressure
                        collection.updateElement(organization(-1, "x", 1.0), -1);
                        collectionAdd(collection, thread * OPS_PER_THREAD + i);
                    }
                    return null;
                }));
            }
            start.countDown();
            for (Future<?> future : adds) {
                future.get();
            }
            assertEquals(THREADS * OPS_PER_THREAD, collection.snapshot().size());

            List<Future<?>> removals = new ArrayList<>();
            for (int t = 0; t < THREADS; t++) {
                int thread = t;
                removals.add(pool.submit(() -> {
                    for (int i = 0; i < OPS_PER_THREAD; i++) {
                        collection.removeElement(thread * OPS_PER_THREAD + i);
                    }
                    return null;
                }));
            }
            for (Future<?> future : removals) {
                future.get();
            }
            assertEquals(0, collection.snapshot().size());
        } finally {
            pool.shutdownNow();
        }
    }

    /**
     * Regression: scans must run under the list lock — a concurrent
     * add/clear used to throw ConcurrentModificationException mid-scan.
     */
    @Test
    void doesExistSurvivesConcurrentMutation() throws Exception {
        OrganizationCollection collection = new OrganizationCollection(new InMemoryOrganizationStore());
        ExecutorService pool = Executors.newFixedThreadPool(5);
        AtomicBoolean stop = new AtomicBoolean(false);
        try {
            Future<?> writer = pool.submit(() -> {
                int id = 0;
                while (!stop.get()) {
                    collectionAdd(collection, id++);
                    if (id % 10 == 0) {
                        for (int i = id - 10; i < id; i++) {
                            collection.removeElement(i);
                        }
                    }
                }
                return null;
            });
            List<Future<?>> readers = new ArrayList<>();
            for (int t = 0; t < 4; t++) {
                readers.add(pool.submit(() -> {
                    while (!stop.get()) {
                        collection.doesExist(3);
                    }
                    return null;
                }));
            }
            Thread.sleep(500);
            stop.set(true);
            writer.get();
            for (Future<?> reader : readers) {
                reader.get();
            }
        } finally {
            stop.set(true);
            pool.shutdownNow();
        }
    }

    private static void collectionAdd(OrganizationCollection collection, int id) {
        // updateElement/removeElement are the public mutators; direct add goes
        // through reload in production, here we mutate via the snapshot list
        collection.rawList().add(organization(id, "org-" + id, 1.0));
    }
}
