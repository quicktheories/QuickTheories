package org.quicktheories.generators;

import org.quicktheories.core.Gen;

/**
 * A Class for creating String Sources that will produce Strings composed of
 * code points within the specified domain.
 * 
 */
public class StringsDSL {

  private static final int BASIC_LATIN_LAST_CODEPOINT = 0x007E;
  private static final int BASIC_LATIN_FIRST_CODEPOINT = 0x0020;
  private static final int ASCII_LAST_CODEPOINT = 0x007F;
  private static final int LARGEST_DEFINED_BMP_CODEPOINT = 65533;

  /**
   * Generates integers as Strings, and shrinks towards "0".
   * 
   * @return a Source of type String
   */
  public Gen<String> numeric() {
    return numericBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  /**
   * Generates integers within the interval as Strings.
   * 
   * @param startInclusive
   *          - lower inclusive bound of integer domain
   * @param endInclusive
   *          - upper inclusive bound of integer domain
   * @return a Source of type String
   */
  public Gen<String> numericBetween(int startInclusive,
      int endInclusive) {
    ArgumentAssertions.checkArguments(startInclusive <= endInclusive,
        "There are no Integer values to be generated between startInclusive (%s) and endInclusive (%s)",
        startInclusive, endInclusive);
    return  Strings.boundedNumericStrings(startInclusive, endInclusive);
  }

  /**
   * Constructs a StringGeneratorBuilder which will build Strings composed from
   * all defined code points
   * 
   * @return a StringGeneratorBuilder
   */
  public StringGeneratorBuilder allPossible() {
    return new StringGeneratorBuilder(Character.MIN_CODE_POINT,
        Character.MAX_CODE_POINT);
  }

  /**
   * Constructs a StringGeneratorBuilder which will build Strings composed from
   * all defined code points in the Basic Multilingual Plane
   * 
   * @return a StringGeneratorBuilder
   */
  public StringGeneratorBuilder basicMultilingualPlaneAlphabet() {
    return new StringGeneratorBuilder(Character.MIN_CODE_POINT,
        LARGEST_DEFINED_BMP_CODEPOINT);
  }

  /**
   * Constructs a StringGeneratorBuilder which will build Strings composed from
   * Unicode Basic Latin Alphabet
   * 
   * @return a StringGeneratorBuilder
   */
  public StringGeneratorBuilder basicLatinAlphabet() {
    return new StringGeneratorBuilder(BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT);
  }

  /**
   * Constructs a StringGeneratorBuilder which will build Strings composed from
   * Unicode Ascii Alphabet
   * 
   * @return a StringGeneratorBuilder
   */
  public StringGeneratorBuilder ascii() {
    return new StringGeneratorBuilder(Character.MIN_CODE_POINT,
        ASCII_LAST_CODEPOINT);
  }

  public static class StringGeneratorBuilder {

    private final int minCodePoint;
    private final int maxCodePoint;

    private StringGeneratorBuilder(int minCodePoint, int maxCodePoint) {
      this.minCodePoint = minCodePoint;
      this.maxCodePoint = maxCodePoint;
    }

    /**
     * Generates Strings of a fixed number of code points.
     * 
     * @param codePoints
     *          - the fixed number of code points for the String
     * @return a a Source of type String
     */
    public Gen<String> ofFixedNumberOfCodePoints(int codePoints) {
      ArgumentAssertions.checkArguments(codePoints >= 0,
          "The number of codepoints cannot be negative; %s is not an accepted argument",
          codePoints);
      return Strings.withCodePoints(minCodePoint,
          maxCodePoint, Generate.constant(codePoints));
    }

    /**
     * Generates Strings of a fixed length. 
     * 
     * @param fixedLength
     *          - the fixed length for the Strings
     * @return a Source of type String
     */
    public Gen<String> ofLength(int fixedLength) {
      return ofLengthBetween(fixedLength, fixedLength);
    }

    /**
     * Generates Strings of length bounded between minLength and maxLength
     * inclusively. 
     * 
     * @param minLength
     *          - minimum inclusive length of String
     * @param maxLength
     *          - maximum inclusive length of String
     * @return a Source of type String
     */
    public Gen<String> ofLengthBetween(int minLength, int maxLength) {
      ArgumentAssertions.checkArguments(minLength <= maxLength,
          "The minLength (%s) is longer than the maxLength(%s)",
          minLength, maxLength);
      ArgumentAssertions.checkArguments(minLength >= 0,
          "The length of a String cannot be negative; %s is not an accepted argument",
          minLength);
      return Strings.ofBoundedLengthStrings(minCodePoint, maxCodePoint,
          minLength, maxLength);
    }

  }

}
