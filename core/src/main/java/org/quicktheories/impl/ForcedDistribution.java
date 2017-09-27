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
class ForcedDistribution<T> implements Distribution<T> {
  
  private ArrayDeque<long[]> toVisit = new ArrayDeque<long[]>();
  
  private final Gen<T> gen;
  private final Strategy config;
  
  
  ForcedDistribution(Strategy config, Gen<T> gen, long[] forced) {
    this.gen = gen;
    this.config = config;
    toVisit.add(forced);  
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
    
}
