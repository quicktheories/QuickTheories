package org.quicktheories.core;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.StreamSupport;

/**
 * Configures the Strategy for the corresponding QuickTheory
 *
 */
public abstract class Configuration {

  public final static String SEED = "QT_SEED";
  public final static String EXAMPLES = "QT_EXAMPLES";
  public final static String SHRINKS = "QT_SHRINKS";
  public final static String GENERATE_ATTEMPTS = "QT_ATTEMPTS";

  /**
   * Sets the strategy for the corresponding QuickTheory. Default values are set
   * if the user has not overridden them elsewhere.
   * 
   * @return a Strategy
   */
  public static Strategy systemStrategy() {
    return new Strategy(defaultPRNG(pickSeed()), pickExamples(), pickShrinks(), pickAttempts(),
        new ExceptionReporter(), pickGuidance());
  }

  private static int pickAttempts() {
    Optional<String> userValue = Optional.ofNullable(System.getProperty(GENERATE_ATTEMPTS));
    return userValue.map(Integer::valueOf).orElseGet(() -> 10);
  }

  private static int pickShrinks() {
    Optional<String> userValue = Optional
        .ofNullable(System.getProperty(SHRINKS));
    return userValue.map(Integer::valueOf)
        .orElseGet(() -> pickExamples() * 100);
  }

  private static int pickExamples() {
    Optional<String> userValue = Optional
        .ofNullable(System.getProperty(EXAMPLES));
    return userValue.map(Integer::valueOf).orElseGet(() -> 1000);
  }

  private static long pickSeed() {
    Optional<String> userValue = Optional.ofNullable(System.getProperty(SEED));
    return userValue.map(Long::valueOf).orElseGet(() -> System.nanoTime());
  }

  private static Function<PseudoRandom, Guidance> pickGuidance() {
    ServiceLoader<GuidanceFactory> guidance = ServiceLoader.load(GuidanceFactory.class);
    return StreamSupport.stream(guidance.spliterator(), false).findFirst().orElse( prng -> new NoGuidance());
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
