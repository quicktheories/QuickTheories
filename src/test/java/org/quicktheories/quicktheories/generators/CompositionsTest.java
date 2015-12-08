package org.quicktheories.quicktheories.generators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.quicktheories.quicktheories.generators.SourceAssert.assertThatSource;
import static org.quicktheories.quicktheories.generators.SourceDSL.arbitrary;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.ShrinkContext;
import org.quicktheories.quicktheories.core.Source;

public class CompositionsTest {

  PseudoRandom prng = Configuration.defaultPRNG(2);

  @Test
  public void shouldInterleaveValues() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5);
    Source<Integer> b = arbitrary().sequence(10, 20, 30, 40, 50);
    Source<Integer> testee = Compositions.interleave(a, b);

    assertThatSource(testee).generatesTheFirstFourValues(1, 10, 2, 20);

  }

  @Test
  public void shouldAppropriatelyShrinkInterleavedValues() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5);
    Source<Integer> b = arbitrary().sequence(10, 20, 30, 40, 50);

    Source<Integer> combined = Compositions.interleave(a, b);

    assertThat(combined.shrink(5, step(0)).collect(Collectors.toList()))
        .containsOnly(1, 2, 3, 4);
    assertThat(combined.shrink(30, step(1)).collect(Collectors.toList()))
        .containsOnly(10, 20);

  }

  @Test
  public void shouldNestInterleaves() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5);
    Source<Integer> b = arbitrary().sequence(10, 20, 30, 40, 50);
    Source<Integer> c = arbitrary().sequence(100, 200, 300, 400, 500);

    Source<Integer> combined = Compositions.interleave(a,
        Compositions.interleave(b, c));

    assertThatSource(combined).generatesTheFirstSixValues(1, 10, 2, 100, 3,
        20);
  }

  @Test
  public void shouldAppropriatelyShrinkNestedRHSInterleavedValues() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5);
    Source<Integer> b = arbitrary().sequence(10, 20, 30, 40, 50);
    Source<Integer> c = arbitrary().sequence(100, 200, 300, 400, 500);

    Source<Integer> combined = Compositions.interleave(a,
        Compositions.interleave(b, c));

    assertThat(combined.shrink(5, step(0)).collect(Collectors.toList()))
        .containsOnly(1, 2, 3, 4);
    assertThat(combined.shrink(30, step(1)).collect(Collectors.toList()))
        .containsOnly(10, 20);
    assertThat(combined.shrink(3, step(2)).collect(Collectors.toList()))
        .containsOnly(1, 2);
    assertThat(combined.shrink(300, step(3)).collect(Collectors.toList()))
        .containsOnly(100, 200);
  }

  @Test
  public void shouldAppropriatelyShrinkNestedLHSInterleavedValues() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5);
    Source<Integer> b = arbitrary().sequence(10, 20, 30, 40, 50);
    Source<Integer> c = arbitrary().sequence(100, 200, 300, 400, 500);

    Source<Integer> combined = Compositions
        .interleave(Compositions.interleave(a, b), c);

    assertThat(combined.shrink(5, step(0)).collect(Collectors.toList()))
        .containsOnly(1, 2, 3, 4);
    assertThat(combined.shrink(200, step(1)).collect(Collectors.toList()))
        .containsOnly(100);
    assertThat(combined.shrink(20, step(2)).collect(Collectors.toList()))
        .containsOnly(10);
    assertThat(combined.shrink(300, step(3)).collect(Collectors.toList()))
        .containsOnly(100, 200);
  }

  @Test
  public void shouldAppropriatelyShrinkThreeLevelNestedInterleavedValues() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5);
    Source<Integer> b = arbitrary().sequence(10, 20, 30, 40, 50);
    Source<Integer> c = arbitrary().sequence(100, 200, 300, 400, 500);
    Source<Integer> d = arbitrary().sequence(1000, 2000, 3000, 4000, 5000);

    Source<Integer> combined = Compositions.interleave(
        Compositions.interleave(a, Compositions.interleave(b, c)), d);

    assertThat(combined.shrink(3, step(4)).collect(Collectors.toList()))
        .containsOnly(1, 2);
    assertThat(combined.shrink(300, step(6)).collect(Collectors.toList()))
        .containsOnly(100, 200);
  }

  @Test
  public void shouldComposeInSequence() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5);
    Source<Integer> b = arbitrary().sequence(10, 20, 30, 40, 50);

    Source<Integer> combined = Compositions.ntimesThen(2, a, b);

    assertThatSource(combined).generatesTheFirstFourValues(1, 2, 10, 20);
  }

  @Test
  public void shouldAppropriatelyShrinkValuesComposedInSequence() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5);
    Source<Integer> b = arbitrary().sequence(10, 20, 30, 40, 50);

    Source<Integer> combined = Compositions.ntimesThen(4, a, b);

    assertThat(combined.shrink(3, step(0)).collect(Collectors.toList()))
        .containsOnly(1, 2);
    assertThat(combined.shrink(40, step(5)).collect(Collectors.toList()))
        .containsOnly(10, 20, 30);
  }

  @Test
  public void shouldAlwaysReturnExtraRequestedValues() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5);
    Source<Integer> combined = Compositions.combineWithValues(a, 20, 30);

    assertThatSource(combined).generatesAllOf(20, 30);
  }

  @Test
  public void shouldShrinkExtraRequestedValuesSeperately() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5);
    Source<Integer> combined = Compositions.combineWithValues(a, 20, 30);

    assertThat(combined.shrink(30, step(0)).collect(Collectors.toList()))
        .isEqualTo(asList(20));
  }

  @Test
  public void shouldAlwaysReturnSpecificallyRequestedValues() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5);
    // note contract broken here as 20 and 30 cannot be generated by 1,2,3,4,5
    Source<Integer> combined = Compositions.weightWithValues(a, 20, 30);

    assertThatSource(combined).generatesAllOf(20, 30);
  }

  @Test
  public void shouldShrinkSpeciallyRequestedValuesNormally() {
    Source<Integer> a = arbitrary().sequence(1, 2, 3, 4, 5, 20, 30);
    Source<Integer> combined = Compositions.weightWithValues(a, 20, 30);

    assertThat(combined.shrink(20, step(0)).collect(Collectors.toList()))
        .containsOnly(1, 2, 3, 4, 5);
  }

  private ShrinkContext step(int step) {
    return new ShrinkContext(step, 0, Configuration.defaultPRNG(2));
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> asList(T... ts) {
    return java.util.Arrays.asList(ts);
  }

}
