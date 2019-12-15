package org.quicktheories.api;

/**
 * State for a theory involving three values
 *
 * @param
 *          <P>
 *          Type of first value
 * @param <P2>
 *          Type of second value
 * @param <T>
 *          Type of third value
 */
public interface Subject3<P, P2, T> {

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  void check(final Predicate3<P, P2, T> property);

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  void checkAssert(final TriConsumer<P, P2, T> property);

}
