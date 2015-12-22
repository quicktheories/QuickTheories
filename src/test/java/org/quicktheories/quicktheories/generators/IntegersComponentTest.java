package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.Strategy;

public class IntegersComponentTest extends ComponentTest<Integer> {

  @Test
  public void shouldFindAValueTowardsTargetWithDomainAcrossZeroMarker() {
    assertThatFor(Integers.range(-4000, 4000))
        .check(i -> i <= -3900);
    smallestValueFoundGreaterThan(-3900);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithDomainAcrossZeroMarker() {
    assertThatFor(Integers.range(-4000, 4000))
        .check(i -> i <= -3900);
    smallestValueIsEqualTo(0);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithDomainWithUpperBoundZero() {
    assertThatFor(Integers.range(-4000, 0))
        .check(i -> i <= -3900);
    smallestValueIsEqualTo(0);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithDomainWithLowerBoundZero() {
    assertThatFor(Integers.range(0, 4001))
        .check(i -> i >= 3090);
    smallestValueIsEqualTo(0);
  }

  @Test
  public void shouldFindAValueEqualToTargetDomainWithinThePositiveIntegers() {
    assertThatFor(Integers.range(1, Integer.MAX_VALUE))
        .check(i -> i >= 4000780);
    smallestValueIsEqualTo(1);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithBigDomainAcrossZeroMarker() {
    assertThatFor(Integers.range(-1000000000, 1000000000))
        .check(i -> i <= -3900);
    smallestValueIsEqualTo(0);
  }

  @Test
  public void shouldFindAValueTowardsTargetWithDomainBelowZeroMarker() {
    assertThatFor(Integers.range(-6000, -2001))
        .check(i -> i <= -3000);
    smallestValueFoundIsInRange(-2999, -2001);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithDomainBelowZeroMarker() {
    int target = -1;
    assertThatFor(Integers.range(-6, -1),
        new Strategy(Configuration.defaultPRNG(0), 1, 2, this.reporter))
            .check(i -> i > target);
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldFindAValueTowardsTargetWithDomainAboveZeroMarker() {
    assertThatFor(Integers.range(4000, 8000))
        .check(i -> i <= 5000);
    smallestValueFoundIsInRange(5001, 8000);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithDomainAboveZeroMarker() {
    int target = 5001;
    assertThatFor(Integers.range(4000, 8000))
        .check(i -> i < target);
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldCreateAListOfIntegersOfDecreasingAbsValueOrderWithDomainAcrossZeroMarker() {
    assertThatFor(Integers.range(-4000, 4000))
        .check(i -> false);
    listIsInDecreasingAbsValueOrder();
  }

  @Test
  public void shouldCreateAListOfIntegersOfDecreasingAbsValueOrderWithDomainBelowZeroMarker() {
    assertThatFor(Integers.range(-4000, -1000))
        .check(i -> false);
    listIsInDecreasingAbsValueOrder();
  }

  @Test
  public void shouldCreateAListOfIntegersOfDecreasingAbsValueOrderWithDomainAboveZeroMarker() {
    assertThatFor(Integers.range(50, 8940)).check(i -> false);
    listIsInDecreasingAbsValueOrder();
  }

  @Test
  public void shouldCreateAListWhoseElementsAllPassTheInitialAssumptionsPredicateTest() {
    assertThatFor(Integers.range(-40000, 40000))
        .assuming(i -> i % 2 != 0).check(i -> false);
    for (int i : listOfShrunkenItems()) {
      assertTrue(i % 2 != 0);
    }
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithDomainAboveZeroMarker() {
    int target = 200;
    assertThatFor(Integers.range(100, 500),
        withShrinkCycles(150)).check(i -> i < target);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithDomainAcrossZeroMarker() {
    int target = -1;
    assertThatFor(Integers.range(-23532, 74745),
        defaultStrategy.withFixedSeed(0)).check(i -> i % 2 == 0);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target); // Could be +1 with other seeds
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithDomainBelowZeroMarker() {
    int target = -4745;
    assertThatFor(Integers.range(-23532, -4745),
        withShrinkCycles(150)).check(i -> i >= 0);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithDomainTheMaximumRangeSizeBelowZeroMarker() {
    int target = -1;
    assertThatFor(Integers.range(-Integer.MAX_VALUE, -1),
        withShrinkCycles(150)).check(i -> i >= 0);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithDomainTheMaximumRangeSizeBelowZeroMarkerIncludingMinValue() {
    int target = -2;
    assertThatFor(Integers.range(Integer.MIN_VALUE, -2),
        withShrinkCycles(150)).check(i -> i >= 0);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithDomainInThePositiveIntegers() {
    int target = 1;
    assertThatFor(Integers.range(1, Integer.MAX_VALUE), withShrinkCycles(150))
        .check(i -> i % 2 == 0);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithNegativeDomainSizeEqualToMinInteger() {
    int target = 0;
    assertThatFor(Integers.range(Integer.MIN_VALUE, 0),
        defaultStrategy.withExamples(1000000)).check(i -> Math.abs(i) >= 6000);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithPositiveDomainSizeEqualToMaxInteger() {
    int target = 0;
    assertThatFor(Integers.range(0, Integer.MAX_VALUE),
        defaultStrategy.withExamples(1000000)).check(i -> Math.abs(i) >= 6000);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesWithDomainSizeMuchLargerToMaxInteger() {
    int target = 0;
    assertThatFor(Integers.range(-Integer.MAX_VALUE / 2, Integer.MAX_VALUE),
       defaultStrategy.withExamples(1000000))
            .check(i -> Math.abs(i) >= 6000);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldProvideTheTargetValueWithDomainTheIntegers() {
    int target = 0;
    assertThatFor(Integers.range(Integer.MIN_VALUE, Integer.MAX_VALUE),
        withShrinkCycles(1000))
            .check(i -> i % 2 == 1);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
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

  private void smallestValueFoundIsInRange(int lower, int upper) {
    assertTrue(
        "Expected to be within [" + lower + "," + upper + "], but received "
            + smallestValueFound(),
        smallestValueFound() >= lower && smallestValueFound() <= upper);
  }

  private void smallestValueFoundGreaterThan(int lower) {
    assertTrue("Expected to be greater than " + lower + ", but received "
        + smallestValueFound(), smallestValueFound() > lower);
  }

  private void smallestValueIsEqualTo(int target) {
    assertTrue("Expected " + smallestValueFound() + " to be equal to " + target,
        (target - smallestValueFound() == 0));
  }

}
