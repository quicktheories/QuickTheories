package org.quicktheories.quicktheories.impl;

import static org.quicktheories.quicktheories.impl.Util.equaliseShrinkLength;
import static org.quicktheories.quicktheories.impl.Util.zip;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.quicktheories.quicktheories.api.Function4;
import org.quicktheories.quicktheories.api.Pair;
import org.quicktheories.quicktheories.api.Predicate4;
import org.quicktheories.quicktheories.api.QuadConsumer;
import org.quicktheories.quicktheories.api.Subject1;
import org.quicktheories.quicktheories.api.Subject5;
import org.quicktheories.quicktheories.api.Tuple3;
import org.quicktheories.quicktheories.api.Tuple4;
import org.quicktheories.quicktheories.api.Tuple5;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Generator;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.Strategy;

public final class TheoryBuilder4<A, B, C, D> {
  private final Supplier<Strategy> state;
  private final Source<A> as;
  private final Source<B> bs;
  private final Source<C> cs;
  private final Source<D> ds;
  private final Predicate4<A, B, C, D> assumptions;

  /**
   * Builds theories about values of type A, B, C and D
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
   * @param ds
   *          the fourth source of the values to be generated and potentially
   *          shrunk
   * @param assumptions
   *          limits the possible values of type A, type B, type C and type D
   */
  public TheoryBuilder4(final Supplier<Strategy> state, final Source<A> as,
      Source<B> bs, Source<C> cs, Source<D> ds,
      Predicate4<A, B, C, D> assumptions) {
    this.state = state;
    this.as = as;
    this.bs = bs;
    this.cs = cs;
    this.ds = ds;
    this.assumptions = assumptions;
  }

  /**
   * Constrains the values a theory must be true for by the given assumption
   * 
   * @param newAssumption
   *          an assumption that must be true of all values
   * @return theory builder based on the given assumption
   */
  public TheoryBuilder4<A, B, C, D> assuming(
      Predicate4<A, B, C, D> newAssumption) {
    return new TheoryBuilder4<A, B, C, D>(this.state, this.as, this.bs, this.cs,
        this.ds,
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
      Function4<A, B, C, D, T> mapping) {
    return new MappingTheoryBuilder<>(state, combine(),
        precursor -> assumptions.test(precursor._1, precursor._2,
            precursor._3, precursor._4),
        tuple -> mapping.apply(tuple._1, tuple._2,
            tuple._3, tuple._4),
        t -> t.toString());
  }

  /**
   * Converts theory to one about a different type using the given function
   * retaining all precursor values
   * @param mapping
   *          Function from types A,B,C,D to type T
   * @return a Subject5 relating to the state of a theory involving five values
   */
  public <T> Subject5<A, B, C, D, T> asWithPrecursor(
      Function4<A, B, C, D, T> mapping) {
    Shrink<Tuple5<A, B, C, D, T>> shrink = (original,
        context) -> combineShrinks()
            .shrink(
                Tuple4.of(original._1, original._2, original._3, original._4),
                context)
            .map(precursor -> precursor.extend(mapping));

    Source<Tuple5<A, B, C, D, T>> gen = Source
        .of(generatePrecursorValueTuple(mapping)).withShrinker(shrink);
    return new PrecursorTheoryBuilder4<A, B, C, D, T>(state, gen, assumptions);
  }

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  public void check(final Predicate4<A, B, C, D> property) {
    final TheoryRunner<Tuple4<A, B, C, D>, Tuple4<A, B, C, D>> qc = new TheoryRunner<>(
        this.state.get(),
        combine(), convertPredicate(), x -> x,
        tuple4 -> "{" + tuple4._1.toString() + ", " + tuple4._2.toString()
            + ", " + tuple4._3.toString() + ", " + tuple4._4.toString() + "}");
    qc.check(
        x -> property.test(x._1, x._2, x._3, x._4));
  }

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public final void checkAssert(final QuadConsumer<A, B, C, D> property) {
    check((a, b, c, d) -> {
      property.accept(a, b, c, d);
      return true;
    });
  }

  private <T> Generator<Tuple5<A, B, C, D, T>> generatePrecursorValueTuple(
      Function4<A, B, C, D, T> mapping) {
    return prgnToTuple().andThen(precursor -> precursor.extend(mapping));
  }

  private Predicate<Tuple4<A, B, C, D>> convertPredicate() {
    return tuple -> this.assumptions.test(tuple._1, tuple._2,
        tuple._3, tuple._4);
  }

  private Source<Tuple4<A, B, C, D>> combine() {
    return Source.of(prgnToTuple()).withShrinker(combineShrinks());
  }

  private Shrink<Tuple4<A, B, C, D>> combineShrinks() {
    return (tuple, context) -> {
      Stream<A> equalLengthedSteamOfA = equaliseShrinkLength(as, () -> tuple._1,
          context);
      Stream<B> equalLengthedSteamOfB = equaliseShrinkLength(bs, () -> tuple._2,
          context);
      Stream<C> equalLengthedSteamOfC = equaliseShrinkLength(cs, () -> tuple._3,
          context);
      Stream<D> equalLengthedSteamOfD = equaliseShrinkLength(ds, () -> tuple._4,
          context);

      Stream<Pair<C, D>> cdStream = zip(equalLengthedSteamOfC,
          equalLengthedSteamOfD,
          (c, d) -> Pair.of(c, d));

      Stream<Tuple3<B, C, D>> bcdStream = zip(equalLengthedSteamOfB,
          cdStream,
          (b, cd) -> cd.prepend(b));

      return zip(
          equalLengthedSteamOfA, bcdStream,
          (a, bcd) -> bcd.prepend(a));

    };
  }

  private Generator<Tuple4<A, B, C, D>> prgnToTuple() {
    return (prng, step) -> Tuple4.of(
        this.as.next(prng, step),
        this.bs.next(prng, step),
        this.cs.next(prng, step),
        this.ds.next(prng, step));
  }

}