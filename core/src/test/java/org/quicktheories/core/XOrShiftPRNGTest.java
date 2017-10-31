package org.quicktheories.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Test;

public class XOrShiftPRNGTest {
  
  PseudoRandom testee = new XOrShiftPRNG(1);

  @Test
  public void xOrShiftPrngGeneratesIntegersWithinTheInterval() {
    generatesExactly(testee, prng -> prng.nextInt(-5, 5), -5, -4, -3, -2,
        -1, 0, 1, 2, 3, 4, 5);
  }

  @Test
  public void xOrShiftPrngGeneratesOnlyIntegerPossibleWhenIntervalIsZeroWide() {
    generatesExactly(testee, prng -> prng.nextInt(0, 0), 0);
  }


  @Test
  public void xOrShiftPrngGeneratesOnlyLongPossibleWhenIntervalIsZeroWide() {
    generatesExactly(testee,
        prng -> prng.nextLong(0L, 0L), 0L);
  }

  @Test
  public void xOrShiftPrngGeneratesExtremeLongs() {
    generatesExactly(testee,
        prng -> prng.nextLong(Long.MIN_VALUE,
            Long.MIN_VALUE + 3),
        Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MIN_VALUE + 2,
        Long.MIN_VALUE + 3);
  }

  @Test
  public void xOrShiftPrngReturnsCorrectInitialSeed() {
    testee = new XOrShiftPRNG(42);
    generateLongValues(testee,
        prng -> prng.nextLong(0, 100), 10);
    assertTrue(
        "Expected seed to be one, but received "
            + testee.getInitialSeed(),
        testee.getInitialSeed() == 42);
  }
  
  @Test
  public void shouldGenerateUpToLongMax() {
    XOrShiftPRNG testee = new XOrShiftPRNG(0);
    long actual = testee.nextLong(Long.MAX_VALUE, Long.MAX_VALUE);
    assertThat(actual).isEqualTo(Long.MAX_VALUE);
  }
  
  @Test
  public void shouldGenerateFromLowerLimit() {
    XOrShiftPRNG testee = new XOrShiftPRNG(0);
    Set<Long> generated = new HashSet<>();
    for (int i = 0; i != 10; i++) {
      generated.add(testee.nextLong(Long.MIN_VALUE, Long.MIN_VALUE + 1));
    }

    assertThat(generated).contains(Long.MIN_VALUE);
    assertThat(generated).contains(Long.MIN_VALUE + 1);    
  }  
  
  @Test
  public void shouldGenerateAcrossFullRange() {
    // given size of the range expect 1000 unique values in 1000 attempts
    generatesAtLeastXUniqueValues(testee,
        prng -> prng.nextLong(Long.MIN_VALUE,
                Long.MAX_VALUE),
       1000);
  }

  @Test
  public void shouldGenerateAcrossReducedMaxRange() {
    // given size of the range expect 1000 unique values in 1000 attempts
    generatesAtLeastXUniqueValues(testee,
        prng -> prng.nextLong(Long.MIN_VALUE,
                Long.MAX_VALUE -2 ),
       1000);
  }
  
  @Test
  public void shouldGenerateAcrossReducedMinRange() {
    // given size of the range expect 1000 unique values in 1000 attempts
    generatesAtLeastXUniqueValues(testee,
        prng -> prng.nextLong(Long.MIN_VALUE + 2,
                Long.MAX_VALUE),
       1000);
  } 
  
  @Test
  public void shouldGenerateAcrossHalfDomain() {
    // given size of the range expect 1000 unique values in 1000 attempts
    generatesAtLeastXUniqueValues(testee,
        prng -> prng.nextLong(Long.MIN_VALUE,
                Long.MAX_VALUE / 2),
       1000);
  } 
  
  @Test
  public void shouldGenerateAcrossSmallDomain() {
    // given size of the range expect duplicates
    generatesAtLeastXUniqueValues(testee,
        prng -> prng.nextLong(-1000,
                1000),
       500);
  } 
  
  @Test
  public void valuesObeyConstraintsInLargeRange() {
    long min = Long.MIN_VALUE + 1000;
    long max = Long.MAX_VALUE - 1000;    
    allValuesMatch(testee, prng -> prng.nextLong(min,max), l -> l >= min && l <= max );
  }
  
  
  @Test
  public void errorsNicelyWhenNonSensicalRangeRequested() {
    try {
      testee.nextLong(1, 0);
      fail();
    } catch(IllegalArgumentException ex) {
      assertThat(ex).hasMessageContaining("Invalid range 1 to 0");
      // pass
    }
  }
  
  private void allValuesMatch(PseudoRandom prng, Function<PseudoRandom, Long> longGeneratingMethod, Predicate<Long> test) {
   Stream<Long> shouldNotHaveGenerated = generateLongValues(prng, longGeneratingMethod, 1000).stream().filter(test.negate());
   assertThat(shouldNotHaveGenerated).isEmpty();
  }

  private void generatesAtLeastXUniqueValues(PseudoRandom prng,
      Function<PseudoRandom, Long> longGeneratingMethod, int uniqueValues) {
    Set<Long> generated = new HashSet<>(generateLongValues(prng, longGeneratingMethod, 1000));

    org.assertj.core.api.Assertions.assertThat(generated.size()).isGreaterThanOrEqualTo(uniqueValues);
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
