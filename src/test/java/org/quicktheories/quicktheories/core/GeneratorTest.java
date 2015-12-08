package org.quicktheories.quicktheories.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class GeneratorTest {

  @Test
  public void shouldComposeWithOtherFunctions() {
    Generator<Integer> testee = (prng, step) -> 1;
    assertThat(testee.andThen(i -> i.toString()).next(aPrng(), someInt()))
        .isEqualTo("1");
  }

  @Test
  public void shouldCombineWithOneOtherGenerator() {
    Generator<Integer> testee = (prng, step) -> step;
    Generator<String> other = (prng, step) -> "A";
    Generator<String> actual = testee.combine(other, (i, s) -> s + i);
    assertThat(actual.next(aPrng(), 42)).isEqualTo("A42");
  }

  @Test
  public void shouldCombineWithTwoOtherGenerators() {
    Generator<Integer> testee = (prng, step) -> step;
    Generator<String> as = (prng, step) -> "A";
    Generator<String> bs = (prng, step) -> "B";
    Generator<String> actual = testee.combine(as, bs, (t, a, b) -> a + b + t);
    assertThat(actual.next(aPrng(), 42)).isEqualTo("AB42");
  }

  @Test
  public void shouldCombineWithThreeOtherGenerators() {
    Generator<Integer> testee = (prng, step) -> step;
    Generator<String> as = (prng, step) -> "A";
    Generator<String> bs = (prng, step) -> "B";
    Generator<String> cs = (prng, step) -> "C";
    Generator<String> actual = testee.combine(as, bs, cs,
        (t, a, b, c) -> a + b + c + t);
    assertThat(actual.next(aPrng(), 42)).isEqualTo("ABC42");
  }

  private PseudoRandom aPrng() {
    return null;
  }

  private int someInt() {
    return 0;
  }

}
