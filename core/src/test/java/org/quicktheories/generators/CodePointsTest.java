package org.quicktheories.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.quicktheories.WithQuickTheories;
import org.quicktheories.core.Gen;
import org.quicktheories.generators.CodePoints;

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
