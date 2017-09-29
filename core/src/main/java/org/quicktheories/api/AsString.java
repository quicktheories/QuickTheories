package org.quicktheories.api;

@FunctionalInterface
public interface AsString<T> {

  public String asString(T t);
  
}
