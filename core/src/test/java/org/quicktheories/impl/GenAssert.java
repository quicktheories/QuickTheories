package org.quicktheories.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.assertj.core.api.AbstractAssert;
import org.quicktheories.api.Pair;
import org.quicktheories.core.Configuration;
import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;

public class GenAssert<T>
    extends AbstractAssert<GenAssert<T>, Gen<T>> {

  protected GenAssert(Gen<T> actual) {
    super(actual, GenAssert.class);
  }

  public static <T> GenAssert<T> assertThatGenerator(
      Gen<T> actual) {
    return new GenAssert<>(actual);
  }

  public GenAssert<T> generatesTheMinAndMax(T min, T max) {
    Strategy config = Configuration.systemStrategy().withFixedSeed(0);
    RandomDistribution<T> random = new RandomDistribution<>(config,actual);
    PrecursorDataPair<T> somePoint = random.generate();
    
    T actualMin = generateFrom(somePoint.precursor().minLimit(), config);
    T actualMax = generateFrom(somePoint.precursor().maxLimit(), config);
    
    org.assertj.core.api.Assertions.assertThat(actualMin).isEqualTo(min);
    org.assertj.core.api.Assertions.assertThat(actualMax).isEqualTo(max);
    return this;
  }
  
  public GenAssert<T> shrinksTowards(T target) {
    Strategy config = Configuration.systemStrategy().withFixedSeed(0);
    BoundarySkewedDistribution<T> boundaries = new BoundarySkewedDistribution<>(config,actual);
    long[] t = boundaries.generate().precursor().shrinkTarget();
    T actual = generateFrom(t, config); 
        
    org.assertj.core.api.Assertions.assertThat(actual).isEqualTo(target);
    return this;
  }
  
  public GenAssert<T> hasNoShrinkPoint() {
    Strategy config = Configuration.systemStrategy().withFixedSeed(0);
    BoundarySkewedDistribution<T> boundaries = new BoundarySkewedDistribution<>(config,actual);

    PrecursorDataPair<T> possibleShrinkPoint = boundaries.generate();
    
    org.assertj.core.api.Assertions.assertThat(possibleShrinkPoint.precursor().shrinkTarget(0)).isEmpty();
    return this;
  }

  @SafeVarargs
  public final GenAssert<T> generatesAllOf(T... ts) {
    return generatesAllOfWithNSamples(1000, ts);
  }
  
  public GenAssert<T> generatesAllOfWithNSamples(int samples, @SuppressWarnings("unchecked") T... ts) {
    List<T> generated = generateValues(samples);
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

  @SafeVarargs
  public final GenAssert<T> generatesInProportion(Pair<T,Double>... wts) {
    return generatesInProportion(1000, 0.05, wts);
  }

  public final GenAssert<T> generatesInProportion(int samples, double errorThresh,
                                                  @SuppressWarnings("unchecked") Pair<T,Double>... wts) {
    List<T> generated = generateValues(samples);
    Map<T, Long> counted = generated.stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    Arrays.stream(wts).forEach(wt ->
        org.assertj.core.api.Assertions.assertThat((wt._2))
            .isCloseTo(counted.get(wt._1).doubleValue() / samples,
                org.assertj.core.api.Assertions.within(errorThresh)));
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
    Strategy config = Configuration.systemStrategy().withFixedSeed(0);
    BoundarySkewedDistribution<T> dist = new BoundarySkewedDistribution<>(config, gen); 
    List<T> generated = new ArrayList<>();
    for (int i = 0; i != count; i++) {
      generated.add((T) dist.generate().value());
    }
    return generated;
  }

  
  private T generateFrom(long[] ls, Strategy config) {
    ForcedDistribution<T> forced = new ForcedDistribution<>(config, actual, ls);
    return forced.generate().value();    
  }
  
}
