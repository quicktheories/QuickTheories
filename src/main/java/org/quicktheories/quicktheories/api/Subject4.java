package org.quicktheories.quicktheories.api;

import java.util.function.Function;

/**
 * The state for a theory involving four values
 *
 * @param
 *          <P>
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
  public void check(final Predicate4<P, P2, P3, T> property);

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public void checkAssert(final QuadConsumer<P, P2, P3, T> property);

  /**
   * Specifies how the objects of type P, P2, P3 and T will be output as Strings
   * by the ExceptionReporter
   * 
   * @param pToString
   *          function to transform the value of type P to a String
   * @param p2ToString
   *          function to transform the value of type P2 to a String
   * @param p3ToString
   *          function to transform the value of type P3 to a String
   * @param tToString
   *          function to transform the value of type T to a String
   * @return a Subject4
   */
  public Subject4<P, P2, P3, T> describedAs(Function<P, String> pToString,
      Function<P2, String> p2ToString, Function<P3, String> p3ToString,
      Function<T, String> tToString);

}
