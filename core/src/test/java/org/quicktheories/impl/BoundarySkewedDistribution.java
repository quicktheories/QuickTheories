package org.quicktheories.impl;

import java.util.ArrayDeque;

import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;
import org.quicktheories.impl.Distribution;
import org.quicktheories.impl.PrecursorDataPair;
import org.quicktheories.impl.ShapedDataSource;

/**
 * Visits (in order) any likely shrink target, minima and maxima before switching to a
 * random distribution.
 *
 * @param <T>
 */
class BoundarySkewedDistribution<T> implements Distribution<T> {
  
  private ArrayDeque<long[]> toVisit = new ArrayDeque<long[]>();
  
  private final Gen<T> gen;
  private final Strategy config;
  
  
  BoundarySkewedDistribution(Strategy config, Gen<T> gen) {
    this.gen = gen;
    this.config = config;
    findBoundaries(config, gen);   
  }

  public PrecursorDataPair<T> generate() {
    final long[] forced;
    if (!toVisit.isEmpty()) {
      forced = toVisit.pop();
    } else {
      forced = new long[0];
    }
    return generate(gen, forced, config.generateAttempts());
  }
  
  private PrecursorDataPair<T> generate(Gen<T> gen, long[] forced, int maxTries) {
    ShapedDataSource buffer = new ShapedDataSource(config.prng(), forced,
        maxTries);
    T t = gen.generate(buffer);
    return new PrecursorDataPair<>(buffer.capturedPrecursor(), buffer.failedAssumptions(), t);
  }
    
  private void findBoundaries(Strategy config, Gen<T> gen) {
    PrecursorDataPair<T> result = generate(gen, new long[0], config.generateAttempts());
    toVisit.add(result.precursor().shrinkTarget());   
    toVisit.add(result.precursor().minLimit());   
    toVisit.add(result.precursor().maxLimit());
  }
  

}
