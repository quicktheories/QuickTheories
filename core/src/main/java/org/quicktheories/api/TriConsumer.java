package org.quicktheories.api;

/**
 * Represents an operation that accepts three input arguments and returns no
 * result.
 * 
 * @param <A>
 *          the type of the first argument to the operation
 * @param <B>
 *          the type of the second argument to the operation
 * @param <C>
 *          the type of the second argument to the operation
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {

  /**
   * Performs this operation on the given arguments.
   *
   * @param a
   *          the first input argument
   * @param b
   *          the second input argument
   * @param c
   *          the third input argument
   */
  void accept(A a, B b, C c);

}