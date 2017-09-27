package org.quicktheories.quicktheories.api;

@FunctionalInterface
public interface AsString<T> {

  public String asString(T t);
  
}
