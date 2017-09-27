package org.quicktheories.dsl;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.CheckReturnValue;

import org.quicktheories.api.AsString;
import org.quicktheories.api.Function4;
import org.quicktheories.api.Predicate4;
import org.quicktheories.api.QuadConsumer;
import org.quicktheories.api.Subject1;
import org.quicktheories.api.Subject5;
import org.quicktheories.api.Tuple4;
import org.quicktheories.api.Tuple5;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;
import org.quicktheories.impl.TheoryRunner;

public final class TheoryBuilder4<A, B, C, D> {
  private final Supplier<Strategy> state;
  private final Gen<A> as;
  private final Gen<B> bs;
  private final Gen<C> cs;
  private final Gen<D> ds;
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
  public TheoryBuilder4(final Supplier<Strategy> state, final Gen<A> as,
      Gen<B> bs, Gen<C> cs, Gen<D> ds,
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
  @CheckReturnValue
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
  @CheckReturnValue
  public <T> Subject1<T> as(
      Function4<A, B, C, D, T> mapping) {
    return new MappingTheoryBuilder<>(this.state, combine(),
        tuple -> mapping.apply(tuple._1, tuple._2,
            tuple._3, tuple._4),
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
   *          Function from types A,B,C,D to type T
   * @return a Subject5 relating to the state of a theory involving five values
   */
  @CheckReturnValue
  public <T> Subject5<A, B, C, D, T> asWithPrecursor(
      Function4<A, B, C, D, T> mapping) {
    return asWithPrecursor(mapping, t -> t.toString());
  }

  /**
   * Converts theory to one about a different type using the given function
   * retaining all precursor values
   *
   * @param <T>
   *          type to create theory about
   *
   * @param mapping
   *          Function from types A,B,C,D to type T
   * @param typeToString
   *          Function to describe generated type
   * @return a Subject5 relating to the state of a theory involving five values
   */
  @CheckReturnValue
  public <T> Subject5<A, B, C, D, T> asWithPrecursor(
      Function4<A, B, C, D, T> mapping, Function<T, String> typeToString) {
 
    final AsString<Tuple5<A, B, C, D, T>> desc = tuple -> tuple
        .map(a -> as.asString(a), b -> bs.asString(b),
            c -> cs.asString(c), d -> ds.asString(d),
            typeToString)
        .toString();

    final Gen<Tuple5<A, B, C, D, T>> gen = generatePrecursorValueTuple(mapping)
        .describedAs(desc);
    return new PrecursorTheoryBuilder4<A, B, C, D, T>(this.state, gen,
        this.assumptions);
  }

  /**
   * Checks a boolean property across a random sample of possible values
   *
   * @param property
   *          property to check
   */
  public void check(final Predicate4<A, B, C, D> property) {
    final TheoryRunner<Tuple4<A, B, C, D>, Tuple4<A, B, C, D>> qc = TheoryRunner
        .runner(
            this.state.get(),
            combine());
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

  private <T> Gen<Tuple5<A, B, C, D, T>> generatePrecursorValueTuple(
      Function4<A, B, C, D, T> mapping) {
    return prgnToTuple().map(precursor -> precursor.extend(mapping));
  }


  private Gen<Tuple4<A, B, C, D>> combine() {
    return prgnToTuple()
        .describedAs(joinToString())
        .assuming(convertPredicate());
  }

  private Predicate<Tuple4<A, B, C, D>> convertPredicate() {
    return tuple -> this.assumptions.test(tuple._1, tuple._2,
        tuple._3, tuple._4);
  }
  

  private Gen<Tuple4<A, B, C, D>> prgnToTuple() {
    return prng -> Tuple4.of(
        this.as.generate(prng),
        this.bs.generate(prng),
        this.cs.generate(prng),
        this.ds.generate(prng));
  }

  private AsString<Tuple4<A, B, C, D>> joinToString() {
    return tuple -> tuple
        .map(a -> as.asString(a), b-> bs.asString(b),
            c -> cs.asString(c), d -> ds.asString(d))
        .toString();
  }

}