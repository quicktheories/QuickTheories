package org.quicktheories.quicktheories.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.Source;

public class ArbritaryTest {

  PseudoRandom prng = Configuration.defaultPRNG(2);

  @Test
  public void shouldReturnValuesFromConstant() {
    Source<Integer> testee = Arbitrary.constant(42);

    assertThat(testee.next(prng, 0)).isEqualTo(42);
    assertThat(testee.next(prng, 1)).isEqualTo(42);
  }

  @Test
  public void shouldNotShrinkConstants() {
    Source<Integer> testee = Arbitrary.constant(42);

    assertThatSource(testee).cannotShrink(42);
  }

  @Test
  public void shouldReturnAllItemsListWhenPickingRandomly() {
    Source<String> testee = Arbitrary
        .pick(java.util.Arrays.asList("a", "1", "b", "2"));
    assertThatSource(testee).generatesAllOf("a", "1", "b", "2");
  }

  @Test
  public void shouldShrinkTowardsFirstItemsInList() {
    Source<String> testee = Arbitrary
        .pick(java.util.Arrays.asList("a", "1", "b", "2"));
    assertThatSource(testee).shrinksValueTo("2", "b");
    assertThatSource(testee).shrinksValueTo("1", "a");
    assertThatSource(testee).cannotShrink("a");
  }

  @Test
  public void shouldReturnAllItemsInSequence() {
    Source<String> testee = Arbitrary
        .sequence(java.util.Arrays.asList("a", "1", "b", "2"));
    assertThatSource(testee).generatesTheFirstFourValues("a", "1", "b", "2");
  }

  @Test
  public void shouldRepeatSequence() {
    Source<String> testee = Arbitrary
        .sequence(java.util.Arrays.asList("a", "1", "Z"));
    assertThatSource(testee).generatesTheFirstSixValues("a", "1", "Z", "a",
        "1", "Z");
  }

  @Test
  public void shouldShrinkSequenceTowardsItsStart() {
    Source<String> testee = Arbitrary
        .sequence(java.util.Arrays.asList("1", "2", "3"));
    assertThatSource(testee).shrinksValueTo("3", "2");
    assertThatSource(testee).shrinksValueTo("2", "1");
    assertThatSource(testee).cannotShrink("1");
  }

  static enum AnEnum {
    A, B, C, D, E;
  }

  @Test
  public void shouldRandomlySelectEnumValues() {
    Source<AnEnum> testee = Arbitrary
        .pick(java.util.Arrays.asList(AnEnum.class.getEnumConstants()));
    assertThatSource(testee).generatesAllOf(AnEnum.A, AnEnum.B, AnEnum.C,
        AnEnum.D, AnEnum.E);
  }

  @Test
  public void shouldShrinkEnumsTowardsFirstDefinedConstant() {
    Source<AnEnum> testee = Arbitrary
        .pick(java.util.Arrays.asList(AnEnum.class.getEnumConstants()));
    assertThatSource(testee).shrinksValueTo(AnEnum.C, AnEnum.B);
  }

}
