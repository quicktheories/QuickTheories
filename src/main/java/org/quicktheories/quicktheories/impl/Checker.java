package org.quicktheories.quicktheories.impl;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.quicktheories.quicktheories.api.Pair;
import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.core.Source;

final class Checker<P, T> {

  private static final int ATTEMPT_RATIO = 10;

  private final Strategy strategy;
  private final Source<P> precursorSource;
  private final Predicate<P> assumptions;
  private final Function<P, T> precursorToValue;

  Checker(final Strategy state, final Source<P> source,
      Predicate<P> assumptions, Function<P, T> f) {
    this.strategy = state;
    this.precursorSource = source;
    this.assumptions = assumptions;
    this.precursorToValue = f;
  }

  public CheckerResults<P, T> check(final Predicate<T> property) {

    FalsificationFunction<P, T> falisfy = new FalsificationFunction<>(property);

    final PseudoRandom prng = this.strategy.prng();

    Stream<IndexedItem<Pair<P, T>>> values = IntStream.iterate(0, i -> i + 1)
        .mapToObj(step -> new IndexedItem<>(step,
            precursorSource.next(prng, step)))
        .map(item -> new IndexedItem<>(item.step,
            Pair.of(item.value, precursorToValue.apply(item.value))));

    final int maxAttempts = this.strategy.examples() * ATTEMPT_RATIO;

    AtomicInteger executedExamples = new AtomicInteger();

    Optional<Falsification<P, T>> firstFalsifyingValue = values
        .limit(maxAttempts)
        .filter(item -> assumptions.test(item.value._1))
        .limit(this.strategy.examples())
        .peek(v -> executedExamples.incrementAndGet())
        .flatMap(falisfy)
        .findFirst();

    return new CheckerResults<>(firstFalsifyingValue, executedExamples.get());

  }

  static class CheckerResults<P, T> {
    final Optional<Falsification<P, T>> falsification;
    final int executedExamples;

    public CheckerResults(Optional<Falsification<P, T>> falsification,
        int executedExamples) {
      this.falsification = falsification;
      this.executedExamples = executedExamples;
    }

    public boolean wasFalsified() {
      return falsification.isPresent();
    }
  }

}
