package org.quicktheories.generators;

import static org.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.quicktheories.core.Gen;
import org.quicktheories.generators.Generate;

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
