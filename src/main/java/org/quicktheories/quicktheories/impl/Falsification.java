package org.quicktheories.quicktheories.impl;

import java.util.Optional;

import org.quicktheories.quicktheories.api.Pair;

final class Falsification<P, T> {

  private final Optional<Throwable> cause;
  private final Pair<P, T> value;
  private final int step;

  Falsification(Pair<P, T> value, Optional<Throwable> cause, int step) {
    this.value = value;
    this.cause = cause;
    this.step = step;
  }

  static <P, T> Falsification<P, T> fromException(Throwable t, Pair<P, T> value,
      int step) {
    return new Falsification<>(value, Optional.of(t), step);
  }

  static <P, T> Falsification<P, T> fromProperty(Pair<P, T> value, int step) {
    return new Falsification<>(value, Optional.empty(), step);
  }

  P precursor() {
    return this.value._1;
  }

  T value() {
    return this.value._2;
  }

  Optional<Throwable> cause() {
    return cause;
  }

  int step() {
    return step;
  }

}
