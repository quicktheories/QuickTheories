package org.quicktheories.quicktheories.api;

import java.util.function.Function;

/**
 * The state for a theory involving five values
 *
 * @param
 *          <P>
 *          Type of first value
 * @param <P2>
 *          Type of second value
 * @param <P3>
 *          Type of third value
 * @param <P4>
 *          Type of fourth value
 * @param <T>
 *          Type of fifth value
 */
public interface Subject5<P, P2, P3, P4, T> {

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  public void check(final Predicate5<P, P2, P3, P4, T> property);

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public void checkAssert(final Consumer5<P, P2, P3, P4, T> property);

  /**
   * Specifies how the objects of type P, P2, P3, P4 and T will be output as
   * Strings by the ExceptionReporter
   * 
   * @param pToString
   *          function to transform the value of type P to a String
   * @param p2ToString
   *          function to transform the value of type P2 to a String
   * @param p3ToString
   *          function to transform the value of type P3 to a String
   * @param p4ToString
   *          function to transform the value of type P4 to a String
   * @param tToString
   *          function to transform the value of type T to a String
   * @return a Subject5
   */
  public Subject5<P, P2, P3, P4, T> withStringFormat(
      Function<P, String> pToString,
      Function<P2, String> p2ToString, Function<P3, String> p3ToString,
      Function<P4, String> p4ToString,
      Function<T, String> tToString);
}
