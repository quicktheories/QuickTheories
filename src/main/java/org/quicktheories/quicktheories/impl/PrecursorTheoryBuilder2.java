package org.quicktheories.quicktheories.impl;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

import org.quicktheories.quicktheories.api.Predicate3;
import org.quicktheories.quicktheories.api.Subject3;
import org.quicktheories.quicktheories.api.TriConsumer;
import org.quicktheories.quicktheories.api.Tuple3;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;

class PrecursorTheoryBuilder2<P, P2, T> implements Subject3<P, P2, T> {

  private final Supplier<Strategy> state;
  private final Source<Tuple3<P, P2, T>> ps;
  private final BiPredicate<P, P2> assumptions;

  PrecursorTheoryBuilder2(final Supplier<Strategy> state,
      final Source<Tuple3<P, P2, T>> source,
      BiPredicate<P, P2> assumptions) {
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

  public final void check(final Predicate3<P, P2, T> property) {
    final TheoryRunner<Tuple3<P, P2, T>, Tuple3<P, P2, T>> qc = new TheoryRunner<>(
        this.state.get(), ps, pair -> assumptions.test(pair._1, pair._2),
        Function.identity(), ps);
    qc.check(tuple -> property.test(tuple._1, tuple._2, tuple._3));
  }


  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public final void checkAssert(final TriConsumer<P, P2, T> property) {
    check((p, p2, t) -> {
      property.accept(p, p2, t);
      return true;
    });
  }

}
