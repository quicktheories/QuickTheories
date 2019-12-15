package org.quicktheories.dsl;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.CheckReturnValue;

import org.quicktheories.api.AsString;
import org.quicktheories.api.Function3;
import org.quicktheories.api.Predicate3;
import org.quicktheories.api.Subject1;
import org.quicktheories.api.Subject4;
import org.quicktheories.api.TriConsumer;
import org.quicktheories.api.Tuple3;
import org.quicktheories.api.Tuple4;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;
import org.quicktheories.impl.TheoryRunner;

public final class TheoryBuilder3<A, B, C> {
  private final Supplier<Strategy> state;
  private final Gen<A> as;
  private final Gen<B> bs;
  private final Gen<C> cs;
  private final Predicate3<A, B, C> assumptions;

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
  public TheoryBuilder3(final Supplier<Strategy> state, final Gen<A> as,
      Gen<B> bs, Gen<C> cs, Predicate3<A, B, C> assumptions) {
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
  @CheckReturnValue
  public TheoryBuilder3<A, B, C> assuming(Predicate3<A, B, C> newAssumption) {
    return new TheoryBuilder3<>(this.state, this.as, this.bs, this.cs,
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
      Function3<A, B, C, T> mapping) {
    return new MappingTheoryBuilder<>(this.state, combine(),
        tuple -> mapping.apply(tuple._1, tuple._2,
            tuple._3),
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
   *          Function from types A,B,C to type T
   * @return a Subject4 relating to the state of a theory involving four values
   */
  @CheckReturnValue
  public <T> Subject4<A, B, C, T> asWithPrecursor(
      Function3<A, B, C, T> mapping) {
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
   *          Function from types A,B,C to type T
   * @param typeToString
   *          Function to describe generated type
   * @return a Subject4 relating to the state of a theory involving four values
   */
  @CheckReturnValue
  public <T> Subject4<A, B, C, T> asWithPrecursor(
      Function3<A, B, C, T> mapping, Function<T, String> typeToString) {

    final AsString<Tuple4<A, B, C, T>> desc = tuple -> tuple
        .map(a -> as.asString(a), b -> bs.asString(b),
            c -> cs.asString(c),
            typeToString)
        .toString();

    final Gen<Tuple4<A, B, C, T>> gen = generatePrecursorValueTuple(mapping)
        .describedAs(desc);
    
    return new PrecursorTheoryBuilder3<>(this.state, gen,
        this.assumptions);
  }

  /**
   * Checks a boolean property across a random sample of possible values
   *
   * @param property
   *          property to check
   */
  public void check(final Predicate3<A, B, C> property) {
    final TheoryRunner<Tuple3<A, B, C>, Tuple3<A, B, C>> qc = TheoryRunner
        .runner(
            this.state.get(),
            combine());
    qc.check(x -> property.test(x._1, x._2, x._3));
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

  private <T> Gen<Tuple4<A, B, C, T>> generatePrecursorValueTuple(
      Function3<A, B, C, T> mapping) {
    return combine().map(precursor -> precursor.extend(mapping));
  }


  private Gen<Tuple3<A, B, C>> combine() {
    return prgnToTuple()
        .describedAs(joinToString())
        .assuming(combineAssumptions());
  }

  private Predicate<Tuple3<A, B, C>> combineAssumptions() {
    return precursor -> this.assumptions.test(precursor._1,
        precursor._2,  precursor._3);
  }
  
  private Gen<Tuple3<A, B, C>> prgnToTuple() {
    return prng -> Tuple3.of(this.as.generate(prng),
        this.bs.generate(prng), this.cs.generate(prng));
  }

  private AsString<Tuple3<A, B, C>> joinToString() {
    return tuple -> tuple.map(a -> as.asString(a),
        b -> bs.asString(b), c -> cs.asString(c)).toString();
  }

}