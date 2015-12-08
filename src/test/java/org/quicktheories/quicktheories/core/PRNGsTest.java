package org.quicktheories.quicktheories.core;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

public class PRNGsTest {

  PseudoRandom javaUtilPrng = new JavaUtilPRNG(0);
  PseudoRandom xOrShiftPrng = new XOrShiftPRNG(1);

  @Test
  public void javaUtilPrngGeneratesIntegersWithinTheInterval() {
    generatesExactly(javaUtilPrng, prng -> prng.nextInt(-5, 5), -5, -4, -3, -2,
        -1, 0, 1, 2, 3, 4, 5);
  }

  @Test
  public void xOrShiftPrngGeneratesIntegersWithinTheInterval() {
    generatesExactly(xOrShiftPrng, prng -> prng.nextInt(-5, 5), -5, -4, -3, -2,
        -1, 0, 1, 2, 3, 4, 5);
  }

  @Test
  public void javaUtilPrngGeneratesOnlyIntegerPossibleWhenIntervalIsZeroWide() {
    generatesExactly(javaUtilPrng, prng -> prng.nextInt(0, 0), 0);
  }

  @Test
  public void xOrShiftPrngGeneratesOnlyIntegerPossibleWhenIntervalIsZeroWide() {
    generatesExactly(xOrShiftPrng, prng -> prng.nextInt(0, 0), 0);
  }

  @Test
  public void javaUtilPrngGeneratesOnlyLongPossibleWhenIntervalIsZeroWide() {
    generatesExactly(javaUtilPrng,
        prng -> prng.generateRandomLongWithinInterval(0l, 0l), 0l);
  }

  @Test
  public void xOrShiftPrngGeneratesOnlyLongPossibleWhenIntervalIsZeroWide() {
    generatesExactly(xOrShiftPrng,
        prng -> prng.generateRandomLongWithinInterval(0l, 0l), 0l);
  }

  @Test
  public void javaUtilPrngGeneratesExtremeLongs() {
    generatesExactly(javaUtilPrng,
        prng -> prng.generateRandomLongWithinInterval(Long.MIN_VALUE,
            Long.MIN_VALUE + 3),
        Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MIN_VALUE + 2,
        Long.MIN_VALUE + 3);
  }

  @Test
  public void xOrShiftPrngGeneratesExtremeLongs() {
    generatesExactly(xOrShiftPrng,
        prng -> prng.generateRandomLongWithinInterval(Long.MIN_VALUE,
            Long.MIN_VALUE + 3),
        Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MIN_VALUE + 2,
        Long.MIN_VALUE + 3);
  }

  @Test
  public void javaUtilPrngReturnsCorrectInitialSeed() {
    @SuppressWarnings("unused")
    List<Long> notUsed = generateLongValues(javaUtilPrng,
        prng -> prng.nextLong(), 10);
    assertTrue(
        "Expected seed to be zero, but received "
            + javaUtilPrng.getInitialSeed(),
        javaUtilPrng.getInitialSeed() == 0);
  }

  @Test
  public void xOrShiftPrngReturnsCorrectInitialSeed() {
    @SuppressWarnings("unused")
    List<Long> notUsed = generateLongValues(xOrShiftPrng,
        prng -> prng.nextLong(), 10);
    assertTrue(
        "Expected seed to be one, but received "
            + xOrShiftPrng.getInitialSeed(),
        xOrShiftPrng.getInitialSeed() == 1);
  }

  @Test
  public void javaUtilPrngReturnsExceptionIfArgumentsIncorrect() {
    try {
      javaUtilPrng.nextLongWithinCheckedInterval(2, 1);
      fail("Genereated a long in an incorrect interval!");
    } catch (IllegalArgumentException expected) {
    }
  }

  private void generatesExactly(PseudoRandom prng,
      Function<PseudoRandom, Long> longGeneratingMethod, Long... ts) {
    List<Long> generated = generateLongValues(prng, longGeneratingMethod, 1000);
    org.assertj.core.api.Assertions.assertThat(generated).containsOnly(ts);
  }

  private void generatesExactly(PseudoRandom prng,
      Function<PseudoRandom, Integer> integerGeneratingMethod, Integer... ts) {
    List<Integer> generated = generateIntegerValues(prng,
        integerGeneratingMethod, 1000);
    org.assertj.core.api.Assertions.assertThat(generated).containsOnly(ts);
  }

  private List<Long> generateLongValues(PseudoRandom prng,
      Function<PseudoRandom, Long> longGeneratingMethod, int count) {
    List<Long> generated = new ArrayList<Long>();
    for (int i = 0; i != count; i++) {
      generated.add(longGeneratingMethod.apply(prng));
    }
    return generated;
  }

  private List<Integer> generateIntegerValues(PseudoRandom prng,
      Function<PseudoRandom, Integer> integerGeneratingMethod, int count) {
    List<Integer> generated = new ArrayList<Integer>();
    for (int i = 0; i != count; i++) {
      generated.add(integerGeneratingMethod.apply(prng));
    }
    return generated;
  }

}
