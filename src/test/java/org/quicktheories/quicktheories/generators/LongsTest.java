package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Gen;

public class LongsTest {

  @Test
  public void shouldGenerateAllLongsInRangeUpToMax() {
    Gen<Long> testee = Generate.longRange(Long.MAX_VALUE - 2, Long.MAX_VALUE);
    assertThatGenerator(testee).generatesAllOf(Long.MAX_VALUE - 1,
        Long.MAX_VALUE - 2, Long.MAX_VALUE);
  }

  @Test
  public void shouldGenerateAllIntegersInRangeDownToMin() {
    Gen<Long> testee = Generate.longRange(Long.MIN_VALUE, Long.MIN_VALUE + 2);
    assertThatGenerator(testee).generatesAllOf(Long.MIN_VALUE,
        Long.MIN_VALUE + 1, Long.MIN_VALUE + 2);
  }

  @Test
  public void shouldShrinkTowardsZeroByDefault() {
    Gen<Long> testee = Generate.longRange(-300l, 100l);
    assertThatGenerator(testee).shrinksTowards(0l);
  }
  
  @Test
  public void shouldShrinkTowardsSuppliedPoint() {
    Gen<Long> testee = Generate.longRange(2l, 100l, 42l);
    assertThatGenerator(testee).shrinksTowards(42l);
  }

}
