package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import java.util.function.Predicate;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

public class CharactersTest {

  private static final int BASIC_LATIN_LAST_CODEPOINT = 0x007E;
  private static final int BASIC_LATIN_FIRST_CODEPOINT = 0x0020;
  private static final int ASCII_LAST_CODEPOINT = 0x007F;
  private static final int FIRST_CODEPOINT = 0x0000;
  private static final int LARGEST_DEFINED_BMP_CODEPOINT = 65533;

  @Test
  public void shouldNotShrinkSpaceCharacterInBasicLatin() {
    Source<Character> testee = Characters
        .ofCharacters(BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT);
    assertThatSource(testee).cannotShrink(' ');
  }

  @Test
  public void shouldNotShrinkNullCharacterInAscii() {
    Source<Character> testee = Characters.ofCharacters(FIRST_CODEPOINT,
        ASCII_LAST_CODEPOINT);
    assertThatSource(testee).cannotShrink('\u0000');
  }

  @Test
  public void shouldNotShrinkNullCharacterinBMP() {
    Source<Character> testee = Characters.ofCharacters(FIRST_CODEPOINT,
        LARGEST_DEFINED_BMP_CODEPOINT);
    assertThatSource(testee).cannotShrink('\u0000');
  }

  @Test
  public void shouldShrinkBLCharacterByOneCodePointWhenRemainingCyclesIsGreaterThanDistanceToTarget() {
    Source<Character> testee = Characters
        .ofCharacters(BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT);
    assertThatSource(testee).shrinksValueTo('F', 'E',
        new ShrinkContext(0, 39, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkBLCharacterByOneCodePointWhenRemainingCyclesIsEqualToDistanceToTarget() {
    Source<Character> testee = Characters
        .ofCharacters(BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT);
    assertThatSource(testee).shrinksValueTo('F', 'E',
        new ShrinkContext(0, 38, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkBLCharacterWhenRemainingCyclesIsLessThanDistanceToTarget() {
    Source<Character> testee = Characters
        .ofCharacters(BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT);
    char original = '{';
    Predicate<Character> charShrinksInRightDirection = (
        i) -> (int) i <= (int) original && (int) i >= 0x0020;
    assertThatSource(testee).shrinksConformTo(original,
        charShrinksInRightDirection,
        new ShrinkContext(0, 1, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldNotShrinkBMPCharacterToAnUndefinedCharacter() {
    Source<Character> testee = Characters.ofCharacters(FIRST_CODEPOINT,
        LARGEST_DEFINED_BMP_CODEPOINT);
    assertThatSource(testee).shrinksValueTo('\u085E', '\u085B',
        new ShrinkContext(0, 10000000, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkBMPCharacterWhenRemainingCyclesIsLessThanDistanceToTarget() {
    Source<Character> testee = Characters.ofCharacters(FIRST_CODEPOINT,
        LARGEST_DEFINED_BMP_CODEPOINT);
    char original = '\u0Af0';
    Predicate<Character> charShrinksInRightDirection = (
        i) -> (int) i <= (int) original && (int) i >= 0x0000;
    assertThatSource(testee).shrinksConformTo(original,
        charShrinksInRightDirection,
        new ShrinkContext(0, 1, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkAsciiCharacterByOneCodePontWhenRemainingCyclesIsGreaterThanDistanceToTarget() {
    Source<Character> testee = Characters.ofCharacters(FIRST_CODEPOINT,
        ASCII_LAST_CODEPOINT);
    assertThatSource(testee).shrinksValueTo('\u0004', '\u0003',
        new ShrinkContext(0, 1000, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkAsciiCharacterWhenRemainingCyclesIsLessThanDistanceToTarget() {
    Source<Character> testee = Characters.ofCharacters(FIRST_CODEPOINT,
        ASCII_LAST_CODEPOINT);
    char original = '\u007F';
    Predicate<Character> charShrinksInRightDirection = (
        i) -> (int) i <= (int) original && (int) i >= 0x0000;
    assertThatSource(testee).shrinksConformTo(original,
        charShrinksInRightDirection,
        new ShrinkContext(0, 1, Configuration.defaultPRNG(2)));
  }

}
