package org.quicktheories.impl;

import java.util.ArrayDeque;

import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;

/**
 * Visits (in order) any likely shrink target, minima and maxima before switching to a
 * random distribution.
 *
 * @param <T>
 */
class BoundarySkewedDistribution<T> implements Distribution<T> {
  
  private ArrayDeque<long[]> toVisit;
  
  private final Gen<T> gen;
  private final Strategy config;
  
  
  BoundarySkewedDistribution(Strategy config, Gen<T> gen) {
    this.gen = gen;
    this.config = config;
    toVisit = findBoundaries(config, gen);   
  }

  public PrecursorDataPair<T> generate() {
    final long[] forced;
    if (!toVisit.isEmpty()) {
      forced = toVisit.pop();
    } else {
      forced = new long[0];
    }
    return generate(gen, config, forced);
  }
  
  private PrecursorDataPair<T> generate(Gen<T> gen, Strategy config, long[] forced) {
    ShapedDataSource buffer = new ShapedDataSource(config.prng(), forced, config.generateAttempts());
    T t = gen.generate(buffer);
    return new PrecursorDataPair<>(buffer.capturedPrecursor(), buffer.failedAssumptions(), t);
  }
     

  private ArrayDeque<long[]> findBoundaries(Strategy config, Gen<T> gen) {
    ArrayDeque<long[]> ordered = new ArrayDeque<>(); 
    PrecursorDataPair<T> startPoint = generate(gen, config, new long[0]);  
    ordered.add(startPoint.precursor().shrinkTarget());
    ordered.add(startPoint.precursor().minLimit());
    ordered.add(startPoint.precursor().maxLimit());   
    return ordered;
  }
  
}
