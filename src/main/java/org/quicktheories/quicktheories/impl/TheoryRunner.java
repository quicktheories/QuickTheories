package org.quicktheories.quicktheories.impl;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.quicktheories.quicktheories.api.AsString;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.impl.Checker.CheckerResults;
import org.quicktheories.quicktheories.impl.Shrinker.ShrinkResult;

final class TheoryRunner<P, T> {

  private final Strategy strategy;
  private final Source<P> precursorSource;
  private final Predicate<P> assumptions;
  private final Function<P, T> precursorToValue;
  private final AsString<T> toString;

  TheoryRunner(final Strategy state, final Source<P> source,
      Predicate<P> assumptions, Function<P, T> f,
      AsString<T> toString) {
    this.strategy = state;
    this.precursorSource = source;
    this.assumptions = assumptions;
    this.precursorToValue = f;
    this.toString = toString;
  }

  void check(final Predicate<T> property) {
    Checker<P, T> checker = new Checker<P, T>(strategy, precursorSource,
        assumptions, precursorToValue);
    CheckerResults<P, T> result = checker.check(property);

    if (result.wasFalsified()) {
      reportResult(property, result);
    } else if (result.executedExamples != strategy.examples()) {
      this.strategy.reporter().valuesExhausted(result.executedExamples);
    }

  }

  @SuppressWarnings("unchecked")
  private void reportResult(final Predicate<T> property,
      CheckerResults<P, T> result) {
    long seed = strategy.prng().getInitialSeed();
    Shrinker<P, T> shrinker = new Shrinker<P, T>(strategy, precursorSource,
        assumptions, precursorToValue);
    ShrinkResult<P, T> shrinkResult = shrinker.shrink(property,
        result.falsification.get());
    T smallest = shrinkResult.smallest.value();
    if (shrinkResult.smallest.cause().isPresent()) {
      this.strategy.reporter().falisification(seed, result.executedExamples,
          smallest, shrinkResult.smallest.cause().get(),
          (List<Object>) shrinkResult.otherExamples,
          (AsString<Object>) toString);
    } else {
      this.strategy.reporter().falisification(seed, result.executedExamples,
          smallest,
          (List<Object>) shrinkResult.otherExamples,
          (AsString<Object>) toString);
    }
  }
}
