package io.github.alialibekovich.collection.stress;

import io.github.alialibekovich.collection.model.IdPool;
import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.ZZ_Result;

/**
 * Atomicity of {@link IdPool#claim(int)}: when two threads race for the same
 * id, exactly one must win. The pre-fix implementation (check-then-add on a
 * plain HashSet) allowed both threads to "claim" the id.
 */
@JCStressTest
@Outcome(id = "true, false", expect = Expect.ACCEPTABLE, desc = "first actor claimed the id")
@Outcome(id = "false, true", expect = Expect.ACCEPTABLE, desc = "second actor claimed the id")
@Outcome(id = "true, true", expect = Expect.FORBIDDEN, desc = "both actors claimed the same id")
@Outcome(id = "false, false", expect = Expect.FORBIDDEN, desc = "the id was lost entirely")
@State
public class IdPoolClaimStressTest {

    private final IdPool pool = new IdPool();

    @Actor
    public void first(ZZ_Result result) {
        result.r1 = pool.claim(42);
    }

    @Actor
    public void second(ZZ_Result result) {
        result.r2 = pool.claim(42);
    }
}
