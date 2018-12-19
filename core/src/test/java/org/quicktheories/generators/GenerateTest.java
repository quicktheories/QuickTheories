package org.quicktheories.generators;

import static org.quicktheories.impl.GenAssert.assertThatGenerator;

import org.junit.Test;
import org.quicktheories.api.Pair;
import org.quicktheories.core.Gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GenerateTest {

  @Test
  public void oneOfGeneratesFromSingleSuppliedGen() { 
    Gen<Integer> testee = Generate.oneOf(Generate.constant(1));
    assertThatGenerator(testee).generatesAllOf(1);
  }
  
  @Test
  public void oneOfGeneratesFromAllSuppliedGens() { 
    int samples = 6;
    Gen<Integer> testee = Generate.oneOf(Generate.constant(1), Generate.constant(2), Generate.constant(3));
    
    assertThatGenerator(testee).generatesAllOfWithNSamples(samples, 1,2,3);
  }

  @Test
  public void frequencyZeroWeightsAreNeverPicked() {
    int samples = 6;
    Gen<Integer> testee = Generate.frequency(
        Pair.of(0,  Generate.constant(1)),
        Pair.of(15, Generate.constant(2)),
        Pair.of(30, Generate.constant(3)));

    assertThatGenerator(testee).generatesAllOfWithNSamples(samples, 2, 3);
  }

  @Test
  public void frequencyMustHaveAtLeastOneEntry() {
    List<Pair<Integer,Gen<Integer>>> emptyList = new ArrayList<>();
    Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() ->
        Generate.frequency(emptyList));
    org.assertj.core.api.Assertions.assertThat(thrown)
        .isInstanceOf(IllegalArgumentException.class)
        .hasNoCause()
        .hasMessage("List of generators must not be empty");
  }

  @Test
  public void frequencyMustHaveOnePositiveWeight() {
    Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() ->
        Generate.frequency(Arrays.asList(Pair.of(-1,  Generate.constant(1)),
            Pair.of(0, Generate.constant(2)),
            Pair.of(0, Generate.constant(3)))));
    org.assertj.core.api.Assertions.assertThat(thrown)
        .isInstanceOf(IllegalArgumentException.class)
        .hasNoCause()
        .hasMessage("At least one generator must have a positive weight");
  }

  @Test
  public void oneOfGeneratesByWeightPicked() {
    Gen<Integer> testee = Generate.frequency(
        Pair.of(1, Generate.constant(1)),
        Pair.of(2, Generate.constant(2)),
        Pair.of(3, Generate.constant(3)));

    assertThatGenerator(testee).generatesInProportion(
        Pair.of(1, 1.0/6.0), Pair.of(2, 2.0/6.0), Pair.of(3, 3.0/6.0));
  }

  @Test
  public void frequencyWithNoShrinkPointHasNoShrinkPoint() {
    Gen<Integer> testee = Generate.frequencyWithNoShrinkPoint(
        Arrays.asList(Pair.of(1,  Generate.constant(1)),
            Pair.of(10, Generate.constant(2)),
            Pair.of(100, Generate.constant(3))));
    assertThatGenerator(testee).hasNoShrinkPoint();
  }

  @Test
  public void frequencyVarArgsWithNoShrinkPointHasNoShrinkPoint() {
    Gen<Integer> testee = Generate.frequencyWithNoShrinkPoint(
        Pair.of(1,  Generate.constant(1)),
        Pair.of(10, Generate.constant(2)),
        Pair.of(100, Generate.constant(3)));
    assertThatGenerator(testee).hasNoShrinkPoint();
  }
}
