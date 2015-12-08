package org.quicktheories.quicktheories.impl;

import static org.quicktheories.quicktheories.impl.Util.equaliseShrinkLength;
import static org.quicktheories.quicktheories.impl.Util.zip;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.quicktheories.quicktheories.api.Function3;
import org.quicktheories.quicktheories.api.Pair;
import org.quicktheories.quicktheories.api.Predicate3;
import org.quicktheories.quicktheories.api.Subject1;
import org.quicktheories.quicktheories.api.Subject4;
import org.quicktheories.quicktheories.api.TriConsumer;
import org.quicktheories.quicktheories.api.Tuple3;
import org.quicktheories.quicktheories.api.Tuple4;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Generator;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.Strategy;

public final class TheoryBuilder3<A, B, C> {
  private final Supplier<Strategy> state;
  private final Source<A> as;
  private final Source<B> bs;
  private final Source<C> cs;
  private final Predicate3<A, B, C> assumptions;

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

  /**
   * Builds theories about values of type A, B and C
   * 
   * @param state
   *          supplies the strategy to be implemented
   * @param as
   *          the first source of the values to be generated and potentially
   *          shrunk
   * @param bs
   *          the second source of the values to be generated and potentially
   *          shrunk
   * @param cs
   *          the third source of the values to be generated and potentially
   *          shrunk
   * @param assumptions
   *          limits the possible values of type A, type B and type C
   */
  public TheoryBuilder3(final Supplier<Strategy> state, final Source<A> as,
      Source<B> bs, Source<C> cs, Predicate3<A, B, C> assumptions) {
    this.state = state;
    this.as = as;
    this.bs = bs;
    this.cs = cs;
    this.assumptions = assumptions;
  }

  /**
   * Constrains the values a theory must be true for by the given assumption
   * 
   * @param newAssumption
   *          an assumption that must be true of all values
   * @return theory builder based on the given assumption
   */
  public TheoryBuilder3<A, B, C> assuming(Predicate3<A, B, C> newAssumption) {
    return new TheoryBuilder3<A, B, C>(this.state, this.as, this.bs, this.cs,
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
  public <T> Subject1<T> as(
      Function3<A, B, C, T> mapping) {
    return new MappingTheoryBuilder<>(state, combine(),
        precursor -> assumptions.test(precursor._1, precursor._2,
            precursor._3),
        tuple -> mapping.apply(tuple._1, tuple._2,
            tuple._3));
  }

  /**
   * Converts theory to one about a different type using the given function
   * retaining all precursor values 
   *   
   * @param mapping
   * Function from types A,B,C to type T
   * @return a Subject4 relating to the state of a theory involving four values
   */
  public <T> Subject4<A, B, C, T> asWithPrecursor(
      Function3<A, B, C, T> mapping) {
    Shrink<Tuple4<A, B, C, T>> shrink = (original,
        context) -> combineShrinks().shrink(
            Tuple3.of(original._1, original._2, original._3), context)
            .map(precursor -> precursor.extend(mapping));

    Source<Tuple4<A, B, C, T>> gen = Source
        .of(generatePrecursorValueTuple(mapping)).withShrinker(shrink);
    return new PrecursorTheoryBuilder3<A, B, C, T>(state, gen, assumptions);
  }

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  public void check(final Predicate3<A, B, C> property) {
    final TheoryRunner<Tuple3<A, B, C>, Tuple3<A, B, C>> qc = new TheoryRunner<>(
        this.state.get(),
        combine(), convertPredicate(), x -> x);
    qc.check(
        x -> property.test(x._1, x._2, x._3));
  }

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public final void checkAssert(final TriConsumer<A, B, C> property) {
    check((a, b, c) -> {
      property.accept(a, b, c);
      return true;
    });
  }

  private <T> Generator<Tuple4<A, B, C, T>> generatePrecursorValueTuple(
      Function3<A, B, C, T> mapping) {
    return prgnToTuple().andThen(precursor -> precursor.extend(mapping));
  }

  private Predicate<Tuple3<A, B, C>> convertPredicate() {
    return tuple -> this.assumptions.test(tuple._1, tuple._2,
        tuple._3);
  }

  private Source<Tuple3<A, B, C>> combine() {
    return Source.of(prgnToTuple()).withShrinker(combineShrinks());
  }

  private Shrink<Tuple3<A, B, C>> combineShrinks() {
    return (tuple, context) -> {
      Stream<A> equalLengthedSteamOfA = equaliseShrinkLength(as, () -> tuple._1,
          context);
      Stream<B> equalLengthedSteamOfB = equaliseShrinkLength(bs, () -> tuple._2,
          context);
      Stream<C> equalLengthedSteamOfC = equaliseShrinkLength(cs, () -> tuple._3,
          context);

      Stream<Pair<B, C>> bcStream = zip(equalLengthedSteamOfB,
          equalLengthedSteamOfC, (b, c) -> Pair.of(b, c));

      return zip(
          bcStream, equalLengthedSteamOfA, (bc, a) -> bc.prepend(a));

    };
  }

  private Generator<Tuple3<A, B, C>> prgnToTuple() {
    return (prng, step) -> Tuple3.of(this.as.next(prng, step),
        this.bs.next(prng, step), this.cs.next(prng, step));
  }

}