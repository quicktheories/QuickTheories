package org.quicktheories.quicktheories.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Test;
import org.quicktheories.quicktheories.api.Pair;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.Shrink;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.impl.Shrinker.ShrinkResult;

public class ShrinkerTest {

  Shrinker<Integer, Integer> testee;

  Strategy strategy = new Strategy(Configuration.defaultPRNG(0), 10, 10, null);

  @Test
  public void shouldReportOriginalAsSmallestFalsifyingValueWhenNoSmallerValueFound() {
    testee = makeTesteeFor((original, context) -> Stream.empty());
    ShrinkResult<Integer, Integer> actual = testee.shrink(i -> false,
        falsifiedWith(101));
    assertThat(actual.smallest.value()).isEqualTo(101);
  }

  @Test
  public void shouldReportOtherFalsifyingValuesThanFirstFound() {
    testee = makeTesteeFor((o, c) -> Stream.of(o - 1));
    ShrinkResult<Integer, Integer> actual = testee.shrink(i -> false,
        falsifiedWith(5));
    assertThat(actual.otherExamples).contains(4, 3, 2, 1);
  }

  @Test
  public void shouldConstrainShrunkValuesByAssumptions() {
    final int breaksAssumption = 1;
    Predicate<Integer> assumption = i -> i != breaksAssumption;
    testee = makeTesteeFor(
        (original, context) -> Stream.of(breaksAssumption, 2, 3, 4),
        assumption);
    ShrinkResult<Integer, Integer> actual = testee.shrink(i -> false,
        falsifiedWith(5));
    assertThat(actual.otherExamples).doesNotContain(breaksAssumption);
  }

  @Test
  public void shouldUseExactlyRequestedNumberOfShrinkCyclesWhenMoreThanOneIteration() {
    strategy = strategy.withShrinkCycles(9);
    Shrink<Integer> shrink = (original, context) -> Stream.iterate(original - 1,
        i -> i - 1);
    testee = makeTesteeFor(shrink);
    ShrinkResult<Integer, Integer> actual = testee.shrink(i -> i % 2 == 0,
        falsifiedWith(9));
    assertThat(actual.otherExamples).isEqualTo(Arrays.asList(7, 5, 3, 1));
  }

  private Falsification<Integer, Integer> falsifiedWith(int i) {
    return new Falsification<Integer, Integer>(Pair.of(i, i), Optional.empty(),
        0);
  }

  private Shrinker<Integer, Integer> makeTesteeFor(Shrink<Integer> shrink) {
    return makeTesteeFor(shrink, i -> true);
  }

  private Shrinker<Integer, Integer> makeTesteeFor(Shrink<Integer> shrink,
      Predicate<Integer> assumption) {
    return new Shrinker<Integer, Integer>(strategy, shrink, assumption, x -> x);
  }

}