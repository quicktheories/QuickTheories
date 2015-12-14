package org.quicktheories.quicktheories.api;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * State for a theory involving two values
 *
 * @param
 *          <P>
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
  public void check(final BiPredicate<P, T> property);

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public void checkAssert(final BiConsumer<P, T> property);

  /**
   * Specifies how the objects of type P and T will be output as Strings by the
   * ExceptionReporter
   * 
   * @param pToString
   *          function to transform the value of type P to a String
   * @param tToString
   *          function to transform the value of type T to a String
   * @return a Subject2
   */
  public Subject2<P, T> describedAs(Function<P, String> pToString,
      Function<T, String> tToString);

}
