package io.github.alialibekovich.collection.server.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.alialibekovich.collection.server.util.CollectionManagerTest.organization;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionUtilsConcurrencyTest {

    @Test
    void doesExistFindsPresentAndAbsentIds() {
        CollectionManager.initializeCollection();
        CollectionManager.getCollection().add(organization(7, "Acme", 100.0));

        assertTrue(CollectionUtils.doesExist(7));
        assertFalse(CollectionUtils.doesExist(8));
    }

    /**
     * Regression: doesExist used to iterate the synchronized list without
     * holding its lock, so a concurrent add/remove could throw
     * ConcurrentModificationException mid-scan.
     */
    @Test
    void doesExistSurvivesConcurrentMutation() throws Exception {
        CollectionManager.initializeCollection();
        ExecutorService pool = Executors.newFixedThreadPool(5);
        AtomicBoolean stop = new AtomicBoolean(false);
        try {
            Future<?> writer = pool.submit(() -> {
                int id = 0;
                while (!stop.get()) {
                    CollectionManager.getCollection().add(organization(id++, "Acme", 100.0));
                    if (id % 10 == 0) {
                        CollectionManager.getCollection().clear();
                    }
                }
                return null;
            });
            List<Future<?>> readers = new ArrayList<>();
            for (int t = 0; t < 4; t++) {
                readers.add(pool.submit(() -> {
                    while (!stop.get()) {
                        CollectionUtils.doesExist(3);
                    }
                    return null;
                }));
            }
            Thread.sleep(500);
            stop.set(true);
            writer.get();
            for (Future<?> reader : readers) {
                reader.get(); // rethrows ConcurrentModificationException if the scan raced
            }
        } finally {
            stop.set(true);
            pool.shutdownNow();
        }
    }
}
