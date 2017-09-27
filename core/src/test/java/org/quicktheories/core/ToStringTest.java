package org.quicktheories.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.arbitrary;
import static org.quicktheories.generators.SourceDSL.arrays;
import static org.quicktheories.generators.SourceDSL.bigIntegers;
import static org.quicktheories.generators.SourceDSL.strings;

import java.util.Arrays;

import org.junit.Test;

public class ToStringTest {

  @Test
  public void shouldRetainCustomToStringForAirity1() {
    try {
      qt()
          .forAll(arbitrary().constant(42).describedAs(l -> "EXPECTED" + l))
          .check(l -> false);
      failIfReached();
    } catch (final AssertionError error) {
      assertThat(error)
          .hasMessageContaining("EXPECTED42");
    }
  }

  @Test
  public void shouldRetainCustomToStringForAirity2() {
    try {
      qt()
          .forAll(arbitrary().constant(1).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(2).describedAs(l -> "EXPECTED" + l))
          .check((a, b) -> false);
      failIfReached();
    } catch (final AssertionError error) {
      assertThat(error)
          .hasMessageContaining("EXPECTED1, EXPECTED2");
    }
  }

  @Test
  public void shouldRetainCustomToStringForAirity3() {
    try {
      qt()
          .forAll(arbitrary().constant(1).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(2).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(3).describedAs(l -> "EXPECTED" + l))
          .check((a, b, c) -> false);
      failIfReached();
    } catch (final AssertionError error) {
      assertThat(error)
          .hasMessageContaining("EXPECTED1, EXPECTED2, EXPECTED3");
    }
  }

  @Test
  public void shouldRetainCustomToStringForAirity4() {
    try {
      qt()
          .forAll(arbitrary().constant(1).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(2).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(3).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(4).describedAs(l -> "EXPECTED" + l))
          .check((a, b, c, d) -> false);
      failIfReached();
    } catch (final AssertionError error) {
      assertThat(error)
          .hasMessageContaining("EXPECTED1, EXPECTED2, EXPECTED3, EXPECTED4");
    }
  }

  @Test
  public void shouldRetainCustomToStringWhenConvertingWithPrecursor() {
    try {
      qt()
          .forAll(arbitrary().constant(42).describedAs(l -> "EXPECTED" + l))
          .asWithPrecursor(i -> "astring")
          .check((a, b) -> false);
      failIfReached();
    } catch (final AssertionError error) {
      assertThat(error)
          .hasMessageContaining("EXPECTED42");
    }
  }

  @Test
  public void shouldRetainCustomToStringWhenConvertingWithPrecursorForAirity2() {
    try {
      qt()
          .forAll(arbitrary().constant(1).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(2).describedAs(l -> "EXPECTED" + l))
          .asWithPrecursor((a, b) -> "astring")
          .check((a, b, c) -> false);
      failIfReached();
    } catch (final AssertionError error) {
      assertThat(error)
          .hasMessageContaining("EXPECTED1, EXPECTED2");
    }
  }

  @Test
  public void shouldRetainCustomToStringWhenConvertingWithPrecursorForAirity3() {
    try {
      qt()
          .forAll(arbitrary().constant(1).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(2).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(3).describedAs(l -> "EXPECTED" + l))
          .asWithPrecursor((a, b, c) -> "astring")
          .check((a, b, c, d) -> false);
      failIfReached();
    } catch (final AssertionError error) {
      assertThat(error)
          .hasMessageContaining("EXPECTED1, EXPECTED2, EXPECTED3");
    }
  }

  @Test
  public void shouldRetainCustomToStringWhenConvertingWithPrecursorForAirity4() {
    try {
      qt()
          .forAll(arbitrary().constant(1).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(2).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(3).describedAs(l -> "EXPECTED" + l),
              arbitrary().constant(4).describedAs(l -> "EXPECTED" + l))
          .asWithPrecursor((a, b, c, d) -> "astring")
          .check((a, b, c, d, e) -> false);
      failIfReached();
    } catch (final AssertionError error) {
      assertThat(error)
          .hasMessageContaining("EXPECTED1, EXPECTED2, EXPECTED3, EXPECTED4");
    }
  }

  @Test
  public void shouldPrintReadableRepresentationOfArrays() {
    try {
      qt().withFixedSeed(5)
      .forAll(arrays().ofStrings(strings().ascii().ofLength(1))
          .withLength(1))
          .check(i -> false);
      failIfReached();
    } catch (final AssertionError error) {
      assertThat(error)
          .hasMessageContaining(Arrays.deepToString(new String[] { "!" }));
    }
  }

  @Test
  public void shouldPrintArrayAsToDeepString() {
    try {
      qt().withFixedSeed(5)
      .forAll(bigIntegers().ofBytes(5))
          .as(b -> b.toByteArray())
          .describedAs(a -> Arrays.toString(a))
          .check(i -> false);
      failIfReached();
    } catch (final AssertionError error) {
      assertThat(error)
          .hasMessageContaining(Arrays.deepToString(new Integer[] { 0 }));
    }
  }
  
  @Test
  public void shouldPrintReadableRepresentationOfNull() throws Exception {
      try {
        qt().withFixedSeed(5)
        .forAll(arbitrary().pick(null, "test"))
        .check(value -> value != null);
    } catch (final AssertionError error) {
        assertThat(error)
            .hasMessageContaining("null");
    }
  }
    
  private void failIfReached() throws AssertionError {
    throw new Error("Expected an AssertionError but didn't get one");
  }

}
