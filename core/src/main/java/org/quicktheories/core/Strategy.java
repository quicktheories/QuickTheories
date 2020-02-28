package org.quicktheories.core;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Describes the components that make up the QuickTheory: random generator,
 * number of examples, number of shrink cycles and falsification reporter
 */
public class Strategy {

  private final PseudoRandom prng;
  private final int generateAttempts;
  private final int examples;
  private final long testingTimeMillis;
  private final int shrinkCycles;
  private final int minStatefulSteps;
  private final int maxStatefulSteps;
  private final Reporter reporter;
  private final Function<PseudoRandom, Guidance> guidance;

  /**
   * The strategy used in a QuickTheory
   * 
   * @param prng
   *          PseudoRandom used to generate random values
   * @param examples
   *          number of examples to generate (unless testingTimeMillis is reached first)
   * @param testingTimeMillis
   *          the number of milliseconds to generate examples for (unless examples is reached first)
   * @param shrinkCycles
   *          maximum number of shrink cycles that will occur if falsifying
   *          value found
   * @param generateAttempts
   *          maximum number of attempts (due to failed assumptions) to generate each example 
   * @param reporter
   *          a reporter to provide the results if values are exhausted or if a
   *          value falsifies
   * @param guidance
   *          Strategy to use to guide search
   */
  public Strategy(final PseudoRandom prng, final int examples, final long testingTimeMillis,
                  final int shrinkCycles,  final int minStatefulSteps, final int maxStatefulSteps, final int generateAttempts,
                  Reporter reporter, Function<PseudoRandom, Guidance> guidance) {
    this.prng = prng;
    this.examples = examples;
    this.testingTimeMillis = testingTimeMillis;
    this.shrinkCycles = shrinkCycles;
    this.minStatefulSteps = minStatefulSteps;
    this.maxStatefulSteps = maxStatefulSteps;
    this.reporter = reporter;
    this.generateAttempts = generateAttempts;
    this.guidance = guidance;
  }

  /**
   * Returns the number of examples
   * 
   * @return number of examples
   */
  public int examples() {
    return this.examples;
  }

  public long testingTimeMillis() {
    return this.testingTimeMillis;
  }

  /**
   * Returns the PseudoRandom used
   * 
   * @return a PseudoRandom
   */
  public PseudoRandom prng() {
    return prng;
  }

  /**
   * Returns the maximum number of shrink cycles that will occur
   * 
   * @return number of shrink cycles
   */
  public int shrinkCycles() {
    return this.shrinkCycles;
  }

  /**
   * Returns the minimum number of steps that will be generated in a run of a stateful model
   *
   * @return the minimum number of steps that will be generated in a run of a stateful model
   */
  public int minStatefulSteps() {
    return this.minStatefulSteps;
  }

  /**
   * Returns the maximum number of steps that will be generated in a run of a stateful model
   *
   * @return the maximum number of steps that will be generated in a run of a stateful model
   */
  public int maxStatefulSteps() {
    return this.maxStatefulSteps;
  }

  /**
   * Returns the maximum number of times to try to retrieve each value before giving up.
   * 
   * @return maximum number of times to try to retrieve each value
   */
  public int generateAttempts() {
    return this.generateAttempts;
  }

  /**
   * Returns the reporter used to inform the user of failures
   * 
   * @return a Reporter
   */
  public Reporter reporter() {
    return this.reporter;
  }

  public Guidance guidance() {
    return guidance.apply(prng());
  }
  
  /**
   * Creates a strategy with a fixed seed
   * 
   * @param seed
   *          initial seed of the PseudoRandom generator
   * @return a strategy with a PseudoRandom value generator with initial seed
   *         supplied
   */
  public Strategy withFixedSeed(long seed) {
    return new Strategy(defaultPRNG(seed), examples, testingTimeMillis, shrinkCycles, minStatefulSteps, maxStatefulSteps,
            generateAttempts, reporter, guidance);
  }

  /**
   * Creates a strategy which will produce a maximum number of examples supplied (unless {@link #withTestingTime(long, TimeUnit)}
   * is reached first).
   *
   * @param examples
   *          the maximum number of examples to be generated. Pass -1 to rely solely on {@link #withTestingTime(long, TimeUnit)}
   * @return a strategy with the maximum number of examples as supplied
   */
  public Strategy withExamples(int examples) {
    return new Strategy(prng, examples, testingTimeMillis, shrinkCycles, minStatefulSteps, maxStatefulSteps,
            generateAttempts, reporter, guidance);
  }

