package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Gen;

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

  /**
   * Generates a Basic Latin Character, and shrinks to the value with the
   * smallest code point, which is the whitespace character in this domain.
   * 
   * @return a Source of type Character
   */
  public Gen<Character> basicLatinCharacters() {
    return Generate.characters(BASIC_LATIN_FIRST_CODEPOINT,
            BASIC_LATIN_LAST_CODEPOINT);
  }

  /**
   * Generates an Ascii character, and shrinks to the value with the smallest
   * code point, which is the null character in this domain.
   * 
   * @return a Source of type Character
   */
  public Gen<Character> ascii() {
    return Generate.characters(FIRST_CODEPOINT, ASCII_LAST_CODEPOINT);
  }

  /**
   * Generates a character in the Basic Multilingual Plane, and shrinks to the
   * value with the smallest code point, which is the null character in this
   * domain.
   * 
   * @return a Source of type Character
   */
  public Gen<Character> basicMultilingualPlane() {
    return Generate.characters(FIRST_CODEPOINT, LARGEST_DEFINED_BMP_CODEPOINT);
  }

}
