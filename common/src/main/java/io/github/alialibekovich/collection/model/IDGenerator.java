package io.github.alialibekovich.collection.model;

/**
 * Generates collection-unique ids. Safe to call from concurrent request
 * handlers — see {@link IdPool} for the atomicity contract.
 */
public final class IDGenerator {

    private static final IdPool POOL = new IdPool();

    private IDGenerator() {
    }

    public static int generateID() {
        return POOL.next();
    }
}
