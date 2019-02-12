package org.quicktheories;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.CheckReturnValue;

import org.quicktheories.core.Configuration;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Guidance;
import org.quicktheories.core.PseudoRandom;
import org.quicktheories.core.Reporter;
import org.quicktheories.core.Strategy;
import org.quicktheories.dsl.TheoryBuilder;
import org.quicktheories.dsl.TheoryBuilder2;
import org.quicktheories.dsl.TheoryBuilder3;
import org.quicktheories.dsl.TheoryBuilder4;
import org.quicktheories.impl.stateful.StatefulCore;
import org.quicktheories.impl.stateful.StatefulTheory;

/**
 * Entry point for property based testing.
 */
@CheckReturnValue
public class QuickTheory {

  protected final Supplier<Strategy> state;

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
   * Uses the configuration from the selected registered profile
   *
   * @param testClass The class that registered the profiles
   * @return a QuickTheory using the configuration from the selected registered profile
   */
  public QuickTheory withRegisteredProfiles(Class<?> testClass) {
    return new QuickTheory(() -> Configuration.initialStrategy(Configuration.ensureLoaded(testClass)));
  }

  /**
   * Uses the configuration from a specific profile
   *
   * @param testClass The class that registered the profile
   * @param name The name of the registered profile
   * @return a QuickTheory using the configuration from the given profile
   */
  public QuickTheory withProfile(Class<?> testClass, String name) {
    return new QuickTheory(() -> Configuration.profileStrategy(Configuration.ensureLoaded(testClass), name));
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
   * value set elsewhere. The property runs until the number of examples is reached,
   * the amount of time given to {@link #withTestingTime(long, TimeUnit)} passes, or a falsifying example is found.
   * 
   * @param examples
   *          number of examples to use. Pass -1 to rely solely on {@link #withTestingTime(long, TimeUnit)}
   * @return a QuickTheory using the given number of examples
   */
  public QuickTheory withExamples(int examples) {
    return new QuickTheory(() -> state.get().withExamples(examples));
  }

  /**
   * Removes the limit on the number of examples run (limiting the test run by the value passed to
   * {@link #withTestingTime(long, TimeUnit)}
   *
   * @return A QuickTheory that runs an unlimited number of examples and is only limited by {@link #withTestingTime(long, TimeUnit)}
   */
  public QuickTheory withUnlimitedExamples() {
    return withExamples(-1);
  }

  /**
   * Sets the amount of time to generate and run examples for. The property runs until the given amount of time
   * passes, the number of examples passed to {@link #withExamples(int)} is reached, or a falsifying example is found.
   *
   * @param time the amount of time to generate tests for. Pass a value {@literal <= 0} to rely solely on {@link #withExamples(int)}
   * @param timeUnit the time unit for the given time
   * @return a QuickTheory using the given testing time
   */
  public QuickTheory withTestingTime(long time, TimeUnit timeUnit) {
    return new QuickTheory(() -> state.get().withTestingTime(time, timeUnit));
  }

  /**
   * Removes the time limit on the duration of the run. NOTE: This is the default but can be used to be explicit.
   *
   * @return A QuickTheory that runs for an unlimited amount of time and is only limited by {@link #withExamples(int)}
   */
  public QuickTheory withUnlimitedTestingTime() {
    return withTestingTime(-1, TimeUnit.MILLISECONDS);
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
   * Sets the minimum of number of steps to be generated when running a stateful model
   *
   * @param minStatefulSteps minimum number of steps to generate
   * @return a QuickTheory using the new minimum number of steps
   */
  public QuickTheory withMinStatefulSteps(int minStatefulSteps) {
    return new QuickTheory(() -> state.get().withMinStatefulSteps(minStatefulSteps));
  }

  /**
   * Sets the maximum of number of steps to be generated when running a stateful model
   *
   * @param maxStatefulSteps maximum number of steps to generate
   * @return a QuickTheory using the new maximum number of steps
   */
  public QuickTheory withMaxStatefulSteps(int maxStatefulSteps) {
    return new QuickTheory(() -> state.get().withMaxStatefulSteps(maxStatefulSteps));
  }

  /**
   * Sets the number of generate attempts to use
   * @param generateAttempts number of generate attempts
   * @return n a QuickTheory using the given number of generate attempts
   */
  public QuickTheory withGenerateAttempts(int generateAttempts) {
    return new QuickTheory(() -> state.get().withGenerateAttempts(generateAttempts));
  }
  
  /**
   * Sets guidance approach to use
   * @param guidance Guidance approach
   * @return a QuickTheory using the given guidance
   */
  public QuickTheory withGuidance(Function<PseudoRandom, Guidance> guidance) {
    return new QuickTheory(() -> state.get().withGuidance(guidance));
  }

  /**
   * Sets reporter to use
   * @param reporter Reporter to use
   * @return a QuickTheory using the given reporter
   */
  public QuickTheory withReporter(Reporter reporter) {
    return new QuickTheory(() -> state.get().withReporter(reporter));
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
  public <A> TheoryBuilder<A> forAll(final Gen<A> values) {
    return new TheoryBuilder<>(state, values);
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
      final Gen<A> as,
      final Gen<B> bs) {
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
      final Gen<A> as,
      final Gen<B> bs,
      final Gen<C> cs) {
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
      final Gen<A> as,
      final Gen<B> bs,
      final Gen<C> cs,
      final Gen<D> ds) {
    return new TheoryBuilder4<>(state, as, bs, cs, ds, (a, b, c, d) -> true);
  }

  /**
   * Set the stateful model used to generate examples. The model is only used if
   * {@link StatefulTheoryBuilder#checkStateful()} is called instead of forAll.
   *
   * Stateful models customize QuickTheory properties with the following:
   * <ul>
   *   <li>Raises generate attempts to account for cases where there are a small number of steps, which can accidentally
   *   exhaust the generator
   *   <li>Turn off shrinking because the current shrinker is not effective at shrinking stateful models for a variety
   *   of reasons
   * </ul>
   * These properties can be overridden just as they would with any QuickTheory property.
   *
   * @param theory A supplier of the stateful model used to generate examples
   * @see #stateful(Supplier)
   * @see StatefulTheoryBuilder#checkStateful()
   * @return A {@link StatefulTheoryBuilder} that can continue to be used to configure QuickTheories
   * or run the model
   */
  public StatefulTheoryBuilder withStatefulModel(final Supplier<StatefulTheory<?>> theory) {
    return new StatefulTheoryBuilder(() -> state.get().withShrinkCycles(0).withGenerateAttempts(100), theory);
  }

  /**
   * Set the stateful model used to generate examples. The model is run immediately as if
   * {@code withStatefulModel(...).checkStateful()} were called. This is useful when no further customization to QuickTheory
   * properties is needed.
   *
   * @param theory A supplier of the stateful model used to generate examples
   * @see #withStatefulModel(Supplier)
   * @see StatefulTheoryBuilder#checkStateful()
   */
  public void stateful(final Supplier<StatefulTheory<?>> theory) {
    withStatefulModel(theory).checkStateful();
  }

  public static class StatefulTheoryBuilder extends QuickTheory {

    private final Supplier<StatefulTheory<?>> statefulTheory;
    private StatefulTheoryBuilder(Supplier<Strategy> state, Supplier<StatefulTheory<?>> statefulTheory) {
      super(state);
      this.statefulTheory = statefulTheory;
    }

    public void checkStateful() {
      new TheoryBuilder<>(state, StatefulCore.generator(statefulTheory)).check(sm -> sm.run(state));
    }

  }

}
