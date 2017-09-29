package org.quicktheories.api;

import java.util.Objects;

@FunctionalInterface
public interface Predicate4<A, B, C, D> {

  boolean test(A a, B b, C c, D d);

  default Predicate4<A, B, C, D> and(
      Predicate4<? super A, ? super B, ? super C, ? super D> other) {
    Objects.requireNonNull(other);
    return (a, b, c, d) -> test(a, b, c, d) && other.test(a, b, c, d);
  }

}
