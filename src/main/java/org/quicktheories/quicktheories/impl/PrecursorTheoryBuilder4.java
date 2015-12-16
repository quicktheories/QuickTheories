package org.quicktheories.quicktheories.impl;

import java.util.function.Function;
import java.util.function.Supplier;

import org.quicktheories.quicktheories.api.AsString;
import org.quicktheories.quicktheories.api.Consumer5;
import org.quicktheories.quicktheories.api.Predicate4;
import org.quicktheories.quicktheories.api.Predicate5;
import org.quicktheories.quicktheories.api.Subject5;
import org.quicktheories.quicktheories.api.Tuple5;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;

class PrecursorTheoryBuilder4<P, P2, P3, P4, T>
    implements Subject5<P, P2, P3, P4, T> {

  private final Supplier<Strategy> state;
  private final Source<Tuple5<P, P2, P3, P4, T>> ps;
  private final Predicate4<P, P2, P3, P4> assumptions;


  PrecursorTheoryBuilder4(final Supplier<Strategy> state,
      final Source<Tuple5<P, P2, P3, P4, T>> source,
      Predicate4<P, P2, P3, P4> assumptions) {
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
  public final void check(final Predicate5<P, P2, P3, P4, T> property) {
    final TheoryRunner<Tuple5<P, P2, P3, P4, T>, Tuple5<P, P2, P3, P4, T>> qc = new TheoryRunner<>(
        this.state.get(), ps,
        pair -> assumptions.test(pair._1, pair._2, pair._3, pair._4),
        Function.identity(), this.ps);
    qc.check(tuple -> property.test(tuple._1, tuple._2, tuple._3, tuple._4,
        tuple._5));
  }

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public final void checkAssert(final Consumer5<P, P2, P3, P4, T> property) {
    check((p, p2, p3, p4, t) -> {
      property.accept(p, p2, p3, p4, t);
      return true;
    });
  }

  @Override
  public Subject5<P, P2, P3, P4, T> describedAs(
      Function<P, String> pToString,
      Function<P2, String> p2ToString, Function<P3, String> p3ToString,
      Function<P4, String> p4ToString,
      Function<T, String> tToString) {
    return new PrecursorTheoryBuilder4<P, P2, P3, P4, T>(this.state, this.ps.describedAs(combineToStrings(pToString,p2ToString,p3ToString,p4ToString,tToString)),
        this.assumptions);
  }
  
  private static <A, B, C, D, E> AsString<Tuple5<A, B, C, D, E>> combineToStrings(
      Function<A, String> fa, Function<B, String> fb,
      Function<C, String> fc, Function<D, String> fd, Function<E, String> fe) {
    return tuple -> tuple.map(fa, fb, fc, fd, fe).toString();
  }

}
