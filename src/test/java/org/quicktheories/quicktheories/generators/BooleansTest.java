package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Source;
import org.quicktheories.quicktheories.generators.BooleansDSL.Booleans;

public class BooleansTest {

  @Test
  public void shouldGenerateBothOptions() {
    Source<Boolean> testee = Booleans.generate();
    assertThatSource(testee).generatesAllOf(true, false);
  }

  @Test
  public void shouldShrinkTrueToFalse() {
    Source<Boolean> testee = Booleans.generate();
    assertThatSource(testee).shrinksValueTo(true, false);
  }

  @Test
  public void shouldNotShrinkFalse() {
    Source<Boolean> testee = Booleans.generate();
    assertThatSource(testee).cannotShrink(false);
  }

}
