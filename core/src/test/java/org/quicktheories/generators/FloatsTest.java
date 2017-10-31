package org.quicktheories.generators;

import static org.quicktheories.impl.GenAssert.assertThatGenerator;

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
  public void shouldGenerateDistinctValuesUsingFromZeroToOne() {
   Gen<Float> testee = Floats.fromZeroToOne();
   assertThatGenerator(testee).generatesAtLeastNDistinctValues(1000);
  }
  
}
