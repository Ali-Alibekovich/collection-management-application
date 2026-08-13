package io.github.alialibekovich.collection.model;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Pool of unique ids. {@link #claim(int)} is atomic: for any id exactly one
 * caller wins, which is what makes {@link #next()} safe to call from
 * concurrent request handlers. The atomicity contract is verified by the
 * jcstress suite in the {@code stress} module.
 */
public final class IdPool {

    private final Set<Integer> used = ConcurrentHashMap.newKeySet();

    /** @return {@code true} if this call claimed the id, {@code false} if it was already taken */
    public boolean claim(int id) {
        return used.add(id);
    }

    public int next() {
        while (true) {
            int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
            if (claim(id)) {
                return id;
            }
        }
    }
}
