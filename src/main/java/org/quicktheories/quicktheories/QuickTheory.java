package org.quicktheories.quicktheories;

import java.util.function.Supplier;

import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.impl.TheoryBuilder;
import org.quicktheories.quicktheories.impl.TheoryBuilder2;
import org.quicktheories.quicktheories.impl.TheoryBuilder3;
import org.quicktheories.quicktheories.impl.TheoryBuilder4;

/**
 * Entry point for property based testing.
 */
public class QuickTheory {

  private final Supplier<Strategy> state;

  private QuickTheory(Supplier<Strategy> state) {
    this.state = state;
  }

  /**
   * Start defining a property using an explicit strategy overriding any
   * properties set elsewhere.
   * 
   * @param strategy
   *          Strategy to use
   * @return a QuickTheory
   */
  public static QuickTheory qt(Supplier<Strategy> strategy) {
    return new QuickTheory(strategy);
  }

  /**
   * Start defining a property using default system settings
   * 
   * @return a QuickTheory
   */
  public static QuickTheory qt() {
    return new QuickTheory(() -> Configuration.systemStrategy());
  }

  /**
   * Sets the seed to be used for randomness overriding any value set elsewhere.
   * 
   * @param seed
   *          Seed to use
   * @return a QuickTheory using the given seed
   */
  public QuickTheory withFixedSeed(long seed) {
    return new QuickTheory(() -> state.get().withFixedSeed(seed));
  }

  /**
   * Sets the number of examples to use to verify a property overriding any
   * value set elsewhere
   * 
   * @param examples
   *          number of examples to use
   * @return a QuickTheory using the given number of examples
   */
  public QuickTheory withExamples(int examples) {
    return new QuickTheory(() -> state.get().withExamples(examples));
  }

  /**
   * Sets the number of shrink cycles to use overriding any value set elsewhere
   * 
   * @param shrinks
   *          number of shrinks to use
   * @return a QuickTheory using the given number of shrinks
   */
  public QuickTheory withShrinkCycles(int shrinks) {
    return new QuickTheory(() -> state.get().withShrinkCycles(shrinks));
  }

  /**
   * Specifies a Source of type A for which the property must hold true
   * 
   * @param <A>
   *          type of values the theory is expressed in
   * @param values
   *          the source of values over which the property should be true
   * @return state for which a property can be defined
   */
  public <A> TheoryBuilder<A, A> forAll(final Source<A> values) {
    return new TheoryBuilder<>(state, values, a -> true, x -> x,
        a -> a.toString());
  }

  /**
   * Specifies Sources of type A and B for which the property must hold true
   * 
   * @param <A>
   *          type of values the theory is expressed in
   * @param <B>
   *          type of values the theory is expressed in
   * @param as
   *          the source of values of type A over which the property should be
   *          true
   * @param bs
   *          the source of values of type B over which the property should be
   *          true
   * @return state for which a property can be defined
   */
  public <A, B> TheoryBuilder2<A, B> forAll(
      final Source<A> as,
      final Source<B> bs) {
    return new TheoryBuilder2<>(state, as, bs, (a, b) -> true);
  }

  /**
   * Specifies Sources of type A, B and C for which the property must hold true
   * 
   * @param <A>
   *          type of values the theory is expressed in
   * @param <B>
   *          type of values the theory is expressed in
   * @param <C>
   *          type of values the theory is expressed in
   * @param as
   *          the source of values of type A over which the property should be
   *          true
   * @param bs
   *          the source of values of type B over which the property should be
   *          true
   * @param cs
   *          the source of values of type C over which the property should be
   *          true
   * @return state for which a property can be defined
   */
  public <A, B, C> TheoryBuilder3<A, B, C> forAll(
      final Source<A> as,
      final Source<B> bs,
      final Source<C> cs) {
    return new TheoryBuilder3<>(state, as, bs, cs, (a, b, c) -> true);
  }

  /**
   * Specifies Sources of type A, B, C and D for which the property must hold
   * true
   * 
   * @param <A>
   *          type of values the theory is expressed in
   * @param <B>
   *          type of values the theory is expressed in
   * @param <C>
   *          type of values the theory is expressed in
   * @param <D>
   *          type of values the theory is expressed in
   * @param as
   *          the source of values of type A over which the property should be
   *          true
   * @param bs
   *          the source of values of type B over which the property should be
   *          true
   * @param cs
   *          the source of values of type C over which the property should be
   *          true
   * @param ds
   *          the source of values of type D over which the property should be
   *          true
   * @return state for which a property can be defined
   */
  public <A, B, C, D> TheoryBuilder4<A, B, C, D> forAll(
      final Source<A> as,
      final Source<B> bs,
      final Source<C> cs,
      final Source<D> ds) {
    return new TheoryBuilder4<>(state, as, bs, cs, ds, (a, b, c, d) -> true);
  }

}
