package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Gen;

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
  public void shouldGenerateDistinctValuesUsingFromZeroToOne() {
   Gen<Float> testee = Floats.fromZeroToOne();
   assertThatGenerator(testee).generatesAtLeastNDistinctValues(1000);
  }

}
