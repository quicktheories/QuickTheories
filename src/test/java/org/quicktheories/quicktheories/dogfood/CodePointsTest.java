package org.quicktheories.quicktheories.dogfood;

import org.junit.Test;
import org.quicktheories.quicktheories.WithQuickTheories;
import org.quicktheories.quicktheories.generators.CodePoints;

public class CodePointsTest implements WithQuickTheories {
  
  private static final int BASIC_LATIN_LAST_CODEPOINT = 0x007E;
  private static final int BASIC_LATIN_FIRST_CODEPOINT = 0x0020;
  
  @Test
  public void generatesOnlyValidCodepoints() {
    qt()
    .withExamples(100000)
    .forAll(CodePoints.codePoints(Character.MIN_CODE_POINT, Character.MAX_CODE_POINT))
    .check( i -> Character.isValidCodePoint(i));
  }
  
  @Test
  public void generatesOnlyValidCodepointsInBasicLatinRange() {
    qt()
    .withExamples(100000)
    .forAll(CodePoints.codePoints(BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT))
    .check( i -> Character.isValidCodePoint(i));
  }
  
  @Test
  public void generatesOnlyCodepointsWithinTheSuppliedRange() {
    qt()
    .withExamples(100000)
    .forAll(CodePoints.codePoints(BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT))
    .check( i ->  i >= BASIC_LATIN_FIRST_CODEPOINT && i <= BASIC_LATIN_LAST_CODEPOINT);
  }

}
