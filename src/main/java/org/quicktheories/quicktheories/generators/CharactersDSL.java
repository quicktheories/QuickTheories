package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;

/**
 * 
 * A Class for creating Sources of Characters, that will produce Characters by
 * considering code points of the specified domain.
 *
 */
public class CharactersDSL {

  private static final int BASIC_LATIN_LAST_CODEPOINT = 0x007E;
  private static final int BASIC_LATIN_FIRST_CODEPOINT = 0x0020;
  private static final int ASCII_LAST_CODEPOINT = 0x007F;
  private static final int FIRST_CODEPOINT = 0x0000;
  private static final int LARGEST_DEFINED_BMP_CODEPOINT = 65533;
  private static final char SMALLEST_LATIN_CHARACTER = 0x007E;
  private static final char LARGEST_LATIN_CHARACTER = 0x0020;
  private static final char SMALLEST_CHARACTER = 0x0000;
  private static final char LARGEST_ASCII_CHARACTER = 0x007F;
  private static final char LARGEST_DEFINED_BMP_CHARACTER = '\ufffd';

  /**
   * Generates a Basic Latin Character, and shrinks to the value with the
   * smallest code point, which is the whitespace character in this domain.
   * 
   * The Source is weighted so that is likely to generate the first Basic Latin
   * codepoint (007E) the highest Basic Latin codepoint (0020) one or more
   * times.
   * 
   * @return a Source of type Character
   */
  public Source<Character> basicLatinCharacters() {
    return Compositions.weightWithValues(
        Characters.ofCharacters(BASIC_LATIN_FIRST_CODEPOINT,
            BASIC_LATIN_LAST_CODEPOINT),
        LARGEST_LATIN_CHARACTER, SMALLEST_LATIN_CHARACTER);
  }

  /**
   * Generates an Ascii character, and shrinks to the value with the smallest
   * code point, which is the null character in this domain.
   * 
   * The Source is weighted so it is likely to generate 007F and 0000 one or
   * more times.
   * 
   * @return a Source of type Character
   */
  public Source<Character> ascii() {
    return Compositions.weightWithValues(
        Characters.ofCharacters(FIRST_CODEPOINT, ASCII_LAST_CODEPOINT),
        LARGEST_ASCII_CHARACTER, SMALLEST_CHARACTER);
  }

  /**
   * Generates a character in the Basic Multilingual Plane, and shrinks to the
   * value with the smallest code point, which is the null character in this
   * domain.
   * 
   * The Source is weighted so it is likely to generate the upper and lower
   * values of the domain one of more times.
   * 
   * 
   * @return a Source of type Character
   */
  public Source<Character> basicMultilingualPlane() {
    return Compositions.weightWithValues(
        Characters.ofCharacters(FIRST_CODEPOINT, LARGEST_DEFINED_BMP_CODEPOINT),
        LARGEST_DEFINED_BMP_CHARACTER, SMALLEST_CHARACTER);
  }

}
