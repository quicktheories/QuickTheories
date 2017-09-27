package org.quicktheories.api;

/**
 * Represents an operation that accepts five input arguments and returns no
 * result.
 * 
 * @param <A>
 *          the type of the first argument to the operation
 * @param <B>
 *          the type of the second argument to the operation
 * @param <C>
 *          the type of the third argument to the operation
 * @param <D>
 *          the type of the fourth argument to the operation
 * @param <E>
 *          the type of the fifth argument to the operation
 */
@FunctionalInterface
public interface Consumer5<A, B, C, D, E> {

  /**
   * Performs this operation on the given arguments.
   *
   * @param a
   *          the first input argument
   * @param b
   *          the second input argument
   * @param c
   *          the third input argument
   * @param d
   *          the fourth input argument
   * @param e
   *          the fifth input argument
   */
  void accept(A a, B b, C c, D d, E e);

}