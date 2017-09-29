package org.quicktheories.generators;

import static org.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.quicktheories.core.Gen;
import org.quicktheories.generators.Generate;

public class IntegersTest {
  
  @Test
  public void generatesIntegerMinAndMaxLimits() {
    Gen<Integer> testee = Generate.range(Integer.MIN_VALUE,
        Integer.MAX_VALUE);
    assertThatGenerator(testee).generatesTheMinAndMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void generatesNegativeMaxLimits() {
    Gen<Integer> testee = Generate.range(-44,-42);
    assertThatGenerator(testee).generatesTheMinAndMax(-44, -42);
  }
  
  @Test
  public void shrinksTowardsZeroByDefault() {
    Gen<Integer> testee = Generate.range(Integer.MIN_VALUE,
        Integer.MAX_VALUE);
    assertThatGenerator(testee).shrinksTowards(0);
  }
  
  @Test
  public void shrinksTowardsSuppliedTarget() {
    Gen<Integer> testee = Generate.range(Integer.MIN_VALUE,
        Integer.MAX_VALUE, 42);
    assertThatGenerator(testee).shrinksTowards(42);
  }
  
  @Test
  public void doesNotGenerateValueOutsideOfRange() {
    Gen<Integer> testee = Generate.range(1, 10);
    assertThatGenerator(testee).doesNotGenerate(0);
    assertThatGenerator(testee).doesNotGenerate(11);
  }

  @Test
  public void generatesAllIntegersInRange() {
    Gen<Integer> testee = Generate.range(1, 4);
    assertThatGenerator(testee).generatesAllOf(1, 2, 3, 4);
  }

  @Test
  public void generatesAllIntegersInNEgativeRange() {
    Gen<Integer> testee = Generate.range(-4, -1);
    assertThatGenerator(testee).generatesAllOf(-1, -2, -3, -4);
  }
}
