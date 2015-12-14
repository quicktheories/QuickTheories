package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.Reporter;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.impl.TheoryBuilder;

public class LongsComponentTest extends ComponentTest<Long> {

  Reporter reporter = mock(Reporter.class);
  Strategy strategy = new Strategy(Configuration.defaultPRNG(2), 1000, 1000,
      this.reporter);

  @Test
  public void shouldFindFalsifyingValueOutsideOfIntegerRange() {
    assertThatFor(Longs.range(Long.MIN_VALUE, Long.MAX_VALUE),
        withShrinkCycles(100000))
            .check(i -> i <= Integer.MAX_VALUE);
    listIsInDecreasingAbsValueOrder();
    smallestValueFoundIsInRange((long) Integer.MAX_VALUE + 1, Long.MAX_VALUE);
  }

  @Test
  public void shouldFindFalsifyingValuesFromOutsideAndInsideIntegerRange() {
    assertThatFor(
        Longs.range((long) Integer.MIN_VALUE - 50, -5000000),
        withShrinkCycles(10000)).check(i -> i >= -5000000);
    listIsInDecreasingAbsValueOrder();
    smallestValueIsEqualTo(-5000001);
  }

  @Test
  public void shouldShrinkByOneInPositiveNumbersWhereDistanceIsLessThanRemainingCycles() {
    assertThatFor(Longs.range(532, 3532), withShrinkCycles(3000))
        .check(i -> false);
    listIsDecreasingByAtMostOne();
    smallestValueIsEqualTo(532L);
  }

  @Test
  public void shouldShrinkByOneAcrossNaturalNumbersWhereDistanceIsLessThanRemainingCycles() {
    assertThatFor(Longs.range(0, 50), withShrinkCycles(50)).check(i -> false);
    listIsDecreasingByAtMostOne();
    smallestValueIsEqualTo(0);
  }

  @Test
  public void shouldShrinkUpwardsAcrossNaturalNumbersWhereDistanceIsLessThanRemainingCycles() {
    assertThatFor(
        Longs.range(0, 50).withShrinker(Longs.shrinkTowardsTarget(50)),
        withShrinkCycles(50)).check(i -> false);
    smallestValueIsEqualTo(50);
  }

  @Test
  public void shouldShrinkDownwardsToTargetWhereIntervalIsAcrossZeroAndDistanceIsLessThanRemainingCycles() {
    assertThatFor(
        Longs.range(-100, 1000).withShrinker(Longs.shrinkTowardsTarget(-80)),
        withShrinkCycles(2000)).check(i -> false);
    smallestValueIsEqualTo(-80);
  }

  @Test
  public void shouldNotShrinkWhereTargetEqualsOriginal() {
    assertThatFor(Longs.range(0, 0), withShrinkCycles(1)).check(i -> false);
    assertTrue(
        "Expected list " + listOfShrunkenItems()
            + " to contain no shrunken values",
        listOfShrunkenItems().isEmpty());
    smallestValueIsEqualTo(0);
  }

  @Test
  public void shouldShrinkCodePointToMinimumCodePointByOneInIntervalWhereDistanceToTargetIsLessThanRemainingCycles() {
    int target = 0x0021;
    assertThatFor(Longs.codePoints(0x0020, 0x007E), withShrinkCycles(18))
        .check(i -> false);
    listIsDecreasingByAtMostOne(); // Falsifies with this number of shrink
                                   // cycles and this seed
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldShrinkCodePointToMinimumCodePointInIntervalWhereDistanceToTargetIsInitiallyGreaterThanRemainingCycles() {
    int target = 0x0021;
    assertThatFor(Longs.codePoints(0x0020, 0x007E), withShrinkCycles(6))
        .check(i -> false);
    listIsInDecreasingAbsValueOrder();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldShrinkUpwardsFindingAtLeastFiveDistinctFalsifyingValuesAndTheTarget() {
    long target = 22999992l;
    assertThatFor(Longs.range(13523522, 23523522).withShrinker(
        Longs.shrinkTowardsTarget(23000000)), withShrinkCycles(1000000))
            .check(i -> i % 10 != 2);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  private TheoryBuilder<Long, Long> assertThatFor(
      Source<Long> generator, Strategy strategy) {
    return theoryBuilder(generator, strategy, this.reporter);
  }

  private Strategy withShrinkCycles(int shrinkCycles) {
    return new Strategy(Configuration.defaultPRNG(2), 10000, shrinkCycles,
        this.reporter).withExamples(100000);
  }

  private void listIsInDecreasingAbsValueOrder() {
    for (int i = 1; i < listOfShrunkenItems().size(); i++) {
      assertTrue(
          "Expected " + (listOfShrunkenItems().get(i - 1))
              + " to be bigger than " + (listOfShrunkenItems().get(i)),
          Math.abs(listOfShrunkenItems().get(i - 1)) >= Math
              .abs(listOfShrunkenItems().get(i)));
    }
  }

  private void listIsDecreasingByAtMostOne() {
    for (int i = 1; i < listOfShrunkenItems().size(); i++) {
      assertTrue(
          "Expected " + (listOfShrunkenItems().get(i - 1))
              + " to be at most one bigger than "
              + (listOfShrunkenItems().get(i)),
          Math.abs(listOfShrunkenItems().get(i - 1)
              - listOfShrunkenItems().get(i)) <= 1);
    }
  }

  private void smallestValueIsEqualTo(long target) {
    assertTrue("Expected " + smallestValueFound() + " to be equal to " + target,
        (target - smallestValueFound() == 0));
  }

  private void smallestValueFoundIsInRange(long lower, long upper) {
    assertTrue(
        "Expected to be within [" + lower + "," + upper + "], but received "
            + smallestValueFound(),
        smallestValueFound() >= lower && smallestValueFound() <= upper);
  }

}
