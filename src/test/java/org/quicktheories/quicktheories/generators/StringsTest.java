package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Gen;

public class StringsTest {

  private static final int BASIC_LATIN_LAST_CODEPOINT = 0x007E;
  private static final int BASIC_LATIN_FIRST_CODEPOINT = 0x0020;

  @Test
  public void shouldShrinkNumericStringsTowardsZero() {
    Gen<String> testee = Strings.boundedNumericStrings(Integer.MIN_VALUE,
        Integer.MAX_VALUE);
    assertThatGenerator(testee).shrinksTowards("0");
  }


  @Test
  public void shouldReturnEmptyStringIfFixedLengthSetToZero() {
    Gen<String> testee = Strings.ofBoundedLengthStrings(
        BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT, 0, 0);
    assertThatGenerator(testee).generatesAllOf("");
  }

  @Test
  public void shouldShrinkTowardsZeroLengthString() {
    Gen<String> testee = Strings.ofBoundedLengthStrings(
        BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT, 0, 100);
    assertThatGenerator(testee).shrinksTowards("");
  }
  
  @Test
  public void shouldShrinkFixedCodepointStringsTowardsExclaimationMarks() {
    Gen<String> testee = Strings.withCodePoints(
        BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT, Generate.constant(3));
    assertThatGenerator(testee).shrinksTowards("!!!");
  }

}
