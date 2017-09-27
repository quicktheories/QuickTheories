package org.quicktheories.api;

import java.util.Objects;

@FunctionalInterface
public interface Predicate5<A, B, C, D, E> {

  boolean test(A a, B b, C c, D d, E e);

  default Predicate5<A, B, C, D, E> and(
      Predicate5<? super A, ? super B, ? super C, ? super D, ? super E> other) {
    Objects.requireNonNull(other);
    return (a, b, c, d, e) -> test(a, b, c, d, e) && other.test(a, b, c, d, e);
  }

}