package org.quicktheories.quicktheories.impl;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.quicktheories.quicktheories.api.Pair;
import org.quicktheories.quicktheories.api.Subject2;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Generator;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.Strategy;

/**
 * Builds theories about values of type T based on values of precursor types P
 *
 * @param
 *          <P>
 *          Precursor type
 * @param <T>
 *          Final type
 */
public final class TheoryBuilder<P, T> extends MappingTheoryBuilder<P, T> {

  /**
   * Builds theories about values of type T based on values of precursor types P
   * 
   * @param state
   *          supplies the strategy to be implemented
   * @param source
   *          the source of the values to be generated and potentially shrunk
   * @param predicate
   *          limits the possible values of type P
   * @param conversion
   *          function defining the conversion from type P to type T
   * @param tToString
   *          function specifying how a value of type T should be output to
   *          String in the falsification output
   */
  public TheoryBuilder(final Supplier<Strategy> state, final Source<P> source,
      Predicate<P> predicate, Function<P, T> conversion,
      Function<T, String> tToString) {
    super(state, source, predicate, conversion, tToString);
  }

  /**
   * Constrains the values a theory must be true for by the given assumption
   * 
   * @param newAssumption
   *          an assumption that must be true of all values
   * @return TheoryBuilder based on the given assumption
   */
  public TheoryBuilder<P, T> assuming(Predicate<P> newAssumption) {
    return new TheoryBuilder<P, T>(this.state, this.ps,
        this.assumptions.and(newAssumption),
        conversion, tToString);
  }

  /**
   * Converts theory to one about a different type using the given function
   * 
   * @param <N>
   *          type to convert to
   * @param mapping
   *          function with which to map values to desired type
   * @return TheoryBuilder about type N
   */
  public <N> TheoryBuilder<Pair<P, T>, N> as(
      Function<T, N> mapping) {
    return new TheoryBuilder<Pair<P, T>, N>(state,
        convertedSource(mapping), convertedPredicate(mapping),
        internalConversion(mapping), n -> n.toString());
  }

  /**
   * Converts theory to one about a different type using the given function
   * retaining all precursor values
   * @param mapping
   *          Function from type T to type N
   * @return a Subject2 relating to the state of a theory involving two values
   */
  public <N> Subject2<P, N> asWithPrecursor(Function<T, N> mapping) {
    Generator<Pair<P, N>> g = (prng, step) -> {
      P p = this.ps.next(prng, step);
      T t = this.conversion.apply(p);
      return Pair.of(p, mapping.apply(t));
    };

    Shrink<Pair<P, N>> s = (original, context) -> ps
        .shrink(original._1, context)
        .map(p -> Pair.of(p, conversion.andThen(mapping).apply(p)));

    Source<Pair<P, N>> gen = Source.of(g).withShrinker(s);
    return new PrecursorTheoryBuilder1<P, N>(state, gen, assumptions,
        a -> a.toString(), b -> b.toString());

  }

  private <N> Function<Pair<P, T>, N> internalConversion(
      Function<T, N> conversion2) {
    return pair -> conversion2.apply(conversion.apply(pair._1));
  }

  private <N> Source<Pair<P, T>> convertedSource(
      Function<T, N> conversion2) {
    return Source.of(generatorFunction()).withShrinker(shrinker());
  }

  private <N> Generator<Pair<P, T>> generatorFunction() {
    return (prng, step) -> {
      P p = ps.next(prng, step);
      return Pair.of(p, conversion.apply(p));
    };
  }

  private <N> Shrink<Pair<P, T>> shrinker() {
    return (pair, context) -> ps.shrink(pair._1, context)
        .map(pre -> Pair.of(pre, conversion.apply(pre)));
  }

  private <N> Predicate<Pair<P, T>> convertedPredicate(
      Function<T, N> conversion2) {
    return pair -> assumptions.test(pair._1);
  }

}
