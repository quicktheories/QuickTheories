package org.quicktheories.quicktheories.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

import org.quicktheories.quicktheories.api.Pair;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Strategy;

final class Shrinker<P, T> {

  private final Strategy strategy;
  private final Shrink<P> precursorShrinkSource;
  private final Predicate<P> assumptions;
  private final Function<P, T> precursorToValue;

  Shrinker(final Strategy state, final Shrink<P> shrinkSource,
      Predicate<P> assumptions, Function<P, T> f) {
    this.strategy = state;
    this.precursorShrinkSource = shrinkSource;
    this.assumptions = assumptions;
    this.precursorToValue = f;
  }

  ShrinkResult<P, T> shrink(final Predicate<T> property,
      final Falsification<P, T> smallestKnown) {
    FalsificationFunction<P, T> falisfy = new FalsificationFunction<>(property);

    List<T> smaller = new ArrayList<T>();

    int attempts = 0;
    Falsification<P, T> last = smallestKnown;
    // Progressively shrink, stopping each time a falsifying value is found.
    // Stopping allows re-calculation of value ranges. This may not be
    // significant for deterministic shrink functions, but may increase the
    // efficiency of random ones
    while (attempts < strategy.shrinkCycles()) {
      int remainingDomain = strategy.shrinkCycles() - attempts;
      ShrinkContext context = new ShrinkContext(smallestKnown.step(),
          remainingDomain, this.strategy.prng());
      final AtomicInteger chunkCount = new AtomicInteger();
      Optional<Falsification<P, T>> maybeSmaller = this.precursorShrinkSource
          .shrink(last.precursor(), context)
          .limit(remainingDomain)
          .peek(t -> chunkCount.incrementAndGet())
          .filter(assumptions)
          .map(p -> new IndexedItem<>(smallestKnown.step(),
              Pair.of(p, precursorToValue.apply(p))))
          .flatMap(falisfy)
          .findFirst();

      if (maybeSmaller.isPresent()) {
        last = maybeSmaller.get();
        smaller.add(last.value());
      } else if (chunkCount.get() < remainingDomain) {
        break;
      }

      attempts = attempts + chunkCount.get();
    }

    return new ShrinkResult<>(smaller, last);

  }

  static class ShrinkResult<P, T> {
    final List<T> otherExamples;
    final Falsification<P, T> smallest;

    public ShrinkResult(List<T> otherExamples, Falsification<P, T> smallest) {
      this.otherExamples = otherExamples;
      this.smallest = smallest;
    }

  }

}
