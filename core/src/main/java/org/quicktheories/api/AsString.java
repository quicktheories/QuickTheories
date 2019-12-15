package org.quicktheories.api;

@FunctionalInterface
public interface AsString<T> {

  String asString(T t);
  
}
