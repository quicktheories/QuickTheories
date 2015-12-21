package org.quicktheories.quicktheories.impl;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.quicktheories.quicktheories.api.Pair;
import org.quicktheories.quicktheories.api.Subject2;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;

class PrecursorTheoryBuilder1<P, T> implements Subject2<P, T> {

  private final Supplier<Strategy> state;
  private final Source<Pair<P, T>> ps;
  private final Predicate<P> assumptions;

  PrecursorTheoryBuilder1(final Supplier<Strategy> state,
      final Source<Pair<P, T>> source,
      Predicate<P> assumptions) {
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

  public final void check(final BiPredicate<P, T> property) {
    final TheoryRunner<Pair<P, T>, Pair<P, T>> qc = new TheoryRunner<Pair<P, T>, Pair<P, T>>(
        this.state.get(), ps, pair -> assumptions.test(pair._1),
        Function.identity(), ps);
    qc.check(pair -> property.test(pair._1, pair._2));
  }

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public final void checkAssert(final BiConsumer<P, T> property) {
    check((p, t) -> {
      property.accept(p, t);
      return true;
    });
  }
}
