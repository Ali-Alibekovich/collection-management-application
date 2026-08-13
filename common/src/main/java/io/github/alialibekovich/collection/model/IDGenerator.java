package io.github.alialibekovich.collection.model;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates collection-unique ids. Safe to call from concurrent request
 * handlers: uniqueness is guaranteed by the atomic add to a concurrent set.
 */
public final class IDGenerator {

    private static final Set<Integer> USED_IDS = ConcurrentHashMap.newKeySet();

    private IDGenerator() {
    }

    public static int generateID() {
        while (true) {
            int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
            if (USED_IDS.add(id)) {
                return id;
            }
        }
    }
}
