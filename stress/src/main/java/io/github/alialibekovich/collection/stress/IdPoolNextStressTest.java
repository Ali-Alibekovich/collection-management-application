package io.github.alialibekovich.collection.stress;

import io.github.alialibekovich.collection.model.IdPool;
import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

/**
 * Uniqueness of {@link IdPool#next()} under contention: four threads draw ids
 * concurrently and the arbiter counts distinct values — anything less than
 * four means two threads were handed the same id.
 */
@JCStressTest
@Outcome(id = "4", expect = Expect.ACCEPTABLE, desc = "all ids distinct")
@Outcome(expect = Expect.FORBIDDEN, desc = "duplicate ids handed out")
@State
public class IdPoolNextStressTest {

    private final IdPool pool = new IdPool();
    private int id1;
    private int id2;
    private int id3;
    private int id4;

    @Actor
    public void actor1() {
        id1 = pool.next();
    }

    @Actor
    public void actor2() {
        id2 = pool.next();
    }

    @Actor
    public void actor3() {
        id3 = pool.next();
    }

    @Actor
    public void actor4() {
        id4 = pool.next();
    }

    @Arbiter
    public void arbiter(I_Result result) {
        result.r1 = (int) java.util.stream.IntStream.of(id1, id2, id3, id4).distinct().count();
    }
}
