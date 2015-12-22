package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

public class LongsTest extends ComponentTest<Long> {

  @Test
  public void shouldGenerateAllLongsInRangeUpToMax() {
    Source<Long> testee = Longs.range(Long.MAX_VALUE - 2, Long.MAX_VALUE);
    assertThatSource(testee).generatesAllOf(Long.MAX_VALUE - 1,
        Long.MAX_VALUE - 2, Long.MAX_VALUE);
  }

  @Test
  public void shouldGenerateAllIntegersInRangeDownToMin() {
    Source<Long> testee = Longs.range(Long.MIN_VALUE, Long.MIN_VALUE + 2);
    assertThatSource(testee).generatesAllOf(Long.MIN_VALUE,
        Long.MIN_VALUE + 1, Long.MIN_VALUE + 2);
  }

  @Test
  public void shouldShrinkTowardsTargetByOneIfRemainingcyclesGreaterThanDistanceToTarget() {
    Source<Long> testee = Longs.range(-5, 450);
    assertThatSource(testee).shrinksValueTo(-4L, -3L,
        new ShrinkContext(0, 100, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldShrinkUpwardsOneStepInPositiveIntegersIfRemainingCyclesGreaterThanDistanceToTarget() {
    Source<Long> testee = Longs.range(2, 100)
        .withShrinker(Longs.shrinkTowardsTarget(100));
    assertThatSource(testee).shrinksValueTo(50L, 51L,
        new ShrinkContext(0, 100, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldNotShrinkUpwardsInPositiveIntegersPastTarget() {
    Source<Long> testee = Longs.range(2, 100)
        .withShrinker(Longs.shrinkTowardsTarget(100));
    assertThatSource(testee).cannotShrink(100L);
  }

  @Test
  public void shouldNotShrinkUpwardsWhenMinIsTarget() {
    Source<Long> testee = Longs.range(Long.MIN_VALUE, Long.MAX_VALUE)
        .withShrinker(Longs.shrinkTowardsTarget(Long.MIN_VALUE));
    assertThatSource(testee).cannotShrink(Long.MIN_VALUE);
  }

  @Test
  public void shouldShrinkUpwardsInPositiveIntegersIfRemainingCyclesLessThanDistanceToTarget() {
    Source<Long> testee = Longs.range(2, 100)
        .withShrinker(Longs.shrinkTowardsTarget(100));
    assertThatSource(testee).shrinksConformTo(50L, i -> i >= 50 && i <= 100,
        new ShrinkContext(0, 1, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldShrinkDownwardOneStepInNegativeIntegersIfRemainingCyclesGreaterThanDistanceToTarget() {
    Source<Long> testee = Longs.range(-100, 0)
        .withShrinker(Longs.shrinkTowardsTarget(-100));
    assertThatSource(testee).shrinksValueTo(-50L, -51L,
        new ShrinkContext(0, 100, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldNotShrinkDownwardsInNegativeIntegersPastTarget() {
    Source<Long> testee = Longs.range(-100, -2)
        .withShrinker(Longs.shrinkTowardsTarget(-100));
    assertThatSource(testee).cannotShrink(-100L);
  }

  @Test
  public void shouldShrinkDownwardsInNegativeIntegersIfRemainingCyclesLessThanDistanceToTarget() {
    Source<Long> testee = Longs.range(-100, 0)
        .withShrinker(Longs.shrinkTowardsTarget(-100));
    assertThatSource(testee).shrinksConformTo(0L, i -> i <= 0 && i >= -100,
        new ShrinkContext(0, 1, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldShrinkDownwardsTowardsLongMinimum() {
    Source<Long> testee = Longs.range(Long.MIN_VALUE, 0)
        .withShrinker(Longs.shrinkTowardsTarget(Long.MIN_VALUE));
    assertThatSource(testee).shrinksConformTo(0L,
        i -> i <= 0 && i >= Long.MIN_VALUE,
        new ShrinkContext(0, 1, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldShrinkUpwardsFromLongMinimumWhenRemainingCyclesGreaterThanDistanceToTarget() {
    Source<Long> testee = Longs.range(Long.MIN_VALUE,
        Long.MIN_VALUE + 100);
    assertThatSource(testee).shrinksValueTo(Long.MIN_VALUE, -Long.MAX_VALUE,
        new ShrinkContext(0, 1000, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldShrinkCodePointsInRightDirectionWhenEndInclusiveIsLessThanTarget() {
    Source<Long> testee = CodePoints.codePoints(0x0000, 0x0020);
    assertThatSource(testee).shrinksConformTo(0x0017L,
        i -> i <= 0x0017 && i >= 0x0000,
        new ShrinkContext(0, 30, Configuration.defaultPRNG(0)));
  }

}
