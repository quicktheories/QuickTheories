package org.quicktheories.quicktheories.api;

@FunctionalInterface
public interface Function4<A, B, C, D, T> {

  T apply(A a, B b, C c, D d);

}
