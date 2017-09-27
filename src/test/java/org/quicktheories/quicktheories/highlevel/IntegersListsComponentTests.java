package org.quicktheories.quicktheories.highlevel;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.quicktheories.quicktheories.WithQuickTheories;
import org.quicktheories.quicktheories.core.PseudoRandom;

public class IntegersListsComponentTests extends ComponentTest<List<Integer>> implements WithQuickTheories{

  PseudoRandom prng = mock(PseudoRandom.class);

  @Test
  public void shouldReturnALinkedListAfterShrinkageIfOneGenerated() {
    assertThatFor(lists().of(integers().from(-4000).upToAndIncluding(4000)).ofType(lists().linkedList()).ofSize(6))
            .check(i -> false);
    isLinkedListAfterShrinking();
  }

  @Test
  public void shouldReturnAnArrayListAfterShrinkageIfOneGenerated() {
    assertThatFor(lists().of(integers().from(-4000).upToAndIncluding(4000)).ofType(lists().arrayList()).ofSize(6))
            .check(i -> false);
    isArrayListAfterShrinking();
  }

  @Test
  public void shouldFindAFixedLengthListEqualToTargetWithItemsWhoseDomainIsAcrossZeroMarker() {
    assertThatFor(
        lists().of(integers().from(-4000).upToAndIncluding(4000)).ofSize(6))
            .check(i -> false);
    smallestValueIsEqualTo(Arrays.asList(0, 0, 0, 0, 0, 0));
  }

  @Test
  public void shouldFindABoundedListEqualToTargetWithItemsWhoseDomainIsAcrossZeroMarker() {
    assertThatFor(
        lists().of(integers().from(-4000).upToAndIncluding(4000)).ofSizeBetween(2,6))
            .check(i -> false);
    smallestValueIsEqualTo(Arrays.asList(0, 0));
  }

  @Test
  public void shouldFindAFixedLengthListEqualToTargetWithItemsWhoseDomainIsWithinThePositiveIntegers() {
    assertThatFor(
        lists().of(integers().from(1).upToAndIncluding(Integer.MAX_VALUE)).ofSize(5))
            .check(i -> false);
    smallestValueIsEqualTo(Arrays.asList(1, 1, 1, 1, 1));
  }

  @Test
  public void shouldFindAFixedLengthListEqualToTargetWithItemsWhoseDomainHasUpperBoundZero() {
    assertThatFor(lists().of(integers().from(-4000).upToAndIncluding(0)).ofType(lists().linkedList()).ofSize(7))
            .check(i -> false);
    smallestValueIsEqualTo(
        new LinkedList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0)));
  }

  @Test
  public void shouldFindAFixedLengthListEqualToTargetWithItemsWhoseDomainHasLowerBoundZero() {
    assertThatFor(lists().of(integers().from(0).upToAndIncluding(6000)).ofType(lists().linkedList()).ofSize(7))
            .check(i -> false);
    smallestValueIsEqualTo(
        new LinkedList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0)));
  }
  @Test
  public void shouldReturnAnEmptyListIfMinimumIsSetToZero() {
    assertThatFor(
        lists().of(integers().from(-40000).upToAndIncluding(40000)).ofSizeBetween(0, 100))
            .check(i -> false);
    smallestValueIsEqualTo(Arrays.asList());
  }


  private void isLinkedListAfterShrinking() {
    assertTrue(
        "Expected " + smallestValueFound()
            + " to be of type LinkedList, but was of type "
            + smallestValueFound().getClass().getSimpleName(),
        smallestValueFound() instanceof LinkedList);
  }

  private void isArrayListAfterShrinking() {
    assertTrue(
        "Expected " + smallestValueFound()
            + " to be of type ArrayList, but was of type "
            + smallestValueFound().getClass().getSimpleName(),
        smallestValueFound() instanceof ArrayList);
  }

  private void smallestValueIsEqualTo(List<Integer> target) {
    for (int i = 0; i < target.size(); i++) {
      assertTrue(
          "Expected " + smallestValueFound() + " to be equal to " + target,
          (target.get(i) - smallestValueFound().get(i) == 0));
    }
  }

}
