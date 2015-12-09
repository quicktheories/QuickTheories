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
  private final Function<P, String> pToString;
  private final Function<P2, String> p2ToString;
  private final Function<T, String> tToString;

  PrecursorTheoryBuilder2(final Supplier<Strategy> state,
      final Source<Tuple3<P, P2, T>> source,
      BiPredicate<P, P2> assumptions, Function<P, String> pToString,
      Function<P2, String> p2ToString, Function<T, String> tToString) {
    this.state = state;
    this.ps = source;
    this.assumptions = assumptions;
    this.pToString = pToString;
    this.p2ToString = p2ToString;
    this.tToString = tToString;
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
        Function.identity(), toStringFunction());
    qc.check(tuple -> property.test(tuple._1, tuple._2, tuple._3));
  }

  private Function<Tuple3<P, P2, T>, String> toStringFunction() {
    return tuple3 -> "{" + this.pToString.apply(tuple3._1) + ", "
        + this.p2ToString.apply(tuple3._2) + ", "
        + this.tToString.apply(tuple3._3) + "}";
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

  @Override
  public Subject3<P, P2, T> describedAs(Function<P, String> pToString,
      Function<P2, String> p2ToString,
      Function<T, String> tToString) {
    return new PrecursorTheoryBuilder2<P, P2, T>(this.state, this.ps,
        this.assumptions, pToString, p2ToString, tToString);
  }

}
