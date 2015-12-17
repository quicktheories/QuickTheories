package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.Reporter;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.impl.TheoryBuilder;

public class IntegerShrinkerImplementationTest extends ComponentTest<Integer> {

  PseudoRandom prng = mock(PseudoRandom.class);
  Reporter reporter = mock(Reporter.class);

  @Test
  public void shouldShrinkByIncrementsOfOneWhenRemainingCyclesIsGreaterThanDistanceToTarget() {
    int examples = 1;
    int shrinkCycles = 1000;
    when(this.prng.generateRandomLongWithinInterval(anyInt(), anyInt()))
        .thenReturn((long) shrinkCycles - 100, 5000L);
    assertThatFor(Integers.range(1, 4000),
        strategy(examples, shrinkCycles)).check(i -> i < 2);
    shrinkIsExpectedValue(listOfShrunkenItems().get(0), shrinkCycles - 101);
    listDecreasesInIncrementsOfOne(listOfShrunkenItems());
  }

  @Test
  public void shouldShrinkByIncrementsOfOneWhenRemainingCyclesIsEqualToDistanceToTarget() {
    int examples = 1;
    int shrinkCycles = 1000;
    when(this.prng.generateRandomLongWithinInterval(anyInt(), anyInt()))
        .thenReturn((long) shrinkCycles, 6660L);
    assertThatFor(Integers.range(-6000, 6000),
        strategy(examples, shrinkCycles)).check(i -> i < 0);
    shrinkIsExpectedValue(listOfShrunkenItems().get(0), shrinkCycles - 1);
    listDecreasesInIncrementsOfOne(listOfShrunkenItems());
  }

  @Test
  public void shouldShrinkByIncrementsOfOneWhenRemainingCyclesIsEqualToDistanceToTargetWhichIsNotEqualToZero() {
    int examples = 1;
    int shrinkCycles = 100;
    when(this.prng.generateRandomLongWithinInterval(anyInt(), anyInt()))
        .thenReturn((long) shrinkCycles, 5000L);
    assertThatFor(Integers.range(20, 120),
        strategy(examples, shrinkCycles)).check(i -> i < 20);
    shrinkIsExpectedValue(listOfShrunkenItems().get(0), shrinkCycles - 1);
    listDecreasesInIncrementsOfOne(listOfShrunkenItems());
  }

  @Test
  public void shouldNotShrinkByIncrementsOfOneInitiallyWhenRemainingCyclesIsLessThanDistanceToTarget() {
    int examples = 1;
    int shrinkCycles = 1000;
    when(this.prng.generateRandomLongWithinInterval(anyInt(), anyInt()))
        .thenReturn((long) shrinkCycles + 1, 500L, 5000L);
    assertThatFor(Integers.range(-4000, 4000),
        strategy(examples, shrinkCycles)).check(i -> i < 0);
    shrinkIsExpectedValue(listOfShrunkenItems().get(0), 500);
    listDecreasesInIncrementsOfOne(listOfShrunkenItems());
  }

  @Test
  public void shouldRandomlyShrinkUntilRemainingAttemptsIsGreaterThanSizeOfRangeToSearch() {
    int examples = 2;
    int shrinkCycles = 1000;
    when(this.prng.generateRandomLongWithinInterval(anyInt(), anyInt()))
        .thenReturn(-4000L, 3200L, 1100L, 900L, 5000L);
    assertThatFor(Integers.range(-4000, 4000),
        strategy(examples, shrinkCycles)).check(i -> i <= 20);
    shrinkIsExpectedValue(listOfShrunkenItems().get(0), 1100);
    shrinkIsExpectedValue(listOfShrunkenItems().get(1), 900);
    listDecreasesInIncrementsOfOne(
        listOfShrunkenItems().subList(2, listOfShrunkenItems().size() - 1));
  }

  @Test
  public void shouldBeAbleToRandomlyShrinkToTheTargetValueInOneStep() {
    int examples = 1;
    int shrinkCycles = 1;
    when(this.prng.generateRandomLongWithinInterval(anyInt(), anyInt()))
        .thenReturn(-6L, -1L, 5000L);
    assertThatFor(Integers.range(-6, -1), strategy(examples, shrinkCycles))
        .check(i -> false);
    shrinkIsExpectedValue(listOfShrunkenItems().get(0), -1);
  }

  private TheoryBuilder<Integer> assertThatFor(
      Source<Integer> generator, Strategy strategy) {
    return theoryBuilder(generator, strategy, this.reporter);
  }

  private Strategy strategy(int examples, int shrinkCycles) {
    return new Strategy(this.prng, examples, shrinkCycles, this.reporter);
  }

  private void listDecreasesInIncrementsOfOne(List<Integer> intList) {
    for (int i = 1; i < intList.size(); i++) {
      assertTrue(
          "Expected " + (intList.get(i - 1)) + " to be one bigger than "
              + (intList.get(i)),
          Math.abs(intList.get(i - 1)) - Math.abs(intList.get(i)) == 1);
    }
  }

  private void shrinkIsExpectedValue(int produced, int expected) {
    assertTrue("Expected to produce " + expected + ", but received " + produced,
        produced == expected);
  }

}