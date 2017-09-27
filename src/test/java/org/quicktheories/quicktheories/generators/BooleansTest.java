package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Gen;

public class BooleansTest {

  @Test
  public void booleansShouldGenerateBothTrueAndFalse() {
    Gen<Boolean> testee = Generate.booleans();
    assertThatGenerator(testee).generatesAllOf(true, false);
  }

  @Test
  public void shouldShrinkTrueToFalse() {
    Gen<Boolean> testee = Generate.booleans();
    assertThatGenerator(testee).shrinksTowards(false);
  }

}
