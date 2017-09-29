package org.quicktheories.impl;

import org.quicktheories.core.Gen;
import org.quicktheories.core.Strategy;

class RandomDistribution<T>  implements Distribution<T> {

  private final Gen<T> gen;
  private final Strategy config;
  
  RandomDistribution(Strategy config, Gen<T> gen) {
    this.gen = gen;
    this.config = config;
  }

  public PrecursorDataPair<T> generate() {
    return generate(gen,  config.generateAttempts());
  }
  
  private PrecursorDataPair<T> generate(Gen<T> gen, int maxTries) {
    ShapedDataSource buffer = new ShapedDataSource(config.prng(), new long[0],
        maxTries);
    T t = gen.generate(buffer);
    return new PrecursorDataPair<>(buffer.capturedPrecursor(), buffer.failedAssumptions(), t);
  }
  
}
