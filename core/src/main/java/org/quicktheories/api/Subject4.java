package org.quicktheories.api;

/**
 * The state for a theory involving four values
 *
 * @param <P>
 *          Type of first value
 * @param <P2>
 *          Type of second value
 * @param <P3>
 *          Type of third value
 * @param <T>
 *          Type of fourth value
 */
public interface Subject4<P, P2, P3, T> {

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  void check(final Predicate4<P, P2, P3, T> property);

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  void checkAssert(final QuadConsumer<P, P2, P3, T> property);

}
