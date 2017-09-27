package org.quicktheories.dsl;

import java.util.function.Function;
import java.util.function.Supplier;

import org.quicktheories.api.Consumer5;
import org.quicktheories.api.Predicate4;
import org.quicktheories.api.Predicate5;
import org.quicktheories.api.Subject5;
import org.quicktheories.api.Tuple5;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;
import org.quicktheories.impl.TheoryRunner;

class PrecursorTheoryBuilder4<P, P2, P3, P4, T>
    implements Subject5<P, P2, P3, P4, T> {

  private final Supplier<Strategy> state;
  private final Gen<Tuple5<P, P2, P3, P4, T>> ps;
  private final Predicate4<P, P2, P3, P4> assumptions;


  PrecursorTheoryBuilder4(final Supplier<Strategy> state,
      final Gen<Tuple5<P, P2, P3, P4, T>> source,
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
        this.state.get(), ps.assuming(pair -> assumptions.test(pair._1, pair._2, pair._3, pair._4)),
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
 
}
