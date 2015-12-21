package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;
import static org.quicktheories.quicktheories.generators.SourceDSL.arbitrary;
import static org.quicktheories.quicktheories.generators.SourceDSL.arrays;
import static org.quicktheories.quicktheories.generators.SourceDSL.bigDecimals;
import static org.quicktheories.quicktheories.generators.SourceDSL.bigIntegers;
import static org.quicktheories.quicktheories.generators.SourceDSL.characters;
import static org.quicktheories.quicktheories.generators.SourceDSL.dates;
import static org.quicktheories.quicktheories.generators.SourceDSL.doubles;
import static org.quicktheories.quicktheories.generators.SourceDSL.floats;
import static org.quicktheories.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.quicktheories.generators.SourceDSL.lists;
import static org.quicktheories.quicktheories.generators.SourceDSL.localDates;
import static org.quicktheories.quicktheories.generators.SourceDSL.longs;
import static org.quicktheories.quicktheories.generators.SourceDSL.strings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

public class SourceDSLTest {

  private static final int LOCAL_DATE_MIN_EPOCH_DAY_COUNT = -999999999;
  private static final int LOCAL_DATE_MAX_EPOCH_DAY_COUNT = 999999999;

  @Test
  public void shouldGenerateLongMaxAndMin() {
    Source<Long> testee = longs().all();
    assertThatSource(testee).generatesAllOf(Long.MAX_VALUE, Long.MIN_VALUE);
  }

  @Test
  public void shouldGenerateLongStartAndEndInclusive() {
    Source<Long> testee = longs().from(-87078).upToAndIncluding(8706);
    assertThatSource(testee).generatesAllOf(-87078l, 8706l);
  }

  @Test
  public void shouldGenerateLongStartAndEndExclusive() {
    Source<Long> testee = longs().from(-87078).upTo(8706);
    assertThatSource(testee).generatesAllOf(-87078l, 8705l);
  }

