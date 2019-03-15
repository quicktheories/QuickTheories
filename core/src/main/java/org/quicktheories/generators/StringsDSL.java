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
  // https://en.wikipedia.org/wiki/Geohash
  private static final char[] GEOHASH = {
          '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g',
          'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
  };
  // https://tools.ietf.org/html/rfc4648
  private static final char[] BASE_32 = {
          'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
          'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7'
  };

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
    return betweenCodePoints(Character.MIN_CODE_POINT,
        Character.MAX_CODE_POINT);
  }

  /**
   * Constructs a StringGeneratorBuilder which will build Strings composed from
   * all defined code points in the Basic Multilingual Plane
   * 
   * @return a StringGeneratorBuilder
   */
  public StringGeneratorBuilder basicMultilingualPlaneAlphabet() {
    return betweenCodePoints(Character.MIN_CODE_POINT,
        LARGEST_DEFINED_BMP_CODEPOINT);
  }

  /**
   * Constructs a StringGeneratorBuilder which will build Strings composed from
   * Unicode Basic Latin Alphabet
   * 
   * @return a StringGeneratorBuilder
   */
  public StringGeneratorBuilder basicLatinAlphabet() {
    return betweenCodePoints(BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT);
  }

  /**
   * Constructs a StringGeneratorBuilder which will build Strings composed from
   * Unicode Ascii Alphabet
   * 
   * @return a StringGeneratorBuilder
   */
  public StringGeneratorBuilder ascii() {
    return betweenCodePoints(Character.MIN_CODE_POINT,
        ASCII_LAST_CODEPOINT);
  }
  
  /**
   * Strings with characters between two (inclusive) code points
   * @param minInclusive minimum code point
   * @param maxInclusive max code point
   * @return Builder for strings
   */
  public StringGeneratorBuilder betweenCodePoints(int minInclusive, int maxInclusive) {
    return new StringGeneratorBuilder(minInclusive,
        maxInclusive);
  }

  /**
   * Constructs a EncodingStringGeneratorBuilder which will build Strings composed from
   * base32 encoding
   *
   * @return a EncodingStringGeneratorBuilder
   */
  public EncodingStringGeneratorBuilder base32() {
    return new EncodingStringGeneratorBuilder(BASE_32);
  }

  /**
   * Constructs a EncodingStringGeneratorBuilder which will build Strings composed from
   * geohash encoding
   *
   * @return a EncodingStringGeneratorBuilder
   */
  public EncodingStringGeneratorBuilder geohash() {
    return new EncodingStringGeneratorBuilder(GEOHASH);
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

  public static class EncodingStringGeneratorBuilder {
    private final char[] domain;

    private EncodingStringGeneratorBuilder(char[] domain) {
      this.domain = domain;
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
      return Generate.charArrays(Generate.range(minLength, maxLength), domain).map(a -> new String(a));
    }
  }

}
