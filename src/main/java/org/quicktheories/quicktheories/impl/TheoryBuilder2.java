package org.quicktheories.quicktheories.impl;

import static org.quicktheories.quicktheories.impl.Util.equaliseShrinkLength;
import static org.quicktheories.quicktheories.impl.Util.zip;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.quicktheories.quicktheories.api.AsString;
import org.quicktheories.quicktheories.api.Pair;
import org.quicktheories.quicktheories.api.Subject1;
import org.quicktheories.quicktheories.api.Subject2;
import org.quicktheories.quicktheories.api.Subject3;
import org.quicktheories.quicktheories.api.Tuple3;
import org.quicktheories.quicktheories.core.Generator;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;

public final class TheoryBuilder2<A, B> implements Subject2<A, B> {
  private final Supplier<Strategy> state;
  private final Source<A> as;
  private final Source<B> bs;
  private final BiPredicate<A, B> assumptions;

  /**
   * Builds theories about values of type A and B
   *
   * @param state
   *          supplies the strategy to be implemented
   * @param as
   *          the first source of the values to be generated and potentially
   *          shrunk
   * @param bs
   *          the second source of the values to be generated and potentially
   *          shrunk
   * @param assumptions
   *          limits the possible values of type A and of type B
   */
  public TheoryBuilder2(final Supplier<Strategy> state, final Source<A> as,
      Source<B> bs, BiPredicate<A, B> assumptions) {
    this.state = state;
    this.as = as;
    this.bs = bs;
    this.assumptions = assumptions;
  }

  /**
   * Constrains the values a theory must be true for by the given assumption
   *
   * @param newAssumption
   *          an assumption that must be true of all values
   * @return theory builder based on the given assumption
   */
  public TheoryBuilder2<A, B> assuming(BiPredicate<A, B> newAssumption) {
    return new TheoryBuilder2<A, B>(this.state, this.as, this.bs,
        this.assumptions.and(newAssumption));
  }

  /**
   * Checks a boolean property across a random sample of possible values
   *
   * @param property
   *          property to check
   */
  @Override
  public void check(final BiPredicate<A, B> property) {
    final TheoryRunner<Pair<A, B>, Pair<A, B>> qc = TheoryRunner.runner(
        this.state.get(),
        combine(), convertPredicate());
    qc.check(x -> property.test(x._1, x._2));
  }

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   *
   * @param property
   *          property to check
   */
  @Override
  public final void checkAssert(final BiConsumer<A, B> property) {
    check((a, b) -> {
      property.accept(a, b);
      return true;
    });
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
  public <T> Subject1<T> as(
      BiFunction<A, B, T> mapping) {
    return new MappingTheoryBuilder<>(this.state, combine(),
        precursor -> this.assumptions.test(precursor._1,
            precursor._2),
        precursor -> mapping.apply(precursor._1, precursor._2),
        t -> t.toString());
  }

  /**
   * Converts theory to one about a different type using the given function
   * retaining all precursor values
   *
   * @param <T>
   *          type to create theory about
   *
   * @param mapping
   *          Function from types A and B to type T
   * @return a Subject3 relating to the state of a theory involving three values
   */
  public <T> Subject3<A, B, T> asWithPrecursor(BiFunction<A, B, T> mapping) {
    return this.asWithPrecursor(mapping, t -> t.toString());
  }

  /**
   * Converts theory to one about a different type using the given function
   * retaining all precursor values
   *
   * @param <T>
   *          type to create theory about
   *
   * @param mapping
   *          Function from types A and B to type T
   * @param typeToString
   *          Function to use to display built type
   * @return a Subject3 relating to the state of a theory involving three values
   */
  public <T> Subject3<A, B, T> asWithPrecursor(BiFunction<A, B, T> mapping,
      Function<T, String> typeToString) {
    final Generator<Tuple3<A, B, T>> g = (prng, step) -> {
      final A a = this.as.next(prng, step);
      final B b = this.bs.next(prng, step);
      return Tuple3.of(a, b, mapping.apply(a, b));
    };

    final Shrink<Tuple3<A, B, T>> s = (original, context) -> joinShrunkStreams()
        .shrink(Pair.of(original._1, original._2), context)
        .map(p -> Tuple3.of(p._1, p._2, mapping.apply(p._1, p._2)));

    final AsString<Tuple3<A, B, T>> desc = tuple -> tuple
        .map(this.as.asToStringFunction(), this.bs.asToStringFunction(),
            typeToString)
        .toString();

    final Source<Tuple3<A, B, T>> gen = Source.of(g).withShrinker(s)
        .describedAs(desc);
    return new PrecursorTheoryBuilder2<A, B, T>(this.state, gen,
        this.assumptions);

  }

  private Predicate<Pair<A, B>> convertPredicate() {
    return pair -> this.assumptions.test(pair._1, pair._2);
  }

  private Source<Pair<A, B>> combine() {
    return Source.of(prngToPair()).withShrinker(joinShrunkStreams())
        .describedAs(joinToString());
  }

  private Shrink<Pair<A, B>> joinShrunkStreams() {
    return (pair, context) -> {
      final Stream<A> equalLengthedSteamOfA = equaliseShrinkLength(this.as,
          () -> pair._1,
          context);
      final Stream<B> equalLengthedSteamOfB = equaliseShrinkLength(this.bs,
          () -> pair._2,
          context);

      return zip(equalLengthedSteamOfA,
          equalLengthedSteamOfB, (a, b) -> Pair.of(a, b));
    };
  }

  private Generator<Pair<A, B>> prngToPair() {
    return (prng, step) -> Pair.of(this.as.next(prng, step),
        this.bs.next(prng, step));
  }

  private AsString<Pair<A, B>> joinToString() {
    return pair -> pair
        .map(this.as.asToStringFunction(), this.bs.asToStringFunction())
        .toString();
  }

}