package org.quicktheories.quicktheories.api;

@FunctionalInterface
public interface Function3<A, B, C, T> {

  T apply(A a, B b, C c);

}
