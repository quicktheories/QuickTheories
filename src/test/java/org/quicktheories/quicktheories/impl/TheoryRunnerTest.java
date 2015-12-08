package org.quicktheories.quicktheories.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.quicktheories.quicktheories.generators.SourceDSL.arbitrary;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.Reporter;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;

@RunWith(MockitoJUnitRunner.class)
public class TheoryRunnerTest {

  TheoryRunner<Integer, Integer> testee;

  Strategy strategy;

  @Mock
  Reporter reporter;

  @Before
  public void setup() {
    strategy = new Strategy(Configuration.defaultPRNG(0), 10, 10, reporter);
  }

  @Test
  public void shouldNotFalisifyTheTruth() {
    testee = makeTesteeFor(arbitrary().sequence(1, 2, 3, 4, 5));
    testee.check(i -> true);
    verify(reporter, never()).falisification(anyLong(), anyInt(),
        any(Object.class), anySmallerValues(), anyObject());
  }

  @Test
  public void shouldFalisifyUniversalFalsehood() {
    testee = makeTesteeFor(arbitrary().sequence(1, 2, 3, 4, 5));
    testee.check(i -> false);
    verify(reporter, times(1)).falisification(anyLong(), anyInt(),
        any(Object.class), anySmallerValues(), anyObject());
  }

  @Test
  public void shouldFalsifyPartialTruth() {
    testee = makeTesteeFor(arbitrary().sequence(1, 2, 3, 4, 5));
    testee.check(i -> i > 3);
    verify(reporter, times(1)).falisification(anyLong(), anyInt(),
        any(Object.class), anySmallerValues(), anyObject());
  }

  @Test
  public void shouldFalsifyWhenPredicateThrowsException() {
    testee = makeTesteeFor(arbitrary().sequence(1, 2, 3, 4, 5));
    testee.check(i -> {
      if (i > 3) {
        throw new AssertionError();
      }
      return true;
    });
    verify(reporter, times(1)).falisification(anyLong(), anyInt(),
        any(Object.class), any(Throwable.class), anySmallerValues(), anyObject());
  }

  @Test
  public void shouldReportSmallestFalsifyingValueFound() {
    testee = makeTesteeFor(arbitrary().sequence(1, 2, 3, 4, 5));
    testee.check(i -> i <= 3);
    verify(reporter, times(1)).falisification(anyLong(), anyInt(), eq(4),
        anySmallerValues(), anyObject());
  }

  @Test
  public void shouldConstrainGeneratorsByAssumptions() {
    testee = makeTesteeFor(arbitrary().sequence(1, 2, 3, 4, 5),
        i -> i != 4);
    testee.check(i -> i <= 3);
    verify(reporter, times(1)).falisification(anyLong(), anyInt(), eq(5),
        anySmallerValues(), anyObject());
  }

  @Test
  public void shouldReportSmallerFalsifyingValuesThanFirstFound() {
    testee = makeTesteeFor(
        arbitrary().reverse(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    testee.check(i -> i > 7);
    verify(reporter, times(1)).falisification(anyLong(), anyInt(), anyInt(),
        eq(Arrays.asList(6, 5, 4, 3, 2, 1)), anyObject());
  }

  @Test
  public void shouldConstrainShrunkValuesByAssumptions() {
    Predicate<Integer> assumption = i -> i != 1;
    testee = makeTesteeFor(arbitrary().reverse(1, 2, 3, 4), assumption);
    testee.check(i -> i > 3);

    verify(reporter, times(1)).falisification(anyLong(), anyInt(), anyInt(),
        eq(Arrays.asList(2)), anyObject());
  }

  @Test
  public void shouldReportInitialSeed() {
    long seed = 42;
    strategy = new Strategy(Configuration.defaultPRNG(seed), 10, 10, reporter);
    testee = makeTesteeFor(arbitrary().sequence(1));
    testee.check(i -> false);
    verify(reporter, times(1)).falisification(eq(seed), anyInt(), anyInt(),
        anySmallerValues(), anyObject());
  }

  @Test
  public void shouldReportWhenValuesExhausted() {
    testee = makeTesteeFor(arbitrary().sequence(1), i -> false);
    testee.check(i -> true);
    verify(reporter, times(1)).valuesExhausted(anyInt());
  }

  @Test
  public void shouldReportNumberOfFoundExamplesWhenValuesExhausted() {
    int numberOfExamples = 3;
    strategy = new Strategy(Configuration.defaultPRNG(0), numberOfExamples, 0,
        reporter);
    // will make 30 attempts, only 2 of which will pass the i == 2 assumption
    testee = makeTesteeFor(
        arbitrary().sequence(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
        i -> i == 2);
    testee.check(i -> true);
    verify(reporter, times(1)).valuesExhausted(2);
  }

  @Test
  public void shouldReportNumberOfExamplesUsed() {
    testee = makeTesteeFor(
        arbitrary().sequence(0, 1, 2, 3, 4, 5));
    testee.check(i -> i != 4);
    verify(reporter, times(1)).falisification(anyLong(), eq(5), anyInt(),
        anySmallerValues(), anyObject());

  }

  private TheoryRunner<Integer, Integer> makeTesteeFor(
      Source<Integer> generator) {
    return makeTesteeFor(generator, i -> true);
  }

  private TheoryRunner<Integer, Integer> makeTesteeFor(
      Source<Integer> generator,
      Predicate<Integer> assumption) {
    return new TheoryRunner<Integer, Integer>(strategy, generator, assumption,
        x -> x, a-> a.toString());
  }

  private List<Object> anySmallerValues() {
    return anyListOf(Object.class);
  }

}
