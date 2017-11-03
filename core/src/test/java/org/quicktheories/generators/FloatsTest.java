package org.quicktheories.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.impl.GenAssert.assertThatGenerator;
import static org.quicktheories.impl.GenAssert.generateValues;

import java.util.List;

import org.junit.Test;
import org.quicktheories.core.Gen;
import org.quicktheories.generators.Floats;

public class FloatsTest {

  @Test
  public void shouldShrinkNegativeInfinityToNegativeZero() {
    Gen<Float> testee = Floats.fromNegativeInfinityToNegativeZero();
    assertThatGenerator(testee).shrinksTowards(-0f);
  }


  @Test
  public void shouldShrinkPositiveInfinityTowardsZero() {
    Gen<Float> testee = Floats.fromZeroToPositiveInfinity();
    assertThatGenerator(testee).shrinksTowards(0f);
  }

  @Test
  public void shouldShrinkOneTowardsZero() {
    Gen<Float> testee = Floats.fromZeroToOne();
    assertThatGenerator(testee).shrinksTowards(0f);
  }

  @Test
  public void shouldGenerateZeroAndOne() {
    Gen<Float> testee = Floats.fromZeroToOne();
    assertThatGenerator(testee).generatesTheMinAndMax(0f, 1f);
  }
  
  
  @Test
  public void shouldGenerateDistinctValuesUsingFromZeroToOne() {
   Gen<Float> testee = Floats.fromZeroToOne();
   assertThatGenerator(testee).generatesAtLeastNDistinctValues(1000);
  }
  
  @Test
  public void generatesLimitsWithinPositiveRange() {
    Gen<Float> testee = Floats.between(0, Float.MAX_VALUE);
    assertThatGenerator(testee).generatesTheMinAndMax(0f, Float.MAX_VALUE);
  }
  
  @Test
  public void generatesLimitsWithinNegativeRange() {
    Gen<Float> testee = Floats.between(-12.0f, -1.0f);
    assertThatGenerator(testee).generatesTheMinAndMax(-12.0f, -1.0f);
  }  
  
  @Test
  public void generatesLimitsWithinRangeSpanningZero() {
    Gen<Float> testee = Floats.between(-42.0f, 42.0f);
    assertThatGenerator(testee).generatesTheMinAndMax(-42.0f, 42.0f);
  }  
  
  @Test
  public void generatesValuesAcrossPositiveRange() {
    Gen<Float> testee = Floats.between(10f, 1000f);
    List<Float> vals = generateValues(testee, 100);
    assertThat(vals.stream().filter( f -> f < 100f).count()).isGreaterThan(5);
    assertThat(vals.stream().filter( f -> f > 500f).count()).isGreaterThan(40);
    assertThat(vals.stream().filter( f -> f > 900f).count()).isLessThan(10);
  }  
  
  @Test
  public void generatesValuesAcrossRangeSpanningZero() {
    Gen<Float> testee = Floats.between(-10f, 10f);
    List<Float> vals = generateValues(testee, 100);
    assertThat(vals.stream().filter( d -> d < 0f).count()).isGreaterThan(40);
    assertThat(vals.stream().filter( d -> d < -5f).count()).isLessThan(30);  
    assertThat(vals.stream().filter( d -> d > 0f).count()).isGreaterThan(40);
    assertThat(vals.stream().filter( d -> d > 5f).count()).isLessThan(30);    
  }  
    
  
}
