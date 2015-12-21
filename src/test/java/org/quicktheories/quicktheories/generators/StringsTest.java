package org.quicktheories.quicktheories.generators;

import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

public class StringsTest {

  private static final int BASIC_LATIN_LAST_CODEPOINT = 0x007E;
  private static final int BASIC_LATIN_FIRST_CODEPOINT = 0x0020;

  @Test
  public void shouldNotShrinkNumericStringConsistingOfZero() {
    Source<String> testee = Strings.boundedNumericStrings(Integer.MIN_VALUE,
        Integer.MAX_VALUE);
    assertThatSource(testee).cannotShrink("0");
  }

  @Test
  public void shouldShrinkPositiveNumericStringByOneIntegerWhenRemainingCyclesGreaterThanDomainSize() {
    Source<String> testee = Strings.boundedNumericStrings(Integer.MIN_VALUE,
        Integer.MAX_VALUE);
    assertThatSource(testee).shrinksValueTo("65", "64", someShrinkContext());
  }

  @Test
  public void shouldShrinkNegativeNumericStringByOneIntegerWhenRemainingCyclesGreaterThanDomainSize() {
    Source<String> testee = Strings.boundedNumericStrings(-100, -50);
    assertThatSource(testee).shrinksValueTo("-65", "-64",
        someShrinkContext());
  }

  @Test
  public void shouldShrinkPositiveNumericStringWhenRemainingCyclesLessThanDomainSize() {
    Source<String> testee = Strings.boundedNumericStrings(Integer.MIN_VALUE,
        Integer.MAX_VALUE);
    String original = "13253";
    Predicate<String> stringShrinksInRightDirection = (
        i) -> Integer.parseInt(i) <= Integer.parseInt(original)
            && Integer.parseInt(i) >= 0;
    assertThatSource(testee).shrinksConformTo(original,
        stringShrinksInRightDirection, someShrinkContext());
  }

  @Test
  public void shouldShrinkNegativeNumericStringWhenRemainingCyclesLessThanDomainSize() {
    Source<String> testee = Strings.boundedNumericStrings(-Integer.MIN_VALUE,
        -50);
    String original = "-13253";
    Predicate<String> stringShrinksInRightDirection = (
        i) -> Integer.parseInt(i) >= Integer.parseInt(original)
            && Integer.parseInt(i) <= 0;
    assertThatSource(testee).shrinksConformTo(original,
        stringShrinksInRightDirection, someShrinkContext());
  }

  @Test
  public void shouldReturnEmptyStringIfFixedLengthSetToZero() {
    Source<String> testee = Strings.ofBoundedLengthStrings(
        BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT, 0, 0);
    assertThatSource(testee).generatesAllOf("");
  }

  @Test
  public void shouldNotShrinkStringOfZeroCodePoints() {
    Source<String> testee = Strings.ofFixedNumberOfCodePointsStrings(
        BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT, 0);
    assertThatSource(testee).cannotShrink("");
  }

  @Test
  public void shouldNotShrinkStringOfLengthZero() {
    Source<String> testee = Strings.ofBoundedLengthStrings(
        BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT, 0, 0);
    assertThatSource(testee).cannotShrink("");
  }

  @Test
  public void shouldShrinkExclamationStringBasicLatinAlphabetFixedCodePointStringToItself() {
    Source<String> testee = Strings.ofFixedNumberOfCodePointsStrings(
        BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT, 4);
    assertThatSource(testee).shrinksValueTo("!!!!", "!!!!",
        someShrinkContext());
  }

