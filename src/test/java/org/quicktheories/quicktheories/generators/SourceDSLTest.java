package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.quicktheories.quicktheories.generators.SourceDSL.arbitrary;
import static org.quicktheories.quicktheories.generators.SourceDSL.arrays;
import static org.quicktheories.quicktheories.generators.SourceDSL.bigDecimals;
import static org.quicktheories.quicktheories.generators.SourceDSL.bigIntegers;
import static org.quicktheories.quicktheories.generators.SourceDSL.booleans;
import static org.quicktheories.quicktheories.generators.SourceDSL.characters;
import static org.quicktheories.quicktheories.generators.SourceDSL.dates;
import static org.quicktheories.quicktheories.generators.SourceDSL.doubles;
import static org.quicktheories.quicktheories.generators.SourceDSL.floats;
import static org.quicktheories.quicktheories.generators.SourceDSL.integers;
import static org.quicktheories.quicktheories.generators.SourceDSL.lists;
import static org.quicktheories.quicktheories.generators.SourceDSL.localDates;
import static org.quicktheories.quicktheories.generators.SourceDSL.longs;
import static org.quicktheories.quicktheories.generators.SourceDSL.strings;
import static org.quicktheories.quicktheories.impl.GenAssert.assertThatGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Gen;

public class SourceDSLTest {

  private static final int LOCAL_DATE_MIN_EPOCH_DAY_COUNT = -999999999;
  private static final int LOCAL_DATE_MAX_EPOCH_DAY_COUNT = 999999999;

  @Test
  public void shouldGenerateLongMaxAndMin() {
    Gen<Long> testee = longs().all();
    assertThatGenerator(testee).generatesTheMinAndMax(Long.MIN_VALUE, Long.MAX_VALUE);
  }

  @Test
  public void shouldGenerateLongStartAndEndInclusive() {
    Gen<Long> testee = longs().from(-87078).upToAndIncluding(8706);
    assertThatGenerator(testee).generatesTheMinAndMax(-87078L, 8706L);
  }

  @Test
  public void shouldGenerateLongStartAndEndExclusive() {
    Gen<Long> testee = longs().from(-87078).upTo(8706);
    assertThatGenerator(testee).generatesTheMinAndMax(-87078L, 8705L);
  }

