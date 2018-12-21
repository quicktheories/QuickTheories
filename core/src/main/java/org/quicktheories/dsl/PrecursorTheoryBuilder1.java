package org.quicktheories.dsl;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

import org.quicktheories.api.Pair;
import org.quicktheories.api.Subject2;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;
import org.quicktheories.impl.TheoryRunner;

class PrecursorTheoryBuilder1<P, T> implements Subject2<P, T> {

  private final Supplier<Strategy> state;
  private final Gen<Pair<P, T>> ps;

  PrecursorTheoryBuilder1(final Supplier<Strategy> state,
      final Gen<Pair<P, T>> source) {
    this.state = state;
    this.ps = source;
  }

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */

  public final void check(final BiPredicate<P, T> property) {
    final TheoryRunner<Pair<P, T>, Pair<P, T>> qc = TheoryRunner.runner(
        this.state.get(), ps, Function.identity(), ps);
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
