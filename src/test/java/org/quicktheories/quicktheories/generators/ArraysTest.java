package org.quicktheories.quicktheories.generators;

import static org.junit.Assert.assertTrue;
import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

public class ArraysTest {

  private static final int ASCII_LAST_CODEPOINT = 0x007F;
  private static final int FIRST_CODEPOINT = 0x0000;

  @Test
  public void shouldGenerateAllPossibleArraysWithinDomain() {
    Source<Integer[]> testee = Arrays.arraysOf(Integers.range(1, 2),
        Integer.class, 2, 2);
    assertThatSource(testee).generatesAllOf(
        new Integer[] { 1, 1 }, new Integer[] { 1, 2 }, new Integer[] { 2, 1 },
        new Integer[] { 2, 2 });
  }

  @Test
  public void shouldNotShrinkAnEmptyFixedSizeArray() {
    Source<Character[]> testee = Arrays
        .arraysOf(
            Characters.ofCharacters(FIRST_CODEPOINT, ASCII_LAST_CODEPOINT),
            Character.class, 0, 0);
    assertThatSource(testee)
        .cannotShrink(new Character[] {});
  }

  @Test
  public void shouldShrinkNegativeIntegersByOneInAFixedLengthArrayWhereAllValuesAreWithinRemainingCyclesOfTarget() {
    Source<Integer[]> testee = Arrays
        .arraysOf(Integers.range(-9, 0), Integer.class, 3, 3);
    assertThatSource(testee).shrinksArrayValueTo(
        new Integer[] { -6, -3, -1 }, new Integer[] { -5, -2, 0 },
        new ShrinkContext(0, 100, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldGenerateAllPossibleArraysWithinSizeRange() {
    Source<Integer[]> testee = Arrays
        .arraysOf(Integers.range(1, 1), Integer.class, 1, 4);
    assertThatSource(testee).generatesAllOf(new Integer[] { 1 },
        new Integer[] { 1, 1 }, new Integer[] { 1, 1, 1 },
        new Integer[] { 1, 1, 1, 1 });
  }

  @Test
  public void shouldNotShrinkAnEmptyBoundedSizeArray() {
    Source<String[]> testee = Arrays
        .arraysOf(Strings.boundedNumericStrings(152, 32523), String.class, 0,
            5);
    assertThatSource(testee).cannotShrinkArray(new String[] {});
  }

  @Test
  public void shouldShrinkAFixedLengthArrayToAnArrayOfSameLength() {
    Source<Integer[]> testee = Arrays
        .arraysOf(Integers.range(1, Integer.MAX_VALUE), Integer.class, 4, 4);
    Integer[] input = new Integer[] { -7, -2, 0, -4 };
    Integer[] shrunk = testee
        .shrink(input, new ShrinkContext(0, 100, Configuration.defaultPRNG(-2)))
        .iterator()
        .next();
    isExpectedLength(shrunk, 4);
  }

  @Test
  public void shouldShrinkElementsOfFixedArrayByOneIfAllWithinRemainingCyclesOfTarget() {
    Source<String[]> testee = Arrays
        .arraysOf(Strings.ofBoundedLengthStrings(Character.MIN_CODE_POINT,
            Character.MAX_CODE_POINT, 1, 8), String.class, 2, 2);
    String[] input = new String[] { "\ud81b\udf33", "b" };
    String[] expected = new String[] { "\ufffd", "a" };
    assertThatSource(testee).shrinksArrayValueTo(input, expected,
        new ShrinkContext(0, 1000000000, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkBoundedArrayNotMinimumLengthByOne() {
    Source<Character[]> testee = Arrays.arraysOf(
        Characters.ofCharacters(FIRST_CODEPOINT, ASCII_LAST_CODEPOINT),
        Character.class, 3, 7);
    assertThatSource(testee).shrinksArrayValueTo(
        new Character[] { 'a', 'a', 'a', 'a' },
        new Character[] { 'a', 'a', 'a' },
        new ShrinkContext(0, 100, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldShrinkBoundedArrayOfMinimumLengthAsFixedArray() {
    Source<Character[]> testee = Arrays.arraysOf(
        Characters.ofCharacters(FIRST_CODEPOINT, ASCII_LAST_CODEPOINT),
        Character.class, 4, 7);
    assertThatSource(testee).shrinksArrayValueTo(
        new Character[] { 'b', 'b', 'b', 'b' },
        new Character[] { 'a', 'a', 'a', 'a' },
        new ShrinkContext(0, 100, Configuration.defaultPRNG(2)));
  }

  @Test
  public void shouldDescribeArrayContents() {
    Source<Integer[]> testee = Arrays.arraysOf(Integers.range(0, 1),
        Integer.class, 2, 2);
    Integer[] anArray = { 1, 2, 3 };
    assertThat(testee.asString(anArray)).isEqualTo("[1, 2, 3]");
  }

  private <T> void isExpectedLength(T[] shrunkOutput, int expected) {
    assertTrue(
        "Expected " + java.util.Arrays.toString(shrunkOutput) + " to be of length " + expected
            + "rather than " + shrunkOutput.length,
        shrunkOutput.length == expected);
  }

}
