package org.quicktheories.quicktheories.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;
import static org.quicktheories.quicktheories.impl.GenAssert.generateValues;

import java.util.List;

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.quicktheories.quicktheories.core.Gen;

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
