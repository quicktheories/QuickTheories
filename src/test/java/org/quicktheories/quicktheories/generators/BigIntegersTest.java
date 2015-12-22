package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import java.math.BigInteger;
import java.util.function.Predicate;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.generators.BigIntegersDSL.BigIntegers;

public class BigIntegersTest extends ComponentTest<BigInteger> {

  @Test
  public void shouldShrinkZeroToItself() {
    Source<BigInteger> testee = BigIntegers.random(5);
    assertThatSource(testee).shrinksValueTo(new BigInteger("0"),
        new BigInteger("0"));
  }

  @Test
  public void shouldShrinkNegativeIntegerTowardsZero() {
    Source<BigInteger> testee = BigIntegers.random(4);
    BigInteger original = new BigInteger("-896876976");
    assertThatSource(testee).shrinksConformTo(original,
        smallerAbsoluteValueThan(original), someShrinkContext());
  }

  @Test
  public void willShrinkANegativeIntegerPastZeroButToASmallerAbsoluteValue() {
    Source<BigInteger> testee = BigIntegers.random(4);
    BigInteger original = new BigInteger("-8968766");
    assertThatSource(testee).shrinksConformTo(original,
        smallerAbsoluteValueThan(original), someShrinkContext());
  }

  @Test
  public void shouldShrinkPositiveIntegerToASmallerAbsoluteValue() {
    Source<BigInteger> testee = BigIntegers.random(7);
    BigInteger original = new BigInteger("89687098066");
    assertThatSource(testee).shrinksConformTo(original,
        smallerAbsoluteValueThan(original), someShrinkContext());
  }

  @Test
  public void shouldShrinkAlway() {
    assertThatFor(BigIntegers.random(32)).check(i -> false);
    listIsInDecreasingAbsValueOrder();
    smallestValueIsEqualTo(new BigInteger("0"));
  }

  @Test
  public void shouldProvideAtLeastFiveOtherShrunkenValues() {
    assertThatFor(BigIntegers.random(14))
        .check(b -> b.compareTo(new BigInteger("0")) != -1);
    atLeastFiveDistinctFalsifyingValuesAreFound();
  }

  @Test
  public void shouldShrinkToTargetValueOfOne() {
    assertThatFor(BigIntegers.random(14))
        .check(b -> b.compareTo(new BigInteger("0")) != 1);
    atLeastFiveDistinctFalsifyingValuesAreFound();
    smallestValueIsEqualTo(new BigInteger("1"));
  }

  @Test
  public void shouldShrinkPositiveIntegerWithZeroAsFirstItemInByteArray() {
    Source<BigInteger> testee = BigIntegers.random(7);
    BigInteger original = new BigInteger("769874764676");
    assertThatSource(testee).shrinksConformTo(original,
        smallerAbsoluteValueThan(original), someShrinkContext());
  }

  private ShrinkContext someShrinkContext() {
    return new ShrinkContext(0, 1, Configuration.defaultPRNG(2));
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

  private Predicate<BigInteger> smallerAbsoluteValueThan(BigInteger original) {
    return i -> original.abs().compareTo(i.abs()) != -1;
  }

  private void smallestValueIsEqualTo(BigInteger target) {
    assertTrue("Expected " + smallestValueFound() + " to be equal to " + target,
        (target.compareTo(smallestValueFound()) == 0));
  }

}