  @Test
  public void shouldGenerateLongsBetween() {
    Source<Long> testee = longs().between(-87078, 8706);
    assertThatSource(testee).generatesAllOf(-87078l, 8706l);
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnExclusiveLongIntervalWithMaxLessThanMin() {
    try {
      Source<Long> testee = longs().from(-5).upTo(-5);
      fail("Created a long generator where max is less than min!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnExclusiveLongIntervalWithMaxOneMoreThanMin() {
    try {
      Source<Long> testee = longs().from(-5).upTo(-4);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalWithMaxLessThanMin() {
    try {
      Source<Long> testee = longs().from(-5).upToAndIncluding(-6);
      fail("Created a long generator where max is less than min!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveLongIntervalWithMaxEqualToMin() {
    try {
      Source<Long> testee = longs().from(-5).upToAndIncluding(-5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalWithBetweenWithMaxLessThanMin() {
    try {
      Source<Long> testee = longs().between(-5, -6);
      fail("Created a long generator where max is less than min!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveLongIntervalWithBetweenWithMaxEqualToMin() {
    try {
      Source<Long> testee = longs().between(-5, -5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @Test
  public void shouldGenerateIntegerMaxAndMin() {
    Source<Integer> testee = integers().all();
    assertThatSource(testee).generatesAllOf(Integer.MAX_VALUE,
        Integer.MIN_VALUE);
  }

  @Test
  public void shouldGenerateIntegerMaxAnd1() {
    Source<Integer> testee = integers().allPositive();
    assertThatSource(testee).generatesAllOf(Integer.MAX_VALUE, 1);
  }

  @Test
  public void shouldGenerateIntegerStartAndEndInclusive() {
    Source<Integer> testee = integers().from(-87078).upToAndIncluding(8706);
    assertThatSource(testee).generatesAllOf(-87078, 8706);
  }

  @Test
  public void shouldGenerateIntegerStartAndEndExclusive() {
    Source<Integer> testee = integers().from(-87078).upTo(8706);
    assertThatSource(testee).generatesAllOf(-87078, 8705);
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnExclusiveIntervalWithMaxLessThanMin() {
    try {
      Source<Integer> testee = integers().from(-5).upTo(-5);
      fail("Created an integer generator where max is less than min!");
    } catch (IllegalArgumentException expected) {
      assertTrue("Expected exception message to relate to improper interval",
          expected.getMessage()
              .indexOf("There are no Integer values to be generated") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnExclusiveIntervalWithMaxOneMoreThanMin() {
    try {
      Source<Integer> testee = integers().from(-5).upTo(-4);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveIntervalWithMaxLessThanMin() {
    try {
      Source<Integer> testee = integers().from(-5).upToAndIncluding(-6);
      fail("Created an integer generator where max is less than min!");
    } catch (IllegalArgumentException expected) {
      assertTrue("Expected exception message to relate to improper interval",
          expected.getMessage()
              .indexOf("There are no Integer values to be generated") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveIntervalWithMaxEqualToMin() {
    try {
      Source<Integer> testee = integers().from(-5).upToAndIncluding(-5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveIntervalWithBetweenWithMaxLessThanMin() {
    try {
      Source<Integer> testee = integers().between(-5, -6);
      fail("Created an integer generator where max is less than min!");
    } catch (IllegalArgumentException expected) {
      assertTrue("Expected exception message to relate to improper interval",
          expected.getMessage()
              .indexOf("There are no Integer values to be generated") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveIntervalWithBetweenWithMaxEqualToMin() {
    try {
      Source<Integer> testee = integers().between(-5, -5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @Test
  public void shouldGenerateDoubleInfinitiesAndNaN() {
    Source<Double> testee = doubles().allDoubles();
    assertThatSource(testee).generatesAllOf(Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY, Double.MAX_VALUE, Double.NaN);
  }

  @Test
  public void shouldGenerateDoubleInfinities() {
    Source<Double> testee = doubles()
        .fromNegativeInfinityToPositiveInfinity();
    assertThatSource(testee).generatesAllOf(Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY);
  }

  @Test
  public void shouldGenerateDoubleMaxes() {
    Source<Double> testee = doubles()
        .fromNegativeDoubleMaxToPositiveDoubleMax();
    assertThatSource(testee).generatesAllOf(-Double.MAX_VALUE,
        Double.MAX_VALUE);
  }

  @Test
  public void shouldGenerateDoubleNegativeInfinityAndNegativeZero() {
    Source<Double> testee = doubles().fromNegativeInfinityToNegativeZero();
    assertThatSource(testee).generatesAllOf(Double.NEGATIVE_INFINITY, -0d);
  }

  @Test
  public void shouldGeneratorDoubleMinusMaxAndNegativeZero() {
    Source<Double> testee = doubles().fromNegativeDoubleMaxToNegativeZero();
    assertThatSource(testee).generatesAllOf(-Double.MAX_VALUE, -0d);
  }

  @Test
  public void shouldGenerateDoublePositiveInfinityAndZero() {
    Source<Double> testee = doubles().fromZeroToPositiveInfinity();
    assertThatSource(testee).generatesAllOf(Double.POSITIVE_INFINITY, 0d);
  }

  @Test
  public void shouldGenerateDoublePositiveMaxAndZero() {
    Source<Double> testee = doubles().fromZeroToDoubleMax();
    assertThatSource(testee).generatesAllOf(Double.MAX_VALUE, 0d);
  }

  @Test
  public void shouldGenerateDoubleZeroAndOne() {
    Source<Double> testee = doubles().fromZeroToOne();
    assertThatSource(testee).generatesAllOf(0d, 1d);
  }

  @Test
  public void shouldGenerateFloatInfinities() {
    Source<Float> testee = floats().fromNegativeInfinityToPositiveInfinity();
    assertThatSource(testee).generatesAllOf(Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, Float.MAX_VALUE);
  }

  @Test
  public void shouldGenerateFloatNegativeInfinityAndNegativeZero() {
    Source<Float> testee = floats().fromNegativeInfinityToNegativeZero();
    assertThatSource(testee).generatesAllOf(Float.NEGATIVE_INFINITY, -0f);
  }

  @Test
  public void shouldGeneratorFloatMinusMaxAndNegativeZero() {
    Source<Float> testee = floats().fromNegativeFloatMaxToNegativeZero();
    assertThatSource(testee).generatesAllOf(-Float.MAX_VALUE, -0f);
  }

  @Test
  public void shouldGenerateFloatPositiveInfinityAndZero() {
    Source<Float> testee = floats().fromZeroToPositiveInfinity();
    assertThatSource(testee).generatesAllOf(Float.POSITIVE_INFINITY, 0f);
  }

  @Test
  public void shouldGenerateFloatPositiveMaxAndZero() {
    Source<Float> testee = floats().fromZeroToFloatMax();
    assertThatSource(testee).generatesAllOf(Float.MAX_VALUE, 0f);
  }

  @Test
  public void shouldGenerateFloatZeroAndOne() {
    Source<Float> testee = floats().fromZeroToOne();
    assertThatSource(testee).generatesAllOf(0f, 1f);
  }

  @Test
  public void shouldGenerateFloatMaxes() {
    Source<Float> testee = floats().fromNegativeFloatMaxToPositiveFloatMax();
    assertThatSource(testee).generatesAllOf(-Float.MAX_VALUE,
        Float.MAX_VALUE);
  }

  @Test
  public void shouldGenerateFloatInfinitiesAndNaN() {
    Source<Float> testee = floats().allFloats();
    assertThatSource(testee).generatesAllOf(Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, Float.MAX_VALUE, Float.NaN);
  }

  @Test
  public void shouldGenerateExtremeBasicLatinCharacters() {
    Source<Character> testee = characters().basicLatinCharacters();
    assertThatSource(testee).generatesAllOf('\u0020', '\u007E');
  }

  @Test
  public void shouldGenerateExtremeAsciiCharacters() {
    Source<Character> testee = characters().ascii();
    assertThatSource(testee).generatesAllOf('\u0000', '\u007F');
  }

  @Test
  public void shouldGenerateExtremeBMPCharacters() {
    Source<Character> testee = characters().basicMultilingualPlane();
    assertThatSource(testee).generatesAllOf('\u0000', '\ufffd');
  }

  @Test
  public void shouldShrinkBasicLatinCharactersAsExpected() {
    Source<Character> testee = characters().basicLatinCharacters();
    assertThatSource(testee).shrinksValueTo('a', '`');
  }

  @Test
  public void shouldShrinkAsciiCharactersAsExpected() {
    Source<Character> testee = characters().ascii();
    assertThatSource(testee).shrinksValueTo('\u0003', '\u0002');
  }

  @Test
  public void shouldShrinkBMPCharactersAsExpected() {
    Source<Character> testee = characters().basicMultilingualPlane();
    assertThatSource(testee).shrinksValueTo('\u1C3B', '\u1C37',
        new ShrinkContext(0, 1000000, Configuration.defaultPRNG(0)));
  }

  @Test
  public void shouldGenerateMaxAndMinIntegersAsStrings() {
    Source<String> testee = strings().numeric();
    assertThatSource(testee).generatesAllOf(
        Integer.toString(Integer.MAX_VALUE),
        Integer.toString(Integer.MIN_VALUE));
  }

  @Test
  public void shouldGenerateExtremeNumericStrings() {
    Source<String> testee = strings().numericBetween(-30, 5500);
    assertThatSource(testee).generatesAllOf("-30", "5500");
  }

  @Test
  public void shouldShrinkBasicLatinStringsAsExpected() {
    Source<String> testee = strings().basicLatinAlphabet()
        .ofLength(4);
    assertThatSource(testee).shrinksValueTo("bbbb", "aaaa");
  }

  @Test
  public void shouldShrinkAsciiStringsAsExpected() {
    Source<String> testee = strings().ascii().ofFixedNumberOfCodePoints(3);
    assertThatSource(testee).shrinksValueTo("\u0001\u0001\u0003",
        "\u0002\u0002\u0004");
  }

  @Test
  public void shouldShrinkBMPStringsAsExpected() {
    Source<String> testee = strings().basicMultilingualPlaneAlphabet()
        .ofLength(2);
    assertThatSource(testee).shrinksValueTo("\u0021\u31F9", "\u0021\u31F8",
        new ShrinkContext(0, 1000000, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkBoundedStringsAsExpected() {
    Source<String> testee = strings().allPossible().ofLengthBetween(2, 5);
    assertThatSource(testee).shrinksValueTo("aaaa", "aaa");
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingNumericStringsWithMaxLessThanMin() {
    try {
      Source<String> testee = strings().numericBetween(-5, -6);
      fail("Created an String generator where max is less than min!");
    } catch (IllegalArgumentException expected) {
      assertTrue("Expected exception message to relate to improper interval",
          expected.getMessage()
              .indexOf("There are no Integer values to be generated") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingNumericStringsWithMaxEqualToMin() {
    try {
      Source<String> testee = strings().numericBetween(-5, -5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingStringsOfNegativeCodePoints() {
    try {
      Source<String> testee = strings().ascii()
          .ofFixedNumberOfCodePoints(-4);
      fail("Created a string generator with negative codepoints!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingStringsOfZeroCodePoints() {
    try {
      Source<String> testee = strings().ascii()
          .ofFixedNumberOfCodePoints(0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAStringOfNegativeLength() {
    try {
      Source<String> testee = strings().basicMultilingualPlaneAlphabet()
          .ofLength(-7);
      fail("Created a string generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingStringsOfZeroLength() {
    try {
      Source<String> testee = strings().basicMultilingualPlaneAlphabet()
          .ofLength(0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedStringWithMinLengthNegative() {
    try {
      Source<String> testee = strings().basicLatinAlphabet()
          .ofLengthBetween(-2, 6);
      fail("Created a string generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingBoundedStringsWithMinLenghtOfZeroLength() {
    try {
      Source<String> testee = strings().basicLatinAlphabet()
          .ofLengthBetween(0, 6);
      ;
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedStringWithMaxLengthNegative() {
    try {
      Source<String> testee = strings().basicLatinAlphabet()
          .ofLengthBetween(5, -6);
      fail("Created a string generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedStringWithMaxLengthLessThanMinLength() {
    try {
      Source<String> testee = strings().basicLatinAlphabet()
          .ofLengthBetween(2, 0);
      fail("Created a string generator with maxLength smaller than minLength!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingBoundedStringsWithMinLengthEqualToMaxLength() {
    try {
      Source<String> testee = strings().basicLatinAlphabet()
          .ofLengthBetween(0, 0);
      ;
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @Test
  public void shouldShrinkFixedSizedListsAsExpected() {
    Source<List<String>> testee = lists()
        .allListsOf(strings().numeric())
        .ofSize(5);
    assertThatSource(testee).shrinksValueTo(
        java.util.Arrays.asList("5", "8", "-45", "60", "0"),
        java.util.Arrays.asList("4", "7", "-44", "59", "0"));
  }

  @Test
  public void shouldShrinkLinkedBoundedSizedListsAsExpected() {
    Source<List<Integer>> testee = lists()
        .linkedListsOf(integers().allPositive()).ofSizeBetween(3, 6);
    assertThatSource(testee).shrinksValueTo(
        java.util.Arrays.asList(1, 1, 1, 1, 1),
        java.util.Arrays.asList(1, 1, 1, 1));
  }

  @Test
  public void shouldShrinkBoundedNonSpecifiedLIstAsExpected() {
    Source<List<Integer>> testee = lists().allListsOf(integers().all())
        .ofSizeBetween(3, 6);
    assertThatSource(testee).shrinksValueTo(
        java.util.Arrays.asList(1, 1, 1, 1, 1),
        java.util.Arrays.asList(1, 1, 1, 1));
  }

  @Test
  public void shouldShrinkFixedSizedArrayListsAsExpected() {
    Source<List<Long>> testee = lists().arrayListsOf(longs().between(0, 35))
        .ofSize(2);
    assertThatSource(testee).shrinksValueTo(java.util.Arrays.asList(1l, 9l),
        java.util.Arrays.asList(0l, 8l));
  }

  @Test
  public void shouldSetTypeOfList() {
    Source<List<Integer>> testee = lists().allListsOf(integers().all())
        .ofType(lists().createListCollector(LinkedList::new)).ofSize(4);
    assertThatSource(testee).shrinksConformTo(
        new LinkedList<Integer>(java.util.Arrays.asList(4, 4, 2)),
        list -> list instanceof LinkedList == true,
        new ShrinkContext(0, 20, Configuration.defaultPRNG(2)));
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAListOfNegativeSize() {
    try {
      Source<List<Integer>> testee = lists()
          .allListsOf(integers().allPositive()).ofSize(-3);
      fail("Created a list generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingListsOfZeroSize() {
    try {
      Source<List<Integer>> testee = lists()
          .allListsOf(integers().allPositive()).ofSize(0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingALinkedListOfNegativeSize() {
    try {
      Source<List<Integer>> testee = lists()
          .linkedListsOf(integers().allPositive()).ofSize(-3);
      fail("Created a list generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingBoundedListsWithMinNegative() {
    try {
      Source<List<Integer>> testee = lists()
          .arrayListsOf(integers().allPositive()).ofSizeBetween(-3, 6);
      fail("Created a list generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingBoundedListsOfMinSizeEqualToZero() {
    try {
      Source<List<Integer>> testee = lists()
          .arrayListsOf(integers().allPositive()).ofSizeBetween(0, 6);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedListWithMaxNegative() {
    try {
      Source<List<Integer>> testee = lists()
          .linkedListsOf(integers().allPositive()).ofSizeBetween(-3, -2);
      fail("Created a list generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedListWithMaxSmallerThanMin() {
    try {
      Source<List<Integer>> testee = lists()
          .allListsOf(integers().allPositive()).ofSizeBetween(2, 0);
      fail("Created a list generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingBoundedListsOfMinSizeEqualToMaxSize() {
    try {
      Source<List<Integer>> testee = lists()
          .allListsOf(integers().allPositive()).ofSizeBetween(0, 0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @Test
  public void shouldShrinkCharacterFixedArraysAsExpected() {
    Source<Character[]> testee = arrays()
        .ofCharacters(characters().basicLatinCharacters()).withLength(4);
    assertThatSource(testee).shrinksArrayValueTo(
        new Character[] { '!', '!', '!', '!' },
        new Character[] { ' ', ' ', ' ', ' ' },
        new ShrinkContext(0, 5, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkStringBoundedArraysAsExpected() {
    Source<String[]> testee = arrays().ofStrings(strings().numeric())
        .withLengthBetween(2, 4);
    assertThatSource(testee).shrinksArrayValueTo(
        new String[] { "45", "45", "45" }, new String[] { "45", "45" },
        new ShrinkContext(0, 5, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkIntegerArrayAsExpected() {
    Source<Integer[]> testee = arrays().ofIntegers(integers().allPositive())
        .withLength(3);
    assertThatSource(testee).shrinksArrayValueTo(
        new Integer[] { 45, 45, 45 }, new Integer[] { 44, 44, 44 },
        new ShrinkContext(0, 50, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkSpecifiedClassArrayAsExpected() {
    Source<Long[]> testee = arrays()
        .ofClass(longs().between(-35, 700), Long.class).withLength(3);
    assertThatSource(testee).shrinksArrayValueTo(
        new Long[] { 45l, 45l, 45l }, new Long[] { 44l, 44l, 44l },
        new ShrinkContext(0, 50, Configuration.defaultPRNG(2)));
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnArrayOfNegativeSize() {
    try {
      Source<Integer[]> testee = arrays()
          .ofIntegers(integers().allPositive()).withLength(-3);
      fail("Created an array generator with negative length!");
    } catch (IllegalArgumentException expected) {
      assertTrue("Expected exception message to relate to negative length",
          expected.getMessage()
              .indexOf("The length of an array cannot be negative") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingArraysOfZeroLength() {
    try {
      Source<Integer[]> testee = arrays()
          .ofIntegers(integers().allPositive()).withLength(0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedArrayWithMinNegative() {
    try {
      Source<Integer[]> testee = arrays()
          .ofIntegers(integers().allPositive()).withLengthBetween(-3, 6);
      fail("Created an array generator with negative length!");
    } catch (IllegalArgumentException expected) {
      assertTrue("Expected exception message to relate to negative length",
          expected.getMessage()
              .indexOf("The length of an array cannot be negative") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingBoundedArraysWithMinLengthEqualToZero() {
    try {
      Source<Integer[]> testee = arrays()
          .ofIntegers(integers().allPositive()).withLengthBetween(0, 6);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedArrayWithMaxNegative() {
    try {
      Source<Integer[]> testee = arrays()
          .ofIntegers(integers().allPositive()).withLengthBetween(-3, -2);
      fail("Created an array generator with negative length!");
    } catch (IllegalArgumentException expected) {
      assertTrue("Expected exception message to relate to negative length",
          expected.getMessage()
              .indexOf("The length of an array cannot be negative") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedArrayWithMaxSmallerThanMin() {
    try {
      Source<Integer[]> testee = arrays()
          .ofIntegers(integers().allPositive()).withLengthBetween(2, 0);
      fail("Created an array generator with negative length!");
    } catch (IllegalArgumentException expected) {
      assertTrue(
          "Expected exception message to relate to minLength longer than maxLength",
          expected.getMessage()
              .indexOf("is longer than the maxLength") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingBoundedArraysWithMinLengthEqualToMaxLength() {
    try {
      Source<Integer[]> testee = arrays()
          .ofIntegers(integers().allPositive()).withLengthBetween(0, 0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @Test
  public void shouldShrinkBigIntegersAsExpected() {
    Source<BigInteger> testee = bigIntegers().ofBytes(7);
    BigInteger original = new BigInteger("7097809");
    assertThatSource(testee).shrinksConformTo(original,
        i -> original.abs().compareTo(i.abs()) != -1,
        new ShrinkContext(0, 50, Configuration.defaultPRNG(2)));
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABigIntegerOfNegativeNumberOfBytes() {
    try {
      Source<BigInteger> testee = bigIntegers().ofBytes(0);
      fail("Created an BigInteger generator with improper length byte array!");
    } catch (IllegalArgumentException expected) {
      assertTrue(
          "Expected exception message to relate to negative length byte array",
          expected.getMessage()
              .indexOf(
                  "The length of this array cannot be less than one") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingABigIntegerWithOneByte() {
    try {
      Source<BigInteger> testee = bigIntegers().ofBytes(1);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @Test
  public void shouldShrinkBigDecimalsAsExpected() {
    Source<BigDecimal> testee = bigDecimals().ofBytes(12).withScale(3);
    BigDecimal original = new BigDecimal("709879689609.342");
    assertThatSource(testee).shrinksConformTo(original,
        i -> original.abs().compareTo(i.abs()) != -1,
        new ShrinkContext(0, 50, Configuration.defaultPRNG(2)));
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABigDecimalOfNegativeNumberOfBytes() {
    try {
      Source<BigDecimal> testee = bigDecimals().ofBytes(0).withScale(2);
      fail("Created an BigDecimal generator with improper length byte array!");
    } catch (IllegalArgumentException expected) {
      assertTrue(
          "Expected exception message to relate to negative length byte array",
          expected.getMessage()
              .indexOf(
                  "The length of this array cannot be less than one") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingABigDecimalWithOneByte() {
    try {
      Source<BigDecimal> testee = bigDecimals().ofBytes(1).withScale(2);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @Test
  public void shouldNotShrinkArbitraryConstant() {
    Source<Integer> testee = arbitrary().constant(42);
    assertThatSource(testee).cannotShrink(42);
  }

  @Test
  public void shouldShrinkTowardsFirstItemInPickedList() {
    Source<String> testee = arbitrary()
        .pick(java.util.Arrays.asList("a", "b", "c"));
    assertThatSource(testee).shrinksValueTo("c", "b");
  }

  @Test
  public void shouldShrinkTowardsFirstItemInPickedSequence() {
    Source<String> testee = arbitrary().pick("a", "b", "c");
    assertThatSource(testee).shrinksValueTo("c", "b");
  }

  @Test
  public void shouldShrinkTowardsFirstItemInSequencedList() {
    Source<String> testee = arbitrary()
        .sequence(java.util.Arrays.asList("a", "b", "c"));
    assertThatSource(testee).shrinksValueTo("c", "b");
  }

  @Test
  public void shouldShrinkTowardsFirstItemInSequencedSequence() {
    Source<String> testee = arbitrary().sequence("a", "b", "c");
    assertThatSource(testee).shrinksValueTo("c", "b");
  }

  @Test
  public void shouldShrinkEnumsTowardsFirstDefinedConstant() {
    Source<AnEnum> testee = arbitrary().enumValues(AnEnum.class);
    assertThatSource(testee).shrinksValueTo(AnEnum.C, AnEnum.B);
  }

  @Test
  public void shouldShrinkTowardsFirstItemInReversedSequence() {
    Source<String> testee = arbitrary().reverse("a", "b", "c");
    assertThatSource(testee).shrinksValueTo("c", "b");
  }

  static enum AnEnum {
    A, B, C, D, E;
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingADateOfNegativeLong() {
    try {
      Source<Date> testee = dates().withMilliseconds(-234);
      fail("Created a date with a negative number of milliseconds");
    } catch (IllegalArgumentException expected) {
      assertTrue(
          "Expected exception message to relate to negative long argument",
          expected.getMessage()
              .indexOf(
                  "not an accepted number of milliseconds") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingADateWithLongOfZero() {
    try {
      Source<Date> testee = dates().withMilliseconds(0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalForDatesWithMaxLessThanMin() {
    try {
      Source<Date> testee = dates().withMillisecondsBetween(342, 3);
      fail("Created a Date where max long is less than min long!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveLongIntervalForDatesWithMaxEqualToMin() {
    try {
      Source<Date> testee = dates().withMillisecondsBetween(352, 352);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalForDatesWithMinLessThanZero() {
    try {
      Source<Date> testee = dates().withMillisecondsBetween(-5, 6);
      fail("Created a Date where min long is less than zero!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveLongIntervalForDatesWithMinEqualToZero() {
    try {
      Source<Date> testee = dates().withMillisecondsBetween(0, 5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @Test
  public void shouldGenerateDateMax() {
    Source<Date> testee = dates().withMilliseconds(7890789);
    assertThatSource(testee).generatesAllOf(new Date(7890789));
  }

  @Test
  public void shouldGenerateDateAtStartAndEndInclusive() {
    Source<Date> testee = dates().withMillisecondsBetween(3245352,
        72938572398752l);
    assertThatSource(testee).generatesAllOf(new Date(3245352),
        new Date(72938572398752l));
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingALocalDateBelowMinEpochDayCount() {
    try {
      Source<LocalDate> testee = localDates()
          .withDays(LOCAL_DATE_MIN_EPOCH_DAY_COUNT - 1);
      fail("Created a localDate with an improper value");
    } catch (IllegalArgumentException expected) {
      assertTrue(
          "Expected exception message to relate to out of suitable range long argument",
          expected.getMessage()
              .indexOf(
                  "The long value representing the number of days from the epoch must be bounded") >= 0);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingALocalDateAboveMaxEpochDayCount() {
    try {
      Source<LocalDate> testee = localDates()
          .withDays(LOCAL_DATE_MAX_EPOCH_DAY_COUNT + 1);
      fail("Created a localDate with an improper value");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingALocalDateAtMinEpochDayCount() {
    try {
      Source<LocalDate> testee = localDates()
          .withDays(LOCAL_DATE_MIN_EPOCH_DAY_COUNT);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingALocalDateAtMaxEpochDayCount() {
    try {
      Source<LocalDate> testee = localDates()
          .withDays(LOCAL_DATE_MAX_EPOCH_DAY_COUNT);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalForLocalDatesWithMaxLessThanMin() {
    try {
      Source<LocalDate> testee = localDates().withDaysBetween(342, 3);
      fail("Created a localDate where max long is less than min long!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveLongIntervalForLocalDatesWithMaxEqualToMin() {
    try {
      Source<LocalDate> testee = localDates().withDaysBetween(352, 352);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalForLocalDatesWithMinLessThanMinEpochDayCount() {
    try {
      Source<LocalDate> testee = localDates().withDaysBetween(
          LOCAL_DATE_MIN_EPOCH_DAY_COUNT - 1,
          LOCAL_DATE_MAX_EPOCH_DAY_COUNT + 1);
      fail("Created a Date where min long is less than zero!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveLongIntervalForDatesWithMinEqualToMinEpochDayCount() {
    try {
      Source<LocalDate> testee = localDates().withDaysBetween(
          LOCAL_DATE_MIN_EPOCH_DAY_COUNT, LOCAL_DATE_MAX_EPOCH_DAY_COUNT);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @Test
  public void shouldGenerateLocalDateMax() {
    Source<LocalDate> testee = localDates().withDays(7890789);
    assertThatSource(testee).generatesAllOf(LocalDate.ofEpochDay(7890789));
  }

  @Test
  public void shouldGenerateLocalDateAtStartAndEndInclusive() {
    Source<LocalDate> testee = localDates().withDaysBetween(3245352, 729385723);
    assertThatSource(testee).generatesAllOf(LocalDate.ofEpochDay(3245352),
        LocalDate.ofEpochDay(729385723));
  }

}
