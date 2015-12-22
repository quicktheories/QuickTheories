package org.quicktheories.quicktheories.core;

import java.util.stream.Stream;

import javax.annotation.CheckReturnValue;

@FunctionalInterface
@CheckReturnValue
public interface Shrink<T> {

  /**
   * Simplifies the value T in a manner specified
   * 
   * @param original
   *          value T that we are looking to 'shrink'/simplify
   * @param context
   *          ShrinkContext which contains details such as the number of shrink
   *          cycles, step and PseudoRandom to be used
   * @return a Stream of T
   */
  Stream<T> shrink(T original, ShrinkContext context);

}
