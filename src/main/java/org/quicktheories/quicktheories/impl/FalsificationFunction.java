package org.quicktheories.quicktheories.impl;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.quicktheories.quicktheories.api.Pair;

final class FalsificationFunction<P, T>
    implements Function<IndexedItem<Pair<P, T>>, Stream<Falsification<P, T>>> {
  private final Predicate<T> property;

  FalsificationFunction(Predicate<T> property) {
    this.property = property;
  }

  @Override
  public Stream<Falsification<P, T>> apply(IndexedItem<Pair<P, T>> item) {
    try {
      if (!property.test(item.value._2)) {
        return Stream.of(Falsification.fromProperty(item.value, item.step));
      }
      return Stream.empty();

    } catch (Throwable ex) {
      return Stream.of(Falsification.fromException(ex, item.value, item.step));
    }
  }
};
