package org.quicktheories.quicktheories.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.AbstractAssert;
import org.quicktheories.quicktheories.core.Configuration;
import org.quicktheories.quicktheories.core.Gen;
import org.quicktheories.quicktheories.core.PseudoRandom;
import org.quicktheories.quicktheories.core.Strategy;
import org.quicktheories.quicktheories.impl.BoundarySkewedDistribution;
import org.quicktheories.quicktheories.impl.ShapedDataSource;

public class GenAssert<T>
    extends AbstractAssert<GenAssert<T>, Gen<T>> {

  protected GenAssert(Gen<T> actual) {
    super(actual, GenAssert.class);
  }

  public static <T> GenAssert<T> assertThatGenerator(
      Gen<T> actual) {
    return new GenAssert<T>(actual);
  }

  public GenAssert<T> generatesTheMinAndMax(T min, T max) {
    Strategy config = Configuration.systemStrategy().withFixedSeed(0);
    BoundarySkewedDistribution<T> boundaries = new BoundarySkewedDistribution<T>(config,actual);
    boundaries.generate(); // skip the shrink target
    T actualMin = boundaries.generate().value();
    T actualMax = boundaries.generate().value();
    
    org.assertj.core.api.Assertions.assertThat(actualMin).isEqualTo(min);
    org.assertj.core.api.Assertions.assertThat(actualMax).isEqualTo(max);
    return this;
  }
  
  public GenAssert<T> shrinksTowards(T target) {
    Strategy config = Configuration.systemStrategy().withFixedSeed(0);
    BoundarySkewedDistribution<T> boundaries = new BoundarySkewedDistribution<T>(config,actual);
    // test relies on implementation detail that first returned value is the shrink point
    T t = boundaries.generate().value();
    
    org.assertj.core.api.Assertions.assertThat(t).isEqualTo(target);
    return this;
  }
  
  public GenAssert<T> hasNoShrinkPoint() {
    Strategy config = Configuration.systemStrategy().withFixedSeed(0);
    BoundarySkewedDistribution<T> boundaries = new BoundarySkewedDistribution<T>(config,actual);

    PrecursorDataPair<T> possibleShrinkPoint = boundaries.generate();
    
    org.assertj.core.api.Assertions.assertThat(possibleShrinkPoint.precursor().shrinkTarget(0)).isEmpty();
    return this;
  }

  public GenAssert<T> generatesAllOf(
      @SuppressWarnings("unchecked") T... ts) {
    List<T> generated = generateValues(1000);
    org.assertj.core.api.Assertions.assertThat(generated).contains(ts);
    return this;
  }
  
  public GenAssert<T> generatesAllDistinctValuesBetween(int start, int end) {
    return generatesAtLeastNDistinctValues(end -start + 1);
  }
  
  public GenAssert<T> generatesAtLeastNDistinctValues(int count) {
    //Arbitrarily allowing a 1 in 10 duplication
    List<T> generated = generateValues(count * 10).stream().distinct().collect(Collectors.toList());
    org.assertj.core.api.Assertions.assertThat(generated.size()).isGreaterThanOrEqualTo(count);
    return this;
  }

  private List<T> generateValues(int count) {
    isNotNull();
    return generateValues(actual,count);
  }

  public GenAssert<T> doesNotGenerate(
      @SuppressWarnings("unchecked") T... ts) {
    List<T> generated = generateValues(100);
    org.assertj.core.api.Assertions.assertThat(generated).doesNotContain(ts);
    return this;
  }
  
  public static <T> List<T> generateValues(Gen<T> gen, int count) {
    PseudoRandom prng = Configuration.defaultPRNG(0);
    ShapedDataSource sds = new ShapedDataSource(prng, new long[0], count);
    List<T> generated = new ArrayList<T>();
    for (int i = 0; i != count; i++) {
      generated.add((T) gen.generate(sds));
    }
    return generated;
  }

  

}
