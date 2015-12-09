package org.quicktheories.quicktheories.api;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The state for a theory involving one value
 *
 * @param <T>
 *          Type of the value
 */
public interface Subject1<T> {

  /**
   * Checks a boolean property across a random sample of possible values
   * 
   * @param property
   *          property to check
   */
  public void check(final Predicate<T> property);

  /**
   * Checks a property across a random sample of possible values where
   * falsification is indicated by an unchecked exception such as an assertion
   * 
   * @param property
   *          property to check
   */
  public void checkAssert(final Consumer<T> property);

  /**
   * Specifies how the object of type T will be output as a String by the
   * ExceptionReporter
   * 
   * @param tToString
   *          function to transform the value of type T to a String
   * @return a Subject1 of type T
   */
  public Subject1<T> describedAs(Function<T, String> tToString);

}
