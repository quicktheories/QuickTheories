package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.quicktheories.quicktheories.generators.Lists.alternatingBoundedListsOf;
import static org.quicktheories.quicktheories.generators.Lists.alternatingFixedListsOf;
import static org.quicktheories.quicktheories.generators.Lists.arrayListCollector;
import static org.quicktheories.quicktheories.generators.Lists.linkedListCollector;
import static org.quicktheories.quicktheories.generators.Lists.listsOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.Reporter;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.impl.TheoryBuilder;

public class IntegersListsComponentTests extends ComponentTest<List<Integer>> {

  Reporter reporter = mock(Reporter.class);
  PseudoRandom prng = mock(PseudoRandom.class);
  Strategy strategy = new Strategy(Configuration.defaultPRNG(5), 1000, 10000,
      this.reporter);

  @Test
  public void shouldReturnALinkedListAfterShrinkageIfOneGenerated() {
    assertThatFor(
        listsOf(Integers.range(-4000, 4000), linkedListCollector(), 6))
            .check(i -> false);
    isLinkedListAfterShrinking();
  }

  @Test
  public void shouldReturnAnArrayListAfterShrinkageIfOneGenerated() {
    assertThatFor(
        alternatingFixedListsOf(Integers.range(-4000, 4000), 6))
            .check(i -> false);
    isArrayListAfterShrinking();
  }

