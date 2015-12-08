package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import java.math.BigDecimal;
import java.util.function.Predicate;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.Reporter;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.generators.BigDecimalsDSL.BigDecimals;
import org.quicktheories.quicktheories.impl.TheoryBuilder;

public class BigDecimalsTest extends ComponentTest<BigDecimal> {

  Reporter reporter = mock(Reporter.class);
  Strategy strategy = new Strategy(Configuration.defaultPRNG(2), 1000, 10000,
      this.reporter);

  @Test
  public void shouldShrinkZeroToItself() {
    Source<BigDecimal> testee = BigDecimals.randomWithScale(14, 5);
    assertThatSource(testee).shrinksValueTo(new BigDecimal("0"),
        new BigDecimal("0.00000"));
  }

  @Test
  public void shouldShrinkNegativeIntegerTowardsZero() {
    Source<BigDecimal> testee = BigDecimals.randomWithScale(12, 4);
    BigDecimal original = new BigDecimal("-89687.6976");
    assertThatSource(testee).shrinksConformTo(original,
        smallerAbsoluteValueThan(original), someShrinkContext());
  }

  @Test
  public void shouldShrinkNegativelyScaledDecimalTowardsZero() {
    Source<BigDecimal> testee = BigDecimals.randomWithScale(12, -4);
    BigDecimal original = new BigDecimal("-8.96876976E+12");
    assertThatSource(testee).shrinksConformTo(original,
        smallerAbsoluteValueThan(original), someShrinkContext());
  }

  @Test
  public void shouldShrinkPositiveIntegerTowardsZero() {
    Source<BigDecimal> testee = BigDecimals.randomWithScale(12, 2);
    BigDecimal original = new BigDecimal("7698747646.76");
    assertThatSource(testee).shrinksConformTo(original,
        smallerAbsoluteValueThan(original), someShrinkContext());
  }

  @Test
  public void shouldShrinkAlways() {
    assertThatFor(BigDecimals.randomWithScale(14, 3)).check(i -> false);
    listIsInDecreasingAbsValueOrder();
    smallestValueIsEqualTo(new BigDecimal("0.000"));
  }

  @Test
  public void shouldProvideAtLeastTwoOtherShrunkenValues() {
    assertThatFor(BigDecimals.randomWithScale(14, 4))
        .check(b -> b.intValue() % 2 == 0);
    listIsInDecreasingAbsValueOrder();
    atLeastNDistinctFalsifyingValuesAreFound(2);
  }

  @Test
  public void shouldShrinkToTargetValueOfOne() {
    assertThatFor(BigDecimals.randomWithScale(22, 4))
        .check(b -> b.compareTo(new BigDecimal("0.0000")) != 1);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(new BigDecimal("0.0001"));
  }

  @Test
  public void shouldShrinkWithScaleSetToZero() {
    assertThatFor(BigDecimals.randomWithScale(22, 0))
        .check(b -> b.compareTo(new BigDecimal("0")) != 1);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(new BigDecimal("1"));
  }

  private ShrinkContext someShrinkContext() {
    return new ShrinkContext(0, 1, Configuration.defaultPRNG(2));
  }

  private TheoryBuilder<BigDecimal, BigDecimal> assertThatFor(
      Source<BigDecimal> generator) {
    return theoryBuilder(generator, this.strategy, this.reporter);
  }

  private void listIsInDecreasingAbsValueOrder() {
    for (int i = 1; i < listOfShrunkenItems().size(); i++) {
      assertTrue(
          "Expected " + (listOfShrunkenItems().get(i - 1))
              + " to be bigger than " + (listOfShrunkenItems().get(i)),
          listOfShrunkenItems().get(i - 1).abs()
              .compareTo(listOfShrunkenItems().get(i).abs()) != -1);
    }
  }

  private Predicate<BigDecimal> smallerAbsoluteValueThan(BigDecimal original) {
    return i -> original.abs().compareTo(i.abs()) != -1;
  }

  private void smallestValueIsEqualTo(BigDecimal target) {
    assertTrue("Expected " + smallestValueFound() + " to be equal to " + target,
        (target.compareTo(smallestValueFound()) == 0));
  }

}
