package org.quicktheories.highlevel;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;

public class LongsComponentTest extends ComponentTest<Long> implements WithQuickTheories {

  @Test
  public void shouldFindFalsifyingValueOutsideOfIntegerRange() {
    assertThatFor(longs().between(Long.MIN_VALUE, Long.MAX_VALUE),
        withShrinkCycles(100000))
            .check(i -> i <= Integer.MAX_VALUE);
    smallestValueFoundIsInRange((long) Integer.MAX_VALUE + 1, Long.MAX_VALUE);
  }

  @Test
  public void shouldFindFalsifyingValuesFromOutsideAndInsideIntegerRange() {
    assertThatFor(longs().between((long) Integer.MIN_VALUE - 50, -5000000),
        withShrinkCycles(10000)).check(i -> i >= -5000000);
    smallestValueIsEqualTo(-5000001);
  }

  @Test
  public void shouldShrinkByOneInPositiveNumbersWhereDistanceIsLessThanRemainingCycles() {
    assertThatFor(longs().between(532l, 3532l), withShrinkCycles(3000))
        .check(i -> false);
    listIsDecreasingByAtMostOne();
    smallestValueIsEqualTo(532L);
  }

  @Test
  public void shouldShrinkByOneAcrossNaturalNumbersWhereDistanceIsLessThanRemainingCycles() {
    assertThatFor(longs().between(0l, 50l), withShrinkCycles(50)).check(i -> false);
    listIsDecreasingByAtMostOne();
    smallestValueIsEqualTo(0);
  }


  @Test
  public void shouldNotShrinkWhereTargetEqualsOriginal() {
    assertThatFor(longs().between(0l, 0l), withShrinkCycles(1)).check(i -> false);
    assertTrue(
        "Expected list " + listOfShrunkenItems()
            + " to contain no shrunken values",
        listOfShrunkenItems().isEmpty());
    smallestValueIsEqualTo(0);
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
