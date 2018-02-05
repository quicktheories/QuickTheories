package org.quicktheories.dsl;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.quicktheories.api.AsString;
import org.quicktheories.api.Subject1;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;
import org.quicktheories.impl.TheoryRunner;

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

  private final Supplier<Strategy> state;
  private final Gen<P> ps;
  private final Function<P, T> conversion;
  private final AsString<T> asString;

  MappingTheoryBuilder(final Supplier<Strategy> state, final Gen<P> source,
      Function<P, T> conversion,
      AsString<T> asString) {
    this.state = state;
    this.ps = source;
    this.conversion = conversion;
    this.asString = asString;
  }

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  public final void check(final Predicate<T> property) {
    final TheoryRunner<P, T> qc = new TheoryRunner<>(this.state.get(), this.ps,
        conversion, asString);
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
  public Subject1<T> describedAs(Function<T,String> toString) {
   return new MappingTheoryBuilder<>(this.state, this.ps, 
        this.conversion, v -> toString.apply(v));
  }

}
