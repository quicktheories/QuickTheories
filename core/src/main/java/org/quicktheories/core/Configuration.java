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

  private static final int DEFAULT_NO_ATTEMPTS = 10;
  private static final int DEFAULT_NO_EXAMPLES = 1000;
  private static final int DEFAULT_TESTING_TIME_MILLIS = -1;

  public final static String PROFILE = "QT_PROFILE";
  public final static String SEED = "QT_SEED";
  public final static String EXAMPLES = "QT_EXAMPLES";
  public final static String SHRINKS = "QT_SHRINKS";
  public final static String TESTING_TIME = "QT_TESTING_TIME";
  public final static String GENERATE_ATTEMPTS = "QT_ATTEMPTS";

  /**
   * Returns the initial profile to use for a {@link org.quicktheories.QuickTheory} taking into account
   * profiles
   *
   * @return a Strategy that takes into account the active profile and default values
   */
  public static Strategy initialStrategy(Class<?> testClass) {
    // if there is a profile name given, use it to look up a profile strategy and use it if it exists,
    // otherwise use the registered default if it exists, otherwise use the system strategy
    return Optional.ofNullable(System.getProperty(PROFILE))
            .map(p -> Configuration.profileStrategy(testClass, p))
            .orElseGet(() -> Configuration.defaultStrategy(testClass));

  }

  public static Strategy profileStrategy(Class<?> testClass, String profile) {
    // if there is a profile of the given name, use it, otherwise use the registered default if it exists, otherwise
    // use the system strategy
    return Profile.getProfile(testClass, profile).map(f -> f.apply(systemStrategy())).orElseGet(() -> Configuration.defaultStrategy(testClass));
  }

  public static Strategy defaultStrategy(Class<?> testClass) {
    // if there is a default profile registered use it, otherwise use the system strategy
    return Profile.getDefaultProfile(testClass).map(f -> f.apply(systemStrategy())).orElseGet(Configuration::systemStrategy);
  }

  /**
   * Sets the strategy for the corresponding QuickTheory. Default values are set
   * if the user has not overridden them elsewhere.
   * 
   * @return a Strategy
   */
  public static Strategy systemStrategy() {
    return new Strategy(defaultPRNG(pickSeed()), pickExamples(), pickTestingTimeMillis(), pickShrinks(), pickAttempts(),
        new ExceptionReporter(), pickGuidance());
  }

  private static int pickAttempts() {
    Optional<String> userValue = Optional.ofNullable(System.getProperty(GENERATE_ATTEMPTS));
    return userValue.map(Integer::valueOf).orElseGet(() -> DEFAULT_NO_ATTEMPTS);
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
    return userValue.map(Integer::valueOf).orElseGet(() -> DEFAULT_NO_EXAMPLES);
  }

  private static long pickTestingTimeMillis() {
    Optional<String> userValue = Optional
            .ofNullable(System.getProperty(TESTING_TIME));
    return userValue.map(Integer::valueOf).orElseGet(() -> DEFAULT_TESTING_TIME_MILLIS);
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

  public static <T> Class<T> ensureLoaded(Class<T> klass) {
    try {
      Class.forName(klass.getName(), true, klass.getClassLoader());
    } catch (ClassNotFoundException e) {
      throw new AssertionError(e);  // Can't happen
    }
    return klass;
  }
}
