package org.quicktheories.quicktheories.generators;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import java.util.function.Predicate;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

public class IntegersTest {

  PseudoRandom prng = mock(PseudoRandom.class);

  @Test
  public void shouldGenerateAllIntegersInRangeUpToMax() {
    Source<Integer> testee = Integers.range(Integer.MAX_VALUE - 2,
        Integer.MAX_VALUE);
    assertThatSource(testee).generatesAllOf(Integer.MAX_VALUE - 1,
        Integer.MAX_VALUE - 2, Integer.MAX_VALUE);
  }

  @Test
  public void shouldGenerateAllIntegersInRangeDownToMin() {
    Source<Integer> testee = Integers.range(Integer.MIN_VALUE,
        Integer.MIN_VALUE + 2);
    assertThatSource(testee).generatesAllOf(Integer.MIN_VALUE,
        Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 2);
  }

  @Test
  public void shouldGenerateAllIntegersInRange() {
    Source<Integer> testee = Integers.range(1, 4);
    assertThatSource(testee).generatesAllOf(1, 2, 3);
  }

  @Test
  public void shouldNotGenerateValueOutsideOfRange() {
    Source<Integer> testee = Integers.range(1, Integer.MAX_VALUE);
    assertThatSource(testee).doesNotGenerate(0);
  }

  @Test
  public void shouldNotShrinkTargetValueInPositiveIntegers() {
    Source<Integer> testee = Integers.range(1, Integer.MAX_VALUE);
    assertThatSource(testee).cannotShrink(1);
  }

  @Test
  public void shouldNotShrinkZero() {
    Source<Integer> testee = Integers.range(0, 0);
    assertThatSource(testee).cannotShrink(0);
  }

  @Test
  public void shouldNotShrinkTargetValueInNegativeIntegers() {
    Source<Integer> testee = Integers.range(-1000, -1);
    assertThatSource(testee).cannotShrink(-1);
  }

  @Test
  public void shouldShrinkMaximumIntegerTowardsZeroInIntegerDomain() {
    Predicate<Integer> headsTowardsZero = i -> i <= Integer.MAX_VALUE && 0 <= i;
    Source<Integer> testee = Integers.range(Integer.MIN_VALUE,
        Integer.MAX_VALUE);
    assertThatSource(testee).shrinksConformTo(Integer.MAX_VALUE,
        headsTowardsZero, someShrinkContext());
  }

  @Test
  public void shouldShrinkMaximumIntegerTowardsZeroInPositiveIntegers() {
    Predicate<Integer> headsTowardsOne = i -> i <= Integer.MAX_VALUE && 1 <= i;
    Source<Integer> testee = Integers.range(1, Integer.MAX_VALUE);
    assertThatSource(testee).shrinksConformTo(Integer.MAX_VALUE,
        headsTowardsOne, someShrinkContext());
  }

  @Test
  public void shouldShrinkMinimumIntegerTowardsZeroInIntegerDomain() {
    Predicate<Integer> headsTowardsZero = i -> i >= Integer.MIN_VALUE && i <= 0;
    Source<Integer> testee = Integers.range(Integer.MIN_VALUE,
        Integer.MAX_VALUE);
    assertThatSource(testee).shrinksConformTo(Integer.MIN_VALUE,
        headsTowardsZero, someShrinkContext());
  }

  @Test
  public void shouldShrinkMinimumIntegerTowardsNegativeTwoInThisRange() {
    Predicate<Integer> headsTowardsNegativeTwo = i -> i >= Integer.MIN_VALUE
        && i <= -2;
    Source<Integer> testee = Integers.range(Integer.MIN_VALUE, -2);
    assertThatSource(testee).shrinksConformTo(Integer.MIN_VALUE,
        headsTowardsNegativeTwo, someShrinkContext());
  }

  @Test
  public void shouldShrinkTwoIntegersLessThanIntegerMaxDistanceApart() {
    Predicate<Integer> headsTowardsZero = i -> i >= -Integer.MAX_VALUE + 100
        && i <= 99;
    Source<Integer> testee = Integers.range(-Integer.MAX_VALUE + 100, 99);
    assertThatSource(testee).shrinksConformTo(-Integer.MAX_VALUE + 100,
        headsTowardsZero, someShrinkContext());
  }

  @Test
  public void shouldShrinkNegativeNumbersTowardsZero() {
    Source<Integer> testee = Integers.range(-1000, 1000);
    assertThatSource(testee).shrinksConformTo(-800,
        headsTowardsLowerAbsoluteValue(-1000, 1000, -800), someShrinkContext());
  }

  @Test
  public void shouldShrinkPositiveNumbersTowardsZero() {
    Source<Integer> testee = Integers.range(-1000, 000);
    assertThatSource(testee).shrinksConformTo(800,
        headsTowardsLowerAbsoluteValue(-1000, 1000, 800), someShrinkContext());
  }

  @Test
  public void shouldShrinkNegativeRangeTowardsBiggestNegativeNumber() {
    Source<Integer> testee = Integers.range(-2000, -1000);
    assertThatSource(testee).shrinksConformTo(-1400,
        headsTowardsLowerAbsoluteValue(-2000, -1000, -1400),
        someShrinkContext());
  }

  @Test
  public void shouldShrinkPositiveRangeTowardsSmallestPositiveNumber() {
    Source<Integer> testee = Integers.range(1000, 2000);
    assertThatSource(testee).shrinksConformTo(1909,
        headsTowardsLowerAbsoluteValue(1000, 2000, 1909), someShrinkContext());
  }

  @Test
  public void shouldShrinkZeroLowerBoundedRangeTowardsZero() {
    Source<Integer> testee = Integers.range(0, 1000);
    assertThatSource(testee).shrinksConformTo(200,
        headsTowardsLowerAbsoluteValue(0, 1000, 200), someShrinkContext());
  }

  @Test
  public void shouldShrinkZeroUpperBoundedRangeTowardsZero() {
    Source<Integer> testee = Integers.range(-1000, 0);
    assertThatSource(testee).shrinksConformTo(-505,
        headsTowardsLowerAbsoluteValue(-1000, 0, -505), someShrinkContext());
  }

  @Test
  public void shouldShrinkByOneWhenRemainingCyclesGreaterThanDomainSize() {
    Source<Integer> testee = Integers.range(0, 7);
    ShrinkContext context = new ShrinkContext(0, 5, prng);
    when(prng.generateRandomLongWithinInterval(anyInt(), anyInt()))
        .thenReturn(30L);
    assertThatSource(testee).shrinksValueTo(5, 4, context);
  }

  @Test
  public void shouldShrinkRandomlyWhenRemainingCyclesFewerThanDomainSize() {
    ShrinkContext context = new ShrinkContext(0, 6, this.prng);
    when(prng.generateRandomLongWithinInterval(anyInt(), anyInt()))
        .thenReturn(-4L);
    assertThatSource(Integers.range(-30, 0))
        .shrinksValueTo(-30, -4, context);
  }

  private ShrinkContext someShrinkContext() {
    return new ShrinkContext(0, 1, Configuration.defaultPRNG(0));
  }

  protected static Predicate<Integer> headsTowardsLowerAbsoluteValue(
      int lowerBound, int higherBound, int origin) {
    return (i) -> ((lowerBound <= 0 && 0 <= higherBound
        && Math.abs(i) <= Math.abs(origin) && 0 <= Math.abs(i))
        || (lowerBound < 0 && higherBound < 0 && origin <= i
            && i <= higherBound)
        ||
        (lowerBound > 0 && higherBound > 0 && lowerBound <= i && i <= origin));
  }

}
