package org.quicktheories.quicktheories.impl;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.quicktheories.quicktheories.api.Pair;
import org.quicktheories.quicktheories.api.Subject1;
import org.quicktheories.quicktheories.api.Subject2;
import org.quicktheories.quicktheories.core.Generator;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;

/**
 * Builds theories about values of type A
 *
 * @param <A>
 *          Final type
 */
public final class TheoryBuilder<A> implements Subject1<A> {
  
  private final Supplier<Strategy> state;
  private final Source<A> ps;
  private final Predicate<A> assumptions;
  
  /**
   * Builds theories about values of type T
   * 
   * @param state
   *          supplies the strategy to be implemented
   * @param source
   *          the source of the values to be generated and potentially shrunk
   * @param assumptions
   *          limits the possible values of type T
   * @param asString
   *          function specifying how a value of type T should be output to
   *          String in the falsification output
   */
  public TheoryBuilder(final Supplier<Strategy> state, final Source<A> source,
      Predicate<A> assumptions) {
    this.state = state;
    this.ps = source;
    this.assumptions = assumptions;
  }

  /**
   * Constrains the values a theory must be true for by the given assumption
   * 
   * @param newAssumption
   *          an assumption that must be true of all values
   * @return TheoryBuilder based on the given assumption
   */
  public TheoryBuilder<A> assuming(Predicate<A> newAssumption) {
    return new TheoryBuilder<A>(this.state, this.ps,
        this.assumptions.and(newAssumption));
  }

  /**
   * Converts theory to one about a different type using the given function
   * 
   * @param <T>
   *          type to convert to
   * @param mapping
   *          function with which to map values to desired type
   * @return theory builder about type T
   */
  public <T> Subject1<T> as(Function<A, T> mapping) {
    return new MappingTheoryBuilder<>(state, ps, assumptions, mapping,
        t -> t.toString());
  }

  /**
   * Converts theory to one about a different type using the given function
   * retaining all precursor values
   * @param mapping
   *          Function from types A and B to type T
   * @return a Subject3 relating to the state of a theory involving three values
   */
  public <T> Subject2<A, T> asWithPrecursor(Function<A, T> mapping) {
    Generator<Pair<A, T>> g = (prng, step) -> {
      A a = this.ps.next(prng, step);
      return Pair.of(a, mapping.apply(a));
    };

    Shrink<Pair<A, T>> s = (original, context) -> ps
        .shrink(original._1, context)
        .map(p -> Pair.of(p, mapping.apply(p)));

    Source<Pair<A, T>> gen = Source.of(g).withShrinker(s);
    return new PrecursorTheoryBuilder1<A, T>(state, gen, assumptions);

  }
  
  @Override
  public Subject1<A> describedAs(Function<A,String> toString) {
   return new TheoryBuilder<A>(this.state, this.ps.describedAs(a-> toString.apply(a)), this.assumptions);
  }  
  
  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  public final void check(final Predicate<A> property) {
    final TheoryRunner<A, A> qc = new TheoryRunner<>(this.state.get(), this.ps,
        this.assumptions,
        x -> x, ps);
    qc.check(property);
  }

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public final void checkAssert(final Consumer<A> property) {
    check(t -> {
      property.accept(t);
      return true;
    });
  }  

}