  @Test
  public void shouldGenerateLongsBetween() {
    Gen<Long> testee = longs().between(-87078, 8706);
    assertThatGenerator(testee).generatesTheMinAndMax(-87078L, 8706L);   
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnExclusiveLongIntervalWithMaxLessThanMin() {
    try {
      Gen<Long> testee = longs().from(-5).upTo(-5);
      fail("Created a long generator where max is less than min!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnExclusiveLongIntervalWithMaxOneMoreThanMin() {
    try {
      Gen<Long> testee = longs().from(-5).upTo(-4);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalWithMaxLessThanMin() {
    try {
      Gen<Long> testee = longs().from(-5).upToAndIncluding(-6);
      fail("Created a long generator where max is less than min!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveLongIntervalWithMaxEqualToMin() {
    try {
      Gen<Long> testee = longs().from(-5).upToAndIncluding(-5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalWithBetweenWithMaxLessThanMin() {
    try {
      Gen<Long> testee = longs().between(-5, -6);
      fail("Created a long generator where max is less than min!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveLongIntervalWithBetweenWithMaxEqualToMin() {
    try {
      Gen<Long> testee = longs().between(-5, -5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @Test
  public void shouldGenerateIntegerMaxAndMin() {
    Gen<Integer> testee = integers().all();
    assertThatGenerator(testee).generatesTheMinAndMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  @Test
  public void shouldGenerateIntegerMaxAnd1() {
    Gen<Integer> testee = integers().allPositive();
    assertThatGenerator(testee).generatesTheMinAndMax(1, Integer.MAX_VALUE);
  }

  @Test
  public void shouldGenerateIntegerStartAndEndInclusive() {
    Gen<Integer> testee = integers().from(-87078).upToAndIncluding(8706);
    assertThatGenerator(testee).generatesTheMinAndMax(-87078, 8706);
  }

  @Test
  public void shouldGenerateIntegerStartAndEndExclusive() {
    Gen<Integer> testee = integers().from(-87078).upTo(8706);
    assertThatGenerator(testee).generatesTheMinAndMax(-87078, 8705);
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnExclusiveIntervalWithMaxLessThanMin() {
    try {
      Gen<Integer> testee = integers().from(-5).upTo(-5);
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
      Gen<Integer> testee = integers().from(-5).upTo(-4);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveIntervalWithMaxLessThanMin() {
    try {
      Gen<Integer> testee = integers().from(-5).upToAndIncluding(-6);
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
      Gen<Integer> testee = integers().from(-5).upToAndIncluding(-5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveIntervalWithBetweenWithMaxLessThanMin() {
    try {
      Gen<Integer> testee = integers().between(-5, -6);
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
      Gen<Integer> testee = integers().between(-5, -5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }


  @Test
  public void shouldGeneratePositiveDoublesBetweenZeroAndInfinity() {
    Gen<Double> testee = doubles().positive();
    assertThatGenerator(testee).generatesTheMinAndMax(0.0d,
        Double.POSITIVE_INFINITY);
  }

  @Test
  public void shouldGenerateDistrinctPositiveDoubles() {
    Gen<Double> testee = doubles().positive();
    assertThatGenerator(testee).generatesAtLeastNDistinctValues(1000);
  }  

  @Test
  public void shouldGenerateNegativeDoublesBetweenZeroAndInfinity() {
    Gen<Double> testee = doubles().negative();
    assertThatGenerator(testee).generatesTheMinAndMax(-0d, Double.NEGATIVE_INFINITY);
  }

  @Test
  public void shouldGenerateDistrinctNegativeDoubles() {
    Gen<Double> testee = doubles().negative();
    assertThatGenerator(testee).generatesAtLeastNDistinctValues(1000);
  }  

  
  
  @Test
  public void shouldGenerateDoublePositiveInfinityAndZero() {
    Gen<Double> testee = doubles().positive();
    assertThatGenerator(testee).generatesTheMinAndMax(0d, Double.POSITIVE_INFINITY);
  }

  @Test
  public void shouldGenerateDoubleZeroAndOne() {
    Gen<Double> testee = doubles().fromZeroToOne();
    assertThatGenerator(testee).generatesTheMinAndMax(0d, 1d);
  }

  @Test
  public void shouldGenerateFloatNegativeInfinityAndNegativeZero() {
    Gen<Float> testee = floats().negative();
    assertThatGenerator(testee).generatesTheMinAndMax(-0f, Float.NEGATIVE_INFINITY);
  }

  @Test
  public void shouldGenerateDistinctFloats() {
    Gen<Float> testee = floats().any();
    assertThatGenerator(testee).generatesAtLeastNDistinctValues(1000);
  }

  @Test
  public void shouldGenerateFloatPositiveInfinityAndZero() {
    Gen<Float> testee = floats().positive();
    assertThatGenerator(testee).generatesTheMinAndMax(0f,Float.POSITIVE_INFINITY);
  }

  @Test
  public void shouldGenerateFloatZeroAndOne() {
    Gen<Float> testee = floats().fromZeroToOne();
    assertThatGenerator(testee).generatesTheMinAndMax(0f, 1f);
  }

  @Test
  public void shouldGenerateExtremeBasicLatinCharacters() {
    Gen<Character> testee = characters().basicLatinCharacters();
    assertThatGenerator(testee).generatesAllOf('\u0020', '\u007E');
  }

  @Test
  public void shouldGenerateAllBasicLatinCharacter() {
    Gen<Character> testee = characters().basicLatinCharacters();
    assertThatGenerator(testee).generatesAllDistinctValuesBetween('\u0020', '\u007E');
  }
  
  @Test
  public void shouldGenerateExtremeAsciiCharacters() {
    Gen<Character> testee = characters().ascii();
    assertThatGenerator(testee).generatesAllOf('\u0000', '\u007F');
  }

  @Test
  public void shouldGenerateAllAsciiCharacters() {
    Gen<Character> testee = characters().ascii();
    assertThatGenerator(testee).generatesAllDistinctValuesBetween('\u0000', '\u007F');
  }
  
  @Test
  public void shouldGenerateExtremeBMPCharacters() {
    Gen<Character> testee = characters().basicMultilingualPlane();
    assertThatGenerator(testee).generatesTheMinAndMax('\u0000', '\ufffd');
  }

  @Test
  public void shouldShrinkBasicLatinCharactersTowardsExclaimationMark() {
    Gen<Character> testee = characters().basicLatinCharacters();
    assertThatGenerator(testee).shrinksTowards('!');
  }

  @Test
  public void shouldShrinkAsciiCharactersTowardsExclaimationMark() {
    Gen<Character> testee = characters().ascii();
    assertThatGenerator(testee).shrinksTowards('!');
  }

  @Test
  public void shouldGenerateMaxAndMinIntegersAsStrings() {
    Gen<String> testee = strings().numeric();
    assertThatGenerator(testee).generatesTheMinAndMax(
        Integer.toString(Integer.MIN_VALUE),
        Integer.toString(Integer.MAX_VALUE));
  }

  @Test
  public void shouldGenerateExtremeNumericStrings() {
    Gen<String> testee = strings().numericBetween(-30, 5500);
    assertThatGenerator(testee).generatesTheMinAndMax("-30", "5500");
  }

  @Test
  public void shouldShrinkBasicLatinStringsTowardsExclaimation() {
    Gen<String> testee = strings().basicLatinAlphabet()
        .ofLength(4);
    assertThatGenerator(testee).shrinksTowards("!!!!");
  }

  @Test
  public void shouldShrinkAsciiStringsAsExpected() {
    Gen<String> testee = strings().ascii().ofFixedNumberOfCodePoints(3);
    assertThatGenerator(testee).shrinksTowards("!!!");
  }

  @Test
  public void shouldShrinkBoundedStringsAsExpected() {
    Gen<String> testee = strings().allPossible().ofLengthBetween(2, 5);
    assertThatGenerator(testee).shrinksTowards("!!");
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingNumericStringsWithMaxLessThanMin() {
    try {
      Gen<String> testee = strings().numericBetween(-5, -6);
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
      Gen<String> testee = strings().numericBetween(-5, -5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingStringsOfNegativeCodePoints() {
    try {
      Gen<String> testee = strings().ascii()
          .ofFixedNumberOfCodePoints(-4);
      fail("Created a string generator with negative codepoints!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingStringsOfZeroCodePoints() {
    try {
      Gen<String> testee = strings().ascii()
          .ofFixedNumberOfCodePoints(0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAStringOfNegativeLength() {
    try {
      Gen<String> testee = strings().basicMultilingualPlaneAlphabet()
          .ofLength(-7);
      fail("Created a string generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingStringsOfZeroLength() {
    try {
      Gen<String> testee = strings().basicMultilingualPlaneAlphabet()
          .ofLength(0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedStringWithMinLengthNegative() {
    try {
      Gen<String> testee = strings().basicLatinAlphabet()
          .ofLengthBetween(-2, 6);
      fail("Created a string generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingBoundedStringsWithMinLenghtOfZeroLength() {
    try {
      Gen<String> testee = strings().basicLatinAlphabet()
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
      Gen<String> testee = strings().basicLatinAlphabet()
          .ofLengthBetween(5, -6);
      fail("Created a string generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedStringWithMaxLengthLessThanMinLength() {
    try {
      Gen<String> testee = strings().basicLatinAlphabet()
          .ofLengthBetween(2, 0);
      fail("Created a string generator with maxLength smaller than minLength!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingBoundedStringsWithMinLengthEqualToMaxLength() {
    try {
      Gen<String> testee = strings().basicLatinAlphabet()
          .ofLengthBetween(0, 0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @Test
  public void shouldShrinkFixedSizedListContents() {
    Gen<List<String>> testee = lists()
        .of(strings().numeric())
        .ofSize(5);
    assertThatGenerator(testee).shrinksTowards(
        java.util.Arrays.asList("0", "0", "0", "0", "0"));
  }

  @Test
  public void shouldShrinkLinkedBoundedSizedListsWithinBounds() {
    Gen<List<Integer>> testee = lists().of(integers().allPositive()).ofSizeBetween(3, 6);
    assertThatGenerator(testee).shrinksTowards(
        java.util.Arrays.asList(1, 1, 1));
  }

  @Test
  public void shouldShrinkBoundedNonSpecifiedListAsExpected() {
    Gen<List<Integer>> testee = lists().of(integers().all())
        .ofSizeBetween(3, 6);
    assertThatGenerator(testee).shrinksTowards(
        java.util.Arrays.asList(0, 0, 0));
  }

  @Test
  public void shouldShrinkFixedSizedArrayListsAsExpected() {
    Gen<List<Long>> testee = lists().of(longs().between(0, 35))
        .ofType(lists().arrayList())
        .ofSize(2);
    assertThatGenerator(testee).shrinksTowards(java.util.Arrays.asList(0L, 0L));
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAListOfNegativeSize() {
    try {
      Gen<List<Integer>> testee = lists()
          .of(integers().allPositive()).ofSize(-3);
      fail("Created a list generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingListsOfZeroSize() {
    try {
      Gen<List<Integer>> testee = lists()
          .of(integers().allPositive()).ofSize(0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingALinkedListOfNegativeSize() {
    try {
      Gen<List<Integer>> testee = lists().of(integers().allPositive()).ofType(lists().linkedList()).ofSize(-3);
      fail("Created a list generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingBoundedListsWithMinNegative() {
    try {
      Gen<List<Integer>> testee = lists().of(integers().allPositive()).ofSizeBetween(-3, 6);
      fail("Created a list generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingBoundedListsOfMinSizeEqualToZero() {
    try {
      Gen<List<Integer>> testee = lists().of(integers().allPositive()).ofSizeBetween(0, 6);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedListWithMaxNegative() {
    try {
      Gen<List<Integer>> testee = lists().of(integers().allPositive()).ofSizeBetween(-3, -2);
      fail("Created a list generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedListWithMaxSmallerThanMin() {
    try {
      Gen<List<Integer>> testee = lists()
          .of(integers().allPositive()).ofSizeBetween(2, 0);
      fail("Created a list generator with negative length!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingBoundedListsOfMinSizeEqualToMaxSize() {
    try {
      Gen<List<Integer>> testee = lists()
          .of(integers().allPositive()).ofSizeBetween(0, 0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnArrayOfNegativeSize() {
    try {
      Gen<Integer[]> testee = arrays()
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
      Gen<Integer[]> testee = arrays()
          .ofIntegers(integers().allPositive()).withLength(0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedArrayWithMinNegative() {
    try {
      Gen<Integer[]> testee = arrays()
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
      Gen<Integer[]> testee = arrays()
          .ofIntegers(integers().allPositive()).withLengthBetween(0, 6);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingABoundedArrayWithMaxNegative() {
    try {
      Gen<Integer[]> testee = arrays()
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
      Gen<Integer[]> testee = arrays()
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
      Gen<Integer[]> testee = arrays()
          .ofIntegers(integers().allPositive()).withLengthBetween(0, 0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldReportUsefulErrorWhenAttemptingToCreate0LengthBigInteger() {
    try {
      Gen<BigInteger> testee = bigIntegers().ofBytes(0);
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
  public void shouldCreateABigIntegersWithOneByte() {
    try {
      Gen<BigInteger> testee = bigIntegers().ofBytes(1);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }


  @SuppressWarnings("unused")
  @Test
  public void shouldReportUsefulErrorWhenAttemptingToCreateBigDecimalOfNegativeNumberOfBytes() {
    try {
      Gen<BigDecimal> testee = bigDecimals().ofBytes(0).withScale(2);
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
  public void shouldCreateABigDecimalsWithOneByte() {
    try {
      Gen<BigDecimal> testee = bigDecimals().ofBytes(1).withScale(2);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @Test
  public void shouldShrinkConstantsTowardsConstantValue() {
    Gen<Integer> testee = arbitrary().constant(42);
    assertThatGenerator(testee).shrinksTowards(42);
  }

  @Test
  public void shouldShrinkTowardsFirstItemInPickedList() {
    Gen<String> testee = arbitrary()
        .pick(java.util.Arrays.asList("a", "b", "c"));
    assertThatGenerator(testee).shrinksTowards("a");
  }

  @Test
  public void shouldShrinkEnumsTowardsFirstDefinedConstant() {
    Gen<AnEnum> testee = arbitrary().enumValues(AnEnum.class);
    assertThatGenerator(testee).shrinksTowards(AnEnum.A);
  }
  
  @Test
  public void shouldNotShrinkEnumsWhenNoShrinkPoint() {
    Gen<AnEnum> testee = arbitrary().enumValuesWithNoOrder(AnEnum.class);
    assertThatGenerator(testee).hasNoShrinkPoint();
  }


  static enum AnEnum {
    A, B, C, D, E;
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingADateOfNegativeLong() {
    try {
      Gen<Date> testee = dates().withMilliseconds(-234);
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
      Gen<Date> testee = dates().withMilliseconds(0);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalForDatesWithMaxLessThanMin() {
    try {
      Gen<Date> testee = dates().withMillisecondsBetween(342, 3);
      fail("Created a Date where max long is less than min long!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveLongIntervalForDatesWithMaxEqualToMin() {
    try {
      Gen<Date> testee = dates().withMillisecondsBetween(352, 352);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalForDatesWithMinLessThanZero() {
    try {
      Gen<Date> testee = dates().withMillisecondsBetween(-5, 6);
      fail("Created a Date where min long is less than zero!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveLongIntervalForDatesWithMinEqualToZero() {
    try {
      Gen<Date> testee = dates().withMillisecondsBetween(0, 5);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @Test
  public void shouldGenerateDateMax() {
    Gen<Date> testee = dates().withMilliseconds(7890789);
    assertThatGenerator(testee).generatesTheMinAndMax(new Date(0),new Date(7890789));
  }

  @Test
  public void shouldGenerateDateAtStartAndEndInclusive() {
    Gen<Date> testee = dates().withMillisecondsBetween(3245352,
        72938572398752L);
    assertThatGenerator(testee).generatesTheMinAndMax(new Date(3245352),  new Date(72938572398752L));
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingALocalDateBelowMinEpochDayCount() {
    try {
      Gen<LocalDate> testee = localDates()
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
      Gen<LocalDate> testee = localDates()
          .withDays(LOCAL_DATE_MAX_EPOCH_DAY_COUNT + 1);
      fail("Created a localDate with an improper value");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingALocalDateAtMinEpochDayCount() {
    try {
      Gen<LocalDate> testee = localDates()
          .withDays(LOCAL_DATE_MIN_EPOCH_DAY_COUNT);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingALocalDateAtMaxEpochDayCount() {
    try {
      Gen<LocalDate> testee = localDates()
          .withDays(LOCAL_DATE_MAX_EPOCH_DAY_COUNT);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable input!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalForLocalDatesWithMaxLessThanMin() {
    try {
      Gen<LocalDate> testee = localDates().withDaysBetween(342, 3);
      fail("Created a localDate where max long is less than min long!");
    } catch (IllegalArgumentException expected) {
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldNotCatchWhenCreatingAnInclusiveLongIntervalForLocalDatesWithMaxEqualToMin() {
    try {
      Gen<LocalDate> testee = localDates().withDaysBetween(352, 352);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void shouldCatchWhenCreatingAnInclusiveLongIntervalForLocalDatesWithMinLessThanMinEpochDayCount() {
    try {
      Gen<LocalDate> testee = localDates().withDaysBetween(
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
      Gen<LocalDate> testee = localDates().withDaysBetween(
          LOCAL_DATE_MIN_EPOCH_DAY_COUNT, LOCAL_DATE_MAX_EPOCH_DAY_COUNT);
    } catch (IllegalArgumentException expected) {
      fail("Threw an exception for an acceptable interval!");
    }
  }

  @Test
  public void shouldGenerateLocalDateMax() {
    Gen<LocalDate> testee = localDates().withDays(7890789);
    assertThatGenerator(testee).generatesTheMinAndMax(LocalDate.ofEpochDay(0),LocalDate.ofEpochDay(7890789));
  }

  @Test
  public void shouldGenerateLocalDateAtStartAndEndInclusive() {
    Gen<LocalDate> testee = localDates().withDaysBetween(3245352, 729385723);
    assertThatGenerator(testee).generatesTheMinAndMax(LocalDate.ofEpochDay(3245352),
        LocalDate.ofEpochDay(729385723));
  }

  @Test
  public void shouldGenerateBooleansAsExpected() {
    Gen<Boolean> testee = booleans().all();
    assertThatGenerator(testee).generatesAllOf(true, false);
  }

}
