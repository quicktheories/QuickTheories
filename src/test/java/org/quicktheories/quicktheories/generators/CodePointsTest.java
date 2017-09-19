package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.quicktheories.quicktheories.WithQuickTheories;
import org.quicktheories.quicktheories.core.Gen;

public class CodePointsTest implements WithQuickTheories {
  
  @Test
  public void generatesCodePointsWithinValidRange() {
    Gen<Integer> testee = CodePoints.codePoints(Character.MIN_CODE_POINT, Character.MAX_CODE_POINT);
    assertThatGenerator(testee).generatesAtLeastNDistinctValues(1000);
  }
  
  @Test
  public void describesCodePoint() {
    Gen<Integer> testee = CodePoints.codePoints(Character.MIN_CODE_POINT, Character.MAX_CODE_POINT);
    assertThat(testee.asString(42)).isEqualTo("42");
  }

}
