package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

public class DoublesTest {

  @Test
  public void shouldNotShrinkTargetValueFromNegativeInfinityToNegativeZero() {
    Source<Double> testee = Doubles.fromNegativeInfinityToNegativeZero();
    assertThatSource(testee).cannotShrink(-0d);
  }

  @Test
  public void shouldNotShrinkTargetValueFromNegativeDoubleMaxToNegativeZero() {
    Source<Double> testee = Doubles.fromNegativeDoubleMaxToNegativeZero();
    assertThatSource(testee).cannotShrink(-0d);
  }

  @Test
  public void shouldNotShrinkTargetValueFromZeroToPositiveInfinity() {
    Source<Double> testee = Doubles.fromZeroToPositiveInfinity();
    assertThatSource(testee).cannotShrink(0d);
  }

  @Test
  public void shouldNotShrinkTargetValueFromZeroToDoubleMax() {
    Source<Double> testee = Doubles.fromZeroToDoubleMax();
    assertThatSource(testee).cannotShrink(0d);
  }

  @Test
  public void shouldNotShrinkTargetValueFromZeroToOne() {
    Source<Double> testee = Doubles.fromZeroToOne();
    assertThatSource(testee).cannotShrink(0d);
  }

  @Test
  public void willShrinkPositiveTargetButNotNegativeAtZeroStepFromNegativeInfinityToPositiveInfinity() {
    Source<Double> testee = Doubles.fromNegativeInfinityToPositiveInfinity();
    assertThatSource(testee).shrinksConformTo(0d, i -> i < 0d,
        withCycles(1));// Should never be generated
    assertThatSource(testee).cannotShrink(-0d);
  }

  @Test
  public void willShrinkPositiveTargetButNotNegativeAtZeroStepFromNegativeMaxToPositiveMax() {
    Source<Double> testee = Doubles.fromNegativeDoubleMaxToDoubleMax();
    assertThatSource(testee).shrinksConformTo(0d, i -> i < 0d,
        withCycles(1));// Should never be generated
    assertThatSource(testee).cannotShrink(-0d);
  }

  @Test
  public void shouldShrinkTowardsTargetValueFromNegativeInfinityToNegativeZero() {
    Source<Double> testee = Doubles.fromNegativeInfinityToNegativeZero();
    assertThatSource(testee).shrinksConformTo(-23.52352,
        inclusivelyBetween(-23.52352, -0d), withCycles(100));
  }

  @Test
  public void shouldShrinkTowardsTargetValueFromNegativeDoubleMaxToNegativeZero() {
    Source<Double> testee = Doubles.fromNegativeDoubleMaxToNegativeZero();
    assertThatSource(testee).shrinksConformTo(-Double.MAX_VALUE,
        inclusivelyBetween(-Double.MAX_VALUE, -0d), withCycles(100));
  }

  @Test
  public void shouldShrinkTowardsTargetValueFromZeroToPositiveInfinity() {
    Source<Double> testee = Doubles.fromZeroToPositiveInfinity();
    assertThatSource(testee).shrinksConformTo(Double.POSITIVE_INFINITY,
        inclusivelyBetween(0d, Double.POSITIVE_INFINITY), withCycles(100));
  }

  @Test
  public void shouldShrinkTowardsTargetValueFromZeroToDoubleMax() {
    Source<Double> testee = Doubles.fromZeroToDoubleMax();
    assertThatSource(testee).shrinksConformTo(432.52352,
        inclusivelyBetween(0d, 432.52352), withCycles(100));
  }

  @Test
  public void shouldShrinkWithinRightGeneratorAcrossTheDoubles() {
    Source<Double> testee = Doubles.fromNegativeInfinityToPositiveInfinity();
    assertThatSource(testee).shrinksConformTo(-23.52352,
        inclusivelyBetween(-23.52352, -0d), withCycles(100));
    assertThatSource(testee).shrinksConformTo(432.52352,
        inclusivelyBetween(0d, 432.52352),
        new ShrinkContext(1, 100, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkTowardsTargetValueFromZeroToOne() {
    Source<Double> testee = Doubles.fromZeroToOne();
    assertThatSource(testee).shrinksConformTo(1d, inclusivelyBetween(0d, 1d),
        withCycles(1));
  }

  @Test
  public void shouldGenerateDoublesOfAlternatingSignAcrossRange() {
    List<Double> generated = generateValues(
        Doubles.fromNegativeInfinityToPositiveInfinity(), 6);
    for (int i = 0; i < 6; i++) {
      if (i % 2 == 0) {
        assertTrue(
            "Expected generated list (" + generated + ") to alternate sign",
            generated.get(i) <= -0d);
      } else {
        assertTrue(
            "Expected generated list (" + generated + ") to alternate sign",
            generated.get(i) >= 0d);
      }
    }
  }
  
  @Test
  public void shouldGenerateAtLeastThreeDistinctValuesUsingFromZeroToOne() {
   Source<Double> testee = Doubles.fromZeroToOne();
   assertThatSource(testee).generatesAtLeastNDistinctValues(3);
  }

  private List<Double> generateValues(Source<Double> generator, int count) {
    PseudoRandom prng = Configuration.defaultPRNG(2);
    List<Double> generated = new ArrayList<Double>();
    for (int i = 0; i != count; i++) {
      generated.add(generator.next(prng, i));
    }
    return generated;
  }

  private ShrinkContext withCycles(int cycles) {
    return new ShrinkContext(0, cycles, Configuration.defaultPRNG(2));
  }

  private Predicate<Double> inclusivelyBetween(double bottom, double top) {
    return i -> i >= bottom && i <= top;
  }

}
