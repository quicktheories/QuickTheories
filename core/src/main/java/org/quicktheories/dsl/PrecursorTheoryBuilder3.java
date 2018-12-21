package org.quicktheories.dsl;

import java.util.function.Function;
import java.util.function.Supplier;

import org.quicktheories.api.Predicate3;
import org.quicktheories.api.Predicate4;
import org.quicktheories.api.QuadConsumer;
import org.quicktheories.api.Subject4;
import org.quicktheories.api.Tuple4;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;
import org.quicktheories.impl.TheoryRunner;

class PrecursorTheoryBuilder3<P, P2, P3, T> implements Subject4<P, P2, P3, T> {

  private final Supplier<Strategy> state;
  private final Gen<Tuple4<P, P2, P3, T>> ps;
  private final Predicate3<P, P2, P3> assumptions;

  PrecursorTheoryBuilder3(final Supplier<Strategy> state,
      final Gen<Tuple4<P, P2, P3, T>> source,
      Predicate3<P, P2, P3> assumptions) {
    this.state = state;
    this.ps = source;
    this.assumptions = assumptions;
  }

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  public final void check(final Predicate4<P, P2, P3, T> property) {
    final TheoryRunner<Tuple4<P, P2, P3, T>, Tuple4<P, P2, P3, T>> qc = TheoryRunner.runner(
        this.state.get(), ps.assuming(pair -> assumptions.test(pair._1, pair._2, pair._3)),
        Function.identity(), ps);
    qc.check(tuple -> property.test(tuple._1, tuple._2, tuple._3, tuple._4));
  }

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public final void checkAssert(final QuadConsumer<P, P2, P3, T> property) {
    check((p, p2, p3, t) -> {
      property.accept(p, p2, p3, t);
      return true;
    });
  }

}
