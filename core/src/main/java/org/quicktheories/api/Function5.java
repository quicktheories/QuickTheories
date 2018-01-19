package org.quicktheories.api;

@FunctionalInterface
public interface Function5<A, B, C, D, E, T> {

  T apply(A a, B b, C c, D d, E e);

}
