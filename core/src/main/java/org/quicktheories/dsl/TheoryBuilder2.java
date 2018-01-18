package org.quicktheories.dsl;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.CheckReturnValue;

import org.quicktheories.api.AsString;
import org.quicktheories.api.Pair;
import org.quicktheories.api.Subject1;
import org.quicktheories.api.Subject2;
import org.quicktheories.api.Subject3;
import org.quicktheories.api.Tuple3;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;
import org.quicktheories.generators.Generate;
import org.quicktheories.impl.TheoryRunner;

public final class TheoryBuilder2<A, B> implements Subject2<A, B> {
  private final Supplier<Strategy> state;
  private final Gen<A> as;
  private final Gen<B> bs;
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
  public TheoryBuilder2(final Supplier<Strategy> state, final Gen<A> as,
      Gen<B> bs, BiPredicate<A, B> assumptions) {
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
  @CheckReturnValue
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
        combine());
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
  @CheckReturnValue
  public <T> Subject1<T> as(
      BiFunction<A, B, T> mapping) {
    
    return new MappingTheoryBuilder<>(this.state, combine(),
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
  @CheckReturnValue
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
   *          Function from types A and B to a generator of type T
   * @return a Subject3 relating to the state of a theory involving three values
   */
  @CheckReturnValue
  public <T> Subject3<A, B, T> withPrecursorGen(BiFunction<A, B, Gen<T>> mapping) {
    return withPrecursorGen(mapping, t -> t.toString());
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
  @CheckReturnValue
  public <T> Subject3<A, B, T> asWithPrecursor(BiFunction<A, B, T> mapping,
      Function<T, String> typeToString) {
    return withPrecursorGen((a, b) -> Generate.constant(mapping.apply(a, b)), typeToString);
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
  @CheckReturnValue
  public <T> Subject3<A, B, T> withPrecursorGen(BiFunction<A, B, Gen<T>> mapping,
                                               Function<T, String> typeToString) {
    final Gen<Tuple3<A, B, T>> g = prng -> {
      final A a = this.as.generate(prng);
      final B b = this.bs.generate(prng);
      return Tuple3.of(a, b, mapping.apply(a, b).generate(prng));
    };

    final AsString<Tuple3<A, B, T>> desc = tuple -> tuple
        .map(a -> as.asString(a), b -> bs.asString(b),
            typeToString)
        .toString();

    final Gen<Tuple3<A, B, T>> gen = g.describedAs(desc);
    return new PrecursorTheoryBuilder2<>(this.state, gen,
        this.assumptions);

  }
  

  private Gen<Pair<A, B>> combine() {
    return this.as.zip(bs, (t,b) -> Pair.of(t,b))
        .describedAs(joinToString())
        .assuming(combineAssumptions());
  }
  
  private Predicate<Pair<A, B>> combineAssumptions() {
    return precursor -> this.assumptions.test(precursor._1,
        precursor._2);
  }



  private AsString<Pair<A, B>> joinToString() {
    return pair -> pair
        .map(a -> as.asString(a), b -> bs.asString(b))
        .toString();
  }

}