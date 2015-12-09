package org.quicktheories.quicktheories.api;

import java.util.function.Function;

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
  public void check(final Predicate3<P, P2, T> property);

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public void checkAssert(final TriConsumer<P, P2, T> property);

  /**
   * Specifies how the objects of type P, P2 and T will be output as Strings by
   * the ExceptionReporter
   * 
   * @param pToString
   *          function to transform the value of type P to a String
   * @param p2ToString
   *          function to transform the value of type P2 to a String
   * @param tToString
   *          function to transform the value of type T to a String
   * @return a Subject3
   */
  public Subject3<P, P2, T> withStringFormat(Function<P, String> pToString,
      Function<P2, String> p2ToString,
      Function<T, String> tToString);

}
