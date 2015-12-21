package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.quicktheories.quicktheories.generators.IntegersTest.headsTowardsLowerAbsoluteValue;
import static org.quicktheories.quicktheories.generators.Lists.arrayListCollector;
import static org.quicktheories.quicktheories.generators.Lists.linkedListCollector;
import static org.quicktheories.quicktheories.generators.Lists.listsOf;
import static org.quicktheories.quicktheories.generators.Lists.alternatingBoundedListsOf;
import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

public class ListsTest {

  @SuppressWarnings("unchecked")
  @Test
  public void shouldGenerateAllPossibleLinkedListsWithinSizeRange() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(1, 1), Lists.linkedListCollector(), 1, 4);
    assertThatSource(testee).generatesAllOf(
        new LinkedList<Integer>(Arrays.asList(1)),
        new LinkedList<Integer>(Arrays.asList(1, 1)),
        new LinkedList<Integer>(Arrays.asList(1, 1, 1)),
        new LinkedList<Integer>(Arrays.asList(1, 1, 1, 1)));
    assertThatSource(testee).doesNotGenerate(
        new LinkedList<Integer>(Arrays.asList(1, 1, 1, 1, 1)),
        new LinkedList<Integer>(Arrays.asList(1, 1, 1, 1, 1, 1)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void shouldGenerateAllPossibleArrayListsWithinSizeRange() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(1, 1), arrayListCollector(), 1, 4);
    assertThatSource(testee).generatesAllOf(Arrays.asList(1),
        Arrays.asList(1, 1), Arrays.asList(1, 1, 1), Arrays.asList(1, 1, 1, 1));
    assertThatSource(testee).doesNotGenerate(Arrays.asList(1, 1, 1, 1, 1),
        Arrays.asList(1, 1, 1, 1, 1, 1));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void shouldGenerateBothTypesOfList() {
    Source<List<Integer>> testee = alternatingBoundedListsOf(
        Integers.range(1, 1), 5, 5);
    assertThatSource(testee).generatesAllOf(Arrays.asList(1, 1, 1, 1, 1),
        new LinkedList<Integer>(Arrays.asList(1, 1, 1, 1, 1)));
  }

  @Test
  public void shouldNotShrinkAnEmptyFixedSizeArrayList() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(1, Integer.MAX_VALUE), arrayListCollector(), 0, 0);
    assertThatSource(testee).cannotShrink(new ArrayList<>());
  }

  @Test
  public void shouldNotShrinkAnEmptyFixedSizeLinkedList() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(1, Integer.MAX_VALUE), linkedListCollector(), 0, 0);
    assertThatSource(testee).cannotShrink(new LinkedList<Integer>());
  }

  @Test
  public void shouldNotShrinkAnEmptyBoundedSizeArrayList() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(1, Integer.MAX_VALUE), arrayListCollector(), 0, 0);
    assertThatSource(testee).cannotShrink(new ArrayList<>());
  }

  @Test
  public void shouldNotShrinkAnEmptyBoundedSizeLinkedList() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(1, Integer.MAX_VALUE), linkedListCollector(), 0, 0);
    assertThatSource(testee).cannotShrink(new LinkedList<Integer>());
  }

  @Test
  public void shouldShrinkAFixedLengthLinkedListToALinkedListOfSameLength() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(-9, 9), linkedListCollector(), 5, 5);
    List<Integer> input = new LinkedList<Integer>(
        Arrays.asList(-6, -3, 0, -3, -5));
    List<Integer> shrunk = testee.shrink(input, someShrinkContext()).iterator()
        .next();
    isExpectedLength(shrunk, 5);
    isLinkedListAfterShrinking(shrunk);
  }

  @Test
  public void shouldShrinkAFixedLengthArrayListToAnArrayListOfSameLength() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(-9, 9), arrayListCollector(), 5, 5);
    List<Integer> input = Arrays.asList(-6, -3, 0, -3, -5);
    List<Integer> shrunk = testee.shrink(input, someShrinkContext()).iterator()
        .next();
    isExpectedLength(shrunk, 5);
    isArrayListAfterShrinking(shrunk);
  }

  @Test
  public void shouldShrinkABoundedArrayListToAnArrayList() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(-9, 9), arrayListCollector(), 5, 10);
    List<Integer> input = Arrays.asList(-6, -3, 0, -3, -5, 2, 2);
    List<Integer> shrunk = testee.shrink(input, someShrinkContext()).iterator()
        .next();
    isExpectedLength(shrunk, input.size() - 1);
    isArrayListAfterShrinking(shrunk);
  }

  @Test
  public void shouldShrinkABoundedLinkedListToALinkedList() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(-9, 9), linkedListCollector(), 5, 10);
    List<Integer> input = new LinkedList<Integer>(
        Arrays.asList(-6, -3, 0, -3, -5, 2, 2));
    List<Integer> shrunk = testee.shrink(input, someShrinkContext()).iterator()
        .next();
    isExpectedLength(shrunk, input.size() - 1);
    isLinkedListAfterShrinking(shrunk);
  }

  @Test
  public void shouldShrinkALinkedListOfGreaterThanMinimumSizeByOneElement() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(-9, 9), linkedListCollector(), 4, 10);
    List<Integer> input = new LinkedList<Integer>(
        Arrays.asList(-6, -3, 0, 2, 3, 4, -3, -5));
    List<Integer> shrunk = testee.shrink(input, someShrinkContext()).iterator()
        .next();
    isExpectedLength(shrunk, input.size() - 1);
  }

  @Test
  public void shouldShrinkPositiveIntegersByOneInAFixedLengthArrayListWhereAllValuesAreWithinRemainingCyclesOfTarget() {
    Source<List<Integer>> testee = listsOf(Integers.range(0, 9),
        arrayListCollector(), 5, 5);
    assertThatSource(testee).shrinksValueTo(Arrays.asList(1, 2, 3, 4, 5),
        Arrays.asList(0, 1, 2, 3, 4), someShrinkContext());
  }

  @Test
  public void shouldShrinkNegativeIntegersByOneInAFixedLengthLinkedListWhereAllValuesAreWithinRemainingCyclesOfTarget() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(-9, 0), linkedListCollector(), 3, 3);
    assertThatSource(testee).shrinksValueTo(
        new LinkedList<Integer>(Arrays.asList(-6, -3, -1)),
        new LinkedList<Integer>(Arrays.asList(-5, -2, 0)), someShrinkContext());
  }

  @Test
  public void shouldShrinkAllIntegersByOneInAFixedLengthArrayListWhereAllValuesAreWithinRemainingCyclesOfTarget() {
    Source<List<Integer>> testee = alternatingBoundedListsOf(
        Integers.range(-9, 9), 3, 3);
    assertThatSource(testee).shrinksValueTo(Arrays.asList(-6, 3, -1),
        Arrays.asList(-5, 2, 0), someShrinkContext());
  }

  @Test
  public void shouldShrinkABoundedLengthLinkedListOfMinimumSizeAsIfAFixedLengthLinkedListOfMinimumSize() {
    Source<List<Integer>> testee = listsOf(Integers.range(-9, 9),
        linkedListCollector(), 4, 10);
    assertThatSource(testee).shrinksValueTo(
        new LinkedList<Integer>(Arrays.asList(-6, 3, 0, -1)),
        new LinkedList<Integer>(Arrays.asList(-5, 2, 0, 0)),
        someShrinkContext());
  }

  @Test
  public void shouldShrinkAFixedLengthListIfOneEntryCanStillBeShrunk() {
    Source<List<Integer>> testee = listsOf(Integers.range(0, 9),
        arrayListCollector(), 5, 5);
    assertThatSource(testee).shrinksValueTo(Arrays.asList(0, 0, 3, 0, 0),
        Arrays.asList(0, 0, 2, 0, 0), someShrinkContext());
  }

  @Test
  public void shouldShrinkFixedLengthNegativelyRangedArrayLists() {
    Source<List<Integer>> testee = alternatingBoundedListsOf(
        Integers.range(-2000, -1000), 5, 5);
    assertThatSource(testee).shrinksConformTo(
        Arrays.asList(-1400, -2000, -1000, -1235, -1052),
        allItemsHeadTowardsLowerAbsoluteValue(-2000, -1000,
            Arrays.asList(-1400, -2000, -1000, -1235, -1052)),
        someShrinkContext());
  }

  @Test
  public void shouldShrinkFixedLengthPositivelyRangedLinkedLists() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(1, Integer.MAX_VALUE), linkedListCollector(), 4, 4);
    assertThatSource(testee).shrinksConformTo(
        new LinkedList<Integer>(Arrays.asList(4342, 5435, 757, 12)),
        allItemsHeadTowardsLowerAbsoluteValue(1, Integer.MAX_VALUE,
            new LinkedList<Integer>(Arrays.asList(4342, 5435, 757, 12))),
        someShrinkContext());
  }

  @Test
  public void shouldShrinkFixedLengthIntegerArrayLists() {
    Source<List<Integer>> testee = listsOf(
        Integers.range(-2000, 2000), arrayListCollector(), 5, 5);
    assertThatSource(testee).shrinksConformTo(
        Arrays.asList(1400, 2000, -1000, -1235, 1052),
        allItemsHeadTowardsLowerAbsoluteValue(-2000, 2000,
            Arrays.asList(1400, 2000, -1000, -1235, 1052)),
        someShrinkContext());
  }

  @Test
  public void shouldShrinkAMultiShrinkListAsAFixedListIfOfMinimumSize() {
    Source<Integer> generator = Integers.range(1, Integer.MAX_VALUE);
    Source<List<Integer>> testee = listsOf(generator, arrayListCollector(),
        3, 7)
            .withShrinker(Lists.swapBetweenShrinkMethodsForBoundedIntegerLists(
                generator, arrayListCollector(), 3));
    assertThatSource(testee).shrinksValueTo(Arrays.asList(15, 19, 1),
        Arrays.asList(14, 18, 1));
  }

  private void isLinkedListAfterShrinking(List<Integer> shrunkOutput) {
    assertTrue(
        "Expected " + shrunkOutput
            + " to be of type LinkedList, but was of type "
            + shrunkOutput.getClass().getSimpleName(),
        shrunkOutput instanceof LinkedList);
  }

  private void isArrayListAfterShrinking(List<Integer> shrunkOutput) {
    assertTrue(
        "Expected " + shrunkOutput
            + " to be of type ArrayList, but was of type "
            + shrunkOutput.getClass().getSimpleName(),
        shrunkOutput instanceof ArrayList);
  }

  private void isExpectedLength(List<Integer> shrunkOutput, int expected) {
    assertTrue(
        "Expected " + shrunkOutput + " to be of length " + expected
            + " rather than " + shrunkOutput.size(),
        shrunkOutput.size() == expected);
  }

  private static Predicate<List<Integer>> allItemsHeadTowardsLowerAbsoluteValue(
      int lowerBound, int higherBound, List<Integer> origin) {
    return list -> {
      for (int i = 0; i < list.size(); i++) {
        if (!headsTowardsLowerAbsoluteValue(lowerBound, higherBound,
            origin.get(i)).test(list.get(i))) {
          return false;
        }
      }
      return true;
    };
  }

  private ShrinkContext someShrinkContext() {
    return new ShrinkContext(0, 10, Configuration.defaultPRNG(0));
  }

}
