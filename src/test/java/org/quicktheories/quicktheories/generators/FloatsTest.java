package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import java.util.function.Predicate;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

public class FloatsTest {

  @Test
  public void shouldNotShrinkTargetValueFromNegativeInfinityToNegativeZero() {
    Source<Float> testee = Floats.fromNegativeInfinityToNegativeZero();
    assertThatSource(testee).cannotShrink(-0f);
  }

  @Test
  public void shouldNotShrinkTargetValueFromNegativeFloatMaxToNegativeZero() {
    Source<Float> testee = Floats.fromNegativeFloatMaxToNegativeZero();
    assertThatSource(testee).cannotShrink(-0f);
  }

  @Test
  public void shouldNotShrinkTargetValueFromZeroToPositiveInfinity() {
    Source<Float> testee = Floats.fromZeroToPositiveInfinity();
    assertThatSource(testee).cannotShrink(0f);
  }

  @Test
  public void shouldNotShrinkTargetValueFromZeroToFloatMax() {
    Source<Float> testee = Floats.fromZeroToFloatMax();
    assertThatSource(testee).cannotShrink(0f);
  }

  @Test
  public void shouldNotShrinkTargetValueFromZeroToOne() {
    Source<Float> testee = Floats.fromZeroToOne();
    assertThatSource(testee).cannotShrink(0f);
  }

  @Test
  public void willShrinkPositiveTargetButNotNegativeAtZeroStepFromNegativeInfinityToPositiveInfinity() {
    Source<Float> testee = Floats.fromNegativeInfinityToPositiveInfinity();
    assertThatSource(testee).shrinksConformTo(0f, i -> i < 0f,
        withCycles(1));// Should never be generated
    assertThatSource(testee).cannotShrink(-0f);
  }

  @Test
  public void shouldShrinkTowardsTargetValueFromNegativeInfinityToNegativeZero() {
    Source<Float> testee = Floats.fromNegativeInfinityToNegativeZero();
    assertThatSource(testee).shrinksConformTo(-23.52352f,
        inclusivelyBetween(-23.52352f, -0f), withCycles(100));
  }

  @Test
  public void shouldShrinkTowardsTargetValueFromNegativeFloatMaxToNegativeZero() {
    Source<Float> testee = Floats.fromNegativeFloatMaxToNegativeZero();
    assertThatSource(testee).shrinksConformTo(-Float.MAX_VALUE,
        inclusivelyBetween(-Float.MAX_VALUE, -0f), withCycles(100));
  }

  @Test
  public void shouldShrinkTowardsTargetValueFromZeroToPositiveInfinity() {
    Source<Float> testee = Floats.fromZeroToPositiveInfinity();
    assertThatSource(testee).shrinksConformTo(Float.POSITIVE_INFINITY,
        inclusivelyBetween(0f, Float.POSITIVE_INFINITY), withCycles(100));
  }

  @Test
  public void shouldShrinkTowardsTargetValueFromZeroToFloatMax() {
    Source<Float> testee = Floats.fromZeroToFloatMax();
    assertThatSource(testee).shrinksConformTo(432.52352f,
        inclusivelyBetween(0f, 432.52352f), withCycles(100));
  }

  @Test
  public void shouldShrinkWithinRightGeneratorAcrossTheFloats() {
    Source<Float> testee = Floats.fromNegativeInfinityToPositiveInfinity();
    assertThatSource(testee).shrinksConformTo(-23.52352f,
        inclusivelyBetween(-23.52352f, -0f), withCycles(100));
    assertThatSource(testee).shrinksConformTo(432.52352f,
        inclusivelyBetween(0f, 432.52352f),
        new ShrinkContext(1, 100, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkWithinRightGeneratorBetweenFloatMaxes() {
    Source<Float> testee = Floats.fromNegativeFloatMaxToPositiveFloatMax();
    assertThatSource(testee).shrinksConformTo(-23.52352f,
        inclusivelyBetween(-23.52352f, -0f), withCycles(100));
    assertThatSource(testee).shrinksConformTo(432.52352f,
        inclusivelyBetween(0f, 432.52352f),
        new ShrinkContext(1, 100, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkTowardsTargetValueFromZeroToOne() {
    Source<Float> testee = Floats.fromZeroToOne();
    assertThatSource(testee).shrinksConformTo(1f, inclusivelyBetween(0f, 1f),
        withCycles(1));
  }
  
  @Test
  public void shouldGenerateAtLeastThreeDistinctValuesUsingFromZeroToOne() {
   Source<Float> testee = Floats.fromZeroToOne();
   assertThatSource(testee).generatesAtLeastNDistinctValues(3);
  }

  private ShrinkContext withCycles(int cycles) {
    return new ShrinkContext(0, cycles, Configuration.defaultPRNG(2));
  }

  private Predicate<Float> inclusivelyBetween(float bottom, float top) {
    return i -> i >= bottom && i <= top;
  }

}