  @Test
  public void shouldShrinkFixedBasicLatinAlphabetStringWhenRemainingCyclesGreaterThanDomainSize() {
    Source<String> testee = Strings.ofFixedNumberOfCodePointsStrings(
        BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT, 8);
    assertThatSource(testee).shrinksValueTo("Agji{{&C", "@fihzz%B",
        new ShrinkContext(0, 1000, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldShrinkFixedBasicLatinAlphabetStringWhenRemainingCyclesLessThanDomainSize() {
    Source<String> testee = Strings.ofFixedNumberOfCodePointsStrings(
        BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT, 8);
    assertThatSource(testee).shrinksConformTo("Agji{{&C",
        allLatinCharactersShrink(0x0020, "Agji{{&C"),
        new ShrinkContext(0, 1, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldShrinkNullStringUpwards() {
    Source<String> testee = Strings.ofFixedNumberOfCodePointsStrings(
        Character.MIN_CODE_POINT, Character.MAX_CODE_POINT, 4);
    assertThatSource(testee).shrinksValueTo("\u0000\u0000\u0000\u0000",
        "\u0001\u0001\u0001\u0001", someShrinkContext());
  }

  @Test
  public void shouldShrinkBMPInAllPossibleWhenRemainingCyclesGreaterThanDomainSize() {
    Source<String> testee = Strings.ofFixedNumberOfCodePointsStrings(
        Character.MIN_CODE_POINT, Character.MAX_CODE_POINT, 9);
    assertThatSource(testee).shrinksValueTo("Agji\u0000{{&C",
        "@fih\u0001zz%B",
        new ShrinkContext(0, 100000000, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldShrinkSupplementaryWhenRemainingCyclesGreaterThanDomainSize() {
    Source<String> testee = Strings.ofFixedNumberOfCodePointsStrings(
        Character.MIN_CODE_POINT, Character.MAX_CODE_POINT, 3);
    assertThatSource(testee).shrinksValueTo("\ud800\udf30\u0000\ud800\udf23",
        "\ud800\udf23\u0001\ud800\udf22",
        new ShrinkContext(0, 100000000, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldShrinkBasicLatinAlphabetStringOfFixedLength() {
    Source<String> testee = Strings.ofBoundedLengthStrings(
        BASIC_LATIN_FIRST_CODEPOINT,
        BASIC_LATIN_LAST_CODEPOINT, 8, 8);
    assertThatSource(testee).shrinksConformTo("Agji{{&C",
        allLatinCharactersShrink(0x0020, "Agji{{&C"),
        new ShrinkContext(0, 1, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldShrinkAllPossibleWhenRemainingCyclesGreaterThanDomainSize() {
    Source<String> testee = Strings.ofBoundedLengthStrings(
        Character.MIN_CODE_POINT,
        Character.MAX_CODE_POINT, 5, 5);
    assertThatSource(testee).shrinksValueTo("\ud800\udc00\u0001\ud800\udc00",
        "\ufffd\ufffd\u0002\ufffd\ufffd",
        new ShrinkContext(0, 100000000, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldNotShrinkToAnUndefinedCharacterInStringWhenRemainingCyclesGreaterThanDomainSize() {
    Source<String> testee = Strings.ofBoundedLengthStrings(
        Character.MIN_CODE_POINT,
        Character.MAX_CODE_POINT, 5, 5);
    assertThatSource(testee).shrinksValueTo("\ud800\udc00\u0020\ud801\udca0",
        "\ufffd\ufffd\u0021\ud801\udc9d",
        new ShrinkContext(0, 100000000, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldNotShrinkTheEmptyStringInBoundedString() {
    Source<String> testee = Strings.ofBoundedLengthStrings(
        BASIC_LATIN_FIRST_CODEPOINT, BASIC_LATIN_LAST_CODEPOINT, 0, 14);
    assertThatSource(testee).cannotShrink("");
  }

  @Test
  public void shouldShrinkAnySupplementaryCharacterToLargestSingleCodePointInBoundedDownString() {
    Source<String> testee = Strings.ofBoundedLengthStrings(
        Character.MIN_CODE_POINT,
        Character.MAX_CODE_POINT, 1, 2);
    assertThatSource(testee).shrinksValueTo("\ud81b\udf33", "\ufffd");
  }

  @Test
  public void shouldShrinkStringsThatAreOfMinimumLengthAsIfFixedLengthStrings() {
    Source<String> testee = Strings.ofBoundedLengthStrings(
        Character.MIN_CODE_POINT,
        Character.MAX_CODE_POINT, 5, 8);
    assertThatSource(testee).shrinksValueTo("\ud800\udc00\u0001\ud800\udc00",
        "\ufffd\ufffd\u0002\ufffd\ufffd",
        new ShrinkContext(0, 100000000, Configuration.defaultPRNG(0)));
  }

  private ShrinkContext someShrinkContext() {
    return new ShrinkContext(0, 100, Configuration.defaultPRNG(0));
  }

  private static Predicate<String> allLatinCharactersShrink(
      int smallestCodePoint, String origin) {
    return string -> {
      List<Integer> original = origin.codePoints().boxed()
          .collect(Collectors.toList());
      List<Integer> shrunk = string.codePoints().boxed()
          .collect(Collectors.toList());
      for (int i = 0; i < original.size(); i++) {
        if (!(shrunk.get(i) <= original.get(i))) {
          return false;
        }
      }
      return true;

    };
  }

}
