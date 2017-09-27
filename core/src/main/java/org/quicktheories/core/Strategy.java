package org.quicktheories.core;

/**
 * Describes the components that make up the QuickTheory: random generator,
 * number of examples, number of shrink cycles and falsification reporter
 */
public class Strategy {

  private final PseudoRandom prng;
  private final int generateAttempts;
  private final int examples;
  private final int shrinkCycles;
  private final Reporter reporter;

  /**
   * The strategy used in a QuickTheory
   * 
   * @param prng
   *          PseudoRandom used to generate random values
   * @param examples
   *          number of examples to generate
   * @param shrinkCycles
   *          maximum number of shrink cycles that will occur if falsifying
   *          value found
   * @param reporter
   *          a reporter to provide the results if values are exhausted or if a
   *          value falsifies
   */
  public Strategy(final PseudoRandom prng, final int examples,
      final int shrinkCycles, final int generateAttempts, Reporter reporter) {
    this.prng = prng;
    this.examples = examples;
    this.shrinkCycles = shrinkCycles;
    this.reporter = reporter;
    this.generateAttempts = generateAttempts;
  }

  /**
   * Returns the number of examples
   * 
   * @return number of examples
   */
  public int examples() {
    return this.examples;
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

  /**
   * Creates a strategy with a fixed seed
   * 
   * @param seed
   *          initial seed of the PseudoRandom generator
   * @return a strategy with a PseudoRandom value generator with initial seed
   *         supplied
   */
  public Strategy withFixedSeed(long seed) {
    return new Strategy(defaultPRNG(seed), examples, shrinkCycles, generateAttempts,
        reporter);
  }

  /**
   * Creates a strategy which will produce a maximum number of examples supplied
   * 
   * @param examples
   *          the maximum number of examples to be generated
   * @return a strategy with the maximum number of examples as supplied
   */
  public Strategy withExamples(int examples) {
    return new Strategy(prng, examples, shrinkCycles, generateAttempts, reporter);
  }

  /**
   * Creates a strategy with the maximum number of shrink attempts supplied
   * 
   * @param shrinks
   *          the maximum number of shrinks that will be attempted
   * @return a strategy with the maximum number of shrinks as supplied
   */
  public Strategy withShrinkCycles(int shrinks) {
    return new Strategy(prng, examples, shrinks, generateAttempts, reporter);
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