  @Test
  public void shouldFindAFixedLengthListEqualToTargetWithItemsWhoseDomainIsAcrossZeroMarker() {
    assertThatFor(
        listsOf(Integers.range(-4000, 4000), linkedListCollector(), 6))
            .check(i -> false);
    smallestValueIsEqualTo(
        new LinkedList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0)));
  }

  @Test
  public void shouldFindABoundedListEqualToTargetWithItemsWhoseDomainIsAcrossZeroMarker() {
    assertThatFor(
        listsOf(Integers.range(-4000, 4000), linkedListCollector(), 2, 6))
            .check(i -> false);
    smallestValueIsEqualTo(new LinkedList<Integer>(Arrays.asList(0, 0)));
  }

  @Test
  public void shouldFindAFixedLengthListEqualToTargetWithItemsWhoseDomainIsWithinThePositiveIntegers() {
    assertThatFor(
        alternatingFixedListsOf(Integers.range(1, Integer.MAX_VALUE), 5))
            .check(i -> false);
    smallestValueIsEqualTo(Arrays.asList(1, 1, 1, 1, 1));
  }

  @Test
  public void shouldFindAFixedLengthListEqualToTargetWithItemsWhoseDomainHasUpperBoundZero() {
    assertThatFor(listsOf(Integers.range(-4000, 0), linkedListCollector(), 7))
        .check(i -> false);
    smallestValueIsEqualTo(
        new LinkedList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0)));
  }

  @Test
  public void shouldFindAFixedLengthListEqualToTargetWithItemsWhoseDomainHasLowerBoundZero() {
    assertThatFor(
        listsOf(Integers.range(0, 6000), linkedListCollector(), 7))
            .check(i -> false);
    smallestValueIsEqualTo(
        new LinkedList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0)));
  }

  @Test
  public void shouldFindAFixedLengthListEqualToTargetWithItemsWhoseDomainIsBelowZeroMarker() {
    assertThatFor(
        alternatingFixedListsOf(Integers.range(-6000, -2001), 5))
            .check(i -> false);
    smallestValueIsEqualTo(Arrays.asList(-2001, -2001, -2001, -2001, -2001));
  }

  @Test
  public void shouldFindABoundedListEqualToTargetWithItemsWhoseDomainIsBelowZeroMarker() {
    assertThatFor(
        listsOf(Integers.range(-6000, -2001), arrayListCollector(), 5, 6))
            .check(i -> false);
    smallestValueIsEqualTo(Arrays.asList(-2001, -2001, -2001, -2001, -2001));
  }

  @Test
  public void shouldCreateAListOfFixedLengthIntegerArrayListsWithItemsOfDecreasingAbsValueOrderWhoseDomainIsAboveZeroMarker() {
    assertThatFor(listsOf(Integers.range(50, 8940), arrayListCollector(), 6))
        .check(list -> false);
    listIsInDecreasingAbsValueOrder();
  }

  @Test
  public void shouldCreateAListOfFixedLengthIntegerArrayListsWithItemsOfDecreasingAbsValueOrderWhoseDomainIsBelowZeroMarker() {
    assertThatFor(
        listsOf(Integers.range(-4000, -1000), arrayListCollector(), 6))
            .check(list -> false);
    listIsInDecreasingAbsValueOrder();
  }

  @Test
  public void shouldCreateAListOfFixedLengthIntegerLinkedListsWithItemsOfDecreasingAbsValueOrderWhoseDomainIsAcrossZeroMarker() {
    assertThatFor(
        listsOf(Integers.range(-40000, 40000), linkedListCollector(), 6))
            .check(list -> false);
    listIsInDecreasingAbsValueOrder();
  }

  @Test
  public void shouldReturnAnEmptyListIfMinimumIsSetToZero() {
    assertThatFor(
        listsOf(Integers.range(-40000, 40000), linkedListCollector(), 0, 6))
            .check(i -> false);
    smallestValueIsEqualTo(new LinkedList<Integer>(Arrays.asList()));
  }

  @Test
  public void shoulCreateAListWhoseElementsAllPassTheInitialAssumptionsPredicateTest() {
    Predicate<List<Integer>> sumGreaterThanOrEqualTo50 = list -> list.stream()
        .mapToInt(Number::intValue).sum() >= 50;
    assertThatFor(
        listsOf(Integers.range(-40000, 40000), linkedListCollector(), 6))
            .assuming(sumGreaterThanOrEqualTo50)
            .check(list -> false);
    for (List<Integer> list : listOfShrunkenItems()) {
      assertTrue(list.stream().mapToInt(Number::intValue).sum() >= 50);
    }
  }

  @Test
  public void willGetStuckAtLocalMinimaWhenOneValueInListShrinksToBottomWithoutFalsifiying() {
    Predicate<List<Integer>> allEntriesOdd = list -> list.stream()
        .filter(v -> v % 2 == 0).findFirst().isPresent();
    assertThatFor(listsOf(Integers.range(-2333, 232), linkedListCollector(), 5),
        withShrinkCycles(3000))
            .check(allEntriesOdd);
    atLeastFiveDistinctFalsifyingValuesAreFound();
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesForAFixedListWithDomainBelowZeroMarker() {
    Predicate<List<Integer>> sumLessThanMinus5000 = list -> list.stream()
        .mapToInt(Number::intValue).sum() <= -500000;
    List<Integer> target = Arrays.asList(-3523, -3523, -3523, -3523, -3523,
        -3523, -3523);
    assertThatFor(
        listsOf(Integers.range(-23435, -3523), arrayListCollector(), 7),
        withShrinkCycles(3000)).check(sumLessThanMinus5000);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(target);
  }

  @Test
  public void shouldProvideAtLeastFiveDistinctFalsifyingValuesForABoundedListOfPositiveIntegers() {
    Predicate<List<Integer>> sumSmallerThanOrEqualTo400000 = list -> list
        .stream().mapToInt(Number::intValue).sum() < 400000;
    assertThatFor(
        listsOf(Integers.range(1, Integer.MAX_VALUE), arrayListCollector(), 3,
            45),
        withShrinkCycles(3000))
            .check(sumSmallerThanOrEqualTo400000);
    atLeastNDistinctFalsifyingValuesAreFound(4);
  }

  @Test
  public void willStopShrinkingBoundedListWhenWeTryToRemoveReasonForFalsificationUntilLimitIsReached() {
    Predicate<List<Integer>> noZeroesInList = list -> list.stream()
        .allMatch(v -> v > 0);
    assertThatFor(listsOf(Integers.range(0, 511), arrayListCollector(), 1, 21),
        withShrinkCycles(2000)).check(noZeroesInList);
    atLeastFiveDistinctFalsifyingValuesAreFound();
  }

  @Test
  public void willNotShrinkAsFixedListWhenMinimumSizeIsNonFalsifying() {
    Predicate<List<Integer>> evenNumberOfItems = list -> list.size() % 2 == 0;
    assertThatFor(listsOf(Integers.range(0, 511), linkedListCollector(), 0, 18),
        withShrinkCycles(1000)).check(evenNumberOfItems);
    smallestValueIsSizeEqualTo(1);
  }

  @Test
  public void willNotShrinkToTargetUsingMultiShrinkerWhenMinimumSizeIsNonFalsifying() {
    Predicate<List<Integer>> evenNumberOfItems = list -> list.size() % 3 == 0;
    assertThatFor(listsOf(Integers.range(0, 511), linkedListCollector(), 0, 18)
        .withShrinker(Lists.swapBetweenShrinkMethodsForBoundedIntegerLists(
            Integers.range(0, 511), linkedListCollector(), 0)),
        withShrinkCycles(10000)).check(evenNumberOfItems);
    smallestValueIsSizeEqualTo(1);
  }

  @Test
  public void canShrinkToTargetUsingMultiShrinker() {
    Predicate<List<Integer>> evenNumberOfItems = list -> list.size() % 3 == 0;
    assertThatFor(listsOf(Integers.range(0, 511), linkedListCollector(), 1, 18)
        .withShrinker(Lists.swapBetweenShrinkMethodsForBoundedIntegerLists(
            Integers.range(0, 511), linkedListCollector(), 1)),
        withShrinkCycles(10000)).check(evenNumberOfItems);
    smallestValueIsSizeEqualTo(1);
    smallestValueIsEqualTo(new LinkedList<>(Arrays.asList(0)));
  }

  @Test
  public void shouldShrinkAsFixedListWhenReachesMinimumSize() {
    Predicate<List<Integer>> evenNumberOfItems = list -> list.size() % 2 == 1;
    assertThatFor(listsOf(Integers.range(0, 511), linkedListCollector(), 2, 18),
        withShrinkCycles(1000)).check(evenNumberOfItems);
    smallestValueIsEqualTo(new LinkedList<Integer>(Arrays.asList(0, 0)));
  }

  @Test
  public void shouldShrinkInOnlyOneTypeOfList() {
    assertThatFor(
        alternatingBoundedListsOf(Integers.range(1, Integer.MAX_VALUE), 2, 6))
            .check(list -> list instanceof ArrayList);
    isLinkedListAfterShrinking();
    smallestValueIsEqualTo(new LinkedList<Integer>(Arrays.asList(1, 1)));
  }

  private TheoryBuilder<List<Integer>, List<Integer>> assertThatFor(
      Source<List<Integer>> generator) {
    return theoryBuilder(generator, this.strategy, this.reporter);
  }

  private TheoryBuilder<List<Integer>, List<Integer>> assertThatFor(
      Source<List<Integer>> generator, Strategy strategy) {
    return theoryBuilder(generator, strategy, this.reporter);
  }

  private Strategy withShrinkCycles(int shrinkCycles) {
    return new Strategy(Configuration.defaultPRNG(0), 100, shrinkCycles,
        this.reporter);
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

  private void listIsInDecreasingAbsValueOrder() {
    for (int i = 1; i < listOfShrunkenItems().size(); i++) {
      for (int j = 0; j < listOfShrunkenItems().get(0).size(); j++) {
        assertTrue(
            "Expected " + (listOfShrunkenItems().get(i - 1))
                + " to be bigger than " + (listOfShrunkenItems().get(i)),
            Math.abs(listOfShrunkenItems().get(i - 1).get(j)) >= Math
                .abs(listOfShrunkenItems().get(i).get(j)));
      }
    }
  }

  private void smallestValueIsEqualTo(List<Integer> target) {
    for (int i = 0; i < target.size(); i++) {
      assertTrue(
          "Expected " + smallestValueFound() + " to be equal to " + target,
          (target.get(i) - smallestValueFound().get(i) == 0));
    }
  }

  private void smallestValueIsSizeEqualTo(int size) {
    assertTrue("Expected " + smallestValueFound() + " to be a list with " + size
        + " elements", smallestValueFound().size() == size);
  }

}
