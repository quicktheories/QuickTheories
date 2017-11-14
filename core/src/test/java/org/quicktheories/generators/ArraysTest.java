package org.quicktheories.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.impl.GenAssert.assertThatGenerator;
import static org.quicktheories.generators.Generate.range;

import org.junit.Test;
import org.quicktheories.core.Gen;
import org.quicktheories.generators.Generate;

public class ArraysTest {

  private static final int ASCII_LAST_CODEPOINT = 0x007F;
  private static final int FIRST_CODEPOINT = 0x0000;

  @Test
  public void shouldGenerateAllPossibleArraysWithinDomain() {
    Gen<Integer[]> testee = Generate.arraysOf(range(1, 2),
        Integer.class, range(2, 2));
    assertThatGenerator(testee).generatesAllOf(
        new Integer[] { 1, 1 }, new Integer[] { 1, 2 }, new Integer[] { 2, 1 },
        new Integer[] { 2, 2 });
  }

  @Test
  public void shrinksTowardsEmptyArrayWhenZeroLengthsAllowed() {
    Gen<Character[]> testee = Generate
        .arraysOf(
            Generate.characters(FIRST_CODEPOINT, ASCII_LAST_CODEPOINT),
            Character.class, range(0, 10));
    assertThatGenerator(testee).shrinksTowards(new Character[0]);
  }

  
  @Test
  public void shrinksTowardsSmallestAllowedArrayWithSmallestContents() {
    Gen<Integer[]> testee = Generate
        .arraysOf(
            range(0, 10),
            Integer.class, 
            range(0, 10));
    assertThatGenerator(testee).shrinksTowards(new Integer[0]);
  }


  @Test
  public void shouldGenerateAllPossibleArraysWithinSizeRange() {
    Gen<Integer[]> testee = Generate
        .arraysOf(Generate.range(1, 1), Integer.class, range(1, 4));
    assertThatGenerator(testee).generatesAllOf(new Integer[] { 1 },
        new Integer[] { 1, 1 }, new Integer[] { 1, 1, 1 },
        new Integer[] { 1, 1, 1, 1 });
  }

  @Test
  public void shouldDescribeArrayContents() {
    Gen<Integer[]> testee = Generate.arraysOf(range(0, 1),
        Integer.class, range(2, 2));
    Integer[] anArray = { 1, 2, 3 };
    assertThat(testee.asString(anArray)).isEqualTo("[1, 2, 3]");
  }

  @Test
  public void shouldDescribeArrayContentsUsingProvidedSource() {
    Gen<String> sourceWithCustomDescription = Generate.constant("x").describedAs(x -> "custom description for x");
    Gen<String[]> testee = Generate.arraysOf(sourceWithCustomDescription, String.class, range(2, 2));

    String[] anArray = { "foo", "bar"};

    assertThat(testee.asString(anArray)).isEqualTo("[custom description for x, custom description for x]");
  }
  
  @Test
  public void shouldGenerateAllPossibleBytesArraysWithinSmallDomain() {
    Gen<byte[]> testee = Generate.byteArrays(Generate.range(1, 2), Generate.bytes((byte)1, (byte)2, (byte) 0));
    assertThatGenerator(testee).generatesAllOf(
        new byte[] { 1, 1 }, new byte[] { 1, 2 }, new byte[] { 2, 1 },
        new byte[] { 2, 2 });
  }
  
  @Test
  public void shouldGenerateAllPossibleIntArraysWithinSmallDomain() {
    Gen<int[]> testee = Generate.intArrays(Generate.range(1, 2), Generate.range(1, 2));
    assertThatGenerator(testee).generatesAllOf(
        new int[] { 1, 1 }, new int[] { 1, 2 }, new int[] { 2, 1 },
        new int[] { 2, 2 });
  }
  
  @Test
  public void shouldGenerateAllPossibleTwoDimensionalIntArraysWithinSmallDomain() {
    Gen<int[][]> testee = Generate.intArrays(Generate.range(1, 2), Generate.range(1, 2), Generate.range(1, 2));
    assertThatGenerator(testee).generatesAllOf(
        new int[][] { {1, 1}, {1,1} }, new int[][] { {1, 1}, {1, 2} }, new int[][] { {1,2}, {1, 1} }, new int[][] { {1, 2}, {1,2} },
        new int[][] { {2, 1}, {1,1} }, new int[][] { {2, 1}, {1, 2} }, new int[][] { {1,2}, {2, 1} }, new int[][] { {2, 2}, {2,2} });
  }
  
  @Test
  public void shouldProvideReadableDescriptionOfIntegerArrays() {
    Gen<int[]> testee = Generate.intArrays(Generate.range(1, 2), Generate.range(1, 2));
    assertThat(testee.asString(new int[] {1,2} )).containsSequence("1, 2");
  }
  
  @Test
  public void shouldProvideReadableDescriptionOfTwoDimensionalIntegerArrays() {
    Gen<int[][]> testee = Generate.intArrays(Generate.range(1, 2), Generate.range(1, 2), Generate.range(1, 2));
    assertThat(testee.asString(new int[][] { {1,2}, {6,12} } )).containsSequence("[1, 2], [6, 12]");
  }
  
  @Test
  public void shouldProvideReadableDescriptionOfByteArrays() {
    Gen<byte[]> testee = Generate.byteArrays(Generate.range(1, 2), Generate.bytes((byte)1, (byte)2, (byte) 0));
    assertThat(testee.asString(new byte[] {1,2} )).containsSequence("1, 2");
  }  

}
