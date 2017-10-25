package org.quicktheories.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.impl.GenAssert.assertThatGenerator;
import static org.quicktheories.impl.GenAssert.generateValues;

import java.util.List;

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.quicktheories.core.Gen;
import org.quicktheories.generators.Doubles;

public class DoublesTest {


  @Test
  public void shouldGenerateDoublesOfBothSignsAcrossRange() {
    List<Double> generated = generateValues(
        Doubles.fromNegativeInfinityToPositiveInfinity(), 6);
    assertThat(generated).haveAtLeast(1, negative());
    assertThat(generated).haveAtLeast(1, positive());
  }
  

  @Test
  public void generatesDistinctValuesBetweenZeroAndOne() {
    Gen<Double> testee = Doubles.fromZeroToOne();
    assertThatGenerator(testee).generatesAtLeastNDistinctValues(1000);
  }

  @Test
  public void generatesLimitsWithinPositiveRange() {
    Gen<Double> testee = Doubles.between(0, Double.MAX_VALUE);
    assertThatGenerator(testee).generatesTheMinAndMax(0d, Double.MAX_VALUE);
  }
  
  @Test
  public void generatesLimitsWithinNegativeRange() {
    Gen<Double> testee = Doubles.between(-42.0d, -1.0d);
    assertThatGenerator(testee).generatesTheMinAndMax(-42.0d, -1.0d);
  }  
  
  @Test
  public void generatesLimitsWithinRangeSpanningZero() {
    Gen<Double> testee = Doubles.between(-42.0d, 42.0d);
    assertThatGenerator(testee).generatesTheMinAndMax(-42.0d, 42.0d);
  }  
  
  @Test
  public void generatesValuesAcrossPositiveRange() {
    Gen<Double> testee = Doubles.between(10d, 1000d);
    List<Double> vals = generateValues(testee, 100);
    assertThat(vals.stream().filter( d -> d < 100d).count()).isGreaterThan(5);
    assertThat(vals.stream().filter( d -> d > 500d).count()).isGreaterThan(40);
    assertThat(vals.stream().filter( d -> d > 900d).count()).isLessThan(10);
  }  
  
  @Test
  public void generatesValuesAcrossRangeSpanningZero() {
    Gen<Double> testee = Doubles.between(-10d, 10d);
    List<Double> vals = generateValues(testee, 100);
    assertThat(vals.stream().filter( d -> d < 0d).count()).isGreaterThan(40);
    assertThat(vals.stream().filter( d -> d < -5d).count()).isLessThan(25);  
    assertThat(vals.stream().filter( d -> d > 0d).count()).isGreaterThan(40);
    assertThat(vals.stream().filter( d -> d > 5d).count()).isLessThan(25);    
  }  
  
  @Test(expected = IllegalArgumentException.class)
  public void doesNotAllowInvalidRanges() {
    Doubles.between(1, 0);
  }
  
  private Condition<Double> negative() {
    return new  Condition<Double>() {
      @Override
      public boolean matches(Double value) {
        return value.toString().startsWith("-");
      }   
    };
  }

  private Condition<Double> positive() {
    return new  Condition<Double>() {
      @Override
      public boolean matches(Double value) {
        return !value.toString().startsWith("-");
      }   
    };
  }
}
