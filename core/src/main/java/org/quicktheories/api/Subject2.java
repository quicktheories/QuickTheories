package org.quicktheories.api;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * State for a theory involving two values
 *
 * @param <P>
 *          Type of first value
 * @param <T>
 *          Type of second value
 */
public interface Subject2<P, T> {

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  void check(final BiPredicate<P, T> property);

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  void checkAssert(final BiConsumer<P, T> property);


}
