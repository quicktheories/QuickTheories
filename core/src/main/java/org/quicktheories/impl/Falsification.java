package org.quicktheories.impl;

import java.util.Optional;

final class Falsification<T> {

  private final Optional<Throwable> cause;
  private final T value;

  Falsification(T value, Optional<Throwable> cause) {
    this.value = value;
    this.cause = cause;
  }

  static <T> Falsification<T> fromException(Throwable t, T value) {
    return new Falsification<>(value, Optional.of(t));
  }

  static <T> Falsification<T> fromProperty(T value) {
    return new Falsification<>(value, Optional.empty());
  }

  T value() {
    return this.value;
  }

  Optional<Throwable> cause() {
    return cause;
  }


}
