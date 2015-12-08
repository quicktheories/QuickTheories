package org.quicktheories.quicktheories.impl;

import java.util.function.Function;
import java.util.function.Supplier;

import org.quicktheories.quicktheories.api.Predicate3;
import org.quicktheories.quicktheories.api.Predicate4;
import org.quicktheories.quicktheories.api.QuadConsumer;
import org.quicktheories.quicktheories.api.Subject4;
import org.quicktheories.quicktheories.api.Tuple4;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;

class PrecursorTheoryBuilder3<P, P2, P3, T> implements Subject4<P, P2, P3, T> {

  private final Supplier<Strategy> state;
  private final Source<Tuple4<P, P2, P3, T>> ps;
  private final Predicate3<P, P2, P3> assumptions;
  private final Function<P, String> pToString;
  private final Function<P2, String> p2ToString;
  private final Function<P3, String> p3ToString;
  private final Function<T, String> tToString;

  PrecursorTheoryBuilder3(final Supplier<Strategy> state,
      final Source<Tuple4<P, P2, P3, T>> source,
      Predicate3<P, P2, P3> assumptions, Function<P, String> pToString,
      Function<P2, String> p2ToString, Function<P3, String> p3ToString,
      Function<T, String> tToString) {
    this.state = state;
    this.ps = source;
    this.assumptions = assumptions;
    this.pToString = pToString;
    this.p2ToString = p2ToString;
    this.p3ToString = p3ToString;
    this.tToString = tToString;
  }

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  public final void check(final Predicate4<P, P2, P3, T> property) {
    final TheoryRunner<Tuple4<P, P2, P3, T>, Tuple4<P, P2, P3, T>> qc = new TheoryRunner<>(
        this.state.get(), ps,
        pair -> assumptions.test(pair._1, pair._2, pair._3),
        Function.identity(), toStringFunction());
    qc.check(tuple -> property.test(tuple._1, tuple._2, tuple._3, tuple._4));
  }
  
  private Function<Tuple4<P, P2, P3, T>, String> toStringFunction() {
    return tuple4 -> "{" + this.pToString.apply(tuple4._1) + ", "
        + this.p2ToString.apply(tuple4._2) + ", "
        + this.p3ToString.apply(tuple4._3) + ", "
        + this.tToString.apply(tuple4._4) + "}";
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
  
  @Override
  public Subject4<P, P2, P3, T> withStringFormat(Function<P, String> pToString,
      Function<P2, String> p2ToString, Function<P3, String> p3ToString,
      Function<T, String> tToString) {
    return new PrecursorTheoryBuilder3<P, P2, P3, T>(this.state, this.ps,
        this.assumptions, pToString, p2ToString, p3ToString, tToString);
  }

}
