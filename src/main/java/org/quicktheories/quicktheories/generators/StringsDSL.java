package org.quicktheories.quicktheories.generators;

import org.quicktheories.quicktheories.core.Source;

/**
 * A Class for creating String Sources that will produce Strings composed of
 * code points within the specified domain. The method by which a String will be
 * shrunk depends on whether it is a numeric String or of fixed length, fixed
 * number of code points or bounded length.
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
   * The Source is weighted so that it is likely to generate Integer.MAX_VALUE
   * (2147483647) and Integer.MIN_VALUE (-2147483648) at least once.
   * 
   * @return a Source of type String
   */
  public Source<String> numeric() {
    return numericBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  /**
   * Generates integers within the interval as Strings. It shrinks towards the
   * smallest absolute value as a String.
   * 
   * The Source is weighted so that it is likely to generate endInclusive and
   * startInclusive at least once.
   * 
   * @param startInclusive
   *          - lower inclusive bound of integer domain
   * @param endInclusive
   *          - upper inclusive bound of integer domain
   * @return a Source of type String
   */
  public Source<String> numericBetween(int startInclusive,
      int endInclusive) {
    ArgumentAssertions.checkArguments(startInclusive <= endInclusive,
        "There are no Integer values to be generated between startInclusive (%s) and endInclusive (%s)",
        startInclusive, endInclusive);
    return Compositions.weightWithValues(
        Strings.boundedNumericStrings(startInclusive, endInclusive),
        Integer.toString(endInclusive), Integer.toString(startInclusive));
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
     * Generates Strings of a fixed number of code points. Will shrink by
     * reducing the numeric value of code points in tandem.
     * 
     * @param codePoints
     *          - the fixed number of code points for the String
     * @return a a Source of type String
     */
    public Source<String> ofFixedNumberOfCodePoints(int codePoints) {
      ArgumentAssertions.checkArguments(codePoints >= 0,
          "The number of codepoints cannot be negative; %s is not an accepted argument",
          codePoints);
      return Strings.ofFixedNumberOfCodePointsStrings(minCodePoint,
          maxCodePoint, codePoints);
    }

    /**
     * Generates Strings of a fixed length. Will shrink by reducing the numeric
     * value of code points in tandem. If the code point to be shrunk is a
     * supplementary code point, either a lower-valued supplementary code point
     * will be produced, or a BMP code point will be produced twice.
     * 
     * @param fixedLength
     *          - the fixed length for the Strings
     * @return a Source of type String
     */
    public Source<String> ofLength(int fixedLength) {
      return ofLengthBetween(fixedLength, fixedLength);
    }

    /**
     * Generates Strings of length bounded between minLength and maxLength
     * inclusively. If length of String is greater than minLength, will shrink
     * by removing a random substring of length 1. If this substring is part of
     * a supplementary code point, the supplementary code point is replaced with
     * a BMP code point. If the String is equal to minLength, it shrinks as if a
     * fixed length String.
     * 
     * @param minLength
     *          - minimum inclusive length of String
     * @param maxLength
     *          - maximum inclusive length of String
     * @return a Source of type String
     */
    public Source<String> ofLengthBetween(int minLength, int maxLength) {
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
