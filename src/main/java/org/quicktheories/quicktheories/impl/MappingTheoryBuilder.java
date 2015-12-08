package org.quicktheories.quicktheories.impl;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.quicktheories.quicktheories.api.Subject1;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;

/**
 * Builds theories about values of type T based on values of precursor types P
 * and a mapping function.
 *
 * @param
 *          <P>
 *          Precursor type from which T will be built
 * @param <T>
 *          Final type of value required to theory
 */
class MappingTheoryBuilder<P, T> implements Subject1<T> {

  final Supplier<Strategy> state;
  final Source<P> ps;
  final Predicate<P> assumptions;
  final Function<P, T> conversion;
  final Function<T, String> tToString;

  MappingTheoryBuilder(final Supplier<Strategy> state, final Source<P> source,
      Predicate<P> assumptions, Function<P, T> conversion,
      Function<T, String> tToString) {
    this.state = state;
    this.ps = source;
    this.assumptions = assumptions;
    this.conversion = conversion;
    this.tToString = tToString;
  }

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  public final void check(final Predicate<T> property) {
    final TheoryRunner<P, T> qc = new TheoryRunner<>(this.state.get(), this.ps,
        this.assumptions,
        conversion, tToString);
    qc.check(property);
  }

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public final void checkAssert(final Consumer<T> property) {
    check(t -> {
      property.accept(t);
      return true;
    });
  }

  @Override
  public Subject1<T> withStringFormat(Function<T, String> toString) {
    return new MappingTheoryBuilder<P, T>(this.state, this.ps, this.assumptions,
        this.conversion, toString);
  }

}
