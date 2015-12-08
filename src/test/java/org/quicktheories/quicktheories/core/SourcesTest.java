package org.quicktheories.quicktheories.core;

import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;
import static org.quicktheories.quicktheories.generators.SourceDSL.*;

import java.util.stream.Stream;

import org.junit.Test;

public class SourcesTest {

  @Test
  public void shouldCreateSimpleConvertedTypes() {
    Source<String> actual = arbitrary().sequence(1, 2, 3, 4, 5)
        .asWithoutShrinking(i -> i.toString());
    assertThatSource(actual).generatesAllOf("1", "2", "3", "4", "5");
  }

  @Test
  public void willNotShrinkConvertedValuesWhenNoBackFunctionSupplied() {
    Source<String> actual = arbitrary().sequence(1, 2, 3, 4, 5)
        .asWithoutShrinking(i -> i.toString());
    assertThatSource(actual).cannotShrink("5");
  }

  @Test
  public void shouldShrinkValuesWhenBackFunctionSupplied() {
    Source<String> actual = arbitrary().sequence(1, 2, 3, 4, 5)
        .as(i -> i.toString(), s -> Integer.parseInt(s));
    assertThatSource(actual).shrinksValueTo("5", "4");
  }

  @Test
  public void shouldUseSuppliedShrinkInstnace() {
    Source<Integer> actual = arbitrary().sequence(1, 2, 3, 4, 5)
        .withShrinker((original, context) -> Stream.of(42));
    assertThatSource(actual).shrinksValueTo(1, 42);
  }

  @Test
  public void shouldAlternateWithSuppliedGenerator() {
    Source<Integer> rhs = arbitrary().sequence(10, 20, 30, 40, 50);
    Source<Integer> testee = arbitrary().sequence(1, 2, 3, 4, 5)
        .andAlternateWithSource(rhs);
    assertThatSource(testee).generatesTheFirstFourValues(1, 10, 2, 20);
  }

  @Test
  public void shouldAlwaysGenerateSuppliedValues() {
    Source<String> testee = strings().basicLatinAlphabet().ofLength(1)
        .andAlwaysTheValues("a", "b", "c", "d");
    assertThatSource(testee).generatesTheFirstFourValues("a", "b", "c", "d");
  }

  @Test
  public void shouldSwitchGeneratorAfterNTimes() {
    Source<Integer> after = arbitrary().sequence(0);
    Source<Integer> testee = arbitrary().sequence(1, 2).nTimesThenSwitchTo(2,
        after);
    assertThatSource(testee).generatesTheFirstSixValues(1, 2, 0, 0, 0, 0);
  }

}
