package org.quicktheories.quicktheories.api;

import java.util.Objects;

@FunctionalInterface
public interface Predicate3<A, B, C> {

  boolean test(A a, B b, C c);

  default Predicate3<A, B, C> and(
      Predicate3<? super A, ? super B, ? super C> other) {
    Objects.requireNonNull(other);
    return (a, b, c) -> test(a, b, c) && other.test(a, b, c);
  }

}
