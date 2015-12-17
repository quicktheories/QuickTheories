package org.quicktheories.quicktheories.api;

import java.util.function.Function;

@FunctionalInterface
public interface AsString<T> {

  public String asString(T t);
  
  default Function<T,String> asToStringFunction() {
    return t -> asString(t);
  }
}
