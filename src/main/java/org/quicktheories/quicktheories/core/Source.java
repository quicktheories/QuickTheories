package org.quicktheories.quicktheories.core;

import java.util.function.Function;
import java.util.stream.Stream;

import org.quicktheories.quicktheories.generators.Compositions;

/**
 * Produces and shrinks values of type T based on the randomness supplied by a
 * PseudoRandom.
 * 
 * Production and shrinking of values are tied together to allow shrinking to be
 * more intelligent and to understand the constraints placed on value
 * generation.
 *
 * @param <T>
 *          Type of values to generate
 */
public final class Source<T> implements Shrink<T>, Generator<T> {

  private final Generator<T> prngToValue;
  private final Shrink<T> shrink;

  /**
   * Creates a Source of type T
   * 
   * @param prngToValue
   *          Function from PseudoRandom to a value
   * @param shrinker
   *          Shrink strategy for values from this generator
   */
  private Source(Generator<T> prngToValue, Shrink<T> shrinker) {
    this.prngToValue = prngToValue;
    this.shrink = shrinker;
  }

  /**
   * Produce a Source from a generator of type T. The resulting values will not
   * shrink.
   * 
   * @param <T>
   *          Type of values to generate
   * @param generator
   *          a generator of values
   * @return a Source of values that cannot be shrunk
   */
  public static <T> Source<T> of(Generator<T> generator) {
    return new Source<>(generator, noShrink());
  }

  /**
   * Produces a Source which will be shrunk using the Shrink instance.
   * 
   * It is up to the caller to ensure that the Shrink instance will respect any
   * constraints this generator places on values,
   * 
   * @param shrink
   *          shrink instance to use
   * @return a Source of T using the given Shrink instance
   */
  public Source<T> withShrinker(Shrink<T> shrink) {
    return new Source<>(this.prngToValue, shrink);
  }

  /**
   * Produce a single value
   * 
   * @param prng
   *          Source of randomness
   * @return A value based on prng
   */
  public T next(final PseudoRandom prng, int step) {
    return prngToValue.next(prng, step);
  }

  /**
   * Produces a stream of values "smaller" than original.
   * 
   * What represents "smaller" is somewhat subjective. It should be simpler and
   * easier for a human to work with.
   * 
   * @param original
   *          Value that values in stream should be smaller than
   * @param context
   *          The shrinking context
   * @return A stream of values of type T
   */
  public Stream<T> shrink(T original, ShrinkContext context) {
    return shrink.shrink(original, context);
  }

  /**
   * Produces a Source of type B.
   * 
   * Values created by this method will not shrink.
   * 
   * @param <B>
   *          type to convert to
   * @param conversion
   *          Function to use to convert from T to B
   * @return A Source of type B
   */
  public <B> Source<B> asWithoutShrinking(Function<T, B> conversion) {
    return new Source<>(prngToValue.andThen(conversion), noShrink());
  }

  /**
   * Produces a Source of type B. The supplied backFunction will be used to
   * allow shrinking of the produced values using the original strategy for type
   * T.
   * 
   * It is important that backFunction works for all values of T.
   * 
   * @param <B>
   *          type to convert to
   * @param conversion
   *          A function from T to B
   * @param backFunction
   *          A function from B back to T
   * @return A Source of type B
   */
  public <B> Source<B> as(Function<T, B> conversion,
      Function<B, T> backFunction) {
    return new Source<>(prngToValue.andThen(conversion),
        (original, context) -> shrink
            .shrink(backFunction.apply(original), context).map(conversion));
  }

  /**
   * Returns a Source of type T, where the supplied values are guaranteed to be
   * produced at least once.
   * 
   * If a property is falsified by one of the supplied values then shrunk values
   * will be selected from those specifically supplied only.
   * 
   * @param values
   *          values that must be produced
   * @return a Source of T
   */
  public Source<T> andAlwaysTheValues(
      @SuppressWarnings("unchecked") T... values) {
    return Compositions.combineWithValues(this, values);
  }

  /**
   * Composes the given Source with the Source supplied as a parameter such that
   * their values are produced interleaved.
   * 
   * @param rhs
   *          - Source from which to retrieve the even values
   * @return a Source combining the given Source and rhs
   */
  public Source<T> andAlternateWithSource(Source<T> rhs) {
    return Compositions.interleave(this, rhs);
  }

  /**
   * Composes the given Source with the Source supplied as a parameter such that
   * the first n values are chosen from the initial, given, Source. After this,
   * the supplied Source is implemented for the remainder of the generating
   * period. If a falsifying value is found before the nth attempt, the value
   * will be shrunk in accordance with the given Source.
   * 
   * @param n
   *          - number of values to be taken from supplied Values
   * @param after
   *          - the Source to provide subsequent values after the first n
   *          attempts
   * @return a composed Source of T
   */
  public Source<T> nTimesThenSwitchTo(int n, Source<T> after) {
    return Compositions.ntimesThen(n, this, after);
  }

  private static <T> Shrink<T> noShrink() {
    return (original, context) -> Stream.empty();
  }

}