  /**
   * Removes the limit on the number of examples run (limiting the test run by the value passed to
   * {@link #withTestingTime(long, TimeUnit)}
   *
   * @return A Strategy that runs an unlimited number of examples and is only limited by {@link #withTestingTime(long, TimeUnit)}
   */
  public Strategy withUnlimitedExamples() {
    return withExamples(-1);
  }

  /**
   * Creates a strategy which will produce examples for the amount of time given (unless the value passed to
   * {@link #withExamples(int)} is reached first)
   *
   * @param time the amount of time to generate tests for. Pass a value {@literal <= 0} to rely solely on {@link #withExamples(int)}
   * @param timeUnit the time unit for the given time
   * @return a strategy with the testing time set to the amount of time given.
   */
  public Strategy withTestingTime(long time, TimeUnit timeUnit) {
    return new Strategy(prng, examples, timeUnit.toMillis(time), shrinkCycles, minStatefulSteps, maxStatefulSteps,
            generateAttempts, reporter, guidance);
  }

  /**
   * Removes the time limit on the duration of the run. NOTE: This is the default but can be used to be explicit.
   *
   * @return A QuickTheory that runs for an unlimited amount of time and is only limited by {@link #withExamples(int)}
   */
  public Strategy withUnlimitedTestingTime() {
    return withTestingTime(-1, TimeUnit.MILLISECONDS);
  }

  /**
   * Creates a strategy which will allow the suppled number of failed generation attempts before
   * erroring 
   * @param generateAttempts Maximum number of failed geneation attempts
   * @return a strategy
   */
  public Strategy withGenerateAttempts(int generateAttempts) {
    return new Strategy(prng, examples, testingTimeMillis, shrinkCycles, minStatefulSteps, maxStatefulSteps,
            generateAttempts, reporter, guidance);
  }
  
  /**
   * Creates a strategy using the supplied guidance approach
   * @param guidance Guidance approach to use
   * @return a strategy
   */
  public Strategy withGuidance(Function<PseudoRandom, Guidance> guidance) {
    return new Strategy(prng, examples, testingTimeMillis, shrinkCycles, minStatefulSteps, maxStatefulSteps,
            generateAttempts, reporter, guidance);
  }

  /**
   * Creates a strategy with the maximum number of shrink attempts supplied
   * 
   * @param shrinks
   *          the maximum number of shrinks that will be attempted
   * @return a strategy with the maximum number of shrinks as supplied
   */
  public Strategy withShrinkCycles(int shrinks) {
    return new Strategy(prng, examples, testingTimeMillis, shrinks, minStatefulSteps, maxStatefulSteps,
            generateAttempts, reporter, guidance);
  }

  public Strategy withMinStatefulSteps(int minStatefulSteps)
  {
    return new Strategy(prng, examples, testingTimeMillis, shrinkCycles, minStatefulSteps, maxStatefulSteps,
            generateAttempts, reporter, guidance);
  }

  public Strategy withMaxStatefulSteps(int maxStatefulSteps)
  {
    return new Strategy(prng, examples, testingTimeMillis, shrinkCycles, minStatefulSteps, maxStatefulSteps,
            generateAttempts, reporter, guidance);
  }

  /**
   * Creates a strategy using the supplied reporter
   * @param reporter Reporter to use
   * @return a strategy with suppled reporter
   */
  public Strategy withReporter(Reporter reporter) {
    return new Strategy(prng, examples, testingTimeMillis, shrinkCycles, minStatefulSteps, maxStatefulSteps,
            generateAttempts, reporter, guidance);
  }

  /**
   * Returns the default PRNG with initial seed supplied. Note: the XOrShiftPRNG
   * cannot have seed 0 (if to be useful) and will therefore reset the seed to
   * 1.
   * 
   * @param seed
   *          initial seed of the PseudoRandom generator.
   * @return an XOrShiftPRNG
   */
  public static PseudoRandom defaultPRNG(long seed) {
    return new XOrShiftPRNG(seed);
  }
}
