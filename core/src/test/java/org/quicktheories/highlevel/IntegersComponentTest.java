package org.quicktheories.highlevel;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Configuration;
import org.quicktheories.core.NoGuidance;
import org.quicktheories.core.Strategy;

public class IntegersComponentTest extends ComponentTest<Integer> implements WithQuickTheories {

  @Test
  public void shouldFindAValueTowardsTargetWithDomainAcrossZeroMarker() {
    assertThatFor(integers().from(-4000).upToAndIncluding(4000))
        .check(i -> i <= -3900);
    smallestValueFoundGreaterThan(-3900);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithDomainAcrossZeroMarker() {
    assertThatFor(integers().from(-4000).upToAndIncluding(4000))
        .check(i -> i <= -3900);
    smallestValueIsEqualTo(0);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithDomainWithUpperBoundZero() {
    assertThatFor(integers().from(-4000).upToAndIncluding(0))
        .check(i -> i <= -3900);
    smallestValueIsEqualTo(0);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithDomainWithLowerBoundZero() {
    assertThatFor(integers().from(-4000).upToAndIncluding(4001))
        .check(i -> i >= 3090);
    smallestValueIsEqualTo(0);
  }

  @Test
  public void shouldFindAValueEqualToTargetDomainWithinThePositiveIntegers() {
    assertThatFor(integers().from(1).upToAndIncluding(Integer.MAX_VALUE))
        .check(i -> i >= 4000780);
    smallestValueIsEqualTo(1);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithBigDomainAcrossZeroMarker() {
    assertThatFor(integers().from(-1000000000).upToAndIncluding(1000000000))
        .check(i -> i <= -3900);
    smallestValueIsEqualTo(0);
  }

  @Test
  public void shouldFindAValueTowardsTargetWithDomainBelowZeroMarker() {
    assertThatFor(integers().from(-6000).upToAndIncluding(-2001))
        .check(i -> i <= -3000);
    smallestValueFoundIsInRange(-2999, -2001);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithDomainBelowZeroMarker() {
    int target = -1;
    assertThatFor(integers().from(-6).upToAndIncluding(-1),
        new Strategy(Configuration.defaultPRNG(0), 1, 0, 2, 1, 10, 1, this.reporter, prng -> new NoGuidance()))
            .check(i -> i > target);
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldFindAValueTowardsTargetWithDomainAboveZeroMarker() {
    assertThatFor(integers().from(4000).upToAndIncluding(8000))
        .check(i -> i <= 5000);
    smallestValueFoundIsInRange(5001, 8000);
  }

  @Test
  public void shouldFindAValueEqualToTargetWithDomainAboveZeroMarker() {
    int target = 5001;
    assertThatFor(integers().from(4000).upToAndIncluding(8000))
        .check(i -> i < target);
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldCreateAListWhoseElementsAllPassTheInitialAssumptionsPredicateTest() {
    assertThatFor(integers().from(-40000).upToAndIncluding(40000))
        .assuming(i -> i % 2 != 0).check(i -> false);
    for (int i : listOfShrunkenItems()) {
      assertTrue(i % 2 != 0);
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
