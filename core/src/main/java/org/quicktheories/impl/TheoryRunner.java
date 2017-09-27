package org.quicktheories.impl;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.quicktheories.api.AsString;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;

public final class TheoryRunner<P, T> {

  private final Strategy       strategy;
  private final Gen<P>         precursorSource;
  private final Function<P, T> precursorToValue;
  private final AsString<T>    toString;

  public TheoryRunner(final Strategy state, final Gen<P> source, Function<P, T> f,
      AsString<T> toString) {
    this.strategy = state;
    this.precursorSource = source;
    this.precursorToValue = f;
    this.toString = toString;
  }

  public static <T> TheoryRunner<T, T> runner(final Strategy state,
      final Gen<T> source) {
    return new TheoryRunner<>(state, source, t -> t, source);
  }

  public void check(final Predicate<T> property) {
    final Core core = new Core(this.strategy);
    final Property<T> prop = new Property<>(property,
        this.precursorSource.map(this.precursorToValue));
    final SearchResult<T> results = core.run(prop);
    if (results.isFalsified()) {
      reportFalsification(results);
    } else if (results.wasExhausted()) {
      this.strategy.reporter().valuesExhausted(results.getExecutedExamples());
    }

  }

  @SuppressWarnings("unchecked")
  private void reportFalsification(SearchResult<T> result) {
    final long seed = this.strategy.prng().getInitialSeed();
    if (result.getSmallestThrowable().isPresent()) {
      this.strategy.reporter().falisification(seed,
          result.getExecutedExamples(), result.smallest(),
          result.getSmallestThrowable().get(),
          (List<Object>) result.getFalsifictions(),
          (AsString<Object>) this.toString);
    } else {
      this.strategy.reporter().falisification(seed,
          result.getExecutedExamples(), result.smallest(),
          (List<Object>) result.getFalsifictions(),
          (AsString<Object>) this.toString);
    }

  }
}
